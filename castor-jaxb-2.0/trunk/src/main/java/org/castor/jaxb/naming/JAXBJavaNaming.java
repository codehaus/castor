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
package org.castor.jaxb.naming;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.castor.xml.JavaNaming;
import org.castor.xml.JavaNamingImpl;

/**
 * JAXB specific implementation of JavaNaming which provides all rules to build
 * (and interpret) names conforming to Java naming rules.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class JAXBJavaNaming implements JavaNaming {
    /**
     * For all stuff that doesn't differ from Castor implementation this
     * class delegates to it.
     */
    private JavaNaming _javaNaming = new JavaNamingImpl();

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#extractFieldNameFromMethod(java.lang.reflect.Method)
     */
    public String extractFieldNameFromMethod(final Method method) {
        return _javaNaming.extractFieldNameFromMethod(method);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getAddMethodNameForField(java.lang.String)
     */
    public String getAddMethodNameForField(final String fieldName) {
        return _javaNaming.getAddMethodNameForField(fieldName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getCreateMethodNameForField(java.lang.String)
     */
    public String getCreateMethodNameForField(final String fieldName) {
        return _javaNaming.getCreateMethodNameForField(fieldName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getGetMethodNameForField(java.lang.String)
     */
    public String getGetMethodNameForField(final String fieldName) {
        return _javaNaming.getGetMethodNameForField(fieldName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getIsMethodNameForField(java.lang.String)
     */
    public String getIsMethodNameForField(final String fieldName) {
        return _javaNaming.getIsMethodNameForField(fieldName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getPackageName(java.lang.String)
     */
    public String getPackageName(final String className) {
        return _javaNaming.getPackageName(className);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getQualifiedFileName(java.lang.String, java.lang.String)
     */
    public String getQualifiedFileName(final String fileName, final String packageName) {
        return _javaNaming.getQualifiedFileName(fileName, packageName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getSetMethodNameForField(java.lang.String)
     */
    public String getSetMethodNameForField(final String fieldName) {
        return _javaNaming.getSetMethodNameForField(fieldName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isAddMethod(java.lang.reflect.Method)
     */
    public boolean isAddMethod(final Method method) {
        return _javaNaming.isAddMethod(method);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isCreateMethod(java.lang.reflect.Method)
     */
    public boolean isCreateMethod(final Method method) {
        return _javaNaming.isCreateMethod(method);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isGetMethod(java.lang.reflect.Method)
     */
    public boolean isGetMethod(final Method method) {
        return _javaNaming.isGetMethod(method);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isIsMethod(java.lang.reflect.Method)
     */
    public boolean isIsMethod(final Method method) {
        return _javaNaming.isIsMethod(method);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isKeyword(java.lang.String)
     */
    public boolean isKeyword(final String name) {
        return _javaNaming.isKeyword(name);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isSetMethod(java.lang.reflect.Method)
     */
    public boolean isSetMethod(final Method method) {
        return _javaNaming.isSetMethod(method);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isValidJavaIdentifier(java.lang.String)
     */
    public boolean isValidJavaIdentifier(final String string) {
        return _javaNaming.isValidJavaIdentifier(string);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#isValidPackageName(java.lang.String)
     */
    public boolean isValidPackageName(final String packageName) {
        return _javaNaming.isValidPackageName(packageName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#packageToPath(java.lang.String)
     */
    public String packageToPath(final String packageName) {
        return _javaNaming.packageToPath(packageName);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#toJavaClassName(java.lang.String)
     */
    public String toJavaClassName(final String name) {
        return _javaNaming.toJavaClassName(name);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#toJavaMemberName(java.lang.String)
     */
    public String toJavaMemberName(final String name) {
        return _javaNaming.toJavaMemberName(name);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#toJavaMemberName(java.lang.String, boolean)
     */
    public String toJavaMemberName(final String name, final boolean useKeywordSubstitutions) {
        return _javaNaming.toJavaMemberName(name, useKeywordSubstitutions);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#extractFieldNameFromField(java.lang.reflect.Field)
     */
    public String extractFieldNameFromField(final Field field) {
        return _javaNaming.extractFieldNameFromField(field);
    }

    /**
     * {@inheritDoc}
     * @see org.castor.xml.JavaNaming#getClassName(java.lang.Class)
     */
    public String getClassName(final Class clazz) {
        return _javaNaming.getClassName(clazz);
    }
}
