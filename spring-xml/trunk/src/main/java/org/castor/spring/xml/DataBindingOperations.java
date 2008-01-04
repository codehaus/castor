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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.xml.sax.InputSource;

public interface DataBindingOperations {

    /**
     * Marshalls the given object to its XML representation, and streams it to 
     * the Writer instance provided.
     * @param object The Object instance to be marshalled.
     * @param writer The Writer instance to capture the XML representation.
     * @throws MarshallingException If the marshalling fails
     * @throws IOException If there's a problem with accessing the writer instance.
     */
    void marshal(Object object, Writer writer) 
    throws MarshallingException, IOException, ValidationException;
    
    /**
     * Unmarshalls the given XML data into its object equivalent, taking the XML
     * data from the InputSource provided.
     * @param inputSource A SAX InputSource.
     * @param type The expected object type.
     * @return An object instance, as a resulf of the unmarshalling.
     * @throws MarshallingException If there's a problem with unmarshalling
     * @throws IOException If there's a problem with accessing the InputSource
     */
    Object unmarshal(InputSource inputSource, Class type)
    throws MarshallingException, IOException, ValidationException;

//    /**
//     * Unmarshalls the given XML data into its object equivalent, taking the XML
//     * data from the InputSource provided.
//     * @param saxSource A SAXSource instance, providing the XML to unmarshal..
//     * @param type The expected object type.
//     * @return An object instance, as a resulf of the unmarshalling.
//     * @throws MarshallingException If there's a problem with unmarshalling
//     * @throws IOException If there's a problem with accessing the InputSource
//     */
//    Object unmarshal(SAXSource saxSource, Class type)
//    throws MarshallingException, IOException, ValidationException;

//    /**
//     * Unmarshalls the given XML data into its object equivalent, taking the XML
//     * data from the InputSource provided.
//     * @param domSource A DOMSource instance, providing the XML to unmarshal..
//     * @param type The expected object type.
//     * @return An object instance, as a resulf of the unmarshalling.
//     * @throws MarshallingException If there's a problem with unmarshalling
//     * @throws IOException If there's a problem with accessing the InputSource
//     */
//    Object unmarshal(DOMSource domSource, Class type)
//    throws MarshallingException, IOException, ValidationException;

    /**
     * Unmarshalls the given XML data into its object equivalent, taking the XML
     * data from the InputSource provided.
     * @param reader An java.io.Reader instance, providing the XML to unmarshal.
     * @param type The expected object type.
     * @return An object instance, as a resulf of the unmarshalling.
     * @throws MarshallingException If there's a problem with unmarshalling
     * @throws IOException If there's a problem with accessing the InputSource
     */
    Object unmarshal(Reader reader, Class type)
    throws MarshallingException, IOException, ValidationException;

}
