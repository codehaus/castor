package org.castor.jaxb.test.functional;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.castor.jaxb.test.functional.elementWrapper.ElementWithAnnotationWithExplicitNameWithWrapper;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithAnnotationWithWrapper;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class ElementWrapperMarshallingTest extends BaseFunctionalTest {

    @Before
    @Override
    public void setUp() throws JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("org.castor.jaxb.test.functional.elementWrapper");
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(builder.toString());
        marshaller = context.createMarshaller();
    }
    
    @Test
    public void testElementDefaultWithWrapper() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><wrapper><name>test</name></wrapper></entity>";

        ElementWithWrapper element = new ElementWithWrapper();
        element.setName("test");

        String actualXml = marshal(element);

        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

    @Test
    public void testElementWithAnnotationWithWrapper() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><wrapper><name>test</name></wrapper></entity>";

        ElementWithAnnotationWithWrapper element = new ElementWithAnnotationWithWrapper();
        element.setName("test");

        String actualXml = marshal(element);

        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

    @Test
    public void testElementWithAnnotationWithExplicitNameWithWrapper() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><wrapper><other-name>test</other-name></wrapper></entity>";

        ElementWithAnnotationWithExplicitNameWithWrapper element = new ElementWithAnnotationWithExplicitNameWithWrapper();
        element.setName("test");

        String actualXml = marshal(element);

        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

}
