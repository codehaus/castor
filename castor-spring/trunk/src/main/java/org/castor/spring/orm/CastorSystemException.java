package org.castor.spring.orm;

import org.exolab.castor.jdo.PersistenceException;
import org.springframework.dao.UncategorizedDataAccessException;

/**
 * JDO-specific subclass of UncategorizedDataAccessException,
 * for JDO system errors that do not match any concrete
 * <code>org.springframework.dao</code> exceptions.
 * @author Juergen Hoeller
 * @since 03.06.2003
 * @see JDOManagerUtils#convertJdoAccessException
 */
public class CastorSystemException extends UncategorizedDataAccessException {

	/**
     * <code>serialVersionUID</code> generated serial version UID
     */
    private static final long serialVersionUID = 3258135738951350323L;

    public CastorSystemException(PersistenceException ex) {
		super(ex.getMessage(), ex);
	}
	
}
