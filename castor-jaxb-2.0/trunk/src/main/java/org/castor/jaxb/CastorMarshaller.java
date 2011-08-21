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

import org.castor.jaxb.adapters.MarshalListenerAdapter;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
public class CastorMarshaller implements Marshaller {

    /**
     * Represents the instance of {@link CastorJAXBContext} that created this marshaller.
     */
    private final CastorJAXBContext context;

    /**
     * Represents the instance of map of marshaller propeties.
     */
    private final Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * Represents the marshall listener.
     */
    private Marshaller.Listener listener;

    /**
     * Represents the {@link Schema} instance against which marshalled result will be validated.
     */
    private Schema schema;

    /**
     * Creates new instance of {@link CastorMarshaller} with the given {@link CastorJAXBContext}
     * instance.
     *
     * @param context the {@link CastorJAXBContext} to use
     * @throws IllegalArgumentException if marshaller is null
     */
    CastorMarshaller(CastorJAXBContext context) {
        // checks input
        CastorJAXBUtils.checkNotNull(context, "context");

        // assigns the namesake field
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, Result result) throws JAXBException {
        // checks the input parameter
        CastorJAXBUtils.checkNotNull(result, "result");

        try {
            // creates the instance of marshaller
            org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
            // sets the output
            marshaller.setResult(result);
            // marshals the object
            validateAndMarshal(marshaller, jaxbElement);
        } catch (IOException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, OutputStream os) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(os, "os");

        try {
            // creates the instance of marshaller
            org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
            // sets the output
            marshaller.setWriter(new OutputStreamWriter(os));
            // marshals the object
            validateAndMarshal(marshaller, jaxbElement);
        } catch (IOException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, File output) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(output, "output");

        try {
            // creates the instance of marshaller
            org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
            // sets the output
            marshaller.setWriter(new FileWriter(output));
            // marshals the object
            validateAndMarshal(marshaller, jaxbElement);
        } catch (IOException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, Writer writer) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(writer, "writer");

        try {
            // creates the instance of marshaller
            org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
            // sets the output
            marshaller.setWriter(writer);
            // marshals the object
            validateAndMarshal(marshaller, jaxbElement);
        } catch (IOException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, ContentHandler handler) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(handler, "handler");

        // creates the instance of marshaller
        org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
        // sets the output
        marshaller.setContentHandler(handler);
        // marshals the object
        validateAndMarshal(marshaller, jaxbElement);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, Node node) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(node, "node");

        // creates the instance of marshaller
        org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
        // sets the output
        marshaller.setNode(node);
        // marshals the object
        validateAndMarshal(marshaller, jaxbElement);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(writer, "writer");

        // creates the instance of marshaller
        org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
        // sets the output
        marshaller.setXmlStreamWriter(writer);
        // marshals the object
        validateAndMarshal(marshaller, jaxbElement);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(writer, "writer");

