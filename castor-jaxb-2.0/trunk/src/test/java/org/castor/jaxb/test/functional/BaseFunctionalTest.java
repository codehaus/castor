package org.castor.jaxb.test.functional;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.castor.jaxb.test.functional.attribute.Attribute;
import org.castor.jaxb.test.functional.attribute.AttributeWithExplicitName;
import org.castor.jaxb.test.functional.fieldTransient.EntityWithTransientField;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.xml.sax.SAXException;

@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class BaseFunctionalTest {

    protected javax.xml.bind.Marshaller marshaller;

    @Before
    public void setUp() throws JAXBException {
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Attribute.class, AttributeWithExplicitName.class, EntityWithTransientField.class);
        marshaller = context.createMarshaller();
    }

    protected void assertXmlEquals(String message, String expected, String actual) throws SAXException, IOException {
        String body = StringUtils.removeStart(actual, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        assertXMLEqual(message, expected, body);
    }
    
    protected String marshal(Object object) throws JAXBException {
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, new StreamResult(writer));
        return writer.toString();
    }

}
