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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link CastorJAXBContext} class.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
public class CastorJAXBIntrospectorTest {

     /**
     * Represents the instance of the tested class.
     */
    private JAXBIntrospector introspector;

    /**
     * Sets up the test environment.
     *
     * @throws javax.xml.bind.JAXBException
     *             if any error occurs
     */
    @Before
    public void setUp() throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Entity.class);
        introspector = context.createJAXBIntrospector();
    }

    /**
     * Tests the {@link JAXBIntrospector#isElement(Object)} method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testIsElementTrue() throws Exception {

        boolean result = introspector.isElement(new Entity());

        assertTrue("Method isElement returned false, when true was expected.", result);
    }

    /**
     * Tests the {@link JAXBIntrospector#isElement(Object)} method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    @Ignore("Does not work yet.")
    public void testIsElementFalse() throws Exception {

        boolean result = introspector.isElement(new NotMappedEntity());

        assertFalse("Method isElement returned true, when false was expected.", result);
    }

    /**
     * Tests the {@link JAXBIntrospector#getElementName(Object)} method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testGetElementName() throws Exception {

        QName name = introspector.getElementName(new Entity());

        assertEquals("Element has invalid name.", "Entity", name.getLocalPart());
    }

    /**
     * Tests the {@link JAXBIntrospector#getElementName(Object)} method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    @Ignore("Does not work yet.")
    public void testGetElementNameNull() throws Exception {

        QName name = introspector.getElementName(new NotMappedEntity());

        assertNull("Null was expected.", name);
    }
}
