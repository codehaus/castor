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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.io.Writer;

import static junit.framework.Assert.assertNotNull;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

/**
 * Tests the {@link CastorJAXBContext} class.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class CastorJAXBContextTest {

    /**
     * Represents the instance of the tested class.
     */
    private JAXBContext context;

    /**
     * Sets up the test environment.
     *
     * @throws javax.xml.bind.JAXBException
     *             if any error occurs
     */
    @Before
    public void setUp() throws JAXBException {

        context = JAXBContext.newInstance(Entity.class);
    }

    /**
     * Tests the {@link JAXBContext#createMarshaller()} method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testCreateMarshaller() throws Exception {

        Marshaller marshaller = context.createMarshaller();

        assertNotNull("JAXBContext created null marshaller.", marshaller);
    }

    /**
     * Tests the {@link javax.xml.bind.JAXBContext#createUnmarshaller()} method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testCreateUnmarshaller() throws Exception {

        Unmarshaller unmarshaller = context.createUnmarshaller();

        assertNotNull("JAXBContext created null unmarshaller.", unmarshaller);
    }

    /**
     * Tests the {@link javax.xml.bind.JAXBContext#createJAXBIntrospector()} method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testCreateJAXBIntrospector() throws Exception {

        JAXBIntrospector jaxbIntrospector = context.createJAXBIntrospector();

        assertNotNull("JAXBContext created null introspector.", jaxbIntrospector);
    }
}
