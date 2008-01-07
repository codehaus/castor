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

/**
 * The package info class holds all package related information gathered
 * using reflection and concentrating on JAXB annotations and those that
 * somehow relevant when working with JAXB. It is no reflection wrapper
 * just some class to hold information.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public final class PackageInfo implements ReflectionInfo {
    /** Package to describe. */
    private Package _pkg;
    /** All known namespaces. */
    private ArrayList < NamespaceInfo > _namespacePrefixes = new ArrayList < NamespaceInfo > ();
    /** Target namespace to use. */
    private String _schemaNamespace;
    /** Element form to use. */
    private XmlNsForm _elementForm;
    /** Attribute form to use. */
    private XmlNsForm _attributeForm;
    /** Access type to use for all classes within this package. */
    private XmlAccessType _xmlAccessType;
    /** Access order to use for all classes within this package. */
    private XmlAccessOrder _xmlAccessOrder;
    /** Schema types. */
    private ArrayList < SchemaTypeInfo > _schemaTypes = new ArrayList < SchemaTypeInfo > ();

    /**
     * @param namespace target namespace to use
     */
    public void setSchemaNamespace(final String namespace) {
        _schemaNamespace = namespace;
    }
    
    /**
     * @return target namespace
     */
    public String getSchemaNamespace() {
        return _schemaNamespace;
    }

    /**
     * @param elementFormDefault element form to use
     */
    public void setSchemaElementForm(final XmlNsForm elementFormDefault) {
        _elementForm = elementFormDefault;
    }
    
    /**
     * @return element form
     */
    public XmlNsForm getSchemaElementForm() {
        return _elementForm;
    }

    /**
     * @param attributeFormDefault attribute for to use
     */
    public void setSchemaAttributeForm(final XmlNsForm attributeFormDefault) {
        _attributeForm = attributeFormDefault;
    }

    /**
     * @return attribute form
     */
    public XmlNsForm getSchemaAttributeForm() {
        return _attributeForm;
    }

    /**
     * @param pAccessType default access type to use for all classes in this package
     */
    public void setAccessType(final XmlAccessType pAccessType) {
        _xmlAccessType = pAccessType;
    }

    /**
     * @return default XmlAccessType to use
     */
    public XmlAccessType getAccessType() {
        return _xmlAccessType;
    }

    /**
     * @param pAccessOrder default access order to use for all classes in this package
     */
    public void setAccessOrder(final XmlAccessOrder pAccessOrder) {
        _xmlAccessOrder = pAccessOrder;
    }
    
    /**
     * @return default XmlAccessOrder to use
     */
    public XmlAccessOrder getAccessOrder() {
        return _xmlAccessOrder;
    }

    /**
     * @param xmlNsArray array of namespace definitions
     */
    public void addSchemaNsArray(final XmlNs[] xmlNsArray) {
        for (int i = 0; i < xmlNsArray.length; i++) {
            XmlNs xmlNs = xmlNsArray[i];
            _namespacePrefixes.add(new NamespaceInfo(xmlNs));
        }
    }
    
    /**
     * @return Collection of Namespaces
     */
    public Collection < NamespaceInfo > getNamespaces() {
        return _namespacePrefixes;
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
    public Collection < SchemaTypeInfo > getSchemaTypes() {
        return _schemaTypes;
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
        _schemaTypes.add(new SchemaTypeInfo(name, namespace, schemaType));
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

    /**
     * @param pkg the Package to describe
     */
    public void setPackage(final Package pkg) {
        _pkg = pkg;
    }
    
    /**
     * @return the Package to describe
     */
    public Package getPackage() {
        return _pkg;
    }
}
