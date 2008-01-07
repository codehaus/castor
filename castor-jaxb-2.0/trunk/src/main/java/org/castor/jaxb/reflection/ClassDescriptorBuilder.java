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

import javax.xml.bind.annotation.XmlAccessType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.jaxb.naming.NamingJava2Xml;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.PackageInfo;
import org.castor.jaxb.reflection.info.PackageInfo.NamespaceInfo;
import org.castor.xml.XMLNaming;
import org.exolab.castor.mapping.CollectionHandler;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.loader.CollectionHandlers;
import org.exolab.castor.mapping.loader.TypeInfo;
import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLFieldDescriptor;

/**
 * A service class to build a XMLClassDescriptor instance from ClassInfo.
 * It takes the values of ClassInfo, adds Castor (JAXB) default values where
 * required and creates a ClassDescriptor from it.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public final class ClassDescriptorBuilder {
    /**
     * Logger to use.
     */
    private static final Log LOG = LogFactory.getLog(ClassDescriptorBuilder.class);
    /**
     * The naming service to use for build XML names.
     */
    private XMLNaming _xmlNaming;
    
    /**
     * Default constructor.
     */
    protected ClassDescriptorBuilder() {
        super();
        _xmlNaming = new NamingJava2Xml();
    }

    /**
     * To set the XMLNaming service to be used.
     * @param xmlNaming XMLNaming service to use
     */
    protected void setXMLNaming(final XMLNaming xmlNaming) {
        _xmlNaming = xmlNaming;
    }

    /**
     * Builds a XMLClassDescriptor from the class information collected in
     * ClassInfo.
     * @param classInfo all information collected about a certain class
     * @param saveMapKeys @HACK no idea what this is good for...
     * @return the XMLClassDescriptor representing the class
     */
    XMLClassDescriptor buildClassDescriptor(
            final ClassInfo classInfo,
            final boolean saveMapKeys) {
        if (classInfo == null) {
            String message = "Argument classInfo must not be null";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        JAXBClassDescriptorImpl classDescriptor = new JAXBClassDescriptorImpl();
        classDescriptor.setJavaClass(classInfo.getClazz());
        classDescriptor.setXMLName(classInfo.getRootElementName());
        classDescriptor.setXMLName(getXMLName(classInfo));
        classDescriptor.setNameSpacePrefix(getNamespacePrefix(classInfo));
        classDescriptor.setNameSpaceURI(getNamespaceURI(classInfo));
        for (FieldInfo fieldInfo : classInfo.getFieldInfos()) {
            XMLFieldDescriptor fieldDescriptor = 
                buildFieldDescriptor(fieldInfo, classInfo.getXmlAccessType(), saveMapKeys);
            classDescriptor.addFieldDescriptor(fieldDescriptor);
        }
        return classDescriptor;
    }

    /**
     * Creates a XMLFieldDescriptor for the information collected
     * in FieldInfo.
     * @param fieldInfo all information collected about a field
     * @param xmlAccessType the access type to use
     * @param saveMapKeys @HACK no idea what this is good for...
     * @return the XMLFieldDescriptor representing the field
     */
    private XMLFieldDescriptor buildFieldDescriptor(
            final FieldInfo fieldInfo,
            final XmlAccessType xmlAccessType,
            final boolean saveMapKeys) {
        if (fieldInfo == null) {
            String message = "Argument fieldInfo must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        NodeType nodeType = null;
        String xmlName = null;
        String fieldName = null;
        if (fieldInfo.isElement()) {
            nodeType = NodeType.Element;
            xmlName = fieldInfo.getElementName();
            fieldName = fieldInfo.getElementName();
        } else if (fieldInfo.isAttribute()) {
            nodeType = NodeType.Attribute;
            xmlName = fieldInfo.getAttributeName();
            fieldName = fieldInfo.getAttributeName();
        } else {
            if (LOG.isDebugEnabled()) {
                String message = "Field: " + fieldInfo
                        + " is neither Element nor Attribute - so ignored";
                LOG.debug(message);
            }
            return null;
        }
        JAXBFieldDescriptorImpl fieldDescriptor = 
            new JAXBFieldDescriptorImpl(fieldInfo.getClass(), fieldName, xmlName, nodeType);
        FieldHandler fieldHandler = buildFieldHandler(fieldInfo, saveMapKeys);
        fieldDescriptor.setHandler(fieldHandler);
        return fieldDescriptor;
    }

    /**
     * To create the 'right' field handler for the field. For single value fields
     * the field handler depends on the type... for multivalue fields (which are
     * recognized by field type or methods available) more sophisticated algorithms
     * are used...
     * @param fieldInfo the field for which the handler is created
     * @param saveMapKeys @HACK no idea what this is good for...
     * @return the field handler for the field
     */
    private FieldHandler buildFieldHandler(
            final FieldInfo fieldInfo,
            final boolean saveMapKeys) {
        if (fieldInfo == null) {
            String message = "Argument fieldInfo must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        TypeInfo typeInfo = buildTypeInfo(fieldInfo);
        FieldHandler fieldHandler = null;
//        try {
//            FieldHandlerImpl fieldHandlerImpl = null;
//            if (fieldInfo.isPureField()) {
//                fieldHandlerImpl = new FieldHandlerImpl(fieldInfo.getField(), typeInfo);
//            } else {
//                fieldHandlerImpl = new FieldHandlerImpl(fieldInfo.getFieldName(),
//                        null,
//                        null,
//                        fieldInfo.getGetMethod(),
//                        fieldInfo.getSetMethod(),
//                        typeInfo);
//            }
//            if (fieldInfo.getAddMethod() != null) {
//                fieldHandlerImpl.setAddMethod(fieldInfo.getAddMethod());
//            }
//            if (fieldInfo.getCreateMethod() != null) {
//                fieldHandlerImpl.setCreateMethod(fieldInfo.getCreateMethod());
//            }
//            if (fieldInfo.isCollection() 
//                    && saveMapKeys
//                    && getCollectionCompatibilityKit().isMapCollection(fieldInfo.getFieldType())) {
//                fieldHandlerImpl.setConvertFrom(new IdentityConvertor());
//            } // if isCollection
//            fieldHandler = fieldHandlerImpl;
//            FieldHandlerFactory fhFactory = getHandlerFactory(fieldInfo.getFieldType());
//            if (fhFactory != null) {
//                GeneralizedFieldHandler gFieldHandler = 
//                    fhFactory.createFieldHandler(fieldInfo.getFieldType());
//                if (gFieldHandler != null) {
//                    gFieldHandler.setFieldHandler(fieldHandlerImpl);
//                    fieldHandler = gFieldHandler;
//                    if (gFieldHandler.getFieldType() != null) {
//                        fieldInfo.setFieldType(gFieldHandler.getFieldType());
//                    }
//                }
//            } // if fhFactory != null
//        } catch (MappingException e) {
//            LOG.warn("Fatally failed to create FieldHandler with exception: " + e);
//            throw new RuntimeException(e);
//        }
        return fieldHandler;
    }

    /**
     * Creates the TypeInfo for the field to create the correct FieldHandler. For collection
     * fields a collection handler is instantiated and for arrays the type is set to the
     * object type of the array elements.
     * @param fieldInfo the field for which the TypeInfo should be created
     * @return the TypeInfo instance
     */
    private TypeInfo buildTypeInfo(final FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            String message = "Argument fieldInfo must not be null.";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        CollectionHandler collectionHandler = null;
        if (fieldInfo.isMultivalue()) {
            try {
                collectionHandler = CollectionHandlers.getHandler(fieldInfo.getElementType());
            } catch (MappingException e) {
                // No collection found - continue without collection handler
            }
            if (fieldInfo.getElementType().isArray()) {
                if (fieldInfo.getElementType().getComponentType() == Byte.TYPE) {
                    collectionHandler = null;
                } else {
//                    fieldInfo.setFieldType(fieldInfo.getElementType());
                }
            }
        }
        return new TypeInfo(
                fieldInfo.getElementType(), null, null, false, null, collectionHandler);
    }
    
    /**
     * To get the XML name for the class. This is either the name spectified
     * in the RootElememnt annotation of derived from the class name.
     * 
     * @param classInfo the ClassInfo for which a XML name is wanted
     * @return the XML name
     */
    private String getXMLName(final ClassInfo classInfo) {
        if (classInfo == null) {
            throw new IllegalArgumentException();
        }
        if (classInfo.getRootElementName() == null
                || classInfo.getRootElementName().length() == 0) {
            return _xmlNaming.createXMLName(classInfo.getClazz());
        } else {
            return classInfo.getRootElementName();
        }
    }

    /**
     * To get the namespace URI either defined in the RootElement annotation
     * or Castor default (null).
     * 
     * @param classInfo the source
     * @return the namespace exracted or a default value
     */
    private String getNamespaceURI(final ClassInfo classInfo) {
        if (classInfo == null) {
            throw new IllegalArgumentException();
        }
        if (classInfo.getRootElementNamespace() == null
                || classInfo.getRootElementNamespace().length() == 0) {
            return null; // currently the default
        } else {
            return classInfo.getRootElementNamespace();
        }
    }

    /**
     * To get the namespace prefix to use. If no prefix is specified null is returned.
     * @param classInfo for which class
     * @return the namespace prefix found or null
     */
    private String getNamespacePrefix(final ClassInfo classInfo) {
        if (classInfo == null) {
            throw new IllegalArgumentException();
        }
        if (classInfo.getRootElementNamespace() == null
                || classInfo.getRootElementNamespace().length() == 0) {
            return null;
        } else {
            PackageInfo pi = classInfo.getPackageInfo();
            for (NamespaceInfo ni : pi.getNamespaces()) {
                if (classInfo.getRootElementNamespace().equals(ni.getNamespaceURI())) {
                    return ni.getPrefix();
                }
            }
            return null;
        }
    }
}
