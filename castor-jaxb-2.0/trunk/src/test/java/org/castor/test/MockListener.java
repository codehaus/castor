package org.castor.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.Unmarshaller.Listener;

public class MockListener extends Listener {
    
    private static final Log LOG = LogFactory.getLog(MockListener.class);

    @Override
    public void afterUnmarshal(Object object, Object parent) {
        LOG.debug("Successfully unmarshalled " + object);
    }

    @Override
    public void beforeUnmarshal(Object object, Object parent) {
        LOG.debug("About to unmarshal " + object);
    }
    
    

}
