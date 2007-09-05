package org.castor.spring.orm.dialect;

import java.sql.SQLException;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

/**
 * SPI strategy that encapsulates certain functionality that Castor JDO does
 * not offer despite being relevant in the context of O/R mapping, like access to
 * the underlying JDBC Connection and explicit flushing of changes to the database.
 *
 * @author Werner Guttmann
 * @since 07.03.2006
 * @see CastorTransactionManager#setCastorDialect
 * @see CastorAccessor#setCastorDialect
 */
public interface CastorDialect {

    /**
     * Begin a transaction with the given Castor JDO Database, applying the 
     * semantics specified by the given Spring transaction definition (in 
     * particular, an isolation level and a timeout). Invoked by 
     * CastorTransactionManager on transaction begin.
     * <p>An implementation can also apply read-only flag and isolation level to the
     * underlying JDBC Connection before beginning the transaction. In that case,
     * a transaction data object can be returned that holds the previous isolation
     * level (and possibly other data), to be reset in cleanupTransaction.
     * @param database Castor JDO Database to start a local transaction with.
     * @param definition the Spring transaction definition that defines semantics
     * @return an arbitrary object that holds transaction data, if any
     * (to be passed into cleanupTransaction)
     * @throws PersistenceException if thrown by Castor JDO methods
     * @throws SQLException if thrown by JDBC methods
     * @throws TransactionException in case of invalid arguments
     * @see #cleanupTransaction
     * @see org.exolab.castor.persist.TransactionContext#begin
     * @see org.springframework.jdbc.datasource.DataSourceUtils#prepareConnectionForTransaction
     */
    Object beginTransaction(Database database, TransactionDefinition definition)
            throws PersistenceException, SQLException, TransactionException;

    /**
     * Clean up the transaction via the given transaction data.
     * Invoked by CastorTransactionManager on transaction cleanup.
     * <p>An implementation can, for example, reset read-only flag and
     * isolation level of the underlying JDBC Connection.
     * @param transactionData arbitrary object that holds transaction data, if any
     * (as returned by beginTransaction)
     * @see #beginTransaction
     * @see org.springframework.jdbc.datasource.DataSourceUtils#resetConnectionAfterTransaction
     */
    void cleanupTransaction(Object transactionData);

    /**
     * Retrieve the JDBC Connection that the given Castor JDO Database uses underneath,
     * if accessing a relational database. This method will just get invoked if actually
     * needing access to the underlying JDBC Connection, usually within an active Castor JDO
     * transaction (for example, by CastorTransactionManager). The returned handle will
     * be passed into the <code>releaseJdbcConnection</code> method when not needed anymore.
     * <p>Implementations are encouraged to return an unwrapped Connection object, i.e.
     * the Connection as they got it from the connection pool. This makes it easier for
     * application code to get at the underlying native JDBC Connection, like an
     * OracleConnection, which is sometimes necessary for LOB handling etc. We assume
     * that calling code knows how to properly handle the returned Connection object.
     * <p>In a simple case where the returned Connection will be auto-closed with the
     * Castor JDO Database or can be released via the Connection object itself, an
     * implementation can return a SimpleConnectionHandle that just contains the
     * Connection. If some other object is needed in <code>releaseJdbcConnection</code>,
     * an implementation should use a special handle that references that other object.
     * @param database the current Castor JDO Database
     * @param readOnly true if read-only
     * @return a handle for the JDBC Connection, to be passed into
     * <code>releaseJdbcConnection</code>, or null if no JDBC Connection can be retrieved
     * @throws PersistenceException if thrown by Castor JDO methods
     * @throws SQLException if thrown by JDBC methods
     * @see #releaseJdbcConnection
     * @see org.springframework.jdbc.datasource.ConnectionHandle#getConnection
     * @see org.springframework.jdbc.datasource.SimpleConnectionHandle
     * @see CastorTransactionManager#setDataSource
     * @see org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor
     */
    ConnectionHandle getJdbcConnection(Database database, boolean readOnly)
            throws PersistenceException, SQLException;

    /**
     * Release the given JDBC Connection, which has originally been retrieved
     * via <code>getJdbcConnection</code>. This should be invoked in any case,
     * to allow for proper release of the retrieved Connection handle.
     * <p>An implementation might simply do nothing, if the Connection returned
     * by <code>getJdbcConnection</code> will be implicitly closed when the Castor
     * transaction completes or when the Castor JDO Database is closed.
     * @param conHandle the JDBC Connection handle to release
     * @param database the current Castor JDO Database
     * @throws PersistenceException if thrown by Castor JDO methods
     * @throws SQLException if thrown by JDBC methods
     * @see #getJdbcConnection
     */
    void releaseJdbcConnection(ConnectionHandle conHandle, Database database)
            throws PersistenceException, SQLException;

    /**
     * Apply the given timeout to the given Castor JDO query object.
     * <p>Invoked by CastorTemplate with the remaining time of a specified
     * transaction timeout, if any.
     * @param query the Castor JDO query object to apply the timeout to
     * @param timeout the timeout value to apply
     * @throws PersistenceException if thrown by Castor JDO methods
     * @see CastorTemplate#prepareQuery
     */
    void applyQueryTimeout(OQLQuery query, int timeout) throws PersistenceException;

    /**
     * Flush the given Castor JDO Database, i.e. flush all changes (that have been
     * applied to persistent objects) to the underlying database. This method will
     * just get invoked when eager flushing is actually necessary, for example when
     * JDBC access code needs to see changes within the same transaction.
     * @param database the current Castor JDO Database
     * @throws PersistenceException if thrown by Castor JDO methods
     * @see CastorAccessor#setFlushEager
     * @see org.exolab.castor.jdo.Database
     */
    void flush(Database database) throws PersistenceException;

    /**
     * Translate the given JDOException to a corresponding exception from Spring's
     * generic DataAccessException hierarchy. An implementation should apply
     * JDOManagerUtils' standard exception translation if can't do
     * anything more specific.
     * <p>Of particular importance is the correct translation to
     * DataIntegrityViolationException, for example on constraint violation.
     * <p>Can use a SQLExceptionTranslator for translating underlying SQLExceptions
     * in a database-specific fashion.
     * @param ex the JDOException thrown
     * @return the corresponding DataAccessException (must not be null)
     * @see CastorAccessor#convertJdoAccessException
     * @see CastorTransactionManager#convertJdoAccessException
     * @see JDOManagerUtils#convertJdoAccessException
     * @see org.springframework.dao.DataIntegrityViolationException
     * @see org.springframework.jdbc.support.SQLExceptionTranslator
     */
    DataAccessException translateException(PersistenceException ex);

}
