package dax;


import java.net.URL;
import org.xml.sax.InputSource;
import org.exolab.castor.dax.engine.DirectorySourceImpl;
import org.exolab.castor.dax.Directory;
import org.exolab.castor.dax.DirectorySource;


public class Test
{


    public static void main( String[] args )
    {
	try {

	    DirectorySourceImpl.loadMapping( new InputSource( Test.class.getResource( "mapping.xml" ).toString() ) );

	    DirectorySourceImpl dirs;
	    Directory           dir;
	    User                user;

	    dirs = new DirectorySourceImpl();
	    dirs.setURL( "ldap://localhost/ou=people,dc=exoffice,dc=com" );
	    dir = dirs.getDirectory( "dc=exoffice,dc=com", "secret" );
	    dir.begin();
	    user = (User) dir.read( "kvisco" );
	    if ( user == null ) {
		user = new User();
		user.uid = "kvisco";
		user.first = "Keith";
		user.last = "Visco";
		user.full = "Keith Visco";
		user.email = new String[] { "kvisco@exoffice.com" };
		System.out.println( "Creating: " + user );
		dir.create( user );
	    } else {
		System.out.println( "Query: " + user );
		user.first = user.first + ".";
	    }
	    dir.commit();


	} catch ( Exception except ) {
	    System.out.println( except );
	    except.printStackTrace();
	}
    }


}
