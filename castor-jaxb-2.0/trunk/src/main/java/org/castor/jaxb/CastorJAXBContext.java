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

import org.castor.jaxb.naming.JAXBJavaNaming;
import org.castor.jaxb.naming.JAXBXmlNaming;
import org.castor.jaxb.reflection.ClassDescriptorBuilder;
import org.castor.jaxb.reflection.ClassInfoBuilder;
import org.castor.jaxb.resolver.JAXBClassResolverCommand;
import org.castor.jaxb.resolver.JAXBPackageResolverCommand;
import org.castor.jaxb.resolver.JAXBResolverStrategy;
import org.castor.xml.InternalContext;
import org.castor.xml.JavaNaming;
import org.castor.xml.XMLNaming;
import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.exolab.castor.xml.XMLContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Node;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import java.io.IOException;
import java.util.Map;

/**
 * A implementation of {@link JAXBContext}, that wraps the Castor marshalling framework.
 *
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorJAXBContext extends JAXBContext {

    /**
     * Represents the {@link XMLContext} instance used for configuring the backend marshalling framework.
     */
    private XMLContext xmlContext;

    /**
     * Creates new instance of {@link CastorJAXBContext} class.
     */
    CastorJAXBContext() {

        xmlContext = new XMLContext();
        InternalContext internalContext = xmlContext.getInternalContext();

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/castor-jaxb-context.xml");
        
        JavaNaming javaNaming = context.getBean("jaxbJavaNaming", JAXBJavaNaming.class);
        XMLNaming xmlNaming = context.getBean("jaxbXmlNaming", JAXBXmlNaming.class);

        internalContext.setJavaNaming(javaNaming);
        internalContext.setXMLNaming(xmlNaming);

        JAXBResolverStrategy resolverStrategy = context.getBean("jaxbResolverStrategy", JAXBResolverStrategy.class);

        internalContext.setResolverStrategy(resolverStrategy);
        XMLClassDescriptorResolver classDescriptorResolver = internalContext.getXMLClassDescriptorResolver();
        classDescriptorResolver.setResolverStrategy(resolverStrategy);
    }

    /**
     * Sets the context path for this {@link CastorJAXBContext}.
     *
     * @param contextPath the context path to use
     * @param classLoader the classLoader to use
     *
     * @throws JAXBException if any error occurs when settings the classes
     */
    public void setContextPath(String contextPath, ClassLoader classLoader) throws JAXBException {
        try {
            xmlContext.addPackage(contextPath);
        } catch (ResolverException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when setting the context path.", e);
        }
    }

    /**
     * Sets the classes for this {@link CastorJAXBContext}.
     *
     * @param classes the classes to be bound
     *
     * @throws JAXBException if any error occurs when settings the classes
     */
    public void setClasses(Class[] classes) throws JAXBException {
        try {
            xmlContext.addClasses(classes);
        } catch (ResolverException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when setting the classes.", e);
        }
    }

    /**
     * Sets the properties for this context.
     *
     * @param properties the map of the properties to set
     */
    public void setProperties(Map<String, Object> properties) {
        // TODO check input

        for (Map.Entry<String, Object> property : properties.entrySet()) {
            xmlContext.setProperty(property.getKey(), property.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Marshaller createMarshaller() throws JAXBException {
        return new CastorMarshaller(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        return new CastorUnmarshaller(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validator createValidator() throws JAXBException {

        throw new UnsupportedOperationException("JAXBContext.createValidator method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Binder<T> createBinder(Class<T> domType) {
        // TODO implement
        return super.createBinder(domType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Binder<Node> createBinder() {
        // TODO implement
        return super.createBinder();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBIntrospector createJAXBIntrospector() {
        return new CastorJAXBIntrospector(xmlContext.getInternalContext().getXMLClassDescriptorResolver());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
        // TODO implement
        super.generateSchema(outputResolver);
    }

    /**
     * Creates new instance of {@link org.exolab.castor.xml.Marshaller} class.
     * @return the new instance of {@link org.exolab.castor.xml.Marshaller}
     */
    org.exolab.castor.xml.Marshaller createCastorMarshaller() {

        return xmlContext.createMarshaller();
    }

    /**
     * Creates new instance of {@link org.exolab.castor.xml.Unmarshaller} class.
     * @return the new instance of {@link org.exolab.castor.xml.Unmarshaller}
     */
    org.exolab.castor.xml.Unmarshaller createCastorUnmarshaller() {

        return xmlContext.createUnmarshaller();
    }
}
