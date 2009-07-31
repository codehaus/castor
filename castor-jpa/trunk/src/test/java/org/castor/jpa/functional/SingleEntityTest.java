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

import java.sql.SQLException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.castor.jpa.functional.model.Book;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SingleEntityTest extends AbstractSpringBaseTest {

    private EntityManagerFactory factory;

    /**
     * Invoked before every single test method.
     * 
     */
    @Before
    public void before() throws SQLException {
        factory = Persistence.createEntityManagerFactory("castor");
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
        deleteFromTable("book");

        Long isbn = Long.valueOf(1);
        String title = "unit-test-book-title-1";

        // Insert test values.
        executeUpdate("INSERT INTO book (isbn, title, version) VALUES (?, ?, ?)", isbn, title,
                System.currentTimeMillis());

        // Look up book.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Book result = em.find(Book.class, Long.valueOf(isbn));

        em.getTransaction().commit();
        em.close();

        // Verify result.
        verifyBook(result, isbn, title);

        deleteFromTable("book");
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
        deleteFromTable("book");

        Long isbn = Long.valueOf(1);

        // Look up book.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Book result = em.find(Book.class, Long.valueOf(isbn));

        // Verify null result.
        assertNull(result);

        em.getTransaction().commit();
        em.close();

        deleteFromTable("book");
    }

    /**
     * Invokes the {@link EntityManager#persist(Object)} method and verifies
     * correct persistence of a {@link Book} object.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void persistBook() throws SQLException {
        deleteFromTable("book");

        Long isbn = Long.valueOf(2);
        String title = "unit-test-book-title-2";

        // Persist a new book.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Book book = new Book(isbn, title);
        em.persist(book);

        em.getTransaction().commit();

        // Verify result via JPA.
        em.getTransaction().begin();

        Book result = em.find(Book.class, Long.valueOf(isbn));

        em.getTransaction().commit();
        em.close();

        verifyBook(result, isbn, title);

        deleteFromTable("book");
    }

    /**
     * Invokes the {@link EntityManager#persist(Object)} method and verifies
     * correct behavior when persisting a {@link Book} object twice, which
     * should end up with an {@link EntityExistsException}.
     */
    @Test
    public void persistBookTwice() throws SQLException {
        deleteFromTable("book");

        Long isbn = Long.valueOf(2);
        String title = "unit-test-book-title-2";

        // Persist a new book.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Book book = new Book(isbn, title);
        em.persist(book);

        em.getTransaction().commit();

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
            em.getTransaction().rollback();
        } finally {
            // Release resources.
            em.close();
        }

        // Verify, old book is still in database.
        verifyPersistentBook(book);
        assertEquals(1, countRowsInTable("book"));

        deleteFromTable("book");
    }

    /**
     * Deletes a {@link Book} which was not obtained from an
     * {@link EntityManager} before.
     * 
     * @throws SQLException
     *             in case insertion of test values fails.
     */
    @Test
    public void removeUnloadedBook() throws SQLException {
        deleteFromTable("book");

        Long isbn = Long.valueOf(1);
        String title = "unit-test-book-title-4";

        // Insert test values.
        executeUpdate("INSERT INTO book (isbn, title, version) VALUES (?, ?, ?)", isbn, title,
                System.currentTimeMillis());

        // Look up book.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Book book = new Book(isbn, title);
        try {
            em.remove(book);
            fail("Entity must not be removed when not loaded before!");
        } catch (IllegalArgumentException iae) {
            // Should be here.
        }

        em.getTransaction().commit();
        em.close();

        // Verify result.
        assertEquals(1, countRowsInTable("book"));

        deleteFromTable("book");
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
        deleteFromTable("book");

        Long isbn = Long.valueOf(1);
        String title = "unit-test-book-title-5";

        // Insert test values.
        executeUpdate("INSERT INTO book (isbn, title, version) VALUES (?, ?, ?)", isbn, title,
                System.currentTimeMillis());

        // Look up book and delete it.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Book book = em.find(Book.class, isbn);
        em.remove(book);

        em.getTransaction().commit();
        em.close();

        // Verify result.
        assertEquals(0, countRowsInTable("book"));

        deleteFromTable("book");
    }

    /**
     * Tries to delete a non existing {@link Book}. Should result in an
     * {@link IllegalArgumentException}.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void removeNonExistingBook() throws SQLException {
        deleteFromTable("book");

        Long isbn = Long.valueOf(1);
        String title = "unit-test-book-title-6";

        // Look up book and delete it.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        // Try to delete a non existing book.
        Book book = new Book(isbn, title);

        // Should fail.
        em.remove(book);
    }

    /**
     * Invokes the {@link EntityManager#find(Class, Object)} method and merges
     * the detached instance.
     * 
     * @throws SQLException
     *             in case clean up fails.
     * 
     */
    @Test
    public void merge() throws SQLException {
        deleteFromTable("book");

        Book book = new Book(1L, "unit-test-title-7");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();

        // Apply some changes to the detached entity.
        book.setTitle("new-unit-test-title-7");

        // Merge the entity.
        em.getTransaction().begin();
        Book merged = em.merge(book);
        em.getTransaction().commit();

        assertNotNull(merged);
        verifyBook(merged, 1L, "new-unit-test-title-7");

        assertSame(merged, book);

        // Verify changes.
        em.getTransaction().begin();
        Book result = em.find(Book.class, 1L);
        em.getTransaction().commit();

        verifyBook(result, 1L, "new-unit-test-title-7");

        deleteFromTable("book");
    }

    /**
     * Invokes the {@link EntityManager#find(Class, Object)} method and merges
     * the detached instance within another {@link EntityManager}.
     * 
     * @throws SQLException
     *             in case clean up fails.
     * 
     */
    @Test
    public void mergeWithinAnotherEntityManager() throws SQLException {
        deleteFromTable("book");

        Book book = new Book(1L, "unit-test-title-8");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();
        em.close();

        // Apply some changes to the detached entity.
        book.setTitle("new-unit-test-title-8");

        // Merge the entity.
        em = factory.createEntityManager();
        em.getTransaction().begin();
        Book merged = em.merge(book);
        em.getTransaction().commit();

        assertNotNull(merged);
        verifyBook(merged, 1L, "new-unit-test-title-8");

        // Verify changes.
        em.getTransaction().begin();
        Book result = em.find(Book.class, 1L);
        em.getTransaction().commit();

        verifyBook(result, 1L, "new-unit-test-title-8");

        deleteFromTable("book");
    }

    /**
     * Updates a loaded entity within a transaction.
     * 
     * @throws SQLException
     *             in case clean up fails.
     * 
     */
    @Test
    public void updateEntity() throws SQLException {
        deleteFromTable("book");

        Long isbn = Long.valueOf(1);
        String title = "unit-test-book-title-9";

        // Insert test values.
        executeUpdate("INSERT INTO book (isbn, title, version) VALUES (?, ?, ?)", isbn, title,
                System.currentTimeMillis());

        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        Book book = em.find(Book.class, 1L);
        book.setTitle("new-unit-test-title-9");
        em.getTransaction().commit();

        // Verify changes.
        em.getTransaction().begin();
        Book result = em.find(Book.class, 1L);
        verifyBook(result, 1L, "new-unit-test-title-9");

        deleteFromTable("book");
    }

    /**
     * Verifies that object modifications within multiple transactions are
     * detected when merging detached entities. This is based on (implicit)
     * optimistic locking.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void mergeModifiedObject() throws SQLException {
        deleteFromTable("book");

        // Load book.
        Book book = new Book(1L, "unit-test-title-10");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();

        // Wait to achieve different time stamps between merge calls.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Don't care.
            e.printStackTrace();
        }

        // Load same book.
        EntityManager em2 = factory.createEntityManager();
        em2.getTransaction().begin();
        Book book2 = em2.find(Book.class, 1L);
        em2.getTransaction().commit();

        // Apply some changes to the detached entity.
        book.setTitle("new-unit-test-title-10");

        // Merge the entity back.
        em.getTransaction().begin();
        Book merged = em.merge(book);
        em.getTransaction().commit();

        assertNotNull(merged);
        verifyBook(merged, 1L, "new-unit-test-title-10");

        // Now try to merge the second book.
        book2.setTitle("fail-unit-test-title-10");
        try {
            em2.getTransaction().begin();
            Book merged2 = em2.merge(book2);
            verifyBook(merged2, 1L, "fail-unit-test-title-10");
            em2.getTransaction().commit();
            fail("Object was modified. Merge operation must not succeed!");
        } catch (Exception iae) {
            // Normal behavior.
        }

        // Verify changes on second entity manager.
        em.getTransaction().begin();
        Book result = em.find(Book.class, 1L);
        em.getTransaction().commit();

        verifyBook(result, 1L, "new-unit-test-title-10");

        deleteFromTable("book");
    }

    /**
     * Verifies that calling {@link EntityManager#merge(Object)} with a new
     * object results in a new managed object.
     * 
     * @throws SQLException
     */
    @Test
    public void mergeNewEntity() throws SQLException {
        deleteFromTable("book");

        // Load book.
        Book book = new Book(1L, "unit-test-title-10");
        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        em.getTransaction().begin();
        Book merged = em.merge(book);

        // Verify that a new object was returned.
        assertNotNull(merged);

        // Verify that the new object is managed.
        assertTrue(em.contains(book));
        assertTrue(em.contains(merged));

        em.getTransaction().commit();

        deleteFromTable("book");
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
}