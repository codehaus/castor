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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.util.ResolverStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * To test the resolver strategy for Castor running in JAXB compliance
 * mode.
 * 
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class JAXBResolverStrategyTest {

    @Autowired
    private JAXBResolverStrategy _strategy;
    
    private MyResolverResults _resolverResults;

    /**
     * Correctly setup the resolver strategy.
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() {
        _resolverResults = new MyResolverResults();
    }

    /**
     * Test method for {@link org.castor.jaxb.resolver.JAXBResolverStrategy#resolveClass(
     * org.exolab.castor.xml.util.ResolverStrategy.ResolverResults, java.lang.String)}.
     */
    @Test
    public void testResolveClassNullNull() {
        try {
            _strategy.resolveClass(null, null);
            Assert.fail("The resolver results must not be null!");
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }

    /**
     * Test method for {@link org.castor.jaxb.resolver.JAXBResolverStrategy#resolveClass(
     * org.exolab.castor.xml.util.ResolverStrategy.ResolverResults, java.lang.String)}.
     */
    @Test
    public void testResolveClassMyRRNull() {
        try {
            _strategy.resolveClass(_resolverResults, null);
            Assert.fail("The class name must not be null!");
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }

    /**
     * Test method for {@link org.castor.jaxb.resolver.JAXBResolverStrategy#resolvePackage(
     * org.exolab.castor.xml.util.ResolverStrategy.ResolverResults, java.lang.String)}.
     */
    @Test
    public void testResolvePackageNullNull() {
        try {
            _strategy.resolvePackage(null, null);
            Assert.fail("The resolver results must not be null!");
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }

    /**
     * Test method for {@link org.castor.jaxb.resolver.JAXBResolverStrategy#resolvePackage(
     * org.exolab.castor.xml.util.ResolverStrategy.ResolverResults, java.lang.String)}.
     */
    @Test
    public void testResolvePackageMyRRNull() {
        try {
            _strategy.resolvePackage(_resolverResults, null);
            Assert.fail("The package name must not be null!");
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }


    private class MyTestClass {

    }

    @Test
    public void testResolveClass() {
        try {
            _strategy.resolveClass(_resolverResults, MyTestClass.class.getName());
            Assert.assertEquals(1, _resolverResults.size());
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        }
    }

    @Test
    @Ignore
    public void testResolvePackage() {
        try {
            _strategy.resolvePackage(_resolverResults, MyTestClass.class.getPackage().getName());
            Assert.fail("Currently not implemented.");
            Assert.assertEquals(1, _resolverResults.size());
        } catch (ResolverException e) {
            Assert.fail("Failed with exception: " + e);
        } catch (UnsupportedOperationException e) {
            // for now it is ok..
        }
    }

    /**
     * Test method for {@link org.castor.jaxb.resolver.JAXBResolverStrategy#setProperty(
     * java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testSetProperty() {
        //fail("Not yet implemented");
    }

    /**
     * A ResolverResults implementation for test purposes.
     * 
     * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
     * @version $Id$
     */
    private class MyResolverResults implements ResolverStrategy.ResolverResults {
        private HashMap < String, XMLClassDescriptor > _rr =
            new HashMap < String, XMLClassDescriptor > ();

        public MyResolverResults() {
            super();
        }

        public int size() {
            return _rr.size();
        }

        public void addAllDescriptors(final Map descriptors) {
            for (Iterator keyIter = descriptors.keySet().iterator(); keyIter.hasNext();) {
                String key = (String) keyIter.next();
                _rr.put(key, (XMLClassDescriptor) descriptors.get(key));
            }
        }

        public void addDescriptor(
                final String className,
                final XMLClassDescriptor descriptor) {
            _rr.put(className, descriptor);
        }

        public XMLClassDescriptor getDescriptor(final String className) {
            return _rr.get(className);
        }

        public List<XMLClassDescriptor> getAllDescriptors() {
            return new ArrayList<XMLClassDescriptor>(_rr.values());
        }

        public void removeDescriptor(String className) {
            _rr.remove(className);
        }

    }
}
