/*
 * Copyright 2008 Joachim Grueneis
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

package org.castor.jaxb.reflection.info;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.DomHandler;


/**
 * The JAXB field nature holds setters and getters for all attributes read
 * from JAXB annotations.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_org
 */
public class JaxbFieldNature extends OoFieldNature implements JaxbFieldNatureProperties {
    /**
     * @param fieldInfo the holder of the nature properties
     */
    public JaxbFieldNature(final FieldInfo fieldInfo) {
        super(fieldInfo);
    }

    /**
     * @return the unique identifier of this nature
     * @see org.castor.core.nature.Nature#getId()
     */
    public String getId() {
        return getClass().getName();
    }

    /**
     * @param name
     *            XmlElement.name
     */
    public void setElementName(final String name) {
        setProperty(ELEMENT_NAME, name);
    }

    /**
     * @param nillable
     *            XmlElement.nillable
     */
    public void setElementNillable(final boolean nillable) {
        setProperty(ELEMENT_NILLABLE, new Boolean(nillable));
    }

    /**
     * @param required
     *            XmlElement.required
     */
    public void setElementRequired(final boolean required) {
        setProperty(ELEMENT_REQUIRED, new Boolean(required));
    }

    /**
     * @param namespace
     *            XmlElement.namespace
     */
    public void setElementNamespace(final String namespace) {
        setProperty(ELEMENT_NAMESPACE, namespace);
    }

    /**
     * @param type
     *            XmlElement.type
     */
    public void setElementType(final Class < ? > type) {
        setProperty(ELEMENT_TYPE, type);
    }

    /**
     * @param defaultValue
     *            XmlElement.defaultValue
     */
    public void setElementDefaultValue(final String defaultValue) {
        setProperty(ELEMENT_DEFAULT_VALUE, defaultValue);
    }

    /**
     * @param rootElementName
     *            XmlRootElement.name
     */
    public void setRootElementName(final String rootElementName) {
        setProperty(ROOT_ELEMENT_NAME, rootElementName);
    }

    /**
     * @param rootElementNamespace
     *            XmlRootElement.namespace
     */
    public void setRootElementNamespace(final String rootElementNamespace) {
        setProperty(ROOT_ELEMENT_NAMESPACE, rootElementNamespace);
    }

    /**
     * @param attributeName
     *            XmlAttribute.name
     */
    public void setAttributeName(final String attributeName) {
        setProperty(ATTRIBUTE_NAME, attributeName);
    }

    /**
     * @param attributeNamespace
     *            XmlAttribute.namespace
     */
    public void setAttributeNamespace(final String attributeNamespace) {
        setProperty(ATTRIBUTE_NAMESPACE, attributeNamespace);
    }

    /**
     * @param attributeRequired
     *            XmlElement.required
     */
    public void setAttributeRequired(final boolean attributeRequired) {
        setProperty(ATTRIBUTE_REQUIRED, attributeRequired);
    }

    /**
     * @return the XmlElement.name
     */
    public String getElementName() {
        return (String) getProperty(ELEMENT_NAME);
    }

    /**
     * @return the XmlElement.nillable
     */
    public boolean isElementNillable() {
        return getBooleanPropertyDefaultFalse(ELEMENT_NILLABLE);
    }

    /**
     * @return the XmlElement.required
     */
    public boolean isElementRequired() {
        return getBooleanPropertyDefaultFalse(ELEMENT_REQUIRED);
    }

    /**
     * @return the XmlElement.namespace
     */
    public String getElementNamespace() {
        return (String) getProperty(ELEMENT_NAMESPACE);
    }

    /**
     * @return the XmlElement.type
     */
    public Class < ? > getElementType() {
        return (Class < ? > ) getProperty(ELEMENT_TYPE);
    }

    /**
     * @return the XmlElement.DefaultValue
     */
    public String getElementDefaultValue() {
        return (String) getProperty(ELEMENT_DEFAULT_VALUE);
    }

