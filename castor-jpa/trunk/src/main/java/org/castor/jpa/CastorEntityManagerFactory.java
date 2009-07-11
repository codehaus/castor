package org.castor.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.PersistenceException;

/**
 * Implementation using Castor JDO internally.
 * 
 * @author lukas.lang
 * 
 */
public class CastorEntityManagerFactory implements EntityManagerFactory {

	private JDOManager jdoManager;
	private final Log log = LogFactory.getLog(CastorEntityManagerFactory.class);

	/**
	 * Indicates whether this {@link EntityManagerFactory} is open.
	 */
	boolean isOpen = true;

	/**
	 * Constructor taking a {@link JDOManager}.
	 * 
	 * @param jdoManager
	 *            the manager to use.
	 */
	public CastorEntityManagerFactory(JDOManager jdoManager) {
		this.jdoManager = jdoManager;
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#createEntityManager()
	 */
	@Override
	public EntityManager createEntityManager() {
		assureOpen();

		EntityManager entityManager;
		try {
			entityManager = new CastorEntityManager(jdoManager.getDatabase());
		} catch (PersistenceException e) {
			// Log error and bail out.
			if (log.isErrorEnabled()) {
				log
						.error(
								"Could not obtain Castor JDO Database instance from JDOManager.",
								e);
			}
			throw new javax.persistence.PersistenceException(
					"Could not obtain Castor JDO Database instance from JDOManager.");
		}
		return entityManager;
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#createEntityManager(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EntityManager createEntityManager(final Map map) {
		assureOpen();
		throw new UnsupportedOperationException("Not yet supported.");
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#close()
	 */
	@Override
	public void close() {
		if (!isOpen) {
			throw new IllegalStateException(
					"EntityManagerFactory is closed already.");
		}
		isOpen = false;
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Assure that the {@link EntityManagerFactory} is still opened.
	 * 
	 * @throws IllegalStateException
	 *             if {@link EntityManagerFactory} is closed.
	 */
	private void assureOpen() {
		if (!isOpen) {
			throw new IllegalStateException("EntityManagerFactory is closed.");
		}
	}
}
