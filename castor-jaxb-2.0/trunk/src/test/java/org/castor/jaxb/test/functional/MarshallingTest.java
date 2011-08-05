package org.castor.jaxb.test.functional;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.castor.jaxb.test.functional.attribute.Attribute;
import org.castor.jaxb.test.functional.attribute.AttributeWithExplicitName;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MarshallingTest {

    private javax.xml.bind.Marshaller marshaller;

    @Before
    public void setUp() throws JAXBException {
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Attribute.class, AttributeWithExplicitName.class);
        marshaller = context.createMarshaller();
    }

    @Test
    public void testAttributeDefault() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<entity name=\"test\"></entity>";
        
        Attribute attribute = new Attribute();
        attribute.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(attribute, new StreamResult(writer));
        
        assertXMLEqual("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testAttributeWithExplicitName() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<entity other-name=\"test\"></entity>";
        
        AttributeWithExplicitName attributeWithExplicitName = new AttributeWithExplicitName();
        attributeWithExplicitName.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(attributeWithExplicitName, new StreamResult(writer));
        
        assertXMLEqual("Marshaller written invalid result.", expectedXml, writer.toString());
    }


}
