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

import org.castor.mapping.BindingType;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.ClassDescriptorResolverFactory;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLClassDescriptorResolver;

/**
 * The SpringXMLContext implementation to be used when Spring-XML works with a version of
 * Castor which has no InternalContext. The approach in this case is to simply hold the
 * XMLClassDescriptorResolver instance locally and provide it by getter.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class NoInternalContextAdapter implements SpringXMLContext {
    /** The XMLClassDescriptorResolver instance to use by Marshaller and Unmarshaller created. */
    private final XMLClassDescriptorResolver resolver;

    /**
     * A XMLClassDescriptorResolver instance is required to be instantiated.
     */
    public NoInternalContextAdapter() {
        resolver = (XMLClassDescriptorResolver) ClassDescriptorResolverFactory.createClassDescriptorResolver(BindingType.XML);
    }

    /**
     * For 'old' Castor releases this simply means to set the resolver
     * into the Marshaller.
     * 
     * @see org.castor.spring.xml.SpringXMLContext#setContext(org.exolab.castor.xml.Marshaller)
     */
    public void setContext(Marshaller marshaller) {
        marshaller.setResolver(resolver);
    }

    /**
     * For 'old' Castor releases this simply means to set the resolver
     * into the Unmarshaller.
     * @see org.castor.spring.xml.SpringXMLContext#setContext(org.exolab.castor.xml.Unmarshaller)
     */
    public void setContext(Unmarshaller unmarshaller) {
        unmarshaller.setResolver(resolver);
    }

    /**
     * Call setMappingLoader on the resolver managed by this context.
     * 
     * @see org.castor.spring.xml.SpringXMLContext#setMappingLoader(org.exolab.castor.mapping.MappingLoader)
     */
    public void setMappingLoader(MappingLoader mappingLoader) {
        resolver.setMappingLoader(mappingLoader);
    }
}
