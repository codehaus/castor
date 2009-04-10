package org.castor.jpa;

import javax.persistence.EntityTransaction;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.TransactionNotInProgressException;

public class JDOEntityTransaction implements EntityTransaction {
    
    /**
     * The Castor JDO {@link Database} instance proxied by this class; it
     * is this class where most of the methods will be delegated to.
     */
    private Database database;
    
    /**
     * Indicates whether the ongoing transactio should be rolled back only.
     */
    private boolean rollbackOnly = false;
    
    /**
     * Creates a new EntityTransaction instance specific for Castor JDO. This 
     * class basically proxies Castor JDO's Database interface, and delegates
     * most of the methods exposed by this class to the corresponding methods
     * on the {@link Database} interface
     * @param database
     */
    public JDOEntityTransaction (Database database) {
        setDatabase(database);
    }

    /**
     * Sets the Castor JDO Database instance proxied by this class. 
     * @param database A Castor JDO Database instance
     */
    private void setDatabase(Database database) {
        this.database = database;
    }

    /**
     * @inheritDoc
     * @see javax.persistence.EntityTransaction#begin()
     */
    public void begin() {
        
        if (isActive()) {
            throw new IllegalStateException ("An active EntityTransaction is already in progress. Impossible to start a new transaction.");
        }
        
        try {
            database.begin();
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("Problem starting a new entity transaction", e);
        }
    }

    /**
     * @inheritDoc
     * @see javax.persistence.EntityTransaction#commit()
     */
    public void commit() {
        if (!isActive()) {
            throw new IllegalStateException ("Commit called on an inactive entity transaction.");
        }

        if (getRollbackOnly()) {
            rollback();
        }
        
        try {
            database.commit();
        } catch (TransactionNotInProgressException e) {
            throw new IllegalStateException ("Commit called on an inactive entity transaction.");
        } catch (TransactionAbortedException e) {
            throw new javax.persistence.PersistenceException("Problem starting a new entity transaction", e);
        }

    }

    /**
     * @inheritDoc
     * @see javax.persistence.EntityTransaction#rollback()
     */
    public void rollback() {
        if (!isActive()) {
            throw new IllegalStateException ("Rollback called on an inactive entity transaction.");
        }

        try {
            database.rollback();
        } catch (TransactionNotInProgressException e) {
            throw new IllegalStateException ("Rollback called on an inactive entity transaction.");
        }

    }

    /**
     * @inheritDoc
     * @see javax.persistence.EntityTransaction#isActive()
     */
    public boolean isActive() {
        return database.isActive();
    }

    /**
     * {@inheritDoc}
     * @see javax.persistence.EntityTransaction#getRollbackOnly()
     */
    public boolean getRollbackOnly() {
        return this.rollbackOnly;
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

}
