package org.castor.spring.orm.tests;

import org.exolab.castor.jdo.JDOManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestLocalCastorFactoryBean extends BaseSpringTestCase {

    private JDOManager jdoManager;

    public void testGetJDOManager () throws Exception {
        this.jdoManager = (JDOManager) this.context.getBean("jdoManager");
        assertNotNull (this.jdoManager);
    }
    
    protected ClassPathXmlApplicationContext getApplicationContext() {
        return new ClassPathXmlApplicationContext("app-config.xml");
    }
    
}
