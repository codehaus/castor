package ptf.jdo.rel1toN;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Run all tests of the ptf.jdo.rel1toN package.
 */
public class TestAll extends TestCase {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite("All ptf.jdo.rel1toN performance tests");

        suite.addTest(TestCreate.suite());
        suite.addTest(TestLoadBiNto1.suite());
        suite.addTest(TestLoadUniNto1.suite());
        suite.addTest(TestLoadBi1toN.suite());
        suite.addTest(TestLoadLazy1toN.suite());
        suite.addTest(TestLoadUni1toN.suite());
        suite.addTest(TestRemove.suite());

        return suite;
    }

    public TestAll(String name) { super(name); }
}
