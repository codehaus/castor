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

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.xml.InternalContext;
import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLClassDescriptorResolver;

/**
 * The Castor JAXB implementation specific JAXBIntrospector. Used to check
 * if an object is a known element and the element name of it.
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class JAXBIntrospector extends javax.xml.bind.JAXBIntrospector {
    /** Logger instance to use. */
    private static final Log LOG = LogFactory.getLog(JAXBIntrospector.class);
    /**
     * A link to the Castor internal context to look for
     * known descriptors.
     */
    private InternalContext _internalContext;
    /**
     * The class descriptor resolver is required to either read
     * the descriptor form the cache or create a new descriptor.
     */
    private XMLClassDescriptorResolver _classDescriptorResolver;
    
    /**
     * JAXBIntrospector is only allowed to be instantiated through
     * JAXBContext!
     */
    protected JAXBIntrospector() {
        super();
    }
    
    /**
     * To set the InternalContext.
     * @param internalContext the InternalContext
     */
    protected void setInternalContext(final InternalContext internalContext) {
        _internalContext = internalContext;
    }

    /**
     * To set the ClassDescriptorResolver.
     * @param classDescriptorResolver the ClassdescriptorResolver
     */
    protected void setClassDescriptorResolver(
            final XMLClassDescriptorResolver classDescriptorResolver) {
        _classDescriptorResolver = classDescriptorResolver;
    }

    /**
     * @param obj the object to get the element name for
     * @return the element name (String) for the obj
     * @see javax.xml.bind.JAXBIntrospector#getElementName(java.lang.Object)
     */
    @Override
    public QName getElementName(final Object obj) {
        if (obj == null) {
            return null;
        }
        XMLClassDescriptor cd = null;
        try {
            cd = (XMLClassDescriptor) _classDescriptorResolver.resolve(obj.getClass());
        } catch (ResolverException e) {
            LOG.info("Ingoring exception: " + e + " at resolve of: " + obj.getClass().getName());
        }
        return new QName(cd.getNameSpaceURI(), cd.getXMLName(), cd.getNameSpacePrefix());
    }

    /**
     * @param obj the object to check if it is a known element
     * @return true if obj is a known element
     * @see javax.xml.bind.JAXBIntrospector#isElement(java.lang.Object)
     */
    @Override
    public boolean isElement(final Object obj) {
        if (obj == null) {
            return false;
        }
        XMLClassDescriptor cd = null;
        try {
            cd = _classDescriptorResolver.resolve(obj.getClass().getName());
        } catch (ResolverException e) {
            LOG.info("Ingoring exception: " + e + " at resolve of: " + obj.getClass().getName());
        }
        return (cd != null);
    }
}
