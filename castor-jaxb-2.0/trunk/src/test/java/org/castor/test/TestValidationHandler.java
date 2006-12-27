package org.castor.test;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestValidationHandler implements ValidationEventHandler {

    private static final Log LOG = LogFactory.getLog(TestValidationHandler.class);

    public boolean handleEvent(ValidationEvent event) {
        LOG.warn("Encountered validation event " + event + " of severity " + event.getSeverity());
        return false;
    }

}
