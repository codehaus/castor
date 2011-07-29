package org.castor.jaxb;

import org.castor.test.Child;
import org.castor.test.MockValidationHandler;
import org.castor.test2.Entity;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringWriter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MarshallerTest {

    @Test
    public void testCastorMarshallerWithoutMappingInformation() throws Exception {
        StringWriter out = new StringWriter();

        org.castor.test.Entity entity = new org.castor.test.Entity();
        Child child = new Child();
        child.setName("werner");
        entity.setChild(child);

        org.exolab.castor.xml.Marshaller.marshal(entity, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    @Test
    public void testMarshalToWriter() throws JAXBException {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        org.castor.test.Entity entity = new org.castor.test.Entity();
        Child child = new Child();
        child.setName("werner");
        entity.setChild(child);

        marshaller.marshal(entity, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    @Test
    public void testMarshalTest2ToWriter() throws JAXBException {

        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Entity.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        Entity entity = new Entity();
        org.castor.test2.Child child = new org.castor.test2.Child();
        child.setName("werner");
        entity.setChild(child);

        marshaller.marshal(entity, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    @Test
    public void testMarshalToWriterWithValidation() throws Exception {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        org.castor.test.Entity entity = new org.castor.test.Entity();
        Child child = new Child();
        child.setName("werner");
        entity.setChild(child);

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(new StreamSource(getClass().getClassLoader()
                .getResourceAsStream("test.xsd")));
        marshaller.setSchema(schema);
        marshaller.setEventHandler(new MockValidationHandler());
        marshaller.marshal(entity, out);

        System.out.println(out.toString());

        assertNotNull(out);

        String xml = out.toString();
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    @Test
    public void testMarshalToWriterAlternateWithValidation() throws Exception {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Test.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        Entity entity = new Entity();
        org.castor.test2.Child child = new org.castor.test2.Child();
        child.setName("werner");
        entity.setChild(child);

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(new StreamSource(getClass().getClassLoader()
                .getResourceAsStream("testa.xsd")));
        marshaller.setSchema(schema);
        marshaller.setEventHandler(new MockValidationHandler());
        marshaller.marshal(entity, out);

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
    private static class Artist {

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

    @Test
    public void testMarshalArtist() throws JAXBException {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(Artist.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();

        StringWriter out = new StringWriter();

        Artist a = new Artist();
        a.setStageName("Falco");
        a.setFirstName("Hans");
        a.setLastName("Hölzel");

        marshaller.marshal(a, out);

        System.out.println(out.toString());
    }
}
