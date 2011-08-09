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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;

import org.castor.core.annotationprocessing.AnnotationProcessingService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * A service class which is meant to read (interpret) a given class and remember
 * all information in form of a {@link ClassInfo} instance. It uses the class
 * information itself but most of the information is taken by interpreting the
 * annotations found for the class.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
@Component("classInfoBuilder")
public final class ClassInfoBuilder {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * The service to process class level annotations.
     */
    @Autowired
    @Qualifier("packageAnnotationProcessingService")
    private AnnotationProcessingService packageAnnotationProcessingService;

    /**
     * The service to process class level annotations.
     */
    @Autowired
    @Qualifier("classAnnotationProcessingService")
    private AnnotationProcessingService classAnnotationProcessingService;

    /**
     * The service to process field level annotations.
     */
    @Autowired
    @Qualifier("fieldAnnotationProcessingService")
    private AnnotationProcessingService fieldAnnotationProcessingService;

    /**
     * Which JavaNaming service to use for interpreting Java method, field or
     * other names.
     */
    @Autowired
    @Qualifier("jaxbJavaNaming")
    private JavaNaming javaNaming;

    // /**
    // * Creates the required annotation processing services.
    // */
    // public ClassInfoBuilder() {
    // super();
    // // packageAnnotationProcessingService = new
    // PackageAnnotationProcessingServiceImpl();
    // // classAnnotationProcessingService = new
    // ClassAnnotationProcessingServiceImpl();
    // // fieldAnnotationProcessingService = new
    // FieldAnnotationProcessingServiceImpl();
    // }

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
                String message = "Class: " + type + " cannot be described using this builder.";
                LOG.debug(message);
            }
            return null;
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Now starting to build ClassInfo for: " + type);
        }

        ClassInfo classInfo = createClassInfo(javaNaming.getClassName(type));
        JaxbClassNature jaxbClassNature = new JaxbClassNature(classInfo);

        jaxbClassNature.setType(type);
        jaxbClassNature.setSupertype(type.getSuperclass());
        jaxbClassNature.setInterfaces(type.getInterfaces());
        jaxbClassNature.setHasPublicEmptyConstructor(hasPublicEmptyConstructor(type));

        Annotation[] unprocessedClassAnnotations = this.classAnnotationProcessingService.processAnnotations(
                jaxbClassNature, type.getAnnotations());
        for (Field field : type.getDeclaredFields()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Now evaluating field: " + field);
            }
            if (isDescribeable(type, field)) {
                buildFieldInfo(classInfo, field);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring field: " + field + " of type: " + type.getName()
                            + " it is not useable for mapping.");
                }
            }
        }
        for (Method method : type.getDeclaredMethods()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Now evaluating method: " + method);
            }
            if (isDescribeable(type, method)) {
                buildFieldInfo(classInfo, method);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring method: " + method + " of type: " + type.getName()
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
                LOG.debug("Failed to get empty public constructor for: " + type + " with exception: " + e
                        + " - ingoring exception!");
            }
        } catch (NoSuchMethodException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to get empty public constructor for: " + type + " with exception: " + e
                        + " - ingoring exception!");
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
        if (Object.class.equals(type) || Void.class.equals(type) || Class.class.equals(type)) {
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
        if ((declaringClass != null) && !type.equals(declaringClass) && (!declaringClass.isInterface())) {
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
        if (!(javaNaming.isAddMethod(method) || javaNaming.isCreateMethod(method) || javaNaming.isGetMethod(method)
                || javaNaming.isIsMethod(method) || javaNaming.isSetMethod(method))) {
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
        if ((declaringClass != null) && !type.equals(declaringClass) && (!declaringClass.isInterface())) {
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
    private void buildFieldInfo(final ClassInfo classInfo, final Field field) {
        if (classInfo == null) {
            String message = "Argument classInfo must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (field == null) {
            String message = "Argument field must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        String fieldName = javaNaming.extractFieldNameFromField(field);
        FieldInfo fieldInfo = classInfo.getFieldInfo(fieldName);
        if (fieldInfo == null) {
            fieldInfo = createFieldInfo(fieldName);
            classInfo.addFieldInfo(fieldInfo);
            fieldInfo.setParentClassInfo(classInfo);
        }
        JaxbFieldNature jaxbFieldNature = new JaxbFieldNature(fieldInfo);
        jaxbFieldNature.setField(field);
        Class<?> fieldType = field.getDeclaringClass();
        if (fieldType.isArray() || fieldType.isAssignableFrom(Collection.class)) {
            jaxbFieldNature.setMultivalued(true);
        }
        jaxbFieldNature.setGenericType(field.getGenericType());
        fieldAnnotationProcessingService.processAnnotations(jaxbFieldNature, field.getAnnotations());
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
    private void buildFieldInfo(final ClassInfo classInfo, final Method method) {
        if (classInfo == null) {
            String message = "Argument classInfo must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (method == null) {
            String message = "Argument method must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        String fieldName = javaNaming.extractFieldNameFromMethod(method);
        FieldInfo fieldInfo = classInfo.getFieldInfo(fieldName);
        if (fieldInfo == null) {
            fieldInfo = createFieldInfo(fieldName);
            classInfo.addFieldInfo(fieldInfo);
            fieldInfo.setParentClassInfo(classInfo);
        }
        JaxbFieldNature jaxbFieldNature = new JaxbFieldNature(fieldInfo);
        if (javaNaming.isAddMethod(method)) {
            jaxbFieldNature.setMethodAdd(method);
            jaxbFieldNature.setMultivalued(true);
        } else if (javaNaming.isCreateMethod(method)) {
            jaxbFieldNature.setMethodCreate(method);
        } else if (javaNaming.isGetMethod(method)) {
            jaxbFieldNature.setMethodGet(method);
            handleMultivaluedness(method.getReturnType(), jaxbFieldNature, method.getGenericReturnType());
        } else if (javaNaming.isSetMethod(method)) {
            jaxbFieldNature.setMethodSet(method);
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1) {
                handleMultivaluedness(parameterTypes[0], jaxbFieldNature, method.getGenericParameterTypes()[0]);
            }
        } else if (javaNaming.isIsMethod(method)) {
            jaxbFieldNature.setMethodIs(method);
        } else {
            if (LOG.isDebugEnabled()) {
                String message = "Method: " + method + " is of unsupported type and ignored.";
                LOG.debug(message);
            }
        }
        fieldAnnotationProcessingService.processAnnotations(jaxbFieldNature, method.getAnnotations());
    }

    private void handleMultivaluedness(Class<?> fieldType, JaxbFieldNature fieldNature, Type type) {
        if (fieldType.isArray() || fieldType.isAssignableFrom(Collection.class)
                || Collection.class.isAssignableFrom(fieldType)) {
            fieldNature.setGenericType(type);
            fieldNature.setMultivalued(true);
        }

    }

    /**
     * Build the {@link PackageInfo} for a {@link Package}.
     * 
     * @param pkg
     *            the Package to describe
     * @return the PackageInfo build
     */
    private PackageInfo buildPackageInfo(final Package pkg) {
        PackageInfo packageInfo = createPackageInfo(pkg.getName());
        OoPackageNature ooPackageNature = new OoPackageNature(packageInfo);
        ooPackageNature.setPackage(pkg);
        packageAnnotationProcessingService.processAnnotations(new JaxbPackageNature(packageInfo), pkg.getAnnotations());
        return packageInfo;
    }

    public AnnotationProcessingService getPackageAnnotationProcessingService() {
        return packageAnnotationProcessingService;
    }

    public void setPackageAnnotationProcessingService(
            final AnnotationProcessingService packageAnnotationProcessingService) {
        this.packageAnnotationProcessingService = packageAnnotationProcessingService;
    }

    public AnnotationProcessingService getClassAnnotationProcessingService() {
        return classAnnotationProcessingService;
    }

    public void setClassAnnotationProcessingService(final AnnotationProcessingService classAnnotationProcessingService) {
        this.classAnnotationProcessingService = classAnnotationProcessingService;
    }

    public AnnotationProcessingService getFieldAnnotationProcessingService() {
        return fieldAnnotationProcessingService;
    }

    public void setFieldAnnotationProcessingService(final AnnotationProcessingService fieldAnnotationProcessingService) {
        this.fieldAnnotationProcessingService = fieldAnnotationProcessingService;
    }

    public JavaNaming getJavaNaming() {
        return javaNaming;
    }

    public void setJavaNaming(final JavaNaming javaNaming) {
        this.javaNaming = javaNaming;
    }

    /**
     * Creates a new {@link ClassInfo} with all Nature's registered.
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
     * Creates a new {@link FieldInfo} with all Nature's registered.
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
     * Creates a new {@link PackageInfo} with all Nature's registered.
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
