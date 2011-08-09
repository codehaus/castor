package org.castor.jaxb.test.functional;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.castor.jaxb.test.functional.fieldTransient.EntityWithTransientField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransientFieldMarshallingTest extends BaseFunctionalTest {

    @Test
    public void testPropertyTransient() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entityWithTransientField></entityWithTransientField>";
        
        EntityWithTransientField entity = new EntityWithTransientField();
        entity.setName("test");

        String actualXml = marshal(entity);
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

}
