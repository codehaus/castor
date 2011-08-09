package org.castor.jaxb.test.functional;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.castor.jaxb.test.functional.element.Element;
import org.castor.jaxb.test.functional.element.ElementWithAnnotation;
import org.castor.jaxb.test.functional.element.ElementWithAnnotationWithExplicitName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class ElementMarshallingTest extends BaseFunctionalTest {

    @Before
    @Override
    public void setUp() throws JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("org.castor.jaxb.test.functional.element");
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(builder.toString());
        marshaller = context.createMarshaller();
    }

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
