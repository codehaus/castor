package javax.persistence;

/**
 * The OptimisticLockException is thrown by the persistence provider when an optimistic
 * locking conflict occurs. This exception may be thrown as part of an API call, at
 * flush, or at commit time. The current transaction, if one is active, will be marked
 * for rollback.
 */
public class OptimisticLockException extends PersistenceException {

    public OptimisticLockException(String message) {
        super(message);
    }

}
