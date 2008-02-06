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
import javax.xml.bind.ValidationEventLocator;

/**
 * POJO to create a JAXB conform validation event.
 * @see javax.xml.bind.ValidationEvent
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class CastorValidationEvent implements ValidationEvent {

    /**
     * Warning severity.
     */
    private int _severity;
    
    /**
     * Message of validation error.
     */
    private String _message;

    /**
     * Exception of validation error.
     */
    private Throwable _linkedException;

    /**
     * ValidationEventLocator.
     */
    private ValidationEventLocator _validationEventLocator;

    /**
     * Constructor which forces to set severity.
     * @param severity ValidationEvent severity constant
     */
    protected CastorValidationEvent(final int severity) {
        this._severity = severity;
    }

    /**
     * Get the linked exception of the event.
     * @return linked exception
     */
    public Throwable getLinkedException() {
        return _linkedException;
    }

    /**
     * Locator of validation event. Currently not supported!
     * @return null as this is not yet supported
     */
    public ValidationEventLocator getLocator() {
        return _validationEventLocator;
    }

    /**
     * Message of the validation event. In Castor this
     * message is taken from the SAXParserException.
     * @return message of SAXParserException
     */
    public String getMessage() {
        return this._message;
    }

    /**
     * Get severity ValidationEvent constant.
     * @return ValidationEvent constant
     */
    public int getSeverity() {
        return this._severity;
    }

    /**
     * To set the validation event message. In Castor set
     * from SAXParserException.
     * @param message validation event message
     */
    public void setMessage(final String message) {
        this._message = message;
    }

    /**
     * Builds string from severity and message.
     * @return String from severity and message
     */
    public String toString() {
        return _severity + "/" + _message;
    }

    /**
     * Set linkedException.
     * @param linkedException linkedException
     */
    public void setLinkedException(final Throwable linkedException) {
        _linkedException = linkedException;
    }

    /**
     * Set validationEventLocator.
     * @param validationEventLocator validationEventLocator
     */
    public void setValidationEventLocator(final ValidationEventLocator validationEventLocator) {
        _validationEventLocator = validationEventLocator;
    }

}