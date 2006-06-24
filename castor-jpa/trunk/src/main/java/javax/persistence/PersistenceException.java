package javax.persistence;

public class PersistenceException extends RuntimeException {

    public PersistenceException(String message, Exception e) {
        super (message, e);
    }

    public PersistenceException(String message) {
        super (message);
    }

}
