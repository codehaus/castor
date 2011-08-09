package org.castor.jaxb.test.functional;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.castor.jaxb.test.functional.accessOrderClassLavel.EntityWithAlphabeticalOrder;
import org.castor.jaxb.test.functional.accessOrderClassLavel.EntityWithUndefinedOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class AccessorOrderMarshallingTest extends BaseFunctionalTest {

    @Before
    public void setUp() throws JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("org.castor.jaxb.test.functional.accessOrderClassLevel");
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(builder.toString());
        marshaller = context.createMarshaller();
    }

    @Test
    public void testAccessOrderUndefined() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entityWithUndefinedOrder><number>test-number</number><name>test</name></entityWithUndefinedOrder>";
        
        EntityWithUndefinedOrder entity = new EntityWithUndefinedOrder();
        entity.setName("test");
        entity.setNumber("test-number");

        String actualXml = marshal(entity);
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

    @Test
    public void testAccessOrderAlphabetical() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entityWithUndefinedOrder><name>test</name><number>test-number</number></entityWithUndefinedOrder>";
        
        EntityWithAlphabeticalOrder entity = new EntityWithAlphabeticalOrder();
        entity.setName("test");
        entity.setNumber("test-number");

        String actualXml = marshal(entity);
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

}
