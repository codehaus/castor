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

import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.JaxbFieldNature;

/**
 * The reflection of classes had an error which can not be recovered.
 *
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 *
 */
public class ReflectionException extends CastorJAXBException {

    /**
     * Exception default constructor.
     * @param classInfo fieldInfo
     * @param message message
     */
    public ReflectionException(final ClassInfo classInfo, final String message) {
        super(message);
    }

    /**
     * Exception default constructor.
     * @param fieldInfo fieldInfo
     * @param message message
     */
    public ReflectionException(final FieldInfo fieldInfo, final String message) {
        super(message);
    }
//
//    /**
//     * Exception default constructor.
//     * @param exception exception
//     */
//    public ReflectionException(final Throwable exception) {
//        super(exception);
//    }
//
//    /**
//     * Exception default constructor.
//     * @param message message
//     * @param exception exception
//     */
//    public ReflectionException(final String message, final Throwable exception) {
//        super(message, exception);
//    }

    public ReflectionException(final JaxbFieldNature jaxbFieldNature, final String message) {
        super(message);
    }
}
