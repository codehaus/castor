package jdo;


import java.io.PrintWriter;
import org.exolab.castor.jdo.JDOSource;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectModifiedException;


/**
 */
public class MultiRead
{


    private JDOSource      _jdo;


    private Database       _db;


    private PrintWriter    _logger;


    public MultiRead( JDOSource jdo, PrintWriter logger )
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
        
        
        // Open a new transaction in order to conduct test
        _db.begin();
        oql.bind( new Integer( TestObject.DefaultId ) );
        object = (TestObject) oql.execute();
        object.name = object.name + ":1" ;
        _logger.println( "Loaded object: " + object );
        oql.bind( new Integer( TestObject.DefaultId ) );
        object = (TestObject) oql.execute();
        object.name = object.name + ":2" ;
        _logger.println( "Loaded object: " + object );

        _logger.println( "Committing JDO update" );
        _db.commit();

        // Open a new transaction in order to conduct test
        _db.begin();
        oql.bind( new Integer( TestObject.DefaultId ) );
        object = (TestObject) oql.execute();
        _logger.println( "Loaded object: " + object );

        _db.commit();
        _db.close();
    }


}


