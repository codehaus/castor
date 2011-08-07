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
package org.castor.jaxb.reflection;

import org.junit.Assert;
import org.castor.jaxb.naming.JAXBJavaNaming;
import org.castor.jaxb.naming.JAXBXmlNaming;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.xml.JavaNaming;
import org.castor.xml.XMLNaming;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Tests the ClassDescriptorBuilder.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class ClassDescriptorBuilderTest {
    private ClassInfoBuilder _ciBuilder;
    /** The ClassDescriptorBuilder to test. */
    private ClassDescriptorBuilder _cdBuilder;

    /**
     * Setting up the test. Creates the ClassDescriptorBuilder and fills
     * it with the XMLNaming service to use.
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() {
        JavaNaming jn = new JAXBJavaNaming();
        XMLNaming xn = new JAXBXmlNaming();
        _ciBuilder = new ClassInfoBuilder();
        _ciBuilder.setJavaNaming(jn);
        _cdBuilder = new ClassDescriptorBuilder();
        _cdBuilder.setXMLNaming(xn);
    }

    /**
     * A private class to introspect.
     */
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

    @Test
    public void testArtist() {
        ClassInfo ci = _ciBuilder.buildClassInfo(Artist.class);
        XMLClassDescriptor cd = _cdBuilder.buildClassDescriptor(ci, true);
        Assert.assertNotNull(cd);
        Assert.assertEquals(Artist.class, cd.getJavaClass());
        Assert.assertEquals("Artist", cd.getXMLName());
        Assert.assertNull(cd.getNameSpacePrefix());
        Assert.assertNull(cd.getNameSpaceURI());
    }
    
// @TODO Joachim: I'll reactivate this test later...
//    public void testSongsSong() {
//        ClassInfo ci = _ciBuilder.buildClassInfo(Song.class);
//        XMLClassDescriptor cd = _cdBuilder.buildClassDescriptor(ci, true);
//        Assert.assertNotNull(cd);
//        Assert.assertEquals(Song.class, cd.getJavaClass());
//        Assert.assertEquals("song", cd.getXMLName());
//        Assert.assertNull(cd.getNameSpacePrefix());
//        Assert.assertNull(cd.getNameSpaceURI());
//        Assert.assertNull(cd.getContentDescriptor());
//        Assert.assertNotNull(cd.getAttributeDescriptors());
//        Assert.assertEquals(0, cd.getAttributeDescriptors().length);
//        Assert.assertNotNull(cd.getElementDescriptors());
//        Assert.assertEquals(4, cd.getElementDescriptors().length);
//    }
    
    /**
     * A class without any annotation.
     */
    private class NoXmlElementAnnotations {
        private String noElementAnnotation;
        private int _intWithNoAnnotation;
    }

    @Test
    public void testNoXmlElementAnnotations() {
        JaxbClassNature ci = new JaxbClassNature(_ciBuilder.buildClassInfo(NoXmlElementAnnotations.class));
        Assert.assertNotNull("ClassInfo generated must not be null", ci);
        Assert.assertNull(
                "Without XmlRootElement annotation this has to be null", ci.getRootElementName());
        Assert.assertNull(
                "Without XmlRootElement annotation this has to be null",
                ci.getRootElementNamespace());
        Assert.assertEquals(NoXmlElementAnnotations.class, ci.getType());
        Assert.assertEquals("One property leads to one field info", 2, ci.getFields().size());
        List < JaxbFieldNature > fis = ci.getFields();
        Assert.assertNull("Without XmlElement no element name is set", fis.get(0).getElementName());
        Assert.assertNull("Without XmlAttribute no attribute name is set", fis.get(0).getAttributeName());
        Assert.assertNull("Without XmlElement no element name is set", fis.get(1).getElementName());
        Assert.assertNull("Without XmlAttribute no attribute name is set", fis.get(1).getAttributeName());
    }
    
    /**
     * A class with annotations but without any names given.
     */
    @XmlRootElement
    private class EmptyXmlElementAnnotations {
        @XmlElement
        private String emptyElementAnnotation;
        @XmlAttribute
        private String emptyAttributeAnnotation;
    }

    @Test
    public void testEmptyXmlElementAnnotations() {
        JaxbClassNature ci = new JaxbClassNature(_ciBuilder.buildClassInfo(EmptyXmlElementAnnotations.class));
        Assert.assertNotNull("ClassInfo generated must not be null", ci);
        Assert.assertNull(ci.getRootElementName());
        Assert.assertNull(ci.getRootElementNamespace());
        Assert.assertEquals(EmptyXmlElementAnnotations.class, ci.getType());
        Assert.assertEquals("Two properties lead to two field infos", 2, ci.getFields().size());
        List < JaxbFieldNature > fis = ci.getFields();
        Assert.assertNull("XmlElement.name is not set", fis.get(0).getElementName());
        Assert.assertNull("Without XmlAttribute no attribute name is set", fis.get(0).getAttributeName());
        Assert.assertNull("Without XmlElement no element name is set", fis.get(1).getElementName());
        Assert.assertNull("XmlAttribute.name is not set", fis.get(1).getAttributeName());
    }
    
    /**
     * A class with annotations that contain names.
     */
    @XmlRootElement(name = "NamedXmlElement")
    private class NamedXmlElementAnnotations {
        @XmlElement(name = "NamedElement")
        private String namedElementAnnotation;
        @XmlAttribute(name = "NamedAttribute")
        private String namedAttributeAnnotation;
    }

    @Test
    public void testNamedXmlElementAnnotations() {
        ClassInfo ci = _ciBuilder.buildClassInfo(NamedXmlElementAnnotations.class);
        XMLClassDescriptor cd = _cdBuilder.buildClassDescriptor(ci, false);
        Assert.assertNotNull("ClassDescriptor generated must not be null", cd);
        Assert.assertEquals("NamedXmlElement", cd.getXMLName());
        Assert.assertNull(cd.getNameSpacePrefix());
        Assert.assertNull(cd.getNameSpaceURI());
        Assert.assertEquals(NamedXmlElementAnnotations.class, cd.getJavaClass());
        Assert.assertNull(cd.getContentDescriptor());
        Assert.assertEquals(1, cd.getElementDescriptors().length);
        Assert.assertEquals(1, cd.getAttributeDescriptors().length);
        List < JaxbFieldNature > fis = new JaxbClassNature(ci).getFields();
        Assert.assertEquals("NamedElement", fis.get(0).getElementName());
        Assert.assertNull("Without XmlAttribute no attribute name is set", fis.get(0).getAttributeName());
        Assert.assertNull("Without XmlElement no element name is set", fis.get(1).getElementName());
        Assert.assertEquals("NamedAttribute", fis.get(1).getAttributeName());
    }

    @Test
    public void testEnumAnnotations() {
        //Assert.fail("Not yet implemented!");
    }

    @Test
    public void testXmlEnumAnnotations() {
        //Assert.fail("Not yet implemented!");
    }
}
