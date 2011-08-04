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

import org.apache.commons.logging.Log;

import javax.xml.bind.JAXBException;

/**
 * A utility class that defines helper method uses in this component.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT com
 * @version 1.0
 */
public class CastorJAXBUtils {

    /**
     * Checks if passed parameter is not null, if it is then {@link IllegalArgumentException} is being thrown.
     *
     * @param param     the parameter to check
     * @param paramName the parameter name
     *
     * @throws IllegalArgumentException if param is null
     */
    public static void checkNotNull(Object param, String paramName) {

        if (param == null) {

            // throws IllegalArgumentException if param is null
            throw new IllegalArgumentException(String.format("Argument '%s' can not be null.", paramName));
        }
    }

    /**
     * Checks if passed parameter is not null and not empty string, if it is then {@link IllegalArgumentException} is
     * being thrown.
     *
     * @param param     the parameter to check
     * @param paramName the parameter name
     *
     * @throws IllegalArgumentException if param is null or empty
     */
    public static void checkNotEmpty(String param, String paramName) {

        if (param.trim().length() == 0) {

            // throws IllegalArgumentException if param is empty string
            throw new IllegalArgumentException(String.format("Argument '%s' can not be empty string.", paramName));
        }
    }

    /**
     * Coverts the passed exception into a new instance of {@link javax.xml.bind.JAXBException}.
     *
     * @param msg the error message
     * @param e   the exception which was caused of the error
     *
     * @return JAXBException newly created {@link JAXBException} which wrapps the passed object
     */
    public static JAXBException convertToJAXBException(String msg, Throwable e) {

        // returns the newly created exception
        return new JAXBException(msg, e);
    }

    /**
     * Logs the message with debug level, this method performs checking if debug level is enabled before logging the
     * message.
     *
     * @param log the log to use
     * @param msg the log message
     */
    public static void logDebug(Log log, String msg) {

        if (log.isDebugEnabled()) {

            log.debug(msg);
        }
    }

    /**
     * Logs the message with error level.
     *
     * @param log the log to use
     * @param msg the log message
     * @param exc the exception to log
     */
    public static void logError(Log log, String msg, Throwable exc) {

        log.error(msg);
    }
}
