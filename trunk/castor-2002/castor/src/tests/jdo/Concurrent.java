package jdo;


import myapp.*;
import java.io.PrintWriter;
import org.odmg.Implementation;
import org.odmg.Database;
import org.odmg.Transaction;
import org.odmg.OQLQuery;
import java.sql.Connection;
import java.sql.DriverManager;


/**
 * Simple test for key duplicity. Will report to the console failure to create
 * two groups with the same identifier, once in memory (in the same transaction)
 * and once in the database (two different transactions).
 */
public class Concurrent
{


    private Implementation  odmg;


    private Database        db;


    private PrintWriter     logger;


    private String          jdbcUri;


    static final int        GroupId = 3;


    public Concurrent( Implementation odmg, String dbName, PrintWriter logger,
		       String driverClass, String jdbcUri )
	throws Exception
    {
	this.odmg = odmg;
	this.logger = logger;
	db = odmg.newDatabase();
	db.open( dbName, db.OPEN_READ_WRITE );

	if ( driverClass == null )
	    driverClass = "postgresql.Driver";
	logger.println( "Using JDBC driver " + driverClass );
	Class.forName( driverClass );

	if ( jdbcUri == null )
	    this.jdbcUri = "jdbc:postgresql:test?user=test&password=test";
	else
	    this.jdbcUri = jdbcUri;
	logger.println( "Using JDBC URI " + jdbcUri );
    }


    public void run()
    {
	Transaction   tx;
	OQLQuery      oql;
	ProductGroup  group;
	Connection    conn;

	try {
	    // Must be associated with an open transaction in order to
	    // use the ODMG database
	    tx = odmg.newTransaction();
	    tx.begin();
	    
	    oql = odmg.newOQLQuery();
	    oql.create( "SELECT pg FROM myapp.ProductGroup pg WHERE id = $1" );
	    // If no such group exists in the database, create a new
	    // object and persist it
	    oql.bind( new Integer( GroupId ) );
	    group = (ProductGroup) oql.execute();
	    if ( group == null ) {
		group = new ProductGroup();
		group.id = GroupId;
		group.name = "new group";
		logger.println( "Creating new group: " + group );
		db.makePersistent( group );
	    } else {
		group.name = "new group";
		logger.println( "Query result: " + group );
	    }
	    logger.println( "Assured one group exists in the database" );
	    tx.commit();
	    
	    tx.begin();
	    oql.bind( new Integer( GroupId ) );
	    group = (ProductGroup) oql.execute();
	    
	    JdbcThread jdbc;
	    
	    jdbc = new JdbcThread();
	    jdbc.logger = logger;
	    jdbc.jdbcUri = jdbcUri;
	    jdbc.start();
	    // Give JDBC thread 2 seconds to wait for lock
	    jdbc.join();
	    group.name = group.name + "XXX";
	    
	    tx.commit();
	    db.close();
	} catch ( Exception except ) {
	    logger.println( except );
	    except.printStackTrace( logger );
	}
    }


    static class JdbcThread
	extends Thread
    {

	PrintWriter logger;

	String      jdbcUri;

	public void run()
	{
	    Connection conn;

	    try {
		logger.println( "Attempting to update group from JDBC" );
		conn = DriverManager.getConnection( jdbcUri );
		conn.createStatement().execute( "UPDATE prod_group SET name='old_group' WHERE id=" + GroupId );
		logger.println( "Succeeded to update group from JDBC" );
		conn.close();
	    } catch ( Exception except ) {
		logger.println( "JDBC: " + except );
	    }
	}

    }


}
