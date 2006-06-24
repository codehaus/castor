package javax.persistence;

/**
 * The NonUniqueResultException is thrown by the persistence provider when
 * Query.getSingleResult is invoked and there is more than one result from the query.
 * This exception will not cause the current transaction, if one is active, to be marked
 * for roll back.
 */
public class NonUniqueResultException extends PersistenceException {

    public NonUniqueResultException(String message) {
        super(message);
    }
    
    

}
