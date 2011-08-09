package org.castor.jaxb.test.functional;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.castor.jaxb.test.functional.fieldTransient.EntityWithTransientChild;
import org.castor.jaxb.test.functional.fieldTransient.TransientChild;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
public class TransientClassMarshallingTest extends BaseFunctionalTest {

    @Before
    @Override
    public void setUp() throws JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("org.castor.jaxb.test.functional.fieldTransient");
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(builder.toString());
        marshaller = context.createMarshaller();
    }
    
    @Test
    public void testTransientClass() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entityWithTransientChild><name>test</name><id>test</id></entityWithTransientChild>";
        
        EntityWithTransientChild entity = new EntityWithTransientChild();
        entity.setName("test");
        
        TransientChild child = new TransientChild();
        child.setId("test");
        
        entity.setChild(child);

        String actualXml = marshal(entity);
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }

}
