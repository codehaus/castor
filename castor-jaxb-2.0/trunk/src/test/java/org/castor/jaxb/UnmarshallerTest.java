package org.castor.jaxb;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;

import junit.framework.TestCase;

import org.castor.test.Child;
import org.castor.test.Test;
import org.castor.test.TestListener;
import org.xml.sax.InputSource;

public class UnmarshallerTest extends TestCase {

    public void testUnmarshalInputSource() throws JAXBException {
        javax.xml.bind.JAXBContext jaxbContext =
            org.castor.jaxb.JAXBContext.newInstance(Test.class, Child.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputSource inputSource =
            new InputSource(getClass().getClassLoader().getResource("sample.xml").toExternalForm());
        Object object = unmarshaller.unmarshal(inputSource);
        assertNotNull(object);
    }

    public void testUnmarshalInputSourceWithListener() throws JAXBException {
        javax.xml.bind.JAXBContext jaxbContext 
        = org.castor.jaxb.JAXBContext.newInstance(Test.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Listener listener = new TestListener();
        unmarshaller.setListener(listener);
        InputSource inputSource = 
            new InputSource(getClass().getClassLoader().getResource("sample.xml").toExternalForm());
        Object object = unmarshaller.unmarshal(inputSource);
        assertNotNull(object);
    }

}
