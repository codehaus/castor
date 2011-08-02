package org.castor.jaxb;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;

import javax.xml.bind.JAXBException;

/**
 * A utility class that defines helper method uses in this component.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT com
 * @version $Id$
 */
class Utils {

    /**
     * Checks if passed parameter is not null, if it is then {@link IllegalArgumentException} is being thrown.
     *
     * @param log       the log to use
     * @param param     the parameter to check
     * @param paramName the parameter name
     *
     * @throws IllegalArgumentException if param is null
     */
    static void checkNotNull(Logger log, Object param, String paramName) {

        if (param == null) {

            if (log != null) {
                log.error(String.format("Argument '%s' can not be null.", paramName));
            }

            throw new IllegalArgumentException(String.format("Argument '%s' can not be null.", paramName));
        }
    }

    /**
     * Checks if passed parameter is not null and not empty string, if it is then {@link IllegalArgumentException} is being thrown.
     *
     * @param log       the log to use
     * @param param     the parameter to check
     * @param paramName the parameter name
     *
     * @throws IllegalArgumentException if param is null or empty
     */
    static void checkNotEmpty(Log log, String param, String paramName) {

        if (param.trim().length() == 0) {

            if (log != null) {
                log.error(String.format("Argument '%s' can not be empty string.", paramName));
            }

            throw new IllegalArgumentException(String.format("Argument '%s' can not be empty string.", paramName));
        }
    }

    /**
     * Coverts the passed exception into a new instance of {@link javax.xml.bind.JAXBException}.
     *
     * @param log the log to use
     * @param msg the error message
     * @param e   the exception which was caused of the error
     *
     * @return JAXBException newly created {@link JAXBException} which wrapps the passed object
     */
    static JAXBException convertToJAXBException(Logger log, String msg, Throwable e) {

        // log error message
        if (log != null) {
            log.error(msg, e);
        }

        // returns the newly created exception
        return new JAXBException(msg, e);
    }
}
