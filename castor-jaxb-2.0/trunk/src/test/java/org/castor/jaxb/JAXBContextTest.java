package org.castor.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.castor.test.Test;
import org.xml.sax.InputSource;

public class JAXBContextTest extends TestCase {

    public void testCreateJAXBContext() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        assertNotNull(context);
    }
    
    public void testCreateJAXBContextForMOreThanOneClass() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(new Class[] { Test.class });
        assertNotNull(context);
    }
    
    public void testCreateUnmarshaller() throws JAXBException {
        JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        assertNotNull(unmarshaller);
    }

    
    public void testCreateAndUseUnmarshaller() throws JAXBException {
        JAXBContext context = new org.castor.jaxb.JAXBContext(Test.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        assertNotNull(unmarshaller);
        
        InputSource inputSource = new InputSource(getClass().getClassLoader().getResource("sample.xml").toExternalForm());
        Object object = unmarshaller.unmarshal(inputSource);
        assertNotNull(object);
    }

}
