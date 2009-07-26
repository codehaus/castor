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
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ObjectNotFoundException;

/**
 * Castor implementation of the {@link EntityManager} interface.
 * 
 * @author lukas.lang
 * 
 */
public final class CastorEntityManager implements EntityManager {

    /**
     * A logger.
     */
    private final Log log = LogFactory.getLog(CastorEntityManager.class);

    /**
     * The Castor {@link Database} to use.
     */
    private Database database;

    /**
     * Stays <code>true</code> until {@link EntityManager#close()} is called.
     */
    private boolean open = true;

    /**
     * This {@link EntityManager}'s {@link EntityTransaction}.
     */
    private EntityTransaction entityTransaction;

    /**
     * Constructor taking a Castor {@link Database}.
     * 
     * @param database
     *            the Castor {@link Database} used by this {@link EntityManager}
     *            .
     */
    public CastorEntityManager(final Database database) {
        this.database = database;
        this.entityTransaction = new CastorEntityTransaction(this, database);
    }

    /**
     * Clear the persistence context, causing all managed entities to become
     * detached. Changes made to entities that have not been flushed to the
     * database will not be persisted.
     * 
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     */
    public void clear() {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Close an application-managed EntityManager. After the close method has
     * been invoked, all methods on the EntityManager instance and any Query
     * objects obtained from it will throw the IllegalStateException except for
     * getTransaction and isOpen (which will return false). If this method is
     * called when the EntityManager is associated with an active transaction,
     * the persistence context remains managed until the transaction completes.
     * 
     * TODO lukas.lang: Add test for a {@link Query} on a closed EM.
     * {@link EntityManager}.
     * 
     * TODO lukas.lang: Add test with a running TX.
     * 
     * @throws IllegalStateException
     *             if the EntityManager is container-managed or has been already
     *             closed..
     */
    public void close() {
        verifyOpenEntityManager();

        // TODO lukas.lang: Should the Castor database get closed?
        // Close Castor database.
        // this.database.close();

        // Close entity manager.
        this.open = false;
    }

    /**
     * Check if the instance belongs to the current persistence context.
     * 
     * @param entity
     * @return <code>true</code> if the instance belongs to the current
     *         persistence context.
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if not an entity
     */
    public boolean contains(Object entity) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Create an instance of Query for executing a named query (in the Java
     * Persistence query language or in native SQL).
     * 
     * @param name
     *            the name of a query defined in metadata
     * @return the new query instance
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if a query has not been defined with the given name
     */
    public Query createNamedQuery(String name) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Create an instance of Query for executing a native SQL statement, e.g.,
     * for update or delete.
     * 
     * @param sqlString
     *            a native SQL query string
     * @return the new query instance
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     */
    public Query createNativeQuery(String sqlString) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Create an instance of Query for executing a native SQL query.
     * 
     * @param sqlString
     *            a native SQL query string
     * @param resultClass
     *            the class of the resulting instance(s)
     * @return the new query instance
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     */
    @SuppressWarnings("unchecked")
    public Query createNativeQuery(String sqlString, Class resultClass) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Create an instance of Query for executing a native SQL query.
     * 
     * @param sqlString
     *            a native SQL query string
     * @param resultSetMapping
     *            the name of the result set mapping
     * @return the new query instance
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     */
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Create an instance of Query for executing a Java Persistence query
     * language statement.
     * 
     * @param qlString
     *            a Java Persistence query language query string
     * @return the new query instance
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if query string is not valid
     */
    public Query createQuery(String qlString) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Find by primary key.
     * 
     * @param entityClass
     * @param primaryKey
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key
     */
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        T object = null;

        try {

            // Load the entity from the Castor database.
            object = (T) this.database.load(entityClass, primaryKey);
        } catch (ObjectNotFoundException onfe) {
            if (log.isDebugEnabled()) {
                log.debug("Entity with primary key >" + primaryKey + "< not found.");
            }
            // Do nothing.
        } catch (ClassNotPersistenceCapableException npce) {
            log
                    .error("Entity class >" + entityClass.getName() + "< not persistence capable.",
                            npce);
            throw new IllegalArgumentException("Entity class >" + entityClass.getName()
                    + "< not persistence capable.");

        } catch (org.exolab.castor.jdo.PersistenceException e) {
            log
                    .error("Could not load entity with id >" + primaryKey
                            + "< from Castor database.", e);
            throw new IllegalArgumentException("Could not load entity with id >" + primaryKey
                    + "< from Castor database.", e);
        }

        // Return the object or null.
        return object;
    }

