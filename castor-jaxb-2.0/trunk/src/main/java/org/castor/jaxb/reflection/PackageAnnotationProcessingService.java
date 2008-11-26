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
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSchemaTypes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbPackageNature;

/**
 * A service class to precess all package level annotations.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class PackageAnnotationProcessingService extends
        XMLBaseAnnotationProcessingService {
    /** The Logger to use. */
    private static final Log LOG = LogFactory
            .getLog(PackageAnnotationProcessingService.class);
    /** Default for names. */
    private static final String ANNOTATION_PROPERTY_NAME_DEFAULT = "##default";
    /** Default for namespace. */
    private static final String ANNOTATION_PROPERTY_NAMESPACE_DEFAULT = "##default";

    /**
     * Constructs a AnnotationProcessingService what means to register all
     * available AnnotationProcessing classes.
     */
    public PackageAnnotationProcessingService() {
        addAnnotationProcessor(new XmlSchemaProcessor());
        addAnnotationProcessor(new XmlAccessorTypeProcessor());
        addAnnotationProcessor(new XmlAccessorOrderProcessor());
        addAnnotationProcessor(new XmlSchemaTypeProcessor());
        addAnnotationProcessor(new XmlSchemaTypesProcessor());
    }

    /**
     * Annotation processor for XmlSchema.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlSchemaProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.ReflectionInfo,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlSchema)
                    && (info instanceof JaxbPackageNature)) {
                XmlSchema xmlSchema = (XmlSchema) annotation;
                JaxbPackageNature packageInfo = (JaxbPackageNature) info;
                annotationVisitMessage(LOG, xmlSchema);
                //
                packageInfo.addSchemaNsArray(xmlSchema.xmlns());
                packageInfo.setSchemaNamespace(xmlSchema.namespace());
                packageInfo
                        .setSchemaElementForm(xmlSchema.elementFormDefault());
                packageInfo.setSchemaAttributeForm(xmlSchema
                        .attributeFormDefault());
                return true;
            }
            return false;
        } // -- processAnnotation

        /**
         * {@inheritDoc}
         * 
         * @see AnnotationProcessor#forAnnotationClass()
         */
        public final Class<? extends Annotation> forAnnotationClass() {
            return XmlSchema.class;
        }
    }

    /**
     * Annotation processor for XmlAccessorType.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAccessorTypeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.NatureExtendable,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlAccessorType)
                    && (info instanceof JaxbPackageNature)) {
                XmlAccessorType xmlAccessorType = (XmlAccessorType) annotation;
                JaxbPackageNature packageInfo = (JaxbPackageNature) info;
                annotationVisitMessage(LOG, xmlAccessorType);
                //
                packageInfo.setAccessType(xmlAccessorType.value());
                // NONE, PROPERTY, FIELD, PUBLIC_MEMBER
                return true;
            }
            return false;
        } // -- processAnnotation

        /**
         * {@inheritDoc}
         * 
         * @see AnnotationProcessor#forAnnotationClass()
         */
        public final Class<? extends Annotation> forAnnotationClass() {
            return XmlAccessorType.class;
        }
    }

    /**
     * Annotation processor for XmlAccessorOrder.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAccessorOrderProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlAccessorOrder)
                    && (info instanceof JaxbPackageNature)) {
                XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder) annotation;
                JaxbPackageNature packageInfo = (JaxbPackageNature) info;
                annotationVisitMessage(LOG, xmlAccessorOrder);
                //
                packageInfo.setAccessOrder(xmlAccessorOrder.value());
                // UNDEFINED, ALPHABETICAL
                return true;
            }
            return false;
        } // -- processAnnotation

        /**
         * {@inheritDoc}
         * 
         * @see AnnotationProcessor#forAnnotationClass()
         */
        public final Class<? extends Annotation> forAnnotationClass() {
            return XmlAccessorOrder.class;
        }
    }

    /**
     * Annotation processor for XmlSchemaType.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlSchemaTypeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlSchemaType)
                    && (info instanceof JaxbPackageNature)) {
                XmlSchemaType xmlSchemaType = (XmlSchemaType) annotation;
                JaxbPackageNature packageInfo = (JaxbPackageNature) info;
                annotationVisitMessage(LOG, xmlSchemaType);
                //
                packageInfo.addSchemaType(xmlSchemaType.name(), xmlSchemaType
                        .namespace(), xmlSchemaType.type());
                return true;
            }
            return false;
        } // -- processAnnotation

        /**
         * {@inheritDoc}
         * 
         * @see AnnotationProcessor#forAnnotationClass()
         */
        public final Class<? extends Annotation> forAnnotationClass() {
            return XmlSchemaType.class;
        }
    }

    /**
     * Annotation processor for XmlSchemaTypes.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlSchemaTypesProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlSchemaTypes)
                    && (info instanceof JaxbPackageNature)) {
                XmlSchemaTypes xmlSchemaTypes = (XmlSchemaTypes) annotation;
                JaxbPackageNature packageInfo = (JaxbPackageNature) info;
                annotationVisitMessage(LOG, xmlSchemaTypes);
                //
                packageInfo.addSchemaTypeArray(xmlSchemaTypes.value());
                return true;
            }
            return false;
        } // -- processAnnotation

        /**
         * {@inheritDoc}
         * 
         * @see AnnotationProcessor#forAnnotationClass()
         */
        public Class<? extends Annotation> forAnnotationClass() {
            return XmlSchemaTypes.class;
        }
    }
}
