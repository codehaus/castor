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
import org.xml.sax.SAXException;

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
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
     * Represents the instance of {@link CastorJAXBContext}.
     */
    private CastorJAXBContext context;

    /**
     * Represents the instance of map of unmarshaller propeties.
     */
    private final Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * Represents the unmarshalling listener.
     */
    private Unmarshaller.Listener listener;

    /**
     * Represents the {@link Schema} instance used for validating the input.
     */
    private Schema schema;

    /**
     * Creates new instance of {@link CastorUnmarshaller} with the given {@link CastorJAXBContext}
     * instance.
     *
     * @param context the {@link CastorJAXBContext} to use
     * @throws IllegalArgumentException if unmarshaller is null
     */
    CastorUnmarshaller(CastorJAXBContext context) {
        // checks input
        CastorJAXBUtils.checkNotNull(context, "context");

        // assigns the namesake field
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(File f) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(f, "f");

        try {
            // unmarshalls object
            return unmarshalAndValidateSource(new StreamSource(new FileInputStream(f)));
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
        return unmarshalAndValidateSource(new StreamSource(is));
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(Reader reader) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");

        return unmarshalAndValidateSource(new StreamSource(reader));
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(URL url) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(url, "url");

        try {
            return unmarshalAndValidateSource(new StreamSource(url.openStream()));
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

        return unmarshalAndValidateSource(new SAXSource(source));
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(Node node) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(node, "node");

        return unmarshalAndValidateSource(new DOMSource(node));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(node, "node");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        return unmarshalAndValidateSource(new DOMSource(node), declaredType);
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(Source source) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(source, "source");

        return unmarshalAndValidateSource(source);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(source, "source");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        return unmarshalAndValidateSource(source, declaredType);
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(XMLStreamReader reader) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");

        try {
            // unmarshalls object
            return createUnmarshaller().unmarshal(reader);
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
    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        try {
            // create unmarshaller instance
            org.exolab.castor.xml.Unmarshaller unmarshaller = createUnmarshaller();
            // sets the expected class
            unmarshaller.setClass(declaredType);
            // unmarshalls object
            T result = (T) unmarshaller.unmarshal(reader);
            // converts the result into JAXBElement
            return createJAXBElement(unmarshaller, declaredType, result);
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
            return createUnmarshaller().unmarshal(reader);
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
    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
        // checks input
        CastorJAXBUtils.checkNotNull(reader, "reader");
        CastorJAXBUtils.checkNotNull(declaredType, "declaredType");

        try {
            // create unmarshaller instance
            org.exolab.castor.xml.Unmarshaller unmarshaller = createUnmarshaller();
            // sets the expected class
            unmarshaller.setClass(declaredType);
            // unmarshalls object
            T result = (T) unmarshaller.unmarshal(reader);
            // converts the result into JAXBElement
            return createJAXBElement(unmarshaller, declaredType, result);
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
        return new CastorUnmarshallerHandler(createUnmarshaller().createHandler());
    }

    /**
     * {@inheritDoc}
     */
    public void setValidating(boolean validating) throws JAXBException {

        throw new UnsupportedOperationException("Unmarshaller.setValidating method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidating() throws JAXBException {

        throw new UnsupportedOperationException("Unmarshaller.isValidating method is unsupported.");
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

        // sets the property value
        properties.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty(String name) throws PropertyException {

        // retrieves the property value
        return properties.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setSchema(Schema schema) {

        this.schema = schema;
    }

    /**
     * {@inheritDoc}
     */
    public Schema getSchema() {

        return schema;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void setAdapter(XmlAdapter adapter) {
        CastorJAXBUtils.checkNotNull(adapter, "adapter");

        setAdapter((Class<XmlAdapter>) adapter.getClass(), adapter);
    }

    /**
     * {@inheritDoc}
     */
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        CastorJAXBUtils.checkNotNull(type, "type");

        context.getJaxbAdapterRegistry().setAdapter(type, adapter);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        CastorJAXBUtils.checkNotNull(type, "type");

        return (A) context.getJaxbAdapterRegistry().getAdapter(type);
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
    public void setListener(Unmarshaller.Listener listener) {

        this.listener = listener;
    }

    /**
     * {@inheritDoc}
     */
    public Listener getListener() {

        return listener;
    }

    /**
     * Unmarshalls and validates the given {@link Source} instance.
     *
     * @param source the {@link Source} instance to unmarshall
     * @return the unmarshalled object
     * @throws JAXBException if any error occurs during unmarshalling
     */
    private Object unmarshalAndValidateSource(Source source) throws JAXBException {

        try {
            // reads the content of the input
            byte[] content = readSourceToByteArray(source);
            // validates the source
            validateSource(createSource(content));
            // unmarshalls the object
            return createUnmarshaller().unmarshal(createSource(content));
        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (SAXException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (IOException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (TransformerException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        }
    }

    /**
     * Unmarshalls and validates the given {@link Source} into a well know type.
     *
     * @param source       the {@link Source} to use for unmarshalling
     * @param declaredType the expected class of the unmarshalled object
     * @param <T>          the type of expected object
     * @return the unmarshalled object
     * @throws JAXBException if any error occurs during unmarshalling
     */
    @SuppressWarnings("unchecked")
    private <T> JAXBElement<T> unmarshalAndValidateSource(Source source, Class<T> declaredType) throws JAXBException {

        try {
            // reads the content of the input
            byte[] content = readSourceToByteArray(source);
            // create unmarshaller instance
            org.exolab.castor.xml.Unmarshaller unmarshaller = createUnmarshaller();
            // validates the source
            validateSource(createSource(content));
            // sets the expected class
            unmarshaller.setClass(declaredType);
            // unmarshalls object and converts the result into JAXBElement
            return createJAXBElement(unmarshaller, declaredType, (T) unmarshaller.unmarshal(createSource(content)));
        } catch (ClassCastException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (SAXException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (IOException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Error occurred when unmarshalling object.", e);
        } catch (TransformerException e) {
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
     * Validates the passed {@link Source} instance, against the specified schema.
     *
     * @param source the {@link Source} to validate
     * @throws IOException  if any error occurs during IO operation
     * @throws SAXException if any error occurs during validation
     */
    private void validateSource(Source source) throws IOException, SAXException {

        if (schema != null) {

            schema.newValidator().validate(source);
        }
    }

    /**
     * Creates a instance of {@link JAXBElement} that wraps the passed object.</p>
     *
     * @param unmarshaller the unmarshaller to use
     * @param clazz        the class of the wrapped object
     * @param obj          the object to wrap
     * @param <T>          the type of the wrapped object
     * @return the newly created instance of {@link JAXBElement} that wrapps the passed object
     */
    private <T> JAXBElement<T> createJAXBElement(org.exolab.castor.xml.Unmarshaller unmarshaller,
                                                 Class<T> clazz, T obj) {

        return new JAXBElement<T>(getQNameForClass(unmarshaller, clazz), clazz, obj);
    }

    /**
     * Creates a {@link QName} for the passed class.
     *
     * @param unmarshaller the {@link org.exolab.castor.xml.Unmarshaller} to use
     * @param clazz        the class for which the {@link QName} will be created
     * @param <T>          the type of object
     * @return the {@link QName} for the passed class
     */
    private <T> QName getQNameForClass(org.exolab.castor.xml.Unmarshaller unmarshaller, Class<T> clazz) {

        XMLClassDescriptorResolver resolver;
        XMLClassDescriptor descriptor;

        try {
            resolver = unmarshaller.getInternalContext().getXMLClassDescriptorResolver();
            descriptor = resolver.resolve(clazz.getName());

            if (descriptor != null) {
                if (descriptor.getNameSpacePrefix() != null) {
                    return new QName(descriptor.getNameSpaceURI(), descriptor.getXMLName(),
                            descriptor.getNameSpacePrefix());
                }

                return new QName(descriptor.getNameSpaceURI(), descriptor.getXMLName());
            }
        } catch (ResolverException e) {
            // ignores exception
        }

        return new QName("");
    }

    /**
     * Creates new instance of {@link org.exolab.castor.xml.Unmarshaller} that is used internally
     * as marshaling framework.
     * <p/>
     * Creating each time the unmarshaller instance prevents from race conditions and other thread-safety issues.
     *
     * @return newly created {@link org.exolab.castor.xml.Unmarshaller} instance
     */
    private org.exolab.castor.xml.Unmarshaller createUnmarshaller() {

        org.exolab.castor.xml.Unmarshaller unmarshaller = context.createCastorUnmarshaller();

        configureUnmarshaller(unmarshaller);

        return unmarshaller;
    }

    /**
     * Sets the properties for the passed {@link org.exolab.castor.xml.Unmarshaller} instance.
     *
     * @param unmarshaller the {@link org.exolab.castor.xml.Unmarshaller} instance for which the properties will be set
     */
    private void configureUnmarshaller(org.exolab.castor.xml.Unmarshaller unmarshaller) {

        if (listener != null) {
            unmarshaller.setUnmarshalListener(new UnmarshalListenerAdapter(listener));
        }

        for (Map.Entry<String, Object> property : properties.entrySet()) {

            unmarshaller.getInternalContext().setProperty(property.getKey(), property.getValue());
        }
    }

     /**
     * Reads the entire content of the passed {@link Source} instance and returns the result as a byte array.
     * @param source the {@link Source} instance to read
     * @return the content of the {@link Source} as byte array
     * @throws javax.xml.transform.TransformerException if any error occurs during the transformation
     */
    private static byte[] readSourceToByteArray(Source source) throws TransformerException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(byteArrayOutputStream);
        transformer.transform(source, output);

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Creates new instance of {@link StreamSource} from passed byte array.
     * @param content the byte array containing the data to be unmarshalled
     * @return the {@link StreamSource} created from the byte array
     */
    private static Source createSource(byte[] content) {
        return new StreamSource(new ByteArrayInputStream(content));
    }
}
