package ptf.jdo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Run all tests of the ptf.jdo package.
 */
public class TestAll extends TestCase {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite("All ptf.jdo performance tests");

        suite.addTest(ptf.jdo.rel1toN.TestAll.suite());

        return suite;
    }

    public TestAll(String name) { super(name); }
}
