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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.castor.jaxb.reflection.info.ClassInfo;
import org.exolab.castor.xml.XMLClassDescriptor;

/**
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class ClassDescriptorBuilderTest extends TestCase {
    private ClassDescriptorBuilder _builder;
    
    /**
     * @param name
     */
    public ClassDescriptorBuilderTest(final String name) {
        super(name);
    }
    
    public void setUp() {
        _builder = new ClassDescriptorBuilder();
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

    public void testArtist() {
        ClassInfo ci = new ClassInfoBuilder().buildClassInfo(Artist.class);
        
        XMLClassDescriptor cd = _builder.buildClassDescriptor(ci, true);
        Assert.assertNotNull(cd);
        Assert.assertEquals(Artist.class, cd.getJavaClass());
        Assert.assertEquals("Artist", cd.getXMLName());
        Assert.assertNull(cd.getNameSpacePrefix());
        Assert.assertEquals("##default", cd.getNameSpaceURI());
    }
}
