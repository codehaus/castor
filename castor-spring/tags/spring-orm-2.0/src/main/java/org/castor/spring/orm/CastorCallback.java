package org.castor.spring.orm;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;


/**
 * Callback interface for Castor JDO code. To be used with CastorTemplate's execute
 * method, assumably often as anonymous classes within a method implementation.
 * The typical implementation will call Database CRUD to perform
 * some operations on persistent objects.
 *
 * @author Werner Guttmann
 * @since 20.05.2005
 * @see CastorTemplate
 * @see org.castor.spring.orm.CastorCallback
 */
public interface CastorCallback {


    /**
     * Gets called by <code>CastorTemplate.execute</code> with an active Database.
     * Does not need to care about activating or closing the Database,
     * or handling transactions.
     *
     * <p>Note that Castor JDO callback code will not flush any modifications to the
     * database if not executed within a transaction. Thus, you need to make
     * sure that CastorTransactionManager has initiated a Castor JDO transaction when
     * the callback gets called, at least if you want to write to the database.
     *
     * <p>Allows for returning a result object created within the callback,
     * i.e. a domain object or a collection of domain objects.
     * A thrown RuntimeException is treated as application exception,
     * it gets propagated to the caller of the template.
     *
     * @param database active Database
     * @return a result object, or null if none
     * @throws PersistenceException in case of Castor JDO errors
     * @see CastorTemplate#execute(CastorCallback)
     * @see CastorTransactionManager
     */
    Object doInCastor(Database database) throws PersistenceException;

}
