package jdo;


import myapp.*;
import java.io.PrintWriter;
import org.odmg.Implementation;
import org.odmg.Database;
import org.odmg.Transaction;
import org.odmg.OQLQuery;
import org.odmg.ODMGException;


/**
 * Simple test for key duplicity. Will report to the console failure to create
 * two groups with the same identifier, once in memory (in the same transaction)
 * and once in the database (two different transactions).
 */
public class DuplicateKey
{


    private Implementation  odmg;


    private Database        db;


    private PrintWriter     logger;


    public DuplicateKey( Implementation odmg, String dbName, PrintWriter logger )
	throws ODMGException
    {
	this.odmg = odmg;
	this.logger = logger;
	db = odmg.newDatabase();
	db.open( dbName, db.OPEN_READ_WRITE );
    }


    public void run()
    {
	Transaction   tx;
	OQLQuery      oql;
	ProductGroup  group;

	try {
	    // Must be associated with an open transaction in order to
	    // use the ODMG database
	    tx = odmg.newTransaction();
	    tx.begin();
	    
	    oql = odmg.newOQLQuery();
	    oql.create( "SELECT pg FROM myapp.ProductGroup pg WHERE id = $1" );
	    // If no such group exists in the database, create a new
	    // object and persist it
	    oql.bind( new Integer( 3 ) );
	    group = (ProductGroup) oql.execute();
	    if ( group == null ) {
		group = new ProductGroup();
		group.id = 3;
		group.name = "new group";
		logger.println( "Creating new group: " + group );
		db.makePersistent( group );
	    } else {
		logger.println( "Query result: " + group );
	    }
	    logger.println( "Assured one group exists in the database" );
	    tx.commit();
	    
	    tx.begin();
	    oql.bind( new Integer( 3 ) );
	    group = (ProductGroup) oql.execute();
	    group = new ProductGroup();
	    group.id = 3;
	    group.name = "new group";
	    logger.println( "Creating new group: " + group );
	    logger.println( "Will report duplicate identity" );
	    try {
		db.makePersistent( group );
	    } catch ( Exception except ) {
		logger.println( except );
	    }
	    tx.commit();
	    
	    tx.begin();
	    
	    group = new ProductGroup();
	    group.id = 3;
	    group.name = "new group";
	    logger.println( "Creating new group: " + group );
	    logger.println( "Will report duplicate identity" );
	    try {
		db.makePersistent( group );
	    } catch ( Exception except ) {
		logger.println( except );
	    }
	    tx.commit();
	} catch ( Exception except ) {
	    logger.println( except );
	    except.printStackTrace( logger );
	}
    }


}
