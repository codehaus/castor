package jdo;


import java.io.PrintWriter;
import java.util.Enumeration;
import org.exolab.castor.jdo.DataObjects;
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


    private DataObjects    _jdo;


    private Database       _db;


    private PrintWriter    _logger;


    public static final long  Wait = 2000;


    public Deadlock( DataObjects jdo, PrintWriter logger )
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
        Enumeration   enum;

        // Open transaction in order to perform JDO operations
        _db.begin();
        
        // Create two objects in the database -- need something to lock
        oql = _db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE id = $1" );
        oql.bind( TestObject.DefaultId );
        enum = oql.execute();
        if ( enum.hasMoreElements() ) {
            object = (TestObject) enum.nextElement();
            _logger.println( "Found object: " + object );
        } else {
            object = new TestObject();
            object.id = TestObject.DefaultId;
            _logger.println( "Creating new object: " + object );
            _db.create( object );
        }
        object.name = TestObject.DefaultName;
        oql.bind( TestObject.DefaultId + 1 );
        enum = oql.execute();
        if ( enum.hasMoreElements() ) {
            object = (TestObject) enum.nextElement();
            _logger.println( "Found object: " + object );
        } else {
            object = new TestObject();
            object.id = TestObject.DefaultId + 1;
            _logger.println( "Creating new object: " + object );
            _db.create( object );
        }
        object.name = TestObject.DefaultName;
        _db.commit();

        _logger.println( "Note: this test uses a 2 second delay between threads. CPU and database load might cause the test to not perform synchronously, resulting in erroneous results. Make sure that execution is not hampered by CPU/datebase load." );

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
        

        private DataObjects    _jdo;


        private PrintWriter    _logger;


        public void run()
        {
            TestObject   object;
            Database     db = null;
            long         start;

            try {
                db = _jdo.getDatabase();
                db.begin();
                
                start = System.currentTimeMillis();

                // Load first object and change something about it (otherwise will not write)
                _logger.println( "First: Loading object " + TestObject.DefaultId );
                object = (TestObject) db.load( TestObject.class,
                                               new Integer( TestObject.DefaultId ) );
                object.name = TestObject.DefaultName + ":1";
                _logger.println( "First: Modified to " + object );
                
                // Give the other thread a 2 second opportunity.
                sleep( start + Wait - System.currentTimeMillis() );
                start = System.currentTimeMillis();
                
                _logger.println( "First: Loading object " + ( TestObject.DefaultId  + 1 ) );
                object = (TestObject) db.load( TestObject.class,
                                               new Integer( TestObject.DefaultId + 1 ) );
                object.name = TestObject.DefaultName + ":1";
                _logger.println( "First: Modified to " + object );
                
                // Give the other thread a 2 second opportunity.
                sleep( Math.max( start + Wait - System.currentTimeMillis(), 0 ) );

                // Attempt to commit the transaction, must acquire a write
                // lock blocking until the first transaction completes.
                _logger.println( "First: Committing" );
                db.commit();
                _logger.println( "First: Committed" );
                db.close();
            } catch ( Exception except ) {
                _logger.println( "First: " + except );
                except.printStackTrace( _logger );
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

        
        private DataObjects    _jdo;


        private PrintWriter    _logger;

        
        public void run()
        {
            TestObject   object;
            Database     db = null;
            long         start;

            try {
                db = _jdo.getDatabase();
                db.begin();
                
                // Give the other thread a 2 second opportunity.
                sleep( Wait / 2 );
                start = System.currentTimeMillis();
                
                // Load first object and change something about it (otherwise will not write)
                _logger.println( "Second: Loading object " + ( TestObject.DefaultId + 1 ) );
                object = (TestObject) db.load( TestObject.class,
                                               new Integer( TestObject.DefaultId + 1 ) );
                object.name = TestObject.DefaultName + ":2";
                _logger.println( "Second: Modified to " + object );
                
                // Give the other thread a 2 second opportunity.
                sleep( start + Wait - System.currentTimeMillis() );
                start = System.currentTimeMillis();
                
                _logger.println( "Second: Loading object " + TestObject.DefaultId );
                object = (TestObject) db.load( TestObject.class,
                                               new Integer( TestObject.DefaultId ) );
                object.name = TestObject.DefaultName + ":2";
                _logger.println( "Second: Modified to " + object );

                // Give the other thread a 2 second opportunity.
                sleep( start + Wait - System.currentTimeMillis() );
                start = System.currentTimeMillis();
                
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
                except.printStackTrace( _logger );
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


