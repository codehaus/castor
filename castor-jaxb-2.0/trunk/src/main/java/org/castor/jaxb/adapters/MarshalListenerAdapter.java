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
import org.exolab.castor.xml.MarshalListener;

import javax.xml.bind.Marshaller;

/**
 * Implements the Castor MarshalListener and translates each Castor listener
 * call into the according JAXB Marshaller.Listener call. The only logic
 * implemented in this thin layer is: calling the JAXB listener only if a
 * listener is set; Castor allows to return a boolean flag on pre Marhsal which
 * is not used.
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class MarshalListenerAdapter implements MarshalListener {

    /**
     * The JAXB Marshaller Listener instance to forward events to.
     */
    private final Marshaller.Listener _jaxbListener;

    /**
     * Creates new instance of {@link MarshalListenerAdapter} class with given listener.
     *
     * @throws IllegalArgumentException if listener is null
     */
    public MarshalListenerAdapter(javax.xml.bind.Marshaller.Listener listener) {
        CastorJAXBUtils.checkNotNull(listener, "listener");

        _jaxbListener = listener;
    }

    /**
     * Returns the currently set listener, null otherwise.
     * 
     * @return the currently set listener
     */
    public final Marshaller.Listener getJAXBListener() {
        return _jaxbListener;
    }

    /**
     * Callback of Castor marshal listener.
     * @param object the object which will be marshalled now
     * @return true to always allow marshalling
     * @see org.exolab.castor.xml.MarshalListener#preMarshal(java.lang.Object)
     */
    public final boolean preMarshal(final Object object) {
        if (_jaxbListener != null) {
            _jaxbListener.beforeMarshal(object);
        }
        return true;
    }

    /**
     * Callback of Castor marshal listener.
     * @param object the object which has been marshalled now
     * @see org.exolab.castor.xml.MarshalListener#postMarshal(java.lang.Object)
     */
    public final void postMarshal(final Object object) {
        if (_jaxbListener != null) {
            _jaxbListener.afterMarshal(object);
        }
    }

}
