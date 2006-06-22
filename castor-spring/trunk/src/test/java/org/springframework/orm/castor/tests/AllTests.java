package org.springframework.orm.castor.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for org.springframework.orm.castor.tests");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestCastorTemplateFunctions.class);
        suite.addTestSuite(TestDAOWithCastorTemplate.class);
        suite.addTestSuite(TestDAOWithoutTemplate.class);
        suite.addTestSuite(TestLocalCastorFactoryBean.class);
        suite.addTestSuite(TestProductServiceWithDeclarativeTransactionDemarcation.class);
        suite.addTestSuite(TestProductServiceWithDeclarativeTransactionDemarcationShortened.class);
        suite.addTestSuite(TestProductServiceWithProgrammaticTransactionDemarcation.class);
        //$JUnit-END$
        return suite;
    }
}
