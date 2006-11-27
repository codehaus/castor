package org.castor.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

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

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Unmarshaller implements javax.xml.bind.Unmarshaller {

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
        // TODO Auto-generated method stub
        return null;
    }

    public Object getProperty(String arg0) throws PropertyException {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return false;
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

    public void setListener(Listener arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setProperty(String arg0, Object arg1) throws PropertyException {
        // TODO Auto-generated method stub
        
    }

    public void setSchema(Schema arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setValidating(boolean arg0) throws JAXBException {
        // TODO Auto-generated method stub
        
    }

    public Object unmarshal(File arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(InputStream arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(Reader arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(URL arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(InputSource arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(Node arg0) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object unmarshal(Source arg0) throws JAXBException {
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

    public <T> JAXBElement<T> unmarshal(Node arg0, Class<T> arg1) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> JAXBElement<T> unmarshal(Source arg0, Class<T> arg1) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> JAXBElement<T> unmarshal(XMLStreamReader arg0, Class<T> arg1) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> JAXBElement<T> unmarshal(XMLEventReader arg0, Class<T> arg1) throws JAXBException {
        // TODO Auto-generated method stub
        return null;
    }

}
