package xml;


import org.exolab.jtf.CWTestCategory;
import org.exolab.jtf.CWTestCase;
import org.exolab.exceptions.CWClassConstructorException;

import junit.framework.TestSuite;
import junit.framework.TestCase;
import junit.framework.Assert;
import harness.TestHarness;
import harness.CastorTestCase;


public class XMLCategory extends TestHarness {

    public static final String MappingFile = "mapping.xml";

    public XMLCategory( TestHarness harness, String name, String description, Object obj ) {
        super( harness, name, description );
    }


}
