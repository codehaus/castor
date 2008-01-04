/*
 * Copyright 2007 Werner Guttmann
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
package org.castor.spring.xml;

public class ValidationException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4680953719672281309L;

    /**
     * Creates a default instance of this class.
     */
    public ValidationException() {
        super();
    }

    /**
     * Creates an instance of this class, with error message and initial cause provided
     * @param message The original error message
     * @param cause The original error cause.
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an instance of this class, with root error message provided.
     * @param message The original error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates an instance of this class, with root initial cause provided
     * @param cause The original error cause.
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

}
