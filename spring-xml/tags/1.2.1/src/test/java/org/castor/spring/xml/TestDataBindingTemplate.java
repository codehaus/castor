package org.castor.spring.xml;

import java.io.StringWriter;

import org.castor.spring.xml.DataBindingOperations;
import org.castor.spring.xml.entity.Product;
import org.xml.sax.InputSource;

/**
 * JUnit test case for testing various contracts as offered by the 
 *
 */
public class TestDataBindingTemplate extends BaseSpringTestCase {
    
    /**
     * @see org.castor.spring.xml.BaseSpringTestCase#setUp()
     */
    protected void setUp() throws Exception {
        contextResource = "app-config-template.xml";
        super.setUp();
    }
    
    /**
     * Tests creation of the DataBindingTemplate through Spring.
     * @throws Exception Any exception, really ....
     */
    public void testCreateTemplate() throws Exception {
        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
        assertNotNull(template);
    }

    /**
     * Tests the marshal() methdo of DataBindingTemplate
     * @throws Exception Any exception really ...
     */
    public void testMarshalling() throws Exception {
        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
        assertNotNull(template);
        
        StringWriter writer = new StringWriter();
        
        Product product = new Product();
        product.setId(1);
        product.setName("blah");
    
        template.marshal(product, writer);
        
        String output = writer.toString();
        assertNotNull(output);
    }

    /**
     * Tests the unmarshal() methdo of DataBindingTemplate-
     * @throws Exception Any exception really ...
     */
    public void testUnmarshalling() throws Exception {
        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
        assertNotNull(template);
        
        InputSource inputSource = new InputSource (getClass().getClassLoader().getResource("input.xml").openStream());
        Product product = (Product) template.unmarshal(inputSource, Product.class);
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("blah", product.getName());
    
    }

}
