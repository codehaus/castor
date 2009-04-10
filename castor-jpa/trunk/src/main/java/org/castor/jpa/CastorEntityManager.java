package org.castor.jpa;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectNotPersistentException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.TransactionNotInProgressException;

public class CastorEntityManager implements EntityManager {
    
    private static final Log LOG = LogFactory.getLog(CastorEntityManager.class);
    
    private Database database;
    private FlushModeType flushMode;
    private PersistenceContextType persistenceContextType;
    boolean isActive = true;
    boolean isOpen = true;
    
    public CastorEntityManager (
            final PersistenceContextType persistenceContextType,
            final Database database) {
        setPersistenceContextType(persistenceContextType);
        setDatabase(database);
    }
    
    private boolean isActive() {
        return isActive;
    }
    
    private void setDatabase(Database database) {
        this.database = database;
    }

    private void setPersistenceContextType(
            final PersistenceContextType persistenceContextType) {
        this.persistenceContextType = persistenceContextType;
    }

    private void checkIsOpen() {
        if (!isOpen()) {
            throw new IllegalStateException("Method called on inactive EntityManager instance.");
        }
    }
    
    /**
     * Checks whether there is an active transaction, if the specified persistence 
     * context type is of type TRANSACTION.
     * @param persistenceContextType Specified persistence context type.
     * @throws TransactionRequiredException If there is not active transaction.
     */
    private void checkActiveTransaction (
            final PersistenceContextType persistenceContextType) {
        if (persistenceContextType == PersistenceContextType.TRANSACTION 
                && !database.isActive()) {
            throw new TransactionRequiredException("Active transaction required when using persistence context type TRANSACTION");
        }
    }
    
    private void checkNoOrDetachedEntity() {
        // TODO throw IllegalArgumentException if not an entity or entity is detached
    }

    public void persist(Object entity) {

        checkIsOpen();
        checkNoOrDetachedEntity();
        checkActiveTransaction(persistenceContextType);
        
        try {
            database.create(entity);
        } catch (ClassNotPersistenceCapableException e) {
            throw new IllegalArgumentException(entity.getClass().getName() + " is not a valid entity", e);
        } catch (DuplicateIdentityException e) {
            throw new EntityExistsException("Entity of type " + entity.getClass().getName() + " exists already", e);
        } catch (TransactionNotInProgressException e) {
            TransactionRequiredException tre = new TransactionRequiredException("There's no active transaction.");
            tre.initCause(e);
            throw tre;
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("General prob lem", e);
        }
    }

    public <T> T merge(T entity) {

        checkIsOpen();
        checkNoOrDetachedEntity();
        checkActiveTransaction(persistenceContextType);

        // TODO Implement !!!
        throw new UnsupportedOperationException();
    }

    public void remove(Object entity) {
        
        checkIsOpen();
        checkNoOrDetachedEntity();
        checkActiveTransaction(persistenceContextType);
        
        try {
            database.remove(entity);
        } catch (ObjectNotPersistentException e) {
            throw new javax.persistence.EntityNotFoundException("Object of type " + entity.getClass().getName() + " not found");
        } catch (LockNotGrantedException e) {
            // TODO !!!!!!!!!!!! Investigate what should be done !!!!!!!!!!!!!!
            throw new javax.persistence.PersistenceException("Lock not granted by Castor", e);
        } catch (TransactionNotInProgressException e) {
            TransactionRequiredException tre = new TransactionRequiredException("Active transaction required");
            tre.initCause(e);
            throw tre;
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("General prob lem", e);
        }
    }
    
    /**
     * Find by primary key.
     * 
     * @param entityClass Entity class.
     * @param primaryKey Primatry key object instance
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException if the first argument does not denote an 
     *         entity type or the second argument is not a valid type for
     *         that entity’s primary key
     */
    public <T> T find(Class<T> entityClass, Object primaryKey) {

        T object = null;
        
        checkIsOpen();

        // TODO throw IllegalArgumentException if the first argument does not denote an entity type
        
        // TODO throw IllegalArgumentException if the second argument is not a valid type for that entity’s primary key
        
        try {
            object = (T) database.load(entityClass, primaryKey);
        } catch (ObjectNotFoundException e) {
            // do not do anything, as 'null' should be returned
            // throw new EntityNotFoundException("Entity of type " + entityClass.getName() + " with id " + primaryKey + " not found", e);
        } catch (TransactionNotInProgressException e) {
            TransactionRequiredException tre = new TransactionRequiredException("Active transaction required");
            tre.initCause(e);
            throw tre;
        } catch (LockNotGrantedException e) {
            // TODO !!!!!!!!!!!! Investigate what should be done !!!!!!!!!!!!!!
            throw new javax.persistence.PersistenceException("Lock not granted by Castor", e);
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("General prob lem", e);
        }
        
        return object;
    }

