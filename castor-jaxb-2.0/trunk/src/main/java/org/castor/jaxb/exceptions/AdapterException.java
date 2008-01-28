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
package org.castor.jaxb.exceptions;

/**
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 *
 */
public class AdapterException extends CastorJAXBException {

    /**
     * @param message
     */
    public AdapterException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public AdapterException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public AdapterException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
