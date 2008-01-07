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
package org.castor.jaxb.reflection.info;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Contains the information collected for fields. Fields are either real fields or methods matching
 * certain naming (JavaBeans).
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public final class FieldInfo implements ReflectionInfo {
    /** The name of the field. Either from the field itself of taken from a method. */
    private String _fieldName;
    /** The field this information is for. */
    private Field _field;
    /** Add method. */
    private Method _methodAdd;
    /** Get method. */
    private Method _methodGet;
    /** Set method. */
    private Method _methodSet;
    /** Create method. */
    private Method _methodCreate;
    /** Is method. */
    private Method _methodIs;
    /** XmlElement.name(). */
    private String _elementName;
    /** XmlElement.nillable(). */
    private boolean _elementNillable;
    /** XmlElement.required(). */
    private boolean _elementRequired;
    /** XmlElement.namespace(). */
    private String _elementNamespace;
    /** XmlElement.type(). */
    private Class < ? > _elementType;
    /** XmlElement.defaultValue(). */
    private String _elementDefaultValue;
    /** XmlRootElement.name(). */
    private String _rootElementName;
    /** XmlRootElement.namespace(). */
    private String _rootElementNamespace;
    /** XmlAttribute.name(). */
    private String _attributeName;
    /** XmlAttribute.namespace(). */
    private String _attributeNamespace;
    /** XmlAttribute.required(). */
    private boolean _attributeRequired;
    /** The value of the XmlEnumValue annotation. */
    private String _enumValue;
    
    /**
     * Simple constructor.
     */
    public FieldInfo() {
        super();
    }
    
    /**
     * @param name XmlElement.name
     */
    public void setElementName(final String name) {
        _elementName = name;
    }

    /**
     * @param nillable XmlElement.nillable
     */
    public void setElementNillable(final boolean nillable) {
        _elementNillable = nillable;
    }

    /**
     * @param required XmlElement.required
     */
    public void setElementRequired(final boolean required) {
        _elementRequired = required;
    }

    /**
     * @param namespace XmlElement.namespace
     */
    public void setElementNamespace(final String namespace) {
        _elementNamespace = namespace;
    }

    /**
     * @param type XmlElement.type
     */
    public void setElementType(final Class < ? > type) {
        _elementType = type;
    }

    /**
     * @param defaultValue XmlElement.defaultValue
     */
    public void setElementDefaultValue(final String defaultValue) {
        _elementDefaultValue = defaultValue;
    }

    /**
     * @param rootElementName XmlRootElement.name
     */
    public void setRootElementName(final String rootElementName) {
        _rootElementName = rootElementName;
    }

    /**
     * @param rootElementNamespace XmlRootElement.namespace
     */
    public void setRootElementNamespace(final String rootElementNamespace) {
        _rootElementNamespace = rootElementNamespace;
    }

    /**
     * @param attributeName XmlAttribute.name
     */
    public void setAttributeName(final String attributeName) {
        _attributeName = attributeName;
    }

    /**
     * @param attributeNamespace XmlAttribute.namespace
     */
    public void setAttributeNamespace(final String attributeNamespace) {
        _attributeNamespace = attributeNamespace;
    }

    /**
     * @param attributeRequired XmlElement.required
     */
    public void setAttributeRequired(final boolean attributeRequired) {
        _attributeRequired = attributeRequired;
    }

    /**
     * @return the XmlElement.name
     */
    public String getElementName() {
        return _elementName;
    }

    /**
     * @return the XmlElement.nillable
     */
    public boolean isElementNillable() {
        return _elementNillable;
    }

    /**
     * @return the XmlElement.required
     */
    public boolean isElementRequired() {
        return _elementRequired;
    }

    /**
     * @return the XmlElement.namespace
     */
    public String getElementNamespace() {
        return _elementNamespace;
    }

    /**
     * @return the XmlElement.type
     */
    public Class < ? > getElementType() {
        return _elementType;
    }

    /**
     * @return the XmlElement.DefaultValue
     */
    public String getElementDefaultValue() {
        return _elementDefaultValue;
    }

    /**
     * @return the XmlRootElement.name
     */
    public String getRootElementName() {
        return _rootElementName;
    }

    /**
     * @return the XmlRootElement.namespace
     */
    public String getRootElementNamespace() {
        return _rootElementNamespace;
    }

    /**
     * @return the XmlAttribute.name
     */
    public String getAttributeName() {
        return _attributeName;
    }

    /**
     * @return the XmlAttribute.namespace
     */
    public String getAttributeNamespace() {
        return _attributeNamespace;
    }

    /**
     * @return the XmlAttribute.required
     */
    public boolean isAttributeRequired() {
        return _attributeRequired;
    }

    /**
     * @param field the Field of this property
     */
    public void setField(final Field field) {
        _field = field;
    }

    /**
     * @return true if FieldInfo is for an element
     */
    public boolean isElement() {
        if (_elementName != null && _elementName.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @return true if FieldInfo is for an attribute
     */
    public boolean isAttribute() {
        if (_attributeName != null && _attributeName.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @param enumValue value of the XmlEnumValue annotation
     */
    public void setEnumValue(final String enumValue) {
        _enumValue = enumValue;
    }
    
    /**
     * @return true if the field is an enumeration constant
     */
    public boolean isEnumConstant() {
        return _field.isEnumConstant();
    }

    /**
     * @return the methodAdd
     */
    public Method getMethodAdd() {
        return _methodAdd;
    }

    /**
     * @param methodAdd the methodAdd to set
     */
    public void setMethodAdd(final Method methodAdd) {
        _methodAdd = methodAdd;
    }

    /**
     * @return the methodGet
     */
    public Method getMethodGet() {
        return _methodGet;
    }

    /**
     * @param methodGet the methodGet to set
     */
    public void setMethodGet(final Method methodGet) {
        _methodGet = methodGet;
    }

    /**
     * @return the methodSet
     */
    public Method getMethodSet() {
        return _methodSet;
    }

    /**
     * @param methodSet the methodSet to set
     */
    public void setMethodSet(final Method methodSet) {
        _methodSet = methodSet;
    }

    /**
     * @return the methodCreate
     */
    public Method getMethodCreate() {
        return _methodCreate;
    }

    /**
     * @param methodCreate the methodCreate to set
     */
    public void setMethodCreate(final Method methodCreate) {
        _methodCreate = methodCreate;
    }

    /**
     * @return the methodIs
     */
    public Method getMethodIs() {
        return _methodIs;
    }

    /**
     * @param methodIs the methodIs to set
     */
    public void setMethodIs(final Method methodIs) {
        _methodIs = methodIs;
    }

    /**
     * @return the field
     */
    public Field getField() {
        return _field;
    }

    /**
     * @return the enumValue
     */
    public String getEnumValue() {
        return _enumValue;
    }

    /**
     * @return true if the field can be multivalued
     */
    public boolean isMultivalue() {
        return false;
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Auto-generated method stub");
//        // return false;
    }
    
    /**
     * The field type is either taken from the element annotation, the field or from a method.
     * @return returns the type of the field
     */
    public Class < ? > getFieldType() {
        if (_elementType != null) {
            return _elementType;
        } else if (_field != null) {
            return _field.getType();
        } else {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Auto-generated method stub");
            // return false;
        }
    }

    /**
     * @param name the FieldName to use - used to uniquely identifies a field!
     */
    public void setFieldName(final String name) {
        _fieldName = name;
    }
    
    /**
     * @return the unique fieldName
     */
    public String getFieldName() {
        return _fieldName;
    }
}
