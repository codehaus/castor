package org.castor.jaxb.test.functional;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.castor.jaxb.test.functional.attribute.Attribute;
import org.castor.jaxb.test.functional.attribute.AttributeWithExplicitName;
import org.castor.jaxb.test.functional.element.Element;
import org.castor.jaxb.test.functional.element.ElementWithAnnotation;
import org.castor.jaxb.test.functional.element.ElementWithAnnotationWithExplicitName;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithAnnotationWithExplicitNameWithWrapper;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithAnnotationWithWrapper;
import org.castor.jaxb.test.functional.elementWrapper.ElementWithWrapper;
import org.castor.jaxb.test.functional.elementWrapperDefault.ElementWithAnnotationWithDefaultWrapper;
import org.castor.jaxb.test.functional.elementWrapperDefault.ElementWithDefaultWrapper;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MarshallingTest {

    private javax.xml.bind.Marshaller marshaller;

    @Before
    public void setUp() throws JAXBException {
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Attribute.class, AttributeWithExplicitName.class);
        marshaller = context.createMarshaller();
    }

    @Test
    public void testAttributeDefault() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity name=\"test\"></entity>";
        
        Attribute attribute = new Attribute();
        attribute.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(attribute, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testAttributeWithExplicitName() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity other-name=\"test\"></entity>";
        
        AttributeWithExplicitName attributeWithExplicitName = new AttributeWithExplicitName();
        attributeWithExplicitName.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(attributeWithExplicitName, new StreamResult(writer));
        
        assertXMLEqual("Marshaller written invalid result.", expectedXml, writer.toString());
    }
    
    private void assertXmlEquals(String message, String expected, String actual) throws SAXException, IOException {
        String body = StringUtils.removeStart(actual, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        assertXMLEqual(message, expected, body);
    }


    @Test
    public void testElementDefault() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><name>test</name></entity>";
        
        Element element = new Element();
        element.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testElementWithAnnotation() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><name>test</name></entity>";
        
        ElementWithAnnotation element = new ElementWithAnnotation();
        element.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

    @Test
    public void testElementWithAnnotationWithExplicitName() throws JAXBException, SAXException, IOException {
        final String expectedXml = "<entity><other-name>test</other-name></entity>";
        
        ElementWithAnnotationWithExplicitName element = new ElementWithAnnotationWithExplicitName();
        element.setName("test");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, new StreamResult(writer));
        
        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
    }

//    @Test
//    public void testElementDefaultWithDefaultWrapper() throws JAXBException, SAXException, IOException {
//        final String expectedXml = "<entity><name><name>test</name></name></entity>";
//        
//        ElementWithDefaultWrapper element = new ElementWithDefaultWrapper();
//        element.setName("test");
//
//        StringWriter writer = new StringWriter();
//        marshaller.marshal(element, new StreamResult(writer));
//        
//        System.out.println(writer.toString());
//        
//        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
//    }
//
//    @Test
//    public void testElementWithAnnotationWithDefaultWrapper() throws JAXBException, SAXException, IOException {
//        final String expectedXml = "<entity><name>test</name></entity>";
//        
//        ElementWithAnnotationWithDefaultWrapper element = new ElementWithAnnotationWithDefaultWrapper();
//        element.setName("test");
//
//        StringWriter writer = new StringWriter();
//        marshaller.marshal(element, new StreamResult(writer));
//
//        System.out.println(writer.toString());
//
//        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
//    }
//
//    @Test
//    public void testElementWithAnnotationWithExplicitNameWithDefaultWrapper() throws JAXBException, SAXException, IOException {
//        final String expectedXml = "<entity><other-name>test</other-name></entity>";
//        
//        ElementWithAnnotationWithExplicitName element = new ElementWithAnnotationWithExplicitName();
//        element.setName("test");
//
//        StringWriter writer = new StringWriter();
//        marshaller.marshal(element, new StreamResult(writer));
//        
//        assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
//    }

  @Test
  public void testElementDefaultWithWrapper() throws JAXBException, SAXException, IOException {
      final String expectedXml = "<entity><wrapper><name>test</name></wrapper></entity>";
      
      ElementWithWrapper element = new ElementWithWrapper();
      element.setName("test");

      StringWriter writer = new StringWriter();
      marshaller.marshal(element, new StreamResult(writer));
      
      System.out.println(writer.toString());
      
      assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
  }

  @Test
  public void testElementWithAnnotationWithWrapper() throws JAXBException, SAXException, IOException {
      final String expectedXml = "<entity><wrapper><name>test</name></wrapper></entity>";
      
      ElementWithAnnotationWithWrapper element = new ElementWithAnnotationWithWrapper();
      element.setName("test");

      StringWriter writer = new StringWriter();
      marshaller.marshal(element, new StreamResult(writer));

      System.out.println(writer.toString());

      assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
  }

  @Test
  public void testElementWithAnnotationWithExplicitNameWithWrapper() throws JAXBException, SAXException, IOException {
      final String expectedXml = "<entity><wrapper><other-name>test</other-name></wrapper></entity>";
      
      ElementWithAnnotationWithExplicitNameWithWrapper element = new ElementWithAnnotationWithExplicitNameWithWrapper();
      element.setName("test");

      StringWriter writer = new StringWriter();
      marshaller.marshal(element, new StreamResult(writer));
      
      assertXmlEquals("Marshaller written invalid result.", expectedXml, writer.toString());
  }

}
