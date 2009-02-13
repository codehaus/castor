package org.castor.spring.orm.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.castor.spring.orm.CastorObjectRetrievalFailureException;
import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;

public abstract class BaseSpringTestCaseAtDAOLevel extends BaseSpringTestCase {

    protected ProductDao productDAO;

    public void testLoadProduct() throws Exception {
        Product product = this.productDAO.loadProduct(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testLoadNotExistingProduct() throws Exception {
        try {
            productDAO.loadProduct(9);
        }
        catch (CastorObjectRetrievalFailureException e) {
            assertEquals(e.getClass().getName(), "org.castor.spring.orm.CastorObjectRetrievalFailureException");
        }
        catch (Throwable t) {
            fail ("Unexpected throwable");
        }
    }

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

    public void testFindAllProductsNative() throws Exception {
        Collection products = this.productDAO.findProductsNative (Product.class);
        assertNotNull (products);
        assertEquals (1, products.size());
    }

    public void testFindAllProducts() throws Exception {
        Collection products = productDAO.findProducts(Product.class);
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }


    public void testFindAllProductsById() throws Exception {
        Collection products = this.productDAO.findProducts (Product.class, "WHERE id = 1");
        assertNotNull (products);
        assertEquals (1, products.size());
    }

    public void testFindAllWithSimpleWhereClause() throws Exception {
        
        Collection products = productDAO.findProducts(Product.class, "where id = 1");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindAllWithWhereClauseWithPlaceholders() throws Exception {
        
        Collection products = productDAO.findProducts(Product.class, "where id = $1", new Object[] {new Integer(1) });
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindAllProductsByNamedQuery() throws Exception {
        Collection products = productDAO.findProductsByNamedQuery("allProducts");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindSelectedProductsByNamedQuery() throws Exception {
        Collection products = productDAO.findProductsByNamedQuery("selectedProducts", new Object[] { "product1" });
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

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

    public void testCreateProductAndFindAll() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productDAO.createProduct(product);
    
        Product productTest = this.productDAO.loadProduct(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());
    
        Collection products = this.productDAO.findProducts(Product.class);
        assertNotNull (products);
        assertFalse(products.isEmpty());
        assertEquals (2, products.size());
    
        Iterator iter = products.iterator();
        
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

    public void testIsCached() throws Exception {
        
        Product product = this.productDAO.loadProduct(1);
        assertNotNull (product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
        
        boolean isCached = productDAO.isProductCached(product);
        assertTrue(isCached);
        
    }

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
    
        Collection products = this.productDAO.findProducts(Product.class);
        assertNotNull (products);
        assertFalse(products.isEmpty());
        assertEquals (3, products.size());
    
        Iterator iter = products.iterator();
        
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
    
        List productsToDelete = new ArrayList();
        productsToDelete.add(this.productDAO.loadProduct(2));
        productsToDelete.add(this.productDAO.loadProduct(3));
        this.productDAO.deleteProducts(productsToDelete);
    
    }

}
