package org.springframework.xml.castor;

import org.exolab.castor.xml.Unmarshaller;
import org.springframework.xml.entity.Product;
import org.xml.sax.InputSource;

/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestCastorUnmarshallerFactoryBean extends BaseFactoryBeanTestCase {

    private Unmarshaller unmarshaller;

    public void testGetUnmarshaller() throws Exception {
        String resource = getClass().getClassLoader().getResource("input.xml").toExternalForm();
        this.unmarshaller = (Unmarshaller) this.context.getBean("unmarshaller");
        assertNotNull(this.unmarshaller);

        Product product = (Product) unmarshaller.unmarshal(new InputSource(resource));
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("blah", product.getName());
    }

    public void testGetMultipleUnmarshallers() throws Exception {
        Unmarshaller firstUnmarshaller = (Unmarshaller) this.context.getBean("unmarshaller");
        assertNotNull("Created Unmarshaller was null", firstUnmarshaller);

        Unmarshaller secondUnmarshaller = (Unmarshaller) this.context.getBean("unmarshaller");
        assertNotNull("Created Unmarshaller was null", secondUnmarshaller);

        assertNotSame("Created Unmarshaller are same object, but must not", firstUnmarshaller, secondUnmarshaller);
        // TODO: method getResolver is missing...
        // assertSame("Created Unmarshallers have different Resolvers, but must
        // have same", firstMarshaller.getResolver(), secondMarshaller
        // .getResolver());
    }
}