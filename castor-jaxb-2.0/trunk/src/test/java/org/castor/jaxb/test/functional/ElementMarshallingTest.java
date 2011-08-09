package org.castor.jaxb.test.functional;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.castor.jaxb.test.functional.element.Element;
import org.castor.jaxb.test.functional.element.ElementWithAnnotation;
import org.castor.jaxb.test.functional.element.ElementWithAnnotationWithExplicitName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class ElementMarshallingTest extends BaseFunctionalTest {

    @Test
    public void testElementDefault() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><name>test</name></entity>";
        
        Element element = new Element();
        element.setName("test");

        String actualXml = marshal(element);

        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

    @Test
    public void testElementWithAnnotation() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><name>test</name></entity>";
        
        ElementWithAnnotation element = new ElementWithAnnotation();
        element.setName("test");

        String actualXml = marshal(element);

        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

    @Test
    public void testElementWithAnnotationWithExplicitName() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><other-name>test</other-name></entity>";
        
        ElementWithAnnotationWithExplicitName element = new ElementWithAnnotationWithExplicitName();
        element.setName("test");

        String actualXml = marshal(element);

        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }
}
