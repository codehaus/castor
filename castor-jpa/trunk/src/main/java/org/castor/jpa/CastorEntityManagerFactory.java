package org.castor.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;

import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.PersistenceException;

public class CastorEntityManagerFactory implements EntityManagerFactory {
    JDOManager jdoManager = null;

    /**
     * Indicates whether this EntityManagerFactory is open.
     */
    boolean isOpen = true;

    public CastorEntityManagerFactory(JDOManager jdoManager) {
        setJdoManager(jdoManager);
    }

    private void setJdoManager(JDOManager jdoManager) {
        this.jdoManager = jdoManager;
    }

    public EntityManager createEntityManager() {
        if (!isOpen) {
            throw new IllegalStateException(
                    "'createEntityManager' called on a closed EntityManagerFactory.");
        }

        EntityManager entityManager = null;

        try {
            entityManager = new CastorEntityManager(
                    PersistenceContextType.TRANSACTION, jdoManager
                            .getDatabase());
        } catch (PersistenceException e) {
            // TODO !!!!!!!!!!!!!!! Investigate what to do about this
            // !!!!!!!!!!!!!!!!!!!
            throw new javax.persistence.PersistenceException(
                    "Problem obtaining Castor JDO Database instance from JDOManager");
        }

        return entityManager;
    }

    public EntityManager createEntityManager(PersistenceContextType type) {
        if (!isOpen) {
            throw new IllegalStateException(
                    "'createEntityManager' called on a closed EntityManagerFactory.");
        }

        if (type == PersistenceContextType.TRANSACTION) {
            return createEntityManager();
        }

        // deal with PersistenceContextType.EXTENDED
        return null;
    }

    public EntityManager getEntityManager() {
        if (!isOpen) {
            throw new IllegalStateException(
                    "'createEntityManager' called on a closed EntityManagerFactory.");
        }

        throw new UnsupportedOperationException();
    }

    public EntityManager createEntityManager(final Map map) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        if (!isOpen) {
            throw new IllegalStateException(
                    "'createEntityManager' called on a closed EntityManagerFactory.");
        }

        isOpen = false;

    }

    public boolean isOpen() {
        return isOpen;
    }

}
