package org.castor.jaxb.test.functional;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.castor.jaxb.test.functional.attribute.Attribute;
import org.castor.jaxb.test.functional.attribute.AttributeWithExplicitName;
import org.castor.jaxb.test.functional.element.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class AttributeMarshallingTest extends BaseFunctionalTest {

    @Test
    public void testAttributeDefault() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity name=\"test\"></entity>";
        
        Attribute attribute = new Attribute();
        attribute.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(attribute, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testAttributeWithExplicitName() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity other-name=\"test\"></entity>";
        
        AttributeWithExplicitName attributeWithExplicitName = new AttributeWithExplicitName();
        attributeWithExplicitName.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(attributeWithExplicitName, new StreamResult(writer));
        
        assertXMLEqual("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testElementDefault() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><name>test</name></entity>";
        
        Element element = new Element();
        element.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

}
