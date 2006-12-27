package org.castor.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidationEventHandlerAdapter implements ErrorHandler {

    /**
     * The JAXB ValidationEventHandler instance to forward events to.
     */
    private ValidationEventHandler handler;

    public void error(SAXParseException e) throws SAXException {
        if (handler != null) {
            CastorValidationEvent validationEvent = new CastorValidationEvent(ValidationEvent.ERROR);
            validationEvent.setMessage(e.getMessage());
            handler.handleEvent(validationEvent);
        }
    }

    public void fatalError(SAXParseException e) throws SAXException {
        if (handler != null) {
            CastorValidationEvent validationEvent = new CastorValidationEvent(ValidationEvent.FATAL_ERROR);
            validationEvent.setMessage(e.getMessage());
            handler.handleEvent(validationEvent);
        }
    }

    public void warning(SAXParseException e) throws SAXException {
        if (handler != null) {
            CastorValidationEvent validationEvent = new CastorValidationEvent(ValidationEvent.WARNING);
            validationEvent.setMessage(e.getMessage());
            handler.handleEvent(validationEvent);
        }
    }

    public ValidationEventHandler getHandler() {
        return handler;
    }

    public void setHandler(ValidationEventHandler handler) {
        this.handler = handler;
    }

}
