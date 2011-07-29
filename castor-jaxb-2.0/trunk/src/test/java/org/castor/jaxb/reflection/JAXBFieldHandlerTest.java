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
package org.castor.jaxb.reflection;

import org.junit.Assert;
import org.castor.jaxb.exceptions.AdapterException;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.Method;

/**
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 *
 */
public class JAXBFieldHandlerTest {
    private JAXBFieldHandlerImpl _fh;

    @Before
    public void setUp() {
        _fh = new JAXBFieldHandlerImpl();
    }
    /**
     * Tests that a not initialized field handler does return the expected
     * exception: IllegalStateException.
     */
    @Test
    public void testNotInitializedNewInstance() {
        Object result = _fh.newInstance(this);
        Assert.assertNull("Uninitialized FieldHandler has to return null", result);
    }
    private static class ObjectFactory {
        public ObjectFactory() {
            super();
        }
        public Artist createArtist() {
            return new Artist();
        }
    }
    private static class Song {
        private Artist _artist;
        public Song() {
            super();
        }
        public Artist getArtist() {
            return _artist;
        }
        public void setArtist(final Artist artist) {
            _artist = artist;
        }
    }
    private static class Artist {
        private String _name;
        public Artist() {
            super();
        }
        public String getName() {
            return _name;
        }
        public void setName(final String name) {
            _name = name;
        }
    }
    /**
     * 
     */
    @Test
    public void testNewInstanceByClassNewInstance() {
        _fh.setType(Artist.class);
        Object result = _fh.newInstance(null);
        Assert.assertNotNull(result);
        Assert.assertEquals("expected Class is Artist", Artist.class, result.getClass());
    }
    /**
     * 
     */
    @Test
    public void testNewInstanceByWrongObjectFactory() {
        try {
            _fh.setType(Artist.class);
            _fh.setTypeFactory(ObjectFactory.class, "createArtistl");
            _fh.newInstance(null);
            Assert.fail("should fail as method name is wrong");
        } catch (AdapterException e) {
            // expected
        }
    }
    /**
     * 
     */
    @Test
    public void testNewInstanceByObjectFactory() {
        _fh.setTypeFactory(ObjectFactory.class, "createArtist");
        Object result = _fh.newInstance(null);
        Assert.assertNotNull(result);
        Assert.assertEquals("expected Class is Artist", Artist.class, result.getClass());
    }

    @Test
    public void testGetValue() {
        Artist a = new Artist();
        a.setName("Hugo");
        Song s = new Song();
        s.setArtist(a);
        setMethodsIntoFieldHandler(_fh);
        Object result = _fh.getValue(s);
        Assert.assertNotNull(result);
        Assert.assertEquals("expected Class is Artist", Artist.class, result.getClass());
    }

    @Test
    public void testSetValue() {
        Song s = new Song();
        setMethodsIntoFieldHandler(_fh);
        Artist a = new Artist();
        a.setName("Hugo");
        _fh.setValue(s, a);
        Assert.assertNotNull(s.getArtist());
    }

    private static class ArtistAdapter extends XmlAdapter < Artist, String > {
        public ArtistAdapter () {
            super();
        }
        @Override
        public Artist marshal(final String v) throws Exception {
            Artist a = new Artist();
            a.setName(v);
            return a;
        }

        @Override
        public String unmarshal(final Artist v) throws Exception {
            return v.getName();
        }
        
    }

    @Test
    public void testXmlAdapterSetValue() {
        Song s = new Song();
        setMethodsIntoFieldHandler(_fh);
        _fh.setXmlAdapter(new ArtistAdapter());
        _fh.setValue(s, "Hugo");
        Assert.assertNotNull(s.getArtist());
    }

    @Test
    public void testXmlAdapterGetValue() {
        Artist a = new Artist();
        a.setName("Hugo");
        Song s = new Song();
        s.setArtist(a);
        setMethodsIntoFieldHandler(_fh);
        _fh.setXmlAdapter(new ArtistAdapter());
        Object result = _fh.getValue(s);
        Assert.assertNotNull(result);
    }

    private void setMethodsIntoFieldHandler(
            final JAXBFieldHandlerImpl fh) {
        Method getMethod = null;
        Method setMethod = null;
        try {
            getMethod = Song.class.getMethod("getArtist", new Class < ? > [] {});
            setMethod = Song.class.getMethod("setArtist", new Class < ? > [] {Artist.class});
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        fh.setMethods(getMethod, setMethod);
    }
}
