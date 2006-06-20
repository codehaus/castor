package org.springframework.orm.castor.tests;

import java.util.Collection;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestCastorTemplateFunctions extends BaseSpringTestCase {

    private ProductDao productDAO;

    protected void setUp() throws Exception {
        super.setUp();
        this.productDAO = (ProductDao) this.context.getBean ("myProductDao");
        assertNotNull (this.productDAO);
    }
    
    public void testLoad() throws Exception {
        Product product = this.productDAO.loadProduct (1);
        assertNotNull (product);
    }
    
    public void testCreate() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productDAO.createProduct(product);

        Product productTest = this.productDAO.loadProduct (2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
        
        // TODO [WG]: re-enable call to delete
        // this.productDAO.delete (productTest);
    }

    public void testFindAllProducts () throws Exception {
        Collection products = this.productDAO.findProducts (Product.class);
        assertNotNull (products);
        assertEquals (1, products.size());
    }

    public void testFindAllProductsById () throws Exception {
        Collection products = this.productDAO.findProducts (Product.class, "WHERE id = 1");
        assertNotNull (products);
        assertEquals (1, products.size());
    }

}
