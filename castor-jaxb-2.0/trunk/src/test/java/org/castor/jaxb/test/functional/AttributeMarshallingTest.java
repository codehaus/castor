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

        String actualXml = marshal(attribute);

        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

    @Test
    public void testAttributeWithExplicitName() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity other-name=\"test\"></entity>";
        
        AttributeWithExplicitName attributeWithExplicitName = new AttributeWithExplicitName();
        attributeWithExplicitName.setName("test");

        String actualXml = marshal(attributeWithExplicitName);
        
        assertXMLEqual("Marshaller written invalid result.", expectedXml, actualXml);
    }

}
