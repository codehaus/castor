package org.castor.spring.orm.tests.template;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;
import org.exolab.castor.jdo.JDOManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration
public class TestCastorTemplateFunctions {

	@Resource(name="myProductDao")
	private ProductDao productDAO;
	

	@After
	public void disposeJDOManager() {
		JDOManager.disposeInstance("test");
	}

	@Test
    public void testLoadProduct() throws Exception {
        Product product = this.productDAO.loadProduct(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

	@Test(expected=org.castor.spring.orm.CastorObjectRetrievalFailureException.class)
    public void testLoadNotExistingProduct() throws Exception {
		productDAO.loadProduct(9);
    }

	@Test
    public void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productDAO.createProduct(product);
    
        Product productTest = this.productDAO.loadProduct (2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
        
        this.productDAO.deleteProduct (productTest);
    }

	@Test
    public void testFindAllProductsNative() throws Exception {
        Collection<Product> products = this.productDAO.findProductsNative (Product.class);
        assertNotNull (products);
        assertEquals (1, products.size());
    }

	@Test
    public void testFindAllProducts() throws Exception {
        Collection<Product> products = productDAO.findProducts(Product.class);
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }


	@Test
    public void testFindAllProductsById() throws Exception {
        Collection<Product> products = this.productDAO.findProducts (Product.class, "WHERE id = 1");
        assertNotNull (products);
        assertEquals (1, products.size());
    }

	@Test
    public void testFindAllWithSimpleWhereClause() throws Exception {
        
        Collection<Product> products = productDAO.findProducts(Product.class, "where id = 1");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

	@Test
    public void testFindAllWithWhereClauseWithPlaceholders() throws Exception {
        
        Collection<Product> products = productDAO.findProducts(Product.class, "where id = $1", new Object[] {new Integer(1) });
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

	@Test
    public void testFindAllProductsByNamedQuery() throws Exception {
        Collection<Product> products = productDAO.findProductsByNamedQuery("allProducts");
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
        Collection<Product> products = productDAO.findProductsByNamedQuery("selectedProducts", new Object[] { "product1" });
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
        
        Product product = this.productDAO.loadProduct(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
        
        boolean isCached = productDAO.isProductCached(product);
        assertTrue(isCached);
        
        productDAO.evictProduct(product);
        
        isCached = productDAO.isProductCached(product);
        assertFalse(isCached);
        
    }

	@Test
    public void testEvictAllProductsFromCache() throws Exception {
    
        
        Product product = this.productDAO.loadProduct(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
        
        boolean isCached = productDAO.isProductCached(product);
        assertTrue(isCached);
        
        productDAO.evictAll();
        
        isCached = productDAO.isProductCached(product);
        assertFalse(isCached);
        
    }

	@Test
    public void testCreateProductAndFindAll() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productDAO.createProduct(product);
    
        Product productTest = this.productDAO.loadProduct(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
    
        Collection<Product> products = this.productDAO.findProducts(Product.class);
        assertNotNull (products);
        assertFalse(products.isEmpty());
        assertEquals (2, products.size());
    
        Iterator<Product> iter = products.iterator();
        
        product = (Product) iter.next();
        assertNotNull (product);
        assertEquals (1, product.getId());
        assertEquals ("product1", product.getName());
    
        product = (Product) iter.next();
        assertNotNull (product);
        assertEquals (2, product.getId());
        assertEquals ("product 2", product.getName());
    
        this.productDAO.deleteProduct (productTest);
    }

	@Test
    public void testIsCached() throws Exception {
        
        Product product = this.productDAO.loadProduct(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
        
        boolean isCached = productDAO.isProductCached(product);
        assertTrue(isCached);
        
    }

	@Test
    public void testCreateProductAndRemoveAll() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productDAO.createProduct(product);
    
        product = new Product();
        product.setId(3);
        product.setName("product 3");
        this.productDAO.createProduct(product);
        
        Product productTest = this.productDAO.loadProduct(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
    
        productTest = this.productDAO.loadProduct(3);
        assertNotNull (productTest);
        assertEquals (3, productTest.getId());
        assertEquals ("product 3", productTest.getName());
    
        Collection<Product> products = this.productDAO.findProducts(Product.class);
        assertNotNull (products);
        assertFalse(products.isEmpty());
        assertEquals (3, products.size());
    
        Iterator<Product> iter = products.iterator();
        
        product = (Product) iter.next();
        assertNotNull (product);
        assertEquals (1, product.getId());
        assertEquals ("product1", product.getName());
    
        product = (Product) iter.next();
        assertNotNull (product);
        assertEquals (2, product.getId());
        assertEquals ("product 2", product.getName());
    
        product = (Product) iter.next();
        assertNotNull (product);
        assertEquals (3, product.getId());
        assertEquals ("product 3", product.getName());
    
        List<Product> productsToDelete = new ArrayList<Product>();
        productsToDelete.add(this.productDAO.loadProduct(2));
        productsToDelete.add(this.productDAO.loadProduct(3));
        this.productDAO.deleteProducts(productsToDelete);
    
    }

	

}
