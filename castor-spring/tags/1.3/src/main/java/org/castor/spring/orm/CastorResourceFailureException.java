package org.castor.spring.orm;

import org.exolab.castor.jdo.FatalPersistenceException;
import org.springframework.dao.DataAccessResourceFailureException;

/**
 * Castor JDO-specific subclass of DataAccessResourceFailureException.
 * Converts Castor JDO's FatalPersistenceException.
 * @author Werner Guttmann
 * @since JDOManagerUtils#convertCastorAccessException
 */
public class CastorResourceFailureException extends DataAccessResourceFailureException {

	/**
     * <code>serialVersionUID</code> generated serial version UID
     */
    private static final long serialVersionUID = 3546083570696073784L;

    public CastorResourceFailureException(FatalPersistenceException ex) {
		super(ex.getMessage(), ex);
	}

}
