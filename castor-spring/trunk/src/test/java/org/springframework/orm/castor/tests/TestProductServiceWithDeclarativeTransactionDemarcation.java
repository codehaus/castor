package org.springframework.orm.castor.tests;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;

import org.exolab.castor.dao.Product;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.service.ProductService;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestProductServiceWithDeclarativeTransactionDemarcation extends BaseSpringTestCase {

   private ProductService productService;

    protected void setUp() throws Exception {
        super.setUp();
        this.productService = (ProductService) this.context.getBean ("myProductServiceDeclarative");
        assertNotNull (this.productService);
    }
    
    protected void tearDown() throws Exception
    {
        JDOManager jdoManager = (JDOManager) this.context.getBean("jdoManager");
        Database db = jdoManager.getDatabase();
        db.begin();
        Connection connection = db.getJdbcConnection();
        connection.createStatement().execute("delete from spring.product where id <> 1");
        db.commit();
        db.close();
    }

    public void testLoadProduct() throws Exception {
        Product product = this.productService.load(1);
        assertNotNull (product);
    }
    
    public void testCreateProductAndFindAll() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("product 2");
        this.productService.create(product);

        Product productTest = this.productService.load(2);
        assertNotNull (productTest);
        assertEquals (2, productTest.getId());
        assertEquals ("product 2", productTest.getName());

        Collection products = this.productService.find();
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

        // TODO [WG]: re-enable call to delete
        // this.productDAO.delete (productTest);
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
}
