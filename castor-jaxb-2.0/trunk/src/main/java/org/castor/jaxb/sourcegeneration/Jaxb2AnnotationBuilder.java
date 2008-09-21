/*
 * Copyright 2008 Matthias Epheser
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
package org.castor.jaxb.sourcegeneration;

import java.util.ArrayList;

import org.exolab.castor.builder.AnnotationBuilder;
import org.exolab.castor.builder.info.ClassInfo;
import org.exolab.castor.builder.info.FieldInfo;
import org.exolab.castor.builder.info.XMLInfo;
import org.exolab.castor.builder.info.nature.XMLInfoNature;
import org.exolab.castor.builder.types.XSId;
import org.exolab.castor.builder.types.XSIdRef;
import org.exolab.castor.builder.types.XSIdRefs;
import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.javasource.JAnnotatedElement;
import org.exolab.javasource.JAnnotation;
import org.exolab.javasource.JAnnotationType;
import org.exolab.javasource.JClass;
import org.exolab.javasource.JEnum;
import org.exolab.javasource.JEnumConstant;
import org.exolab.javasource.JField;
import org.exolab.javasource.JMethod;

/**
 * <p> Implementation of {@link AnnotationBuilder} that creates JAXB Annotations for 
 * classes and fields. </p>
 * 
 * @author Matthias Epheser
 * @version $Id$
 *
 */
public class Jaxb2AnnotationBuilder implements AnnotationBuilder {

    /**
     * @see org.exolab.castor.builder.AnnotationBuilder#addClassAnnotations
     *  (org.exolab.castor.builder.info.ClassInfo, org.exolab.javasource.JClass)
     */
    public void addClassAnnotations(final ClassInfo classInfo, final JClass jClass) {

        XMLInfoNature xmlNature = new XMLInfoNature(classInfo);
        
        // add import for annotations
        jClass.addImport("javax.xml.bind.annotation.*");

        // @XmlType
        JAnnotationType requestType = new JAnnotationType("XmlType");
        JAnnotation request = new JAnnotation(requestType);
        request.setElementValue("name", "\"" + xmlNature.getNodeName() + "\"");

        // @propOrder
        if (xmlNature.isSequence()) {
            request.setElementValue("name", "\"" + xmlNature.getNodeName()
                    + "\"");
            FieldInfo[] fieldInfo = classInfo.getElementFields();
            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i < fieldInfo.length; i++) {
                FieldInfo fieldInfoSequenceElement = fieldInfo[i];
                arrayList.add("\"" + new XMLInfoNature(fieldInfoSequenceElement).getNodeName()
                        + "\"");
            }
            request.setElementValue("propOrder", (String[]) arrayList
                    .toArray(new String[arrayList.size()]));
        }

        jClass.addAnnotation(request);

        // @XmlRootElement
        /*
         * if (classInfo.isRootElement()) { JAnnotationType annotationType = new
         * JAnnotationType("XmlRootElement"); JAnnotation annotation = new
         * JAnnotation(annotationType); jClass.addAnnotation(annotation); }
         */

    }

    /**
     * @see org.exolab.castor.builder.AnnotationBuilder#addEnumAnnotations
     *  (org.exolab.castor.xml.schema.SimpleType, org.exolab.javasource.JEnum)
     */
    public void addEnumAnnotations(final SimpleType simpleType, final JEnum jEnum) {

        // add import for annotations
        jEnum.addImport("javax.xml.bind.annotation.*");

    }

    /**
     * @see org.exolab.castor.builder.AnnotationBuilder#addEnumConstantAnnotations
     *  (org.exolab.castor.xml.schema.Facet, org.exolab.javasource.JEnumConstant)
     */
    public void addEnumConstantAnnotations(final Facet facet,
            final JEnumConstant enumConstant) {
        JAnnotationType annotationType = new JAnnotationType("XmlEnumValue");
        JAnnotation annotation = new JAnnotation(annotationType);
        annotation.setValue("\"" + facet.getValue() + "\"");
        enumConstant.addAnnotation(annotation);
    }

    /**
     * @see org.exolab.castor.builder.AnnotationBuilder#addFieldAnnotations
     *  (org.exolab.castor.builder.info.FieldInfo, org.exolab.javasource.JField)
     */
    public void addFieldAnnotations(final FieldInfo fieldInfo, final JField field) {
        // TODO check jaxb field mode
        // this.addFieldAnnotations(fieldInfo, (JAnnotatedElement) field);
    }

    /**
     * @see org.exolab.castor.builder.AnnotationBuilder#addFieldGetterAnnotations
     *  (org.exolab.castor.builder.info.FieldInfo, org.exolab.javasource.JMethod)
     */
    public void addFieldGetterAnnotations(final FieldInfo fieldInfo, final JMethod method) {
        // TODO check jaxb field mode
        this.addFieldAnnotations(fieldInfo, (JAnnotatedElement) method);
    }

    /**
     * 
     * @param fieldInfo
     * @param element
     */
    private void addFieldAnnotations(final FieldInfo fieldInfo,
            final JAnnotatedElement element) {

        XMLInfoNature xmlNature = new XMLInfoNature(fieldInfo);
        
        // @XmlElement
        if (xmlNature.getNodeType() == XMLInfo.ELEMENT_TYPE
                && !xmlNature.isMultivalued()) {
            JAnnotationType annotationType = new JAnnotationType("XmlElement");
            JAnnotation annotation = new JAnnotation(annotationType);
            annotation.setElementValue("name", "\"" + xmlNature.getNodeName()
                    + "\"");
            if (xmlNature.getNamespacePrefix() != null) {
                annotation.setElementValue("namespace", "\""
                        + xmlNature.getNamespacePrefix() + "\"");
            }
            annotation.setElementValue("nillable", fieldInfo.isNillable() + "");
            element.addAnnotation(annotation);
        }

        // @XmlAttribute
        else if (xmlNature.getNodeType() == XMLInfo.ATTRIBUTE_TYPE) {
            JAnnotationType annotationType = new JAnnotationType("XmlAttribute");
            JAnnotation annotation = new JAnnotation(annotationType);
            annotation.setElementValue("name", "\"" + xmlNature.getNodeName()
                    + "\"");
            if (xmlNature.getNamespacePrefix() != null) {
                annotation.setElementValue("namespace", "\""
                        + xmlNature.getNamespacePrefix() + "\"");
            }
            annotation.setElementValue("required", xmlNature.isRequired() + "");
            element.addAnnotation(annotation);
        }

        // @XmlId
        if (xmlNature.getSchemaType() instanceof XSId) {
            JAnnotationType annotationType = new JAnnotationType("XmlID");
            JAnnotation annotation = new JAnnotation(annotationType);
            element.addAnnotation(annotation);
        }

        // @XmlIdRef
        else if (xmlNature.getSchemaType() instanceof XSIdRef) {
            JAnnotationType annotationType = new JAnnotationType("XmlIDREF");
            JAnnotation annotation = new JAnnotation(annotationType);
            element.addAnnotation(annotation);
        }

        // @XmlIdRefs
        else if (xmlNature.getSchemaType() instanceof XSIdRefs) {
            JAnnotationType annotationType = new JAnnotationType("XmlIDREF");
            JAnnotation annotation = new JAnnotation(annotationType);
            element.addAnnotation(annotation);
        }
    }

}
