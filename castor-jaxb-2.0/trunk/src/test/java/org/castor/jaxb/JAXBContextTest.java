package org.castor.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.castor.test.Test;

public class JAXBContextTest extends TestCase {

    public void testCreateJAXBContextNullClass() throws JAXBException {
        try {
            org.castor.jaxb.JAXBContext.newInstance((Class) null);
            Assert.fail("It shouldn't be possible to create a JAXBContext instance with null");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testCreateJAXBContextNullString() throws JAXBException {
        try {
            org.castor.jaxb.JAXBContext.newInstance((String) null);
            Assert.fail("It shouldn't be possible to create a JAXBContext instance with null");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    public void testCreateJAXBContext() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        assertNotNull(context);
    }
    
    public void testCreateJAXBContextForMOreThanOneClass() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        assertNotNull(context);
    }
    
    public void testCreateUnmarshaller() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        assertNotNull(unmarshaller);
    }

    
    public void testCreateAndUseUnmarshaller() throws JAXBException {
//        JAXBContext context = new org.castor.jaxb.JAXBContext(Test.class);
//        Unmarshaller unmarshaller = context.createUnmarshaller();
//        assertNotNull(unmarshaller);
//        
//        InputSource inputSource = new InputSource(getClass().getClassLoader().
//        getResource("sample.xml").toExternalForm());
//        Object object = unmarshaller.unmarshal(inputSource);
//        assertNotNull(object);
    }
    
    public void testCreateMarshaller() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        Marshaller marshaller = context.createMarshaller();
        assertNotNull(marshaller);
    }
    
    public void testCreateAndUseMarshaller() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        Marshaller marshaller = context.createMarshaller();
        assertNotNull(marshaller);
        StringWriter sw = new StringWriter();
        marshaller.marshal(new Test(), sw);
        System.out.println(sw.toString());
    }

}
