package dax;


import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPv2;
import netscape.ldap.LDAPEntry;
import org.exolab.castor.dax.desc.FieldDesc;
import org.exolab.castor.dax.desc.DNFieldDesc;
import org.exolab.castor.dax.desc.ObjectDesc;
import org.exolab.castor.dax.engine.Engine;


public class Test
{


    public static void main( String[] args )
    {
	try {
	    LDAPConnection conn;
	    LDAPEntry      entry;

	    conn = new LDAPConnection();
	    conn.connect( "localhost", LDAPv2.DEFAULT_PORT );
	    conn.authenticate( "dc=exoffice,dc=com", "secret" );

	    String dn;

	    dn = "uid=arkin, ou=People, dc=exoffice, dc=com";

	    FieldDesc[] fields;
	    DNFieldDesc dnField;
	    FieldDesc[] dnFields;
	    FieldDesc   attrSet;
	    ObjectDesc  objDesc;
	    Engine      engine;

	    fields = new FieldDesc[ 7 ];
	    fields[ 0 ] = new FieldDesc( Person.class.getField( "uid" ), "uid" );
	    fields[ 1 ] = new FieldDesc( Person.class.getField( "first" ), "sn" );
	    fields[ 2 ] = new FieldDesc( Person.class.getField( "last" ), "givenname" );
	    fields[ 3 ] = new FieldDesc( Person.class.getField( "full" ), "cn" );
	    fields[ 4 ] = new FieldDesc( Person.class.getField( "uid" ), "uid" );
	    fields[ 5 ] = new FieldDesc( Person.class.getField( "ou" ), "ou" );
	    fields[ 6 ] = new FieldDesc( Person.class.getField( "email" ), "mail" );
	    
	    dnFields = new FieldDesc[ 3 ];
	    dnFields[ 0 ] = new FieldDesc( Person.class.getField( "uid" ), "uid" );
	    dnFields[ 1 ] = new FieldDesc( Person.class.getField( "ou" ), "ou" );
	    dnFields[ 2 ] = new FieldDesc( Person.class.getField( "dc" ), "dc" );
	    dnField = new DNFieldDesc( null, dnFields );

	    attrSet = new FieldDesc( Person.class.getField( "attrSet" ), "--" );
	    objDesc = new ObjectDesc( Person.class, "person", fields, dnField, attrSet, null );

	    Person person;

	    engine = new Engine( objDesc );
	    person = (Person) engine.read( conn, dn );
	    System.out.println( person );

	    person.first = null;
	    person.email = new String[] { "arkin@exoffice.com" };
	    engine.update( conn, person );
	    person = (Person) engine.read( conn, dn );
	    System.out.println( person );

	} catch ( Exception except ) {
	    System.out.println( except );
	    except.printStackTrace();
	}
    }


}
