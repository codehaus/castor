/*
 * Copyright 2007 Joachim Grueneis
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
package org.castor.jaxb.resolver;

import org.castor.jaxb.reflection.ClassDescriptorBuilder;
import org.castor.jaxb.reflection.ClassInfoBuilder;
import org.castor.jaxb.reflection.JAXBClassDescriptorImpl;
import org.exolab.castor.xml.ResolverException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Testing the resolver command which is more or less a wrapper of
 * ClassDescriptorResolverBuilder. So only a few tests are found here to show
 * that the wrapping is fine - for detailed tests on descriptor building
 * 
 * @see org.castor.jaxb.reflection.ClassDescriptorBuilderTest .
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 */
public class JAXBClassResolverCommandTest {

    private JAXBClassResolverCommand _classResolverCommand;
    private Map < String, Object > _propertiesMap = 
        new HashMap < String, Object > ();

    /**
     * Correctly initializes JAXBClassResolverCommand.
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() {
        _classResolverCommand = new JAXBClassResolverCommand();
        _classResolverCommand.setClassInfoBuilder(new ClassInfoBuilder());
        _classResolverCommand
                .setClassDescriptorBuilder(new ClassDescriptorBuilder());
    }

    /**
     * Setting ClassDescriptorBuilder to null should fail.
     * {@link org.castor.jaxb.resolver.JAXBClassResolverCommand#setClassDescriptorBuilder(
     * org.castor.jaxb.reflection.ClassDescriptorBuilder)}.
     */
    @Test
    public void testSetClassDescriptorBuilderToNull() {
        try {
            _classResolverCommand.setClassDescriptorBuilder(null);
            Assert.fail("Setting ClassDescriptorBuilder to null should fail");
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }

    /**
     * Setting ClassInfoBuilder to null should fail.
     * {@link org.castor.jaxb.resolver.JAXBClassResolverCommand#setClassInfoBuilder(
     * org.castor.jaxb.reflection.ClassInfoBuilder)}.
     */
    @Test
    public void testSetClassInfoBuilderToNull() {
        try {
            _classResolverCommand.setClassInfoBuilder(null);
            Assert.fail("Setting ClassInfoBuilder to null should fail");
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }

    /**
     * Calling resolve with null should return an empty map.
     * {@link org.castor.jaxb.resolver.JAXBClassResolverCommand#resolve(
     * java.lang.String, java.util.Map)}.
     */
    @Test
    public void testResolveNull() {
        try {
            Map descriptorMap = _classResolverCommand.resolve(null, _propertiesMap);
            Assert.assertTrue("Resolving null should lead to an empty map", 
                    descriptorMap.isEmpty());
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        }
    }

    @XmlRootElement(name = "Artist")
    private class Artist {
        private String _name;
        @XmlElement(name = "Name")
        public final String getName() {
            return _name;
        }
        public final void setName(final String name) {
            _name = name;
        }
    }

    /**
     * Calling resolve with Artist class and check results.
     * {@link org.castor.jaxb.resolver.JAXBClassResolverCommand#resolve(
     * java.lang.String, java.util.Map)}.
     */
    @Test
    public void testResolveArtist() {
        try {
            Map descriptorMap = _classResolverCommand.resolve(
                    Artist.class.getName(), _propertiesMap);
            Assert.assertFalse("Resolving Artist should lead to a non empty map", 
                    descriptorMap.isEmpty());
            Assert.assertEquals("Exactly one descriptor is expected.", 1, descriptorMap.size());
            JAXBClassDescriptorImpl cd = 
                (JAXBClassDescriptorImpl) descriptorMap.get(
                        "org.castor.jaxb.resolver.JAXBClassResolverCommandTest$Artist");
            Assert.assertNotNull("The class descriptor for Artist must be known", cd);
            Assert.assertEquals("Artist", cd.getXMLName());
            Assert.assertEquals(Artist.class, cd.getJavaClass());
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        }
    }

}
