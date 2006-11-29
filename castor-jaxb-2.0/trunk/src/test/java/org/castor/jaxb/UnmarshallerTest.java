package org.castor.jaxb;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

public class UnmarshallerTest extends TestCase {

    public void testUnmarshalInputSource() throws JAXBException {
        Unmarshaller unmarshaller = new Unmarshaller(org.castor.test.Test.class);
        InputSource inputSource = new InputSource(getClass().getClassLoader().getResource("sample.xml").toExternalForm());
        Object object = unmarshaller.unmarshal(inputSource);
        assertNotNull(object);
    }

}
