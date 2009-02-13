package org.castor.spring.orm;

import org.exolab.castor.jdo.Database;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This interceptor binds a new JDO Database to the thread before a method, closing and 
 * removing it afterwards in case of any method outcome.
 * If there already was a pre-bound Database (e.g. from CastorTransactionManager,
 * or from a surrounding Castor JDO-intercepted method), the interceptor simply takes part in it.
 *
 * <p>Application code must retrieve a JDO Database via JDOManagerUtils' getDatabase method, to 
 * be able to detect a thread-bound Database. It is preferable to use getDatabase
 * with allowCreate=false, as the code relies on the interceptor to provide proper
 * Database handling. Typically the code will look as follows:
 *
 * <pre>
 * public void doJdoAction() {
 *   Database database = JDOManagerUtils.getDatabase(this.jdoManager, false);
 *   try {
 *     ...
 *   }
 *   catch (PersistenceException ex) {
 *     throw JDOManagerUtils.convertJdoAccessException(ex);
 *   }
 * }</pre>
 *
 * Note that the application must care about handling PersistenceExceptions itself,
 * preferably via delegating to JDOManagerUtils' convertJdoAccessException
 * that converts them to ones that are compatible with the org.springframework.dao exception
 * hierarchy (like CastorTemplate does). 
 * 
// TODO [WG] * As PersistenceExceptions are unchecked, they can simply
 * get thrown too, sacrificing generic DAO abstraction in terms of exceptions though.
 *
 * <p>This interceptor could convert unchecked PersistenceExceptions to unchecked dao ones
 * on-the-fly. The intercepted method wouldn't have to throw any special checked
 * exceptions to be able to achieve this. Nevertheless, such a mechanism would
 * effectively break the contract of the intercepted method (runtime exceptions
 * can be considered part of the contract too), therefore it isn't supported.
 *
 * <p>This class can be considered a declarative alternative to CastorTemplate's
 * callback approach. The advantages are:
 * <ul>
 * <li>no anonymous classes necessary for callback implementations;
 * <li>the possibility to throw any application exceptions from within data access code.
 * </ul>
 *
 * <p>The drawbacks are:
 * <ul>
 * <li>the dependency on interceptor configuration;
 * <li>the delegating try/catch blocks.
 * </ul>
 *
 * @author Juergen Hoeller, Werner Guttmann
 * @since 13.06.2003
 */
public class CastorInterceptor extends CastorAccessor implements MethodInterceptor {

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		boolean existingTransaction = false;
		Database database = JDOManagerUtils.getDatabase(getJDOManager(), true);
		if (TransactionSynchronizationManager.hasResource(getJDOManager())) {
			this.logger.debug("Found thread-bound Database for Castor JDO interceptor");
			existingTransaction = true;
		}
		else {
			this.logger.debug("Using new Database for Castor JDO interceptor");
			TransactionSynchronizationManager.bindResource(getJDOManager(), new DatabaseHolder(database));
		}
		try {
			return methodInvocation.proceed();
		}
		finally {
			if (existingTransaction) {
				this.logger.debug("Not closing pre-bound JDO Database after interceptor");
			}
			else {
				TransactionSynchronizationManager.unbindResource(getJDOManager());
				JDOManagerUtils.closeDatabaseIfNecessary(database, getJDOManager());
			}
		}
	}

}
