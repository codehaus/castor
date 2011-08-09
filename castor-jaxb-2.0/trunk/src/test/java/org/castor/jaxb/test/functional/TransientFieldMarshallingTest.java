package org.castor.jaxb.test.functional;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.castor.jaxb.test.functional.fieldTransient.EntityWithTransientField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransientFieldMarshallingTest extends BaseFunctionalTest {

    @Before
    @Override
    public void setUp() throws JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("org.castor.jaxb.test.functional.fieldTransient");
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(builder.toString());
        marshaller = context.createMarshaller();
    }
    
    @Test
    public void testPropertyTransient() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entityWithTransientField></entityWithTransientField>";
        
        EntityWithTransientField entity = new EntityWithTransientField();
        entity.setName("test");

        String actualXml = marshal(entity);
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

}
