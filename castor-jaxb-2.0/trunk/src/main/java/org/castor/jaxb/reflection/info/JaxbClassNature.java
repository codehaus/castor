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
public class JaxbClassNature extends OoClassNature implements JaxbClassNatureProperties { //implements Nature {

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
        return (String) getProperty(JaxbClassNatureProperties.TYPE_NAME);
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(final String typeName) {
        setProperty(JaxbClassNatureProperties.TYPE_NAME, typeName);
    }
    /**
     * @return the typeProperties
     */
    public List < String > getTypeProperties() {
        return (List < String > ) getProperty(JaxbClassNatureProperties.TYPE_PROPERTIES);
    }
    /**
     * @param strings the typeProperties to set
     */
    public void setTypeProperties(final String[] strings) {
        ArrayList<String> stringList = new ArrayList<String>();
        for (String string : strings) {
            stringList.add(string);
        }
        setProperty(JaxbClassNatureProperties.TYPE_PROPERTIES, stringList);
    }
    /**
     * @return the typeNamespace
     */
    public String getTypeNamespace() {
        return (String) getProperty(JaxbClassNatureProperties.TYPE_NAMESPACE);
    }
    /**
     * @param typeNamespace the typeNamespace to set
     */
    public void setTypeNamespace(final String typeNamespace) {
        setProperty(JaxbClassNatureProperties.TYPE_NAMESPACE, typeNamespace);
    }
    /**
     * @return the typeFactoryClass
     */
    public Class < ? > getTypeFactoryClass() {
        return (Class < ? > ) getProperty(JaxbClassNatureProperties.TYPE_FACTORY_CLASS);
    }
    /**
     * @param typeFactoryClass the typeFactoryClass to set
     */
    public void setTypeFactoryClass(final Class < ? > typeFactoryClass) {
        setProperty(JaxbClassNatureProperties.TYPE_FACTORY_CLASS, typeFactoryClass);
    }
    /**
     * @return the typeFactoryMethod
     */
    public String getTypeFactoryMethod() {
        return (String) getProperty(JaxbClassNatureProperties.TYPE_FACTORY_METHOD);
    }
    /**
     * @param typeFactoryMethod the typeFactoryMethod to set
     */
    public void setTypeFactoryMethod(final String typeFactoryMethod) {
        setProperty(JaxbClassNatureProperties.TYPE_FACTORY_METHOD, typeFactoryMethod);
    }
    /**
     * @return the rootElementName
     */
    public String getRootElementName() {
        return (String) getProperty(JaxbClassNatureProperties.ROOT_ELEMENT_NAME);
    }
    /**
     * @param rootElementName the rootElementName to set
     */
    public void setRootElementName(final String rootElementName) {
        setProperty(JaxbClassNatureProperties.ROOT_ELEMENT_NAME, rootElementName);
    }
    /**
     * @return the rootElementNamespace
     */
    public String getRootElementNamespace() {
        return (String) getProperty(JaxbClassNatureProperties.ROOT_ELEMENT_NAMESPACE);
    }
    /**
     * @param rootElementNamespace the rootElementNamespace to set
     */
    public void setRootElementNamespace(final String rootElementNamespace) {
        setProperty(JaxbClassNatureProperties.ROOT_ELEMENT_NAMESPACE, rootElementNamespace);
    }
    /**
     * @return the xmlAccessType
     */
    public XmlAccessType getXmlAccessType() {
        return (XmlAccessType) getProperty(JaxbClassNatureProperties.XML_ACCESS_TYPE);
    }
    /**
     * @param xmlAccessorType the xmlAccessType to set
     */
    public void setXmlAccessType(final XmlAccessType xmlAccessorType) {
        setProperty(JaxbClassNatureProperties.XML_ACCESS_TYPE, xmlAccessorType);
    }
    /**
     * @return the xmlAccessOrder
     */
    public XmlAccessOrder getXmlAccessOrder() {
        return (XmlAccessOrder) getProperty(JaxbClassNatureProperties.XML_ACCESS_ORDER);
    }
    /**
     * @param xmlAccessOrder the xmlAccessOrder to set
     */
    public void setXmlAccessOrder(final XmlAccessOrder xmlAccessOrder) {
        setProperty(JaxbClassNatureProperties.XML_ACCESS_ORDER, xmlAccessOrder);
    }
//    /**
//     * @param b the transient flag
//     */
//    public void setTransient(final boolean b) {
//        setProperty(Properties.TRANSIENT, new Boolean(b));
//    }
//    /**
//     * @return true if field is flagges as transient
//     */
//    public boolean isTransient() {
//        Boolean b = (Boolean) getProperty(Properties.TRANSIENT);
//        if (b == null || Boolean.FALSE.equals(b)) {
//            return false;
//        } 
//        return true;
//    }
    /**
     * @param values the array of Class entries found in XmlSeeAlso annotation
     * @return 
     */
    public void setSeeAlsoClasses(final Class < ? > [] values) {
        if (values != null || values.length > 0) {
            ArrayList<Class<?>> seeAlsoClasses = new ArrayList<Class<?>>();
            for (int i = 0; i < values.length; i++) {
                seeAlsoClasses.add(values[i]);
            }
            setProperty(JaxbClassNatureProperties.SEE_ALSO_CLASSES, seeAlsoClasses);
        }
    }
    /**
     * The List of Class es of the XmlSeeAlso annotations.
     * @return the List of Class of the XmlSeeAlso annotation
     */
    public List < Class < ? > > getSeeAlsoClasses() {
        return (List < Class < ? > > ) getProperty(JaxbClassNatureProperties.SEE_ALSO_CLASSES);
    }
    public void setEnumClass(Class<?> value) {
        setProperty(JaxbClassNatureProperties.ENUM_CLASS, value);
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
        setProperty(JaxbClassNatureProperties.HAS_XML_TYPE, b);
    }

