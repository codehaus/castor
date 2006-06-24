package javax.persistence;

import org.exolab.castor.jdo.DuplicateIdentityException;

public class EntityExistsException extends PersistenceException {

    public EntityExistsException(String message, DuplicateIdentityException e) {
        super(message, e);
    }

}
