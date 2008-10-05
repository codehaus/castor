package org.castor.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.TestCase;

import org.castor.test.Child;
import org.castor.test.Test;
import org.castor.test.TestValidationHandler;

public class MarshallerTest extends TestCase {

    public void testCastorMarshallerWithoutMappingInformation() throws Exception {
        StringWriter out = new StringWriter();

        Test test = new Test();
        Child child = new Child();
        child.setName("werner");
        test.setChild(child);

        org.exolab.castor.xml.Marshaller.marshal(test, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    public void testMarshalToWriter() throws JAXBException {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        Test test = new Test();
        Child child = new Child();
        child.setName("werner");
        test.setChild(child);

        marshaller.marshal(test, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    public void testMarshalTest2ToWriter() throws JAXBException {

        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(org.castor.test2.Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        org.castor.test2.Test test = new org.castor.test2.Test();
        org.castor.test2.Child child = new org.castor.test2.Child();
        child.setName("werner");
        test.setChild(child);

        marshaller.marshal(test, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    public void testMarshalToWriterWithValidation() throws Exception {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        Test test = new Test();
        Child child = new Child();
        child.setName("werner");
        test.setChild(child);

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(new StreamSource(getClass().getClassLoader()
                .getResourceAsStream("test.xsd")));
        marshaller.setSchema(schema);
        marshaller.setEventHandler(new TestValidationHandler());
        marshaller.marshal(test, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    public void testMarshalToWriterAlternateWithValidation() throws Exception {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        org.castor.test2.Test test = new org.castor.test2.Test();
        org.castor.test2.Child child = new org.castor.test2.Child();
        child.setName("werner");
        test.setChild(child);

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(new StreamSource(getClass().getClassLoader()
                .getResourceAsStream("testa.xsd")));
        marshaller.setSchema(schema);
        marshaller.setEventHandler(new TestValidationHandler());
        marshaller.marshal(test, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "stageName",
        "firstName",
        "lastName"
    })
    @XmlRootElement(name = "artist")
    private class Artist {

        @XmlElement(name = "stage-name")
        protected String stageName;
        @XmlElement(name = "first-name")
        protected String firstName;
        @XmlElement(name = "last-name")
        protected String lastName;

        public String getStageName() {
            return stageName;
        }

        public void setStageName(String value) {
            this.stageName = value;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String value) {
            this.firstName = value;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String value) {
            this.lastName = value;
        }
    }

    public void testMarshalArtist() throws JAXBException {
javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Artist.class);
javax.xml.bind.Marshaller marshaller = context.createMarshaller();

StringWriter out = new StringWriter();

Artist a = new Artist();
a.setStageName("Falco");
a.setFirstName("Hans");
a.setLastName("Hšlzel");

marshaller.marshal(a, out);

System.out.println(out.toString());
    }
}