    /**
     * @return the XmlRootElement.name
     */
    public String getRootElementName() {
        return (String) getProperty(ROOT_ELEMENT_NAME);
    }

    /**
     * @return the XmlRootElement.namespace
     */
    public String getRootElementNamespace() {
        return (String) getProperty(ROOT_ELEMENT_NAMESPACE);
    }

    /**
     * @return the XmlAttribute.name
     */
    public String getAttributeName() {
        return (String) getProperty(ATTRIBUTE_NAME);
    }

    /**
     * @return the XmlAttribute.namespace
     */
    public String getAttributeNamespace() {
        return (String) getProperty(ATTRIBUTE_NAMESPACE);
    }

    /**
     * @return the XmlAttribute.required
     */
    public boolean isAttributeRequired() {
        return getBooleanPropertyDefaultFalse(ATTRIBUTE_REQUIRED);
    }

//    /**
//     * @return true if FieldInfo is for an element
//     */
//    public boolean isElement() {
//        String elementName = getElementName();
//        if (elementName != null && elementName.length() > 0) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * @return true if FieldInfo is for an attribute
//     */
//    public boolean isAttribute() {
//        String attributeName = getAttributeName();
//        if (attributeName != null && attributeName.length() > 0) {
//            return true;
//        }
//        return false;
//    }

    /**
     * @param enumValue
     *            value of the XmlEnumValue annotation
     */
    public void setEnumValue(final String enumValue) {
        setProperty(ENUM_VALUE, enumValue);
    }

    /**
     * @return the enumValue
     */
    public String getEnumValue() {
        return (String) getProperty(ENUM_VALUE);
    }

    private boolean hasAnnotationXy(String property) {
        return getBooleanPropertyDefaultFalse(property);
    }

    private void setAnnotationXy(String property, boolean set) {
        setProperty(property, set?Boolean.TRUE:Boolean.FALSE);
    }

    public boolean hasXmlElement() {
        return hasAnnotationXy(HAS_XML_ELEMENT);
    }

    public boolean hasXmlAttribute() {
        return hasAnnotationXy(HAS_XML_ATTRIBUTE);
    }

    public boolean hasXmlElements() {
        return hasAnnotationXy(HAS_XML_ELEMENTS);
    }

    public boolean hasXmlElementRef() {
        return hasAnnotationXy(HAS_XML_ELEMENT_REF);
    }

    public boolean hasXmlElementRefs() {
        return hasAnnotationXy(HAS_XML_ELEMENT_REFS);
    }

    public boolean hasXmlElementWrapper() {
        return hasAnnotationXy(HAS_XML_ELEMENT_WRAPPER);
    }

    public boolean hasXmlAnyElement() {
        return hasAnnotationXy(HAS_XML_ANY_ELEMENT);
    }

    public boolean hasXmlAnyAttribute() {
        return hasAnnotationXy(HAS_XML_ANY_ATTRIBUTE);
    }

    public boolean hasXmlTransient() {
        return hasAnnotationXy(HAS_XML_TRANSIENT);
    }

    public boolean hasXmlValue() {
        return hasAnnotationXy(HAS_XML_VALUE);
    }

    public boolean hasXmlID() {
        return hasAnnotationXy(HAS_XML_ID);
    }

    public boolean hasXmlIDREF() {
        return hasAnnotationXy(HAS_XML_IDREF);
    }

    public boolean hasXmlList() {
        return hasAnnotationXy(HAS_XML_LIST);
    }

    public boolean hasXmlMixed() {
        return hasAnnotationXy(HAS_XML_MIXED);
    }

    public boolean hasXmlMimeType() {
        return hasAnnotationXy(HAS_XML_MIME_TYPE);
    }

    public boolean hasXmlAttachmentRef() {
        return hasAnnotationXy(HAS_XML_ATTACHMENT_REF);
    }

