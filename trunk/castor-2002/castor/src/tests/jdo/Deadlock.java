package jdo;


import java.io.PrintWriter;
import org.exolab.castor.jdo.JDOSource;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.TransactionAbortedException;


/**
 * Simple test for deadlock detection. Will report to the console two
 * concurrent transactions working on the same objects. The first transaction
 * will succeed, the second will fail.
 */
public class Deadlock
{


    private JDOSource      _jdo;


    private Database       _db;


    private PrintWriter    _logger;


    public Deadlock( JDOSource jdo, PrintWriter logger )
	throws PersistenceException
    {
        _jdo = jdo;
        _logger = logger;
        _db = _jdo.getDatabase();
        _logger.println( "Opened JDO database " + _db );
    }


    public void run()
	throws PersistenceException
    {
	OQLQuery      oql;
        TestObject    object;

        // Open transaction in order to perform JDO operations
        _db.begin();
        
        // Create two objects in the database -- need something to lock
        oql = _db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE id = $1" );
        oql.bind( new Integer( TestObject.DefaultId ) );
        object = (TestObject) oql.execute();
        if ( object == null ) {
            object = new TestObject();
            object.id = TestObject.DefaultId;
            object.name = TestObject.DefaultName;
            _logger.println( "Creating new object: " + object );
            _db.makePersistent( object );
        } else {
            _logger.println( "Found object: " + object );
        }
        oql.bind( new Integer( TestObject.DefaultId + 1 ) );
        object = (TestObject) oql.execute();
        if ( object == null ) {
            object = new TestObject();
            object.id = TestObject.DefaultId + 1;
            object.name = TestObject.DefaultName;
            _logger.println( "Creating new object: " + object );
            _db.makePersistent( object );
        } else {
            _logger.println( "Found object: " + object );
        }
        _db.commit();


        // Run two threads in parallel attempting to update the
        // two objects in a different order, with the first
        // suceeding and second failing
        FirstThread  first;
        SecondThread second;
            
        first = new FirstThread();
        first._jdo = _jdo;
        first._logger = _logger;
        first.start();
        second = new SecondThread();
        second._jdo = _jdo;
        second._logger = _logger;
        second.start();
    }
    
    
    static class FirstThread
        extends Thread
    {
        

        private JDOSource      _jdo;


        private PrintWriter    _logger;

        
        public void run()
        {
            OQLQuery     oql;
            TestObject   object;
            Database     db = null;

            try {
                db = _jdo.getDatabase();
                db.begin();
                oql = db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE id = $1" );
                
                // Load first object and change something about it (otherwise will not write)
                _logger.println( "First: Loading object " + TestObject.DefaultId );
                oql.bind( new Integer( TestObject.DefaultId ) );
                object = (TestObject) oql.execute();
                _logger.println( "First: Loaded " + object );
                object.name = object.name + ":1";
                // db.lock( group );
                
                // Give the other thread a 2 second opportunity.
                sleep( 2000 );
                
                _logger.println( "First: Loading object " + ( TestObject.DefaultId  + 1 ) );
                oql.bind( new Integer( TestObject.DefaultId + 1 ) );
                object = (TestObject) oql.execute();
                _logger.println( "First: Loaded " + object );
                object.name = object.name + ":1";
                // db.lock( group );
                
                // Give the other thread a 2 second opportunity.
                sleep( 2000 );

                // Attempt to commit the transaction, must acquire a write
                // lock blocking until the first transaction completes.
                _logger.println( "First: Committing" );
                db.commit();
                _logger.println( "First: Committed" );
                db.close();
            } catch ( Exception except ) {
                _logger.println( "Second: " + except );
                try {
                    if ( db != null )
                        db.close();
                } catch ( PersistenceException except2 ) { }
            }
        }
        
    }
    
    
    static class SecondThread
        extends Thread
    {

        
        private JDOSource      _jdo;


        private PrintWriter    _logger;

        
        public void run()
        {
            OQLQuery     oql;
            TestObject   object;
            Database     db = null;

            try {
                db = _jdo.getDatabase();
                db.begin();
                oql = db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE id = $1" );
                
                // Give the other thread a 2 second opportunity.
                sleep( 2000 );
                
                // Load first object and change something about it (otherwise will not write)
                _logger.println( "Second: Loading object " + ( TestObject.DefaultId + 1 ) );
                oql.bind( new Integer( TestObject.DefaultId + 1 ) );
                object = (TestObject) oql.execute();
                _logger.println( "Second: Loaded " + object );
                object.name = object.name + ":2";
                // db.lock( group );
                
                // Give the other thread a 2 second opportunity.
                sleep( 2000 );
                
                _logger.println( "Second: Loading object " + TestObject.DefaultId );
                oql.bind( new Integer( TestObject.DefaultId ) );
                object = (TestObject) oql.execute();
                _logger.println( "Second: Loaded " + object );
                object.name = object.name + ":2";
                // db.lock( group );

                // Give the other thread a 2 second opportunity.
                sleep( 2000 );
                
                // Attempt to commit the transaction, must acquire a write
                // lock blocking until the first transaction completes.
                _logger.println( "Second: Committing" );
                db.commit();
                _logger.println( "Error: deadlock not detected" );
                _logger.println( "Second: Committed" );
                db.close();
            } catch ( TransactionAbortedException except ) {
                if ( except.getException() instanceof LockNotGrantedException )
                    _logger.println( "OK: Deadlock detected" );
                else
                    _logger.println( "Error: " + except );
                _logger.println( "Second: aborting" );
            } catch ( Exception except ) {
                _logger.println( "Error: " + except );
            } finally {
                try {
                    if ( db != null )
                        db.close();
                } catch ( PersistenceException except2 ) { }
            }
        }
        
    }
    
    
}


