package jdo;


import java.util.Hashtable;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.util.Logger;
import org.exolab.castor.util.CommandLineOptions;



public class Test
{


    public static final String DatabaseFile = "database.xml";
    
    public static final String Usage =
        "Usage: test jdo <test-name> [<parameters>]\n" +
        "  duplicate    Duplicate primary key\n" +
        "  deadlock     Deadlock detection\n" +
        "  concurrenct  Concurrent locking\n" +
        "Concurrent require direct access to the database through JDBC.\n" +
        "The JDBC driver class and URI are passed as command line arguments.\n";

    
    public static void main( String[] args )
    {
        PrintWriter   logger;
        JDO           jdo;
        
        logger = new Logger( System.out ).setPrefix( "Test" );
        if ( args.length < 1 ) {
            System.out.println( Usage );
            System.exit( 1 );
        }
        
        try {
            // Load the database configuration and mapping file
            logger.println( "Reading database sources from " + DatabaseFile );
            JDO.loadDatabase( Test.class.getResource( DatabaseFile ).toString() );

            // Create a new JDO object to obtain a database from
            jdo = new JDO();
            jdo.setLogWriter( Logger.getSystemLogger() );
            jdo.setDatabaseName( "test" );
            
            if ( "duplicate".startsWith( args[ 0 ] ) ) {
                DuplicateKey dupKey;
                dupKey = new DuplicateKey( jdo, logger );
                dupKey.run();
            } else if ( "deadlock".startsWith( args[ 0 ] ) ) {
                Deadlock deadlock;
                deadlock = new Deadlock( jdo, logger );
                deadlock.run();
            } else if ( "concurrent".startsWith( args[ 0 ] ) ) {
                Concurrent concurrent;
                concurrent = new Concurrent( jdo, logger,
                                             args.length > 1 ? args[ 1 ] : null,
                                             args.length > 2 ? args[ 2 ] : null );
                concurrent.run();
            } else {
                System.out.println( Usage );
                System.exit( 1 );
            }
        } catch ( Exception except ) {
            logger.println( except );
            except.printStackTrace( logger );
        }
    }
        

}


