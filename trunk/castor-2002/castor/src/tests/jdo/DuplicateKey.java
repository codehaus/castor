package jdo;


import java.io.PrintWriter;
import org.exolab.castor.jdo.JDOSource;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.DuplicateIdentityException;


/**
 */
public class DuplicateKey
{


    private JDOSource      _jdo;


    private Database       _db;


    private PrintWriter    _logger;


    public DuplicateKey( JDOSource jdo, PrintWriter logger )
	throws PersistenceException
    {
	_jdo = jdo;
	_logger = logger;
	_db = jdo.getDatabase();
        _logger.println( "Opened JDO database " + _db );
    }


    public void run()
	throws PersistenceException
    {
	OQLQuery      oql;
	TestObject    object;

        // Open transaction in order to perform JDO operations
        _db.begin();

        // Determine if test object exists, if not create it.
        // If it exists, set the name to some predefined value
        // that this test will later override.
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
            object.name = TestObject.DefaultName;
            _logger.println( "Updating object: " + object );
        }
        _db.commit();


        // Attempt to create a new object with the same identity,
        // while one is in memory. Will report duplicate key from
        // the cache engine.
        _db.begin();
        oql.bind( new Integer( TestObject.DefaultId ) );
        object = (TestObject) oql.execute();

        object = new TestObject();
        object.id = TestObject.DefaultId;
        object.name = TestObject.DefaultName;
        _logger.println( "Creating new object: " + object );
        _logger.println( "Will report duplicate identity from cache engine" );
        try {
            _db.makePersistent( object );
            _logger.println( "Error: DuplicateIdentityException not thrown" );
        } catch ( DuplicateIdentityException except ) {
            _logger.println( "OK: DuplicateIdentityException thrown" );
        } catch ( Exception except ) {
            _logger.println( "Error: " + except );
        }
        _db.commit();
	    
        // Attempt to create a new object with the same identity,
        // in the database. Will report duplicate key from SQL engine.
        _db.begin();
        object = new TestObject();
        object.id = TestObject.DefaultId;
        object.name = TestObject.DefaultName;
        _logger.println( "Creating new object: " + object );
        _logger.println( "Will report duplicate identity from SQL engine" );
        try {
            _db.makePersistent( object );
            _logger.println( "Error: DuplicateIdentityException not thrown" );
        } catch ( DuplicateIdentityException except ) {
            _logger.println( "OK: DuplicateIdentityException thrown" );
        } catch ( Exception except ) {
            _logger.println( "Error: " + except );
        }
        _db.commit();
    }


}
