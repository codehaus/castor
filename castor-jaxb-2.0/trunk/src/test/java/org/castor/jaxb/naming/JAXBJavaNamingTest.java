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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Joachim Grueneis, jgrueneis AT codehaus DOT org
 * @version $Id$
 *
 */
public class JAXBJavaNamingTest extends TestCase {
    /** 
     * Logger to be used.
     */
    private static final Log LOG = LogFactory.getLog(JAXBJavaNamingTest.class);
    /** Object to test. */
    private JAXBJavaNaming _javaNaming;
    private Field _fieldWithoutLeadingUnderscore;
    private Field _fieldWithLeadingUnderscore;
    private Method _anythingGetMethod;
    
    private static class SampleClass {
        public String withoutLeadingUnderscore;
        public String _withLeadingUnderscore;
        public String getAnything() {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
        _javaNaming = new JAXBJavaNaming();
        _fieldWithoutLeadingUnderscore = SampleClass.class.getField("withoutLeadingUnderscore");
        _fieldWithLeadingUnderscore = SampleClass.class.getField("_withLeadingUnderscore");
        _anythingGetMethod = SampleClass.class.getMethod("getAnything", new Class[] {});
    }

    /**
     * @see org.castor.xml.JavaNaming#extractFieldNameFromMethod(java.lang.reflect.Method)
     */
    public void testExtractFieldNameFromMethod() {
        Assert.assertEquals("anything", _javaNaming.extractFieldNameFromMethod(_anythingGetMethod));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getAddMethodNameForField(java.lang.String)
     */
    public void testGetAddMethodNameForField(final String fieldName) {
        Assert.assertNull(_javaNaming.getAddMethodNameForField(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getCreateMethodNameForField(java.lang.String)
     */
    public void testGetCreateMethodNameForField(final String fieldName) {
        Assert.assertNull(_javaNaming.getCreateMethodNameForField(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getGetMethodNameForField(java.lang.String)
     */
    public void testGetGetMethodNameForField(final String fieldName) {
        Assert.assertNull(_javaNaming.getGetMethodNameForField(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getIsMethodNameForField(java.lang.String)
     */
    public void testGetIsMethodNameForField(final String fieldName) {
        Assert.assertNull(_javaNaming.getIsMethodNameForField(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getPackageName(java.lang.String)
     */
    public void testGetPackageName() {
        Assert.assertEquals(
                "org.castor.jaxb.naming", _javaNaming.getPackageName(SampleClass.class.getName()));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getQualifiedFileName(java.lang.String, java.lang.String)
     */
    public void testGetQualifiedFileName(final String fileName, final String packageName) {
        Assert.assertNull(_javaNaming.getQualifiedFileName(null, null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getSetMethodNameForField(java.lang.String)
     */
    public void testGetSetMethodNameForField(final String fieldName) {
        Assert.assertNull(_javaNaming.getSetMethodNameForField(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isAddMethod(java.lang.reflect.Method)
     */
    public void testIsAddMethod(final Method method) {
        Assert.assertFalse(_javaNaming.isAddMethod(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isCreateMethod(java.lang.reflect.Method)
     */
    public void testIsCreateMethod(final Method method) {
        Assert.assertFalse(_javaNaming.isCreateMethod(null));
    }

    /**
     * @see org.castor.xml.JavaNaming#isGetMethod(java.lang.reflect.Method)
     */
    public void testIsGetMethod() {
        Assert.assertTrue(_javaNaming.isGetMethod(_anythingGetMethod));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isIsMethod(java.lang.reflect.Method)
     */
    public void testIsIsMethod(final Method method) {
        Assert.assertFalse(_javaNaming.isIsMethod(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isKeyword(java.lang.String)
     */
    public void testIsKeyword(final String name) {
        Assert.assertFalse(_javaNaming.isKeyword(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isSetMethod(java.lang.reflect.Method)
     */
    public void testIsSetMethod(final Method method) {
        Assert.assertFalse(_javaNaming.isSetMethod(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isValidJavaIdentifier(java.lang.String)
     */
    public void testIsValidJavaIdentifier() {
        Assert.assertFalse(_javaNaming.isValidJavaIdentifier(null));
    }

    /**
     * @see org.castor.xml.JavaNaming#isValidPackageName(java.lang.String)
     */
    public void testIsValidPackageName() {
        Assert.assertTrue(_javaNaming.isValidPackageName(null));
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#packageToPath(java.lang.String)
     */
    public void testPackageToPath() {
        Assert.assertNull(_javaNaming.packageToPath(null));
    }

    /**
     * @see org.castor.xml.JavaNaming#toJavaClassName(java.lang.String)
     */
    public void testToJavaClassName() {
        Assert.assertNull(_javaNaming.toJavaClassName(null));
    }

    /**
     * @see org.castor.xml.JavaNaming#toJavaMemberName(java.lang.String)
     */
    public void testToJavaMemberName() {
        Assert.assertNull(_javaNaming.toJavaMemberName(null));
    }

    /**
     * @see org.castor.xml.JavaNaming#toJavaMemberName(java.lang.String, boolean)
     */
    public void testToJavaMemberName2() {
        Assert.assertNull(_javaNaming.toJavaMemberName(null, true));
    }

    /**
     * @see org.castor.xml.JavaNaming#extractFieldNameFromField(java.lang.reflect.Field)
     */
    public void testExtractFieldNameFromField() {
        Assert.assertNull(_javaNaming.extractFieldNameFromField(null));
        Assert.assertEquals(
                "withLeadingUnderscore", _javaNaming.extractFieldNameFromField(_fieldWithLeadingUnderscore));
    }

    public void testGetClassName() {
        Assert.assertEquals("JAXBJavaNamingTest", _javaNaming.getClassName(JAXBJavaNamingTest.class));
        Assert.assertEquals("JAXBJavaNamingTest$SampleClass", _javaNaming.getClassName(SampleClass.class));
    }
}
