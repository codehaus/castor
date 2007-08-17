package org.castor.spring.xml;

public class ValidationException extends Exception {

    /**
     * Creates a default instance of this class.
     */
    public ValidationException() {
        super();
    }

    /**
     * Creates an instance of this class, with error message and initial cause provided
     * @param message The original error message
     * @param cause The original error cause.
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an instance of this class, with root error message provided.
     * @param message The original error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates an instance of this class, with root initial cause provided
     * @param cause The original error cause.
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

}
