/*
 * Copyright 2007 Joachim Grueneis
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
package org.castor.spring.xml;

import org.castor.xml.InternalContext;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLContext;

/**
 * For Castor releases which 'know' InternalContext we use a simple adapter
 * for the Spring-XML implementation.
 * Of course: This class must not be instantiated when running with an 'old'
 * Castor release.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class InternalContextAdapter implements SpringXMLContext {
    /**
     * The Castor InternalContext to work with.
     */
    private InternalContext internalContext;
    
    /**
     * Instantiate the XMLContext and retrieve a 'complete' InternalContext from it.
     */
    public InternalContextAdapter() {
        XMLContext xmlContext = new XMLContext();
        internalContext = xmlContext.getInternalContext();
    }

    /**
     * Sets the InternContext into the Marshaller.
     * 
     * @see org.castor.spring.xml.SpringXMLContext#setContext(org.exolab.castor.xml.Marshaller)
     */
    public void setContext(Marshaller marshaller) {
        marshaller.setInternalContext(internalContext);
    }

    /**
     * Sets the InternContext into the Unmarshaller.
     * 
     * @see org.castor.spring.xml.SpringXMLContext#setContext(org.exolab.castor.xml.Unmarshaller)
     */
    public void setContext(Unmarshaller unmarshaller) {
        unmarshaller.setInternalContext(internalContext);
    }

    /**
     * Call the setMappingLoader method on the resolver instance which is part of
     * the InternalContext.
     * 
     * @see org.castor.spring.xml.SpringXMLContext#setMappingLoader(org.exolab.castor.mapping.MappingLoader)
     */
    public void setMappingLoader(MappingLoader mappingLoader) {
        internalContext.getXMLClassDescriptorResolver().setMappingLoader(mappingLoader);
    }
}
