/*
 * Copyright 2008 Joachim Grueneis
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

import java.net.URL;

import javax.xml.bind.ValidationEventLocator;

import org.w3c.dom.Node;

/**
 * POJO to create a JAXB conform validation event locator.
 * @see javax.xml.bind.ValidationEventLocator
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class CastorValidationEventLocator implements ValidationEventLocator {
    /** Column number of parsed section causing the validation event. */
    private int _columnNumber;
    /** Line number of parsed section causing the validation event. */
    private int _lineNumber;

    /**
     * Default constructor.
     */
    protected CastorValidationEventLocator() {
        super();
    }

    /**
     * Get the column number of the validation event.
     * Taken from SAXParserException.
     * @return column number of validation event
     * @see javax.xml.bind.ValidationEventLocator#getColumnNumber()
     */
    public int getColumnNumber() {
        return _columnNumber;
    }

    /**
     * Get the line number of the validation event.
     * Taken from SAXParserException.
     * @return the line number of the validation event
     * @see javax.xml.bind.ValidationEventLocator#getLineNumber()
     */
    public int getLineNumber() {
        return _lineNumber;
    }

    /**
     * Not supported.
     * @return null
     * @see javax.xml.bind.ValidationEventLocator#getNode()
     */
    public Node getNode() {
        return null;
    }

    /**
     * Not supported.
     * @return null
     * @see javax.xml.bind.ValidationEventLocator#getObject()
     */
    public Object getObject() {
        return null;
    }

    /**
     * Not supported.
     * @return 0
     * @see javax.xml.bind.ValidationEventLocator#getOffset()
     */
    public int getOffset() {
        return 0;
    }

    /**
     * Not supported.
     * @return null
     * @see javax.xml.bind.ValidationEventLocator#getURL()
     */
    public URL getURL() {
        return null;
    }

    /**
     * @param columnNumber the columnNumber to set
     */
    public void setColumnNumber(final int columnNumber) {
        _columnNumber = columnNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    protected void setLineNumber(final int lineNumber) {
        _lineNumber = lineNumber;
    }
}
