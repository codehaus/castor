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

import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.ReflectionInfo;

/**
 * A class to process all class related annotations and put the results into
 * a ClassInfo instance.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class ClassAnnotationProcessingService extends AnnotationProcessingService {
    /** Logging to use. */
    private static final Log LOG = LogFactory.getLog(ClassAnnotationProcessingService.class);
    /** Default String for name property. */
    protected static final String ANNOTATION_PROPERTY_NAME_DEFAULT = "##default";
    /** Default String for namespace property. */
    protected static final String ANNOTATION_PROPERTY_NAMESPACE_DEFAULT = "##default";

    /**
     * Constructs a AnnotationProcessingService what means to register all available
     * AnnotationProcessing classes.
     */
    public ClassAnnotationProcessingService() {
        addAnnotationProcessor(new XmlTypeProcessor());
        addAnnotationProcessor(new XmlRootElementProcessor());
        addAnnotationProcessor(new XmlTransientProcessor());
        addAnnotationProcessor(new XmlSeeAlsoProcessor());
        addAnnotationProcessor(new XmlAccessorTypeProcessor());
        addAnnotationProcessor(new XmlAccessorOrderProcessor());
        addAnnotationProcessor(new XmlEnumProcessor());
    }

    /**
     * Annotation processor for XMLType.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlTypeProcessor implements AnnotationProcessingService.AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlType) && (info instanceof ClassInfo)) {
                XmlType xmlType = (XmlType) annotation;
                ClassInfo classInfo = (ClassInfo) info;
                annotationVisitMessage(LOG, xmlType);
                //
                classInfo.setTypeName(xmlType.name());
                classInfo.setTypeProperties(xmlType.propOrder());
                classInfo.setTypeNamespace(xmlType.namespace());
                classInfo.setTypeFactoryClass(xmlType.factoryClass());
                classInfo.setTypeFactoryMethod(xmlType.factoryMethod());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.ClassAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlType.class;
        } //-- forAnnotationClass
    } //-- XmlTypeProcessor

    /**
     * Annotation processor for XMLRootElement.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlRootElementProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlRootElement) && (info instanceof ClassInfo)) {
                XmlRootElement xmlRootElement = (XmlRootElement) annotation;
                ClassInfo classInfo = (ClassInfo) info;
                annotationVisitMessage(LOG, xmlRootElement);
                classInfo.setRootElementName(xmlRootElement.name());
                classInfo.setRootElementNamespace(xmlRootElement.namespace());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.ClassAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlRootElement.class;
        } //-- forAnnotationClass
    } //-- XmlRootElementProcessor

    /**
     * Annotation processor for XmlTransient.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlTransientProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlTransient) && (info instanceof ClassInfo)) {
                XmlTransient xmlTransient = (XmlTransient) annotation;
                ClassInfo classInfo = (ClassInfo) info;
                annotationVisitMessage(LOG, xmlTransient);
                classInfo.setTransient(true);
                // no annotation properties
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.ClassAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlTransient.class;
        } //-- forAnnotationClass
    } //-- XmlTransientProcessor

    /**
     * Annotation processor for XmlSeeAlso.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlSeeAlsoProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlSeeAlso) && (info instanceof ClassInfo)) {
                XmlSeeAlso xmlSeeAlso = (XmlSeeAlso) annotation;
                ClassInfo classInfo = (ClassInfo) info;
                annotationVisitMessage(LOG, xmlSeeAlso);
                classInfo.setSeeAlsoClasses(xmlSeeAlso.value());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.ClassAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlSeeAlso.class;
        } //-- forAnnotationClass
    } //-- XmlSeeAlsoProcessor

    /**
     * Annotation processor for XmlAccessorType.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlAccessorTypeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlAccessorType) && (info instanceof ClassInfo)) {
                XmlAccessorType xmlAccessorType = (XmlAccessorType) annotation;
                ClassInfo classInfo = (ClassInfo) info;
                annotationVisitMessage(LOG, xmlAccessorType);
                classInfo.setXmlAccessType(xmlAccessorType.value());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.PackageAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlAccessorType.class;
        } //-- forAnnotationClass
    } //-- XmlAccessorTypeProcessor

    /**
     * Annotation processor for XmlAccessorOrder.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlAccessorOrderProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlAccessorOrder) && (info instanceof ClassInfo)) {
                XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder) annotation;
                ClassInfo classInfo = (ClassInfo) info;
                annotationVisitMessage(LOG, xmlAccessorOrder);
                classInfo.setXmlAccessOrder(xmlAccessorOrder.value());
            }
        }

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.PackageAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlAccessorOrder.class;
        }
    }

    /**
     * Annotation processor for XmlEnum.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlEnumProcessor implements AnnotationProcessingService.AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlEnum) && (info instanceof ClassInfo)) {
                XmlEnum xmlEnum = (XmlEnum) annotation;
                ClassInfo classInfo = (ClassInfo) info;
                annotationVisitMessage(LOG, xmlEnum);
                classInfo.setEnumClass(xmlEnum.value());
            }
        }

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.EnumAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlEnum.class;
        }
    }
} //-- AnnotationProcessingService
