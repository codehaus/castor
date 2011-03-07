package org.castor.spring.orm;

import org.exolab.castor.jdo.PersistenceException;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * Castor JDO-specific subclass of ObjectRetrievalFailureException.
 * Converts Castor JDO's ObjectNotFoundException.
 * @author Werner Guttmann
 * @since 1.1
 * @see JDOManagerUtils#convertJdoAccessException
 */
public class CastorObjectRetrievalFailureException extends ObjectRetrievalFailureException {

	/**
     * <code>serialVersionUID</code> generated serial version UID
     */
    private static final long serialVersionUID = 3546366123609174840L;

    public CastorObjectRetrievalFailureException(PersistenceException ex) {
		super(ex.getMessage(), ex);
	}

}
