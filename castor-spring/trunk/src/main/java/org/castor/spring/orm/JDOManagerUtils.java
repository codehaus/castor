package org.castor.spring.orm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.spring.orm.dialect.CastorDialect;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.FatalPersistenceException;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectNotPersistentException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.TransactionNotInProgressException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * Helper class featuring methods for Castor CastorManager handling,
 * allowing for reuse of Database instances within transactions.
 *
 * <p>Used by CastorTemplate, CastorInterceptor, and CastorTransactionManager.
 * Can also be used directly in application code, e.g. in combination
 * with CastorInterceptor.
 *
 * @author Werner Guttmann
 * @since 20.05.2005
 * @see CastorTemplate
 * @see CastorInterceptor
 * @see CastorTransactionManager
 */
public abstract class JDOManagerUtils {

    /**
     * Order value for TransactionSynchronization objects that clean up JDO
     * Databases. Return DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 100
     * to execute Database cleanup before JDBC Connection cleanup, if any.
     * @see org.springframework.jdbc.datasource.DataSourceUtils#CONNECTION_SYNCHRONIZATION_ORDER
     */
    public static final int DATABASE_SYNCHRONIZATION_ORDER =
            DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 100;

    private static final Log logger = LogFactory.getLog(JDOManagerUtils.class);


    /**
     * Create an appropriate SQLExceptionTranslator for the given JDOManager.
     * If a DataSource is found, create a SQLErrorCodeSQLExceptionTranslator for the
     * DataSource; else, fall back to a SQLStateSQLExceptionTranslator.
     * @param jdoManager the JDOManager to create the translator for
     * @return the SQLExceptionTranslator
     * @see org.exolab.castor.jdo.JDOManager#getConnectionFactory
     * @see org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
     * @see org.springframework.jdbc.support.SQLStateSQLExceptionTranslator
     */
    public static SQLExceptionTranslator newJdbcExceptionTranslator(JDOManager jdoManager) {
//        if (jdoManager != null) {
//            // Check for JDOManager's DataSource.
//            Object cf = jdoManager.getConnectionFactory();
//            if (cf instanceof DataSource) {
//                return new SQLErrorCodeSQLExceptionTranslator((DataSource) cf);
//            }
//        }
        return new SQLStateSQLExceptionTranslator();
    }

    /**
     * Get a JDO Database via the given factory. Is aware of a
     * corresponding Database bound to the current thread,
     * for example when using CastorTransactionManager. Will create a new
     * Database else, if allowCreate is true.
     * @param jdoManager JDOManager to create the Database instance with
     * @param allowCreate if a new Database should be created if no thread-bound found
     * @return the Database
     * @throws DataAccessResourceFailureException if the Database couldn't be created
     * @throws IllegalStateException if no thread-bound Database found and allowCreate false
     */
    public static Database getDatabase(JDOManager jdoManager, boolean allowCreate)
        throws DataAccessResourceFailureException, IllegalStateException {

        return getDatabase(jdoManager, allowCreate, true);
    }

    /**
     * Get a JDO Database via the given factory. Is aware of a
     * corresponding Database bound to the current thread,
     * for example when using CastorTransactionManager. Will create a new
     * Database else, if allowCreate is true.
     * @param jdoManager JDOManager to create the Databae with
     * @param allowCreate if a new Database should be created if no thread-bound found
     * @param allowSynchronization if a new JDO Database is supposed to be
     * registered with transaction synchronization (if synchronization is active).
     * This will always be true for typical data access code.
     * @return the Database
     * @throws DataAccessResourceFailureException if the Database couldn't be created
     * @throws IllegalStateException if no thread-bound Database found and allowCreate false
     */
    public static Database getDatabase(
            JDOManager jdoManager, boolean allowCreate, boolean allowSynchronization)
        throws DataAccessResourceFailureException, IllegalStateException {

        Assert.notNull(jdoManager, "No JDOManager specified");

        DatabaseHolder jdoManagerHolder =
                (DatabaseHolder) TransactionSynchronizationManager.getResource(jdoManager);
        if (jdoManagerHolder != null) {
            return jdoManagerHolder.getDatabase();
        }

        if (!allowCreate) {
            throw new IllegalStateException("No JDO Database bound to thread, " +
                    "and configuration does not allow creation of new one here");
        }

        logger.debug("Opening JDO Database");
        try {
            Database database = jdoManager.getDatabase();
            if (allowSynchronization && TransactionSynchronizationManager.isSynchronizationActive()) {
                logger.debug("Registering transaction synchronization for JDO Database");
                // Use same Database for further JDO actions within the transaction
                // thread object will get removed by synchronization at transaction completion.
                jdoManagerHolder = new DatabaseHolder(database);
                TransactionSynchronizationManager.bindResource(jdoManager, jdoManagerHolder);
                TransactionSynchronizationManager.registerSynchronization(
                    new DatabaseSynchronization(jdoManagerHolder, jdoManager));
            }
            return database;
        }
        catch (PersistenceException ex) {
            throw new DataAccessResourceFailureException("Cannot get JDO Database", ex);
        }
    }

