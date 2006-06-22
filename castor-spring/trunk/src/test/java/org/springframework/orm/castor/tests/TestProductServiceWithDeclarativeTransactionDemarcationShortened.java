package org.springframework.orm.castor.tests;

import java.util.Collection;

import org.exolab.castor.dao.Product;
import org.exolab.castor.service.ProductService;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestProductServiceWithDeclarativeTransactionDemarcationShortened extends BaseSpringTestCase {

   private ProductService productService;

    protected void setUp() throws Exception {
        super.setUp();
        this.productService = (ProductService) this.context.getBean ("myProductServiceDeclarativeShortened");
        assertNotNull (this.productService);
    }
    
    public void testLoadProduct() throws Exception {
        Product product = this.productService.load(1);
        assertNotNull (product);
    }
    
    public void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productService.create(product);

        Product productTest = this.productService.load(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
        
        // TODO [WG]: re-enable call to delete
        // this.productService.deleteProduct(productTest);
    }

    public void testFindAllProducts () throws Exception {
        Collection products = this.productService.find();
        assertNotNull (products);
        assertEquals (1, products.size());
    }

    public void testFindAllProductsById () throws Exception {
        Collection products = this.productService.findByName("product1");
        assertNotNull (products);
        assertEquals (1, products.size());
    }
    
    public void testEvictProductFromCache() throws Exception {

        
        Product product = this.productService.load(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
        
        boolean isCached = productService.isCached(product);
        assertTrue(isCached);
        
        productService.evict(product);
        
        isCached = productService.isCached(product);
        assertFalse(isCached);
        
    }
}
