package org.springframework.orm.castor.tests;


import org.exolab.castor.dao.ProductDao;

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

}
