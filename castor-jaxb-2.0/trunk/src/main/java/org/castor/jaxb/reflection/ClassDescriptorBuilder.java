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
import org.castor.jaxb.exceptions.FatalReflectionException;
import org.castor.jaxb.exceptions.ReflectionException;
import org.castor.jaxb.naming.JAXBXmlNaming;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.PackageInfo;
import org.castor.jaxb.reflection.info.PackageInfo.NamespaceInfo;
import org.castor.xml.XMLNaming;
import org.exolab.castor.mapping.CollectionHandler;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.loader.CollectionHandlers;
import org.exolab.castor.mapping.loader.FieldHandlerImpl;
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
    public ClassDescriptorBuilder() {
        super();
        _xmlNaming = new JAXBXmlNaming();
    }

    /**
     * @return the XMLNaming in use.
     */
    public XMLNaming getXMLNaming() {
        return _xmlNaming;
    }

    /**
     * To set the XMLNaming service to be used.
     * @param xmlNaming XMLNaming service to use
     */
    public void setXMLNaming(final XMLNaming xmlNaming) {
        _xmlNaming = xmlNaming;
    }

    /**
     * Builds a XMLClassDescriptor from the class information collected in
     * ClassInfo.
     * @param classInfo all information collected about a certain class
     * @param saveMapKeys @HACK no idea what this is good for...
     * @return the XMLClassDescriptor representing the class
     */
    public XMLClassDescriptor buildClassDescriptor(
            final ClassInfo classInfo,
            final boolean saveMapKeys) {
        if (classInfo == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument classInfo must not be null");
            LOG.warn(e);
            throw e;
        }
        JAXBClassDescriptorImpl classDescriptor = new JAXBClassDescriptorImpl();
        classDescriptor.setJavaClass(classInfo.getType());
        classDescriptor.setXMLName(getXMLName(classInfo));
        classDescriptor.setNameSpacePrefix(getNamespacePrefix(classInfo));
        classDescriptor.setNameSpaceURI(getNamespaceURI(classInfo));
        for (FieldInfo fieldInfo : classInfo.getFieldInfos()) {
            LOG.info("Field info: " + fieldInfo + " now used");
            XMLFieldDescriptor fieldDescriptor = 
                buildFieldDescriptor(fieldInfo, classInfo.getXmlAccessType(), saveMapKeys);
            classDescriptor.addFieldDescriptor(fieldDescriptor);
        }
        return classDescriptor;
    }

    /**
     * Creates a XMLFieldDescriptor for the information collected
     * in FieldInfo. Default for fields it to use it as Element. Every
     * other case needs to be specified using annotations.
     * 
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
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument fieldInfo must not be null.");
            LOG.warn(e);
            throw e;
        }
        NodeType nodeType = null;
        String xmlName = null;
        String fieldName = fieldInfo.getFieldName();
        if (fieldInfo.isAttribute()) {
            nodeType = NodeType.Attribute;
            xmlName = getXMLName(fieldInfo); //fieldInfo.getAttributeName();
        } else if (fieldInfo.isElement()) {
            nodeType = NodeType.Element;
            xmlName = getXMLName(fieldInfo); //fieldInfo.getElementName();
        } else {
            if (LOG.isDebugEnabled()) {
                String message = "Field: " + fieldInfo
                        + " not specified - use it as Element";
                LOG.debug(message);
            }
            nodeType = NodeType.Element;
            xmlName = getXMLName(fieldInfo); //fieldInfo.getElementName();
        }
        JAXBFieldDescriptorImpl fieldDescriptor = 
            new JAXBFieldDescriptorImpl(getType(fieldInfo), fieldName, xmlName, nodeType);
        fieldDescriptor.setMultivalued(fieldInfo.isMultivalue());
        if (fieldInfo.isMultivalue()) {
            fieldDescriptor.setFieldType(fieldInfo.getGenericType().getClass());
        }
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
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument fieldInfo must not be null.");
            LOG.warn(e);
            throw e;
        }
//        TypeInfo typeInfo = buildTypeInfo(fieldInfo);
        JAXBFieldHandlerImpl fieldHandler = new JAXBFieldHandlerImpl();
        if (fieldInfo.isPureField()) {
            fieldHandler.setField(fieldInfo.getField());
        } else {
            fieldHandler.setMethods(fieldInfo.getMethodGet(), fieldInfo.getMethodSet());
        }
//        if (fieldInfo.getMethodAdd() != null) {
//            fieldHandler.setAddMethod(fieldInfo.getMethodAdd());
//        }
//        if (fieldInfo.getMethodCreate() != null) {
//            fieldHandler.setCreateMethod(fieldInfo.getMethodCreate());
//        }

// if (fieldInfo.isCollection()
// && saveMapKeys
// && getCollectionCompatibilityKit().isMapCollection(fieldInfo.getFieldType()))
// {
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
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument fieldInfo must not be null.");
            LOG.warn(e);
            throw e;
        }
        CollectionHandler collectionHandler = null;
        if (fieldInfo.isMultivalue()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(fieldInfo + " is multivalued and required special collection handler.");
            }
            try {
                collectionHandler = CollectionHandlers.getHandler(getType(fieldInfo));
            } catch (MappingException e) {
                // No collection found - continue without collection handler
            }
            if (getType(fieldInfo).isArray()) {
                if (getType(fieldInfo).getComponentType() == Byte.TYPE) {
                    collectionHandler = null;
                } else {
//                    fieldInfo.setFieldType(fieldInfo.getElementType());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(fieldInfo + " creating TypeInfo.");
        }
        return new TypeInfo(
                getType(fieldInfo), null, null, false, null, collectionHandler);
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
            IllegalArgumentException e = new IllegalArgumentException(
                    "getXMLName(null) is illegal.");
            LOG.warn(e);
            throw e;
        }
        if (classInfo.getRootElementName() == null
                || classInfo.getRootElementName().length() == 0
                || ClassInfo.DEFAULT_ELEMENT_NAME.equals(classInfo.getRootElementName())) {
            return _xmlNaming.toXMLName(classInfo.getClassName());
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
            IllegalArgumentException e = new IllegalArgumentException(
                    "getNamespaceURI(null) is illegal.");
            LOG.warn(e);
            throw e;
        }
        if (classInfo.getRootElementNamespace() == null
                || classInfo.getRootElementNamespace().length() == 0
                || ClassInfo.DEFAULT_ELEMENT_NAMESPACE.equals(classInfo.getRootElementNamespace())) {
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
            IllegalArgumentException e = new IllegalArgumentException(
                    "getNamespacePrefix(null) is illegal.");
            LOG.warn(e);
            throw e;
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
    
    /**
     * To get the XML name for the class. This is either the name specified
     * in the RootElememnt annotation of derived from the class name.
     * 
     * @param fieldInfo the FieldInfo for which a XML name is wanted
     * @return the XML name
     */
    private String getXMLName(final FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getXMLName(null) is illegal.");
            LOG.warn(e);
            throw e;
        }
        String xmlName = null;
        if (fieldInfo.getRootElementName() != null
                && fieldInfo.getRootElementName().length() != 0
                && !FieldInfo.DEFAULT_ELEMENT_NAME.equals(fieldInfo.getRootElementName())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + fieldInfo + " is: " + fieldInfo.getRootElementName());
            }
            xmlName = fieldInfo.getRootElementName();
        } else if (fieldInfo.getElementName() != null
                && fieldInfo.getElementName().length() != 0
                && !FieldInfo.DEFAULT_ELEMENT_NAME.equals(fieldInfo.getElementName())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + fieldInfo + " is: " + fieldInfo.getElementName());
            }
            xmlName = fieldInfo.getElementName();
        } else if (fieldInfo.getAttributeName() != null
                && fieldInfo.getAttributeName().length() != 0
                && !FieldInfo.DEFAULT_ATTRIBUTE_NAME.equals(fieldInfo.getAttributeName())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + fieldInfo + " is: " + fieldInfo.getAttributeName());
            }
            xmlName = fieldInfo.getAttributeName();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + fieldInfo + " is: " + fieldInfo.getFieldName());
            }
            xmlName = fieldInfo.getFieldName();
        }
        return _xmlNaming.toXMLName(xmlName);
    }
    
    /**
     * To get the type for a field. The type of a field is either given by an
     * annotation or needs to be extracted from field/method.
     * 
     * @param fieldInfo the FieldInfo to interpret
     * @return the type
     */
    private Class < ? > getType(final FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getType(null) is illegal.");
            LOG.warn(e);
            throw e;
        }
        Class < ? > type = null;
        if (fieldInfo.isElement()) {
            if (fieldInfo.getElementType() != null) {
                type = fieldInfo.getElementType();
            } else {
                type = getTypeFromFieldOrMethod(fieldInfo);
            }
        } else if (fieldInfo.isAttribute()) {
            type = getTypeFromFieldOrMethod(fieldInfo);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Neither Element nor attribute - use default method to detect type.");
            }
            type = getTypeFromFieldOrMethod(fieldInfo);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Type for field: " + fieldInfo + " is: " + type);
        }
        return type;
    }
    
    /**
     * If the type should be read from field or method but not from an
     * annotation information.
     * 
     * @param fieldInfo to get field and method from
     * @return the type
     */
    private Class < ? > getTypeFromFieldOrMethod(final FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getType(null) is illegal.");
            LOG.warn(e);
            throw e;
        }
        Class < ? > type = null;
        if (fieldInfo.getField() != null) {
            type = fieldInfo.getField().getType();
        } else if (fieldInfo.getMethodGet() != null) {
            type = fieldInfo.getMethodGet().getReturnType();
        } else {
            ReflectionException e = new ReflectionException(fieldInfo,
                    "Failed to get type of fieldInfo, no field and no get method known.");
            LOG.warn(e);
            throw e;
        }
        return type;
    }
}