    /**
     * Get an instance, whose state may be lazily fetched. If the requested 
     * instance does not exist in the database, throws EntityNotFoundException when 
     * the instance state is first accessed. (The persistence provider runtime is 
     * permitted to throw the EntityNotFoundException when getReference is called.) 
     * The application should not expect that the instance state will be available
     * upon detachment, unless it was accessed by the application while the 
     * entity manager was open.
     * 
     * @param entityClass
     * @param primaryKey
     * @return the found entity instance
     * @throws IllegalArgumentException if the first argument does not denote an 
     *         entity type or the second argument is not a valid type for that 
     *         entity’s primary key
     * @throws EntityNotFoundException if the entity state cannot be accessed
     */
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
    
        T object = null;
        
//        // TODO throw IllegalArgumentException if the first argument does not denote an entity type
//        
//        // TODO throw IllegalArgumentException if the second argument is not a valid type for that entity’s primary key
//
//        try {
//            // TODO find a way to pass in 'laziness' at run-time
//            object = (T) database.getReference(entityClass, primaryKey);
//        }
//        catch (ObjectNotFoundException e) {
//            throw new EntityNotFoundException("Entity of type " + entityClass.getName() + " with id " + primaryKey + " not found", e);
//        } catch (TransactionNotInProgressException e) {
//            throw new TransactionRequiredException("Active transaction required", e);
//        } catch (LockNotGrantedException e) {
//            // TODO !!!!!!!!!!!! Investigate what should be done !!!!!!!!!!!!!!
//            throw new javax.persistence.PersistenceException("Lock not granted by Castor", e);
//        } catch (PersistenceException e) {
//            throw new javax.persistence.PersistenceException("General problem", e);
//        }
        
        return object;
    }


    public void flush() {
        checkIsOpen();
        throw new UnsupportedOperationException();
    }

    public void setFlushMode(FlushModeType flushMode) {
        checkIsOpen();
        this.flushMode = flushMode;
    }

    public FlushModeType getFlushMode() {
        checkIsOpen();
        return flushMode;
    }

    public void lock(Object entity, LockModeType lockMode) {
        checkIsOpen();
        throw new UnsupportedOperationException();
    }

    public void refresh(Object entity) {
        checkIsOpen();
        throw new UnsupportedOperationException();
    }

    public void clear() {
        checkIsOpen();
        // database.clear();
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object entity) {
        checkIsOpen();
        return database.isPersistent(entity);
    }

    public Query createQuery(String ejbqlString) {
        checkIsOpen();
        return new CastorQuery (ejbqlString, database);
    }

    public Query createNamedQuery(String name) {
        checkIsOpen();
        try {
            return new CastorQuery(database.getNamedQuery(name));
        } catch (PersistenceException e) {
            throw new javax.persistence.PersistenceException("General problem", e);
        }
    }

    public Query createNativeQuery(String sqlString) {
        checkIsOpen();
        throw new UnsupportedOperationException();
    }

    public Query createNativeQuery(String sqlString, Class resultClass) {
        checkIsOpen();
        return new CastorQuery (sqlString, resultClass, database);
    }

    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        checkIsOpen();
        throw new UnsupportedOperationException();
    }

    public void close() {
        checkIsOpen();
        if (isActive()) {
            try {
                database.close();
            } catch (PersistenceException e) {
                LOG.warn("Problem closing Castor JDO Database instance", e);
            }
            isOpen = false;
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public EntityTransaction getTransaction() {
        checkIsOpen();
        
        // TODO throw IllegalStateException if invoked on a JTA EntityManager 
        
        return new JDOEntityTransaction(database);
    }

    public Object getDelegate() {
        // TODO Auto-generated method stub
        return null;
    }

    public void joinTransaction() {
        // TODO Auto-generated method stub
    }

}
