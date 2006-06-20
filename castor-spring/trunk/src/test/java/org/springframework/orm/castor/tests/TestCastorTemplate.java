package org.springframework.orm.castor.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;
import org.exolab.castor.dao.ProductDaoWithInterceptor;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestCastorTemplate extends BaseSpringTestCase {

    private static final Log LOG = LogFactory.getLog(TestCastorTemplate.class);
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testCastorTemplate () throws Exception {
        ProductDao productDAO = (ProductDao) this.context.getBean ("myProductDao");
        assertNotNull (productDAO);
        Product product = productDAO.loadProduct (1);
        assertNotNull (product);
    }
    
    public void testCastorTemplateSoph() throws Exception {
        ProductDao productDAO = (ProductDao) this.context.getBean ("mySophisticatedProductDao");
        assertNotNull (productDAO);
        Product product = productDAO.loadProduct (1);
        assertNotNull (product);
    }

    public void testCastorInterceptor() throws Exception {
        ProductDaoWithInterceptor productDAO = (ProductDaoWithInterceptor) this.context.getBean ("myProductDaoWithInterceptor");
        assertNotNull (productDAO);
        Product product = productDAO.load (1);
        assertNotNull (product);
    }

    public void testLoadNotExistingProductWithCastorTemplate () throws Exception {
        ProductDao productDAO = (ProductDao) this.context.getBean ("myProductDao");
        assertNotNull (productDAO);
        try {
            Product product = productDAO.loadProduct (9);
        }
        catch (Exception e) {
            assertEquals(e.getClass().getName(), "org.springframework.orm.castor.CastorObjectRetrievalFailureException");
        }
    }
    
}
