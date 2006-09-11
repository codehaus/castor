package org.springframework.xml.castor;

import java.io.IOException;

import org.springframework.xml.entity.Product;
import org.xml.sax.InputSource;



/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestDataBindingOperations extends BaseSpringTestCase {

    protected void setUp() throws Exception {
        contextResource = "app-config-template.xml";
        super.setUp();
    }
    
    public void testGetDataBindingOperationInstance() {
        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
        assertNotNull(template);
    }
    
    public void testUnmarshalFromInputSource() throws IOException, MarshallingException, ValidationException {
        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
        assertNotNull(template);

        InputSource inputSource = new InputSource(getClass().getClassLoader().getResource("input.xml").openStream());
        Object object = template.unmarshal(inputSource, Product.class);
        assertNotNull(object);
        assertTrue(object instanceof Product);
        
        Product product = (Product) object;
        assertNotNull(product);
        
        assertEquals(1, product.getId());
        assertEquals("blah", product.getName());
    }
    
}