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
package org.castor.jaxb.resolver;

import java.util.HashMap;
import java.util.Map;

import org.castor.jaxb.reflection.ClassDescriptorBuilder;
import org.castor.jaxb.reflection.ClassInfoBuilder;
import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.util.ResolverPackageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Joachim Grueneis, jgrueneis AT gmail DOT com
 */
public class JAXBPackageResolverCommand implements ResolverPackageCommand {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    /** The Castor JAXB ClassDescriptor builder. */
    private ClassDescriptorBuilder _classDescriptorBuilder;
    /** The Castor JAXB ClassInfo builder. */
    private ClassInfoBuilder _classInfoBuilder;
    
    /**
     * Empty default constructor.
     */
    public JAXBPackageResolverCommand() {
        super();
    }
    
    /**
     * @param classDescriptorBuilder The Castor JAXB ClassDescriptor builder.
     */
    public void setClassDescriptorBuilder(final ClassDescriptorBuilder classDescriptorBuilder) {
        if (classDescriptorBuilder == null) {
            throw new IllegalArgumentException("ClassDescriptorBuilder must not be set to null.");
        }
        _classDescriptorBuilder = classDescriptorBuilder;
    }
    
    /**
     * @param classInfoBuilder The Castor JAXB ClassInfo builder.
     */
    public void setClassInfoBuilder(final ClassInfoBuilder classInfoBuilder) {
        if (classInfoBuilder == null) {
            throw new IllegalArgumentException("ClassInfoBuilder must not be set to null.");
        }
        _classInfoBuilder = classInfoBuilder;
    }

    /**
     * The one and only purpose resolver commands are good for ;-) . It can
     * be called with className and clazz set, so the command decides which
     * suites it best or at least one of the two arguments set.
     * 
     * @param packageName the name of the package to resolve
     * @param properties the Properties to be used at resolve
     * @return a Map of className and XMLClassDescriptor
     * @throws ResolverException in case that resolving fails fatally
     */
    public Map<String, XMLClassDescriptor> resolve(
            final String packageName, final Map properties)
    throws ResolverException {
        HashMap < String, XMLClassDescriptor > hm = new HashMap < String, XMLClassDescriptor > ();
        if ((packageName == null) || (packageName.length() == 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Package to load descriptors from is null or empty - nothing done.");
            }
            return hm;
        }
        // load the JAXB package info file...
        throw new UnsupportedOperationException("Not yet implemented");
//        return hm;
    }
}
