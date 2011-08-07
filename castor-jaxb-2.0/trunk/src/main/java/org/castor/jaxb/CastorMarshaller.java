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
     * Represents the instance of Castor marshaller used for
     */
    private final org.exolab.castor.xml.Marshaller marshaller;

    /**
     * Represents the marshall listener.
     */
    private Marshaller.Listener listener;

    /**
     * Creates new instance of {@link CastorMarshaller} with the given {@link org.exolab.castor.xml.Marshaller}
     * instance.
     *
     * @param marshaller the marshaller to use
     *
     * @throws IllegalArgumentException if marshaller is null
     */
    CastorMarshaller(org.exolab.castor.xml.Marshaller marshaller) {
        // checks input
        CastorJAXBUtils.checkNotNull(marshaller, "marshaller");

        // assigns the namesake field
        this.marshaller = marshaller;
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, Result result) throws JAXBException {
        // checks the input parameter
        CastorJAXBUtils.checkNotNull(result, "result");

        try {
            // sets the output
            marshaller.setResult(result);
            // marshals the object
            marshal(jaxbElement);
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
            // sets the output
            marshaller.setWriter(new OutputStreamWriter(os));
            // marshals the object
            marshal(jaxbElement);
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
            // sets the output
            marshaller.setWriter(new FileWriter(output));
            // marshals the object
            marshal(jaxbElement);
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
            // sets the output
            marshaller.setWriter(writer);
            // marshals the object
            marshal(jaxbElement);
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

        // sets the output
        marshaller.setContentHandler(handler);
        // marshals the object
        marshal(jaxbElement);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, Node node) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(node, "node");

        // sets the output
        marshaller.setNode(node);
        // marshals the object
        marshal(jaxbElement);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(writer, "writer");

        // sets the output
        marshaller.setXmlStreamWriter(writer);
        // marshals the object
        marshal(jaxbElement);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
        // checks the input
        CastorJAXBUtils.checkNotNull(writer, "writer");

        // sets the output
        marshaller.setXmlEventWriter(writer);
        // marshals the object
        marshal(jaxbElement);
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

    /**
     * {@inheritDoc}
     */
    public Object getProperty(String name) throws PropertyException {

        // gets the property from the internal context
        return marshaller.getInternalContext().getProperty(name);
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
    public void setAdapter(XmlAdapter adapter) {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.setAdapter method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.setAdapter method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.getAdapter method is unsupported.");
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

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.setSchema method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public Schema getSchema() {

        // TODO implement
        throw new UnsupportedOperationException("Marshaller.getSchema method is unsupported.");
    }

    /**
     * {@inheritDoc}
     */
    public void setListener(Listener listener) {

        // sets the listener
        this.listener = listener;
        // if the passed listener is null then it simply unregisters the existing one
        marshaller.setMarshalListener(listener != null ? new MarshalListenerAdapter(listener) : null);
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
     * @param jaxbElement the object to marshall
     *
     * @throws IllegalArgumentException if the argument is null
     * @throws JAXBException            if any error occurs during marshalling
     */
    private void marshal(Object jaxbElement) throws JAXBException {
        // checks the input parameter
        CastorJAXBUtils.checkNotNull(jaxbElement, "jaxbElement");

        try {
            // marshals the object
            marshaller.marshal(unwrapJAXBElement(jaxbElement));

        } catch (MarshalException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        } catch (ValidationException e) {
            // wraps and throws exception
            throw CastorJAXBUtils.convertToJAXBException("Exception occurred when marshalling object.", e);
        }
    }

    /**
     * This utility method checks if the passed object is instance of {@link JAXBElement}.
     *
     * @param jaxbElement the object to process
     *
     * @return the passed object
     */
    private static Object unwrapJAXBElement(Object jaxbElement) {

        // checks if the passed object is instance of JAXBElement
        if (jaxbElement instanceof JAXBElement) {

            // if it is then retrieves the underlying object
            return ((JAXBElement<?>) jaxbElement).getValue();
        }

        // otherwise simply retrieves the object
        return jaxbElement;
    }
}