    /**
     * Synchronize the persistence context to the underlying database.
     * 
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws PersistenceException
     *             if the flush fails
     */
    public void flush() {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Return the underlying provider object for the EntityManager, if
     * available. The result of this method is implementation specific.
     * 
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     */
    public Object getDelegate() {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Get the flush mode that applies to all objects contained in the
     * persistence context.
     * 
     * @return flush mode
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     */
    public FlushModeType getFlushMode() {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Get an instance, whose state may be lazily fetched. If the requested
     * instance does not exist in the database, throws
     * {@link EntityNotFoundException} when the instance state is first
     * accessed. (The persistence provider runtime is permitted to throw
     * {@link EntityNotFoundException} when {@link #getReference} is called.)
     * 
     * TODO lukas.lang: Discuss, whether exception should be thrown immediately.
     * 
     * The application should not expect that the instance state will be
     * available upon detachment, unless it was accessed by the application
     * while the entity manager was open.
     * 
     * @param entityClass
     * @param primaryKey
     * @return the found entity instance
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key
     * @throws EntityNotFoundException
     *             if the entity state cannot be accessed
     */

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Returns the resource-level transaction object. The EntityTransaction
     * instance may be used serially to begin and commit multiple transactions.
     * 
     * @return EntityTransaction instance
     * @throws IllegalStateException
     *             if invoked on a JTA EntityManager.
     */
    public EntityTransaction getTransaction() {
        return this.entityTransaction;
    }

    /**
     * Determine whether the EntityManager is open.
     * 
     * @return true until the EntityManager has been closed.
     */
    public boolean isOpen() {
        return this.open;
    }

    /**
     * Indicate to the EntityManager that a JTA transaction is active. This
     * method should be called on a JTA application managed EntityManager that
     * was created outside the scope of the active transaction to associate it
     * with the current JTA transaction.
     * 
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws TransactionRequiredException
     *             if there is no transaction.
     */
    public void joinTransaction() {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Set the lock mode for an entity object contained in the persistence
     * context.
     * 
     * @param entity
     * @param lockMode
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws PersistenceException
     *             if an unsupported lock call is made
     * @throws IllegalArgumentException
     *             if the instance is not an entity or is a detached entity
     * @throws TransactionRequiredException
     *             if there is no transaction
     */
    public void lock(Object entity, LockModeType lockMode) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Merge the state of the given entity into the current persistence context.
     * 
     * @param entity
     * @return the instance that the state was merged to
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if instance is not an entity or is a removed entity
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     */
    public <T> T merge(T entity) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Make an entity instance managed and persistent.
     * 
     * @param entity
     * @throws EntityExistsException
     *             if the entity already exists. (The EntityExistsException may
     *             be thrown when the persist operation is invoked, or the
     *             EntityExistsException or another PersistenceException may be
     *             thrown at flush or commit time.)
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if not an entity
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     */
    public void persist(Object entity) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        try {

            // Create the entity.
            this.database.create(entity);

        } catch (ClassNotPersistenceCapableException e) {
            log.error("Entity of type >" + entity.getClass().getName()
                    + "< is not valid entity type.", e);
            throw new IllegalArgumentException("Entity of type >" + entity.getClass().getName()
                    + "< is not valid entity type.", e);
        } catch (DuplicateIdentityException e) {
            log.error("Entity of type " + entity.getClass().getName() + " already exists.");
            throw new EntityExistsException("Entity of type " + entity.getClass().getName()
                    + " already exists.", e);
        } catch (org.exolab.castor.jdo.PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Refresh the state of the instance from the database, overwriting changes
     * made to the entity, if any.
     * 
     * @param entity
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if not an entity or entity is not managed
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database.
     */
    public void refresh(Object entity) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Remove the entity instance.
     * 
     * @param entity
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     * @throws IllegalArgumentException
     *             if not an entity or if a detached entity
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     */
    public void remove(Object entity) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();
        // Check whether a transaction is running.
        verifyRunningTransaction();

        try {
            // Remove entity from database.
            this.database.remove(entity);
        } catch (org.exolab.castor.jdo.PersistenceException e) {
            log.error("Could not remove entity.", e);
            throw new IllegalArgumentException("Could not remove entity.", e);
        }
    }

    /**
     * Set the flush mode that applies to all objects contained in the
     * persistence context.
     * 
     * @param flushMode
     * @throws IllegalStateException
     *             if this EntityManager has been closed.
     */
    public void setFlushMode(FlushModeType flushMode) {
        // Check whether the entity manager is open.
        verifyOpenEntityManager();

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * Verifies that {@link EntityManager#isOpen()} returns true.
     * 
     * @throws IllegalStateException
     *             in case {@link EntityManager#close()} was called before.
     */
    private void verifyOpenEntityManager() {
        if (!isOpen()) {
            throw new IllegalStateException("Method called on inactive EntityManager instance.");
        }
    }

    /**
     * Verifies that an {@link EntityTransaction} is active.
     */
    private void verifyRunningTransaction() {
        if (!this.entityTransaction.isActive()) {
            throw new TransactionRequiredException(
                    "Method called while no EntityTransaction was running.");
        }
    }
}