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

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.FieldInfo;

/**
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class ClassInfoBuilderTest extends TestCase {
    /** 
     * Logger to use. 
     */
    //private static final Log LOG = LogFactory.getLog(ClassInfoBuilderTest.class);
    
    /**
     * Object under test.
     */
    private ClassInfoBuilder _builder;
    
    /**
     * @param name
     */
    public ClassInfoBuilderTest(final String name) {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public final void setUp() {
        _builder = new ClassInfoBuilder();
    }

    public final void testNull() {
        try {
            _builder.buildClassInfo(null);
            Assert.fail("'null' cannot be turned into a ClassInfo");
        } catch (IllegalArgumentException e) {
            // right as expected
        }
    }

    public final void testObject() {
        try {
            ClassInfo ci = _builder.buildClassInfo(Object.class);
            Assert.assertNull("'Object.class' cannot be turned into a ClassInfo", ci);
        } catch (IllegalArgumentException e) {
            // right as expected
        }
    }

    @XmlRootElement(name = "Artist")
    private class Artist {
        private String _name;
        private Date _birthday;
        private String _biography;
        @XmlElement(name = "Name", type = String.class)
        public final String getName() {
            return _name;
        }
        public final void setName(final String name) {
            _name = name;
        }
        @XmlElement(name = "Birthday", type = Date.class)
        public final Date getBirthday() {
            return _birthday;
        }
        public final void setBirthday(final Date birthday) {
            _birthday = birthday;
        }
        @XmlElement(name = "Biography", type = String.class)
        public final String getBiography() {
            return _biography;
        }
        public final void setBiography(final String biography) {
            _biography = biography;
        }
    }

    public final void testArtist() {
        ClassInfo ci = _builder.buildClassInfo(Artist.class);
        Assert.assertNotNull("ClassInfo generated must not be null", ci);
        Assert.assertEquals("Artist", ci.getRootElementName());
        Assert.assertEquals(Artist.class, ci.getClazz());
        Assert.assertEquals(3, ci.getFieldInfos().size());
        for (FieldInfo fieldInfo : ci.getFieldInfos()) {
            if ("Name".equals(fieldInfo.getElementName())) {
                Assert.assertEquals("Name", fieldInfo.getElementName());
                Assert.assertEquals(String.class, fieldInfo.getElementType());
            } else if ("Birthday".equals(fieldInfo.getElementName())) {
                Assert.assertEquals("Birthday", fieldInfo.getElementName());
                Assert.assertEquals(Date.class, fieldInfo.getElementType());
            } else if ("Biography".equals(fieldInfo.getElementName())) {
                Assert.assertEquals("Biography", fieldInfo.getElementName());
                Assert.assertEquals(String.class, fieldInfo.getElementType());
            }
        }
    }
}
