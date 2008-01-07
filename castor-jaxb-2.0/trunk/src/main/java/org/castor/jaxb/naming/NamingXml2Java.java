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

import org.castor.xml.JavaNaming;
import org.castor.xml.JavaNamingImpl;

/**
 * JAXB specific implementation of JavaNaming which provides all rules to build
 * (and interpret) names conforming to Java naming rules.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class NamingXml2Java extends JavaNamingImpl implements JavaNaming {
//
//    /**
//     * @see org.castor.xml.JavaNaming#extractFieldNameFromMethod(java.lang.reflect.Method)
//     */
//    public String extractFieldNameFromMethod(Method method) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#getAddMethodNameForField(java.lang.String)
//     */
//    public String getAddMethodNameForField(String fieldName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#getCreateMethodNameForField(java.lang.String)
//     */
//    public String getCreateMethodNameForField(String fieldName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#getGetMethodNameForField(java.lang.String)
//     */
//    public String getGetMethodNameForField(String fieldName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#getIsMethodNameForField(java.lang.String)
//     */
//    public String getIsMethodNameForField(String fieldName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#getPackageName(java.lang.String)
//     */
//    public String getPackageName(String className) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#getQualifiedFileName(java.lang.String, java.lang.String)
//     */
//    public String getQualifiedFileName(String fileName, String packageName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#getSetMethodNameForField(java.lang.String)
//     */
//    public String getSetMethodNameForField(String fieldName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isAddMethod(java.lang.reflect.Method)
//     */
//    public boolean isAddMethod(Method method) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isCreateMethod(java.lang.reflect.Method)
//     */
//    public boolean isCreateMethod(Method method) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isGetMethod(java.lang.reflect.Method)
//     */
//    public boolean isGetMethod(Method method) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isIsMethod(java.lang.reflect.Method)
//     */
//    public boolean isIsMethod(Method method) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isKeyword(java.lang.String)
//     */
//    public boolean isKeyword(String name) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isSetMethod(java.lang.reflect.Method)
//     */
//    public boolean isSetMethod(Method method) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isValidJavaIdentifier(java.lang.String)
//     */
//    public boolean isValidJavaIdentifier(String string) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#isValidPackageName(java.lang.String)
//     */
//    public boolean isValidPackageName(String packageName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#packageToPath(java.lang.String)
//     */
//    public String packageToPath(String packageName) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#toJavaClassName(java.lang.String)
//     */
//    public String toJavaClassName(String name) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#toJavaMemberName(java.lang.String)
//     */
//    public String toJavaMemberName(String name) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
//    /**
//     * @see org.castor.xml.JavaNaming#toJavaMemberName(java.lang.String, boolean)
//     */
//    public String toJavaMemberName(String name, boolean useKeywordSubstitutions) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return null;
//    }
//
}
