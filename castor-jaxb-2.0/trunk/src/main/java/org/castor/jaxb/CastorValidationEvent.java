package org.castor.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;

public class CastorValidationEvent implements ValidationEvent {

    /**
     * Warning severity
     */
    private int severity;
    
    /**
     * Message of validation error
     */
    private String message;

    public CastorValidationEvent(final int severity) {
        this.severity = severity;
    }

    public Throwable getLinkedException() {
        // TODO Auto-generated method stub
        return null;
    }

    public ValidationEventLocator getLocator() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getMessage() {
        return this.message;
    }

    public int getSeverity() {
        return this.severity;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return severity + "/" + message;
    }

}