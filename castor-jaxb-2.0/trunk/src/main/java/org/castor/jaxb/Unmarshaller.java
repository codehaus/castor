package org.castor.jaxb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Unmarshaller implements javax.xml.bind.Unmarshaller {
    
    private org.exolab.castor.xml.Unmarshaller unmarshaller;
    
    private ListenerAdapter unmarshalListener = new ListenerAdapter();
    
    public Unmarshaller (Class aClass) {
        unmarshaller = new org.exolab.castor.xml.Unmarshaller(aClass);
    }

    public <A extends XmlAdapter> A getAdapter(Class<A> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        // TODO Auto-generated method stub
        return null;
    }

    public ValidationEventHandler getEventHandler() throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Listener getListener() {
        return unmarshalListener.getListener();
    }

    /**
     * @see javax.xml.bind.Unmarshaller#getProperty(java.lang.String)
     */
    public Object getProperty(String property) throws PropertyException {
        throw new UnsupportedOperationException();
        // TODO [WG]: add getProperty() to Unmarshaller
        // return unmarshaller.getProperty(property);
    }

    public Schema getSchema() {
        // TODO Auto-generated method stub
        return null;
    }

    public UnmarshallerHandler getUnmarshallerHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isValidating() throws JAXBException {
        return unmarshaller.isValidating();
    }

    public void setAdapter(XmlAdapter arg0) {
        // TODO Auto-generated method stub
        
    }

    public <A extends XmlAdapter> void setAdapter(Class<A> arg0, A arg1) {
        // TODO Auto-generated method stub
        
    }

    public void setAttachmentUnmarshaller(AttachmentUnmarshaller arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setEventHandler(ValidationEventHandler arg0) throws JAXBException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see javax.xml.bind.Unmarshaller#setListener(javax.xml.bind.Unmarshaller.Listener)
     */
    public void setListener(Listener listener) {
        unmarshalListener.setListener(listener);
        unmarshaller.setUnmarshalListener(unmarshalListener);
    }

    /**
     * @see javax.xml.bind.Unmarshaller#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String property, Object value) throws PropertyException {
        throw new UnsupportedOperationException();
        // TODO [WG]: add setProperty(String, Object) to Unmarshaller
        // unmarshaller.setProperty(property, value);
    }

    public void setSchema(Schema arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * @see javax.xml.bind.Unmarshaller#setValidating(boolean)
     */
    public void setValidating(boolean validate) throws JAXBException {
        unmarshaller.setValidation(validate);
    }

    public Object unmarshal(File file) throws JAXBException {
        try {
            return unmarshal(new InputSource(file.toURL().toExternalForm()));
        } catch (MalformedURLException e) {
            throw new JAXBException("Problem unmarshalling from file " + file.getAbsolutePath(), e);
        }
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.InputStream)
     */
    public Object unmarshal(InputStream inputStream) throws JAXBException {
        return unmarshal(new InputSource(inputStream));
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.Reader)
     */
    public Object unmarshal(Reader reader) throws JAXBException {
        return unmarshal(new InputSource(reader));
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.net.URL)
     */
    public Object unmarshal(URL url) throws JAXBException {
        try {
            return unmarshal(new InputSource(url.openStream()));
        } catch (IOException e) {
            throw new JAXBException("Problem unmarshalling from URL " + url.toExternalForm(), e);
        }
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.xml.sax.InputSource)
     */
    public Object unmarshal(InputSource inputSource) throws JAXBException {
        try {
            return unmarshaller.unmarshal(inputSource);
        } catch (MarshalException e) {
            throw new JAXBException("Problem unmarshalling from InputSource " + inputSource.toString(), e);
        } catch (ValidationException e) {
            throw new JAXBException("Problem validating XML from InputSource " + inputSource.toString(), e);
        }
    }

    public Object unmarshal(Node arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(Source source) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(XMLStreamReader arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(XMLEventReader arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(org.w3c.dom.Node, java.lang.Class)
     */
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> type) throws JAXBException {
        return (JAXBElement<T>) unmarshal(node, type);
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.transform.Source, java.lang.Class)
     */
    public <T> JAXBElement<T> unmarshal(Source node, Class<T> type) throws JAXBException {
        return (JAXBElement<T>) unmarshal(node, type);
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLStreamReader, java.lang.Class)
     */
    public <T> JAXBElement<T> unmarshal(XMLStreamReader xmlStreamReader, Class<T> type) throws JAXBException {
        // TODO Auto-generated method stub
        return (JAXBElement<T>) unmarshal (xmlStreamReader, type);
    }

    /**
     * @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.stream.XMLEventReader, java.lang.Class)
     */
    public <T> JAXBElement<T> unmarshal(XMLEventReader arg0, Class<T> arg1) throws JAXBException {
        return (JAXBElement<T>) unmarshal(arg0, arg1);
    }

}
