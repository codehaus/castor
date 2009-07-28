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
package org.castor.jpa;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.castor.jpa.functional.model.Book;
import org.castor.jpa.functional.model.UnknownType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CastorEntityManagerTest {

    private EntityManagerFactory factory;

    /**
     * Invoked before every single test method.
     */
    @Before
    public void before() {
        factory = Persistence.createEntityManagerFactory("castor");
    }

    /**
     * Tries to execute a find without an active transaction. In a
     * transaction-scoped persistence context this must fail.
     */
    @Test(expected = TransactionRequiredException.class)
    public void findWithoutTransaction() {
        EntityManager em = factory.createEntityManager();
        em.find(Book.class, Long.valueOf(1));
        em.close();
    }

    /**
     * Tries to find an unknown entity type, which must fail.
     */
    @Test(expected = IllegalArgumentException.class)
    public void findUnknownEntityType() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.find(org.castor.jpa.functional.model.UnknownType.class, Long.valueOf(1));
        tx.commit();
        em.close();
    }

    /**
     * Tries to look up an invalid primary key type.
     */
    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void findInvalidPrimaryKeyType() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.find(Book.class, Float.valueOf(1 / 3f));
        tx.commit();
        em.close();
    }

    /**
     * Tries to execute a find on a closed {@link EntityManager}, which must
     * fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void findOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        em.close();
        tx.begin();
        em.find(Book.class, Long.valueOf(1));
        tx.commit();
    }

    /**
     * Tries to execute a merge without an active transaction. In a
     * transaction-scoped persistence context this must fail.
     */
    @Test(expected = TransactionRequiredException.class)
    @Ignore
    public void mergeWithoutTransaction() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.merge(entity);
        em.close();
    }

    /**
     * Tries to perform a merge on a closed {@link EntityManager}, which must
     * fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void mergeOnClosedEntityManager() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        em.close();
        tx.begin();
        em.merge(entity);
        tx.commit();
    }

    /**
     * Tries to execute a remove without an active transaction. In a
     * transaction-scoped persistence context this must fail.
     */
    @Test(expected = TransactionRequiredException.class)
    @Ignore
    public void removeWithoutTransaction() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.remove(entity);
        em.close();
    }

    /**
     * Tries to perform a remove on a closed {@link EntityManager}, which must
     * fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void removeOnClosedEntityManager() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        em.close();
        tx.begin();
        em.remove(entity);
        tx.commit();
    }

    /**
     * Tries to execute a refresh without an active transaction. In a
     * transaction-scoped persistence context this must fail.
     */
    @Test(expected = TransactionRequiredException.class)
    @Ignore
    public void refreshWithoutTransaction() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.refresh(entity);
        em.close();
    }

    /**
     * Tries to perform a refresh on a closed {@link EntityManager}, which must
     * fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void refreshOnClosedEntityManager() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.close();
        em.refresh(entity);
        tx.commit();
    }

    /**
     * Tries to perform a clear on a closed {@link EntityManager}, which must
     * fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void clearOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.clear();
    }

    /**
     * Tries to invoke the contains method on a closed {@link EntityManager},
     * which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void containsOnClosedEntityManager() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.close();
        em.contains(entity);
    }

    /**
     * Tries to invoke the contains method with an unknown entity type.
     */
    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void containsUnknownEntityType() {
        EntityManager em = factory.createEntityManager();
        em.contains(new UnknownType());
    }

    /**
     * Tries to obtain a named {@link Query} from a closed {@link EntityManager}
     * , which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void createNamedQueryOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.createNamedQuery("unit-test-named-select-query");
    }

    /**
     * Tries to obtain a native {@link Query} from a closed
     * {@link EntityManager}, which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void createNativeQueryOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.createNativeQuery("SELECT * FROM object");
    }

    /**
     * Tries to obtain a native {@link Query} from a closed
     * {@link EntityManager}, which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void createNativeQueryForClassOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.createNativeQuery("SELECT * FROM object", Object.class);
    }

    /**
     * Tries to obtain a native {@link Query} from a closed
     * {@link EntityManager}, which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void createNativeQueryWthMappingOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.createNativeQuery("SELECT * FROM object", "mapping");
    }

    /**
     * Tries to obtain a JPA {@link Query} from a closed {@link EntityManager},
     * which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void createQueryOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.createQuery("SELECT o from Object o");
    }

    /**
     * Tries to flush a closed {@link EntityManager}, which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void flushOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.flush();
    }

    /**
     * Tries to flush without an {@link EntityTransaction}.
     */
    @Test(expected = TransactionRequiredException.class)
    @Ignore
    public void flushWithoutTransaction() {
        EntityManager em = factory.createEntityManager();
        em.flush();
    }

    /**
     * Tries to obtain the delegate from a closed {@link EntityManager}, which
     * must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void getDelegateOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.getDelegate();
    }

    /**
     * Tries to obtain the {@link FlushModeType} from a closed
     * {@link EntityManager}, which must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void getFlushModeOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.getFlushMode();
    }

    /**
     * Tries to obtain a reference from a closed {@link EntityManager}, which
     * must fail.
     */
    @Test(expected = IllegalStateException.class)
    @Ignore
    public void getReferenceOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.getReference(Object.class, Long.valueOf(1));
    }

    /**
     * Tries to obtain a reference from an unknown entity type.
     */
    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void getReferenceOfUnknownEntityType() {
        EntityManager em = factory.createEntityManager();
        em.getReference(UnknownType.class, Long.valueOf(1));
    }

    /**
     * Tries to obtain a reference from a non existing entity.<br/>
     * TODO lukas.lang: Discuss, whether exception should be thrown immediately.
     */
    @Test(expected = EntityNotFoundException.class)
    @Ignore
    public void getReferenceOfNonExistingEntity() {
        EntityManager em = factory.createEntityManager();
        em.getReference(Book.class, Long.valueOf(1));
    }

    /**
     * Tries to obtain the transaction from a closed {@link EntityManager},
     * which must succeed.
     */
    @Test
    @Ignore
    public void getTransactionOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        EntityTransaction transaction = em.getTransaction();
        assertNotNull(transaction);
    }

    /**
     * Tries to obtain a transaction from an {@link EntityManager}.
     */
    @Test
    public void getTransaction() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        assertNotNull(transaction);
    }

    /**
     * Verifies that a {@link EntityManager#isOpen()} returns false after
     * calling {@link EntityManager#close()}.
     */
    @Test
    public void closeEntityManager() {
        EntityManager em = factory.createEntityManager();
        assertTrue(em.isOpen());
        em.close();
        assertFalse(em.isOpen());
    }

    /**
     * Verifies idempotence of calling {@link EntityManager#close()}.
     */
    @Test(expected = IllegalStateException.class)
    public void closeEntityManagerTwice() {
        EntityManager em = factory.createEntityManager();
        assertTrue(em.isOpen());
        em.close();
        assertFalse(em.isOpen());
        em.close();
    }

    /**
     * Verifies that a {@link EntityManager#isOpen()} returns true after
     * creation.
     */
    @Test
    public void isOpenEntityManager() {
        EntityManager em = factory.createEntityManager();
        assertTrue(em.isOpen());
    }

    /**
     * Verifies correct behavior of {@link EntityManager#isOpen()}.
     */
    @Test
    public void isOpenOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        assertTrue(em.isOpen());
        em.close();
        assertFalse(em.isOpen());
    }

    /**
     * Tries to join transactions on a closed {@link EntityManager}.
     */
    @Test(expected = IllegalStateException.class)
    public void joinTransactionOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.joinTransaction();
    }

    /**
     * Tries to join transactions without an active {@link EntityTransaction}.
     */
    @Test(expected = TransactionRequiredException.class)
    public void joinTransactionWithoutRunningTransaction() {
        EntityManager em = factory.createEntityManager();
        em.joinTransaction();
    }

    /**
     * Tries to get a {@link LockModeType#READ} lock on a closed
     * {@link EntityManager}.
     */
    @Test(expected = IllegalStateException.class)
    public void lockReadOnClosedEntityManager() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.close();
        em.lock(entity, LockModeType.READ);
    }

    /**
     * Tries to get a {@link LockModeType#READ} lock with an unknown entity
     * type.
     */
    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void lockReadWithUnknownEntityType() {
        UnknownType entity = new UnknownType();
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.lock(entity, LockModeType.READ);
    }

    /**
     * Tries to get a {@link LockModeType#READ} lock on a detached entity.
     */
    @Test
    @Ignore
    public void lockReadOnDetachedEntity() {
        Book book = new Book();
        book.setIsbn(1L);
        book.setTitle("unit-test-title");

        // Create a book entity.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();

        // Now get a lock on a detached entity.
        em.getTransaction().begin();
        try {
            em.lock(book, LockModeType.READ);
            fail("Lock should not be granted!");
        } catch (IllegalArgumentException iae) {
            // Should be here.
        } finally {
            // Clean up database.
            book = em.find(Book.class, 1L);
            em.remove(book);
            em.getTransaction().commit();
        }
    }

    /**
     * Tries to get a {@link LockModeType#WRITE} lock on a detached entity.
     */
    @Test
    @Ignore
    public void lockWriteOnDetachedEntity() {
        Book book = new Book();
        book.setIsbn(1L);
        book.setTitle("unit-test-title-2");

        // Create a book entity.
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();

        // Now get a lock on a detached entity.
        em.getTransaction().begin();
        try {
            em.lock(book, LockModeType.WRITE);
            fail("Lock should not be granted!");
        } catch (IllegalArgumentException iae) {
            // Should be here.
        } finally {
            // Clean up database.
            book = em.find(Book.class, 1L);
            em.remove(book);
            em.getTransaction().commit();
        }
    }

    /**
     * Tries to get a {@link LockModeType#WRITE} lock with an unknown entity
     * type.
     */
    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void lockWriteWithUnknownEntityType() {
        UnknownType entity = new UnknownType();
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.lock(entity, LockModeType.WRITE);
    }

    /**
     * Tries to get a {@link LockModeType#READ} lock on a closed
     * {@link EntityManager}.
     */
    @Test(expected = IllegalStateException.class)
    public void lockWriteOnClosedEntityManager() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.close();
        em.lock(entity, LockModeType.WRITE);
    }

    /**
     * Tries to get a {@link LockModeType#READ} lock on a {@link EntityManager}
     * without obtaining a transaction first.
     */
    @Test(expected = TransactionRequiredException.class)
    public void lockReadWithoutTransaction() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.lock(entity, LockModeType.READ);
    }

    /**
     * Tries to get a {@link LockModeType#WRITE} lock on a {@link EntityManager}
     * without obtaining a transaction first.
     */
    @Test(expected = TransactionRequiredException.class)
    public void lockWriteWithoutTransaction() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.lock(entity, LockModeType.WRITE);
    }

    /**
     * Tries to set the {@link FlushModeType#AUTO} a closed
     * {@link EntityManager}.
     */
    @Test(expected = IllegalStateException.class)
    public void setFlushModeAutoOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.setFlushMode(FlushModeType.AUTO);
    }

    /**
     * Tries to set the {@link FlushModeType#COMMIT} a closed
     * {@link EntityManager}.
     */
    @Test(expected = IllegalStateException.class)
    public void setFlushModeCommitOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        em.close();
        em.setFlushMode(FlushModeType.COMMIT);
    }

    /**
     * Tries to persist an {@link Object} within a closed {@link EntityManager}
     * which must fail.
     */
    @Test(expected = IllegalStateException.class)
    public void persistOnClosedEntityManager() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.close();
        em.persist(entity);
    }

    /**
     * Tries to persist an {@link Object} without starting an
     * {@link EntityTransaction}.
     */
    @Test(expected = TransactionRequiredException.class)
    public void persistWithoutTransaction() {
        Object entity = new Object();
        EntityManager em = factory.createEntityManager();
        em.persist(entity);
    }

    /**
     * Tries to persist an unknown entity type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void persistUnknownEntityType() {
        UnknownType entity = new UnknownType();
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    /**
     * Tries to persist an existing entity.
     */
    public void persistExistingEntity() {
        EntityManager em = factory.createEntityManager();

        // Ensure, there exists no book within this entity manager.
        em.getTransaction().begin();
        Book result = em.find(Book.class, 1L);
        em.getTransaction().commit();

        assertNull(result);

        Book book = new Book();
        book.setIsbn(1L);
        book.setTitle("unit-test-title");

        try {
            em.getTransaction().begin();
            em.persist(book);

            Book sameBook = new Book();
            sameBook.setIsbn(1L);
            sameBook.setTitle("unit-test-title");
            em.persist(sameBook);

            fail("Entity already exists!");
        } catch (EntityExistsException e) {
            // Should be here.
            em.getTransaction().rollback();
        }
    }
}