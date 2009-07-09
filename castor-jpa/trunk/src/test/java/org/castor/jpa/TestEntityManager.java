package org.castor.jpa;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import junit.framework.TestCase;

public class TestEntityManager extends TestCase {

	private EntityManagerFactory factory;

	@Override
	protected void setUp() throws Exception {
		factory = javax.persistence.Persistence
				.createEntityManagerFactory("castor");
	}

	public void testFind() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();

		transaction.begin();
		Product product = manager.find(Product.class, Integer.valueOf(1));
		transaction.commit();
		assertNotNull(product);
		assertEquals(1, product.getId());

		manager.close();
	}

	public void testFindWithoutActiveTransaction() throws Exception {

		EntityManager manager = factory.createEntityManager();

		Product product = null;
		try {
			product = manager.find(Product.class, Integer.valueOf(1));
			fail("Expected a TransactionRequiredException");
		} catch (TransactionRequiredException e) {
			// nothing to do, as this is expected
		}
		assertNull(product);

		manager.close();
	}

	public void testNotFound() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		Product product = manager.find(Product.class, Integer.valueOf(100));
		transaction.commit();
		assertNull(product);

		manager.close();
	}

	public void testQuery() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		Query query = manager
				.createQuery("select p from org.castor.jpa.Product p where id = 1");
		Product product = (Product) query.getSingleResult();
		transaction.commit();
		assertNotNull(product);
		assertEquals(1, product.getId());

		manager.close();
	}

	public void testNamedQuery() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		Query query = manager.createNamedQuery("allProducts");
		Product product = (Product) query.getSingleResult();
		transaction.commit();

		assertNotNull(product);
		assertEquals(1, product.getId());

		manager.close();
	}

	public void testCreateAndFind() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();

		transaction.begin();
		Product newProduct = new Product();
		newProduct.setId(10);
		newProduct.setName("product10");
		manager.persist(newProduct);
		transaction.commit();

		transaction.begin();
		Product product = manager.find(Product.class, Integer.valueOf(10));
		transaction.commit();
		assertNotNull(product);
		assertEquals(10, product.getId());

		transaction.begin();
		product = manager.find(Product.class, Integer.valueOf(10));
		manager.remove(product);
		transaction.commit();

		manager.close();

		assertFalse(manager.isOpen());

		try {
			transaction = manager.getTransaction();
			fail("Expected IllegalStateException due to invoking a method on an inactive EntityManager");
		} catch (IllegalStateException e) {
			// that's what we expected
		}
	}

	public void testCreateTwice() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();

		transaction.begin();
		Product newProduct = new Product();
		newProduct.setId(10);
		newProduct.setName("product10");
		manager.persist(newProduct);
		transaction.commit();

		transaction.begin();
		Product product = manager.find(Product.class, Integer.valueOf(10));
		transaction.commit();
		assertNotNull(product);
		assertEquals(10, product.getId());

		transaction.begin();
		newProduct = new Product();
		newProduct.setId(10);
		newProduct.setName("product10");
		try {
			manager.persist(newProduct);
			fail("EntityExistsException expected");
		} catch (EntityExistsException e) {
			// nothing to do, as this is expected
		}
		transaction.commit();

		transaction.begin();
		product = manager.find(Product.class, Integer.valueOf(10));
		manager.remove(product);
		transaction.commit();

		manager.close();

		assertFalse(manager.isOpen());

		try {
			transaction = manager.getTransaction();
			fail("Expected IllegalStateException due to invoking a method on an inactive EntityManager");
		} catch (IllegalStateException e) {
			// that's what we expected
		}
	}

	public void testCreateWithoutTransaction() throws Exception {

		EntityManager manager = factory.createEntityManager();

		Product newProduct = new Product();
		newProduct.setId(10);
		newProduct.setName("product10");

		try {
			manager.persist(newProduct);
			fail("Expected TransactionRequiredException");
		} catch (javax.persistence.TransactionRequiredException e) {
			// that's what we expected
		}
	}

	public void testContains() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		Product product = manager.find(Product.class, Integer.valueOf(1));
		assertTrue(manager.contains(product));
		transaction.commit();

		manager.close();
	}

	// public void testContainsWithoutActiveTransaction() throws Exception {
	//        
	// PersistenceProvider provider = new CastorPersistenceProvider();
	// EntityManagerFactory factory = provider.createEntityManagerFactory("jpa",
	// null);
	// EntityManager manager = factory.createEntityManager();
	// EntityTransaction transaction = manager.getTransaction();
	// transaction.begin();
	// Product product = manager.find(Product.class, Integer.valueOf(1));
	// transaction.commit();
	// manager.close();
	//
	// manager = factory.createEntityManager();
	// try {
	// manager.contains(product);
	// fail ("Expected TransactionRequiredException");
	// }
	// catch (javax.persistence.TransactionRequiredException e) {
	// // that's what we expected
	// }
	//        
	// manager.close();
	// }

	public void testContainsNot() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();

		transaction.begin();
		Product newProduct = new Product();
		newProduct.setId(10);
		newProduct.setName("product10");
		assertFalse(manager.contains(newProduct));
		transaction.commit();

		manager.close();
	}

	public void testNativeQueryWithResultClass() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		Query query = manager.createNativeQuery(
				"select id, name from product where id = 1", Product.class);
		Product product = (Product) query.getSingleResult();
		transaction.commit();
		assertNotNull(product);
		assertEquals(1, product.getId());

		manager.close();
	}

	public void testGetReferenceWithoutAccess() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();

		transaction.begin();
		Product product = manager.getReference(Product.class, Integer
				.valueOf(1));
		transaction.commit();
		assertNotNull(product);

		// assertTrue(product instanceof Factory);

		manager.close();
	}

	public void testGetReference() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();

		transaction.begin();
		Product product = manager.getReference(Product.class, Integer
				.valueOf(1));
		transaction.commit();
		assertNotNull(product);

		// accessing the content of the proxy for the first time
		assertEquals(1, product.getId());
		assertEquals("product1", product.getName());

		manager.close();
	}

	public void testGetReferenceForNotExistingProduct() throws Exception {

		EntityManager manager = factory.createEntityManager();
		EntityTransaction transaction = manager.getTransaction();

		transaction.begin();
		Product product = null;
		try {
			product = manager.getReference(Product.class, Integer.valueOf(100));
			fail("Expected EntityNotFoundException to be thrown.");
		} catch (EntityNotFoundException e) {
			transaction.rollback();
		}

		assertNull(product);

		manager.close();
	}

}
