package javax.persistence;

/**
 * The RollbackException is thrown by the persistence provider when
 * EntityTransaction.commit fails.
 */
public class RollbackException extends PersistenceException {

    public RollbackException(String message) {
        super(message);
    }

}
