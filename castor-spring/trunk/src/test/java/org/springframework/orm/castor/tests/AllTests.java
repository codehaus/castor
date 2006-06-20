package org.springframework.orm.castor.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for org.springframework.orm.castor.tests");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestLocalCastorFactoryBean.class);
        suite.addTestSuite(TestCastorTemplate.class);
        suite.addTestSuite(TestProductServiceWithDeclarativeTransactionDemarcation.class);
        suite.addTestSuite(TestProductServiceWithDeclarativeTransactionDemarcationShortened.class);
        suite.addTestSuite(TestProductServiceWithProgrammaticTransactionDemarcation.class);
        suite.addTestSuite(TestCastorTemplateFunctions.class);
        //$JUnit-END$
        return suite;
    }
}
