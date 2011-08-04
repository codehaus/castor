/*
 * Copyright 2011 Jakub Narloch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.castor.jaxb;

import org.castor.entities.Entity;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

/**
 * Tests the {@link CastorMarshaller} class.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorMarshallerTest {

    /**
     * Represents the instance of the tested class.
     */
    private javax.xml.bind.Marshaller marshaller;

    /**
     * Represents the expected xml.
     */
    private static final String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Entity><Name>Test</Name></Entity>";

    /**
     * Sets up the test environment.
     *
     * @throws JAXBException if any error occurs
     */
    @Before
    public void setUp() throws JAXBException {

        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Entity.class);
        marshaller = context.createMarshaller();
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method when entity is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallWriterNull1() throws Exception {

        marshaller.marshal(null, new StringWriter());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method when writer is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallWriterNull2() throws Exception {

        marshaller.marshal(createEntity(), (Writer) null);
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallWriter() throws Exception {

        Entity entity = createEntity();

        StringWriter writer = new StringWriter();
        marshaller.marshal(entity, writer);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallWriterJAXBElement() throws Exception {

        Entity entity = createEntity();

        StringWriter writer = new StringWriter();
        marshaller.marshal(createJAXBElement(entity), writer);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method when jaxbElement is null. </p>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallOutputStreamNull1() throws Exception {

        marshaller.marshal(null, new ByteArrayOutputStream());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method when jaxbElement is null. </p>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallOutputStreamNull2() throws Exception {

        marshaller.marshal(createEntity(), (OutputStream) null);
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallOutputStream() throws Exception {

        Entity entity = createEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(entity, outputStream);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                new String(outputStream.toByteArray(), "UTF-8"));
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallOutputStreamJAXBElement() throws Exception {

        Entity entity = createEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(createJAXBElement(entity), outputStream);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                new String(outputStream.toByteArray(), "UTF-8"));
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method when jaxbElement is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallXMLStreamWriterNull1() throws Exception {

        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        marshaller.marshal(null, outputFactory.createXMLStreamWriter(writer));
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method when writer is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallXMLStreamWriterNull2() throws Exception {

        marshaller.marshal(createEntity(), (XMLStreamWriter) null);
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLStreamWriter() throws Exception {

        Entity entity = createEntity();

        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(writer);
        marshaller.marshal(entity, xmlStreamWriter);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLStreamWriterJAXBElement() throws Exception {

        Entity entity = createEntity();

        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(writer);
        marshaller.marshal(createJAXBElement(entity), xmlStreamWriter);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method when jaxbElement is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallXMLEventWriterNull1() throws Exception {

        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        marshaller.marshal(null, outputFactory.createXMLEventWriter(writer));
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method when writer is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallXMLEventWriterNull2() throws Exception {

        marshaller.marshal(createEntity(), (XMLEventWriter) null);
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLEventWriter() throws Exception {

        Entity entity = createEntity();

        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter xmlEventWriter = outputFactory.createXMLEventWriter(writer);
        marshaller.marshal(entity, xmlEventWriter);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLEventWriterJAXBElement() throws Exception {

        Entity entity = createEntity();

        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter xmlEventWriter = outputFactory.createXMLEventWriter(writer);
        marshaller.marshal(createJAXBElement(entity), xmlEventWriter);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method when jaxbElement is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallNodeNull1() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        marshaller.marshal(null, builder.newDocument());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method when node is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallNodeNull2() throws Exception {

        marshaller.marshal(createEntity(), (Node) null);
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallNode() throws Exception {

        Entity entity = createEntity();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(entity, document);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallNodeJAXBElement() throws Exception {

        Entity entity = createEntity();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(createJAXBElement(entity), document);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method. </p> {@throws
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallResultNull1() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(null, new DOMResult(document));
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method. </p> {@throws
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallResultNull2() throws Exception {

        marshaller.marshal(createEntity(), (Result) null);
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallDOMResult() throws Exception {

        Entity entity = createEntity();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(entity, new DOMResult(document));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallDOMResultJAXBElement() throws Exception {

        Entity entity = createEntity();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(createJAXBElement(entity), new DOMResult(document));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResult1() throws Exception {

        Entity entity = createEntity();

        StringWriter writer = new StringWriter();
        marshaller.marshal(entity, new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResult2() throws Exception {

        Entity entity = createEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(entity, new StreamResult(outputStream));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                new String(outputStream.toByteArray(), "UTF-8"));
    }

    /**
     * Creates the instance of the {Entity}.
     *
     * @return a {@link Entity} instance used for testing
     */
    private Entity createEntity() {
        Entity entity = new Entity();
        entity.setName("Test");
        return entity;
    }

    /**
     * Creates the {@link JAXBElement} from passed object.
     *
     * @param obj the object to use
     *
     * @return the {@link JAXBElement} that wrapps the passed entity
     */
    private JAXBElement createJAXBElement(Object obj) {

        return new JAXBElement(new QName(""), obj.getClass(), obj);
    }
}
