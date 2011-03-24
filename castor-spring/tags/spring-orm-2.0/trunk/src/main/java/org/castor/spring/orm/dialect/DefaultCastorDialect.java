package org.castor.spring.orm.dialect;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.spring.orm.JDOManagerUtils;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.SimpleConnectionHandle;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

/**
 * Default implementation of the CastorDialect interface.
 * Used by CastorAccessor and CastorTransactionManager as default.
 *
 * <p>Simply begins a standard Castor JDO transaction in <code>beginTransaction</code>.
 * Returns the underlying JDBC connection as used by Castor.
 * Ignores a given query timeout in <code>applyQueryTimeout</code>.
 * Throws a JDOUnsupportedOptionException on <code>flush</code>.
 * Delegates to JDOManagerUtils for exception translation.
 *
 * @author Juergen Hoeller, Werner Guttmann
 * @since 1.2
 * @see org.castor.spring.orm.CastorAccessor#setCastorDialect
 * @see org.castor.spring.orm.CastorTransactionManager#setCastorDialect
 */
public class DefaultCastorDialect implements CastorDialect {

    protected Log logger = LogFactory.getLog(getClass());

    /**
     * Castor JDO JDOManager instance used.
     */
    private JDOManager jdoManager;

    /**
     * SQLExceptionTranslator instance in use.
     */
    private SQLExceptionTranslator jdbcExceptionTranslator;

    /**
     * Create a new DefaultCastorDialect.
     */
    public DefaultCastorDialect() {
    	// no code to execute
    }

    /**
     * Create a new DefaultCastorDialect.
     * @param jdoManager the Castor JDOManager used to initialize the default JDBC exception translator
     */
    public DefaultCastorDialect(JDOManager jdoManager) {
        setJDOManager(jdoManager);
    }

    /**
     * Set the Castor JDOManager used to initialize the default JDBC exception translator if none specified.
     * @param jdoManager Castor JDOManager to use.
     * @see #setJdbcExceptionTranslator
     */
    public void setJDOManager(JDOManager jdoManager) {
        this.jdoManager = jdoManager;
    }

    /**
     * Return the Castor JDOManager that should be used to create Castor JDO Databases.
     * @return Castor JDOManager used to create Database instances.
     * @see org.exolab.castor.jdo.JDOManager
     * @see org.exolab.castor.jdo.Database
     */
    public JDOManager getJDOManager() {
        return this.jdoManager;
    }

    /**
     * Set the JDBC exception translator for this dialect.
     * Applied to SQLExceptions that are the cause of Castor JDO PersistenceExceptions.
     * <p>The default exception translator is either a SQLErrorCodeSQLExceptionTranslator
     * if a DataSource is available, or a SQLStateSQLExceptionTranslator else.
     * @param jdbcExceptionTranslator exception translator
     * @see java.sql.SQLException
     * @see org.exolab.castor.jdo.PersistenceException
     * @see JDOManagerUtils#newJdbcExceptionTranslator
     * @see org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
     * @see org.springframework.jdbc.support.SQLStateSQLExceptionTranslator
     */
    public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
        this.jdbcExceptionTranslator = jdbcExceptionTranslator;
    }

    /**
     * Return the JDBC exception translator for this instance.
     * <p>Creates a default SQLErrorCodeSQLExceptionTranslator or SQLStateSQLExceptionTranslator
     * for the specified JDOManager, if no exception translator explicitly specified.
     * @return JDBC exception translator for this instance
     */
    public SQLExceptionTranslator getJdbcExceptionTranslator() {
        if (this.jdbcExceptionTranslator == null) {
            this.jdbcExceptionTranslator =
                    JDOManagerUtils.newJdbcExceptionTranslator(this.jdoManager);
        }
        return this.jdbcExceptionTranslator;
    }


    /**
     * This implementation invokes the standard Castor JDO <code>Database.begin()</code>
     * method. Throws an InvalidIsolationLevelException if a non-default isolation
     * level is set.
     * @param database The Castor Database to start a transaction with.
     * @param definition A Transaction definition
     * @return Nothing in this case.
     * @throws PersistenceException If the transaction cannot be started.
     * @throws TransactionException If the transaction cannot be started.
     * @see Database#begin
     * @see org.springframework.transaction.InvalidIsolationLevelException
     */
    public Object beginTransaction(Database database, TransactionDefinition definition)
            throws PersistenceException, TransactionException {

        if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
            throw new InvalidIsolationLevelException(
                    "Standard Castor JDO does not support custom isolation levels.");
        }
        database.begin();
        return null;
    }

    /**
     * This implementation does nothing, as the default beginTransaction implementation
     * does not require any cleanup.
     * @param transactionData 
     * @see #beginTransaction
     */
    public void cleanupTransaction(Object transactionData) {
    	// empty method body
    }

    /**
     * This implementation returns null, to indicate that JDBC Connection
     * retrieval is not supported.
     * @param database JDO Database instance.
     * @param readOnly True if data access is read-only.
     * @return A ConnectionHandle instance
     */
    public ConnectionHandle getJdbcConnection(Database database, boolean readOnly) 
    throws PersistenceException 
    {
        return new SimpleConnectionHandle (database.getJdbcConnection());
    }

    /**
     * This implementation does nothing, assuming that the Connection
     * will implicitly be closed with the JDOManager.
     * <p>If Castor returns a Connection handle that it expects the application to 
     * close, the dialect needs to invoke <code>Connection.close</code> here.
     * @param conHandle ConnectionHandle instance
     * @param database Castor JDO Database instance
     * @see java.sql.Connection#close
     */
    public void releaseJdbcConnection(ConnectionHandle conHandle, Database database) {
        // empty implementation
    }

    /**
     * This implementation logs a warning that it cannot apply a query timeout.
     * @param query An OQLQuery to which a timeout should be applied.
     * @param remainingTimeInSeconds Remaining timeout duration in seconds.
     */
    public void applyQueryTimeout(OQLQuery query, int remainingTimeInSeconds) { 
        this.logger.info("DefaultCastorDialect does not support query timeouts: ignoring remaining transaction time");
    }

    /**
     * This implementation throws a general Castor JDO PersistenceException to signal that eager flushing is not supported.
     * @param database The Castor Database instance for which to apply a flush.
     * @throws PersistenceException to signal that eager flushing is not supported. 
    */
    public void flush(Database database) throws PersistenceException {
        throw new PersistenceException ("Eager flushing not supported by Castor JDO Databases.");
    }

    /**
     * This implementation delegates to JDOManagerUtils.
     * @param ex Exception to translate
     * @return A DataAccessException instance. 
     * @see JDOManagerUtils#convertJdoAccessException
     */
    public DataAccessException translateException(PersistenceException ex) {
        if (ex.getCause() instanceof SQLException) {
            return getJdbcExceptionTranslator().translate("Castor JDO operation", null, (SQLException) ex.getCause());
        }
        
        return JDOManagerUtils.convertJdoAccessException(ex);
    }

}
