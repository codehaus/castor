package org.castor.jaxb.test.functional;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
public class ElementWrapperDefaultMarshallingTest extends BaseFunctionalTest {

    // @Test
    // public void testElementDefaultWithDefaultWrapper() throws JAXBException,
    // SAXException, IOException {
    // final String expectedXml =
    // "<entity><name><name>test</name></name></entity>";
    //
    // ElementWithDefaultWrapper element = new ElementWithDefaultWrapper();
    // element.setName("test");
    //
    // StringWriter writer = new StringWriter();
    // marshaller.marshal(element, new StreamResult(writer));
    //
    // System.out.println(writer.toString());
    //
    // assertXmlEquals("Marshaller written invalid result.", expectedXml,
    // writer.toString());
    // }
    //
    // @Test
    // public void testElementWithAnnotationWithDefaultWrapper() throws
    // JAXBException, SAXException, IOException {
    // final String expectedXml = "<entity><name>test</name></entity>";
    //
    // ElementWithAnnotationWithDefaultWrapper element = new
    // ElementWithAnnotationWithDefaultWrapper();
    // element.setName("test");
    //
    // StringWriter writer = new StringWriter();
    // marshaller.marshal(element, new StreamResult(writer));
    //
    // System.out.println(writer.toString());
    //
    // assertXmlEquals("Marshaller written invalid result.", expectedXml,
    // writer.toString());
    // }
    //
    // @Test
    // public void testElementWithAnnotationWithExplicitNameWithDefaultWrapper()
    // throws JAXBException, SAXException, IOException {
    // final String expectedXml =
    // "<entity><other-name>test</other-name></entity>";
    //
    // ElementWithAnnotationWithExplicitName element = new
    // ElementWithAnnotationWithExplicitName();
    // element.setName("test");
    //
    // StringWriter writer = new StringWriter();
    // marshaller.marshal(element, new StreamResult(writer));
    //
    // assertXmlEquals("Marshaller written invalid result.", expectedXml,
    // writer.toString());
    // }

}
