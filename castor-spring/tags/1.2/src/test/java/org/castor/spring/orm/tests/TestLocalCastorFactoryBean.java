package org.castor.spring.orm.tests;

import org.exolab.castor.jdo.JDOManager;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestLocalCastorFactoryBean extends BaseSpringTestCase {

    private JDOManager jdoManager;

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testGetJDOManager () throws Exception {
        this.jdoManager = (JDOManager) this.context.getBean("jdoManager");
        assertNotNull (this.jdoManager);
    }
    
}
