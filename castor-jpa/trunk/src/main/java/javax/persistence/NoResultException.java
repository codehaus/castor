package javax.persistence;

/**
 * The NoResultException is thrown by the persistence provider when Query.getSingleResult
 * is invoked and there is no result to return. This exception will not cause the current
 * transaction, if one is active, to be marked for roll back.
 */
public class NoResultException extends PersistenceException {

    public NoResultException(String message) {
        super(message);
    }

}
