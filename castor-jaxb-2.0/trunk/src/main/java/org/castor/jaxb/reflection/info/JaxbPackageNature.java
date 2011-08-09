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
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchemaType;

import org.castor.core.nature.PropertyHolder;

/**
 * The package info class holds all package related information gathered
 * using reflection and concentrating on JAXB annotations and those that
 * somehow relevant when working with JAXB. It is no reflection wrapper
 * just some class to hold information.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public final class JaxbPackageNature extends OoPackageNature {    

    /**
     * @param holder the holder of the nature properties
     */
    public JaxbPackageNature(final PropertyHolder holder) {
        super(holder);
    }

    /**
     * @return the unique identifier of this nature
     * @see org.castor.core.nature.Nature#getId()
     */
    public String getId() {
        return getClass().getName();
    }

    /**
     * @param namespace target namespace to use
     */
    public void setSchemaNamespace(final String namespace) {
        setProperty(Properties.SCHEMA_NAMESPACE, namespace);
    }
    
    /**
     * @return target namespace
     */
    public String getSchemaNamespace() {
        return (String) getProperty(Properties.SCHEMA_NAMESPACE);
    }

    /**
     * @param elementFormDefault element form to use
     */
    public void setSchemaElementForm(final XmlNsForm elementFormDefault) {
        setProperty(Properties.ELEMENT_FORM, elementFormDefault);
    }
    
    /**
     * @return element form
     */
    public XmlNsForm getSchemaElementForm() {
        return (XmlNsForm) getProperty(Properties.ELEMENT_FORM);
    }

    /**
     * @param attributeFormDefault attribute for to use
     */
    public void setSchemaAttributeForm(final XmlNsForm attributeFormDefault) {
        setProperty(Properties.ATTRIBUTE_FORM, attributeFormDefault);
    }

    /**
     * @return attribute form
     */
    public XmlNsForm getSchemaAttributeForm() {
        return (XmlNsForm) getProperty(Properties.ATTRIBUTE_FORM);
    }

    /**
     * @param pAccessType default access type to use for all classes in this package
     */
    public void setAccessType(final XmlAccessType pAccessType) {
        setProperty(Properties.ACCESS_TYPE, pAccessType);
    }

    /**
     * @return default XmlAccessType to use
     */
    public XmlAccessType getAccessType() {
        return (XmlAccessType) getProperty(Properties.ACCESS_TYPE);
    }

    /**
     * @param pAccessOrder default access order to use for all classes in this package
     */
    public void setAccessOrder(final XmlAccessOrder pAccessOrder) {
        setProperty(Properties.ACCESS_ORDER, pAccessOrder);
    }
    
    /**
     * @return default XmlAccessOrder to use
     */
    public XmlAccessOrder getAccessOrder() {
        return (XmlAccessOrder) getProperty(Properties.ACCESS_ORDER);
    }

    /**
     * @param xmlNsArray array of namespace definitions
     */
    public void addSchemaNsArray(final XmlNs[] xmlNsArray) {
        for (int i = 0; i < xmlNsArray.length; i++) {
            XmlNs xmlNs = xmlNsArray[i];
            getNamespaceInfos().add(new NamespaceInfo(xmlNs));
        }
    }
    
    /**
     * @return Collection of Namespaces
     */
    public Collection < NamespaceInfo > getNamespaceInfos() {
        Collection < NamespaceInfo > namespaceInfos = 
            (Collection < NamespaceInfo > ) getProperty(Properties.NAMESPACES);
        if (namespaceInfos == null) {
            namespaceInfos = new ArrayList < NamespaceInfo > ();
            setNamespaceInfos(namespaceInfos);
        }
        return namespaceInfos;
    }

    /**
     * To set namespaces property.
     * @param namespaceInfos value
     */
    private void setNamespaceInfos(final Collection < NamespaceInfo > namespaceInfos) {
        setProperty(Properties.NAMESPACES, namespaceInfos);
    }

    /**
     * @param xmlSchemaTypes Array of XmlSchemaType definitions
     */
    public void addSchemaTypeArray(final XmlSchemaType[] xmlSchemaTypes) {
        for (XmlSchemaType xst : xmlSchemaTypes) {
            addSchemaType(xst.name(), xst.namespace(), xst.type());
        }
    }
    
    /**
     * @return Collection of SchemaTypes
     */
    public Collection <SchemaTypeInfo> getSchemaTypes() {
        Collection < SchemaTypeInfo > schemaTypes =
            (Collection < SchemaTypeInfo > ) getProperty(Properties.SCHEMA_TYPES);
        if (schemaTypes == null) {
            schemaTypes = new ArrayList < SchemaTypeInfo > ();
            setSchemaTypes(schemaTypes);
        }
        return schemaTypes;
    }

    /**
     * To set the Schema-Types property.
     * @param schemaTypes the Schema-Types collection to set to
     */
    private void setSchemaTypes(final Collection <SchemaTypeInfo> schemaTypes) {
        setProperty(Properties.SCHEMA_TYPES, schemaTypes);
    }

    /**
     * @param name Name of the SchemaType
     * @param namespace Namespace of the SchemaType
     * @param schemaType Type of the SchemaType
     */
    public void addSchemaType(
            final String name,
            final String namespace,
            final Class < ? > schemaType) {
        getSchemaTypes().add(new SchemaTypeInfo(name, namespace, schemaType));
    }

    /**
     * Used to define which schema type (e.g. 'xs:date') should be used for
     * the following property. Also a namespace and a Java class can be
     * specified.
     */
    public final class SchemaTypeInfo {
        /** Name of the SchemaType. */
        private final String _name;
        /** Namespace of the SchemaType. */
        private final String _namespace;
        /** Type of the SchemaType. */
        private final Class < ? > _schemaType;
        /**
         * A SchemaType consists of these three attributes and is immutable after construction.
         * @param name Name of the SchemaType
         * @param namespace Namespace of the SchemaType
         * @param schemaType Type of the SchemaType
         */
        public SchemaTypeInfo(
                final String name,
                final String namespace,
                final Class < ? > schemaType) {
            _name = name;
            _namespace = namespace;
            _schemaType = schemaType;
        }
        /**
         * @return Name of the SchemaType.
         */
        public String getName() {
            return _name;
        }
        /**
         * @return Namespace of the SchemaType.
         */
        public String getNamespace() {
            return _namespace;
        }
        /**
         * @return Type of the SchemaType.
         */
        public Class < ? > getSchemaType() {
            return _schemaType;
        }
    }
    
    /**
     * A namespace definition with prefix and URI.
     * 
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public final class NamespaceInfo {
        /** Prefix of namespace. */
        private String _prefix;
        /** URI of namespace. */
        private String _namespaceURI;
        /**
         * @param xmlNs a namespace prefix definition
         */
        public NamespaceInfo(final XmlNs xmlNs) {
            _prefix = xmlNs.prefix();
            _namespaceURI = xmlNs.namespaceURI();
        }
        /**
         * @return the prefix
         */
        public String getPrefix() {
            return _prefix;
        }
        /**
         * @return the namespaceURI
         */
        public String getNamespaceURI() {
            return _namespaceURI;
        }
    }
    
    private static interface Properties {

        String NAMESPACES = "NAMESPACES";
        String SCHEMA_TYPES = "SCHEMA_TYPES";
        String ACCESS_ORDER = "ACCESS_ORDER";
        String ACCESS_TYPE = "ACCESS_TYPE";
        String ATTRIBUTE_FORM = "ATTRIBUTE_FORM";
        String ELEMENT_FORM = "ELEMENT_FORM";
        String SCHEMA_NAMESPACE = "SCHEMA_NAMESPACE";
        
    }
}
