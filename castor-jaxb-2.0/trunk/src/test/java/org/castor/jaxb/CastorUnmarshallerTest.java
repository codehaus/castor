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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.castor.entities.Entity;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Tests the {@link CastorUnmarshaller} class.
 */
public class CastorUnmarshallerTest {

    /**
     * Represents the instance of the tested class.
     */
    private Unmarshaller unmarshaller;

    /**
     * Represents the input xml.
     */
    private static final String INPUT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Entity><Name>Test</Name></Entity>";

    /**
     * Sets up the test environment.
     *
     * @throws javax.xml.bind.JAXBException if any error occurs
     */
    @Before
    public void setUp() throws JAXBException {

        JAXBContext context = JAXBContext.newInstance("org.castor.entities");
        unmarshaller = context.createUnmarshaller();
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(java.io.Reader)} method when reader is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalReaderNull() throws Exception {
        unmarshaller.unmarshal((Reader) null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(java.io.Reader)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalReader() throws Exception {
        Entity entity = (Entity) unmarshaller.unmarshal(new StringReader(INPUT_XML));
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(java.io.InputStream)} method when inputStream is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalInputStreamNull() throws Exception {
        unmarshaller.unmarshal((InputStream) null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(java.io.InputStream)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalInputStream() throws Exception {
        Entity entity = (Entity) unmarshaller.unmarshal(new ByteArrayInputStream(INPUT_XML.getBytes()));
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(InputSource)} method, when inputSource is null.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalInputSourceNull() throws Exception {
        Entity entity = (Entity) unmarshaller.unmarshal((InputSource) null);
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(InputSource)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalInputSource() throws Exception {
        Entity entity = (Entity) unmarshaller.unmarshal(new InputSource(new StringReader(INPUT_XML)));
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Node)} method when node is null. </p> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalNodeNull() throws Exception {
        unmarshaller.unmarshal((Node) null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Node)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalNode() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Node document = builder.parse(new InputSource(new StringReader(INPUT_XML)));

        Entity entity = (Entity) unmarshaller.unmarshal(document);
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(org.w3c.dom.Node, Class)} method when node is null.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalNodeJAXBElementNull1() throws Exception {
        unmarshaller.unmarshal((Node) null, Entity.class);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(org.w3c.dom.Node, Class)} method when declaredType is null.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalNodeJAXBElementNull2() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Node document = builder.parse(new InputSource(new StringReader(INPUT_XML)));

        unmarshaller.unmarshal(document, null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(org.w3c.dom.Node, Class)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalNodeJAXBElement() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Node document = builder.parse(new InputSource(new StringReader(INPUT_XML)));

        JAXBElement<Entity> jaxbElement = unmarshaller.unmarshal(document, Entity.class);
        testJAXBElement(jaxbElement);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLStreamReader)} method when reader is null. </p> {@link
     * IllegalArgumentException} is null.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalXMLStreamReaderNull() throws Exception {
        unmarshaller.unmarshal((XMLStreamReader) null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLStreamReader)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalXMLStreamReader() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new StringReader(INPUT_XML));

        Entity entity = (Entity) unmarshaller.unmarshal(xmlStreamReader);
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLStreamReader, Class)} method when reader is null. <p/> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalXMLStreamReaderJAXBElementNull1() throws Exception {
        unmarshaller.unmarshal((XMLStreamReader) null, Entity.class);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLStreamReader, Class)} method when declared type is null. <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalXMLStreamReaderJAXBElementNull2() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new StringReader(INPUT_XML));

        unmarshaller.unmarshal(xmlStreamReader, null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLStreamReader, Class)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalXMLStreamReaderJAXBElement() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new StringReader(INPUT_XML));

        JAXBElement<Entity> entity = unmarshaller.unmarshal(xmlStreamReader, Entity.class);
        testJAXBElement(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLEventReader)} method when reader is null. </p> {@link
     * IllegalArgumentException} is null.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalXMLEventReaderNull() throws Exception {
        unmarshaller.unmarshal((XMLEventReader) null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLEventReader)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalXMLEventReader() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(new StringReader(INPUT_XML));

        Entity entity = (Entity) unmarshaller.unmarshal(xmlEventReader);
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLEventReader, Class)} method when reader is null. <p/> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalXMLEventReaderJAXBElementNull1() throws Exception {
        unmarshaller.unmarshal((XMLEventReader) null, Entity.class);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLEventReader, Class)} method when declared type is null. <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalXMLEventReaderJAXBElementNull2() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(new StringReader(INPUT_XML));

        unmarshaller.unmarshal(xmlEventReader, null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(XMLEventReader, Class)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalXMLEventReaderJAXBElement() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(new StringReader(INPUT_XML));

        JAXBElement<Entity> entity = unmarshaller.unmarshal(xmlEventReader, Entity.class);
        testJAXBElement(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source)} method when source is null. <p/> {@link
     * IllegalArgumentException} is expected.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalSourceNull() throws Exception {
        unmarshaller.unmarshal((Source) null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalDOMSource() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Node document = builder.parse(new InputSource(new StringReader(INPUT_XML)));

        Entity entity = (Entity) unmarshaller.unmarshal(new DOMSource(document));
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalStreamSourceReader() throws Exception {
        Entity entity = (Entity) unmarshaller.unmarshal(new StreamSource(new StringReader(INPUT_XML)));
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalStreamSourceInputStream() throws Exception {
        Entity entity = (Entity) unmarshaller.unmarshal(new ByteArrayInputStream(INPUT_XML.getBytes()));
        testEntity(entity);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source)} method when source is null.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalSourceNullJAXBElementNull1() throws Exception {
        unmarshaller.unmarshal((Source) null, Entity.class);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source)} method when declared type is null.
     *
     * @throws Exception if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshalSourceNullJAXBElementNull2() throws Exception {
        unmarshaller.unmarshal(new StreamSource(new StringReader(INPUT_XML)), null);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source, Class)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalDOMSourceJAXBElement() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Node document = builder.parse(new InputSource(new StringReader(INPUT_XML)));

        JAXBElement<Entity> jaxbElement = unmarshaller.unmarshal(new DOMSource(document), Entity.class);
        testJAXBElement(jaxbElement);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source, Class)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalStreamSourceReaderJAXBElement() throws Exception {
        JAXBElement<Entity> jaxbElement = unmarshaller.unmarshal(new StreamSource(new StringReader(INPUT_XML)),
                Entity.class);
        testJAXBElement(jaxbElement);
    }

    /**
     * Tests the {@link CastorUnmarshaller#unmarshal(Source, Class)} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testUnmarshalStreamSourceInputStreamJAXBElement() throws Exception {
        JAXBElement<Entity> jaxbElement = unmarshaller.unmarshal(new StreamSource(
                new ByteArrayInputStream(INPUT_XML.getBytes())), Entity.class);
        testJAXBElement(jaxbElement);
    }

    /**
     * Tests the {@link CastorUnmarshaller#getUnmarshallerHandler()} method.
     *
     * @throws Exception if any error occurs during test
     */
    @Test
    public void testGetUnmarshallHandler() throws Exception {
        UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        XMLReader xmlReader = spf.newSAXParser().getXMLReader();
        xmlReader.setContentHandler(unmarshallerHandler);
        xmlReader.parse(new InputSource(new StringReader(INPUT_XML)));

        Entity entity = (Entity) unmarshallerHandler.getResult();
        testEntity(entity);
    }

    /**
     * Tests the passed {@link Entity} instance.
     *
     * @param entity the {@link Entity} instance
     */
    private void testEntity(Entity entity) {
        assertNotNull("Entity can not be null.", entity);
        assertEquals("Entity has invalid name.", "Test", entity.getName());
    }

    /**
     * Tests the passed {@link JAXBElement}.
     *
     * @param jaxbElement the {@link JAXBElement} to test
     */
    private void testJAXBElement(JAXBElement<Entity> jaxbElement) {
        assertNotNull("Entity can not be null.", jaxbElement);
        testEntity(jaxbElement.getValue());
    }
}
