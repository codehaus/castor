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

import org.castor.entities.Entity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.StringWriter;

/**
 * Tests the {@link CastorMarshaller} class.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorMarshallerTest {

    /**
     * Represents the instance of the tested class.
     */
    private javax.xml.bind.Marshaller marshaller;

    /**
     * Sets up the test environment.
     *
     * @throws JAXBException if any error occurs
     */
    @Before
    public void setUp() throws JAXBException {

        javax.xml.bind.JAXBContext context = org.castor.jaxb.JAXBContext.newInstance(Entity.class);
        marshaller = context.createMarshaller();
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws JAXBException if any error occurs during test
     */
    @Test
    public void testMarshallWriter() throws JAXBException {

        Entity entity = new Entity();
        entity.setName("name");

        StringWriter writer = new StringWriter();
        marshaller.marshal(entity, writer);

        System.out.println(writer.toString());
    }

    /**
     * Tests the {@link CastorMarshaller#marshal(Object, java.io.Writer)} method.
     *
     * @throws JAXBException if any error occurs during test
     */
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void testMarshallWriterJAXBElement() throws JAXBException {

        Entity entity = new Entity();
        entity.setName("name");

        StringWriter writer = new StringWriter();
        marshaller.marshal(new JAXBElement(new QName(""), entity.getClass(), entity), writer);

        System.out.println(writer.toString());
    }
}
