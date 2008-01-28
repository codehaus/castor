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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Contains all information regarding a certain inspected class.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public final class ClassInfo implements ReflectionInfo {
    /** Default string for element annotation. */
    public static final String DEFAULT_ELEMENT_NAME = "##default";
    /** Default string for element annotation. */
    public static final String DEFAULT_ELEMENT_NAMESPACE = "##default";
    /** Default string for root element annotation. */
    public static final String DEFAULT_ROOT_ELEMENT_NAME = "##default";
    /** Default string for root element annotation. */
    public static final String DEFAULT_ROOT_ELEMENT_NAMESPACE = "##default";
    /** Default string for attribute annotation. */
    public static final String DEFAULT_ATTRIBUTE_NAME = "##default";
    /** Default string for attribute annotation. */
    public static final String DEFAULT_ATTRIBUTE_NAMESPACE = "##default";

    /** XMLType: XML type name. */
    private String _typeName;
    /** XMLType: The names of the properties for the XML type in correct order. */
    private String[] _typeProperties;
    /** XMLType: The namespace of the type. */
    private String _typeNamespace;
    /** XMLType: The factory class to create instance of the type. */
    private Class < ? > _typeFactoryClass;
    /** XMLType: The factory method used to create instances of the type. */
    private String _typeFactoryMethod;
    /** XmlRootElement: The element name when used as root element. */
    private String _rootElementName;
    /** XmlRootElement: The namespace when used as root element. */
    private String _rootElementNamespace;
    /** XmlTransient: True if the class is to be considert transient. */
    private boolean _trnsnt;
    /** XmlSeeAlso: The array of class found in XmlSeeAlso annotation... no clue how to use it. */
    private Class < ? > [] _seeAlsoClasses;
    /** XmlAccessorType: How properties of the class should be accessed. */
    private XmlAccessType _xmlAccessType;
    /** XmlAccessorOrder: The order used for the properties. */
    private XmlAccessOrder _xmlAccessOrder;
    /** The Class this descriptor describes. */
    private Class < ? > _type;
    /** The super Class of the Class this descriptor describes. */
    private Class < ? > _supertype;
    /** The interfaces of the Class this descriptor describes. */
    private Class < ? > [] _interfaces;
    /** The fileds of this class. */
    private List < FieldInfo > _fieldInfos;
    /** The Class specified by XmlEnum tag. */
    private Class < ? > _enumClass;
    /** Package information for the package of the class. */
    private PackageInfo _packageInfo;
    /** Does the class have an empty public constructor? */
    private boolean _hasPublicEmptyConstructor;
    /** The name of the type, but NOT taken from XmlType! */
    private String _className;
    
    /**
     * Simple constructor.
     */
    public ClassInfo() {
        super();
        _fieldInfos = new ArrayList < FieldInfo > ();
    }
    
    /**
     * @return the typeName
     */
    public String getTypeName() {
        return _typeName;
    }
    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(final String typeName) {
        _typeName = typeName;
    }
    /**
     * @return the typeProperties
     */
    public String[] getTypeProperties() {
        return _typeProperties;
    }
    /**
     * @param typeProperties the typeProperties to set
     */
    public void setTypeProperties(final String[] typeProperties) {
        _typeProperties = typeProperties;
    }
    /**
     * @return the typeNamespace
     */
    public String getTypeNamespace() {
        return _typeNamespace;
    }
    /**
     * @param typeNamespace the typeNamespace to set
     */
    public void setTypeNamespace(final String typeNamespace) {
        _typeNamespace = typeNamespace;
    }
    /**
     * @return the typeFactoryClass
     */
    public Class < ? > getTypeFactoryClass() {
        return _typeFactoryClass;
    }
    /**
     * @param typeFactoryClass the typeFactoryClass to set
     */
    public void setTypeFactoryClass(final Class < ? > typeFactoryClass) {
        _typeFactoryClass = typeFactoryClass;
    }
    /**
     * @return the typeFactoryMethod
     */
    public String getTypeFactoryMethod() {
        return _typeFactoryMethod;
    }
    /**
     * @param typeFactoryMethod the typeFactoryMethod to set
     */
    public void setTypeFactoryMethod(final String typeFactoryMethod) {
        _typeFactoryMethod = typeFactoryMethod;
    }
    /**
     * @return the rootElementName
     */
    public String getRootElementName() {
        return _rootElementName;
    }
    /**
     * @param rootElementName the rootElementName to set
     */
    public void setRootElementName(final String rootElementName) {
        _rootElementName = rootElementName;
    }
    /**
     * @return the rootElementNamespace
     */
    public String getRootElementNamespace() {
        return _rootElementNamespace;
    }
    /**
     * @param rootElementNamespace the rootElementNamespace to set
     */
    public void setRootElementNamespace(final String rootElementNamespace) {
        _rootElementNamespace = rootElementNamespace;
    }
    /**
     * @return the xmlAccessType
     */
    public XmlAccessType getXmlAccessType() {
        return _xmlAccessType;
    }
    /**
     * @param xmlAccessorType the xmlAccessType to set
     */
    public void setXmlAccessType(final XmlAccessType xmlAccessorType) {
        _xmlAccessType = xmlAccessorType;
    }
    /**
     * @return the xmlAccessOrder
     */
    public XmlAccessOrder getXmlAccessOrder() {
        return _xmlAccessOrder;
    }
    /**
     * @param xmlAccessOrder the xmlAccessOrder to set
     */
    public void setXmlAccessOrder(final XmlAccessOrder xmlAccessOrder) {
        _xmlAccessOrder = xmlAccessOrder;
    }
    /**
     * @param b the transient flag
     */
    public void setTransient(final boolean b) {
        _trnsnt = b;
    }
    /**
     * @param value the array of Class entries found in XmlSeeAlso annotation
     */
    public void setSeeAlsoClasses(final Class < ? > [] value) {
        _seeAlsoClasses = value;
    }
    /**
     * @param type the Class we're talking about
     */
    public void setType(final Class < ? > type) {
        _type = type;
    }
    /**
     * @return the Class described
     */
    public Class < ? > getType() {
        return _type;
    }
    /**
     * @param supertype The super class.
     */
    public void setSupertype(final Class < ? > supertype) {
        _supertype = supertype;
    }
    /**
     * @return get the super class of the described class
     */
    public Class < ? > getSupertype() {
        return _supertype;
    }
    /**
     * @param interfaces the Interfaces of the class
     */
    public void setInterfaces(final Class < ? > [] interfaces) {
        _interfaces = interfaces;
    }
    /**
     * @return get the interfaces of the described class
     */
    public Class < ? > [] getInterfaces() {
        return _interfaces;
    }
    /**
     * @param fieldName the name of the field to find
     * @return null if not found or the field identified
     */
    public FieldInfo getFieldInfo(final String fieldName) {
        FieldInfo foundFieldInfo = null;
        for (FieldInfo fi : _fieldInfos) {
            if (fi.getFieldName().equals(fieldName)) {
                foundFieldInfo = fi;
            }
        }
        return foundFieldInfo;
    }
    /**
     * @param fieldInfo a FieldInfo to add
     */
    public void addFieldInfo(final FieldInfo fieldInfo) {
        _fieldInfos.add(fieldInfo);
    }
    /**
     * @return true if field is flagges as transient
     */
    public boolean isTransient() {
        return _trnsnt;
    }
    /**
     * @return the Array of Class found in the XmlSeeAlso annotation
     */
    public Class < ? > [] getSeeAlsoClasses() {
        return _seeAlsoClasses;
    }

    /**
     * @return the Collection of FieldInfo instances collected
     */
    public List < FieldInfo > getFieldInfos() {
        return _fieldInfos;
    }
    /**
     * @return the enumClass
     */
    public Class < ? > getEnumClass() {
        return _enumClass;
    }
    /**
     * @param enumClass the enumClass to set
     */
    public void setEnumClass(final Class < ? > enumClass) {
        _enumClass = enumClass;
    }
    /**
     * @return the packageInfo
     */
    public PackageInfo getPackageInfo() {
        return _packageInfo;
    }
    /**
     * @param packageInfo the packageInfo for the class
     */
    public void setPackageInfo(final PackageInfo packageInfo) {
        _packageInfo = packageInfo;
    }

    /**
     * @param hasPublicEmptyConstructor hasPublicEmptyConstructor
     */
    public void setHasPublicEmptyConstructor(final boolean hasPublicEmptyConstructor) {
        _hasPublicEmptyConstructor = hasPublicEmptyConstructor;
    }

    /**
     * @return hasPublicEmptyConstructor
     */
    public boolean isHasPublicEmptyConstructor() {
        return _hasPublicEmptyConstructor;
    }
    
    /**
     * Implementing the toString method to get a more meaningful log output.
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName())
        .append("[")
        .append("type:").append(_type)
        .append("]");
        return sb.toString();
    }

    /**
     * To set the Class.getName but already preprocessed (by JavaNaming).
     * @param className the class name of the type
     */
    public void setClassName(final String className) {
        _className = className;
    }
    
    /**
     * The class name which had been set or Class.getName if nothing
     * have been set.
     * @return the class name set or Class.getName()
     */
    public String getClassName() {
        if (_className != null) {
            return _className;
        }
        return _type.getName();
    }
}
