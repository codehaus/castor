package org.castor.spring.orm;


import java.util.Collection;
import org.springframework.dao.DataAccessException;

/**
 * Interface that specifies a basic set of Castor JDO operations.
 * Implemented by CastorTemplate. Not often used, but a useful option
 * to enhance testability, as it can easily be mocked or stubbed.
 *
 * <p>Provides CastorTemplate's data access methods that mirror various
 * Database methods. See the Castor Database javadocs for details 
 * on those methods.
 *
 * <p>Note that lazy loading will just work with an open Castor JDO Database,
 * either within a Spring-driven transaction (with CastorTransactionManager or
 * JtaTransactionManager).
 *
 * Furthermore, some operations just make sense within transactions,
 * for example: <code>evict</code>, <code>evictAll</code>, <code>flush</code>.
 *
 * @author Juergen Hoeller, Werner Guttmann
 * @since 1.2
 * @see CastorTemplate
 * @see org.exolab.castor.jdo.Database
 * @see CastorTransactionManager
 * @see org.springframework.transaction.jta.JtaTransactionManager
 */
public interface CastorOperations {

    /**
     * Execute the action specified by the given action object within the scope 
     * of a Castor JDO Database. Application exceptions thrown by the action object
     * get propagated to the caller (can only be unchecked). Castor exceptions
     * are transformed into appropriate DAO ones. Allows for returning a
     * result object, i.e. a domain object or a collection of domain objects.
     * 
     * Note: Callback code is not supposed to handle transactions itself ! Use an 
     * appropriate transaction manager like CastorTransactionManager or JtaTransactionManager.
     * 
     * @param action callback object that specifies the JDO action
     * @return a result object returned by the action, or null
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see CastorTransactionManager
     * @see org.springframework.dao
     * @see org.springframework.transaction
     * @see org.exolab.castor.jdo.Database
     */
    Object execute(CastorCallback action) throws DataAccessException;

    /**
     * Execute the specified action assuming that the result object is a
     * Collection. This is a convenience method for executing Castor JDO queries
     * within an action.
     * @param action callback object that specifies the Castor JDO action
     * @return a Collection result returned by the action, or null
     * @throws org.springframework.dao.DataAccessException in case of JDO errors
     * @see execute (CastorCallback)
     */
    Collection executeFind(CastorCallback action) throws DataAccessException;


    //-------------------------------------------------------------------------
    // Convenience methods for load, save, delete
    //-------------------------------------------------------------------------

    /**
     * Remove the given object from the Castor cache.
     * @param entity the persistent instance to evict
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.CacheManager#release
     */
    void evict(Object entity) throws DataAccessException;

    /**
     * Remove all objects of a given class from the Castor cache.
     * @param entityClass Entity class type.
     * @throws org.springframework.dao.DataAccessException in case of JDO errors
     * @see org.exolab.castor.jdo.CacheManager#release
     */
    void evictAll(Class entityClass) throws DataAccessException;

    /**
     * Make the given transient instance persistent.
     * @param entity the transient instance to make persistent
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.Database#create
     */
    void create(Object entity) throws DataAccessException;

    /**
     * Loads the persistent instance identified by the id.
     * @param entityClass a persistent class
     * @param idValue Unique identifier. 
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.Database#load
     */
    Object load (Class entityClass, Object idValue) throws DataAccessException;
    
    /**
     * Delete the given persistent instance.
     * @param entity the persistent instance to delete
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.Database#delete
     */
    void remove(Object entity) throws DataAccessException;

    /**
     * Delete all given persistent instances.
     * <p>This can be combined with any of the find methods to delete by query
     * in two lines of code.
     * @param entities the persistent instances to delete
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see remove (Object)
     * @see org.exolab.castor.jdo.Database#delete
     */
    void removeAll(Collection entities) throws DataAccessException;

    /**
     * Updates the given (time-stampable) instance as part of a long
     * transaction.
     * @param entity the time-stampable persistent instance to update
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.Database#delete
     */
    void update(Object entity) throws DataAccessException;
    
    /**
     * Flush all transactional modifications to the database.
     * <p>Only invoke this for selective eager flushing, for example when JDBC code
     * needs to see certain changes within the same transaction. Else, it's preferable
     * to rely on auto-flushing at transaction completion.
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see CastorDialect#flush
     */
    void flush() throws DataAccessException;


    //-------------------------------------------------------------------------
    // Convenience finder methods
    //-------------------------------------------------------------------------

    /**
     * Return all persistent instances of the given class.
     * @param entityClass a persistent class
     * @return the persistent instances
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.Database#createOQLQuery(String)
     */
    Collection find(Class entityClass) throws DataAccessException;

    /**
     * Return all persistent instances of the given class that match the given OQL WHERE clause.
     * @param entityClass a persistent class
     * @param filter the OQL WHERE clause to match (or null if none)
     * @return the persistent instances
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.Database#createOQLQuery(String)
     */
    Collection find(Class entityClass, String filter) throws DataAccessException;

    /**
     * Return all persistent instances of the given class that match the given OQL
     * WHERE clause, with the given result ordering.
     * @param entityClass a persistent class
     * @param filter the OQL WHERE to match (or null if none)
     * @param ordering the ordering of the result (or null if none)
     * @return the persistent instances
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     * @see org.exolab.castor.jdo.Database#createOQLQuery(String)
     */
    Collection find(Class entityClass, String filter, String ordering)
            throws DataAccessException;

    /**
     * Return all persistent instances of the given class that match the given
     * OQL WHERE clause, using the given parameter declarations and parameter values.
     * @param entityClass a persistent class
     * @param filter the OQL WHERE clause to match
     * @param values the corresponding parameter values
     * @return the persistent instances
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
    * @see org.exolab.castor.jdo.Database#createOQLQuery(String)
     */
    Collection find(Class entityClass, String filter, Object[] values)
            throws DataAccessException;

    /**
     * Return all persistent instances of the given class that match the given
     * OQL WHERE clause, using the given parameter declarations and parameter values,
     * with the given result ordering.
     * @param entityClass a persistent class
     * @param filter the OQL WHERE clause to match
     * @param values the corresponding parameter values
     * @param ordering the ordering of the result (or null if none)
     * @return the persistent instances
     * @throws org.springframework.dao.DataAccessException in case of JDO errors
     */
    Collection find(
            Class entityClass, String filter, Object[] values, String ordering)
            throws DataAccessException;
    
    /**
     * Return all persistent instances of a given class as specified in a named
     * query with the given name
     * @param queryName Name of the OQL query to execute
     * @return the persistent instances
     * @throws org.springframework.dao.DataAccessException in case of JDO errors
     */
    public Collection findByNamedQuery(String queryName) throws DataAccessException;
    
    /**
     * Return all persistent instances of a given class as specified in a named
     * query with the given name, supplying additional parameters to be used during
     * uery execution.
     * @param queryName Name of the OQL query to execute
     * @param parameters Additional parameter values to be used during query execution.
     * @return the persistent instances
     * @throws org.springframework.dao.DataAccessException in case of JDO errors
     */
    public Collection findByNamedQuery(final String queryName, final Object[] values);


    /**
     * Indicates whether the object given is cached by Castor JDO.
     * @param entity Object entity.
     * @return True if the given entity is cached.
     */
    boolean isCached(Object entity);


    
}
