package org.castor.jaxb.test.functional;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.castor.jaxb.test.functional.attribute.Attribute;
import org.castor.jaxb.test.functional.attribute.AttributeWithExplicitName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class AttributeMarshallingTest extends BaseFunctionalTest {

    @Before
    @Override
    public void setUp() throws JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("org.castor.jaxb.test.functional.attribute");
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(builder.toString());
        marshaller = context.createMarshaller();
    }
    

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
