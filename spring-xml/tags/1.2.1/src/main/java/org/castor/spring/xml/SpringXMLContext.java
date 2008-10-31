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

import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Out in the wild there are implementations of Castor with or
 * without an InternalContext. This interface describes which access
 * is required independent from the Castor release used beneath.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public interface SpringXMLContext {
    /**
     * Depending on the Castor release in use this method either
     * sets the InternalContext into the Marshaller or it only
     * sets the XMLClassDescriptorResolver.
     * @param marshaller the Marshaller instance to modify
     */
    public void setContext(final Marshaller marshaller);

    /**
     * Depending on the Castor release in use this method either
     * sets the InternalContext into the Unmarshaller or it only
     * sets the XMLClassDescriptorResolver.
     * @param unmarshaller the Unmarshaller instance to modify
     */
    public void setContext(Unmarshaller unmarshaller);

    /**
     * To import the descriptors read by MappingUnmarshaller into
     * the 'context memory'.
     * 
     * @param mappingLoader the descriptors
     */
    public void setMappingLoader(MappingLoader mappingLoader);
}
