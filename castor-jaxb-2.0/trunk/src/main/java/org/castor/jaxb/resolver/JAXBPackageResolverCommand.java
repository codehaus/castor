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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.castor.jaxb.reflection.ClassDescriptorBuilder;
import org.castor.jaxb.reflection.ClassInfoBuilder;
import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.util.ResolverPackageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Joachim Grueneis, jgrueneis AT gmail DOT com
 */
@Component
public class JAXBPackageResolverCommand implements ResolverPackageCommand {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * Castor JAXB ClassDescriptor builder.
     */
    private ClassDescriptorBuilder classDescriptorBuilder;
    /**
     * Castor JAXB ClassInfo builder.
     */
    private ClassInfoBuilder classInfoBuilder;

    /**
     * @param classDescriptorBuilder
     *            The Castor JAXB ClassDescriptor builder.
     */
    public void setClassDescriptorBuilder(final ClassDescriptorBuilder classDescriptorBuilder) {
        if (classDescriptorBuilder == null) {
            throw new IllegalArgumentException("ClassDescriptorBuilder must not be set to null.");
        }
        this.classDescriptorBuilder = classDescriptorBuilder;
    }

    /**
     * @param classInfoBuilder
     *            The Castor JAXB ClassInfo builder.
     */
    public void setClassInfoBuilder(final ClassInfoBuilder classInfoBuilder) {
        if (classInfoBuilder == null) {
            throw new IllegalArgumentException("ClassInfoBuilder must not be set to null.");
        }
        this.classInfoBuilder = classInfoBuilder;
    }

    /**
     * The one and only purpose resolver commands are good for ;-) . It can be
     * called with className and clazz set, so the command decides which suites
     * it best or at least one of the two arguments set.
     * 
     * @param packageName
     *            the name of the package to resolve
     * @param properties
     *            the Properties to be used at resolve
     * 
     * @return a Map of className and XMLClassDescriptor
     * 
     * @throws ResolverException
     *             in case that resolving fails fatally
     */
    public Map<String, XMLClassDescriptor> resolve(final String packageName, final Map properties)
            throws ResolverException {

        String className;
        String fileName;
        String dirName;
        String packageToken;
        Class clazz;
        Enumeration<URL> resources;
        Map<String, XMLClassDescriptor> result = new HashMap<String, XMLClassDescriptor>();

        if ((packageName == null) || (packageName.length() == 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Package to load descriptors from is null or empty - nothing done.");
            }
            return result;
        }

        try {
            StringTokenizer packages = new StringTokenizer(packageName, ":");
            ClassLoader classLoader = getClass().getClassLoader();
            while (packages.hasMoreTokens()) {
                packageToken = packages.nextToken();
                dirName = packageToken.replace('.', '/');

                resources = classLoader.getResources(dirName);

                List<File> dirs = new ArrayList<File>();
                while (resources.hasMoreElements()) {
                    URL resourceUrl = resources.nextElement();
                    dirs.add(new File(resourceUrl.toURI()));
                }

                for (File dir : dirs) {
                    for (File file : dir.listFiles()) {

                        if (file.getName().endsWith(".class")) {
                            fileName = file.getName();
                            className = new StringBuffer().append(packageName).append('.')
                                    .append(fileName.substring(0, fileName.length() - 6)).toString();

                            clazz = Class.forName(className);

                            XMLClassDescriptor descriptor = classDescriptorBuilder.buildClassDescriptor(
                                    classInfoBuilder.buildClassInfo(clazz), false);

                            result.put(className, descriptor);
                        }
                    }
                }
            }

            return result;
        } catch (IOException e) {

            throw new ResolverException("Exception occurred when resolving package: " + packageName, e);
        } catch (URISyntaxException e) {

            throw new ResolverException("Exception occurred when resolving package: " + packageName, e);
        } catch (ClassNotFoundException e) {

            throw new ResolverException("Exception occurred when resolving package: " + packageName, e);
        }
    }
}
