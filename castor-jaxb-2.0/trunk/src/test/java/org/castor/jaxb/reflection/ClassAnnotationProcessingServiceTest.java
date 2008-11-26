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

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.JaxbClassNature;

/**
 * To test all annotation processings ClassNonnotationProcessingService is capable of.
 * these are: XMLType, XmlRootElement, XmlTransient, XmlSeeAlso, XmlAccessorType and
 * XmlAccessorOrder.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class ClassAnnotationProcessingServiceTest extends TestCase {

    /**
     * @param name
     */
    public ClassAnnotationProcessingServiceTest(final String name) {
        super(name);
    }
    
    /**
     * Test method for {@link org.castor.jaxb.annoproc.BaseAnnotationProcessingService#processAnnotations(org.castor.jaxb.reflection.info.Info, 
     * java.lang.annotation.Annotation[])}.
     */
    public final void testProcessAnnotations() {
        Class < WithAnnotations > clazz = WithAnnotations.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo("Franz"));
        caps.processAnnotations(classInfo, annotations);
        Assert.assertEquals("Hugo", classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        Assert.assertEquals("Franz", classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
    }

    private static class WithoutAnnotations {
        
    }

    @XmlType(name = "Franz") //, namespace="", propOrder="", factoryClass="", factoryMethod=""
    @XmlRootElement(name = "Hugo")
    private static class WithAnnotations {
        
    }
    
    @XmlType(name = "Franz") //, namespace="", propOrder="", factoryClass="", factoryMethod=""
    private static class WithXmlTypeAnnotation {
        
    }
    
    /**
     * Test method for {@link org.castor.jaxb.annoproc.BaseAnnotationProcessingService#processAnnotations(org.castor.jaxb.reflection.info.Info, 
     * java.lang.annotation.Annotation[])}.
     */
    public final void testProcessAnnotationsWithXmlTypeAnnotation() {
        Class < WithXmlTypeAnnotation > clazz = WithXmlTypeAnnotation.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo(WithXmlTypeAnnotation.class.getName()));
        caps.processAnnotations(classInfo, annotations);

        // XmlRootElement annotations
        Assert.assertNull(classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        // XmlType annotation
        Assert.assertNull(classInfo.getTypeFactoryClass());
        Assert.assertNull(classInfo.getTypeFactoryMethod());
        Assert.assertEquals("Franz", classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
        Assert.assertEquals(1, classInfo.getTypeProperties().size());
        Assert.assertEquals("", classInfo.getTypeProperties().get(0));
        // XmlTransient annotation
        Assert.assertFalse(classInfo.getXmlTransient());
        // XmlSeeAlso annotation
        Assert.assertNull(classInfo.getSeeAlsoClasses());
        // XmlAccessorOrder annotations
        Assert.assertNull(classInfo.getXmlAccessOrder());
        // XmlAccessorType annotations
        Assert.assertNull(classInfo.getXmlAccessType());
    }

    @XmlRootElement(name = "Hugo")
    private static class WithXmlRootElementAnnotation {
        
    }
    public final void testProcessAnnotationsWithXmlRootElementAnnotation() {
        Class < WithXmlRootElementAnnotation > clazz = WithXmlRootElementAnnotation.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo(WithXmlRootElementAnnotation.class.getName()));
        caps.processAnnotations(classInfo, annotations);

        // XmlRootElement annotations
        Assert.assertEquals("Hugo", classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        // XmlType annotation
        Assert.assertNull(classInfo.getTypeFactoryClass());
        Assert.assertNull(classInfo.getTypeFactoryMethod());
        Assert.assertNull(classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
        Assert.assertNull(classInfo.getTypeProperties());
        // XmlTransient annotation
        Assert.assertFalse(classInfo.getXmlTransient());
        // XmlSeeAlso annotation
        Assert.assertNull(classInfo.getSeeAlsoClasses());
        // XmlAccessorOrder annotations
        Assert.assertNull(classInfo.getXmlAccessOrder());
        // XmlAccessorType annotations
        Assert.assertNull(classInfo.getXmlAccessType());
    }

    @XmlTransient()
    private static class WithXmlTransientAnnotation {
        
    }
    public final void testProcessAnnotationsWithXmlTransientAnnotation() {
        Class < WithXmlTransientAnnotation > clazz = WithXmlTransientAnnotation.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo(WithXmlRootElementAnnotation.class.getName()));
        caps.processAnnotations(classInfo, annotations);

        // XmlRootElement annotations
        Assert.assertNull(classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        // XmlType annotation
        Assert.assertNull(classInfo.getTypeFactoryClass());
        Assert.assertNull(classInfo.getTypeFactoryMethod());
        Assert.assertNull(classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
        Assert.assertNull(classInfo.getTypeProperties());
        // XmlTransient annotation
        Assert.assertTrue(classInfo.getXmlTransient());
        // XmlSeeAlso annotation
        Assert.assertNull(classInfo.getSeeAlsoClasses());
        // XmlAccessorOrder annotations
        Assert.assertNull(classInfo.getXmlAccessOrder());
        // XmlAccessorType annotations
        Assert.assertNull(classInfo.getXmlAccessType());
    }

    @XmlSeeAlso(WithXmlTransientAnnotation.class)
    private static class WithXmlSeeAlsoAnnotation {
        
    }
    public final void testProcessAnnotationsWithXmlSeeAlsoAnnotation() {
        Class < WithXmlSeeAlsoAnnotation > clazz = WithXmlSeeAlsoAnnotation.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo(WithXmlRootElementAnnotation.class.getName()));
        caps.processAnnotations(classInfo, annotations);

        // XmlRootElement annotations
        Assert.assertNull(classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        // XmlType annotation
        Assert.assertNull(classInfo.getTypeFactoryClass());
        Assert.assertNull(classInfo.getTypeFactoryMethod());
        Assert.assertNull(classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
        Assert.assertNull(classInfo.getTypeProperties());
        // XmlTransient annotation
        Assert.assertFalse(classInfo.getXmlTransient());
        // XmlSeeAlso annotation
        Assert.assertEquals(1, classInfo.getSeeAlsoClasses().size());
        Assert.assertEquals(WithXmlTransientAnnotation.class, classInfo.getSeeAlsoClasses().get(0));
        // XmlAccessorOrder annotations
        Assert.assertNull(classInfo.getXmlAccessOrder());
        // XmlAccessorType annotations
        Assert.assertNull(classInfo.getXmlAccessType());
    }

    @XmlAccessorOrder()
    private static class WithXmlAccessorOrderAnnotation {
        
    }
    public final void testProcessAnnotationsWithXmlAccessorOrderAnnotation() {
        Class < WithXmlAccessorOrderAnnotation > clazz = WithXmlAccessorOrderAnnotation.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo(WithXmlAccessorOrderAnnotation.class.getName()));
        caps.processAnnotations(classInfo, annotations);

        // XmlRootElement annotations
        Assert.assertNull(classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        // XmlType annotation
        Assert.assertNull(classInfo.getTypeFactoryClass());
        Assert.assertNull(classInfo.getTypeFactoryMethod());
        Assert.assertNull(classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
        Assert.assertNull(classInfo.getTypeProperties());
        // XmlTransient annotation
        Assert.assertFalse(classInfo.getXmlTransient());
        // XmlSeeAlso annotation
        Assert.assertNull(classInfo.getSeeAlsoClasses());
        // XmlAccessorOrder annotations
        Assert.assertEquals(XmlAccessOrder.UNDEFINED, classInfo.getXmlAccessOrder());
        // XmlAccessorType annotations
        Assert.assertNull(classInfo.getXmlAccessType());
    }

    @XmlAccessorType()
    private static class WithXmlAccessorTypeAnnotation {
        
    }
    public final void testProcessAnnotationsWithXmlAccessorTypeAnnotation() {
        Class < WithXmlAccessorTypeAnnotation > clazz = WithXmlAccessorTypeAnnotation.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo(WithXmlAccessorTypeAnnotation.class.getName()));
        caps.processAnnotations(classInfo, annotations);

        // XmlRootElement annotations
        Assert.assertNull(classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        // XmlType annotation
        Assert.assertNull(classInfo.getTypeFactoryClass());
        Assert.assertNull(classInfo.getTypeFactoryMethod());
        Assert.assertNull(classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
        Assert.assertNull(classInfo.getTypeProperties());
        // XmlTransient annotation
        Assert.assertFalse(classInfo.getXmlTransient());
        // XmlSeeAlso annotation
        Assert.assertNull(classInfo.getSeeAlsoClasses());
        // XmlAccessorOrder annotations
        Assert.assertNull(classInfo.getXmlAccessOrder());
        // XmlAccessorType annotations
        Assert.assertEquals(XmlAccessType.PUBLIC_MEMBER, classInfo.getXmlAccessType());
    }

    @XmlAccessorType(XmlAccessType.NONE)
    private static class WithXmlAccessorTypeAnnotationNONE {
        
    }
    public final void testProcessAnnotationsWithXmlAccessorTypeNONEAnnotation() {
        Class < WithXmlAccessorTypeAnnotationNONE > clazz = WithXmlAccessorTypeAnnotationNONE.class;
        Annotation[] annotations = clazz.getAnnotations();
        ClassAnnotationProcessingService caps = new ClassAnnotationProcessingService();
        JaxbClassNature classInfo = new JaxbClassNature(new ClassInfo(WithXmlAccessorTypeAnnotationNONE.class.getName()));
        caps.processAnnotations(classInfo, annotations);

        // XmlRootElement annotations
        Assert.assertNull(classInfo.getRootElementName());
        Assert.assertNull(classInfo.getRootElementNamespace());
        // XmlType annotation
        Assert.assertNull(classInfo.getTypeFactoryClass());
        Assert.assertNull(classInfo.getTypeFactoryMethod());
        Assert.assertNull(classInfo.getTypeName());
        Assert.assertNull(classInfo.getTypeNamespace());
        Assert.assertNull(classInfo.getTypeProperties());
        // XmlTransient annotation
        Assert.assertFalse(classInfo.getXmlTransient());
        // XmlSeeAlso annotation
        Assert.assertNull(classInfo.getSeeAlsoClasses());
        // XmlAccessorOrder annotations
        Assert.assertNull(classInfo.getXmlAccessOrder());
        // XmlAccessorType annotations
        Assert.assertEquals(XmlAccessType.NONE, classInfo.getXmlAccessType());
    }

    public final void testProcessAnnotationsWithXmlEnum() {
        //Assert.fail("Not yet implemented!");
    }
}
