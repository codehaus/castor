package org.castor.spring.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.castor.spring.xml.entity.Product;
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

    public void testUnmarshalFromReader() throws IOException, MarshallingException, ValidationException {
        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
        assertNotNull(template);

        InputStream resource = getClass().getResourceAsStream("/input.xml");
        Reader reader = new InputStreamReader(resource);
        Object object = template.unmarshal(reader, Product.class);
        assertNotNull(object);
        assertTrue(object instanceof Product);
        
        Product product = (Product) object;
        assertNotNull(product);
        
        assertEquals(1, product.getId());
        assertEquals("blah", product.getName());
    }

//    public void testUnmarshalFromSAXSource() throws IOException, MarshallingException, ValidationException {
//        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
//        assertNotNull(template);
//
//        InputSource inputSource = new InputSource(getClass().getClassLoader().getResource("input.xml").openStream());
//        SAXSource saxSource = new SAXSource(inputSource);
//        Object object = template.unmarshal(saxSource, Product.class);
//        assertNotNull(object);
//        assertTrue(object instanceof Product);
//        
//        Product product = (Product) object;
//        assertNotNull(product);
//        
//        assertEquals(1, product.getId());
//        assertEquals("blah", product.getName());
//    }
//
//    public void testUnmarshalFromDOMSource() throws IOException, MarshallingException, ValidationException {
//        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
//        assertNotNull(template);
//
//        InputSource inputSource = new InputSource(getClass().getClassLoader().getResource("input.xml").openStream());
//        DOMSource domSource = new DOMSource(inputSource);
//        Object object = template.unmarshal(domSource, Product.class);
//        assertNotNull(object);
//        assertTrue(object instanceof Product);
//        
//        Product product = (Product) object;
//        assertNotNull(product);
//        
//        assertEquals(1, product.getId());
//        assertEquals("blah", product.getName());
//    }

    public void testMarshalTo() throws IOException, MarshallingException, ValidationException {
        DataBindingOperations template = (DataBindingOperations) context.getBean("template");
        assertNotNull(template);

        Writer out = new StringWriter();
        
        Product product = new Product();
        product.setId(3);
        product.setName("product3");
        template.marshal(product, out);
        
        String xml = out.toString();
        assertNotNull(xml);
    }

}