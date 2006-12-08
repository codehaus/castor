package org.castor.jaxb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class Marshaller implements javax.xml.bind.Marshaller {

    /**
     * The Castor XML Marshaller instance used for mashalling.
     */
    private org.exolab.castor.xml.Marshaller marshaller;

    /**
     * An adapter for proxying Castor's MarshalListener callbacks 
     */
    private MarshalListenerAdapter marshalListener = new MarshalListenerAdapter();

    /**
     * Validation event handler
     */
    private ValidationEventHandler validationEventHandler;
    

    public <A extends XmlAdapter> A getAdapter(Class<A> arg0) {
        throw new UnsupportedOperationException();
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        throw new UnsupportedOperationException();
    }

    public ValidationEventHandler getEventHandler() throws JAXBException {
        return this.validationEventHandler;
    }

    public Listener getListener() {
        return marshalListener.getListener();
    }

    public Node getNode(Object node) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public Object getProperty(String arg0) throws PropertyException {
        throw new UnsupportedOperationException();
        // TODO [WG]: add getProperty() to Unmarshaller
        // return unmarshaller.getProperty(property);
    }

    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.xml.bind.Marshaller#marshal(java.lang.Object, javax.xml.transform.Result)
     */
    public void marshal(Object arg0, Result arg1) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public void marshal(Object arg0, OutputStream arg1) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.xml.bind.Marshaller#marshal(java.lang.Object, java.io.Writer)
     */
    public void marshal(Object object, Writer writer) throws JAXBException {
        try {
            marshaller = new org.exolab.castor.xml.Marshaller();
            marshaller.setWriter(writer);
            marshaller.marshal(object);
        } catch (MarshalException e) {
            throw new JAXBException ("Error marshalling object " + object + " to java.util.Writer");
        } catch (ValidationException e) {
            throw new JAXBException ("Validation error marshalling object " + object + " to java.util.Writer");
        } catch (IOException e) {
            throw new JAXBException ("Problem opening the java.util.Writer instance for marshalling " + object);
        }

    }

    /**
     * @see javax.xml.bind.Marshaller#marshal(java.lang.Object, org.xml.sax.ContentHandler)
     */
    public void marshal(Object object, ContentHandler contentHandler) throws JAXBException {
        try {
            marshaller = new org.exolab.castor.xml.Marshaller(contentHandler);
            marshaller.marshal(object);
        } catch (MarshalException e) {
            throw new JAXBException ("Error marshalling object " + object + " to SAX ContentHandler");
        } catch (ValidationException e) {
            throw new JAXBException ("Validation error marshalling object " + object + " to SAX ContentHandler");
        } catch (IOException e) {
            throw new JAXBException ("Problem opening the SAX ContentHandler instance for marshalling " + object);
        }
    }

    /**
     * @see javax.xml.bind.Marshaller#marshal(java.lang.Object, org.w3c.dom.Node)
     */
    public void marshal(Object object, Node node) throws JAXBException {
        try {
            marshaller = new org.exolab.castor.xml.Marshaller(node);
            marshaller.marshal(object);
        } catch (MarshalException e) {
            throw new JAXBException ("Error marshalling object " + object + " to DOM node");
        } catch (ValidationException e) {
            throw new JAXBException ("Validation error marshalling object " + object + " to DOM node");
        }
    }

    /**
     * @see javax.xml.bind.Marshaller#marshal(java.lang.Object, javax.xml.stream.XMLStreamWriter)
     */
    public void marshal(Object arg0, XMLStreamWriter arg1) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.xml.bind.Marshaller#marshal(java.lang.Object, javax.xml.stream.XMLEventWriter)
     */
    public void marshal(Object arg0, XMLEventWriter arg1) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public void setAdapter(XmlAdapter arg0) {
        throw new UnsupportedOperationException();
    }

    public <A extends XmlAdapter> void setAdapter(Class<A> arg0, A arg1) {
        throw new UnsupportedOperationException();
    }

    public void setAttachmentMarshaller(AttachmentMarshaller arg0) {
        throw new UnsupportedOperationException();
    }

    public void setEventHandler(ValidationEventHandler validationEventHandler)
            throws JAXBException {
        this.validationEventHandler = validationEventHandler;
    }

    public void setListener(Listener listener) {
        marshalListener.setListener(listener);
        marshaller.setMarshalListener(marshalListener);
    }

    public void setProperty(String arg0, Object arg1) throws PropertyException {
        throw new UnsupportedOperationException();
        // TODO [WG]: add setProperty(String, Object) to Unmarshaller
        // unmarshaller.setProperty(property, value);
    }

    public void setSchema(Schema arg0) {
        throw new UnsupportedOperationException();
    }

}
