package org.castor.spring.orm.tests;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestDAOWithoutTemplate extends BaseSpringTestCaseAtDAOLevel {

    protected void setUp() throws Exception {
        super.setUp();
        productDAO = (ProductDao) this.context.getBean ("myProductDao");
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
