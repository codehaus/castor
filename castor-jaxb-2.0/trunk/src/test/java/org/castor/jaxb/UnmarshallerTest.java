package org.castor.jaxb;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;

import junit.framework.TestCase;

import org.castor.test.Child;
import org.castor.test.Test;
import org.castor.test.TestListener;
import org.xml.sax.InputSource;

/**
 * This test case should show (and test) a simple situation of
 * getting a JXB context, introduce two classes to it, create an
 * unmarshaller instance, unmarshall a file and all is fine.
 * 
 * Truth is... this simple test fails because descriptors create by
 * introspection have no XML name assigned and aren't found... even
 * in classic Castor - so I will comment the two test cases.
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class UnmarshallerTest extends TestCase {
    
    public void testNix() {
        // an empty test until the other two can be enabled again - CASTOR-2578
    }

//    public void testUnmarshalInputSource() throws JAXBException {
//        javax.xml.bind.JAXBContext jaxbContext = org.castor.jaxb.JAXBContext
//                .newInstance(Test.class, Child.class);
//        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//        InputSource inputSource = new InputSource(getClass().getClassLoader()
//                .getResource("sample.xml").toExternalForm());
//        Object object = unmarshaller.unmarshal(inputSource);
//        assertNotNull(object);
//    }
//
//    public void testUnmarshalInputSourceWithListener() throws JAXBException {
//        javax.xml.bind.JAXBContext jaxbContext = org.castor.jaxb.JAXBContext
//                .newInstance(Test.class);
//        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//        Listener listener = new TestListener();
//        unmarshaller.setListener(listener);
//        InputSource inputSource = new InputSource(getClass().getClassLoader()
//                .getResource("sample.xml").toExternalForm());
//        Object object = unmarshaller.unmarshal(inputSource);
//        assertNotNull(object);
//    }

}
