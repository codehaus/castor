package org.castor.jaxb.test.functional;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.castor.jaxb.test.functional.attribute.Attribute;
import org.castor.jaxb.test.functional.attribute.AttributeWithExplicitName;
import org.castor.jaxb.test.functional.element.Element;
import org.castor.jaxb.test.functional.element.ElementWithAnnotation;
import org.castor.jaxb.test.functional.element.ElementWithAnnotationWithExplicitName;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithAnnotationWithExplicitNameWithWrapper;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithAnnotationWithWrapper;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithWrapper;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ElementMarshallingTest extends BaseFunctionalTest {

    @Test
    public void testElementDefault() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><name>test</name></entity>";
        
        Element element = new Element();
        element.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testElementWithAnnotation() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><name>test</name></entity>";
        
        ElementWithAnnotation element = new ElementWithAnnotation();
        element.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testElementWithAnnotationWithExplicitName() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><other-name>test</other-name></entity>";
        
        ElementWithAnnotationWithExplicitName element = new ElementWithAnnotationWithExplicitName();
        element.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }
}
