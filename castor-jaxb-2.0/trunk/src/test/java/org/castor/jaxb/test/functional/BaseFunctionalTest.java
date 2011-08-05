package org.castor.jaxb.test.functional;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.castor.jaxb.test.functional.attribute.Attribute;
import org.castor.jaxb.test.functional.attribute.AttributeWithExplicitName;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class BaseFunctionalTest {

    protected javax.xml.bind.Marshaller marshaller;

    @Before
    public void setUp() throws JAXBException {
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Attribute.class, AttributeWithExplicitName.class);
        marshaller = context.createMarshaller();
    }

    protected void assertXmlEquals(String message, String expected, String actual) throws SAXException, IOException {
        String body = StringUtils.removeStart(actual, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        assertXMLEqual(message, expected, body);
    }

}
