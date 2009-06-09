/*
 * Copyright 2009 Lukas Lang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.castor.jpa.functional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

import org.castor.jpa.functional.model.Book;
import org.castor.jpa.spi.CastorPersistenceProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ContextConfiguration(locations = { "/spring-test-applicationContext.xml" })
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class SingleEntityTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	/**
	 * The {@link DataSource} to use. Injected by Spring.
	 */
	@Autowired
	private DataSource dataSource;

	private PersistenceProvider provider;
	private EntityManagerFactory factory;

	/**
	 * Invoked before every single test method.
	 */
	@Before
	public void before() {
		provider = new CastorPersistenceProvider();
		factory = provider.createEntityManagerFactory("jpa", null);
	}

	/**
	 * Method is being invoked after every single test for cleanup purpose.
	 */
	@After
	public void after() {
		// Clean up table.
		this.deleteFromTables("book");
	}

	/**
	 * Invokes the {@link EntityManager#find(Class, Object)} method and verifies
	 * correct loading of a {@link Book} entity.
	 * 
	 * @throws SQLException
	 *             thrown in case insert of test data fails.
	 */
	@Test
	public void findBook() throws SQLException {
		Long isbn = Long.valueOf(1);
		String title = "unit-test-book-title-1";

		// Insert test values.
		executeUpdate("INSERT INTO book (isbn, title) VALUES (?, ?)", isbn,
				title);

		// Look up book.
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		Book result = em.find(Book.class, Long.valueOf(isbn));

		em.getTransaction().commit();
		em.close();

		// Verify result.
		verifyBook(result, isbn, title);
	}

	/**
	 * Invokes the {@link EntityManager#find(Class, Object)} method and tries to
	 * load a {@link Book} which was not persisted before.
	 * 
	 * @throws SQLException
	 *             thrown in case insertion of test data fails.
	 */
	@Test
	public void findNonExistingBook() throws SQLException {
		Long isbn = Long.valueOf(1);

		// Look up book.
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		Book result = em.find(Book.class, Long.valueOf(isbn));

		// Verify null result.
		assertNull(result);

		em.getTransaction().commit();
		em.close();
	}

	/**
	 * Invokes the {@link EntityManager#persist(Object)} method and verifies
	 * correct persistence of a {@link Book} object.
	 */
	@Test
	public void persistBook() {
		Long isbn = Long.valueOf(2);
		String title = "unit-test-book-title-2";

		// Persist a new book.
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		Book book = new Book(isbn, title);
		em.persist(book);

		em.getTransaction().commit();

		// Verify result natively.
		verifyPersistentBook(book);

		// Verify result via JPA.
		em.getTransaction().begin();

		Book result = em.find(Book.class, Long.valueOf(isbn));

		em.getTransaction().commit();
		em.close();

		verifyBook(result, isbn, title);
	}

	/**
	 * Invokes the {@link EntityManager#persist(Object)} method and verifies
	 * correct behavior when persisting a {@link Book} object twice, which
	 * should end up with an {@link EntityExistsException}.
	 */
	@Test
	public void persistBookTwice() {
		Long isbn = Long.valueOf(2);
		String title = "unit-test-book-title-2";

		// Persist a new book.
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		Book book = new Book(isbn, title);
		em.persist(book);

		em.getTransaction().commit();

		// Verify result natively.
		verifyPersistentBook(book);

		// Persist again.
		em.getTransaction().begin();

		String otherTitle = "unit-test-book-title-3";
		Book newBook = new Book(isbn, otherTitle);
		try {
			// Should fail.
			em.persist(newBook);
			fail("Book already exists. Should end up with an EntityExistsException!");
		} catch (EntityExistsException e) {
			// Correct behavior.
		} finally {
			// Release resources.
			em.close();
		}

		// Verify, old book is still in database.
		verifyPersistentBook(book);
		assertEquals(1, countRowsInTable("book"));
	}

	/**
	 * Deletes a {@link Book} which was not obtained from an
	 * {@link EntityManager} before.
	 * 
	 * @throws SQLException
	 *             in case insertion of test values fails.
	 */
	@Test(expected = EntityNotFoundException.class)
	public void removeBookNotLoadedBefore() throws SQLException {
		Long isbn = Long.valueOf(1);
		String title = "unit-test-book-title-4";

		// Insert test values.
		executeUpdate("INSERT INTO book (isbn, title) VALUES (?, ?)", isbn,
				title);

		// Look up book.
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		Book book = new Book(isbn, title);
		em.remove(book);

		em.getTransaction().commit();
		em.close();

		// Verify result.
		assertEquals(0, countRowsInTable("book"));
	}

	/**
	 * Deletes a {@link Book} which was obtained from an {@link EntityManager}
	 * before.
	 * 
	 * @throws SQLException
	 *             in case insertion of test values fails.
	 */
	@Test
	public void removeLoadedBook() throws SQLException {
		Long isbn = Long.valueOf(1);
		String title = "unit-test-book-title-5";

		// Insert test values.
		executeUpdate("INSERT INTO book (isbn, title) VALUES (?, ?)", isbn,
				title);

		// Look up book and delete it.
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		Book book = em.find(Book.class, isbn);
		em.remove(book);

		em.getTransaction().commit();
		em.close();

		// Verify result.
		assertEquals(0, countRowsInTable("book"));
	}

	/**
	 * Tries to delete a non existing {@link Book}. Should result in an
	 * {@link EntityNotFoundException}.
	 * 
	 */
	@Test(expected = EntityNotFoundException.class)
	public void removeNonExistingBook() {
		Long isbn = Long.valueOf(1);
		String title = "unit-test-book-title-6";

		// Verify whether table is empty.
		assertEquals(0, countRowsInTable("book"));

		// Look up book and delete it.
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		// Try to delete a non existing book.
		Book book = new Book(isbn, title);

		// Should fail.
		em.remove(book);
	}

	/**
	 * This helper method verifies a given {@link Book} using JUnit asserts.
	 * 
	 * @param result
	 *            the {@link Book} to verify.
	 * @param isbn
	 *            the expected isbn.
	 * @param title
	 *            the expected title.
	 */
	private static void verifyBook(Book result, long isbn, String title) {
		assertNotNull(result);
		assertEquals(isbn, result.getIsbn());
		assertEquals(title, result.getTitle());
	}

	private void verifyPersistentBook(Book book) {
		PreparedStatement preparedStatement;
		try {

			// Load the book from the database.
			preparedStatement = this.dataSource.getConnection()
					.prepareStatement(
							"SELECT isbn, title FROM book WHERE isbn = ?");
			preparedStatement.setObject(1, Long.valueOf(book.getIsbn()));
			ResultSet resultSet = preparedStatement.executeQuery();

			// Verify result.
			assertEquals(1, resultSet.getFetchSize());
			assertTrue(resultSet.next());
			assertEquals(book.getIsbn(), resultSet.getLong(1));
			assertEquals(book.getTitle(), resultSet.getString(2));

			// Release resources.
			resultSet.close();
			preparedStatement.close();

		} catch (SQLException e) {
			fail("Could not verify book instance: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This helper method executes a prepared statement including the given
	 * parameters.
	 * 
	 * @param query
	 *            is a native SQL query.
	 * @param parameters
	 *            is an array of {@link Object}s used as parameters.
	 * @return the same as {@link PreparedStatement#executeUpdate()}.
	 * @throws SQLException
	 *             in case execution fails.
	 */
	private int executeUpdate(String query, Object... parameters)
			throws SQLException {
		PreparedStatement preparedStatement = this.dataSource.getConnection()
				.prepareStatement(query);
		for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
			Object parameter = parameters[parameterIndex];
			preparedStatement.setObject(parameterIndex + 1, parameter);
		}

		// Execute query.
		int numberAffected = preparedStatement.executeUpdate();

		// Release resources.
		preparedStatement.close();

		return numberAffected;
	}

}