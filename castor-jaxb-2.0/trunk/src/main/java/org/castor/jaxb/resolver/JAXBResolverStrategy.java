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

import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.util.ResolverStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Joachim Grueneis, jgrueneis AT gmail DOT com
 * @version $Id$
 */
@Component("jaxbResolverStrategy")
public class JAXBResolverStrategy implements ResolverStrategy {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /** 
     * The strategy configuration held as map of properties.
     */
    private Map <String, Object> properties = new HashMap <String, Object>();

    @Autowired
    private JAXBClassResolverCommand classResolverCommand;
    
    @Autowired
    private JAXBPackageResolverCommand packageResolverCommand;

    /**
     * {@inheritDoc}
     */
    public ClassDescriptor resolveClass(
            final ResolverResults resolverResults, final String className)
    throws ResolverException {
        if (resolverResults == null) {
            String message = "ResolverResults must not be null";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        if ((className == null) || (className.length() < 1)) {
            String message = "className to resolve must not be null";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        XMLClassDescriptor descriptor = null;

//        resolverResults.addAllDescriptors(new ByDescriptorClass().resolve(className, _properties));
//        descriptor = resolverResults.getDescriptor(className);
//        if (descriptor != null) {
//            return descriptor;
//        }

        resolverResults.addAllDescriptors(
                classResolverCommand.resolve(className, properties));
        descriptor = resolverResults.getDescriptor(className);

        return descriptor;
    }

    /**
     * {@inheritDoc}
     */
    public void resolvePackage(
            final ResolverResults resolverResults, final String packageName)
    throws ResolverException {
        if (resolverResults == null) {
            String message = "ResolverResults must not be null";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        if ((packageName == null) || (packageName.length() < 1)) {
            String message = "packageName to resolve must not be null";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        resolverResults.addAllDescriptors(
                packageResolverCommand.resolve(packageName, properties));
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty(final String key, final Object value) {
        properties.put(key, value);
    }
    
    public void setClassResolverCommand(JAXBClassResolverCommand classResolverCommand) {
        this.classResolverCommand = classResolverCommand;
    }
    
    public void setPackageResolverCommand(JAXBPackageResolverCommand packageResolverCommand) {
        this.packageResolverCommand = packageResolverCommand;
    }
}
