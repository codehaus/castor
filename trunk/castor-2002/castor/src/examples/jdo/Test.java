package jdo;


import myapp.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.util.Logger;
import org.exolab.castor.xml.Marshaller;
import org.apache.xml.serialize.*;


public class Test
{


    public static final String DatabaseFile = "database.xml";

    
    public static final String Usage = "Usage: example jdo";
    
    
    public static void main( String[] args )
    {
        PrintWriter   logger;
        
        logger = new Logger( System.out ).setPrefix( "test" );
        
        try {
            JDO  jdo;
            
            logger.println( "Reading Java-SQL mapping from " + DatabaseFile );
            // JDO.loadDatabase( Test.class.getResource( DatabaseFile ).toString(), null, Logger.getSystemLogger() );
            jdo = new JDO();
            jdo.setLogWriter( Logger.getSystemLogger() );
            jdo.setConfiguration( Test.class.getResource( DatabaseFile ).toString() );
            jdo.setDatabaseName( "test" );
            test( jdo, logger );
        } catch ( Exception except ) {
            logger.println( except );
            except.printStackTrace( logger );
        }
    }
    
    
    public static void test( JDO jdo, PrintWriter logger )
        throws Exception
    {
        Database      db;
        Product       product;
        ProductGroup  group;
        ProductDetail detail;
        Computer      computer;
        OQLQuery      productOql;
        OQLQuery      groupOql;
        OQLQuery      computerOql;
        Enumeration   enum;

        db = jdo.getDatabase();

        db.begin();
        logger.println( "Begin transaction" );
        
        // Create OQL queries for all three object types
        productOql = db.getOQLQuery( "SELECT p FROM myapp.Product p WHERE id = $1" );
        groupOql = db.getOQLQuery( "SELECT g FROM myapp.ProductGroup g WHERE id = $1" );
        computerOql = db.getOQLQuery( "SELECT c FROM myapp.Computer c WHERE id = $1" );
        
        
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
        db.checkpoint();
        
        
        // If no such group exists in the database, create a new
        // object and persist it
        groupOql.bind( new Integer( 3 ) );
        group = (ProductGroup) groupOql.execute();
        if ( group == null ) {
            group = new ProductGroup();
            group.id = 3;
            group.name = "a group";
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
            product.name = "some product";
            product.price = 55;
            product.group = group;
            product.detail = new Vector();
            detail = new ProductDetail();
            detail.id = 1;
            detail.name = "one";
            detail.product = product;
            product.detail.addElement( detail );
            detail = new ProductDetail();
            detail.id = 2;
            detail.name = "two";
            detail.product = product;
            product.detail.addElement( detail );
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
            computer.name = "MyPC";
            computer.price = 300;
            computer.group = group;
            computer.detail = new Vector();
            detail = new ProductDetail();
            detail.id = 4;
            detail.name = "mouse";
            detail.product = computer;
            computer.detail.addElement( detail );
            detail = new ProductDetail();
            detail.id = 5;
            detail.name = "screen";
            detail.product = computer;
            computer.detail.addElement( detail );
            logger.println( "Creating new computer: " + computer );
            db.makePersistent( computer );
        } else {
            logger.println( "Query result: " + computer );
        }
        
        logger.println( "Commit transaction" );
        db.commit();


        Object     result;
        Serializer ser;

        db.begin();
        productOql = db.getOQLQuery( "SELECT p FROM myapp.Product p" );

        result = productOql.execute();
        ser = new XMLSerializer( new OutputFormat( Method.XML, null, true ) );
        ser.setOutputCharStream( logger );
        ser.asDocumentHandler().startDocument();
        ser.asDocumentHandler().startElement( "products", null );
        if ( result instanceof Enumeration ) {
            enum = (Enumeration) result;
            while( enum.hasMoreElements() )
                Marshaller.marshal( enum.nextElement(), ser.asDocumentHandler() );
        } else 
            Marshaller.marshal( result, ser.asDocumentHandler() );
        ser.asDocumentHandler().endElement( "products" );
        ser.asDocumentHandler().endDocument();
        db.commit();
        db.close();
    }



}


