package org.castor.spring.orm;

import org.springframework.dao.DataAccessException;

/**
 * Castor JDO-specific subclass of DataAccessException.
 * Signals a general problem with missing support for a feature.
 * @author Juergen Hoeller, Werner Guttmann
 */
public class CastorUnsupportedOptionException extends DataAccessException {

    /**
     * <code>serialVersionUID</code> generated serial version UID
     */
    private static final long serialVersionUID = 3258416140203274801L;
    
    public CastorUnsupportedOptionException(String message, Throwable cause) {
        super(message, cause);
    }

}
