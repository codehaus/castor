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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

/**
 * This is a factory class that will registered within jaxb.properties file and will responsible for creating the {@link
 * CastorJAXBContext} class.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorJAXBContextFactory {

    /**
     * Represents the name of the property that allows to register the context factory used by {@link JAXBContext}.
     */
    private static final String JAXBCONTEXT_PROPERTY_NAME = "javax.xml.bind.JAXBContext";

    /**
     * Represents the name of this context factory class.
     */
    private static final String CASTOR_JAXBCONTEXT_FACTORY = "org.castor.jaxb.CastorJAXBContextFactory";

    /**
     * Logger used by this class.
     */
    private static final Log LOG = LogFactory.getLog(CastorJAXBContextFactory.class);

    /**
     * Creates new instance of {@link JAXBContext} class from the given contextPath and class loader.
     *
     * @param contextPath the context path
     * @param classLoader the class loader to load classes
     * @param properties  the properties for configuring the context
     *
     * @return the newly created {@link JAXBContext}
     *
     * @throws IllegalArgumentException if contextPath is null or empty or classLoader or properites are null
     * @throws JAXBException if any error occurs when creating the {@link JAXBContext}
     */
    public static JAXBContext createContext(String contextPath, ClassLoader classLoader,
                                                           Map<String, Object> properties) throws JAXBException {
        try {
            // checks input
            CastorJAXBUtils.checkNotEmpty(contextPath, "contextPath");
            CastorJAXBUtils.checkNotNull(classLoader, "classLoader");
            CastorJAXBUtils.checkNotNull(properties, "properties");

            // creates new context instance
            CastorJAXBContext castorJAXBContext = new CastorJAXBContext();
            castorJAXBContext.setContextPath(contextPath, classLoader);
            castorJAXBContext.setProperties(properties);

            // returns the created context
            return castorJAXBContext;
        } catch (JAXBException e) {

            CastorJAXBUtils.logError(LOG,
                    "Error occurred in CastorJAXBContextFactory.createContext.", e);
            throw e;
        } catch (IllegalArgumentException e) {

            CastorJAXBUtils.logError(LOG,
                    "IllegalArgumentException occurred in CastorJAXBContextFactory.createContext.", e);

            throw e;
        }
    }

    /**
     * Creates new instance of {@link JAXBContext} class from the given classes.
     *
     * @param classes    the classes which will be used for configuring the context
     * @param properties the properties for configuring the context
     *
     * @return the newly created {@link JAXBContext}
     *
     * @throws IllegalArgumentException if classes or properties are null
     * @throws JAXBException if any error occurs when creating the {@link JAXBContext}
     */
    public static JAXBContext createContext(Class[] classes, Map<String, Object> properties)
            throws JAXBException {

        try {
            CastorJAXBUtils.checkNotNull(classes, "classes");
            CastorJAXBUtils.checkNotNull(properties, "properties");

            // creates new context instance
            CastorJAXBContext castorJAXBContext = new CastorJAXBContext();
            castorJAXBContext.setClasses(classes);
            castorJAXBContext.setProperties(properties);

            return castorJAXBContext;
        } catch (JAXBException e) {

            CastorJAXBUtils.logError(LOG,
                    "Error occurred in CastorJAXBContextFactory.createContext.", e);
            throw e;
        } catch (IllegalArgumentException e) {

            CastorJAXBUtils.logError(LOG,
                    "IllegalArgumentException occurred in CastorJAXBContextFactory.createContext.", e);
            throw e;
        }
    }

    /**
     * Registers the {@link CastorJAXBContextFactory} as the default JAXB provider.
     */
    public static void registerContextFactory() {

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                System.setProperty(JAXBCONTEXT_PROPERTY_NAME, CASTOR_JAXBCONTEXT_FACTORY);
                return null;
            }
        });
    }
}
