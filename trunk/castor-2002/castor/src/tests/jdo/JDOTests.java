package jdo;


import java.util.Vector;
import java.util.Enumeration;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.util.Logger;
import org.exolab.jtf.CWTestCategory;
import org.exolab.jtf.CWTestCase;
import org.exolab.exceptions.CWClassConstructorException;



public class JDOTests
    extends CWTestCategory
{


    public static final String DatabaseFile = "database.xml";


    public JDOTests()
        throws CWClassConstructorException
    {
        super( "jdo", "JDO Tests");
        
        CWTestCase tc;
        
        tc = new Concurrent();
        add( tc.name(), tc, true );
        tc = new Deadlock();
        add( tc.name(), tc, true );
        tc = new DuplicateKey();
        add( tc.name(), tc, true );
        tc = new ReadOnly();
        add( tc.name(), tc, true );
    }


    public static Database getDatabase()
        throws PersistenceException
    {
        JDO jdo;

        jdo = new JDO();
        jdo.setConfiguration( JDOTests.class.getResource( DatabaseFile ).toString() );
        jdo.setDatabaseName( "test" );
        jdo.setLogWriter( Logger.getSystemLogger() );
        return jdo.getDatabase();
    }


    public static Connection getJDBCConnection()
        throws SQLException
    {
        String driverClass;

        driverClass = "postgresql.Driver";
        try {
            Class.forName( driverClass );
        } catch ( ClassNotFoundException except ) {
            throw new RuntimeException( except.toString() );
        }

        String jdbcUri;

	jdbcUri = "jdbc:postgresql:test?user=test&password=test";
        return DriverManager.getConnection( jdbcUri );
    }



}
