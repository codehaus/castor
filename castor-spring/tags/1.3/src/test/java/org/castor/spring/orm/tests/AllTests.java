package org.castor.spring.orm.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for org.castor.spring.orm.tests");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestCastorTemplateFunctions.class);
        suite
                .addTestSuite(TestProductServiceWithProgrammaticTransactionDemarcation.class);
        suite
                .addTestSuite(TestProductServiceWithDeclarativeTransactionDemarcation.class);
        suite.addTestSuite(TestDAOWithoutTemplate.class);
//        suite
//                .addTestSuite(Test2PCProductServiceWithDeclarativeTransactionDemarcationShortened.class);
        suite
                .addTestSuite(TestProductServiceWithDeclarativeTransactionDemarcationShortened.class);
        suite.addTestSuite(TestDAOWithCastorTemplate.class);
        suite.addTestSuite(TestLocalCastorFactoryBean.class);
        suite.addTestSuite(TestLocalCastorFactoryBeanWithClassDescriptorResolver.class);
        suite.addTestSuite(TestClassDescriptorResolverFactoryBean.class);
        //$JUnit-END$
        return suite;
    }
}
