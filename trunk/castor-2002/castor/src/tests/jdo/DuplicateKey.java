package jdo;


import java.io.PrintWriter;
import java.util.Enumeration;
import org.exolab.castor.jdo.DataObjects;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.DuplicateIdentityException;


/**
 */
public class DuplicateKey
{


    private DataObjects    _jdo;


    private Database       _db;


    private PrintWriter    _logger;


    public DuplicateKey( DataObjects jdo, PrintWriter logger )
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
        Enumeration   enum;

        // Open transaction in order to perform JDO operations
        _db.begin();

        // Determine if test object exists, if not create it.
        // If it exists, set the name to some predefined value
        // that this test will later override.
        oql = _db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE id = $1" );
        oql.bind( TestObject.DefaultId );
        enum = oql.execute();
        if ( enum.hasMoreElements() ) {
            object = (TestObject) enum.nextElement();
            object.name = TestObject.DefaultName;
            _logger.println( "Updating object: " + object );
        } else {
            object = new TestObject();
            object.id = TestObject.DefaultId;
            object.name = TestObject.DefaultName;
            _logger.println( "Creating new object: " + object );
            _db.create( object );
        }
        _db.commit();


        // Attempt to create a new object with the same identity,
        // while one is in memory. Will report duplicate key from
        // the cache engine.
        _db.begin();
        oql.bind( new Integer( TestObject.DefaultId ) );
        enum = oql.execute();
        while ( enum.hasMoreElements() )
            enum.nextElement();

        object = new TestObject();
        object.id = TestObject.DefaultId;
        object.name = TestObject.DefaultName;
        _logger.println( "Creating new object: " + object );
        _logger.println( "Will report duplicate identity from cache engine" );
        try {
            _db.create( object );
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
            _db.create( object );
            _logger.println( "Error: DuplicateIdentityException not thrown" );
        } catch ( DuplicateIdentityException except ) {
            _logger.println( "OK: DuplicateIdentityException thrown" );
        } catch ( Exception except ) {
            _logger.println( "Error: " + except );
        }
        _db.commit();
        _db.close();
    }


}
