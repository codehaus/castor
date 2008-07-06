package org.castor.spring.orm.tests;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDaoWithInterceptor;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestDAOWithCastorTemplate extends BaseSpringTestCase {

    private ProductDaoWithInterceptor productDAO;

    protected void setUp() throws Exception {
        super.setUp();
        productDAO = (ProductDaoWithInterceptor) this.context.getBean ("myProductDaoWithInterceptor");
        assertNotNull (productDAO);
    }
    
    protected ClassPathXmlApplicationContext getApplicationContext() {
        return new ClassPathXmlApplicationContext("app-config.xml");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        JDOManager jdoManager = (JDOManager) this.context.getBean("jdoManager");
        Database db = jdoManager.getDatabase();
        db.begin();
        Connection connection = db.getJdbcConnection();
        connection.createStatement().execute("delete from spring.product where id <> 1");
        db.commit();
        db.close();
    }


    public void testLoad () throws Exception {
        Product product = productDAO.loadProduct(1);
        assertNotNull (product);
    }
    
    public void testLoadNotExistingProduct() throws Exception {
        try {
            productDAO.loadProduct(9);
        }
        catch (Exception e) {
            assertEquals(e.getClass().getName(), "org.castor.spring.orm.CastorObjectRetrievalFailureException");
        }
    }
    
    public void testFindAll() throws Exception {
        Collection products = productDAO.findProduct(Product.class);
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindAllDescending() throws Exception {
        Collection products = productDAO.findDescending(Product.class, "id");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindAllWithSimpleWhereClause() throws Exception {
        
        Collection products = productDAO.findProduct(Product.class, "where id = 1");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindAllWithWhereClauseWithPlaceholders() throws Exception {
        
        Collection products = productDAO.findProduct(Product.class, "where id = $1", new Object[] {new Integer(1) });
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindAllProductsByNamedQuery() throws Exception {
        Collection products = productDAO.findProductByNamedQuery("allProducts");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }

    public void testFindSelectedProductsByNamedQuery() throws Exception {
        Collection products = productDAO.findProductByNamedQuery("selectedProducts", new Object[] { "product1" });
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
    }


    public void testCreateProductsAndFindAll() throws Exception {
        
        Product product = new Product();
        product.setId(101);
        product.setName("product 101");
        productDAO.createProduct(product);
        
        Collection products = productDAO.findProduct(Product.class);
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(2, products.size());
        
        Iterator productIter = products.iterator();
        
        product = (Product) productIter.next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());

        product = (Product) productIter.next();
        assertNotNull(product);
        assertEquals(101, product.getId());
        assertEquals("product 101", product.getName());

    }

    public void testCreateProductsAndFindAllDescending() throws Exception {
        
        Product product = new Product();
        product.setId(101);
        product.setName("product 101");
        productDAO.createProduct(product);
        
        Collection products = productDAO.findDescending(Product.class, "id desc");
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(2, products.size());
        
        Iterator productIter = products.iterator();
        
        product = (Product) productIter.next();
        assertNotNull(product);
        assertEquals(101, product.getId());
        assertEquals("product 101", product.getName());

        product = (Product) productIter.next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());

    }

}
