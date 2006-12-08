package org.castor.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.castor.test.Child;
import org.castor.test.Test;

public class MarshallerTest extends TestCase {

    public void testMarshalToWriter() throws JAXBException {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();
        
        StringWriter out = new StringWriter();
        
        Test test = new Test();
        Child child = new Child();
        child.setName("werner");
        test.setChild(child);
        
        marshaller.marshal(test, out);
        
        assertNotNull(out);
        
        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

}
