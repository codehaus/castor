package org.castor.spring.orm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.spring.orm.dialect.CastorDialect;
import org.castor.spring.orm.dialect.DefaultCastorDialect;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.PersistenceException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;

/**
 * Base class for CastorTemplate and CastorInterceptor, defining common
 * properties like JDOManager and <really>flushing behavior</really>.
 *
 * <p>Note: With Castor JDO, modifications to persistent objects are just possible
 * within a transaction.
 * 
 * <p>Not intended to be used directly. See CastorTemplate and CastorInterceptor.
 *
 * @author Werner Guttmann
 * @since 20.05.2005
 * @see CastorTemplate
 * @see CastorInterceptor
 */
public abstract class CastorAccessor implements InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private JDOManager jdoManager;

    private CastorDialect castorDialect;

    /**
     * Set the Castor JDOManager that should be used to create Databases.
     * @param jdoManager Castor JDOManager to be used to create Databases
     */
    public void setJDOManager(JDOManager jdoManager) {
        this.jdoManager = jdoManager;
    }

    /**
     * Return the Castor JDOManager used to create Databases.
     * @return Castor JDOManager used to create Databases
     */
    public JDOManager getJDOManager() {
        return this.jdoManager;
    }

    /**
     * Set the JDO dialect to use for this accessor.
     * <p>The dialect object can be used to retrieve the underlying JDBC connection.
     * @param castorDialect Castor JDO dialect to use for this accessor
     */
    public void setCastorDialect(CastorDialect castorDialect) {
        this.castorDialect = castorDialect;
    }

    /**
     * Return the Castor JDO dialect to use for this accessor.
     * <p>Creates a default one for the specified JDOManager none set.
     * @return Castor JDO dialect to be used.
     */
    public CastorDialect getJdoDialect() {
        if (this.castorDialect == null) {
            this.castorDialect = new DefaultCastorDialect(getJDOManager());
        }
        return this.castorDialect;
    }

    /**
     * Eagerly initialize the JDO dialect, creating a default one
     * for the specified JDOManagerif none set.
     */
    public void afterPropertiesSet() {
        if (getJDOManager() == null) {
            throw new IllegalArgumentException("JDOManager is required");
        }
        getJdoDialect();
    }


    /**
     * Convert the given PeristenceException to an appropriate exception from the
     * org.springframework.dao hierarchy. Delegates to the CastorDialect if set, falls
     * back to JDOManagerUtils' standard exception translation else.
     * <p>May be overridden in subclasses.
     * @param ex PeristenceException that occured
     * @return the corresponding DataAccessException instance
     * @see CastorDialect#translateException
     * @see JDOManagerUtils#convertJdoAccessException
     */
    public DataAccessException convertJdoAccessException(PersistenceException ex) {
        return getJdoDialect().translateException(ex);
    }

}
