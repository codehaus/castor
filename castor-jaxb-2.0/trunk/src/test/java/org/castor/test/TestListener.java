package org.castor.test;

import javax.xml.bind.Unmarshaller.Listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestListener extends Listener {
    
    private static final Log LOG = LogFactory.getLog(TestListener.class);

    @Override
    public void afterUnmarshal(Object object, Object parent) {
        LOG.debug("Successfully unmarshalled " + object);
    }

    @Override
    public void beforeUnmarshal(Object object, Object parent) {
        LOG.debug("About to unmarshal " + object);
    }
    
    

}
