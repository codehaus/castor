package org.castor.spring.xml;

import junit.framework.TestCase;

import org.castor.spring.xml.entity.Product;
import org.exolab.castor.util.ObjectFactory;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.InputSource;

/**
 * JUnit test case for Castor's ObjectFactory Spring integration.
 * 
 * @author Paul Philion
 */
public class SpringObjectFactoryTest extends TestCase {
    
    private static final String BEANS = "app-config-with-object-factory.xml";
    
    public void testObjectFactory() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext(BEANS);
        
        ObjectFactory springFactory = (ObjectFactory)context.getBean("springObjectFactory");
        assertNotNull(springFactory);
        
        Unmarshaller unmarshaller = (Unmarshaller)context.getBean("unmarshaller");
        assertNotNull(unmarshaller);
        
        String resource = this.getClass().getClassLoader().getResource("input.xml").toExternalForm();
        Product product = (Product) unmarshaller.unmarshal(new InputSource(resource));
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("blah", product.getName());
        assertEquals("Fall '07 Catalog", product.getCatalog());
        
        // Unmarshal another one
        Product product2 = (Product) unmarshaller.unmarshal(new InputSource(resource));
        System.out.println(product + "---" + product2);
        assertTrue(System.identityHashCode(product2) != System.identityHashCode(product));
        
        assertNotNull(product2);
        assertEquals(1, product2.getId());
        assertEquals("blah", product2.getName());
        assertEquals("Fall '07 Catalog", product2.getCatalog());
    }
}
