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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

public class JDOEntityTransactionTest {

	private EntityManagerFactory factory;

	/**
	 * Invoked before every single test method.
	 */
	@Before
	public void before() {
		factory = Persistence.createEntityManagerFactory("castor");
	}

	/**
	 * Obtains an {@link EntityTransaction} and then closes the
	 * {@link EntityManager}. The transaction must be invalid then.
	 */
	@Test(expected = IllegalStateException.class)
	public void beginOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		// Now close the EntityManager.
		em.close();

		transaction.begin();
	}

	/**
	 * Obtains an {@link EntityTransaction} and then closes the
	 * {@link EntityManager}. The transaction must be invalid then.
	 */
	@Test(expected = IllegalStateException.class)
	public void commitOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		// Now close the EntityManager.
		em.close();

		transaction.commit();
	}

	/**
	 * Obtains an {@link EntityTransaction} and then closes the
	 * {@link EntityManager}. The transaction must be invalid then.
	 */
	@Test(expected = IllegalStateException.class)
	public void rollbackOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		// Now close the EntityManager.
		em.close();

		transaction.rollback();
	}

	/**
	 * Obtains an {@link EntityTransaction} and then closes the
	 * {@link EntityManager}. The transaction must be invalid then.
	 */
	@Test(expected = IllegalStateException.class)
	public void setRollbackOnlyOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		// Now close the EntityManager.
		em.close();

		transaction.setRollbackOnly();
	}

	/**
	 * Obtains an {@link EntityTransaction} and then closes the
	 * {@link EntityManager}. The transaction must be invalid then.
	 */
	@Test(expected = IllegalStateException.class)
	public void getRollbackOnlyOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		// Now close the EntityManager.
		em.close();

		transaction.getRollbackOnly();
	}

	/**
	 * Obtains an {@link EntityTransaction} and then closes the
	 * {@link EntityManager}. The transaction must be invalid then.
	 */
	@Test(expected = IllegalStateException.class)
	public void isActiveOnClosedEntityManager() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		// Now close the EntityManager.
		em.close();

		transaction.isActive();
	}

}