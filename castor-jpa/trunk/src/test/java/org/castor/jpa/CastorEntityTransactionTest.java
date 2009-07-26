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
import javax.persistence.RollbackException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Runs various tests on resource-level {@link EntityTransaction}.
 * 
 * @author lukas.lang
 * 
 */
public class CastorEntityTransactionTest {

    /**
     * The {@link EntityManagerFactory} to use.
     */
    private EntityManagerFactory factory;

    /**
     * Invoked before every single test method.
     */
    @Before
    public void before() {
        factory = Persistence.createEntityManagerFactory("castor");
    }

    /**
     * Obtains an {@link EntityTransaction} and starts the transaction twice,
     * which must fail.
     */
    @Test(expected = IllegalStateException.class)
    public void beginOnActiveTransaction() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        // Start the transaction again.
        transaction.begin();
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
     * Obtains an {@link EntityTransaction} and starts a transaction which must
     * be active then.
     */
    @Test
    public void beginEntityTransaction() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        assertTrue(transaction.isActive());
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
     * Obtains an {@link EntityTransaction} and commits it.
     * {@link EntityTransaction#isActive()} must then return <code>false</code>.
     */
    @Test
    public void commit() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        transaction.commit();
        assertFalse(transaction.isActive());
    }

    /**
     * Obtains an {@link EntityTransaction}, calls
     * {@link EntityTransaction#setRollbackOnly()} and invokes a commit, which
     * must fail.
     */
    @Test(expected = RollbackException.class)
    public void commitOnRollbackOnly() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        transaction.setRollbackOnly();
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
     * Obtains an {@link EntityTransaction}, starts the transaction and performs
     * a roll back. {@link EntityTransaction#isActive()} must then return
     * <code>false</code>.
     */
    @Test
    public void rollback() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        transaction.rollback();
        assertFalse(transaction.isActive());
    }

    /**
     * Obtains an {@link EntityTransaction} and invokes
     * {@link EntityTransaction#rollback()} which must fail due to inactivity.
     */
    @Test(expected = IllegalStateException.class)
    public void rollbackOnInActiveTransaction() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.rollback();
    }

    /**
     * Obtains an {@link EntityTransaction} and sets it roll back only.
     */
    @Test(expected = IllegalStateException.class)
    public void setRollbackOnInActiveTransaction() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.setRollbackOnly();
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
     * Obtains an {@link EntityTransaction}, starts the transaction and then
     * sets it to roll back only. {@link EntityTransaction#getRollbackOnly()}
     * must then return <code>true</code>.
     */
    @Test
    public void getRollbackOnly() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        assertFalse(transaction.getRollbackOnly());
        transaction.setRollbackOnly();
        assertTrue(transaction.getRollbackOnly());
    }

    /**
     * Obtains an {@link EntityTransaction}, invokes
     * {@link EntityTransaction#getRollbackOnly()} which must fail due to
     * inactivity.
     */
    @Test(expected = IllegalStateException.class)
    public void getRollbackOnlyOnInActiveTransaction() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.getRollbackOnly();
    }

    /**
     * Obtains an {@link EntityTransaction} which must not be active.
     */
    @Test
    public void isActiveOnRetrievedTransaction() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        assertNotNull(transaction);
        assertFalse(transaction.isActive());
    }

    /**
     * Obtains an {@link EntityTransaction} and then closes the
     * {@link EntityManager}. The transaction still must active.
     */
    public void isActiveOnClosedEntityManager() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        // Now close the EntityManager.
        em.close();

        assertTrue(transaction.isActive());
    }
}