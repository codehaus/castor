package org.castor.spring.orm.tests;

import org.exolab.castor.jdo.JDOManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestLocalCastorFactoryBeanWithClassDescriptorResolver extends BaseSpringTestCase {

    private JDOManager jdoManager;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected ClassPathXmlApplicationContext getApplicationContext() {
        return new ClassPathXmlApplicationContext("app-config-with-resolver.xml");
    }
    
    /*
    public void testGetJDOManager () throws Exception {
        this.jdoManager = (JDOManager) this.context.getBean("jdoManager");
        assertNotNull (this.jdoManager);
    }
    */
    
}
