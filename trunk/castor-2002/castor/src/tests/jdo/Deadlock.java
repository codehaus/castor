package jdo;


import myapp.*;
import java.io.PrintWriter;
import org.odmg.Implementation;
import org.odmg.Database;
import org.odmg.Transaction;
import org.odmg.OQLQuery;
import org.odmg.ODMGException;
import org.exolab.castor.jdo.ODMG;


/**
 * Simple test for deadlock detection. Will report to the console two
 * concurrent transactions working on the same objects. The first transaction
 * will succeed, the second will fail.
 */
public class Deadlock
{


    private Implementation  odmg;


    private Database        db;


    private PrintWriter     logger;


    static final int FirstGroup = 3;


    static final int SecondGroup = 4;


    public Deadlock( Implementation odmg, String dbName, PrintWriter logger )
	throws ODMGException
    {
	this.odmg = odmg;
	this.logger = logger;
	db = odmg.newDatabase();
	db.open( dbName, db.OPEN_READ_WRITE );
    }


    public void run()
    {
	ProductGroup  group;
	OQLQuery      oql;
	OQLQuery      groupOql;
	Transaction   tx;

	try {
	    // Must be associated with an open transaction in order to
	    // use the ODMG database
	    tx = odmg.newTransaction();
	    tx.begin();
	    logger.println( "Create objects in database" );
	    oql = odmg.newOQLQuery();
	    oql.create( "SELECT pg FROM myapp.ProductGroup pg WHERE id = $1" );
	    
	    // Create two groups in the database
	    oql.bind( new Integer( FirstGroup ) );
	    group = (ProductGroup) oql.execute();
	    if ( group == null ) {
		group = new ProductGroup();
		group.id = FirstGroup;
		group.name = "first group";
		logger.println( "Creating new group: " + group );
		db.makePersistent( group );
	    }
	    oql.bind( new Integer( SecondGroup ) );
	    group = (ProductGroup) oql.execute();
	    if ( group == null ) {
		group = new ProductGroup();
		group.id = SecondGroup;
		group.name = "second group";
		logger.println( "Creating new group: " + group );
		db.makePersistent( group );
	    }
	    tx.commit();
	    
	    FirstThread first;
	    SecondThread second;
	    
	    first = new FirstThread();
	    first.odmg = odmg;
	    first.logger = logger;
	    first.start();
	    second = new SecondThread();
	    second.odmg = odmg;
	    second.logger = logger;
	    second.start();
	} catch ( Exception except ) {
	    logger.println( except );
	    except.printStackTrace( logger );
	}
    }


    static class FirstThread
	extends Thread
    {

	Implementation  odmg;

	PrintWriter     logger;

	public void run()
	{
	    Transaction  tx;
	    OQLQuery     oql;
	    ProductGroup group;
		
	    tx = odmg.newTransaction();
	    try {
		tx.begin();
		oql = odmg.newOQLQuery();
		oql.create( "SELECT pg FROM myapp.ProductGroup pg WHERE id = $1" );

		logger.println( "First: Loading group" );
		oql.bind( new Integer( FirstGroup ) );
		group = (ProductGroup) oql.execute();
		logger.println( "First: " + group );
		group.name = group.name + ":1";
		tx.lock( group, tx.WRITE );
		
		// Give the other thread a 2 second opportunity.
		sleep( 2000 );
		
		logger.println( "First: Loading group" );
		oql.bind( new Integer( SecondGroup ) );
		group = (ProductGroup) oql.execute();
		logger.println( "First: " + group );
		group.name = group.name + ":1";
		tx.lock( group, tx.WRITE );
		
		// Attempt to commit the transaction, must acquire a write
		// lock blocking until the first transaction completes.
		logger.println( "First: Committing" );
		tx.commit();
		logger.println( "First: Completed" );
	    } catch ( Exception except ) {
		except.printStackTrace( logger );
		if ( tx.isOpen() )
		    tx.abort();
	    }
	}

    }


    static class SecondThread
	extends Thread
    {

	Implementation  odmg;

	PrintWriter     logger;

	public void run()
	{
	    Transaction  tx;
	    OQLQuery     oql;
	    ProductGroup group;

	    tx = odmg.newTransaction();
	    try {
		tx.begin();
		oql = odmg.newOQLQuery();
		oql.create( "SELECT pg FROM myapp.ProductGroup pg WHERE id = $1" );
		
		// Give the other thread a 2 second opportunity.
		sleep( 1000 );
		
		// Retrieve the product (Acquires read lock) and change the
		// product.
		logger.println( "Second: Loading Group" );
		oql.bind( new Integer( SecondGroup ) );
		group = (ProductGroup) oql.execute();
		logger.println( "Second: " + group );
		group.name = group.name + ":2";
		tx.lock( group, tx.WRITE );

		// Give the other thread a 2 second opportunity.
		sleep( 2000 );
		
		// Retrieve the product (Acquires read lock) and change the
		// product.
		logger.println( "Second: Loading Group" );
		logger.println( "Will report deadlock detection" );
		oql.bind( new Integer( FirstGroup ) );
		group = (ProductGroup) oql.execute();
		logger.println( "Second: " + group );
		group.name = group.name + ":2";
		tx.lock( group, tx.WRITE );

		// Give the other thread a 2 second opportunity.
		sleep( 2000 );
		tx.commit();
	    } catch ( Exception except ) {
		logger.println( "Second: " + except );
		if ( tx.isOpen() )
		    tx.abort();
	    }
	}

    }


}


