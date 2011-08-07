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
package org.castor.jaxb.naming;

import org.junit.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class JAXBXmlNamingTest {
    /** 
     * Logger to be used.
     */
    private static final Log LOG = LogFactory.getLog(JAXBXmlNamingTest.class);

    /** Object to test. */
    private JAXBXmlNaming _xmlNaming;

    /**
     * {@inheritDoc}
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() {
        _xmlNaming = new JAXBXmlNaming();
    }

    /**
     * The createXMLName is not supported in the JAXB implementation...
     * so we expect to get {@link UnsupportedOperationException} in any
     * case.
     */
    @Test
    public void testCreateXMLName() {
        try {
            _xmlNaming.createXMLName(null);
            Assert.fail("Must not complete successfully!");
        } catch (UnsupportedOperationException e) {
            // expected exception -> fine!
        }
        try {
            _xmlNaming.createXMLName(String.class);
            Assert.fail("Must not complete successfully!");
        } catch (UnsupportedOperationException e) {
            // expected exception -> fine!
        }
        try {
            _xmlNaming.createXMLName(JAXBXmlNaming.class);
            Assert.fail("Must not complete successfully!");
        } catch (UnsupportedOperationException e) {
            // expected exception -> fine!
        }
    }

    @Test
    public void testToXMLName() {
        Assert.assertNull("Null in -> null out", _xmlNaming.toXMLName(null));
        Assert.assertEquals("x", _xmlNaming.toXMLName("x"));
        Assert.assertEquals("JAXBXmlNamingTest", _xmlNaming.toXMLName("JAXBXmlNamingTest"));
        Assert.assertEquals("Hugo", _xmlNaming.toXMLName("Hugo"));
        Assert.assertEquals("HugoFranz", _xmlNaming.toXMLName("HugoFranz"));
        Assert.assertEquals("A", _xmlNaming.toXMLName("A"));
    }
}