    public boolean hasXmlInlineBinaryData() {
        return hasAnnotationXy(HAS_XML_INLINE_BINARY_DATA);
    }

    public boolean hasXmlEnumValue() {
        return hasAnnotationXy(HAS_XML_ENUM_VALUE);
    }

    public void setXmlElement(final boolean value) {
        setAnnotationXy(HAS_XML_ELEMENT, value);
    }

    public void setXmlAttribute(final boolean value) {
        setAnnotationXy(HAS_XML_ATTRIBUTE, value);
    }

    public void setXmlElements(final boolean value) {
        setAnnotationXy(HAS_XML_ELEMENTS, value);
    }

    public void setXmlElementRef(final boolean value) {
        setAnnotationXy(HAS_XML_ELEMENT_REF, value);
    }

    public void setXmlElementRefs(final boolean value) {
        setAnnotationXy(HAS_XML_ELEMENT_REFS, value);
    }

    public void setXmlElementWrapper(final boolean value) {
        setAnnotationXy(HAS_XML_ELEMENT_WRAPPER, value);
    }

    public void setXmlAnyElement(final boolean value) {
        setAnnotationXy(HAS_XML_ANY_ELEMENT, value);
    }

    public void setXmlAnyAttribute(final boolean value) {
        setAnnotationXy(HAS_XML_ANY_ATTRIBUTE, value);
    }

    public void setXmlTransient(final boolean value) {
        setAnnotationXy(HAS_XML_TRANSIENT, value);
    }

    public void setXmlValue(final boolean value) {
        setAnnotationXy(HAS_XML_VALUE, value);
    }

    public void setXmlID(final boolean value) {
        setAnnotationXy(HAS_XML_ID, value);
    }

    public void setXmlIDREF(final boolean value) {
        setAnnotationXy(HAS_XML_IDREF, value);
    }

    public void setXmlList(final boolean value) {
        setAnnotationXy(HAS_XML_LIST, value);
    }

    public void setXmlMixed(final boolean value) {
        setAnnotationXy(HAS_XML_MIXED, value);
    }

    public void setXmlMimeType(final boolean value) {
        setAnnotationXy(HAS_XML_MIME_TYPE, value);
    }

    public void setXmlAttachmentRef(final boolean value) {
        setAnnotationXy(HAS_XML_ATTACHMENT_REF, value);
    }

    public void setXmlInlineBinaryData(final boolean value) {
        setAnnotationXy(HAS_XML_INLINE_BINARY_DATA, value);
    }

    public void setXmlEnumValue(final boolean value) {
        setAnnotationXy(HAS_XML_ENUM_VALUE, value);
    }

    public String getElementRefName() {
        return (String) getProperty(ELEMENT_REF_NAME);
    }
    
    public void setElementRefName(String elementRefName) {
        setProperty(ELEMENT_REF_NAME, elementRefName);
    }

    public String getElementRefNamespace() {
        return (String) getProperty(ELEMENT_REF_NAMESPACE);
    }
    
    public void setElementRefNamespace(String elementRefNamespace) {
        setProperty(ELEMENT_REF_NAMESPACE, elementRefNamespace);
    }

    public Class getElementRefType() {
        return (Class) getProperty(ELEMENT_REF_TYPE);
    }
    
    public void setElementRefType(Class elementRefType) {
        setProperty(ELEMENT_REF_TYPE, elementRefType);
    }

    public void addElementRefsElementRef(final String name, final String namespace, final Class < ? > type) {
        List<ElementRef> elementRefs = (List<ElementRef>) getProperty(ELEMENT_REFS);
        if (elementRefs == null) {
            elementRefs = new ArrayList<ElementRef>();
            setProperty(ELEMENT_REFS, elementRefs);
        }
        elementRefs.add(new ElementRef(name, namespace, type));
    }
    
    public List<ElementRef> getElementRefs() {
        return (List<ElementRef>) getProperty(ELEMENT_REFS);
    }
    
