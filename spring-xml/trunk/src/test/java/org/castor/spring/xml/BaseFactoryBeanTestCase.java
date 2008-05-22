package org.castor.spring.xml;

import java.io.StringWriter;
import java.io.Writer;

import org.castor.spring.xml.entity.Product;
import org.castor.spring.xml.entity.ProductCategory;
import org.exolab.castor.xml.Marshaller;

public abstract class BaseFactoryBeanTestCase extends BaseSpringTestCase {

    public void testGetMarshaller() throws Exception {
        Product product = createProduct();
    
        Marshaller marshaller = (Marshaller) this.context.getBean("marshaller");
        assertNotNull(marshaller);
    
        Writer out = new StringWriter();
        marshaller.setWriter(out);
        marshaller.marshal(product);
    
        System.out.println(out);
    }

    public void testMarshallSuppressNamespaces() throws Exception {
        Product product = createProduct();
    
        Marshaller marshaller = (Marshaller) this.context.getBean("marshaller-suppress");
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

    private Product createProduct() {
        Product product = new Product();
        product.setId(1);
        product.setName("blah");
        
        ProductCategory category = new ProductCategory();
        category.setId(1);
        category.setDescription("category 1");
        
        product.setCategory(category );
        return product;
    }
}
