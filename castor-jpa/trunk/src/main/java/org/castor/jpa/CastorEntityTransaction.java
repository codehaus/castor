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
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.TransactionNotInProgressException;

/**
 * {@link CastorEntityTransaction} implements and projects
 * {@link EntityTransaction} to the Castor equivalent, the {@link Database}.
 * Most of the operations are delegated directly to the local {@link Database}
 * instance.
 * 
 * @author lukas.lang
 * 
 */
public class CastorEntityTransaction implements EntityTransaction {

    /**
     * The corresponding {@link EntityManager}.
     */
    private EntityManager entityManager;

    /**
     * The Castor {@link Database}.
     */
    private Database database;

    /**
     * Indicates whether the current {@link EntityTransaction} is active.
     */
    private boolean active = false;

    /**
     * Indicates whether the current {@link EntityTransaction} is marked as roll
     * back only.
     */
    private boolean rollbackOnly = false;

    /**
     * The {@link Log} to use.
     */
    private final Log log = LogFactory.getLog(CastorEntityTransaction.class);

    public CastorEntityTransaction(EntityManager entityManager, Database database) {
        this.entityManager = entityManager;
        this.database = database;
    }

    /**
     * Start the resource transaction.
     * 
     * @throws IllegalStateException
     *             if {@link #isActive()} is true.
     */
    public void begin() {
        // Verify transaction is not running.
        verifyNotActive();
        // Verify entity manager is open.
        verifyEntityManagerIsOpen();
        // Set transaction active.
        this.active = true;

        // Start database transaction.
        try {
            this.database.begin();
        } catch (org.exolab.castor.jdo.PersistenceException e) {
            log.error("An error occured while starting a new EntityTransaction.");
            throw new javax.persistence.PersistenceException(
                    "An error occured while starting a new EntityTransaction.", e);
        }
    }

    /**
     * Commit the current transaction, writing any unflushed changes to the
     * database.
     * 
     * @throws IllegalStateException
     *             if {@link #isActive()} is false.
     * @throws RollbackException
     *             if the commit fails.
     */
    public void commit() {
        verifyActive();
        verifyEntityManagerIsOpen();

        // Verify whether transaction was marked as roll back only.
        if (getRollbackOnly()) {
            log.error("Could not commit changes. Entity transaction was marked for rollback only.");
            throw new RollbackException(
                    "Could not commit changes. Entity transaction was marked for rollback only.");
        }

        try {

            // Commit changes.
            this.database.commit();
            // Set transaction inactive.
            this.active = false;

        } catch (TransactionNotInProgressException e) {

            // Set transaction to roll back only.
            this.rollbackOnly = true;

            log.error("Could not commit changes. Castor transaction is inactive.", e);
            throw new RollbackException(
                    "Could not commit changes. Castor transaction is inactive.", e);
        } catch (TransactionAbortedException e) {

            // Set transaction to roll back only.
            this.rollbackOnly = true;

            log.error("Could not commit changes. Transaction was aborted.", e);
            throw new RollbackException("Could not commit changes. Transaction was aborted.", e);
        }
    }

    /**
     * Determine whether the current transaction has been marked for roll back.
     * 
     * @throws IllegalStateException
     *             if {@link #isActive()} is false.
     */
    public boolean getRollbackOnly() {
        // Verify if transaction is active.
        verifyActive();
        // Return roll back only.
        return this.rollbackOnly;
    }

    /**
     * Indicate whether a transaction is in progress.
     * 
     * @throws PersistenceException
     *             if an unexpected error condition is encountered.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Roll back the current transaction
     * 
     * @throws IllegalStateException
     *             if {@link #isActive()} is false.
     * @throws PersistenceException
     *             if an unexpected error condition is encountered.
     */
    public void rollback() {
        // Verify transaction is active.
        verifyActive();

        try {
            // Roll back Castor transaction.
            this.database.rollback();
            // Set transaction inactive.
            this.active = false;
            // Set roll back only false.
            this.rollbackOnly = false;
        } catch (TransactionNotInProgressException e) {
            log.error("Could not rollback Castor transaction.", e);
            throw new PersistenceException("Could not rollback Castor transaction.", e);
        }
    }

    /**
     * Mark the current transaction so that the only possible outcome of the
     * transaction is for the transaction to be rolled back.
     * 
     * @throws IllegalStateException
     *             if {@link #isActive()} is false.
     */
    public void setRollbackOnly() {
        // Verify transaction is active.
        verifyActive();
        // Set roll back only.
        this.rollbackOnly = true;
    }

    /**
     * Verifies whether the {@link EntityTransaction} is NOT active. Throws an
     * {@link IllegalStateException} in case condition is not met.
     */
    private void verifyNotActive() {
        if (isActive()) {
            throw new IllegalStateException("The EntityTransaction is already in progress.");
        }
    }

    /**
     * Verifies whether the {@link EntityTransaction} IS active. Throws an
     * {@link IllegalStateException} in case condition is not met.
     */
    private void verifyActive() {
        if (!isActive()) {
            throw new IllegalStateException("The EntityTransaction is not in progress.");
        }
    }

    /**
     * Verifies whether the {@link EntityTransaction} was invoked on an open
     * {@link EntityManager}. Throws {@link IllegalStateException} in case
     * condition is not met.
     */
    private void verifyEntityManagerIsOpen() {
        if (!this.entityManager.isOpen()) {
            throw new IllegalStateException("The EntityTransaction is already in progress.");
        }
    }
}