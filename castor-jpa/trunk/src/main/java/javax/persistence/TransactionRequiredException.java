package javax.persistence;

import org.exolab.castor.jdo.TransactionNotInProgressException;

public class TransactionRequiredException extends PersistenceException {

    public TransactionRequiredException(String message) {
        super(message);
    }

    public TransactionRequiredException(String message, TransactionNotInProgressException e) {
        super (message, e);
    }

}
