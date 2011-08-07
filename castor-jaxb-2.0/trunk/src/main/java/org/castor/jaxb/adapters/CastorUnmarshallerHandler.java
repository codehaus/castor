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

package org.castor.jaxb.adapters;

import org.castor.jaxb.CastorJAXBUtils;
import org.exolab.castor.xml.UnmarshalHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;

/**
 * Implementation of {@link UnmarshallerHandler} that internally delegates to Castor {UnmarshalHandler}.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorUnmarshallerHandler implements UnmarshallerHandler {

    /**
     * The {@link UnmarshalHandler} used internally by this class.
     */
    private final UnmarshalHandler unmarshalHandler;

    /**
     * Creates new instance of {@link CastorUnmarshallerHandler} class.
     *
     * @param unmarshalHandler the {@link UnmarshalHandler} to use
     *
     * @throws IllegalArgumentException if unmarshalHandler is null
     */
    public CastorUnmarshallerHandler(UnmarshalHandler unmarshalHandler) {
        // checks the input
        CastorJAXBUtils.checkNotNull(unmarshalHandler, "unmarshalHandler");

        // sets the handler
        this.unmarshalHandler = unmarshalHandler;
    }

    /**
     * {@inheritDoc}
     */
    public Object getResult() throws JAXBException, IllegalStateException {
        return unmarshalHandler.getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentLocator(Locator locator) {
        unmarshalHandler.setDocumentLocator(locator);
    }

    /**
     * {@inheritDoc}
     */
    public void startDocument() throws SAXException {
        unmarshalHandler.startDocument();
    }

    /**
     * {@inheritDoc}
     */
    public void endDocument() throws SAXException {
        unmarshalHandler.endDocument();
    }

    /**
     * {@inheritDoc}
     */
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        unmarshalHandler.startPrefixMapping(prefix, uri);
    }

    /**
     * {@inheritDoc}
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        unmarshalHandler.endPrefixMapping(prefix);
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        unmarshalHandler.startElement(uri, localName, qName, atts);
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        unmarshalHandler.endElement(uri, localName, qName);
    }

    /**
     * {@inheritDoc}
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        unmarshalHandler.characters(ch, start, length);
    }

    /**
     * {@inheritDoc}
     */
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        unmarshalHandler.ignorableWhitespace(ch, start, length);
    }

    /**
     * {@inheritDoc}
     */
    public void processingInstruction(String target, String data) throws SAXException {
        unmarshalHandler.processingInstruction(target, data);
    }

    /**
     * {@inheritDoc}
     */
    public void skippedEntity(String name) throws SAXException {
        unmarshalHandler.skippedEntity(name);
    }
}
