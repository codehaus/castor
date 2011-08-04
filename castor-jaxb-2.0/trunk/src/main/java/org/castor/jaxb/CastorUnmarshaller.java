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

import org.castor.jaxb.adapters.CastorUnmarshallerHandler;
import org.castor.jaxb.adapters.UnmarshalListenerAdapter;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * The implementation of {@link org.exolab.castor.xml.Marshaller} which wraps the Castor {@link
 * org.exolab.castor.xml.Marshaller} and uses it internally for marshaling.
 *
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorUnmarshaller implements Unmarshaller {

    /**
     * Represents the instance of Castor marshaller used for
     */
    private org.exolab.castor.xml.Unmarshaller unmarshaller;

    /**
     * Represents whether the unmarshaller perform validation.
     */
    private boolean validating;

    /**
     * Represents the unmarshalling listener.
     */
    private Listener listener;

    /**
     * Creates new instance of {@link CastorUnmarshaller} with the given {@link org.exolab.castor.xml.Unmarshaller}
     * instance.
     *
     * @param unmarshaller the unmarshaller to use
     *
     * @throws IllegalArgumentException if unmarshaller is null
     */
    CastorUnmarshaller(org.exolab.castor.xml.Unmarshaller unmarshaller) {
        // checks input
        CastorJAXBUtils.checkNotNull(unmarshaller, "unmarshaller");

        // assigns the namesake field
        this.unmarshaller = unmarshaller;
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(File f) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(f, "f");

        try {
            // unmarshalls object
            return unmarshal(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(InputStream is) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(is, "is");

        // unmarshalls object
        return unmarshal(new InputSource(is));
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(Reader reader) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");

        try {
            // unmarshalls object
            return unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(URL url) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(url, "url");

        try {
            // unmarshalls object
            return unmarshal(url.openStream());
        } catch (IOException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(InputSource source) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(source, "source");

        try {
            // unmarshalls object
            return unmarshaller.unmarshal(source);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(Node node) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(node, "node");

        try {
            // unmarshalls object
            return unmarshaller.unmarshal(node);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(node, "node");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        try {
            // sets the expected class
            unmarshaller.setClass(declaredType);
            // unmarshalls object
            T result = (T) unmarshaller.unmarshal(node);
            // converts the result into JAXBElement
            return createJAXBElement(declaredType, result);
        } catch (ClassCastException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(Source source) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(source, "source");

        try {
            // unmarshalls object
            return unmarshaller.unmarshal(source);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(source, "source");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        try {
            // sets the expected class
            unmarshaller.setClass(declaredType);
            // unmarshalls object
            T result = (T) unmarshaller.unmarshal(source);
            // converts the result into JAXBElement
            return createJAXBElement(declaredType, result);
        } catch (ClassCastException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(XMLStreamReader reader) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");

        try {
            // unmarshalls object
            return unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        try {
            // sets the expected class
            unmarshaller.setClass(declaredType);
            // unmarshalls object
            T result = (T) unmarshaller.unmarshal(reader);
            // converts the result into JAXBElement
            return createJAXBElement(declaredType, result);
        } catch (ClassCastException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(XMLEventReader reader) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");

        try {
            // unmarshalls object
            return unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        try {
            // sets the expected class
            unmarshaller.setClass(declaredType);
            // unmarshalls object
            T result = (T) unmarshaller.unmarshal(reader);
            // converts the result into JAXBElement
            return createJAXBElement(declaredType, result);
        } catch (ClassCastException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public UnmarshallerHandler getUnmarshallerHandler() {

        // creates new castor unmarshaller handler
        return new CastorUnmarshallerHandler(unmarshaller.createHandler());
    }

    /**
     * {@inheritDoc}
     */
    public void setValidating(boolean validating) throws JAXBException {

        // sets the validating field
        this.validating = validating;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidating() throws JAXBException {

        // returns the validating field
        return validating;
    }

    /**
     * {@inheritDoc}
     */
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.setEventHandler method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public ValidationEventHandler getEventHandler() throws JAXBException {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.getEventHandler method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty(String name, Object value) throws PropertyException {

        // retrieves the property value
        unmarshaller.getInternalContext().setProperty(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty(String name) throws PropertyException {

        // retrieves the property value
        return unmarshaller.getInternalContext().getProperty(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setSchema(Schema schema) {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.setSchema method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public Schema getSchema() {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.getSchema method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public void setAdapter(XmlAdapter adapter) {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.setAdapter method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.setAdapter method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.getAdapter method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.gsetAttachmentUnmarshaller method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        // TODO implement
        throw new UnsupportedOperationException("Unmarshaller.getAttachmentUnmarshaller method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public void setListener(Listener listener) {

        this.listener = listener;
        unmarshaller.setUnmarshalListener(new UnmarshalListenerAdapter(listener));
    }

    /**
     * {@inheritDoc}
     */
    public Listener getListener() {

        return listener;
    }

    /**
     * Creates a instance of {@link JAXBElement} that wraps the passed object.</p>
     *
     * @param clazz the class of the wrapped object
     * @param obj   the object to wrap
     * @param <T>   the type of the wrapped object
     *
     * @return the newly created instance of {@link JAXBElement} that wrapps the passed object
     */
    private <T> JAXBElement<T> createJAXBElement(Class<T> clazz, T obj) {

        return new JAXBElement<T>(getQNameForClass(clazz), clazz, obj);
    }

    /**
     * Creates a {@link QName} for the passed class.
     *
     * @param clazz the class for which the {@link QName} will be created
     * @param <T>   the type of object
     *
     * @return the {@link QName} for the passed class
     */
    private <T> QName getQNameForClass(Class<T> clazz) {

        XMLClassDescriptorResolver resolver;
        XMLClassDescriptor descriptor;

        try {
            resolver = unmarshaller.getInternalContext().getXMLClassDescriptorResolver();
            descriptor = resolver.resolve(clazz.getName());

            if (descriptor != null) {
                return new QName(descriptor.getNameSpaceURI(), descriptor.getXMLName(),
                        descriptor.getNameSpacePrefix());
            }
        } catch (ResolverException e) {
            // ignores exception
        }

        return new QName("");
    }
}
