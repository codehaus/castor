package org.springframework.orm.castor.tests;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestDAOWithoutTemplate extends BaseSpringTestCase {

    private ProductDao productDAO;

    protected void setUp() throws Exception {
        super.setUp();
        productDAO = (ProductDao) this.context.getBean ("myProductDao");
        assertNotNull (productDAO);
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
        Product product = productDAO.loadProduct (1);
        assertNotNull (product);
    }
    
    public void testLoadNotExistingProduct() throws Exception {
        try {
            Product product = productDAO.loadProduct (9);
        }
        catch (Exception e) {
            assertEquals(e.getClass().getName(), "org.springframework.orm.castor.CastorObjectRetrievalFailureException");
        }
    }
    
    public void testFindAll() throws Exception {
        Collection products = productDAO.findProducts(Product.class);
        assertNotNull(products);
        assertFalse (products.isEmpty());
        assertEquals(1, products.size());
        
        Product product = (Product) products.iterator().next();
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("product1", product.getName());
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

    public void testCreateProductsAndFindAll() throws Exception {
        
        Product product = new Product();
        product.setId(101);
        product.setName("product 101");
        productDAO.createProduct(product);
        
        Collection products = productDAO.findProducts(Product.class);
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

}
