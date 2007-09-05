/*
 * Copyright 2007 Werner Guttmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.castor.spring.orm.support;

import org.castor.spring.orm.CastorTemplate;
import org.castor.spring.orm.JDOManagerUtils;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.support.DaoSupport;

/**
 * Convenient super class for Castor JDO data access objects.
 *
 * <p>Requires a JDOManager to be set, providing a CastorTemplate
 * based on it to subclasses. Can alternatively be initialized directly with a
 * CastorTemplate, to reuse the latter's settings such as the JDOManager,
 * CastorDialect, flush mode, etc.
 *
 * <p>This base class is mainly intended for CastorTemplate usage but can also
 * be used when working with JDOManagerUtils directly, for example
 * in combination with CastorInterceptor-managed Databases. Convenience
 * <code>getDatabase</code> and <code>releaseDatabase</code>
 * methods are provided for that usage style.
 *
 * <p>This class will create its own CastorTemplate if only a JDOManager
 * is passed in. The "allowCreate" flag on that CastorTemplate will be "true" by default.
 * A custom CastorTemplate instance can be used through overriding <code>createCastorTemplate</code>.
 *
 * @author Werner Guttmann
 * @since 1.0-SNAPSHOT
 * @see #setJDOManager
 * @see #setCastorTemplate
 * @see #createCastorTemplate
 * @see #getDatabase
 * @see #releaseDatabase
 * @see org.springframework.orm.cator.CastorTemplate
 * @see org.castor.spring.orm.CastorInterceptor
 */
public abstract class CastorDaoSupport extends DaoSupport {

    private CastorTemplate castorTemplate;

    /**
     * Set the Castor {@link JDOManager} to be used by this DAO.
     * Will automatically create a CastorTemplate for the given {@link JDOManager}.
     * @see #createCastorTemplate
     * @see #setCastorTemplate
     */
    public final void setJDOManager(JDOManager jdoManager) {
      this.castorTemplate = createCastorTemplate(jdoManager);
    }

    /**
     * Create a CastorTemplate for the given {@link JDOManager}.
     * Only invoked if populating the DAO with a JDOManager reference!
     * <p>Can be overridden in subclasses to provide a CastorTemplate instance
     * with different configuration, or a custom CastorTemplate subclass.
     * @param jdoManager the Castor JDOManager to create a CastorTemplate for
     * @return the new CstorTemplate instance
     * @see #setJDOManager
     */
    protected CastorTemplate createCastorTemplate(JDOManager jdoManager) {
        return new CastorTemplate(jdoManager);
    }

    /**
     * Return the Castor {@link JDOManager} used by this DAO.
     */
    public final JDOManager getJDOManager() {
        return (this.castorTemplate != null ? this.castorTemplate.getJDOManager() : null);
    }

    /**
     * Set the CastorTemplate for this DAO explicitly,
     * as an alternative to specifying a {@link JDOManager}.
     * @see #setJDOManager
     */
    public final void setJdoTemplate(CastorTemplate castorTemplate) {
        this.castorTemplate = castorTemplate;
    }

    /**
     * Return the CastorTemplate for this DAO, pre-initialized
     * with the {@link JDOManager} or set explicitly.
     */
    public final CastorTemplate getCastorTemplate() {
      return castorTemplate;
    }

    protected final void checkDaoConfig() {
        if (this.castorTemplate == null) {
            throw new IllegalArgumentException("JDOManager or castorTemplate is required");
        }
    }


    /**
     * Get a Castor Database, either from the current transaction or
     * a new one. The latter is only allowed if the "allowCreate" setting
     * of this bean's CastorTemplate is true.
     * @return the Castor JDO Database 
     * @throws DataAccessResourceFailureException if the Database couldn't be created
     * @throws IllegalStateException if no thread-bound Database found and allowCreate false
     * @see org.castor.spring.orm.JDOManagerUtils#getDatabase
     */
    protected final Database getDatabase() {
        return getDatabase(this.castorTemplate.isAllowCreate());
    }

    /**
     * Get a Castor JDO Database, either from the current transaction or
     * a new one. The latter is only allowed if "allowCreate" is true.
     * @param allowCreate if a non-transactional Database should be created
     * when no transactional Database can be found for the current thread
     * @return the Castor JDO Database 
     * @throws DataAccessResourceFailureException if the Database couldn't be created
     * @throws IllegalStateException if no thread-bound Database found and allowCreate false
     * @see org.castor.spring.orm.JDOManagerUtils#getDatabase
     */
    protected final Database getDatabase(boolean allowCreate)
        throws DataAccessResourceFailureException, IllegalStateException {

        return JDOManagerUtils.getDatabase(getJDOManager(), allowCreate);
    }

    /**
     * Convert the given {@link PersistenceException} to an appropriate exception from the
     * org.springframework.dao hierarchy.
     * <p>Delegates to the translateException method of this DAO's default dialect.
     * @param ex PersistenceException that occured
     * @return the corresponding {@link DataAccessException} instance
     * @see #setCastorTemplate
     * @see org.castor.spring.orm.dialect.CastorDialect#translateException(PersistenceException)
     */
    protected final DataAccessException convertJdoAccessException(PersistenceException ex) {
        return this.castorTemplate.getJdoDialect().translateException(ex);
    }

    /**
     * Close the given Castor JDO {@link org.castor.jdo.conf.Database}, created via this DAO's
     * JDOManager, if it isn't bound to the thread.
     * @param database Database to close
     * @see org.castor.spring.orm.JDOManagerUtils#releaseDatabase
     */
    protected final void releaseDatabase(Database database) {
        JDOManagerUtils.closeDatabaseIfNecessary(database, getJDOManager());
    }

}
