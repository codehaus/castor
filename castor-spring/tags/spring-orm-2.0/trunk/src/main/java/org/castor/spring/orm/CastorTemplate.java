package org.castor.spring.orm;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryResults;
import org.springframework.dao.DataAccessException;

/**
 * Helper class that simplifies Castor JDO data access code, and converts
 * PersistenceExceptions into Spring DataAccessExceptions, following the
 * <code>org.springframework.dao</code> exception hierarchy.
 *
 * <p>The central method is "execute", supporting Castor JDO code implementing
 * the CastorCallback interface. It provides JDO Database handling
 * such that neither the CstorCallback implementation nor the calling code
 * needs to explicitly care about retrieving/closing Database instances,
 * or handling Castor JDO lifecycle exceptions.
 *
 * <p>Typically used to implement data access or business logic services that
 * use Castor JDO within their implementation but are Castor JDO-agnostic in their interface.
 * The latter or code calling the latter only have to deal with business
 * objects, query objects, and <code>org.springframework.dao</code> exceptions.
 *
 * <p>Can be used within a service implementation via direct instantiation
 * with a JDOManager reference, or get prepared in an application context and given 
 * to services as bean reference.
 * Note: The JDOManager should always be configured as bean in the application context, 
 * in the first case given to the service directly, in the second case to the prepared template.
 *
 * <p>This class can be considered a programmatic alternative to
 * CastorInterceptor. The major advantage is its straightforwardness, the
 * major disadvantage that no checked application exceptions can get thrown
 * from within data access code. Respective checks and the actual throwing of
 * such exceptions can often be deferred to after callback execution, though.
 *
 * <p>Note that even if CastorTransactionManager is used for transaction
 * demarcation in higher-level services, all those services above the data
 * access layer don't need need to be Castor JDO-aware. Setting such a special
 * PlatformTransactionManager is a configuration issue, without introducing
 * code dependencies.
 *
 * <p>LocalCastorFactoryBean is the preferred way of obtaining a
 * reference to a specific JDOManager, at least in a non-EJB
 * environment. Registering a JDOManager with JNDI is only
 * advisable when using a JCA Connector, i.e. when the application server
 * cares for initialization. Else, portability is rather limited: Manual
 * JNDI binding isn't supported by some application servers (e.g. Tomcat).
 *
 * <p>Note that lazy loading will just work with an open JDO Database,
 * either within a Spring-driven transaction (with CastorTransactionManager or
 * JtaTransactionManager) or within OpenDatabaseInViewFilter/Interceptor.
 * Furthermore, some operations just make sense within transactions,
 * for example: <code>evict</code> or <code>evictAll</code>.
 *
 * @author Werner Guttmann
 * @since 20.05.2005
 * @see #JDOManager
 * @see CastorCallback
 * @see org.exolab.castor.jdo.Database
 * @see CastorInterceptor
 * @see LocalCastorFactoryBean
 * @see org.springframework.jndi.JndiObjectFactoryBean
 * @see CastorTransactionManager
 * @see org.springframework.transaction.jta.JtaTransactionManager
 * @see org.castor.spring.orm.support.OpenDatabaseInViewFilter
 * @see org.castor.spring.orm.support.OpenDatabaseInViewInterceptor
 */
public class CastorTemplate extends CastorAccessor implements CastorOperations {

    private boolean allowCreate = true;

    private boolean exposeNativeDatabase = true;


    /**
     * Create a new CastorTemplate instance.
     */
    public CastorTemplate() {
    	// nothing to do ???
    }

    /**
     * Create a new CastorTemplate instance.
     * @param jdoManager JDOManager to create Databases
     */
    public CastorTemplate(JDOManager jdoManager) {
        setJDOManager(jdoManager);
        afterPropertiesSet();
    }

    /**
     * Create a new CastorTemplate instance.
     * @param jdoManager JDOManager to create Databases
     * @param allowCreate if a new Database should be created
     * if no thread-bound found
     */
    public CastorTemplate(JDOManager jdoManager, boolean allowCreate) {
        setJDOManager(jdoManager);
        setAllowCreate(allowCreate);
        afterPropertiesSet();
    }

