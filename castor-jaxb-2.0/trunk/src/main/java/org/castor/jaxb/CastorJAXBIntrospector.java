/*
 * Copyright 2011 Jakub Narloch
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

import javax.xml.bind.JAXBIntrospector;
import javax.xml.namespace.QName;

import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLClassDescriptorResolver;

/**
 * The implementation of {@link JAXBIntrospector} which delegates to {@link XMLClassDescriptorResolver} in other
 * to retrieve information of registered type.
 *
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorJAXBIntrospector extends JAXBIntrospector {

    /**
     * Represents the {@link XMLClassDescriptorResolver} instance used for resolving the objects.
     */
    private final XMLClassDescriptorResolver classDescriptorResolver;

    /**
     * Creates new instance of {@link CastorJAXBIntrospector} with given {@link XMLClassDescriptorResolver}.
     * @param classDescriptorResolver the resolver to use
     *
     * @throws IllegalArgumentException if classDescriptorResolver is null
     */
    CastorJAXBIntrospector(XMLClassDescriptorResolver classDescriptorResolver) {
        // checks input
        CastorJAXBUtils.checkNotNull(classDescriptorResolver, "classDescriptorResolver");

        // assigns the resolver
        this.classDescriptorResolver = classDescriptorResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getElementName(final Object obj) {
        if (obj == null) {
            return null;
        }

        XMLClassDescriptor descriptor;
        try {
            descriptor = (XMLClassDescriptor) classDescriptorResolver.resolve(obj.getClass());

            if(descriptor != null) {
                if(descriptor.getNameSpacePrefix() != null) {
                    return new QName(descriptor.getNameSpaceURI(), descriptor.getXMLName(),
                        descriptor.getNameSpacePrefix());
                }

                return new QName(descriptor.getNameSpacePrefix(), descriptor.getXMLName());
            }
        } catch (ResolverException e) {
            // ignores exception
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElement(final Object obj) {
        if (obj == null) {
            return false;
        }

        try {
            return classDescriptorResolver.resolve(obj.getClass().getName()) != null;
        } catch (ResolverException e) {
            // ignores exception
        }

        return false;
    }
}
