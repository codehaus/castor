package org.springframework.orm.castor.tests;

import java.util.Collection;

import org.exolab.castor.dao.Product;
import org.exolab.castor.service.ProductService;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestProductServiceWithProgrammaticTransactionDemarcation extends BaseSpringTestCase {

   private static final int DEFAULT_ID = 2;
   
   private ProductService productService;

    protected void setUp() throws Exception {
        super.setUp();
        this.productService = (ProductService) context.getBean ("myProductService");
        assertNotNull (this.productService);
    }
    
    public void testLoadProduct() throws Exception {
        Product product = this.productService.loadProduct (1);
        assertNotNull (product);
    }
    
    public void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setId(DEFAULT_ID);
        product.setName("product 2");
        this.productService.createProduct(product);

        Product productTest = this.productService.loadProduct (2);
        assertNotNull (productTest);
        assertEquals (DEFAULT_ID, productTest.getId());
        assertEquals ("product 2", productTest.getName());
    }

    public void testFindAllProducts () throws Exception {
        Collection products = this.productService.findProducts();
        assertNotNull (products);
        assertEquals (1, products.size());
    }

    public void testFindAllProductsById () throws Exception {
        Collection products = this.productService.findProductsByName("product1");
        assertNotNull (products);
        assertEquals (1, products.size());
    }
    
}
