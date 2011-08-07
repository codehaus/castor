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

import org.junit.Assert;
import org.castor.core.annotationprocessing.AnnotationProcessingService;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * To test if the annotations are read and all annotation properties are stored.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_org
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/castor-jaxb-test-context.xml" })
public class FieldAnnotationProcessingServiceTest {

    @Autowired
    private AnnotationProcessingService fieldAnnotationProcessingService;

    private static class NotASingleField {  
        @XmlAttribute(name = "Hugo")
        private String _name;

        public String getName() {
            return _name;
        }
        
        public void setName(final String name) {
            _name = name;
        }
    }

    @Test
    public void testProcessAnnotations() {
        Class < NotASingleField > clazz = NotASingleField.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(method.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, method.getAnnotations());
        }
    }

    private static class WithXmlElement {
        @XmlElement(name="Franz")
        public List<? extends Object> anythingInAList;
    }

    @Test
    public void testWithXmlElement() {
        Class < WithXmlElement > clazz = WithXmlElement.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlElement());
                Assert.assertEquals("Franz", fi.getElementName());
//                Assert.assertNull(fi.getElementNamespace());
//                Assert.assertNull(fi.getElementDefaultValue());
//                Assert.assertNull(fi.getElementType());
            }
        }
    }

    private static class WithXmlAttribute {
        @XmlAttribute(name="Franz")
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlAttribute() {
        Class < WithXmlAttribute > clazz = WithXmlAttribute.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlAttribute());
                Assert.assertEquals("Franz", fi.getAttributeName());
//                Assert.assertNull(fi.getAttributeNamespace());
            }
        }
    }

    private static class WithXmlElements {
        @XmlElements({@XmlElement(name="Franz"),@XmlElement(name="Hugo")})
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlElements() {
        Class < WithXmlElements > clazz = WithXmlElements.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlElements());
//                List<XmlElement> xmlElements = fi.getXmlElements();
//                Assert.assertNotNull(xmlElements);
                
            }
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(method.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, method.getAnnotations());
        }
    }

    private static class WithXmlElementRef {
        @XmlElementRef(name="Franz")
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlElementRef() {
        Class < WithXmlElementRef> clazz = WithXmlElementRef.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlElementRef());
                Assert.assertEquals("Franz", fi.getElementRefName());
                Assert.assertNull(fi.getElementRefNamespace());
                Assert.assertNull(fi.getElementRefType());
            }
        }
    }

    private static class WithXmlElementRefs {
        @XmlElementRefs({@XmlElementRef(name="Franz"),@XmlElementRef(name="Karl")})
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlElementRefs() {
        Class < WithXmlElementRefs> clazz = WithXmlElementRefs.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlElementRefs());
            }
        }
    }

    private static class WithXmlElementWrapper {
        @XmlElementWrapper(name="theWrapper")
        @XmlElement(name="anything")
        public List<? extends Object> anythingInAList;
    }

    @Test
    public void testWithXmlElementWrapper() {
        Class < WithXmlElementWrapper> clazz = WithXmlElementWrapper.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlElementWrapper());
                Assert.assertEquals("theWrapper", fi.getElementWrapperName());
                Assert.assertNull(fi.getElementWrapperNamespace());
                Assert.assertFalse(fi.getElementWrapperNillable());
                Assert.assertFalse(fi.getElementWrapperRequired());
            }
        }
    }

    private static class WithXmlAnyElement {
        @XmlAnyElement(lax=true)
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlAnyElement() {
        Class < WithXmlAnyElement> clazz = WithXmlAnyElement.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlAnyElement());
                Assert.assertTrue(fi.getAnyElementLax());
                Assert.assertNull(fi.getAnyElementDomHandler());
            }
        }
    }

    private static class WithXmlAnyAttribute {
        @XmlAnyAttribute()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public void testWithXmlAnyAttribute() {
        Class < WithXmlAnyAttribute> clazz = WithXmlAnyAttribute.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlAnyAttribute());
            }
        }
    }

    private static class WithXmlTransient {
        @XmlTransient()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public void testWithXmlTransient() {
        Class < WithXmlTransient> clazz = WithXmlTransient.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlTransient());
            }
        }
    }

    private static class WithXmlValue {
        @XmlValue()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlValue() {
        Class < WithXmlValue> clazz = WithXmlValue.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlValue());
                
            }
        }
    }

    private static class WithXmlID {
        @XmlID()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlID() {
        Class < WithXmlID> clazz = WithXmlID.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlID());
                
            }
        }
    }

    private static class WithXmlIDREF {
        @XmlIDREF()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlIDREF() {
        Class < WithXmlIDREF> clazz = WithXmlIDREF.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlIDREF());
                
            }
        }
    }

    private static class WithXmlList {
        @XmlList()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlList() {
        Class < WithXmlList> clazz = WithXmlList.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlList());
                
            }
        }
    }

    private static class WithXmlMixed {
        @XmlMixed()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlMixed() {
        Class < WithXmlMixed> clazz = WithXmlMixed.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlMixed());
                
            }
        }
    }

    private static class WithXmlMimeType {
        @XmlMimeType(value="text/xml")
        public List<? extends Object> anythingInAList;
    }

    @Test
    public void testWithXmlMimeType() {
        Class < WithXmlMimeType> clazz = WithXmlMimeType.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlMimeType());
                Assert.assertEquals("text/xml", fi.getMimtType());
            }
        }
    }

    private static class WithXmlAttachmentRef {
        @XmlAttachmentRef()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public final void testWithXmlAttachmentRef() {
        Class < WithXmlAttachmentRef> clazz = WithXmlAttachmentRef.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlAttachmentRef());
                
            }
        }
    }

    private static class WithXmlInlineBinaryData {
        @XmlInlineBinaryData()
        public List<? extends Object> anythingInAList;
    }

    @Test
    public void testWithXmlInlineBinaryData() {
        Class < WithXmlInlineBinaryData> clazz = WithXmlInlineBinaryData.class;
        Assert.assertFalse(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("anythingInAList".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlInlineBinaryData());
                
            }
        }
    }

    @XmlEnum(Integer.class)
    private static enum WithEnumValue {
        @XmlEnumValue("1") ONE,
        @XmlEnumValue("2") TWO;
    }

    @Test
    public void testWithEnumValue() {
        Class < WithEnumValue> clazz = WithEnumValue.class;
        Assert.assertTrue(clazz.isEnum());
        Assert.assertNotNull(fieldAnnotationProcessingService);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            JaxbFieldNature fi = new JaxbFieldNature(new FieldInfo(field.getName()));
            fieldAnnotationProcessingService.processAnnotations(fi, field.getAnnotations());
            if ("ONE".equals(field.getName())) {
                Assert.assertTrue(fi.hasXmlEnumValue());
                
            }
        }
    }
}