    public boolean getXmlType() {
        return getBooleanPropertyDefaultFalse(JaxbClassNatureProperties.HAS_XML_TYPE);
    }

    public void setXmlRootElement(boolean b) {
        setProperty(JaxbClassNatureProperties.HAS_XML_ROOT_ELEMENT, b);
    }

    public boolean getXmlRootElement() {
        return getBooleanPropertyDefaultFalse(JaxbClassNatureProperties.HAS_XML_ROOT_ELEMENT);
    }

    public void setXmlTransient(boolean b) {
        setProperty(JaxbClassNatureProperties.HAS_XML_TRANSIENT, b);
    }

    public boolean getXmlTransient() {
        return getBooleanPropertyDefaultFalse(JaxbClassNatureProperties.HAS_XML_TRANSIENT);
    }

    public void setXmlSeeAlso(boolean b) {
        setProperty(JaxbClassNatureProperties.HAS_XML_SEE_ALSO, b);
    }

    public boolean getXmlSeeAlso() {
        return getBooleanPropertyDefaultFalse(JaxbClassNatureProperties.HAS_XML_SEE_ALSO);
    }

    public void setXmlAccessorType(boolean b) {
        setProperty(JaxbClassNatureProperties.HAS_XML_ACCESSOR_TYPE, b);
    }

    public boolean getXmlAccessorType() {
        return getBooleanPropertyDefaultFalse(JaxbClassNatureProperties.HAS_XML_ACCESSOR_TYPE);
    }

    public void setXmlAccessorOrder(boolean b) {
        setProperty(JaxbClassNatureProperties.HAS_XML_ACCESSOR_ORDER, b);
    }

    public boolean getXmlAccessorOrder() {
        return getBooleanPropertyDefaultFalse(JaxbClassNatureProperties.HAS_XML_ACCESSOR_ORDER);
    }

    public void setXmlEnum(boolean b) {
        setProperty(JaxbClassNatureProperties.HAS_XML_ENUM, b);
    }

    public boolean getXmlEnum() {
        return getBooleanPropertyDefaultFalse(JaxbClassNatureProperties.HAS_XML_ENUM);
    }
}