    public class ElementRef {
        private String name;
        private String namespace;
        private Class<?> type;
        public ElementRef(final String name, final String namespace, final Class < ? > type) {
            this.name = name;
            this.namespace = namespace;
            this.type = type;
        }
        public String getName() {
            return name;
        }
        public String getNamespace() {
            return namespace;
        }
        public Class<?> getType() {
            return type;
        }
    }

    public String getElementWrapperName() {
        return (String) getProperty(ELEMENT_WRAPPER_NAME);
    }

    public void setElementWrapperName(String elementWrapperName) {
        setProperty(ELEMENT_WRAPPER_NAME, elementWrapperName);
    }

    public String getElementWrapperNamespace() {
        return (String) getProperty(ELEMENT_WRAPPER_NAMESPACE);
    }

    public void setElementWrapperNamespace(String elementWrapperNamespace) {
        setProperty(ELEMENT_WRAPPER_NAMESPACE, elementWrapperNamespace);
    }

    public boolean getElementWrapperNillable() {
        return getBooleanPropertyDefaultFalse(ELEMENT_WRAPPER_NILLABLE);
    }

    public void setElementWrapperNillable(boolean elementWrapperNillable) {
        setProperty(ELEMENT_WRAPPER_NILLABLE, elementWrapperNillable);
    }

    public boolean getElementWrapperRequired() {
        return getBooleanPropertyDefaultFalse(ELEMENT_WRAPPER_REQUIRED);
    }

    public void setElementWrapperRequired(boolean elementWrapperRequired) {
        setProperty(ELEMENT_WRAPPER_REQUIRED, elementWrapperRequired);
    }

    public boolean getAnyElementLax() {
        return getBooleanPropertyDefaultFalse(ANY_ELEMENT_LAX);
    }

    public void setAnyElementLax(boolean lax) {
        setProperty(ANY_ELEMENT_LAX, lax);
    }

    public Class<? extends DomHandler> getAnyElementDomHandler() {
        return (Class<? extends DomHandler>) getProperty(ANY_ELEMENT_DOM_HANDLER);
    }

    public void setAnyElementDomHandler(Class<? extends DomHandler> domHandler) {
        setProperty(ANY_ELEMENT_DOM_HANDLER, domHandler);
    }

    public void setMimeType(String mimeType) {
        setProperty(MIME_TYPE, mimeType);
    }
    
    public String getMimtType() {
        return (String) getProperty(MIME_TYPE);
    }

    public Class getXmlJavaTypeAdapter() {
        return (Class) getProperty(XML_JAVA_TYPE_ADAPTER);
    }

    public void setXmlJavaTypeAdapter(Class clazz) {
        setProperty(XML_JAVA_TYPE_ADAPTER, clazz);
    }

    public void addElement(final String name, final String namespace, final boolean nillable, final boolean required,
            final Class type, final String defaultValue) {
        List<Element> elements = (List<Element>) getProperty(ELEMENTS);
        if (elements == null) {
            elements = new ArrayList<Element>();
            setProperty(ELEMENTS, elements);
        }
        elements.add(new Element(name, namespace, nillable, required, type, defaultValue));
    }
    
    public class Element {
        private String name;
        private String namespace;
        private boolean nillable;
        private boolean required;
        private Class < ? > type;
        private String defaultValue;
        public Element(final String name, final String namespace, final boolean nillable, final boolean required,
                final Class type, final String defaultValue) {
            super();
            this.name = name;
            this.namespace = namespace;
            this.nillable = nillable;
            this.required = required;
            this.type = type;
            this.defaultValue = defaultValue;
        }
        public String getName() {
            return name;
        }
        public String getNamespace() {
            return namespace;
        }
        public boolean isNillable() {
            return nillable;
        }
        public boolean isRequired() {
            return required;
        }
        public Class<?> getType() {
            return type;
        }
        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
