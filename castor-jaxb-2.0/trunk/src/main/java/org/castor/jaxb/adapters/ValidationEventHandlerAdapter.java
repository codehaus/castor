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
package org.castor.jaxb.adapters;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Adapter between Castor (which uses org.xml.sax.ErrorHandler) and 
 * JAXB (javax.xml.bin.ValidationEventHandler).
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class ValidationEventHandlerAdapter implements ErrorHandler {

    /**
     * The JAXB ValidationEventHandler instance to forward events to.
     */
    private ValidationEventHandler _handler;

    /**
     * Empty default constructor.
     */
    protected ValidationEventHandlerAdapter() {
        super();
    }

    /**
     * ErrorHandler callback method.
     * @param e SAX parser exception that had been thrown
     */
    public void error(final SAXParseException e) {
        if (_handler != null) {
            _handler.handleEvent(getValidationEvent(ValidationEvent.ERROR, e));
        }
    }

    /**
     * ErrorHandler callback method.
     * @param e SAX parser exception that had been thrown
     */
    public void fatalError(final SAXParseException e) {
        if (_handler != null) {
            _handler.handleEvent(getValidationEvent(ValidationEvent.FATAL_ERROR, e));
        }
    }

    /**
     * ErrorHandler callback method.
     * @param e SAX parser exception that had been thrown
     */
    public void warning(final SAXParseException e) {
        if (_handler != null) {
            _handler.handleEvent(getValidationEvent(ValidationEvent.WARNING, e));
        }
    }

    /**
     * Get the ValidationEventHandler which is called by this
     * adapter.
     * @return the ValidationEventHandler to call by this adapter
     */
    public ValidationEventHandler getHandler() {
        return _handler;
    }

    /**
     * To set the ValidationEventHandler which should be called
     * by this adapter.
     * @param handler the ValidationEventHandler to call
     */
    public void setHandler(final ValidationEventHandler handler) {
        this._handler = handler;
    }

    /**
     * @param severity the severity to use
     * @param parseException the parse exception to take the information from
     * @return a JXB conform ValidationEvent
     */
    private ValidationEvent getValidationEvent(
            final int severity,
            final SAXParseException parseException) {
        CastorValidationEvent validationEvent = new CastorValidationEvent(severity);
        validationEvent.setMessage(parseException.getMessage());
        validationEvent.setLinkedException(parseException);
        validationEvent.setValidationEventLocator(getValidationEventLocator(parseException));
        return validationEvent;
    }

    /**
     * @param parseException the parse exception to take the information from
     * @return a JXB conform ValidationEventLocator
     */
    private ValidationEventLocator getValidationEventLocator(
            final SAXParseException parseException) {
        CastorValidationEventLocator locator = new CastorValidationEventLocator();
        locator.setLineNumber(parseException.getLineNumber());
        locator.setColumnNumber(parseException.getColumnNumber());
        return locator;
    }
}
