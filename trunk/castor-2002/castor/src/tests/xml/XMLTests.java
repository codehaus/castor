package xml;


import org.exolab.jtf.CWTestCategory;
import org.exolab.jtf.CWTestCase;
import org.exolab.exceptions.CWClassConstructorException;



public class XMLTests
    extends CWTestCategory
{


    public static final String MappingFile = "mapping.xml";


    public XMLTests( String name, String description )
        throws CWClassConstructorException
    {
        super( name, description );
        
        CWTestCase tc;
        
        tc = new Mapping();
        add( tc.name(), tc, true );
    }


}
