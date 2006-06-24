package javax.persistence;

import org.exolab.castor.jdo.ObjectNotFoundException;

public class EntityNotFoundException extends PersistenceException {

    public EntityNotFoundException(String message) {
        super(message);// 
    }

    public EntityNotFoundException(String string, ObjectNotFoundException e) {
        super(string, e);
    }

}
