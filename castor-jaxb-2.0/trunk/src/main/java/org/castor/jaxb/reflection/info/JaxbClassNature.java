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

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * @author joachim
 *
 */
public class JaxbClassNature extends OoClassNature { //implements Nature {

    private ClassInfo classInfo;
    
    /**
     * @param classInfo the property holder to get the properties from
     */
    public JaxbClassNature(final ClassInfo classInfo) {
        super(classInfo);
        this.classInfo = classInfo;
    }

    /**
     * @return simply the class name
     * @see org.castor.core.nature.Nature#getId()
     */
    public String getId() {
        return getClass().getName();
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return (String) getProperty(Properties.TYPE_NAME);
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(final String typeName) {
        setProperty(Properties.TYPE_NAME, typeName);
    }
    /**
     * @return the typeProperties
     */
    public List < String > getTypeProperties() {
        return (List < String > ) getProperty(Properties.TYPE_PROPERTIES);
    }
    /**
     * @param strings the typeProperties to set
     */
    public void setTypeProperties(final String[] strings) {
        ArrayList<String> stringList = new ArrayList<String>();
        for (String string : strings) {
            stringList.add(string);
        }
        setProperty(Properties.TYPE_PROPERTIES, stringList);
    }
    /**
     * @return the typeNamespace
     */
    public String getTypeNamespace() {
        return (String) getProperty(Properties.TYPE_NAMESPACE);
    }
    /**
     * @param typeNamespace the typeNamespace to set
     */
    public void setTypeNamespace(final String typeNamespace) {
        setProperty(Properties.TYPE_NAMESPACE, typeNamespace);
    }
    /**
     * @return the typeFactoryClass
     */
    public Class < ? > getTypeFactoryClass() {
        return (Class < ? > ) getProperty(Properties.TYPE_FACTORY_CLASS);
    }
    /**
     * @param typeFactoryClass the typeFactoryClass to set
     */
    public void setTypeFactoryClass(final Class < ? > typeFactoryClass) {
        setProperty(Properties.TYPE_FACTORY_CLASS, typeFactoryClass);
    }
    /**
     * @return the typeFactoryMethod
     */
    public String getTypeFactoryMethod() {
        return (String) getProperty(Properties.TYPE_FACTORY_METHOD);
    }
    /**
     * @param typeFactoryMethod the typeFactoryMethod to set
     */
    public void setTypeFactoryMethod(final String typeFactoryMethod) {
        setProperty(Properties.TYPE_FACTORY_METHOD, typeFactoryMethod);
    }
    /**
     * @return the rootElementName
     */
    public String getRootElementName() {
        return (String) getProperty(Properties.ROOT_ELEMENT_NAME);
    }
    /**
     * @param rootElementName the rootElementName to set
     */
    public void setRootElementName(final String rootElementName) {
        setProperty(Properties.ROOT_ELEMENT_NAME, rootElementName);
    }
    /**
     * @return the rootElementNamespace
     */
    public String getRootElementNamespace() {
        return (String) getProperty(Properties.ROOT_ELEMENT_NAMESPACE);
    }
    /**
     * @param rootElementNamespace the rootElementNamespace to set
     */
    public void setRootElementNamespace(final String rootElementNamespace) {
        setProperty(Properties.ROOT_ELEMENT_NAMESPACE, rootElementNamespace);
    }
    /**
     * @return the xmlAccessType
     */
    public XmlAccessType getXmlAccessType() {
        return (XmlAccessType) getProperty(Properties.XML_ACCESS_TYPE);
    }
    /**
     * @param xmlAccessorType the xmlAccessType to set
     */
    public void setXmlAccessType(final XmlAccessType xmlAccessorType) {
        setProperty(Properties.XML_ACCESS_TYPE, xmlAccessorType);
    }
    /**
     * @return the xmlAccessOrder
     */
    public XmlAccessOrder getXmlAccessOrder() {
        return (XmlAccessOrder) getProperty(Properties.XML_ACCESS_ORDER);
    }
    /**
     * @param xmlAccessOrder the xmlAccessOrder to set
     */
    public void setXmlAccessOrder(final XmlAccessOrder xmlAccessOrder) {
        setProperty(Properties.XML_ACCESS_ORDER, xmlAccessOrder);
    }
    /**
     * @param b the transient flag
     */
    public void setTransient(final boolean b) {
        setProperty(Properties.TRANSIENT, new Boolean(b));
    }
    /**
     * @return true if field is flagges as transient
     */
    public boolean isTransient() {
        Boolean b = (Boolean) getProperty(Properties.TRANSIENT);
        if (b == null || Boolean.FALSE.equals(b)) {
            return false;
        } 
        return true;
    }
    /**
     * @param values the array of Class entries found in XmlSeeAlso annotation
     * @return 
     */
    public void setSeeAlsoClasses(final Class < ? > [] values) {
        setProperty(Properties.SEE_ALSO_CLASSES, values);
    }
    /**
     * The List of Class es of the XmlSeeAlso annotations.
     * @return the List of Class of the XmlSeeAlso annotation
     */
    public List < Class < ? > > getSeeAlsoClasses() {
        return (List < Class < ? > > ) getProperty(Properties.SEE_ALSO_CLASSES);
    }
    public void setEnumClass(Class<?> value) {
        setProperty(Properties.ENUM_CLASS, value);
    }
    public List<JaxbFieldNature> getFields() {
        ArrayList<JaxbFieldNature> fields = new ArrayList<JaxbFieldNature>();
        for (FieldInfo fieldInfo : classInfo.getFieldInfos()) {
            fields.add(new JaxbFieldNature(fieldInfo));
        }
        return fields;
    }

    public JaxbPackageNature getPackage() {
        return new JaxbPackageNature(classInfo.getPackageInfo());
    }

    public void setXmlType(boolean b) {
        setProperty(Properties.HAS_XML_TYPE, b);
    }

    public boolean getXmlType() {
        return getBooleanPropertyDefaultFalse(Properties.HAS_XML_TYPE);
    }

    public void setXmlRootElement(boolean b) {
        setProperty(Properties.HAS_XML_ROOT_ELEMENT, b);
    }

    public boolean getXmlRootElement() {
        return getBooleanPropertyDefaultFalse(Properties.HAS_XML_ROOT_ELEMENT);
    }

    public void setXmlTransient(boolean b) {
        setProperty(Properties.HAS_XML_TRANSIENT, b);
    }

    public boolean getXmlTransient() {
        return getBooleanPropertyDefaultFalse(Properties.HAS_XML_TRANSIENT);
    }

    public void setXmlSeeAlso(boolean b) {
        setProperty(Properties.HAS_XML_SEE_ALSO, b);
    }

    public boolean getXmlSeeAlso() {
        return getBooleanPropertyDefaultFalse(Properties.HAS_XML_SEE_ALSO);
    }

    public void setXmlAccessorType(boolean b) {
        setProperty(Properties.HAS_XML_ACCESSOR_TYPE, b);
    }

    public boolean getXmlAccessorType() {
        return getBooleanPropertyDefaultFalse(Properties.HAS_XML_ACCESSOR_TYPE);
    }

    public void setXmlAccessorOrder(boolean b) {
        setProperty(Properties.HAS_XML_ACCESSOR_ORDER, b);
    }

    public boolean getXmlAccessorOrder() {
        return getBooleanPropertyDefaultFalse(Properties.HAS_XML_ACCESSOR_ORDER);
    }

    public void setXmlEnum(boolean b) {
        setProperty(Properties.HAS_XML_ENUM, b);
    }

    public boolean getXmlEnum() {
        return getBooleanPropertyDefaultFalse(Properties.HAS_XML_ENUM);
    }
    /**
     * All property names.
     */
    static interface Properties {
        /** . */
        String ENUM_CLASS = "ENUM_CLASS";
        /** . */
        String SEE_ALSO_CLASSES = "SEE_ALSO_CLASSES";
        /** . */
        String TRANSIENT = "TRANSIENT";
        /** . */
        String XML_ACCESS_ORDER = "XML_ACCESS_ORDER";
        /** . */
        String XML_ACCESS_TYPE = "XML_ACCESS_TYPE";
        /** . */
        String ROOT_ELEMENT_NAMESPACE = "ROOT_ELEMENT_NAMESPACE";
        /** . */
        String ROOT_ELEMENT_NAME = "ROOT_ELEMENT_NAME";
        /** . */
        String TYPE_FACTORY_METHOD = "TYPE_FACTORY_METHOD";
        /** . */
        String TYPE_FACTORY_CLASS = "TYPE_FACTORY_CLASS";
        /** . */
        String TYPE_NAMESPACE = "TYPE_NAMESPACE";
        /** . */
        String TYPE_PROPERTIES = "TYPE_PROPERTIES";
        /** . */
        String TYPE_NAME = "TYPE_NAME";
        /** . */
        String HAS_XML_TYPE = "HAS_XML_TYPE";
        /** . */
        String HAS_XML_ROOT_ELEMENT = "HAS_XML_ROOT_ELEMENT";
        /** . */
        String HAS_XML_TRANSIENT = "HAS_XML_TRANSIENT";
        /** . */
        String HAS_XML_SEE_ALSO = "HAS_XML_SEE_ALSO";
        /** . */
        String HAS_XML_ACCESSOR_TYPE = "HAS_XML_ACCESSOR_TYPE";
        /** . */
        String HAS_XML_ACCESSOR_ORDER = "HAS_XML_ACCESSOR_ORDER";
        /** . */
        String HAS_XML_ENUM = "HAS_XML_ENUM";
    }
}
