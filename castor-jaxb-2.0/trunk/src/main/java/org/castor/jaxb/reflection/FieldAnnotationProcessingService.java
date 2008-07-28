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

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.ReflectionInfo;


/**
 * To process all field (attribute and method) specific annotations
 * and put the results info FieldInfo instances.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class FieldAnnotationProcessingService extends AnnotationProcessingService {
    /** Logger to be used. */
    private static final Log LOG = LogFactory.getLog(FieldAnnotationProcessingService.class);

    /**
     * Constructs a AnnotationProcessingService what means to register all available
     * AnnotationProcessing classes.
     */
    public FieldAnnotationProcessingService() {
        addAnnotationProcessor(new XmlElementProcessor());
        addAnnotationProcessor(new XmlRootElementProcessor());
        addAnnotationProcessor(new XmlElementsProcessor());
        addAnnotationProcessor(new XmlElementRefProcessor());
        addAnnotationProcessor(new XmlElementRefsProcessor());
        addAnnotationProcessor(new XmlElementWrapperProcessor());
        addAnnotationProcessor(new XmlAnyElementProcessor());
        addAnnotationProcessor(new XmlAttributeProcessor());
        addAnnotationProcessor(new XmlAnyAttributeProcessor());
        addAnnotationProcessor(new XmlTransientProcessor());
        addAnnotationProcessor(new XmlValueProcessor());
        addAnnotationProcessor(new XmlIDProcessor());
        addAnnotationProcessor(new XmlIDREFProcessor());
        addAnnotationProcessor(new XmlListProcessor());
        addAnnotationProcessor(new XmlMixedProcessor());
        addAnnotationProcessor(new XmlMimeTypeProcessor());
        addAnnotationProcessor(new XmlAttachmentRefProcessor());
        addAnnotationProcessor(new XmlInlineBinaryDataProcessor());
        addAnnotationProcessor(new XmlEnumValueProcessor());
    }

    /**
     * Annotation processor for XMLElement.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlElementProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlElement) && (info instanceof FieldInfo)) {
                XmlElement xmlElement = (XmlElement) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlElement);
                //
                fieldInfo.setElementName(xmlElement.name());
                fieldInfo.setElementNillable(xmlElement.nillable());
                fieldInfo.setElementRequired(xmlElement.required());
                fieldInfo.setElementNamespace(xmlElement.namespace());
                if (XmlElement.DEFAULT.class.equals(xmlElement.type())) {
                    fieldInfo.setElementType(null);
                } else {
                    fieldInfo.setElementType(xmlElement.type());
                }
                fieldInfo.setElementDefaultValue(xmlElement.defaultValue());
            }
        }

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlElement.class;
        }
    }

    /**
     * Annotation processor for XMLRootElement.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlRootElementProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlRootElement) && (info instanceof FieldInfo)) {
                XmlRootElement xmlRootElement = (XmlRootElement) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlRootElement);
                //
                fieldInfo.setRootElementName(xmlRootElement.name());
                fieldInfo.setRootElementNamespace(xmlRootElement.namespace());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlRootElement.class;
        } //-- forAnnotationClass
    } //-- XmlRootElementProcessor

    /**
     * Annotation processor for XMLElements.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementsProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlElements) && (info instanceof FieldInfo)) {
                XmlElements xmlElements = (XmlElements) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlElements);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
//                XmlElement[] xmlElementArray = xmlElements.value();
//                for (int i = 0; i < xmlElementArray.length; i++) {
//                    XmlElement xmlElement = xmlElementArray[i];
//                    fieldInfo.addElement(
//                            xmlElement.name(),
//                            xmlElement.nillable(),
//                            xmlElement.required(),
//                            xmlElement.namespace(),
//                            xmlElement.type(),
//                            xmlElement.defaultValue());
//                }
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlElements.class;
        } //-- forAnnotationClass
    } //-- XmlElementsProcessor

    /**
     * Annotation processor for XmlElementRef.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementRefProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlElementRef) && (info instanceof FieldInfo)) {
                XmlElementRef xmlElementRef = (XmlElementRef) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlElementRef);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
//                fieldInfo.addElementRef(
//                        xmlElementRef.name(), xmlElementRef.namespace(), xmlElementRef.type());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlElementRef.class;
        } //-- forAnnotationClass
    } //-- XmlElementRefProcessor

    /**
     * Annotation processor for XmlElementRefs.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementRefsProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlElementRefs) && (info instanceof FieldInfo)) {
                XmlElementRefs xmlElementRefs = (XmlElementRefs) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlElementRefs);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
//                //
//                XmlElementRef[] xmlElementRefArray = xmlElementRefs.value();
//                for (int i = 0; i < xmlElementRefArray.length; i++) {
//                    XmlElementRef xmlElementRef = xmlElementRefArray[i];
//                    fieldInfo.addElementRef(
//                            xmlElementRef.name(), xmlElementRef.namespace(), xmlElementRef.type());
//                }
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlElementRefs.class;
        } //-- forAnnotationClass
    } //-- XmlElementRefsProcessor

    /**
     * Annotation processor for XMLElement.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementWrapperProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlElementWrapper) && (info instanceof FieldInfo)) {
                XmlElementWrapper xmlElementWrapper = (XmlElementWrapper) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlElementWrapper);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
//                //
//                fieldInfo.setElement(true);
//                fieldInfo.setXmlName(xmlElementWrapper.name());
//                fieldInfo.setNillable(xmlElementWrapper.nillable());
//                fieldInfo.setRequired(xmlElementWrapper.required());
//                fieldInfo.setNamespace(xmlElementWrapper.namespace());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlElementWrapper.class;
        } //-- forAnnotationClass
    } //-- XmlElementWrapperProcessor

    /**
     * Annotation processor for XmlAnyElement.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAnyElementProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlAnyElement) && (info instanceof FieldInfo)) {
                XmlAnyElement xmlAnyElement = (XmlAnyElement) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlAnyElement);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
//                //
//                fieldInfo.setAnyElement(true);
//                fieldInfo.setLax(xmlAnyElement.lax());
//                fieldInfo.setDomHandler(xmlAnyElement.value());
//                Class<? extends DomHandler> 
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlAnyElement.class;
        } //-- forAnnotationClass
    } //-- XmlAnyElementProcessor

    /**
     * Annotation processor for XmlAttribute.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAttributeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlAttribute) && (info instanceof FieldInfo)) {
                XmlAttribute xmlAttribute = (XmlAttribute) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlAttribute);
                //
                fieldInfo.setAttributeName(xmlAttribute.name());
                fieldInfo.setAttributeNamespace(xmlAttribute.namespace());
                fieldInfo.setAttributeRequired(xmlAttribute.required());
                fieldInfo.setAnnotatedXmlAttribute(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlAttribute.class;
        } //-- forAnnotationClass
    } //-- XmlAttributeProcessor

    /**
     * Annotation processor for XmlAnyAttribute.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAnyAttributeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlAnyAttribute) && (info instanceof FieldInfo)) {
                XmlAnyAttribute xmlAnyAttribute = (XmlAnyAttribute) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlAnyAttribute);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setAnyAttribute(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlAnyAttribute.class;
        } //-- forAnnotationClass
    } //-- XmlAnyAttributeProcessor

    /**
     * Annotation processor for XmlTransient.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlTransientProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlTransient) && (info instanceof FieldInfo)) {
                XmlTransient xmlTransient = (XmlTransient) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlTransient);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setTransient(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlTransient.class;
        } //-- forAnnotationClass
    } //-- XmlTransientProcessor

    /**
     * Annotation processor for XmlValue.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlValueProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlValue) && (info instanceof FieldInfo)) {
                XmlValue xmlValue = (XmlValue) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlValue);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setValue(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlValue.class;
        } //-- forAnnotationClass
    } //-- XmlValueProcessor

    /**
     * Annotation processor for XmlID.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlIDProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlID) && (info instanceof FieldInfo)) {
                XmlID xmlID = (XmlID) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlID);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setID(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlID.class;
        } //-- forAnnotationClass
    } //-- XmlIDProcessor

    /**
     * Annotation processor for XmlIDREF.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlIDREFProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlIDREF) && (info instanceof FieldInfo)) {
                XmlIDREF xmlIDREF = (XmlIDREF) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlIDREF);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setIDREF(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlIDREF.class;
        } //-- forAnnotationClass
    } //-- XmlIDREFProcessor

    /**
     * Annotation processor for XmlList.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlListProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlList) && (info instanceof FieldInfo)) {
                XmlList xmlList = (XmlList) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlList);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setList(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlList.class;
        } //-- forAnnotationClass
    } //-- XmlListProcessor

    /**
     * Annotation processor for XmlMixed.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlMixedProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlMixed) && (info instanceof FieldInfo)) {
                XmlMixed xmlMixed = (XmlMixed) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlMixed);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setMixed(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlMixed.class;
        } //-- forAnnotationClass
    } //-- XmlMixedProcessor

    /**
     * Annotation processor for XmlMimeType.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlMimeTypeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlMimeType) && (info instanceof FieldInfo)) {
                XmlMimeType xmlMimeType = (XmlMimeType) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlMimeType);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
//                fieldInfo.setMimeType(xmlMimeType.value());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlMimeType.class;
        } //-- forAnnotationClass
    } //-- XmlMimeTypeProcessor

    /**
     * Annotation processor for XmlAttachmentRef.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAttachmentRefProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlAttachmentRef) && (info instanceof FieldInfo)) {
                XmlAttachmentRef xmlAttachmentRef = (XmlAttachmentRef) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlAttachmentRef);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setAttachmentRef(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlAttachmentRef.class;
        } //-- forAnnotationClass
    } //-- XmlAttachmentRefProcessor

    /**
     * Annotation processor for XmlInlineBinaryData.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlInlineBinaryDataProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlInlineBinaryData) && (info instanceof FieldInfo)) {
                XmlInlineBinaryData xmlInlineBinaryData = (XmlInlineBinaryData) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlInlineBinaryData);
                LOG.warn("Processing of annotation: " 
                        + annotation.getClass().getName() 
                        + " is not yet implemented");
                //
                // no properties
//                fieldInfo.setInlineBinaryData(true);
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.FieldAnnotationProcessingService.
         * AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlInlineBinaryData.class;
        } //-- forAnnotationClass
    } //-- XmlInlineBinaryDataProcessor

    /**
     * Annotation processor for XmlEnumValue.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlEnumValueProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.AnnotationProcessingService.AnnotationProcessor#
         * processAnnotation(org.castor.xml.introspection.ReflectionInfo, java.lang.annotation.Annotation)
         */
        public final < I extends ReflectionInfo, A extends Annotation > 
        void processAnnotation(final I info, final A annotation) {
            if ((annotation instanceof XmlEnumValue) && (info instanceof FieldInfo)) {
                XmlEnumValue xmlEnumValue = (XmlEnumValue) annotation;
                FieldInfo fieldInfo = (FieldInfo) info;
                annotationVisitMessage(LOG, xmlEnumValue);
                fieldInfo.setEnumValue(xmlEnumValue.value());
            }
        } //-- processAnnotation

        /**
         * {@inheritDoc}
         * @see org.castor.jaxb.reflection.EnumAnnotationProcessingService
         * .AnnotationProcessor#forAnnotationClass()
         */
        public Class < ? extends Annotation > forAnnotationClass() {
            return XmlEnumValue.class;
        } //-- forAnnotationClass
    } //-- XmlEnumValueProcessor
} //-- AnnotationProcessingService
