package org.castor.spring.orm;

import org.exolab.castor.jdo.LockNotGrantedException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Castor JDO-specific subclass of ObjectOptimisticLockingFailureException.
 * Converts Castor JDO's ObjectModifiedException.
 * @author Werner Guttmann
 * @since 1.1
 * @see JDOManagerUtils#convertJdoAccessException
 */
public class CastorOptimisticLockingFailureException extends ObjectOptimisticLockingFailureException{

	/**
     * <code>serialVersionUID</code> generated serial version UID
     */
    private static final long serialVersionUID = 3257289149374804274L;

    public CastorOptimisticLockingFailureException(LockNotGrantedException ex) {
		super(ex.getMessage(), ex);
	}

}
