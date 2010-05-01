package org.castor.spring.orm.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.dao.Product;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.service.ProductService;
import org.junit.After;
import org.junit.Test;

public abstract class BaseSpringTestCaseWithTransactionDemarcation {

    protected ProductService productService = null;

	@After
	public void disposeJDOManager() {
		JDOManager.disposeInstance("test");
	}

    @Test
    public void testLoadProduct() throws Exception {
        Product product = this.productService.load(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    @Test(expected=org.castor.spring.orm.CastorObjectRetrievalFailureException.class)
    public void testLoadNotExistingProduct() throws Exception {
            productService.load(9);
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productService.create(product);
    
        Product productTest = this.productService.load(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
        
        this.productService.delete(productTest);
    }

    @Test
    public void testFindAllProductsNative() throws Exception {
        Collection<Product> products = this.productService.findNative();
        assertNotNull (products);
        assertEquals (1, products.size());
    }

    @Test
    public void testFindAllProducts() throws Exception {
        Collection<Product> products = this.productService.find();
        assertNotNull (products);
        assertEquals (1, products.size());
    }

    @Test
    public void testFindAllProductsById() throws Exception {
        Collection<Product> products = this.productService.findByName("product1");
        assertNotNull (products);
        assertEquals (1, products.size());
    }
    
    @Test
    public void testFindAllProductsByNamedQuery() throws Exception {
        Collection<Product> products = this.productService.findByNamedQuery("allProducts");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    @Test
    public void testFindSelectedProductsByNamedQuery() throws Exception {
        Collection<Product> products = this.productService.findByNamedQuery("selectedProducts", new Object[] { "product1" });
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    

    @Test
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

    @Test
    public void testEvictAllProductsFromCache() throws Exception {
    
        
        Product product = this.productService.load(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
        
        boolean isCached = productService.isCached(product);
        assertTrue(isCached);
        
        productService.evictAll();
        
        isCached = productService.isCached(product);
        assertFalse(isCached);
        
    }

    @Test
    public void testCreateProductAndFindAll() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productService.create(product);
    
        Product productTest = this.productService.load(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
    
        Collection<Product> products = this.productService.find();
        assertNotNull (products);
        assertFalse(products.isEmpty());
        assertEquals (2, products.size());
    
        Iterator<Product> iter = products.iterator();
        
        product = iter.next();
        assertNotNull (product);
        assertEquals (1, product.getId());
        assertEquals ("product1", product.getName());
    
        product = iter.next();
        assertNotNull (product);
        assertEquals (2, product.getId());
        assertEquals ("product 2", product.getName());
    
        // TODO [WG]: re-enable call to delete
        this.productService.delete (productTest);
    }

    @Test
    public void testIsCached() throws Exception {
        
        Product product = this.productService.load(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
        
        boolean isCached = productService.isCached(product);
        assertTrue(isCached);
        
    }

    @Test
    public void testCreateProductAndRemoveAll() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productService.create(product);
    
        product = new Product();
        product.setId(3);
        product.setName("product 3");
        this.productService.create(product);
        
        Product productTest = this.productService.load(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());

        productTest = this.productService.load(3);
        assertNotNull (productTest);
        assertEquals (3, productTest.getId());
        assertEquals ("product 3", productTest.getName());

        Collection<Product> products = this.productService.find();
        assertNotNull (products);
        assertFalse(products.isEmpty());
        assertEquals (3, products.size());
    
        Iterator<Product> iter = products.iterator();
        
        product = iter.next();
        assertNotNull (product);
        assertEquals (1, product.getId());
        assertEquals ("product1", product.getName());
    
        product = iter.next();
        assertNotNull (product);
        assertEquals (2, product.getId());
        assertEquals ("product 2", product.getName());

        product = iter.next();
        assertNotNull (product);
        assertEquals (3, product.getId());
        assertEquals ("product 3", product.getName());

        List<Product> productsToDelete = new ArrayList<Product>();
        productsToDelete.add(this.productService.load(2));
        productsToDelete.add(this.productService.load(3));
        this.productService.delete(productsToDelete);

    }

//  public void testCreateProductAndUpdate() throws Exception {
//  Product product = new Product();
//  product.setId(2);
//  product.setName("product 2");
//  this.productService.create(product);
//
//  Product productTest = this.productService.load(2);
//  assertNotNull (productTest);
//  assertEquals (2, productTest.getId());
//  assertEquals ("product 2", productTest.getName());
//
//  Collection products = this.productService.find();
//  assertNotNull (products);
//  assertFalse(products.isEmpty());
//  assertEquals (2, products.size());
//
//  Iterator iter = products.iterator();
//  
//  product = (Product) iter.next();
//  assertNotNull (product);
//  assertEquals (1, product.getId());
//  assertEquals ("product1", product.getName());
//
//  product = (Product) iter.next();
//  assertNotNull (product);
//  assertEquals (2, product.getId());
//  assertEquals ("product 2", product.getName());
//
//  productTest.setName("product 2X");
//  
//  productService.update(productTest);
//  
//  productTest = this.productService.load(2);
//  assertNotNull (productTest);
//  assertEquals (2, productTest.getId());
//  assertEquals ("product 2X", productTest.getName());
//  
//}
    
    
}
