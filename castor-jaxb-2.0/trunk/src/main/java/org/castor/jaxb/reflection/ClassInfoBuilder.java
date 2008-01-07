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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jaxb.naming.NamingXml2Java;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.PackageInfo;
import org.castor.xml.JavaNaming;

/**
 * A service class which is meant to read (interpret) a given class and
 * remember all information in form of a ClassInfo instance. It uses the
 * class information itself but most of the information is taken by
 * interpreting the annotations found for the class.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public final class ClassInfoBuilder {
    /** 
     * Logger to use. 
     */
    private static final Log LOG = LogFactory.getLog(ClassInfoBuilder.class);
    
    /**
     * The service to process class level annotations.
     */
    private PackageAnnotationProcessingService _packageAnnotationProcessingService;
    
    /**
     * The service to process class level annotations.
     */
    private ClassAnnotationProcessingService _classAnnotationProcessingService;
    
    /**
     * The service to process field level annotations.
     */
    private FieldAnnotationProcessingService _fieldAnnotationProcessingService;
    
    /**
     * Which JavaNaming service to use for interpreting Java method, field or other names.
     */
    private JavaNaming _javaNaming;

    /**
     * Creates the required annotation processing services.
     */
    public ClassInfoBuilder() {
        _packageAnnotationProcessingService = new PackageAnnotationProcessingService();
        _classAnnotationProcessingService = new ClassAnnotationProcessingService();
        _fieldAnnotationProcessingService = new FieldAnnotationProcessingService();
        _javaNaming = new NamingXml2Java();
    }

    /**
     * Build the ClassInfo representation for a Class.
     * @param clazz the Class to introspect
     * @return ClassInfo build from the Class
     */
    public ClassInfo buildClassInfo(final Class < ? > clazz) {
        if (clazz == null) {
            String message = "Argument clazz must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (!isDescribeable(clazz)) {
            if (LOG.isDebugEnabled()) {
                String message = "Class: " + clazz + " cannot be described using this builder.";
                LOG.debug(message);
            }
            return null;
        }
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClazz(clazz);
        classInfo.setSuperClass(clazz.getSuperclass());
        classInfo.setInterfaces(clazz.getInterfaces());
        _classAnnotationProcessingService.processAnnotations(classInfo, clazz.getAnnotations());
        for (Field field : clazz.getFields()) {
            classInfo = buildFieldInfo(classInfo, field);
        }
        for (Method method : clazz.getMethods()) {
            if (isDescribeable(clazz, method)) {
                classInfo = buildFieldInfo(classInfo, method);
            } else {
                LOG.debug("Ignoring method: " + method + " it is not declared here.");
            }
        }
        PackageInfo pi = buildPackageInfo(clazz.getPackage());
        classInfo.setPackageInfo(pi);
        return classInfo;
    }
    
    /**
     * @param clazz the Class to check
     * @return true if Class can be described
     */
    private boolean isDescribeable(final Class < ? > clazz) {
        if (Object.class.equals(clazz) || Void.class.equals(clazz) || Class.class.equals(clazz)) {
            return false;
        }
        return true;
    }

    /**
     * @param clazz the Class of the Method
     * @param method the Method to check
     * @return true if the method is describeable 
     */
    private boolean isDescribeable(final Class < ? > clazz, final Method method) {
        boolean isDescribeable = true;
        Class < ? > declaringClass = method.getDeclaringClass();
        if ((declaringClass != null) && !clazz.equals(declaringClass)
                && (!declaringClass.isInterface())) {
            isDescribeable = false;
        }
        if (!(_javaNaming.isAddMethod(method) || _javaNaming.isCreateMethod(method)
                || _javaNaming.isGetMethod(method) || _javaNaming.isIsMethod(method) || _javaNaming
                .isSetMethod(method))) {
            isDescribeable = false;
        }
        return isDescribeable;
    }

    /**
     * Build the FieldInfo for a Field.
     * @param classInfo the ClassInfo to check if this field already exists
     * @param field the Field to describe
     * @return the ClassInfo containing the FieldInfo build
     */
    private ClassInfo buildFieldInfo(final ClassInfo classInfo, final Field field) {
        if (classInfo == null) {
            String message = "Argument classInfo must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        ClassInfo returnClassInfo = classInfo;
        if (field == null) {
            String message = "Argument field must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        //String fieldName = _javaNaming.extractFieldNameFromField(field);
        String fieldName = field.getName();
        FieldInfo fieldInfo = returnClassInfo.getFieldInfo(fieldName);
        if (fieldInfo == null) {
            fieldInfo = new FieldInfo();
            fieldInfo.setFieldName(fieldName);
            returnClassInfo.addFieldInfo(fieldInfo);
        }
        fieldInfo.setField(field);
        _fieldAnnotationProcessingService.processAnnotations(fieldInfo, field.getAnnotations());
        return returnClassInfo;
    }
    
    /**
     * Build the FieldInfo for a Method.
     * @param classInfo the ClassInfo to look in if this field already exists
     * @param method the Method to describe
     * @return the ClassInfo containing the FieldInfo build
     */
    private ClassInfo buildFieldInfo(final ClassInfo classInfo, final Method method) {
        if (classInfo == null) {
            String message = "Argument classInfo must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        ClassInfo returnClassInfo = classInfo;
        if (method == null) {
            String message = "Argument method must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        String fieldName = _javaNaming.extractFieldNameFromMethod(method);
        FieldInfo fieldInfo = returnClassInfo.getFieldInfo(fieldName);
        if (fieldInfo == null) {
            fieldInfo = new FieldInfo();
            fieldInfo.setFieldName(fieldName);
            returnClassInfo.addFieldInfo(fieldInfo);
        }
        if (_javaNaming.isAddMethod(method)) {
            fieldInfo.setMethodAdd(method);
        } else if (_javaNaming.isCreateMethod(method)) {
            fieldInfo.setMethodCreate(method);
        } else if (_javaNaming.isGetMethod(method)) {
            fieldInfo.setMethodGet(method);
        } else if (_javaNaming.isSetMethod(method)) {
            fieldInfo.setMethodSet(method);
        } else if (_javaNaming.isIsMethod(method)) {
            fieldInfo.setMethodIs(method);
        } else {
            if (LOG.isDebugEnabled()) {
                String message = "Method: " + method + " is of unsupported type and ignored.";
                LOG.debug(message);
            }
        }
        _fieldAnnotationProcessingService.processAnnotations(fieldInfo, method.getAnnotations());
        return returnClassInfo;
    }
    
    /**
     * Build the PackageInfo for a Package.
     * @param pkg the Package to describe
     * @return the PackageInfo build
     */
    private PackageInfo buildPackageInfo(final Package pkg) {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.setPackage(pkg);
        _packageAnnotationProcessingService.processAnnotations(packageInfo, pkg.getAnnotations());
        return packageInfo;
        
    }

    /**
     * @param packageAnnotationProcessingService the packageAnnotationProcessingService to set
     */
    public void setPackageAnnotationProcessingService(final 
            PackageAnnotationProcessingService packageAnnotationProcessingService) {
        _packageAnnotationProcessingService = packageAnnotationProcessingService;
    }

    /**
     * @param classAnnotationProcessingService the classAnnotationProcessingService to set
     */
    public void setClassAnnotationProcessingService(final 
            ClassAnnotationProcessingService classAnnotationProcessingService) {
        _classAnnotationProcessingService = classAnnotationProcessingService;
    }

    /**
     * @param fieldAnnotationProcessingService the fieldAnnotationProcessingService to set
     */
    public void setFieldAnnotationProcessingService(final 
            FieldAnnotationProcessingService fieldAnnotationProcessingService) {
        _fieldAnnotationProcessingService = fieldAnnotationProcessingService;
    }

    /**
     * @param javaNaming the javaNaming to set
     */
    public void setJavaNaming(final JavaNaming javaNaming) {
        _javaNaming = javaNaming;
    }
}