        // creates the instance of marshaller
        org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
        // sets the output
        marshaller.setXmlEventWriter(writer);
        // marshals the object
        validateAndMarshal(marshaller, jaxbElement);
    }

    /**
     * {@inheritDoc}
     */
    public Node getNode(Object contentTree) throws JAXBException {

        // TODO implement - but probably this method won't be implemented
        // because the marshaller doesn't use backing DOM tree
        throw new UnsupportedOperationException("Marshaller.getNode method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty(String name, Object value) throws PropertyException {

        // adds the property to map
        properties.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty(String name) throws PropertyException {

        // gets the property from the internal context
        return properties.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.setEventHandler method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public ValidationEventHandler getEventHandler() throws JAXBException {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.getEventHandler method is unsupported.");
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
    public void setAttachmentMarshaller(AttachmentMarshaller am) {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.setAttachmentMarshaller method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public AttachmentMarshaller getAttachmentMarshaller() {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.getAttachmentMarshaller method is unsupported.");
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
    public void setListener(Listener listener) {

        // sets the listener
        this.listener = listener;
    }

    /**
     * {@inheritDoc}
     */
    public Listener getListener() {

        // returns the listener
        return listener;
    }

    /**
     * Marshals the given object.
     *
     * @param marshaller  the marshaller to use
     * @param jaxbElement the object to marshall
     * @throws IllegalArgumentException if the argument is null
     * @throws JAXBException            if any error occurs during marshalling
     */
    private void validateAndMarshal(org.exolab.castor.xml.Marshaller marshaller,
                                    Object jaxbElement) throws JAXBException {
        // checks the input parameter
        CastorJAXBUtils.checkNotNull(jaxbElement, "jaxbElement");

        try {
            Object jaxbObj = unwrapJAXBElement(jaxbElement);

            validate(jaxbObj);

            // marshals the object
            marshaller.marshal(jaxbObj);

        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        }
    }

    /**
     * Validates the result of the given object against the specified schema.
     *
     * @param jaxbElement the object to validate
     *
     * @throws MarshalException if any error occurs during marshalling
     * @throws ValidationException if any error occurs during valdation
     */
    private void validate(Object jaxbElement) throws MarshalException, ValidationException {

        if (schema != null) {

            org.exolab.castor.xml.Marshaller marshaller = createMarshaller();
            marshaller.setContentHandler(schema.newValidatorHandler());
            marshaller.marshal(jaxbElement);
        }
    }

    /**
     * This utility method checks if the passed object is instance of {@link JAXBElement}.
     *
     * @param jaxbElement the object to process
     * @return the passed object
     */
    private static Object unwrapJAXBElement(Object jaxbElement) {

        // checks if the passed object is instance of JAXBElement
        if (jaxbElement instanceof JAXBElement) {

            // if it is then retrieves the underlying object
            return ((JAXBElement<?>) jaxbElement).getValue();
        }

        // otherwise simply returns the object
        return jaxbElement;
    }

    /**
     * Creates new instance of {@link org.exolab.castor.xml.Marshaller} that is used internally
     * as marshaling framework.
     * <p/>
     * Creating each time the marshaller instance prevents from race conditions and other thread-safety issues.
     *
     * @return newly created {@link org.exolab.castor.xml.Marshaller} instance
     */
    private org.exolab.castor.xml.Marshaller createMarshaller() {

        org.exolab.castor.xml.Marshaller marshaller = context.createCastorMarshaller();

        configureMarshaller(marshaller);

        return marshaller;
    }

    /**
     * Sets the properties for the passed {@link org.exolab.castor.xml.Marshaller} instance.
     *
     * @param marshaller the {@link org.exolab.castor.xml.Marshaller} instance for which the properties will be set
     */
    private void configureMarshaller(org.exolab.castor.xml.Marshaller marshaller) {

        if(listener != null) {
            marshaller.setMarshalListener(new MarshalListenerAdapter(listener));
        }

        for (Map.Entry<String, Object> property : properties.entrySet()) {

            setMarshallerProperty(marshaller, property.getKey(), property.getValue());
        }
    }

    /**
     * Sets the single property for the marshaller.
     *
     * @param marshaller the marshaller to configure
     * @param name       the property name
     * @param value      the property value
     */
    private void setMarshallerProperty(org.exolab.castor.xml.Marshaller marshaller,
                                       String name, Object value) {

        if (JAXB_ENCODING.equals(name)) {
            marshaller.setEncoding(String.valueOf(value));
        } else if (JAXB_SCHEMA_LOCATION.equals(name)) {
            marshaller.setSchemaLocation(String.valueOf(value));
        } else if (JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(name)) {
            marshaller.setNoNamespaceSchemaLocation(String.valueOf(value));
        } else if (JAXB_FRAGMENT.equals(name)) {
            marshaller.setMarshalAsDocument(false);
        } else {
            // sets the property for the internal context
            marshaller.getInternalContext().setProperty(name, value);
        }
    }
}
