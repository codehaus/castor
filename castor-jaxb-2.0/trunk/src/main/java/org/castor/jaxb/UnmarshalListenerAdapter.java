/*
 * Copyright 2008 Joachim Grueneis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.castor.jaxb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.UnmarshalListener;

/**
 * An adapter between Castor UnmarshalListener and JAXB UnmarshalListener.
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class UnmarshalListenerAdapter implements UnmarshalListener {
    /** Logger to use. */
    private static final Log LOG = LogFactory.getLog(UnmarshalListenerAdapter.class);
    /** The JAXB listener to call. */
    private Unmarshaller.Listener _jaxbListener;

    /**
     * Default constructor.
     */
    protected UnmarshalListenerAdapter() {
        super();
    }

    /**
     * Callback of Castor unmarshal listener.
     * Ignored as JAXB listener doesn't support it.
     * @param object the object for which the attributes are preocessed
     * @see org.exolab.castor.xml.UnmarshalListener#attributesProcessed(java.lang.Object)
     */
    public void attributesProcessed(final Object object) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("attributesProcessed listener event is ignored.");
        }
        // not used by JAXB 2.0
    }

    /**
     * Callback of Castor unmarshal listener.
     * Ignored as JAXB listener doesn't support it.
     * @param fieldName fieldName
     * @param parent parent
     * @param child child
     * @see org.exolab.castor.xml.UnmarshalListener#fieldAdded(
     * java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void fieldAdded(final String fieldName, final Object parent, final Object child) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("fieldAdded listener event is ignored.");
        }
        // not used by JAXB 2.0
    }

    /**
     * Callback of Castor unmarshal listener.
     * @param object the initialized which will be filled now
     * @see org.exolab.castor.xml.UnmarshalListener#initialized(java.lang.Object)
     */
    public void initialized(final Object object) {
       if (_jaxbListener != null) {
           // TODO: add code to pass parent object as well
           _jaxbListener.beforeUnmarshal(object, null);
       }
    }

    /**
     * Callback of Castor unmarshal listener.
     * @param object the completely unmarshalled object
     * @see org.exolab.castor.xml.UnmarshalListener#unmarshalled(java.lang.Object)
     */
    public void unmarshalled(final Object object) {
        if (_jaxbListener != null) {
            // TODO: amend UnmarshalListener to pass parent object as well
            _jaxbListener.afterUnmarshal(object, null);
        }
    }
    
    // TODO [WG]: add a method that delivers relevant information to dispatch a beforeUnmarshal() event

    /**
     * Returns the currently set Listener, null otherwise.
     * @return the currently set Listener
     */
    public Unmarshaller.Listener getJAXBListener() {
        return _jaxbListener;
    }

    /**
     * Sets the Listener to use with the Unmarshaller instance.
     * @param jaxbListener the JAXB unmarshaller Listener to use with the Unmarshaller instance
     */
    public void setJAXBListener(final Unmarshaller.Listener jaxbListener) {
        this._jaxbListener = jaxbListener;
    }

}
