import java.util.Vector;
import java.util.Enumeration;
import org.exolab.jtf.CWTestCategory;
import org.exolab.jtf.CWTestCase;
import org.exolab.jtf.CWBaseApplication;
import org.exolab.exceptions.CWClassConstructorException;



public class TestHarness
    extends CWBaseApplication
{


    static Vector _categories = new Vector();


    static
    {
        _categories.addElement( jdo.JDOTests.class.getName() );
        _categories.addElement( xml.XMLTests.class.getName() );
    }



    static public void main( String args[] )
    {
        try {
            TestHarness harness;

            harness = new TestHarness();
            harness.run( args );
        } catch ( Exception except ) {
            except.printStackTrace();
        }
    }


    public TestHarness()
        throws CWClassConstructorException
    {
        super( "Castor" );
    }


    protected Enumeration getCategoryClassNames()
    {
        return _categories.elements();
    }


    protected String getApplicationName()
    {
        return getClass().getName();
    }


}
