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
package org.castor.jaxb.adapters;

import org.castor.jaxb.CastorJAXBUtils;
import org.castor.xml.UnmarshalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;

/**
 * An adapter between Castor UnmarshalListener and JAXB UnmarshalListener.
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class UnmarshalListenerAdapter implements UnmarshalListener {
    /**
     * Logger to use.
     */
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    /** The JAXB listener to call. */
    private final Unmarshaller.Listener _jaxbListener;

    /**
     * Default constructor.
     */
    public UnmarshalListenerAdapter(Unmarshaller.Listener listener) {
        // checks input
        CastorJAXBUtils.checkNotNull(listener, "listener");

        _jaxbListener = listener;
    }

    /**
     * Callback of Castor unmarshal listener.
     * Ignored as JAXB listener doesn't support it.
     * @param target the object for which the attributes are preprocessed
     * @param parent the parent of target
     * @see org.exolab.castor.xml.UnmarshalListener#attributesProcessed(java.lang.Object)
     */
    public void attributesProcessed(final Object target, final Object parent) {
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
     * @param target the initialized which will be filled now
     * @param parent the parent of the target
     * @see org.exolab.castor.xml.UnmarshalListener#initialized(java.lang.Object)
     */
    public void initialized(final Object target, final Object parent) {
       if (_jaxbListener != null) {
           _jaxbListener.beforeUnmarshal(target, parent);
       }
    }

    /**
     * Callback of Castor unmarshal listener.
     * @param target the completely unmarshalled object
     * @param parent the parent of target
     * @see org.exolab.castor.xml.UnmarshalListener#unmarshalled(java.lang.Object)
     */
    public void unmarshalled(final Object target, final Object parent) {
        if (_jaxbListener != null) {
            _jaxbListener.afterUnmarshal(target, parent);
        }
    }

    /**
     * Returns the currently set Listener, null otherwise.
     * @return the currently set Listener
     */
    public Unmarshaller.Listener getJAXBListener() {
        return _jaxbListener;
    }
}
