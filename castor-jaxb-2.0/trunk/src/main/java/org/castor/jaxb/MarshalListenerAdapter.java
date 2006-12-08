package org.castor.jaxb;

import javax.xml.bind.Marshaller.Listener;

import org.exolab.castor.xml.MarshalListener;

public class MarshalListenerAdapter implements MarshalListener {
    
    /**
     * The JAXB Marshaller Listener instance to forward events to.
     */
    private Listener listener;

    /**
     * Returns the currently set listener, null otherwise.
     * @return the currently set listener
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Sets the Listener to use with the Marshaller instance.
     * @param listener the Listener to use with the Marshaller instance
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * @see org.exolab.castor.xml.MarshalListener#preMarshal(java.lang.Object)
     */
    public boolean preMarshal(Object object) {
        if (listener != null) {
            listener.beforeMarshal(object);
        }
        return true;
    }
    
    /**
     * @see org.exolab.castor.xml.MarshalListener#postMarshal(java.lang.Object)
     */
    public void postMarshal(Object object) {
        if (listener != null) {
            listener.afterMarshal(object);
        }
    }

}
