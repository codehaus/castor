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
public class JaxbFieldNature extends OoFieldNature {
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
        setProperty(Properties.ELEMENT_NAME, name);
    }

    /**
     * @param nillable
     *            XmlElement.nillable
     */
    public void setElementNillable(final boolean nillable) {
        setProperty(Properties.ELEMENT_NILLABLE, new Boolean(nillable));
    }

    /**
     * @param required
     *            XmlElement.required
     */
    public void setElementRequired(final boolean required) {
        setProperty(Properties.ELEMENT_REQUIRED, new Boolean(required));
    }

    /**
     * @param namespace
     *            XmlElement.namespace
     */
    public void setElementNamespace(final String namespace) {
        setProperty(Properties.ELEMENT_NAMESPACE, namespace);
    }

    /**
     * @param type
     *            XmlElement.type
     */
    public void setElementType(final Class < ? > type) {
        setProperty(Properties.ELEMENT_TYPE, type);
    }

    /**
     * @param defaultValue
     *            XmlElement.defaultValue
     */
    public void setElementDefaultValue(final String defaultValue) {
        setProperty(Properties.ELEMENT_DEFAULT_VALUE, defaultValue);
    }

    /**
     * @param rootElementName
     *            XmlRootElement.name
     */
    public void setRootElementName(final String rootElementName) {
        setProperty(Properties.ROOT_ELEMENT_NAME, rootElementName);
    }

    /**
     * @param rootElementNamespace
     *            XmlRootElement.namespace
     */
    public void setRootElementNamespace(final String rootElementNamespace) {
        setProperty(Properties.ROOT_ELEMENT_NAMESPACE, rootElementNamespace);
    }

    /**
     * @param attributeName
     *            XmlAttribute.name
     */
    public void setAttributeName(final String attributeName) {
        setProperty(Properties.ATTRIBUTE_NAME, attributeName);
    }

    /**
     * @param attributeNamespace
     *            XmlAttribute.namespace
     */
    public void setAttributeNamespace(final String attributeNamespace) {
        setProperty(Properties.ATTRIBUTE_NAMESPACE, attributeNamespace);
    }

    /**
     * @param attributeRequired
     *            XmlElement.required
     */
    public void setAttributeRequired(final boolean attributeRequired) {
        setProperty(Properties.ATTRIBUTE_REQUIRED, attributeRequired);
    }

    /**
     * @return the XmlElement.name
     */
    public String getElementName() {
        return (String) getProperty(Properties.ELEMENT_NAME);
    }

    /**
     * @return the XmlElement.nillable
     */
    public boolean isElementNillable() {
        return getBooleanPropertyDefaultFalse(Properties.ELEMENT_NILLABLE);
    }

    /**
     * @return the XmlElement.required
     */
    public boolean isElementRequired() {
        return getBooleanPropertyDefaultFalse(Properties.ELEMENT_REQUIRED);
    }

    /**
     * @return the XmlElement.namespace
     */
    public String getElementNamespace() {
        return (String) getProperty(Properties.ELEMENT_NAMESPACE);
    }

    /**
     * @return the XmlElement.type
     */
    public Class < ? > getElementType() {
        return (Class < ? > ) getProperty(Properties.ELEMENT_TYPE);
    }

    /**
     * @return the XmlElement.DefaultValue
     */
    public String getElementDefaultValue() {
        return (String) getProperty(Properties.ELEMENT_DEFAULT_VALUE);
    }

    /**
     * @return the XmlRootElement.name
     */
    public String getRootElementName() {
        return (String) getProperty(Properties.ROOT_ELEMENT_NAME);
    }

    /**
     * @return the XmlRootElement.namespace
     */
    public String getRootElementNamespace() {
        return (String) getProperty(Properties.ROOT_ELEMENT_NAMESPACE);
    }

    /**
     * @return the XmlAttribute.name
     */
    public String getAttributeName() {
        return (String) getProperty(Properties.ATTRIBUTE_NAME);
    }

    /**
     * @return the XmlAttribute.namespace
     */
    public String getAttributeNamespace() {
        return (String) getProperty(Properties.ATTRIBUTE_NAMESPACE);
    }

    /**
     * @return the XmlAttribute.required
     */
    public boolean isAttributeRequired() {
        return getBooleanPropertyDefaultFalse(Properties.ATTRIBUTE_REQUIRED);
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
        setProperty(Properties.ENUM_VALUE, enumValue);
    }

    /**
     * @return the enumValue
     */
    public String getEnumValue() {
        return (String) getProperty(Properties.ENUM_VALUE);
    }

    private boolean hasAnnotationXy(String property) {
        return getBooleanPropertyDefaultFalse(property);
    }

    private void setAnnotationXy(String property, boolean set) {
        setProperty(property, set?Boolean.TRUE:Boolean.FALSE);
    }

    public boolean hasXmlElement() {
        return hasAnnotationXy(Properties.HAS_XML_ELEMENT);
    }

    public boolean hasXmlAttribute() {
        return hasAnnotationXy(Properties.HAS_XML_ATTRIBUTE);
    }

    public boolean hasXmlElements() {
        return hasAnnotationXy(Properties.HAS_XML_ELEMENTS);
    }

    public boolean hasXmlElementRef() {
        return hasAnnotationXy(Properties.HAS_XML_ELEMENT_REF);
    }

    public boolean hasXmlElementRefs() {
        return hasAnnotationXy(Properties.HAS_XML_ELEMENT_REFS);
    }

    public boolean hasXmlElementWrapper() {
        return hasAnnotationXy(Properties.HAS_XML_ELEMENT_WRAPPER);
    }

    public boolean hasXmlAnyElement() {
        return hasAnnotationXy(Properties.HAS_XML_ANY_ELEMENT);
    }

    public boolean hasXmlAnyAttribute() {
        return hasAnnotationXy(Properties.HAS_XML_ANY_ATTRIBUTE);
    }

    public boolean hasXmlTransient() {
        return hasAnnotationXy(Properties.HAS_XML_TRANSIENT);
    }

    public boolean hasXmlValue() {
        return hasAnnotationXy(Properties.HAS_XML_VALUE);
    }

    public boolean hasXmlID() {
        return hasAnnotationXy(Properties.HAS_XML_ID);
    }

    public boolean hasXmlIDREF() {
        return hasAnnotationXy(Properties.HAS_XML_IDREF);
    }

    public boolean hasXmlList() {
        return hasAnnotationXy(Properties.HAS_XML_LIST);
    }

    public boolean hasXmlMixed() {
        return hasAnnotationXy(Properties.HAS_XML_MIXED);
    }

    public boolean hasXmlMimeType() {
        return hasAnnotationXy(Properties.HAS_XML_MIME_TYPE);
    }

    public boolean hasXmlAttachmentRef() {
        return hasAnnotationXy(Properties.HAS_XML_ATTACHMENT_REF);
    }

    public boolean hasXmlInlineBinaryData() {
        return hasAnnotationXy(Properties.HAS_XML_INLINE_BINARY_DATA);
    }

    public boolean hasXmlEnumValue() {
        return hasAnnotationXy(Properties.HAS_XML_ENUM_VALUE);
    }

    public void setXmlElement(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ELEMENT, value);
    }

    public void setXmlAttribute(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ATTRIBUTE, value);
    }

    public void setXmlElements(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ELEMENTS, value);
    }

    public void setXmlElementRef(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ELEMENT_REF, value);
    }

    public void setXmlElementRefs(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ELEMENT_REFS, value);
    }

    public void setXmlElementWrapper(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ELEMENT_WRAPPER, value);
    }

    public void setXmlAnyElement(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ANY_ELEMENT, value);
    }

    public void setXmlAnyAttribute(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ANY_ATTRIBUTE, value);
    }

    public void setXmlTransient(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_TRANSIENT, value);
    }

    public void setXmlValue(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_VALUE, value);
    }

    public void setXmlID(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ID, value);
    }

    public void setXmlIDREF(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_IDREF, value);
    }

    public void setXmlList(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_LIST, value);
    }

    public void setXmlMixed(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_MIXED, value);
    }

    public void setXmlMimeType(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_MIME_TYPE, value);
    }

    public void setXmlAttachmentRef(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ATTACHMENT_REF, value);
    }

    public void setXmlInlineBinaryData(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_INLINE_BINARY_DATA, value);
    }

    public void setXmlEnumValue(final boolean value) {
        setAnnotationXy(Properties.HAS_XML_ENUM_VALUE, value);
    }

    public String getElementRefName() {
        return (String) getProperty(Properties.ELEMENT_REF_NAME);
    }
    
    public void setElementRefName(String elementRefName) {
        setProperty(Properties.ELEMENT_REF_NAME, elementRefName);
    }

    public String getElementRefNamespace() {
        return (String) getProperty(Properties.ELEMENT_REF_NAMESPACE);
    }
    
    public void setElementRefNamespace(String elementRefNamespace) {
        setProperty(Properties.ELEMENT_REF_NAMESPACE, elementRefNamespace);
    }

    public Class getElementRefType() {
        return (Class) getProperty(Properties.ELEMENT_REF_TYPE);
    }
    
    public void setElementRefType(Class elementRefType) {
        setProperty(Properties.ELEMENT_REF_TYPE, elementRefType);
    }

    public void addElementRefsElementRef(final String name, final String namespace, final Class < ? > type) {
        List<ElementRef> elementRefs = (List<ElementRef>) getProperty(Properties.ELEMENT_REFS);
        if (elementRefs == null) {
            elementRefs = new ArrayList<ElementRef>();
            setProperty(Properties.ELEMENT_REFS, elementRefs);
        }
        elementRefs.add(new ElementRef(name, namespace, type));
    }
    
    public List<ElementRef> getElementRefs() {
        return (List<ElementRef>) getProperty(Properties.ELEMENT_REFS);
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
        return (String) getProperty(Properties.ELEMENT_WRAPPER_NAME);
    }

    public void setElementWrapperName(String elementWrapperName) {
        setProperty(Properties.ELEMENT_WRAPPER_NAME, elementWrapperName);
    }

    public String getElementWrapperNamespace() {
        return (String) getProperty(Properties.ELEMENT_WRAPPER_NAMESPACE);
    }

    public void setElementWrapperNamespace(String elementWrapperNamespace) {
        setProperty(Properties.ELEMENT_WRAPPER_NAMESPACE, elementWrapperNamespace);
    }

    public boolean getElementWrapperNillable() {
        return getBooleanPropertyDefaultFalse(Properties.ELEMENT_WRAPPER_NILLABLE);
    }

    public void setElementWrapperNillable(boolean elementWrapperNillable) {
        setProperty(Properties.ELEMENT_WRAPPER_NILLABLE, elementWrapperNillable);
    }

    public boolean getElementWrapperRequired() {
        return getBooleanPropertyDefaultFalse(Properties.ELEMENT_WRAPPER_REQUIRED);
    }

    public void setElementWrapperRequired(boolean elementWrapperRequired) {
        setProperty(Properties.ELEMENT_WRAPPER_REQUIRED, elementWrapperRequired);
    }

    public boolean getAnyElementLax() {
        return getBooleanPropertyDefaultFalse(Properties.ANY_ELEMENT_LAX);
    }

    public void setAnyElementLax(boolean lax) {
        setProperty(Properties.ANY_ELEMENT_LAX, lax);
    }

    public Class<? extends DomHandler> getAnyElementDomHandler() {
        return (Class<? extends DomHandler>) getProperty(Properties.ANY_ELEMENT_DOM_HANDLER);
    }

    public void setAnyElementDomHandler(Class<? extends DomHandler> domHandler) {
        setProperty(Properties.ANY_ELEMENT_DOM_HANDLER, domHandler);
    }

    public void setMimeType(String mimeType) {
        setProperty(Properties.MIME_TYPE, mimeType);
    }
    
    public String getMimtType() {
        return (String) getProperty(Properties.MIME_TYPE);
    }

    public void addElement(final String name, final String namespace, final boolean nillable, final boolean required,
            final Class type, final String defaultValue) {
        List<Element> elements = (List<Element>) getProperty(Properties.ELEMENTS);
        if (elements == null) {
            elements = new ArrayList<Element>();
            setProperty(Properties.ELEMENTS, elements);
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

    /**
     * 
     */
    interface Properties {
        String ELEMENTS = "ELEMENTS";
        String ELEMENT_REFS = "ELEMENT_REFS";
        /** ENUM_VALUE. */
        String ENUM_VALUE = "ENUM_VALUE";
        /** ATTRIBUTE_REQUIRED. */
        String ATTRIBUTE_REQUIRED = "ATTRIBUTE_REQUIRED";
        /** ATTRIBUTE_NAMESPACE. */
        String ATTRIBUTE_NAMESPACE = "ATTRIBUTE_NAMESPACE";
        /** ATTRIBUTE_NAME. */
        String ATTRIBUTE_NAME = "ATTRIBUTE_NAME";
        /** ROOT_ELEMENT_NAMESPACE. */
        String ROOT_ELEMENT_NAMESPACE = "ROOT_ELEMENT_NAMESPACE";
        /** ROOT_ELEMENT_NAME. */
        String ROOT_ELEMENT_NAME = "ROOT_ELEMENT_NAME";
        /** ELEMENT_DEFAULT_VALUE. */
        String ELEMENT_DEFAULT_VALUE = "ELEMENT_DEFAULT_VALUE";
        /** ELEMENT_TYPE. */
        String ELEMENT_TYPE = "ELEMENT_TYPE";
        /** ELEMENT_NAMESPACE. */
        String ELEMENT_NAMESPACE = "ELEMENT_NAMESPACE";
        /** ELEMENT_REQUIRED. */
        String ELEMENT_REQUIRED = "ELEMENT_REQUIRED";
        /** ELEMENT_NILLABLE. */
        String ELEMENT_NILLABLE = "ELEMENT_NILLABLE";
        /** ELEMENT_NAME. */
        String ELEMENT_NAME = "ELEMENT_NAME";
        /** HAS_XML_ELEMENT. */
        String HAS_XML_ELEMENT = "HAS_XML_ELEMENT";
        /** HAS_XML_ATTRIBUTE. */
        String HAS_XML_ATTRIBUTE = "HAS_XML_ATTRIBUTE";
        /** HAS_XML_ELEMENTS. */
        String HAS_XML_ELEMENTS = "HAS_XML_ELEMENTS";
        /** HAS_XML_ELEMENT_REF. */
        String HAS_XML_ELEMENT_REF = "HAS_XML_ELEMENT_REF";
        /** HAS_XML_ELEMENT_REFS. */
        String HAS_XML_ELEMENT_REFS = "HAS_XML_ELEMENT_REFS";
        /** HAS_XML_ELEMENT_WRAPPER. */
        String HAS_XML_ELEMENT_WRAPPER = "HAS_XML_ELEMENT_WRAPPER";
        /** HAS_XML_ANY_ELEMENT. */
        String HAS_XML_ANY_ELEMENT = "HAS_XML_ANY_ELEMENT";
        /** HAS_XML_ANY_ATTRIBUTE. */
        String HAS_XML_ANY_ATTRIBUTE = "HAS_XML_ANY_ATTRIBUTE";
        /** HAS_XML_TRANSIENT. */
        String HAS_XML_TRANSIENT = "HAS_XML_TRANSIENT";
        /** HAS_XML_VALUE. */
        String HAS_XML_VALUE = "HAS_XML_VALUE";
        /** HAS_XML_ID. */
        String HAS_XML_ID = "HAS_XML_ID";
        /** HAS_XML_IDREF. */
        String HAS_XML_IDREF = "HAS_XML_IDREF";
        /** HAS_XML_LIST. */
        String HAS_XML_LIST = "HAS_XML_LIST";
        /** HAS_XML_MIXED. */
        String HAS_XML_MIXED = "HAS_XML_MIXED";
        /** HAS_XML_MIME_TYPE. */
        String HAS_XML_MIME_TYPE = "HAS_XML_MIME_TYPE";
        /** HAS_XML_ATTACHMENT_REF. */
        String HAS_XML_ATTACHMENT_REF = "HAS_XML_ATTACHMENT_REF";
        /** HAS_XML_INLINE_BINARY_DATA. */
        String HAS_XML_INLINE_BINARY_DATA = "HAS_XML_INLINE_BINARY_DATA";
        /** HAS_XML_ENUM_VALUE. */
        String HAS_XML_ENUM_VALUE = "HAS_XML_ENUM_VALUE";
        String ELEMENT_REF_NAME = "ELEMENT_REF_NAME";
        String ELEMENT_REF_TYPE = "ELEMENT_REF_TYPE";
        String ELEMENT_REF_NAMESPACE = "ELEMENT_REF_NAMESPACE";
        String ELEMENT_WRAPPER_NAME = "ELEMENT_WRAPPER_NAME";
        String ELEMENT_WRAPPER_NAMESPACE = "ELEMENT_WRAPPER_NAMESPACE";
        String ELEMENT_WRAPPER_NILLABLE = "ELEMENT_WRAPPER_NILLABLE";
        String ELEMENT_WRAPPER_REQUIRED = "ELEMENT_WRAPPER_REQUIRED";
        String ANY_ELEMENT_LAX = "ANY_ELEMENT_LAX";
        String ANY_ELEMENT_DOM_HANDLER = "ANY_ELEMENT_DOM_HANDLER";
        String MIME_TYPE = "MIME_TYPE";
    }
}
