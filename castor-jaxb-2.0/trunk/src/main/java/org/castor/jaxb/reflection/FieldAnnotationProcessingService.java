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

import javax.xml.bind.annotation.W3CDomHandler;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To process all field (attribute and method) specific annotations and put the
 * results info FieldInfo instances.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class FieldAnnotationProcessingService extends
        XMLBaseAnnotationProcessingService {

    public final Logger LOG = LoggerFactory.getLogger(this.getClass());
    /** XmlElement.name default is ##default. */
    private static final String ELEMENT_NAME_DEFAULT = "##default";
    /** XmlElement.namespace default is empty string. */
    private static final String ELEMENT_NAMESPACE_DEFAULT = "";
    /** XmlElement.defaultValue default is \u0000. */
    private static final String ELEMENT_DEFAULT_VALUE_DEFAULT = "\u0000";
    /** XmlElementRef.name default is ##default. */
    private static final String ELEMENT_REF_NAME_DEFAULT = "##default";
    /** XmlElementRef.namespace default is ##default. */
    private static final String ELEMENT_REF_NAMESPACE_DEFAULT = "";
    /** XmlElementWrapper.name default is ##default. */
    private static final String ELEMENT_WRAPPER_NAME_DEFAULT = "##default";
    /** XmlElementWrapper.namespace default is ##default. */
    private static final String ELEMENT_WRAPPER_NAMESPACE_DEFAULT = "##default";
    /** XmlAnyElement.value default is W3CDomHandler. */
    private static final Class<?> ANY_ELEMENT_DOMHANDLER_DEFAULT = W3CDomHandler.class;
    /** XmlAttribute.name default is ##default. */
    private static final String ATTRIBUTE_NAME_DEFAULT = "##default";
    /** XmlAttribute.namespace default is ##default. */
    private static final String ATTRIBUTE_NAMESPACE_DEFAULT = "##default";

    /**
     * Constructs a AnnotationProcessingService what means to register all
     * available AnnotationProcessing classes.
     */
    public FieldAnnotationProcessingService() {
        super();
        addAnnotationProcessor(new XmlElementProcessor());
        // no root element on a field!! addAnnotationProcessor(new
        // XmlRootElementProcessor());
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
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.ReflectionInfo,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlElement)
                    && (info instanceof JaxbFieldNature)) {
                XmlElement xmlElement = (XmlElement) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlElement);
                //
                fieldInfo.setXmlElement(true);
                if (!ELEMENT_NAME_DEFAULT.equals(xmlElement.name())) {
                    fieldInfo.setElementName(xmlElement.name());
                }
                fieldInfo.setElementNillable(xmlElement.nillable());
                fieldInfo.setElementRequired(xmlElement.required());
                if (!ELEMENT_NAMESPACE_DEFAULT.equals(xmlElement.namespace())) {
                    fieldInfo.setElementNamespace(xmlElement.namespace());
                }
                if (!XmlElement.DEFAULT.class.equals(xmlElement.type())) {
                    fieldInfo.setElementType(xmlElement.type());
                }
                if (!ELEMENT_DEFAULT_VALUE_DEFAULT.equals(xmlElement
                        .defaultValue())) {
                    fieldInfo.setElementDefaultValue(xmlElement.defaultValue());
                }
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
            return XmlElement.class;
        }
    }

    /**
     * Annotation processor for XMLElements.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementsProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlElements)
                    && (info instanceof JaxbFieldNature)) {
                XmlElements xmlElements = (XmlElements) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlElements);

                fieldInfo.setXmlElements(true);
                XmlElement[] xmlElementArray = xmlElements.value();
                for (int i = 0; i < xmlElementArray.length; i++) {
                    XmlElement xmlElement = xmlElementArray[i];
                    fieldInfo.addElement((!ELEMENT_NAME_DEFAULT
                            .equals(xmlElement.name())) ? xmlElement.name()
                            : null, (!ELEMENT_NAMESPACE_DEFAULT
                            .equals(xmlElement.namespace())) ? xmlElement
                            .name() : null, xmlElement.nillable(), xmlElement
                            .required(), (!XmlElement.DEFAULT.class
                            .equals(xmlElement.type())) ? xmlElement.type()
                            : null, (!ELEMENT_DEFAULT_VALUE_DEFAULT
                            .equals(xmlElement.defaultValue())) ? xmlElement
                            .defaultValue() : null);
                }
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
            return XmlElements.class;
        } // -- forAnnotationClass
    } // -- XmlElementsProcessor

    /**
     * Annotation processor for XmlElementRef.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementRefProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlElementRef)
                    && (info instanceof JaxbFieldNature)) {
                XmlElementRef xmlElementRef = (XmlElementRef) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlElementRef);
                fieldInfo.setXmlElementRef(true);
                if (!ELEMENT_REF_NAME_DEFAULT.equals(xmlElementRef.name())) {
                    fieldInfo.setElementRefName(xmlElementRef.name());
                }
                if (!ELEMENT_REF_NAMESPACE_DEFAULT.equals(xmlElementRef
                        .namespace())) {
                    fieldInfo.setElementRefNamespace(xmlElementRef.namespace());
                }
                if (!XmlElementRef.DEFAULT.class.equals(xmlElementRef.type())) {
                    fieldInfo.setElementRefType(xmlElementRef.type());
                }
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
            return XmlElementRef.class;
        } // -- forAnnotationClass
    } // -- XmlElementRefProcessor

    /**
     * Annotation processor for XmlElementRefs.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementRefsProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlElementRefs)
                    && (info instanceof JaxbFieldNature)) {
                XmlElementRefs xmlElementRefs = (XmlElementRefs) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlElementRefs);
                fieldInfo.setXmlElementRefs(true);
                //
                XmlElementRef[] xmlElementRefArray = xmlElementRefs.value();
                for (int i = 0; i < xmlElementRefArray.length; i++) {
                    XmlElementRef xmlElementRef = xmlElementRefArray[i];
                    fieldInfo
                            .addElementRefsElementRef(
                                    (!ELEMENT_REF_NAME_DEFAULT
                                            .equals(xmlElementRef.name())) ? xmlElementRef
                                            .name()
                                            : null,
                                    (!ELEMENT_REF_NAMESPACE_DEFAULT
                                            .equals(xmlElementRef.namespace())) ? xmlElementRef
                                            .namespace()
                                            : null,
                                    (!XmlElementRef.DEFAULT.class
                                            .equals(xmlElementRef.type())) ? xmlElementRef
                                            .type()
                                            : null);
                }
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
            return XmlElementRefs.class;
        } // -- forAnnotationClass
    } // -- XmlElementRefsProcessor

    /**
     * Annotation processor for XMLElement.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlElementWrapperProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlElementWrapper)
                    && (info instanceof JaxbFieldNature)) {
                XmlElementWrapper xmlElementWrapper = (XmlElementWrapper) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlElementWrapper);
                fieldInfo.setXmlElementWrapper(true);
                if (!ELEMENT_WRAPPER_NAME_DEFAULT.equals(xmlElementWrapper
                        .name())) {
                    fieldInfo.setElementWrapperName(xmlElementWrapper.name());
                }
                fieldInfo.setElementWrapperNillable(xmlElementWrapper
                        .nillable());
                fieldInfo.setElementWrapperRequired(xmlElementWrapper
                        .required());
                if (!ELEMENT_WRAPPER_NAMESPACE_DEFAULT.equals(xmlElementWrapper
                        .namespace())) {
                    fieldInfo.setElementWrapperNamespace(xmlElementWrapper
                            .namespace());
                }
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
            return XmlElementWrapper.class;
        } // -- forAnnotationClass
    } // -- XmlElementWrapperProcessor

    /**
     * Annotation processor for XmlAnyElement.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAnyElementProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlAnyElement)
                    && (info instanceof JaxbFieldNature)) {
                XmlAnyElement xmlAnyElement = (XmlAnyElement) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlAnyElement);
                fieldInfo.setXmlAnyElement(true);
                fieldInfo.setAnyElementLax(xmlAnyElement.lax());
                if (!ANY_ELEMENT_DOMHANDLER_DEFAULT.equals(xmlAnyElement
                        .value())) {
                    fieldInfo.setAnyElementDomHandler(xmlAnyElement.value());
                }
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
            return XmlAnyElement.class;
        } // -- forAnnotationClass
    } // -- XmlAnyElementProcessor

    /**
     * Annotation processor for XmlAttribute.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAttributeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlAttribute)
                    && (info instanceof JaxbFieldNature)) {
                XmlAttribute xmlAttribute = (XmlAttribute) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlAttribute);
                fieldInfo.setXmlAttribute(true);
                if (!ATTRIBUTE_NAME_DEFAULT.equals(xmlAttribute.name())) {
                    fieldInfo.setAttributeName(xmlAttribute.name());
                }
                if (!ATTRIBUTE_NAMESPACE_DEFAULT.equals(xmlAttribute
                        .namespace())) {
                    fieldInfo.setAttributeNamespace(xmlAttribute.namespace());
                }
                fieldInfo.setAttributeRequired(xmlAttribute.required());
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
            return XmlAttribute.class;
        } // -- forAnnotationClass
    } // -- XmlAttributeProcessor

    /**
     * Annotation processor for XmlAnyAttribute.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAnyAttributeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlAnyAttribute)
                    && (info instanceof JaxbFieldNature)) {
                XmlAnyAttribute xmlAnyAttribute = (XmlAnyAttribute) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlAnyAttribute);
                fieldInfo.setXmlAnyAttribute(true);
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
            return XmlAnyAttribute.class;
        } // -- forAnnotationClass
    } // -- XmlAnyAttributeProcessor

    /**
     * Annotation processor for XmlTransient.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlTransientProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlTransient)
                    && (info instanceof JaxbFieldNature)) {
                XmlTransient xmlTransient = (XmlTransient) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlTransient);
                fieldInfo.setXmlTransient(true);
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
            return XmlTransient.class;
        } // -- forAnnotationClass
    } // -- XmlTransientProcessor

    /**
     * Annotation processor for XmlValue.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlValueProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlValue)
                    && (info instanceof JaxbFieldNature)) {
                XmlValue xmlValue = (XmlValue) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlValue);
                fieldInfo.setXmlValue(true);
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
            return XmlValue.class;
        } // -- forAnnotationClass
    } // -- XmlValueProcessor

    /**
     * Annotation processor for XmlID.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlIDProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlID)
                    && (info instanceof JaxbFieldNature)) {
                XmlID xmlID = (XmlID) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlID);
                fieldInfo.setXmlID(true);
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
            return XmlID.class;
        } // -- forAnnotationClass
    } // -- XmlIDProcessor

    /**
     * Annotation processor for XmlIDREF.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlIDREFProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlIDREF)
                    && (info instanceof JaxbFieldNature)) {
                XmlIDREF xmlIDREF = (XmlIDREF) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlIDREF);
                fieldInfo.setXmlIDREF(true);
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
            return XmlIDREF.class;
        } // -- forAnnotationClass
    } // -- XmlIDREFProcessor

    /**
     * Annotation processor for XmlList.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlListProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlList)
                    && (info instanceof JaxbFieldNature)) {
                XmlList xmlList = (XmlList) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlList);
                fieldInfo.setXmlList(true);
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
            return XmlList.class;
        } // -- forAnnotationClass
    } // -- XmlListProcessor

    /**
     * Annotation processor for XmlMixed.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlMixedProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlMixed)
                    && (info instanceof JaxbFieldNature)) {
                XmlMixed xmlMixed = (XmlMixed) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlMixed);
                fieldInfo.setXmlMixed(true);
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
            return XmlMixed.class;
        } // -- forAnnotationClass
    } // -- XmlMixedProcessor

    /**
     * Annotation processor for XmlMimeType.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlMimeTypeProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlMimeType)
                    && (info instanceof JaxbFieldNature)) {
                XmlMimeType xmlMimeType = (XmlMimeType) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlMimeType);
                fieldInfo.setXmlMimeType(true);
                fieldInfo.setMimeType(xmlMimeType.value());
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
            return XmlMimeType.class;
        } // -- forAnnotationClass
    } // -- XmlMimeTypeProcessor

    /**
     * Annotation processor for XmlAttachmentRef.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlAttachmentRefProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlAttachmentRef)
                    && (info instanceof JaxbFieldNature)) {
                XmlAttachmentRef xmlAttachmentRef = (XmlAttachmentRef) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlAttachmentRef);
                fieldInfo.setXmlAttachmentRef(true);
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
            return XmlAttachmentRef.class;
        } // -- forAnnotationClass
    } // -- XmlAttachmentRefProcessor

    /**
     * Annotation processor for XmlInlineBinaryData.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public class XmlInlineBinaryDataProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlInlineBinaryData)
                    && (info instanceof JaxbFieldNature)) {
                XmlInlineBinaryData xmlInlineBinaryData = (XmlInlineBinaryData) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlInlineBinaryData);
                fieldInfo.setXmlInlineBinaryData(true);
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
            return XmlInlineBinaryData.class;
        } // -- forAnnotationClass
    } // -- XmlInlineBinaryDataProcessor

    /**
     * Annotation processor for XmlEnumValue.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    class XmlEnumValueProcessor implements AnnotationProcessor {
        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.AnnotationProcessor#
         *      processAnnotation(org.castor.xml.introspection.BaseNature,
         *      java.lang.annotation.Annotation)
         */
        public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
                final I info, final A annotation) {
            if ((annotation instanceof XmlEnumValue)
                    && (info instanceof JaxbFieldNature)) {
                XmlEnumValue xmlEnumValue = (XmlEnumValue) annotation;
                JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
                annotationVisitMessage(LOG, xmlEnumValue);
                fieldInfo.setXmlEnumValue(true);
                fieldInfo.setEnumValue(xmlEnumValue.value());
                return true;
            }
            return false;
        } // -- processAnnotation

        /**
         * {@inheritDoc}
         * 
         * @see org.codehaus.castor.annoproc.EnumAnnotationProcessingService
         *      .AnnotationProcessor#forAnnotationClass()
         */
        public Class<? extends Annotation> forAnnotationClass() {
            return XmlEnumValue.class;
        } // -- forAnnotationClass
    } // -- XmlEnumValueProcessor
} // -- AnnotationProcessingService