    /**
     * Apply the current transaction timeout, if any, to the given Castor JDO OQLQuery object.
     * @param query the Castor JDO OQLQuery object
     * @param jdoManager JDO JDOManager that the OQLQuery was created for
     * @param castorDialect the CastorDialect to use for applying a query timeout (must not be null)
     * @throws PersistenceException If the timeout cannot be allied to the OQL query.
     * @see CastorDialect#applyQueryTimeout
     */
    public static void applyTransactionTimeout(
            OQLQuery query, JDOManager jdoManager, CastorDialect castorDialect) throws PersistenceException {

        Assert.notNull(query, "No OQLQuery object specified");
        DatabaseHolder jdoManagerHolder =
            (DatabaseHolder) TransactionSynchronizationManager.getResource(jdoManager);
        if (jdoManagerHolder != null && jdoManagerHolder.hasTimeout()) {
            castorDialect.applyQueryTimeout(query, jdoManagerHolder.getTimeToLiveInSeconds());
        }
    }

    /**
     * Convert the given PersistenceException to an appropriate exception from the
     * org.springframework.dao hierarchy.
     * <p>The most important cases like object not found or optimistic verification
     * failure are covered here. For more fine-granular conversion, CastorAccessor and
     * CastorTransactionManager support sophisticated translation of exceptions via a
     * CastorDialect.
     * @param ex PeristenceException that occured
     * @return the corresponding DataAccessException instance
     * @see CastorAccessor#convertJdoAccessException
     * @see CastorTransactionManager#convertJdoAccessException
     * @see CastorDialect#translateException
     */
    public static DataAccessException convertJdoAccessException(PersistenceException ex) {
        if (ex instanceof ObjectNotFoundException) {
            throw new CastorObjectRetrievalFailureException((ObjectNotFoundException) ex);
        }
        else if (ex instanceof ObjectDeletedException) {
            throw new CastorObjectRetrievalFailureException((ObjectDeletedException) ex);
        }
        else if (ex instanceof ObjectModifiedException) {
            throw new CastorObjectRetrievalFailureException((ObjectModifiedException) ex);
        }
        else if (ex instanceof ObjectNotPersistentException) {
            throw new CastorObjectRetrievalFailureException((ObjectNotPersistentException) ex);
        }
        else if (ex instanceof LockNotGrantedException) {
            throw new CastorOptimisticLockingFailureException((LockNotGrantedException) ex);
        }
        else if (ex instanceof ClassNotPersistenceCapableException) {
            throw new CastorObjectRetrievalFailureException((ClassNotPersistenceCapableException) ex);
        }
        else if (ex instanceof DuplicateIdentityException) {
            throw new CastorObjectRetrievalFailureException((DuplicateIdentityException) ex);
        }
        else if (ex instanceof TransactionAbortedException) {
            throw new CastorObjectRetrievalFailureException((TransactionAbortedException) ex);
        }
        else if (ex instanceof TransactionNotInProgressException) {
            throw new CastorObjectRetrievalFailureException((TransactionNotInProgressException) ex);
        }
        else if (ex instanceof FatalPersistenceException) {
            throw new CastorResourceFailureException((FatalPersistenceException) ex);
        }
//        else if (ex instanceof JDOUserException) {
//            return new JdoUsageException((JDOUserException) ex);
//        }
//        else if (ex instanceof JDOFatalUserException) {
//            return new JdoUsageException((JDOFatalUserException) ex);
//        }
        else {
            // fallback: assuming internal exception
            return new CastorSystemException(ex);
        }
    }

    /**
     * Close the given Database, created via the given factory, if it isn't 
     * bound to the thread.
     * @param database Database to close
     * @param jdoManager JDOManager that the Database was created with
     */
    public static void closeDatabaseIfNecessary(Database database, JDOManager jdoManager) {
        if (database == null) {
            return;
        }

        DatabaseHolder databaseHolder =
            (DatabaseHolder) TransactionSynchronizationManager.getResource(jdoManager);
        if (databaseHolder != null && database == databaseHolder.getDatabase()) {
            // It's the transactional Database: Don't close it.
            return;
        }

        logger.debug("Closing Castor JDO Database");
        try {
            if (database != null && database.isActive()) {
                database.close();
            }
        }
        catch (PersistenceException ex) {
            logger.error("Could not close Castor JDO Database", ex);
        }
    }


    /**
     * Callback for resource cleanup at the end of a non-Castor transaction
     * (e.g. when participating in a JTA transaction).
     */
    private static class DatabaseSynchronization extends TransactionSynchronizationAdapter {

        private final DatabaseHolder databaseHolder;

        private final JDOManager jdoManager;

        public DatabaseSynchronization(DatabaseHolder databaseHolder, JDOManager jdoManager) {
            this.databaseHolder = databaseHolder;
            this.jdoManager = jdoManager;
        }

        public int getOrder() {
            return DATABASE_SYNCHRONIZATION_ORDER;
        }

        public void suspend() {
            TransactionSynchronizationManager.unbindResource(this.jdoManager);
        }

        public void resume() {
            TransactionSynchronizationManager.bindResource(this.jdoManager, this.databaseHolder);
        }

        public void beforeCompletion() {
            TransactionSynchronizationManager.unbindResource(this.jdoManager);
            closeDatabaseIfNecessary(
                this.databaseHolder.getDatabase(),
                this.jdoManager);
        }
    }

}
