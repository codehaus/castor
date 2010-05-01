package org.castor.spring.orm.tests.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Iterator;

import org.castor.spring.orm.tests.BaseSpringTestCaseAtDAOLevel;
import org.exolab.castor.dao.Product;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration
public class TestDAOWithoutTemplate extends BaseSpringTestCaseAtDAOLevel {

	// protected void tearDown() throws Exception
	// {
	// super.tearDown();
	// JDOManager jdoManager = (JDOManager) this.context.getBean("jdoManager");
	// Database db = jdoManager.getDatabase();
	// db.begin();
	// Connection connection = db.getJdbcConnection();
	// connection.createStatement().execute("delete from product where id <> 1");
	// db.commit();
	// db.close();
	// }

	@Ignore
	@Rollback
	public void testCreateProductsAndFindAll() throws Exception {

		Product product = new Product();
		product.setId(101);
		product.setName("product 101");
		productDAO.createProduct(product);

		Collection<Product> products = productDAO.findProducts(Product.class);
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(2, products.size());

		Iterator<Product> productIter = products.iterator();

		product = productIter.next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());

		product = productIter.next();
		assertNotNull(product);
		assertEquals(101, product.getId());
		assertEquals("product 101", product.getName());

	}

}
