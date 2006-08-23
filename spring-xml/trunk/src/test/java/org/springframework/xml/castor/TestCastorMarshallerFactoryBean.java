package org.springframework.xml.castor;

import java.io.StringWriter;
import java.io.Writer;

import org.exolab.castor.xml.Marshaller;
import org.springframework.xml.entity.Product;

/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestCastorMarshallerFactoryBean extends BaseSpringTestCase {

    public void testGetMarshaller() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setName("blah");

        Marshaller marshaller = (Marshaller) this.context.getBean("marshaller");
        assertNotNull(marshaller);

        Writer out = new StringWriter();
        marshaller.setWriter(out);
        marshaller.marshal(product);

        System.out.println(out);
    }

    public void testGetMultipleMarshallers() throws Exception {
        Marshaller firstMarshaller = (Marshaller) this.context.getBean("marshaller");
        assertNotNull("Created Marshaller was null", firstMarshaller);

        Marshaller secondMarshaller = (Marshaller) this.context.getBean("marshaller");
        assertNotNull("Created Marshaller was null", secondMarshaller);

        assertNotSame("Created Marshaller are same object, but must not", firstMarshaller, secondMarshaller);
        assertSame("Created Marshallers have different Resolvers, but must have same", firstMarshaller.getResolver(), secondMarshaller
                .getResolver());
    }
}