    /**
     * Set if a new Database should be created if no thread-bound found.
     * <p>CstorTemplate is aware of a respective Database bound to the
     * current thread, for example when using CastorTransactionManager.
     * If allowCreate is true, a new Database will be created if none
     * found. If false, an IllegalStateException will get thrown in this case.
     * @param allowCreate If true, a new Database will be created if none found
     * @see JDOManagerUtils#getDatabase(JDOManager,boolean)
     */
    public void setAllowCreate(boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    /**
     * Return if a new Database should be created if no thread-bound found.
     * @return True if a new Database will be created if none found. 
     */
    public boolean isAllowCreate() {
        return this.allowCreate;
    }

    /**
     * Set whether to expose the native Castor JDO Database to CastorCallback
     * code. Default is true; if turned off, a Database proxy will be returned, 
     * suppressing <code>close()</code> calls and automatically applying transaction 
     * timeouts (if any).
     * <p>The default is "true" for the time being, because there is often a need
     * to cast to a vendor-specific Database class in DAOs that use the Castor JDO 
     * API and/or other vendor-specific functionality. (This is likely to change 
     * to "false" in a later Spring version)
     * @param exposeNativeDatabase True if native Castor JDO Database should be exposed to CastorCallback
     * code.  
     * @see CastorCallback
     * @see org.exolab.castor.jdo.Database
     * @see #prepareQuery
     */
    public void setExposeNativeDatabase(boolean exposeNativeDatabase) {
        this.exposeNativeDatabase = exposeNativeDatabase;
    }

    /**
     * Return whether to expose the native JDO Database to CastorCallback
     * code, or rather a Database proxy.
     * @return whether native Castor JDO Database should be exposed to CastorCallback code. 
     */
    public boolean isExposeNativeDatabase() {
        return this.exposeNativeDatabase;
    }


    public Object execute(CastorCallback action) throws DataAccessException {
        return execute(action, isExposeNativeDatabase());
    }

    public Collection executeFind(CastorCallback action) throws DataAccessException {
        return (Collection) execute(action, isExposeNativeDatabase());
    }

    /**
     * Execute the action specified by the given action object within a Castor Database.
     * @param action callback object that specifies the Castor JDO action
     * @param exposeNativeDatabase whether to expose the native JDO Database to callback code
     * @return a result object returned by the action, or null
     * @throws org.springframework.dao.DataAccessException in case of Castor JDO errors
     */
    public Object execute(CastorCallback action, boolean exposeNativeDatabase) throws DataAccessException {
        Database database = JDOManagerUtils.getDatabase(
            getJDOManager(), isAllowCreate());
        // TODO [WG]: uncomment after having implemented support for (eager) flushing
        // boolean existingTransaction =
        //    TransactionSynchronizationManager.hasResource(getJDOManager());
        try {
            Database databaseToExpose = (exposeNativeDatabase ? database : createDatabaseProxy(database));
            Object result = action.doInCastor(databaseToExpose);
            // flushIfNecessary(database, existingTransaction);
            return result;
        }
        catch (PersistenceException ex) {
            throw convertJdoAccessException(ex);
        }
        catch (RuntimeException ex) {
            // callback code threw application exception
            throw ex;
        }
        finally {
            JDOManagerUtils.closeDatabaseIfNecessary(database, getJDOManager());
        }
    }

    /**
     * Create a close-suppressing proxy for the given Castor JDO Database.
     * The proxy also prepares returned Castor JDO OQLQuery objects.
     * @param database the Castor JDO Database to create a proxy for
     * @return the Database proxy
     * @see org.exolab.castor.jdo.Database#close
     * @see #prepareQuery
     */
    protected Database createDatabaseProxy(Database database) {
        return (Database) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Database.class},
                new CloseSuppressingInvocationHandler(database));
    }


    //-------------------------------------------------------------------------
    // Convenience methods for load, save, delete
    //-------------------------------------------------------------------------

    
    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#load(java.lang.Class, java.lang.Object)
     */
    public Object load(final Class entityClass, final Object idValue) throws DataAccessException {
        return execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                return database.load(entityClass, idValue);
            }
        });
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#evict(java.lang.Object)
     */
    public void evict(final Object entity) throws DataAccessException {
        execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                database.getCacheManager().expireCache(entity.getClass(), database.getIdentity(entity));
                return null;
            }
        });
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#evictAll(java.lang.Class)
     */
    public void evictAll(final Class entityClass) throws DataAccessException {
        execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                database.getCacheManager().expireCache(new Class[] { entityClass });
                return null;
            }
        });
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#create(java.lang.Object)
     */
    public void create(final Object entity) throws DataAccessException {
        execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                database.create(entity);
                return null;
            }
        });
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#remove(java.lang.Object)
     */
    public void remove(final Object entity) throws DataAccessException {
        execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                database.remove(entity);
                return null;
            }
        });
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#removeAll(java.util.Collection)
     */
    public void removeAll(final Collection entities) throws DataAccessException {
        execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                for (Iterator iter = entities.iterator(); iter.hasNext(); ) {
                    Object object = iter.next();
                    Object object2Delete = database.load(object.getClass(), database.getIdentity(object));
                    database.remove(object2Delete);
                }
                return null;
            }
        });
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#update(java.lang.Object)
     */
    public void update(final Object entity) throws DataAccessException {
        execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                database.update(entity);
                return null;
            }
        });
    }

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#flush()
     */
    public void flush() throws DataAccessException {
        execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                getJdoDialect().flush(database);
                return null;
            }
        });
    }

    //-------------------------------------------------------------------------
    // Convenience isXXX() methods 
    //-------------------------------------------------------------------------

    /**
     * @inheritDoc
     * @see org.castor.spring.orm.CastorOperations#evictAll(java.lang.Class)
     */
    public boolean isCached(final Object entity) throws DataAccessException {
        Boolean isCached = (Boolean) execute(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                boolean isCached = database.getCacheManager().isCached(entity.getClass(), database.getIdentity(entity));
                return Boolean.valueOf(isCached);
            }
        });
        return isCached.booleanValue();
    }

    //-------------------------------------------------------------------------
    // Convenience finder methods
    //-------------------------------------------------------------------------

    public Collection find(Class entityClass) throws DataAccessException {
        return this.find(entityClass, null);
    }

    public Collection findNative(final Class entityClass, final String sqlStatement) throws DataAccessException {
        return executeFind(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                StringBuffer oql = new StringBuffer();
                oql.append ("CALL SQL " + sqlStatement + " AS " + entityClass.getName());
                OQLQuery query = database.getOQLQuery(oql.toString());
                prepareQuery(query);
                QueryResults results = query.execute();
                Collection objects = new ArrayList();
                while (results.hasMore()) {
                    objects.add (results.next());
                }
                return objects;
            }
        });
    }
    
    public Collection find(Class entityClass, String filter) throws DataAccessException {
        return this.find(entityClass, filter, (Object[]) null);
    }

    public Collection find(final Class entityClass, final String filter, final String ordering)
            throws DataAccessException {
        return executeFind(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
            	StringBuffer oql = new StringBuffer();
            	oql.append ("select o from " + entityClass.getName() + " o ");
            	if (filter != null) {
            		oql.append (" " + filter + " ");
            	}
                if (ordering != null) {
                    oql.append (" order by " + ordering);
                }
                OQLQuery query = database.getOQLQuery(oql.toString());
                prepareQuery(query);
                QueryResults results = query.execute();
                Collection objects = new ArrayList();
                while (results.hasMore()) {
                    objects.add (results.next());
                }
                return objects;
            }
        });
    }

    public Collection find(Class entityClass, String filter, Object[] values)
            throws DataAccessException {
        return this.find(entityClass, filter, values, null);
    }

    public Collection find(
            final Class entityClass, final String filter, final Object[] values, final String ordering) 
    throws DataAccessException {
        return executeFind(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
            	StringBuffer oql = new StringBuffer();
            	oql.append ("select o from " + entityClass.getName() + " o ");
            	if (filter != null) {
            		oql.append (" " + filter + " ");
            	}
                if (ordering != null) {
                    oql.append (" order by " + ordering);
                }
                OQLQuery query = database.getOQLQuery(oql.toString());
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        query.bind (values[i]);
                    }
                }
                prepareQuery(query);
                QueryResults results = query.execute();
                Collection objects = new ArrayList();
                while (results.hasMore()) {
                    objects.add (results.next());
                }
                return objects;
            }
        });
    }

    //-------------------------------------------------------------------------
    // Convenience finder methods that return one (single) entity only.
    //-------------------------------------------------------------------------

    public Object findSingle(Class entityClass) throws DataAccessException {
        Collection entities = this.find(entityClass);
        return entities.iterator().next();
    }

    public Object findSingle(Class entityClass, String filter) throws DataAccessException {
        return this.find(entityClass, filter).iterator().next();
    }

    public Object findSingle(final Class entityClass, final String filter, final String ordering)
    throws DataAccessException {
        return this.find(entityClass, filter, ordering).iterator().next();
    }

    public Object findSingle(Class entityClass, String filter, Object[] values)
    throws DataAccessException {
        return this.find(entityClass, filter, values).iterator().next();
    }

    public Object findSingle(
            final Class entityClass, final String filter, final Object[] values, final String ordering) 
    throws DataAccessException {
        return this.find(entityClass, filter, values, ordering).iterator().next();
    }
    
    //-------------------------------------------------------------------------
    // Convenience finder methods using named queries
    //-------------------------------------------------------------------------
    
    public Collection findByNamedQuery(String queryName) throws DataAccessException {
        return this.findByNamedQuery(queryName, null);
    }

    public Collection findByNamedQuery(final String queryName, final Object[] values)
            throws DataAccessException {
        return executeFind(new CastorCallback() {
            public Object doInCastor(Database database) throws PersistenceException {
                OQLQuery query = database.getNamedQuery(queryName);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        query.bind (values[i]);
                    }
                }
                prepareQuery(query);
                QueryResults results = query.execute();
                Collection objects = new ArrayList();
                while (results.hasMore()) {
                    objects.add (results.next());
                }
                return objects;
            }
        });
    }

    /**
     * Prepare the given Castor JDO OQLQuery object. To be used within a CastorCallback.
     * Applies a transaction timeout, if any. If you don't use such timeouts,
     * the call is a no-op.
     * <p>In general, prefer a proxied Database instead, which will
     * automatically apply the transaction timeout (through the use of a special
     * Database proxy). You need to set the "exposeNativeDatabase"
     * property to "false" to activate this. Note that you won't be able to cast
     * to a vendor-specific Castor JDO Database class anymore then.
     * @param query the Castor JDO OQLQuery object
     * @throws PersistenceException if the query could not be properly prepared
     * @see CastorCallback#doInCastor
     * @see JDOManagerUtils#applyTransactionTimeout
     * @see #setExposeNativeDatabase
     */
    public void prepareQuery(OQLQuery query) throws PersistenceException {
        JDOManagerUtils.applyTransactionTimeout(
                query, getJDOManager(), getJdoDialect());
    }


    /**
     * Invocation handler that suppresses close calls on JDO Databases.
     * Also prepares returned Query and Criteria objects.
     * @see org.exolab.castor.jdo.Database#close
     */
    private class CloseSuppressingInvocationHandler implements InvocationHandler {

        private static final String DATABASE_CLOSE_METHOD_NAME = "close";

        private final Database target;

        public CloseSuppressingInvocationHandler(Database target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Handle close method: suppress, not valid.
            if (method.getName().equals(DATABASE_CLOSE_METHOD_NAME)) {
                return null;
            }

            // Invoke method on target connection.
            try {
                Object retVal = method.invoke(this.target, args);

                // If return value is a Castor JDO OQLQuery object, apply transaction timeout.
                if (retVal instanceof OQLQuery) {
                    prepareQuery(((OQLQuery) retVal));
                }

                return retVal;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

}
