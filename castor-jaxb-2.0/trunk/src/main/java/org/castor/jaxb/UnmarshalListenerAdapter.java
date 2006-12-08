package org.castor.jaxb;

import javax.xml.bind.Unmarshaller.Listener;

import org.exolab.castor.xml.UnmarshalListener;

public class UnmarshalListenerAdapter
    implements UnmarshalListener {
    
    private Listener listener;

    /**
     * @see org.exolab.castor.xml.UnmarshalListener#attributesProcessed(java.lang.Object)
     */
    public void attributesProcessed(Object object) {
        // not used by JAXB 2.0
    }

    /**
     * @see org.exolab.castor.xml.UnmarshalListener#fieldAdded(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void fieldAdded(String fieldName, Object parent, Object child) {
        // not used by JAXB 2.0
    }

    /**
     * @see org.exolab.castor.xml.UnmarshalListener#initialized(java.lang.Object)
     */
    public void initialized(Object object) {
        // not used by JAXB 2.0
    }

    public void unmarshalled(Object object) {
        if (listener != null) {
            // TODO: amend UnmarshalListener to pass parent object as well
            listener.afterUnmarshal(object, null);
        }
    }
    
    // TODO [WG]: add a method that delivers relevant information to dispatch a beforeUnmarshal() event

    /**
     * Returns the currently set Listener, null otherwise.
     * @return the currently set Listener
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Sets the Listener to use with the Unmarshaller instance.
     * @param listener the Listener to use with the Unmarshaller instance
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }
    
    

}
