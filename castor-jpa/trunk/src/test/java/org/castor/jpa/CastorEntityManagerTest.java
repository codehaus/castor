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

import java.awt.print.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	 * Tries to execute a find on a closed {@link EntityManager}, which must
	 * fail.
	 */
	@Test(expected = IllegalStateException.class)
	public void findOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.close();
		em.find(Book.class, Long.valueOf(1));
		tx.commit();
	}

	/**
	 * Tries to execute a merge without an active transaction. In a
	 * transaction-scoped persistence context this must fail.
	 */
	@Test(expected = TransactionRequiredException.class)
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
	public void mergeOnClosedEntityManager() {
		Object entity = new Object();
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.close();
		em.merge(entity);
		tx.commit();
	}

	/**
	 * Tries to execute a remove without an active transaction. In a
	 * transaction-scoped persistence context this must fail.
	 */
	@Test(expected = TransactionRequiredException.class)
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
	public void removeOnClosedEntityManager() {
		Object entity = new Object();
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.close();
		em.remove(entity);
		tx.commit();
	}

	/**
	 * Tries to execute a refresh without an active transaction. In a
	 * transaction-scoped persistence context this must fail.
	 */
	@Test(expected = TransactionRequiredException.class)
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
	public void containsOnClosedEntityManager() {
		Object entity = new Object();
		EntityManager em = factory.createEntityManager();
		em.close();
		em.contains(entity);
	}

	/**
	 * Tries to obtain a named {@link Query} from a closed {@link EntityManager}
	 * , which must fail.
	 */
	@Test(expected = IllegalStateException.class)
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
	public void createQueryOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		em.close();
		em.createQuery("SELECT o from Object o");
	}

	/**
	 * Tries to flush a closed {@link EntityManager}, which must fail.
	 */
	@Test(expected = IllegalStateException.class)
	public void flushOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		em.close();
		em.flush();
	}

	/**
	 * Tries to obtain the delegate from a closed {@link EntityManager}, which
	 * must fail.
	 */
	@Test(expected = IllegalStateException.class)
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
	public void getReferenceOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		em.close();
		em.getReference(Object.class, Long.valueOf(1));
	}

	/**
	 * Tries to obtain the transaction from a closed {@link EntityManager},
	 * which must succeed.
	 */
	@Test
	public void getTransactionOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		em.close();
		EntityTransaction transaction = em.getTransaction();
		assertNotNull(transaction);
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
	 * Tries to invoke a {@link LockModeType#READ} lock on a closed
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
	 * Tries to invoke a {@link LockModeType#READ} lock on a closed
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
	 * Tries to invoke a {@link LockModeType#READ} lock on a
	 * {@link EntityManager} without obtaining a transaction first.
	 */
	@Test(expected = TransactionRequiredException.class)
	public void lockReadWithoutTransaction() {
		Object entity = new Object();
		EntityManager em = factory.createEntityManager();
		em.lock(entity, LockModeType.READ);
		em.close();
	}

	/**
	 * Tries to invoke a {@link LockModeType#WRITE} lock on a
	 * {@link EntityManager} without obtaining a transaction first.
	 */
	@Test(expected = TransactionRequiredException.class)
	public void lockWriteWithoutTransaction() {
		Object entity = new Object();
		EntityManager em = factory.createEntityManager();
		em.lock(entity, LockModeType.WRITE);
		em.close();
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

}