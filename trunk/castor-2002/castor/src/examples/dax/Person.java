package dax;


import java.util.Enumeration;
import org.exolab.castor.dax.Attribute;
import org.exolab.castor.dax.AttributeSet;


public class Person
{

    public String uid;


    public String full;


    public String first;


    public String last;


    public String[] email;


    public String[] dc;


    public String  ou;


    public AttributeSet attrSet;


    public String getDN()
    {
	return "uid=" + uid + ",ou=" + ou + ",dc=" + dc[ 0 ] + ",dc=" + dc[ 1 ];
    }


    public String toString()
    {
	StringBuffer str;

	str = new StringBuffer();
	str.append( "DN: " ).append( getDN() ).append( '\n' );
	if ( uid != null )
	    str.append( "uid: " ).append( uid ).append( '\n' );
	if ( full != null )
	    str.append( "cn: " ).append( full ).append( '\n' );
	if ( first != null )
	    str.append( "sn: " ).append( first ).append( '\n' );
	if ( last != null )
	    str.append( "givenname: " ).append( last ).append( '\n' );
	if ( email != null ) {
	    for ( int i = 0 ; i < email.length ; ++i ) {
		str.append( "mail: " ).append( email[ i ]  ).append( '\n' );
	    }
	}
	if ( attrSet != null ) {
	    str.append( attrSet.toString() );
	}
	return str.toString();
    }


}
