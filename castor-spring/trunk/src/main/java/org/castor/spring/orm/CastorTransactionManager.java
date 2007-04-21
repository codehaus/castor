package org.castor.spring.orm;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.spring.orm.dialect.CastorDialect;
import org.castor.spring.orm.dialect.DefaultCastorDialect;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.engine.AbstractDatabaseImpl;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.exolab.castor.mapping.MappingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * PlatformTransactionManager implementation for a single Castor JDO JDOManager.
 * Binds a JDO Database from the specified factory to the thread, potentially
 * allowing for one thread Database per factory. JDOManagerUtils
 * and CastorTemplate are aware of thread-bound Databases and participate in such
 * transactions automatically. Using either is required for Castor JDO access code supporting
 * this transaction management mechanism.
 *
 * <p>This implementation is appropriate for applications that solely use Castor JDO for
 * transactional data access. JTA (usually through JtaTransactionManager) is necessary for
 * accessing multiple transactional resources. Note that you need to configure Castor JDO accordingly 
 * to make it participate in JTA transactions.
 *
 * <p>With a CastorDialect specified, this implementation also supports direct DataSource
 * access within a transaction (i.e. plain JDBC code working with the same DataSource).
 * This allows for mixing services that access Castor JDO (including transactional caching)
 * and services that use plain JDBC (without being aware of Castor JDO)!
 * Application code needs to stick to the same simple Connection lookup pattern as
 * with DataSourceTransactionManager (i.e. <code>DataSourceUtils.getConnection</code>).
 *
 * <p>Note that to be able to register a DataSource's Connection for plain JDBC code,
 * this instance needs to be aware of the DataSource (see "dataSource" property).
 * The given DataSource should obviously match the one used by the given
 * Castor JDOManager. This transaction manager will auto-detect the DataSource
 * that acts as "connectionFactory" of the JDOManager, so you usually
 * don't need to explicitly specify the "dataSource" property.
 *
 * <p>On JDBC 3.0, this transaction manager supports nested transactions via JDBC
 * 3.0 Savepoints. The "nestedTransactionAllowed" flag defaults to false, though,
 * as nested transactions will just apply to the JDBC Connection, not to the JDO 
 * Database and its cached objects. You can manually set the flag to true
 * if you want to use nested transactions for JDBC access code that participates
 * in JDO transactions (provided that your JDBC driver supports Savepoints).
 *
 * @author Werner Guttmann
 * @since 19.05.2005
 * @see #setJDOManager
 * @see #setDataSource
 * @see org.exolab.castor.jdo.JDOManager#getConnectionFactory
 * @see LocalCastorFactoryBean
 * @see JDOManagerUtils#getDatabase(JDOManager,boolean)
 * @see JDOManagerUtils#closeDatabaseIfNecessary
 * @see CastorTemplate#execute(CastorCallback)
 * @see org.springframework.jdbc.datasource.DataSourceUtils#getConnection
 * @see org.springframework.jdbc.datasource.DataSourceUtils#closeConnectionIfNecessary
 * @see org.springframework.jdbc.core.JdbcTemplate
 * @see org.springframework.transaction.jta.JtaTransactionManager
 * @see org.castor.spring.orm.CastorTransactionManager
 */
