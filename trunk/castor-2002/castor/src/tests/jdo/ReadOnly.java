package jdo;


import java.io.IOException;
import java.util.Enumeration;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.jtf.CWVerboseStream;
import org.exolab.jtf.CWTestCase;
import org.exolab.exceptions.CWClassConstructorException;


public class ReadOnly
    extends CWTestCase
{


    private Database       _db;


    static final String    NewName = "new name";


    public ReadOnly()
        throws CWClassConstructorException
    {
        super( "TC04", "Read only test" );
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
            } else {
                object = new TestObject();
                stream.writeVerbose( "Creating new object: " + object );
                _db.create( object );
            } 
            _db.commit();

            _db.begin();
            oql.bind( TestObject.DefaultId );
            enum = oql.execute( Database.ReadOnly );
            object = (TestObject) enum.nextElement();
            stream.writeVerbose( "Retrieved object: " + object );
            object.setName( NewName );
            stream.writeVerbose( "Modified object: " + object );
            _db.commit();
            
            _db.begin();
            oql.bind( TestObject.DefaultId );
            enum = oql.execute( Database.ReadOnly );
            object = (TestObject) enum.nextElement();
            stream.writeVerbose( "Retrieved object: " + object );
            if ( object.getName().equals( NewName ) ) {
                result = false;
                stream.writeVerbose( "Error: modified object was stored" );
            } else
                stream.writeVerbose( "OK: object is read-only" );
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

