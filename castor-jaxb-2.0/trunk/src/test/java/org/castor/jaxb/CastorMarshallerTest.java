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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.*;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.castor.entities.Entity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Tests the {@link CastorMarshaller} class.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/castor-jaxb-test-context.xml"})
public class CastorMarshallerTest {

    /**
     * Represents the expected xml.
     */
    private static final String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<Entity><Name>Test</Name></Entity>";

    /**
     * Represents the path to the schema file.
     */
    private static final String SCHEMA_FILE = "/org/castor/entities/Entity.xsd";

    /**
     * Represents the path to the schema file.
     */
    private static final String INVALID_SCHEMA_FILE = "/org/castor/entities/InvalidEntity.xsd";

    /**
     * Represents the instance of the tested class.
     */
    private Marshaller marshaller;

    /**
     * Sets up the test environment.
     *
     * @throws JAXBException if any error occurs
     */
    @Before
    public void setUp() throws JAXBException {

        JAXBContext context = JAXBContext.newInstance("org.castor.entities");
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

        marshallWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallWriterWithValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallWriterWithValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallWriterJAXBElement() throws Exception {

        marshallWriter(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallWriterJAXBElementWithValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallWriter(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    @SuppressWarnings("unchecked")
    public void testMarshallWriterJAXBElementWithValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallWriter(createJAXBEntity());
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

        marshallOutputStream(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallOutputStreamWithValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallOutputStream(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method when validation fails. <p/>
     * {@link JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallOutputStreamWithValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallOutputStream(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallOutputStreamJAXBElement() throws Exception {

        marshallOutputStream(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallOutputStreamJAXBElementWithValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallOutputStream(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method when validation fails. <p/>
     * {@link JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallOutputStreamJAXBElementWithValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallOutputStream(createJAXBEntity());
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

        marshallXMLStreamWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLStreamWriterValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallXMLStreamWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallXMLStreamWriterValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallXMLStreamWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLStreamWriterJAXBElement() throws Exception {

        marshallXMLStreamWriter(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLStreamWriterJAXBElementWithValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallXMLStreamWriter(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLStreamWriter)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallXMLStreamWriterJAXBElementWithValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallXMLStreamWriter(createJAXBEntity());
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

        marshallXMLEventWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLEventWriterValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallXMLEventWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallXMLEventWriterValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallXMLEventWriter(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLEventWriterJAXBElement() throws Exception {

        marshallXMLEventWriter(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallXMLEventWriterJAXBElementValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallXMLEventWriter(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, XMLEventWriter)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallXMLEventWriterJAXBElementValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallXMLEventWriter(createJAXBEntity());
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

        marshallNode(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallNodeWithValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallNode(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method when validation fails. <p/> {@link JAXBException}
     * if any error occurs.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallNodeWithValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallNode(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallNodeJAXBElement() throws Exception {

        marshallNode(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallNodeJAXBElementValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallNode(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, Node)} method when validation fails. <p/> {@link JAXBException}
     * is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallNodeJAXBElementValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallNode(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method. </p>
     *
     * @throws IllegalArgumentException is expected.
     * @throws Exception                if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMarshallResultNull1() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(null, new DOMResult(document));
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method. </p>
     *
     * @throws IllegalArgumentException is expected.
     * @throws Exception                if any error occurs during test
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

        marshallDOMResult(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallDOMResultValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallDOMResult(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method when validation fails. <p/>
     * {@link JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallDOMResultValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallDOMResult(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallDOMResultJAXBElement() throws Exception {

        marshallDOMResult(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallDOMResultJAXBElementWithValidation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallDOMResult(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, javax.xml.transform.Result)} method when validation fails. <p/>
     * {@link JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallDOMResultJAXBElementWithValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallDOMResult(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResult1() throws Exception {

        marshallStreamResult(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResult1Validation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallStreamResult(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallStreamResult1ValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallStreamResult(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResultJAXBElement1() throws Exception {

        marshallStreamResult(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResultJAXBElement1Validation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallStreamResult(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method when validation fails. <p/> {@link
     * JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallStreamResultJAXBElement1ValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallStreamResult(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResult2() throws Exception {

        marshallStreamResult2(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResult2Validation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallStreamResult2(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method when validation fails.
     * <p/>
     * {@link JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallStreamResult2ValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallStreamResult2(createEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResultJAXBElement2() throws Exception {

        marshallStreamResult2(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testMarshallStreamResultJAXBElement2Validation() throws Exception {

        marshaller.setSchema(loadSchema(SCHEMA_FILE));
        marshallStreamResult2(createJAXBEntity());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.OutputStream)} method when validation fails.
     * <p/>
     * {@link JAXBException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = JAXBException.class)
    public void testMarshallStreamResultJAXBElement2ValidationError() throws Exception {

        marshaller.setSchema(loadSchema(INVALID_SCHEMA_FILE));
        marshallStreamResult2(createJAXBEntity());
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

    /**
     * Creates a {@link Entity} instance wrapped into a {@link JAXBElement} object.
     *
     * @return wrapped {@link Entity} instance
     */
    private JAXBElement createJAXBEntity() {

        return createJAXBElement(createEntity());
    }

    /**
     * Loads the schema for the {@link Entity} class.
     *
     * @param schemaFile the path to the schema file
     *
     * @return the loaded schema
     *
     * @throws SAXException if any error occurs during loading the schema
     */
    private Schema loadSchema(String schemaFile) throws SAXException {

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        return schemaFactory.newSchema(getClass().getResource(schemaFile));
    }

    /**
     * Marshals the object into a {@link Writer}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallWriter(Object entity) throws Exception {
        StringWriter writer = new StringWriter();
        marshaller.marshal(entity, writer);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Marshals the object into a {@link OutputStream}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallOutputStream(Object entity) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(entity, outputStream);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, new String(outputStream.toByteArray(),
                "UTF-8"));
    }

    /**
     * Marshals the object into a {@link XMLStreamWriter}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallXMLStreamWriter(Object entity) throws Exception {
        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(writer);
        marshaller.marshal(entity, xmlStreamWriter);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Marshals the object into a {@link XMLEventWriter}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallXMLEventWriter(Object entity) throws Exception {
        StringWriter writer = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter xmlEventWriter = outputFactory.createXMLEventWriter(writer);
        marshaller.marshal(entity, xmlEventWriter);

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Marshals the object into a {@link Node}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallNode(Object entity) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(entity, document);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Marshals the object into a {@link DOMResult}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallDOMResult(Object entity) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node document = builder.newDocument();

        marshaller.marshal(entity, new DOMResult(document));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Marshals the object into a {@link StreamResult}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallStreamResult(Object entity) throws Exception {
        StringWriter writer = new StringWriter();
        marshaller.marshal(entity, new StreamResult(writer));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML, writer.toString());
    }

    /**
     * Marshals the object into a {@link StreamResult}.
     *
     * @param entity the object to marshall
     *
     * @throws Exception if any error occurs during marshalling
     */
    private void marshallStreamResult2(Object entity) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(entity, new StreamResult(outputStream));

        assertXMLEqual("Marshaller written invalid result.", EXPECTED_XML,
                new String(outputStream.toByteArray(), "UTF-8"));
    }
}
