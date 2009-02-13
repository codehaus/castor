package org.castor.spring.orm.tests;


import org.exolab.castor.dao.ProductDao;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestCastorTemplateFunctions 
    extends BaseSpringTestCaseAtDAOLevel {

    protected void setUp() throws Exception {
        super.setUp();
        this.productDAO = (ProductDao) this.context.getBean ("myProductDao");
        assertNotNull (this.productDAO);
    }
    
    protected ClassPathXmlApplicationContext getApplicationContext() {
        return new ClassPathXmlApplicationContext("app-config.xml");
    }

}
