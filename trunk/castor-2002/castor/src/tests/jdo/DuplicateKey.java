package jdo;


import java.io.IOException;
import java.util.Enumeration;
import org.exolab.castor.jdo.DataObjects;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.exceptions.CWClassConstructorException;


/**
 */
public class DuplicateKey
    extends CWTestCase
{


    private Database       _db;


    public DuplicateKey()
        throws CWClassConstructorException
    {
        super( "TC03", "Test duplicate key" );
        try {
            _db = JDOTests.getDatabase();
        } catch ( Exception except ) {
            throw new CWClassConstructorException( except.toString() );
        }
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
                object.setName( TestObject.DefaultName );
                stream.writeVerbose( "Updating object: " + object );
            } else {
                object = new TestObject();
                stream.writeVerbose( "Creating new object: " + object );
                _db.create( object );
            }
            _db.commit();
            

            // Attempt to create a new object with the same identity,
            // while one is in memory. Will report duplicate key from
            // the cache engine.
            _db.begin();
            oql.bind( new Integer( TestObject.DefaultId ) );
            enum = oql.execute();
            while ( enum.hasMoreElements() )
                enum.nextElement();
            
            object = new TestObject();
            stream.writeVerbose( "Creating new object: " + object );
            stream.writeVerbose( "Will report duplicate identity from cache engine" );
            try {
                _db.create( object );
                result = false;
                stream.writeVerbose( "Error: DuplicateIdentityException not thrown" );
            } catch ( DuplicateIdentityException except ) {
                stream.writeVerbose( "OK: DuplicateIdentityException thrown" );
            } catch ( Exception except ) {
                result = false;
                stream.writeVerbose( "Error: " + except );
            }
            _db.commit();

	    
            // Attempt to create a new object with the same identity,
            // in the database. Will report duplicate key from SQL engine.
            _db.begin();
            object = new TestObject();
            stream.writeVerbose( "Creating new object: " + object );
            stream.writeVerbose( "Will report duplicate identity from SQL engine" );
            try {
                _db.create( object );
                result = false;
                stream.writeVerbose( "Error: DuplicateIdentityException not thrown" );
            } catch ( DuplicateIdentityException except ) {
                stream.writeVerbose( "OK: DuplicateIdentityException thrown" );
            } catch ( Exception except ) {
                // result = false;
                stream.writeVerbose( "Error: " + except );
            }
            _db.commit();
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
