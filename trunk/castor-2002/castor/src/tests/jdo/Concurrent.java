package jdo;


import java.io.PrintWriter;
import org.exolab.castor.jdo.JDOSource;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectModifiedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 */
public class Concurrent
{


    private JDOSource      _jdo;


    private Database       _db;


    private PrintWriter    _logger;


    private String         _jdbcUri;


    static final String    JDBCName = "jdbc value";


    static final String    JDOName = "jdo value";


    public Concurrent( JDOSource jdo, PrintWriter logger,
		       String driverClass, String jdbcUri )
	throws PersistenceException
    {
        _jdo = jdo;
        _logger = logger;
        _db = _jdo.getDatabase();
        _logger.println( "Opened JDO database " + _db );
        
	if ( driverClass == null )
	    driverClass = "postgresql.Driver";
	_logger.println( "Using JDBC driver " + driverClass );
        try {
            Class.forName( driverClass );
        } catch ( ClassNotFoundException except ) {
            throw new RuntimeException( except.toString() );
        }

	if ( jdbcUri == null )
	    _jdbcUri = "jdbc:postgresql:test?user=test&password=test";
	else
	    _jdbcUri = jdbcUri;
	_logger.println( "Using JDBC URI " + _jdbcUri );
    }


    public void run()
        throws PersistenceException, SQLException
    {
	OQLQuery      oql;
        TestObject    object;
	Connection    conn;

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
            _db.create( object );
        } else {
            object.name = TestObject.DefaultName;
            _logger.println( "Updating object: " + object );
        }
        _db.commit();
        
        
        // Open a new transaction in order to conduct test
        _db.begin();
        oql.bind( new Integer( TestObject.DefaultId ) );
        object = (TestObject) oql.execute();
        object.name = JDOName;
        
        // Perform direct JDBC access and override the value of that table
        conn = DriverManager.getConnection( _jdbcUri );
        conn.createStatement().execute( "UPDATE test_table SET name='" + JDBCName + "' WHERE id=" + TestObject.DefaultId );
        _logger.println( "Updated test object from JDBC" );
        conn.close();
        
        // Commit JDO transaction, this should report object modified
        // exception
        _logger.println( "Committing JDO update" );
        try {
            _db.commit();
            _logger.println( "Error: ObjectModifiedException not thrown" );
        } catch ( ObjectModifiedException except ) {
            _logger.println( "OK: ObjectModifiedException thrown" );
        } catch ( Exception except ) {
            _logger.println( "Error: " + except );
        }
        _db.close();
    }


}
