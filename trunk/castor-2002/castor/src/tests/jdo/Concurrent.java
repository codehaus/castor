package jdo;


import java.io.IOException;
import java.util.Enumeration;
import java.sql.Connection;
import java.sql.SQLException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.exceptions.CWClassConstructorException;


/**
 */
public class Concurrent
    extends CWTestCase
{


    private Database       _db;


    private Connection     _conn;


    static final String    JDBCName = "jdbc value";


    static final String    JDOName = "jdo value";


    public Concurrent()
        throws CWClassConstructorException
    {
        super( "TC01", "Test dirty checking" );
        try {
            _db = JDOTests.getDatabase();
            _conn = JDOTests.getJDBCConnection(); 
        } catch ( Exception except ) {
            throw new CWClassConstructorException( except.toString() );
        }

        /*        
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
        */
    }


    public void preExecute()
    {
        super.preExecute();
    }


    public void postExecute()
    {
        super.postExecute();
    }


    public boolean run( CWVerboseStream stream )
    {
        boolean result = true;

        try {
            OQLQuery      oql;
            TestObject    object;
            Enumeration   enum;

            // Open transaction in order to perform JDO operations
            _db.begin();
        
            // Determine if test object exists, if not create it.
            // If it exists, set the name to some predefined value
            // that this test will later override.
            oql = _db.getOQLQuery( "SELECT object FROM jdo.TestObject object WHERE Id = $1" );
            oql.bind( TestObject.DefaultId );
            enum = oql.execute();
            if ( enum.hasMoreElements() ) {
                object = (TestObject) enum.nextElement();
                stream.writeVerbose( "Retrieved object: " + object );
                object.setName( TestObject.DefaultName );
            } else {
                object = new TestObject();
                stream.writeVerbose( "Creating new object: " + object );
                _db.create( object );
            }
            _db.commit();
            
            
            // Open a new transaction in order to conduct test
            _db.begin();
            oql.bind( new Integer( TestObject.DefaultId ) );
            object = (TestObject) oql.execute().nextElement();
            object.setName( JDOName );
            
            // Perform direct JDBC access and override the value of that table
            _conn.createStatement().execute( "UPDATE test_table SET name='" + JDBCName + "' WHERE id=" + TestObject.DefaultId );
            stream.writeVerbose( "Updated test object from JDBC" );
            _conn.close();
        
            // Commit JDO transaction, this should report object modified
            // exception
            stream.writeVerbose( "Committing JDO update" );
            try {
                _db.commit();
                stream.writeVerbose( "Error: ObjectModifiedException not thrown" );
                result = false;
            } catch ( ObjectModifiedException except ) {
                stream.writeVerbose( "OK: ObjectModifiedException thrown" );
            }
            _db.close();
        } catch ( Exception except ) {
            try {
                stream.writeVerbose( "Error: " + except );
            } catch ( IOException except2 ) { }
            except.printStackTrace();
            result = false;
        }
        return result;
    }


}