public class CastorTransactionManager 
    extends AbstractPlatformTransactionManager 
    implements InitializingBean {

	private JDOManager jdoManager;

	private DataSource dataSource;

	private boolean autodetectDataSource = true;

	private CastorDialect castorDialect = new DefaultCastorDialect();


	/**
	 * Create a new CastorTransactionManager instance.
	 * A JDOManager has to be set to be able to use it.
	 * @see #setJDOManager
	 */
	public CastorTransactionManager() {
		// empty method body
	}

	/**
	 * Create a new CastorTransactionManager instance.
	 * @param jdoManager JDOManager to manage transactions for
	 */
	public CastorTransactionManager(JDOManager jdoManager) {
		this.jdoManager = jdoManager;
		afterPropertiesSet();
	}

	/**
	 * Set the JDOManager that this instance should manage transactions for.
	 * @param jdoManager JDOManager that this instance should manage transactions for
	 */
	public void setJDOManager(JDOManager jdoManager) {
		this.jdoManager = jdoManager;
	}

	/**
	 * Return the JDOManager that this instance should manage transactions for.
	 * @return JDOManager that this instance should manage transactions for
	 */
	public JDOManager getJdoManager() {
		return this.jdoManager;
	}

	/**
	 * Set the JDBC DataSource that this instance should manage transactions for.
	 * The DataSource should match the one used by the Castor JDO JDOManager:
	 * for example, you could specify the same JNDI DataSource for both.
	 * <p>If the JDOManager uses a DataSource as connection factory,
	 * the DataSource will be auto-detected: You can still explictly specify the
	 * DataSource, but you don't need to in this case.
	 * <p>A transactional JDBC Connection for this DataSource will be provided to
	 * application code accessing this DataSource directly via DataSourceUtils
	 * or JdbcTemplate. The Connection will be taken from the Castor JDO Database.
	 * <p>The DataSource specified here should be the target DataSource to manage
	 * transactions for, not a TransactionAwareDataSourceProxy. Only data access
	 * code may work with TransactionAwareDataSourceProxy, while the transaction
	 * manager needs to work on the underlying target DataSource. If there's
	 * nevertheless a TransactionAwareDataSourceProxy passed in, it will be
	 * unwrapped to extract its target DataSource.
	 * @param dataSource JDBC DataSource instance.
	 * @see #setAutodetectDataSource
	 * @see #setCastorDialect
	 * @see org.exolab.castor.jdo.JDOManager#getConnectionFactory
	 * @see org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
	 */
	public void setDataSource(DataSource dataSource) {
		if (dataSource instanceof TransactionAwareDataSourceProxy) {
			// If we got a TransactionAwareDataSourceProxy, we need to perform transactions
			// for its underlying target DataSource, else data access code won't see
			// properly exposed transactions (i.e. transactions for the target DataSource).
			this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
		}
		else {
			this.dataSource = dataSource;
		}
	}

	/**
	 * Return the JDBC DataSource that this instance manages transactions for.
	 * @return JDBC DataSource that this instance manages transactions for
	 */
	public DataSource getDataSource() {
		return this.dataSource;
	}

	/**
	 * Set whether to autodetect a JDBC DataSource used by the Castor JDOManager,
	 * as returned by the <code>getConnectionFactory()</code> method. Default is true.
	 * <p>Can be turned off to deliberately ignore an available DataSource,
	 * to not expose Castor JDO transactions as JDBC transactions for that DataSource.
	 * @param autodetectDataSource If true, autodetect a JDBC DataSource 
	 * @see #setDataSource
	 * @see org.exolab.castor.jdo.JDOManager#getConnectionFactory
	 */
	public void setAutodetectDataSource(boolean autodetectDataSource) {
		this.autodetectDataSource = autodetectDataSource;
	}

	/**
	 * Set the Castor JDO dialect to use for this transaction manager.
	 * <p>The dialect object can be used to retrieve the underlying JDBC connection
	 * and thus allows for exposing Castor transactions as JDBC transactions.
	 * @param castorDialect Castor dialect to use.
	 * @see CastorDialect#getJdbcConnection
	 */
	public void setCastorDialect(CastorDialect castorDialect) {
		this.castorDialect = castorDialect;
	}

	/**
	 * Return the Castor dialect to use for this transaction manager.
	 * Creates a default one for the specified JDOManager if none set.
	 * @return a Castor dialect to use for this transaction manager
	 */
	public CastorDialect getCastorDialect() {
		if (this.castorDialect == null) {
			this.castorDialect = new DefaultCastorDialect(getJdoManager());
		}
		return this.castorDialect;
	}

	/**
	 * Eagerly initialize the Castor JDO dialect, creating a default one
	 * for the specified JDOManager if none set.
	 * Auto-detect the JDOManager's DataSource, if any.
	 */
	public void afterPropertiesSet() {
		if (getJdoManager() == null) {
			throw new IllegalArgumentException("jdoManager is required");
		}
		getCastorDialect();

		// Check for DataSource as connection factory.
        // TODO [WG]: we don't externalize a connection factory to the user.
		if (this.autodetectDataSource && getDataSource() == null) {
			Object pmfcf = null;
            try {
                pmfcf = getJdoManager().getConnectionFactory();
            } catch (MappingException e) {
                this.logger.error("Cannot obtain DataSource from JDOManager.", e);
            }
            
			if (pmfcf != null && pmfcf instanceof DataSource) {
				// Use the JDOManager's DataSource for exposing transactions to JDBC code.
				this.dataSource = (DataSource) pmfcf;
				if (this.logger.isInfoEnabled()) {
					this.logger.info("Using DataSource [" + this.dataSource +
							"] of JDOManager for CastorTransactionManager");
				}
			}
		}
	}


	protected Object doGetTransaction() {
		CastorTransactionObject txObject = new CastorTransactionObject();
		txObject.setSavepointAllowed(isNestedTransactionAllowed());

		if (TransactionSynchronizationManager.hasResource(getJdoManager())) {
			DatabaseHolder databaseHolder = (DatabaseHolder)
					TransactionSynchronizationManager.getResource(getJdoManager());
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Found thread-bound Database [" +
						databaseHolder.getDatabase() + "] for Castor transaction");
			}
			txObject.setDatabaseHolder(databaseHolder, false);
			if (getDataSource() != null) {
				ConnectionHolder conHolder = (ConnectionHolder)
						TransactionSynchronizationManager.getResource(getDataSource());
				txObject.setConnectionHolder(conHolder);
			}
		}

		return txObject;
	}

	protected boolean isExistingTransaction(Object transaction) {
		return ((CastorTransactionObject) transaction).hasTransaction();
	}

	protected void doBegin(Object transaction, TransactionDefinition definition) {
		if (getDataSource() != null && TransactionSynchronizationManager.hasResource(getDataSource())) {
			throw new IllegalTransactionStateException(
					"Pre-bound JDBC connection found - CastorTransactionManager does not support " +
					"running within DataSourceTransactionManager if told to manage the DataSource itself. " +
					"It is recommended to use a single CastorTransactionManager for all transactions " +
					"on a single DataSource, no matter whether Castor or JDBC access.");
		}

		// TODO [WG]: can we cope with this
		if (definition.isReadOnly()) {
			this.logger.info("CastorTransactionManager does not support read-only transactions: ignoring 'readOnly' hint");
		}

		CastorTransactionObject txObject = (CastorTransactionObject) transaction;
		if (txObject.getDatabaseHolder() == null) {
			Database database =
					JDOManagerUtils.getDatabase(getJdoManager(), true, false);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Opened new Database [" + database + "] for Castor transaction");
			}
			txObject.setDatabaseHolder(new DatabaseHolder(database), true);
		}

		txObject.getDatabaseHolder().setSynchronizedWithTransaction(true);
		Database database = txObject.getDatabaseHolder().getDatabase();

		try {
			// Delegate to CastorDialect for actual transaction begin.
			Object transactionData = getCastorDialect().beginTransaction(database, definition);
			txObject.setTransactionData(transactionData);

			// Register transaction timeout.
			if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
				txObject.getDatabaseHolder().setTimeoutInSeconds(definition.getTimeout());
			}

			// Register the Castor JDO Database's JDBC Connection for the DataSource, if set.
			if (getDataSource() != null) {
				ConnectionHandle conHandle = getCastorDialect().getJdbcConnection(database, definition.isReadOnly());
				if (conHandle != null) {
					ConnectionHolder conHolder = new ConnectionHolder(conHandle);
					if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
						conHolder.setTimeoutInSeconds(definition.getTimeout());
					}
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Exposing Castor transaction as JDBC transaction [" + conHolder.getConnection() + "]");
					}
					TransactionSynchronizationManager.bindResource(getDataSource(), conHolder);
					txObject.setConnectionHolder(conHolder);
				}
				else {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Not exposing Castor transaction [" + database + "] as JDBC transaction because CastorDialect [" +
								getCastorDialect() + "] does not support JDBC connection retrieval");
					}
				}
			}

			// Bind the Database holder to the thread.
			if (txObject.isNewDatabaseHolder()) {
				TransactionSynchronizationManager.bindResource(
						getJdoManager(), txObject.getDatabaseHolder());
			}
		}

		catch (TransactionException ex) {
			JDOManagerUtils.closeDatabaseIfNecessary(database, getJdoManager());
			throw ex;
		}
		catch (Exception ex) {
			JDOManagerUtils.closeDatabaseIfNecessary(database, getJdoManager());
			throw new CannotCreateTransactionException("Could not create Castor transaction", ex);
		}
	}

	protected Object doSuspend(Object transaction) {
		CastorTransactionObject txObject = (CastorTransactionObject) transaction;
		txObject.setDatabaseHolder(null, false);
		DatabaseHolder databaseHolder = (DatabaseHolder)
				TransactionSynchronizationManager.unbindResource(getJdoManager());
		ConnectionHolder connectionHolder = null;
		if (getDataSource() != null) {
			connectionHolder = (ConnectionHolder) TransactionSynchronizationManager.unbindResource(getDataSource());
		}
		return new SuspendedResourcesHolder(databaseHolder, connectionHolder);
	}

	protected void doResume(Object transaction, Object suspendedResources) {
		SuspendedResourcesHolder resourcesHolder = (SuspendedResourcesHolder) suspendedResources;
		TransactionSynchronizationManager.bindResource(
				getJdoManager(), resourcesHolder.getDatabaseHolder());
		if (getDataSource() != null) {
			TransactionSynchronizationManager.bindResource(getDataSource(), resourcesHolder.getConnectionHolder());
		}
	}

	protected void doCommit(DefaultTransactionStatus status) {
		CastorTransactionObject txObject = (CastorTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			this.logger.debug("Committing Castor transaction on Database [" +
					txObject.getDatabaseHolder().getDatabase() + "]");
		}
		try {
			txObject.getDatabaseHolder().getDatabase().commit();
		}
		catch (PersistenceException ex) {
			// assumably failed to flush changes to database
			throw convertJdoAccessException(ex);
		}
	}

	protected void doRollback(DefaultTransactionStatus status) {
		CastorTransactionObject txObject = (CastorTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			this.logger.debug("Rolling back Castor transaction on Database [" +
					txObject.getDatabaseHolder().getDatabase() + "]");
		}
		try {
			((AbstractDatabaseImpl) txObject.getDatabaseHolder().getDatabase()).getCurrentTransaction().rollback();
		}
		catch (PersistenceException ex) {
			throw new TransactionSystemException("Could not roll back Castor transaction", ex);
		}
	}

	protected void doSetRollbackOnly(DefaultTransactionStatus status) {
		CastorTransactionObject txObject = (CastorTransactionObject) status.getTransaction();
		this.logger.debug("Setting Castor transaction rollback-only");
		txObject.setRollbackOnly();
	}

	protected void doCleanupAfterCompletion(Object transaction) {
		CastorTransactionObject txObject = (CastorTransactionObject) transaction;

		// Remove the Database holder from the thread.
		if (txObject.isNewDatabaseHolder()) {
			TransactionSynchronizationManager.unbindResource(getJdoManager());
		}
		txObject.getDatabaseHolder().clear();

		// Remove the JDBC connection holder from the thread, if set.
		if (txObject.getConnectionHolder() != null) {
			TransactionSynchronizationManager.unbindResource(getDataSource());
			try {
				getCastorDialect().releaseJdbcConnection(txObject.getConnectionHolder().getConnectionHandle(),
						txObject.getDatabaseHolder().getDatabase());
			}
			catch (Exception ex) {
				// Just log it, to keep a transaction-related exception.
				this.logger.error("Could not close JDBC connection after transaction", ex);
			}
		}

		getCastorDialect().cleanupTransaction(txObject.getTransactionData());

		// Remove the database holder from the thread.
		if (txObject.isNewDatabaseHolder()) {
			Database database = txObject.getDatabaseHolder().getDatabase();
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Closing Castor Database [" + database + "] after transaction");
			}
			JDOManagerUtils.closeDatabaseIfNecessary(database, getJdoManager());
		}
		else {
			this.logger.debug("Not closing pre-bound Castor JDO Database after transaction");
		}
	}

	/**
	 * Convert the given JDOException to an appropriate exception from the
	 * org.springframework.dao hierarchy. Delegates to the JdoDialect if set, falls
	 * back to JDOManagerUtils' standard exception translation else.
	 * May be overridden in subclasses.
	 * @param ex PersistenceException that occured
	 * @return the corresponding DataAccessException instance
	 * @see CastorDialect#translateException
	 * @see JDOManagerUtils#convertJdoAccessException
	 */
	protected DataAccessException convertJdoAccessException(PersistenceException ex) {
		return getCastorDialect().translateException(ex);
	}


	/**
	 * Castor JDO transaction object, representing a DatabaseHolder.
	 * Used as transaction object by CastorTransactionManager.
	 *
	 * <p>Derives from JdbcTransactionObjectSupport to inherit the capability
	 * to manage JDBC 3.0 Savepoints for underlying JDBC Connections.
	 *
	 * @see DatabaseHolder
	 */
	private static class CastorTransactionObject extends JdbcTransactionObjectSupport {
        
        private static final Log logger = LogFactory.getLog(CastorTransactionObject.class);

		private DatabaseHolder databaseHolder;

		private boolean newDatabaseHolder;

		private Object transactionData;

		public void setDatabaseHolder(
				DatabaseHolder databaseHolder, boolean newDatabaseHolder) {
			this.databaseHolder = databaseHolder;
			this.newDatabaseHolder = newDatabaseHolder;
		}

		public DatabaseHolder getDatabaseHolder() {
			return this.databaseHolder;
		}

		public boolean isNewDatabaseHolder() {
			return this.newDatabaseHolder;
		}

		public boolean hasTransaction() {
            boolean hasTransaction = false;
			try {
                hasTransaction = 
                    (this.databaseHolder != null &&
                     this.databaseHolder.getDatabase() != null &&
                	 ((AbstractDatabaseImpl) this.databaseHolder.getDatabase()).getCurrentTransaction().isOpen());
            } catch (TransactionNotInProgressException e) {
                logger.debug("Cannot open current exception.", e);
            }
            
            return hasTransaction;
		}

		public void setTransactionData(Object transactionData) {
			this.transactionData = transactionData;
		}

		public Object getTransactionData() {
			return this.transactionData;
		}

		public void setRollbackOnly() {
			getDatabaseHolder().setRollbackOnly();
			if (getConnectionHolder() != null) {
				getConnectionHolder().setRollbackOnly();
			}
		}

		public boolean isRollbackOnly() {
			return getDatabaseHolder().isRollbackOnly();
		}
	}


	/**
	 * Holder for suspended resources.
	 * Used internally by doSuspend and doResume.
	 */
	private static class SuspendedResourcesHolder {

		private final DatabaseHolder databaseHolder;

		private final ConnectionHolder connectionHolder;

		private SuspendedResourcesHolder(DatabaseHolder databaseHolder, ConnectionHolder conHolder) {
			this.databaseHolder = databaseHolder;
			this.connectionHolder = conHolder;
		}

		private DatabaseHolder getDatabaseHolder() {
			return this.databaseHolder;
		}

		private ConnectionHolder getConnectionHolder() {
			return this.connectionHolder;
		}
	}

}
