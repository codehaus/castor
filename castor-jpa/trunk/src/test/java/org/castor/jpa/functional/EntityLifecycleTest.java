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
import javax.persistence.PersistenceContextType;

import org.castor.jpa.functional.model.Book;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Runs various tests on entity life cycle in a transaction-scoped persistence
 * context (i.e. {@link PersistenceContextType#TRANSACTION}). See section
 * "3.2 Entity Instance’s Life Cycle" from the EJB3 JPA specification for more
 * information.<br/>
 * 
 * <b>NOTE: Tests do not take care of cascading!</b>
 * 
 * TODO lukas.lang: Add life cycle tests for cascading operations and entities.
 * 
 * @author lukas.lang
 * 
 */
public class EntityLifecycleTest extends AbstractSpringBaseTest {

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
     * Models the following transitions: <b>New to Managed</b>.
     * 
     * @throws SQLException
     *             thrown in case insert of test data fails.
     */
    @Test
    public void newToManaged() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Managed.
        em.getTransaction().begin();
        em.persist(book);
        assertTrue(em.contains(book));
        em.getTransaction().commit();

        assertFalse(em.contains(book));

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Removed to Managed</b>.
     * 
     * @throws SQLException
     *             thrown in case insert of test data fails.
     */
    @Test
    @Ignore
    public void removedToManaged() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Managed.
        em.getTransaction().begin();
        em.persist(book);
        assertTrue(em.contains(book));

        // Remove.
        em.remove(book);
        assertFalse(em.contains(book));

        // Managed.
        em.persist(book);
        assertTrue(em.contains(book));

        em.getTransaction().commit();

        assertFalse(em.contains(book));

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Detached to Managed</b> with a
     * detached entity by calling {@link EntityManager#persist()}. This
     * transition is not part of JPA specification, hence must fail.
     * 
     * @throws SQLException
     *             thrown in case insert of test data fails.
     */
    @Test
    public void detachedToManagedByPersist() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Managed.
        em.getTransaction().begin();
        em.persist(book);
        assertTrue(em.contains(book));
        em.getTransaction().commit();

        // Detached.
        em.getTransaction().begin();

        // Managed.
        try {
            em.persist(book);
            fail("Persist operation on a detached entity must not succeed!");
        } catch (EntityExistsException e) {
            // Should be here.
            em.getTransaction().rollback();
        }

        assertFalse(em.contains(book));

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>New to Removed</b> which must fail.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newToRemoved() {
        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Remove.
        em.getTransaction().begin();
        assertFalse(em.contains(book));
        em.remove(book);
    }

    /**
     * Models the following transitions: <b>Managed to Removed</b>.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void managedToRemoved() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Managed.
        em.getTransaction().begin();
        em.persist(book);
        assertTrue(em.contains(book));
        em.remove(book);
        assertFalse(em.contains(book));
        em.getTransaction().commit();

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Managed to Removed</b>.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void managedToRemovedByFind() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();

        // Managed.
        em.getTransaction().begin();
        book = em.find(Book.class, 1L);
        assertNotNull(book);
        assertTrue(em.contains(book));
        em.remove(book);
        assertFalse(em.contains(book));
        em.getTransaction().commit();

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Detached to Removed</b>, which must
     * fail with an {@link IllegalArgumentException}.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void detachedToRemoved() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Detached.
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();

        em.getTransaction().begin();
        try {
            em.remove(book);
            fail("Remove of a detached entity must not succeed!");
        } catch (IllegalArgumentException iae) {
            // Should be here.
            em.getTransaction().rollback();
        }

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Removed to Removed</b>, which is
     * ignored.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void removedToRemoved() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Managed.
        em.getTransaction().begin();
        em.persist(book);
        assertTrue(em.contains(book));

        // Removed.
        em.remove(book);
        assertFalse(em.contains(book));

        // Remove again.
        em.remove(book);
        assertFalse(em.contains(book));

        em.getTransaction().commit();

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Detached to Managed</b>. When
     * merging, no entity with the same identity is managed by the persistence
     * context, hence a new instance must be returned.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    @Ignore
    public void detachedToManaged() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Managed.
        em.getTransaction().begin();
        em.persist(book);
        assertTrue(em.contains(book));

        // Commit. Book gets detached.
        em.getTransaction().commit();

        em.getTransaction().begin();
        Book merged = em.merge(book);

        // Merged entity is now managed by the persistence context.
        assertNotNull(merged);
        assertTrue(em.contains(merged));

        // Merged entity must not be same instance as detached one.
        assertNotSame(merged, book);

        // Detached entity must not be managed.
        assertFalse(em.contains(book));
        em.getTransaction().commit();

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Detached to Managed</b>. When
     * merging, an entity with the same identity is managed by the persistence
     * context, hence the managed entity must be updated and returned.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    @Ignore
    public void detachedToManagedWithManagedEntity() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();
        assertFalse(em.contains(book));

        // Managed.
        em.getTransaction().begin();
        em.persist(book);
        assertTrue(em.contains(book));
        // Commit. Book gets detached.
        em.getTransaction().commit();

        // Apply some changes to the detached entity.
        book.setTitle("new-unit-test-title-1");

        em.getTransaction().begin();
        // An entity with same identity gets managed.
        Book managed = em.find(Book.class, 1L);
        assertTrue(em.contains(managed));

        // Merge detached entity.
        Book merged = em.merge(book);
        assertNotNull(merged);
        assertTrue(em.contains(merged));
        assertTrue(em.contains(managed));

        // Verify changes.
        assertEquals("new-unit-test-title-1", merged.getTitle());

        // Previously managed shouldn't be updated unless refresh() is called.
        assertEquals("unit-test-title-1", managed.getTitle());

        // Merged entity must not be same instance as detached.
        assertNotSame(merged, book);

        // Merged entity must be same instance as previously managed entity.
        assertSame(managed, merged);

        // Detached entity must not be managed.
        assertFalse(em.contains(book));
        em.getTransaction().commit();

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>New to Managed</b>. A new entity is
     * passed to the {@link EntityManager#merge(Object)} method, which must
     * result in a managed entity with and a new object.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    @Ignore
    public void newToManagedByMerge() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();

        em.getTransaction().begin();
        assertFalse(em.contains(book));

        // Merge detached entity.
        Book merged = em.merge(book);
        assertNotNull(merged);
        assertTrue(em.contains(merged));

        // Merged entity must not be same instance as detached.
        assertNotSame(merged, book);

        // Detached entity must not be managed.
        assertFalse(em.contains(book));

        em.getTransaction().commit();

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Removed to Managed</b>. Via the
     * merge operation, which must fail.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void removedToManagedByMerge() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();

        em.getTransaction().begin();
        assertFalse(em.contains(book));

        // Managed.
        em.persist(book);
        assertTrue(em.contains(book));

        // Removed.
        em.remove(book);
        assertFalse(em.contains(book));

        try {
            em.merge(book);
            fail("Merge operation must not succeed with a removed entity!");
        } catch (IllegalArgumentException iae) {
            // Removed entity must not be managed.
            assertFalse(em.contains(book));
            em.getTransaction().rollback();
        }

        em.close();
        deleteFromTable("book");
    }

    /**
     * Models the following transitions: <b>Managed to Managed</b>. Merge
     * operation should be ignored when passing a managed entity.
     * 
     * @throws SQLException
     *             in case clean up fails.
     */
    @Test
    public void managedToManagedByMerge() throws SQLException {
        deleteFromTable("book");

        // New.
        Book book = new Book();
        book.setIsbn(1);
        book.setTitle("unit-test-title-1");

        EntityManager em = factory.createEntityManager();

        em.getTransaction().begin();
        assertFalse(em.contains(book));

        // Managed.
        em.persist(book);
        assertTrue(em.contains(book));

        // Verify, managed instance is returned to prevent from changes being
        // merged. No changes can be made due to same reference.
        Book find = em.find(Book.class, 1L);
        assertSame(book, find);

        // Merge.
        Book merged = em.merge(find);
        assertTrue(em.contains(book));

        // Verify same and equal.
        assertSame(book, merged);
        assertEquals(book, merged);

        em.getTransaction().commit();

        em.close();
        deleteFromTable("book");
    }
}