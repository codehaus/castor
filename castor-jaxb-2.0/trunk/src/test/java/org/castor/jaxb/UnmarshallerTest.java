package org.castor.jaxb;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class UnmarshallerTest extends TestCase {

    public void testUnmarshalInputSource() throws JAXBException {
        Unmarshaller unmarshaller = new Unmarshaller(org.castor.test.Test.class);
        InputSource inputSource = new InputSource(getClass().getClassLoader().getResource("sample.xml").toExternalForm());
        Object object = unmarshaller.unmarshal(inputSource);
        assertNotNull(object);
    }

}
