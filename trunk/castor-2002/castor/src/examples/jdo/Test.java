package jdo;


import myapp.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import org.odmg.Implementation;
import org.odmg.Database;
import org.odmg.Transaction;
import org.odmg.OQLQuery;
import org.exolab.castor.util.Logger;
import org.exolab.castor.jdo.ODMG;



public class Test
{


    public static final String DatabaseFile = "database.xml";
    
    public static final String Usage = "Usage: example jdo";
    
    
    public static void main( String[] args )
    {
        PrintWriter   logger;
        
        logger = new Logger( System.out ).setPrefix( "test" );
        
        try {
            ODMG          odmg;
            Database      db;
            
            odmg = new ODMG();
            odmg.setLogWriter( Logger.getSystemLogger() );
            logger.println( "Reading Java-SQL mapping from " + DatabaseFile );
            odmg.loadDatabase( Test.class.getResource( DatabaseFile ).toString(), null );
            
            // Run the ODMG API test, see odmgTest()
            db = odmg.newDatabase();
            db.open( "test", db.OPEN_READ_WRITE );
            odmgTest( odmg, db, logger );
            db.close();

            /* (Future mechanism for SQL -> XML)
            org.exolab.castor.jdo.engine.QueryParser   qp;
            org.apache.xml.serialize.XMLSerializer ser;

            qp = new org.exolab.castor.jdo.engine.QueryParser( org.exolab.castor.jdo.engine.DatabaseSource.getDatabaseSource( "test" ), Product.class );
            ser = new org.apache.xml.serialize.XMLSerializer( System.out, null );
            qp.execute( ser.asDocumentHandler() );
            */
            
        } catch ( Exception except ) {
            logger.println( except );
            except.printStackTrace( logger );
        }
    }
    
    
    public static void odmgTest( Implementation odmg, Database db,
                                 PrintWriter logger )
        throws Exception
    {
        Product       product;
        ProductGroup  group;
        Computer      computer;
        OQLQuery      productOql;
        OQLQuery      groupOql;
        OQLQuery      computerOql;
        Enumeration   enum;
        Transaction   tx;

        // Must be associated with an open transaction in order to
        // use the ODMG database

        tx = odmg.newTransaction();
        tx.begin();
        logger.println( "Begin transaction" );
        
        // Create OQL queries for all three object types
        productOql = odmg.newOQLQuery();
        productOql.create( "SELECT p FROM myapp.Product p WHERE id = $1" );
        groupOql = odmg.newOQLQuery();
        groupOql.create( "SELECT g FROM myapp.ProductGroup g WHERE id = $1" );
        computerOql = odmg.newOQLQuery();
        computerOql.create( "SELECT c FROM myapp.Computer c WHERE id = $1" );
        
        
        // Look up the product and if found in the database,
        // delete this object from the database
        productOql.bind( new Integer( 4 ) );
        product = (Product) productOql.execute();
        if ( product != null ) {
            logger.println( "Deleting existing product: " + product );
            db.deletePersistent(  product );
        }
        
        // Look up the computer and if found in the database,
        // delete this object from the database
        computerOql.bind( new Integer( 6 ) );
        computer = (Computer) computerOql.execute();
        if ( computer != null ) {
            logger.println( "Deleting existing computer: " + computer );
            db.deletePersistent( computer );
        }
        
        // Look up the group and if found in the database,
        // delete this object from the database
        groupOql.bind( new Integer( 3 ) );
        group = (ProductGroup) groupOql.execute();
        if ( group != null ) {
            logger.println( "Deleting existing group: " + group );
            db.deletePersistent( group );
        }
        
        
        // Checkpoint commits all the updates to the database
        // but leaves the transaction (and locks) open
        logger.println( "Transaction checkpoint" );
        tx.checkpoint();
        
        
        // If no such group exists in the database, create a new
        // object and persist it
        groupOql.bind( new Integer( 3 ) );
        group = (ProductGroup) groupOql.execute();
        if ( group == null ) {
            group = new ProductGroup();
            group.id = 3;
            group.name = "new group";
            logger.println( "Creating new group: " + group );
        } else {
            logger.println( "Query result: " + group );
        }
        
        // If no such product exists in the database, create a new
        // object and persist it
        // Note: product uses group, so group object has to be
        //       created first, but can be persisted later
        productOql.bind( new Integer( 4 ) );
        product = (Product) productOql.execute();
        if ( product == null ) {
            product = new Product();
            product.id = 4;
            product.name = "new product";
            product.price = 55;
            product.group = group;
            logger.println( "Creating new product: " + product );
            db.makePersistent( product );
        } else {
            logger.println( "Query result: " + product );
        }
        
        // If no such computer exists in the database, create a new
        // object and persist it
        // Note: computer uses group, so group object has to be
        //       created first, but can be persisted later
        computerOql.bind( new Integer( 6 ) );
        computer = (Computer) computerOql.execute();
        if ( computer == null ) {
            computer = new Computer();
            computer.id = 6;
            computer.cpu = "Pentium";
            computer.name = "new product";
            computer.price = 300;
            computer.group = group;
            logger.println( "Creating new computer: " + computer );
            db.makePersistent( computer );
        } else {
            logger.println( "Query result: " + computer );
        }
        
        logger.println( "Commit transaction" );
        tx.commit();

        tx = odmg.newTransaction();
        tx.begin();
        productOql = odmg.newOQLQuery();
        productOql.create( "SELECT p FROM myapp.Product p" );
        Object result;

        result = productOql.execute();
        if ( result instanceof Enumeration ) {
            enum = (Enumeration) result;
            while( enum.hasMoreElements() ) {
                logger.println( "Query result: " + enum.nextElement() );
            }
        } else 
            logger.println( "Query result: " + result );
        tx.commit();
    }
    

}


