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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.castor.core.annotationprocessing.AnnotationProcessingService;
import org.castor.jaxb.naming.JAXBJavaNaming;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.info.OoClassNature;
import org.castor.jaxb.reflection.info.OoFieldNature;
import org.castor.jaxb.reflection.info.OoPackageNature;
import org.castor.jaxb.reflection.info.PackageInfo;
import org.castor.xml.JavaNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A service class which is meant to read (interpret) a given class and remember
 * all information in form of a ClassInfo instance. It uses the class
 * information itself but most of the information is taken by interpreting the
 * annotations found for the class.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public final class ClassInfoBuilder {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * The service to process class level annotations.
     */
    @Autowired
    private AnnotationProcessingService _packageAnnotationProcessingService;

    /**
     * The service to process class level annotations.
     */
    @Autowired
    private AnnotationProcessingService _classAnnotationProcessingService;

    /**
     * The service to process field level annotations.
     */
    @Autowired
    private AnnotationProcessingService _fieldAnnotationProcessingService;

    /**
     * Which JavaNaming service to use for interpreting Java method, field or
     * other names.
     */
    private JavaNaming _javaNaming;

    /**
     * Creates the required annotation processing services.
     */
    public ClassInfoBuilder() {
        super();
        _packageAnnotationProcessingService = new PackageAnnotationProcessingServiceImpl();
        _classAnnotationProcessingService = new ClassAnnotationProcessingServiceImpl();
        _fieldAnnotationProcessingService = new FieldAnnotationProcessingServiceImpl();
        _javaNaming = new JAXBJavaNaming();
    }

    /**
     * Build the ClassInfo representation for a Class.
     * 
     * @param type
     *            the Class to introspect
     * @return ClassInfo build from the Class
     */
    public ClassInfo buildClassInfo(final Class<?> type) {
        if (type == null) {
            String message = "Argument type must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (!isDescribeable(type)) {
            if (LOG.isDebugEnabled()) {
                String message = "Class: " + type
                        + " cannot be described using this builder.";
                LOG.debug(message);
            }
            return null;
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Now starting to build ClassInfo for: " + type);
        }

        ClassInfo classInfo = createClassInfo(_javaNaming.getClassName(type));
        JaxbClassNature jaxbClassNature = new JaxbClassNature(classInfo);

        jaxbClassNature.setType(type);
        jaxbClassNature.setSupertype(type.getSuperclass());
        jaxbClassNature.setInterfaces(type.getInterfaces());
        jaxbClassNature
                .setHasPublicEmptyConstructor(hasPublicEmptyConstructor(type));

        _classAnnotationProcessingService.processAnnotations(jaxbClassNature,
                type.getAnnotations());
        for (Field field : type.getDeclaredFields()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Now evaluating field: " + field);
            }
            if (isDescribeable(type, field)) {
                classInfo = buildFieldInfo(classInfo, field);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring field: " + field + " of type: "
                            + type.getName()
                            + " it is not useable for mapping.");
                }
            }
        }
        for (Method method : type.getDeclaredMethods()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Now evaluating method: " + method);
            }
            if (isDescribeable(type, method)) {
                classInfo = buildFieldInfo(classInfo, method);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring method: " + method + " of type: "
                            + type.getName()
                            + " it is not useable for mapping.");
                }
            }
        }
        PackageInfo pi = buildPackageInfo(type.getPackage());
        classInfo.setPackageInfo(pi);
        if (LOG.isInfoEnabled()) {
            LOG.info("ClassInfo for: " + type + " build is: " + classInfo);
        }
        return classInfo;
    }

    /**
     * Does the introspected class have a public empty constructor?
     * 
     * @param type
     *            the class to check
     * @return true if an empty public constructor exists
     */
    private boolean hasPublicEmptyConstructor(final Class<?> type) {
        Constructor<?> cnstrctr = null;
        try {
            cnstrctr = type.getConstructor();
        } catch (SecurityException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to get empty public constructor for: " + type
                        + " with exception: " + e + " - ingoring exception!");
            }
        } catch (NoSuchMethodException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to get empty public constructor for: " + type
                        + " with exception: " + e + " - ingoring exception!");
            }
        }
        return (cnstrctr != null);
    }

    /**
     * Check if the Class is describeable.
     * 
     * @param type
     *            the Class to check
     * @return true if Class can be described
     */
    private boolean isDescribeable(final Class<?> type) {
        if (Object.class.equals(type) || Void.class.equals(type)
                || Class.class.equals(type)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the Method is describeable.
     * 
     * @param type
     *            the Class of the Method
     * @param method
     *            the Method to check
     * @return true if the method is describeable
     */
    private boolean isDescribeable(final Class<?> type, final Method method) {
        boolean isDescribeable = true;
        Class<?> declaringClass = method.getDeclaringClass();
        if ((declaringClass != null) && !type.equals(declaringClass)
                && (!declaringClass.isInterface())) {
            isDescribeable = false;
        }
        if (method.isSynthetic()) {
            isDescribeable &= false;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            isDescribeable &= false;
        }
        if (Modifier.isTransient(method.getModifiers())) {
            isDescribeable &= false;
        }
        if (!(_javaNaming.isAddMethod(method)
                || _javaNaming.isCreateMethod(method)
                || _javaNaming.isGetMethod(method)
                || _javaNaming.isIsMethod(method) || _javaNaming
                .isSetMethod(method))) {
            isDescribeable = false;
        }
        return isDescribeable;
    }

    /**
     * Checks if a field of a class is describeable or not e.g. static or
     * transient fields are not described.
     * 
     * @param type
     *            the Class holding the field
     * @param field
     *            the field to check
     * @return true if the field should be described
     */
    private boolean isDescribeable(final Class<?> type, final Field field) {
        boolean isDescribeable = true;
        Class<?> declaringClass = field.getDeclaringClass();
        if ((declaringClass != null) && !type.equals(declaringClass)
                && (!declaringClass.isInterface())) {
            isDescribeable = false;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            isDescribeable &= false;
        }
        if (Modifier.isTransient(field.getModifiers())) {
            isDescribeable &= false;
        }
        if (field.isSynthetic()) {
            isDescribeable &= false;
        }
        return isDescribeable;
    }

    /**
     * Build the FieldInfo for a Field.
     * 
     * @param classInfo
     *            the ClassInfo to check if this field already exists
     * @param field
     *            the Field to describe
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
        String fieldName = _javaNaming.extractFieldNameFromField(field);
        FieldInfo fieldInfo = returnClassInfo.getFieldInfo(fieldName);
        if (fieldInfo == null) {
            fieldInfo = createFieldInfo(fieldName);
            returnClassInfo.addFieldInfo(fieldInfo);
        }
        JaxbFieldNature jaxbFieldNature = new JaxbFieldNature(fieldInfo);
        jaxbFieldNature.setField(field);
        Class<?> fieldType = field.getDeclaringClass();
        if (fieldType.isArray() || fieldType.isAssignableFrom(Collection.class)) {
            jaxbFieldNature.setMultivalued(true);
        }
        jaxbFieldNature.setGenericType(field.getGenericType());
        _fieldAnnotationProcessingService.processAnnotations(jaxbFieldNature,
                field.getAnnotations());
        return returnClassInfo;
    }

    /**
     * Build the FieldInfo for a Method.
     * 
     * @param classInfo
     *            the ClassInfo to look in if this field already exists
     * @param method
     *            the Method to describe
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
            fieldInfo = createFieldInfo(fieldName);
            returnClassInfo.addFieldInfo(fieldInfo);
        }
        JaxbFieldNature jaxbFieldNature = new JaxbFieldNature(fieldInfo);
        if (_javaNaming.isAddMethod(method)) {
            jaxbFieldNature.setMethodAdd(method);
            jaxbFieldNature.setMultivalued(true);
        } else if (_javaNaming.isCreateMethod(method)) {
            jaxbFieldNature.setMethodCreate(method);
        } else if (_javaNaming.isGetMethod(method)) {
            jaxbFieldNature.setMethodGet(method);
            Class<?> fieldType = method.getReturnType();
            if (fieldType.isArray()
                    || fieldType.isAssignableFrom(Collection.class)
                    || Collection.class.isAssignableFrom(fieldType)) {
                jaxbFieldNature.setGenericType(method.getGenericReturnType());
                jaxbFieldNature.setMultivalued(true);
            }
        } else if (_javaNaming.isSetMethod(method)) {
            jaxbFieldNature.setMethodSet(method);
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1) {
                Class<?> fieldType = parameterTypes[0];
                if (fieldType.isArray()
                        || fieldType.isAssignableFrom(Collection.class)
                        || Collection.class.isAssignableFrom(fieldType)) {
                    jaxbFieldNature.setMultivalued(true);
                    jaxbFieldNature.setGenericType(method
                            .getGenericParameterTypes()[0]);
                }
            }
        } else if (_javaNaming.isIsMethod(method)) {
            jaxbFieldNature.setMethodIs(method);
        } else {
            if (LOG.isDebugEnabled()) {
                String message = "Method: " + method
                        + " is of unsupported type and ignored.";
                LOG.debug(message);
            }
        }
        _fieldAnnotationProcessingService.processAnnotations(jaxbFieldNature,
                method.getAnnotations());
        return returnClassInfo;
    }

    /**
     * Build the PackageInfo for a Package.
     * 
     * @param pkg
     *            the Package to describe
     * @return the PackageInfo build
     */
    private PackageInfo buildPackageInfo(final Package pkg) {
        PackageInfo packageInfo = createPackageInfo(pkg.getName());
        OoPackageNature ooPackageNature = new OoPackageNature(packageInfo);
        ooPackageNature.setPackage(pkg);
        _packageAnnotationProcessingService.processAnnotations(
                new JaxbPackageNature(packageInfo), pkg.getAnnotations());
        return packageInfo;
    }

    /**
     * @return the PackageAnnotationProcessingService in use
     */
    public AnnotationProcessingService getPackageAnnotationProcessingService() {
        return _packageAnnotationProcessingService;
    }

    /**
     * @param packageAnnotationProcessingService
     *            the PackageAnnotationProcessingService to set
     */
    public void setPackageAnnotationProcessingService(
            final AnnotationProcessingService packageAnnotationProcessingService) {
        _packageAnnotationProcessingService = packageAnnotationProcessingService;
    }

    /**
     * @return the ClassAnnotationProcessingService in use.
     */
    public AnnotationProcessingService getClassAnnotationProcessingService() {
        return _classAnnotationProcessingService;
    }

    /**
     * @param classAnnotationProcessingService
     *            the classAnnotationProcessingService to set
     */
    public void setClassAnnotationProcessingService(
            final AnnotationProcessingService classAnnotationProcessingService) {
        _classAnnotationProcessingService = classAnnotationProcessingService;
    }

    /**
     * @return the FieldAnnotationProcessingService in use.
     */
    public AnnotationProcessingService getFieldAnnotationProcessingService() {
        return _fieldAnnotationProcessingService;
    }

    /**
     * @param fieldAnnotationProcessingService
     *            the fieldAnnotationProcessingService to set
     */
    public void setFieldAnnotationProcessingService(
            final AnnotationProcessingService fieldAnnotationProcessingService) {
        _fieldAnnotationProcessingService = fieldAnnotationProcessingService;
    }

    /**
     * @return the JavaNaming in use.
     */
    public JavaNaming getJavaNaming() {
        return _javaNaming;
    }

    /**
     * @param javaNaming
     *            the javaNaming to set
     */
    public void setJavaNaming(final JavaNaming javaNaming) {
        _javaNaming = javaNaming;
    }

    /**
     * Creates a new ClassInfo with all Nature's registered.
     * 
     * @param className
     *            the name of the class
     * @return a ClassInfo instance
     */
    private ClassInfo createClassInfo(final String className) {
        ClassInfo classInfo = new ClassInfo(className);
        classInfo.addNature(OoClassNature.class.getName());
        classInfo.addNature(JaxbClassNature.class.getName());
        return classInfo;
    }

    /**
     * Creates a new FieldInfo with all Nature's registered.
     * 
     * @param fieldName
     *            the name of the field
     * @return a FieldInfo instance
     */
    private FieldInfo createFieldInfo(final String fieldName) {
        FieldInfo fieldInfo = new FieldInfo(fieldName);
        fieldInfo.addNature(OoFieldNature.class.getName());
        fieldInfo.addNature(JaxbFieldNature.class.getName());
        return fieldInfo;
    }

    /**
     * Creates a new PackageInfo with all Nature's registered.
     * 
     * @param packageName
     *            the name of the package
     * @return a PackageInfo instance
     */
    private PackageInfo createPackageInfo(final String packageName) {
        PackageInfo packageInfo = new PackageInfo(packageName);
        packageInfo.addNature(OoPackageNature.class.getName());
        packageInfo.addNature(JaxbPackageNature.class.getName());
        return packageInfo;
    }
}
