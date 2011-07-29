package org.castor.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class MockValidationHandler implements ValidationEventHandler {

    private static final Log LOG = LogFactory.getLog(MockValidationHandler.class);

    public boolean handleEvent(ValidationEvent event) {
        LOG.warn("Encountered validation event " + event + " of severity " + event.getSeverity());
        return false;
    }

}
