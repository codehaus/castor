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
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link CastorJAXBContextFactory} class.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class CastorJAXBContextFactoryTest {

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(String, ClassLoader, java.util.Map)}
     * method when contextPath is null.
     * <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContextContextPathNull1() throws Exception {

        CastorJAXBContextFactory.createContext(null,
                        ClassLoader.getSystemClassLoader(), new HashMap<String, Object>());
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(String, ClassLoader, java.util.Map)}
     * method when classloader is null.
     * <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContextContextPathNull2() throws Exception {

        CastorJAXBContextFactory.createContext("org.castor.jaxb.entities",
                        null, new HashMap<String, Object>());
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(String, ClassLoader, java.util.Map)}
     * method when properties is null.
     * <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContextContextPathNull3() throws Exception {

        CastorJAXBContextFactory.createContext("org.castor.jaxb.entities",
                        ClassLoader.getSystemClassLoader(), null);
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(String, ClassLoader, java.util.Map)}
     * method when contextPath is empty.
     * <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContextContextPathEmpty() throws Exception {

        CastorJAXBContextFactory.createContext(" ",
                        ClassLoader.getSystemClassLoader(), new HashMap<String, Object>());
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(String, ClassLoader, java.util.Map)}
     * method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testCreateContextContextPath() throws Exception {

        JAXBContext jaxbContext = CastorJAXBContextFactory.createContext("org.castor.entities",
                ClassLoader.getSystemClassLoader(), new HashMap<String, Object>());

        assertNotNull("CastorJAXBContextFactory created null context.", jaxbContext);
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(Class[], java.util.Map)}
     * method when classes is null.
     * <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContextClassNull1() throws Exception {

        CastorJAXBContextFactory.createContext(null, new HashMap<String, Object>());
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(Class[], java.util.Map)}
     * method when properties is null.
     * <p/>
     * {@link IllegalArgumentException} is expected.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContextClassNull2() throws Exception {

        CastorJAXBContextFactory.createContext(new Class[] {Entity.class}, null);
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(Class[], java.util.Map)}
     * method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testCreateContextClass() throws Exception {

        JAXBContext jaxbContext =
                CastorJAXBContextFactory.createContext(new Class[] {Entity.class}, new HashMap<String, Object>());

        assertNotNull("CastorJAXBContextFactory created null context.", jaxbContext);
    }

    /**
     * Tests the {@link CastorJAXBContextFactory#createContext(Class[], java.util.Map)}
     * method.
     *
     * @throws Exception
     *             if any error occurs during test
     */
    @Test
    public void testRegisterContextFactory() throws Exception {

        CastorJAXBContextFactory.registerContextFactory();

        JAXBContext jaxbContext = JAXBContext.newInstance(NotMappedEntity.class);

        assertNotNull("CastorJAXBContextFactory created null context.", jaxbContext);
        assertTrue("CastorJAXBContextFactory was not registered.", jaxbContext instanceof CastorJAXBContext);
    }
}
