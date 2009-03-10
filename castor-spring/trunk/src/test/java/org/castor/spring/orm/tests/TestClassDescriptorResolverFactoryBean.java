package org.castor.spring.orm.tests;

import org.castor.spring.orm.support.ClassDescriptorResolverProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestClassDescriptorResolverFactoryBean extends BaseSpringTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetClassDescriptorResolver() throws Exception {
        ClassDescriptorResolverProxy classDescriptorResolver = 
            (ClassDescriptorResolverProxy) this.context.getBean("classDescriptorResolver");
        assertNotNull(classDescriptorResolver);
    }

    protected ClassPathXmlApplicationContext getApplicationContext() {
        return new ClassPathXmlApplicationContext("app-config.xml");
    }

}
