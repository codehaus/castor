package org.castor.spring.orm.tests.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDaoWithInterceptor;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TestDAOWithCastorTemplate {

	@Autowired
	private JDOManager jdoManager;
	
	@Resource(name = "myProductDaoWithInterceptor")
	private ProductDaoWithInterceptor productDAO;

	@After
	public void disposeJDOManager() {
		JDOManager.disposeInstance("test");
	}

	
	@Before
	@After
	public void clearTables() throws Exception {
		Database db = jdoManager.getDatabase();
		db.begin();
		Connection connection = db.getJdbcConnection();
		connection.createStatement().execute(
				"delete from product where id <> 1");
		db.commit();
		db.close();
	}	 

	@Test
	@Rollback
	public void testLoad() throws Exception {
		Product product = productDAO.loadProduct(1);
		assertNotNull(product);
	}

	// @Test(expected=org.castor.spring.orm.CastorObjectRetrievalFailureException.class)
	// public void testLoadNotExistingProduct() throws Exception {
	// productDAO.loadProduct(9);
	// }

	@Test
	@Rollback
	public void testFindAll() throws Exception {
		Collection<Product> products = productDAO.findProduct(Product.class);
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(1, products.size());

		Product product = (Product) products.iterator().next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());
	}

	@Test
	@Rollback
	public void testFindAllDescending() throws Exception {
		Collection<Product> products = productDAO.findDescending(Product.class,
				"id");
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(1, products.size());

		Product product = (Product) products.iterator().next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());
	}

	@Test
	@Rollback
	public void testFindAllWithSimpleWhereClause() throws Exception {

		Collection<Product> products = productDAO.findProduct(Product.class,
				"where id = 1");
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(1, products.size());

		Product product = (Product) products.iterator().next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());
	}

	@Test
	@Rollback
	public void testFindAllWithWhereClauseWithPlaceholders() throws Exception {

		Collection<Product> products = productDAO.findProduct(Product.class,
				"where id = $1", new Object[] { new Integer(1) });
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(1, products.size());

		Product product = (Product) products.iterator().next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());
	}

	@Test
	@Rollback
	public void testFindAllProductsByNamedQuery() throws Exception {
		Collection<Product> products = productDAO
				.findProductByNamedQuery("allProducts");
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(1, products.size());

		Product product = (Product) products.iterator().next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());
	}

	@Test
	@Rollback
	public void testFindSelectedProductsByNamedQuery() throws Exception {
		Collection<Product> products = productDAO.findProductByNamedQuery(
				"selectedProducts", new Object[] { "product1" });
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(1, products.size());

		Product product = (Product) products.iterator().next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());
	}

	@Test
	@Rollback
	public void testCreateProductsAndFindAll() throws Exception {

		Product product = new Product();
		product.setId(101);
		product.setName("product 101");
		productDAO.createProduct(product);

		Collection<Product> products = productDAO.findProduct(Product.class);
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

	// TODO: re-include
	@Ignore
	@Rollback
	public void testCreateProductsAndFindAllDescending() throws Exception {

		Product product = new Product();
		product.setId(101);
		product.setName("product 101");
		productDAO.createProduct(product);

		Collection<Product> products = productDAO.findDescending(Product.class,
				"id desc");
		assertNotNull(products);
		assertFalse(products.isEmpty());
		assertEquals(2, products.size());

		Iterator<Product> productIter = products.iterator();

		product = productIter.next();
		assertNotNull(product);
		assertEquals(101, product.getId());
		assertEquals("product 101", product.getName());

		product = productIter.next();
		assertNotNull(product);
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());

	}

}
