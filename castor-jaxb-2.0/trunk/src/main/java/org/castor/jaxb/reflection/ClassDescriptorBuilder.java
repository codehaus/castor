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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.castor.jaxb.exceptions.ReflectionException;
import org.castor.jaxb.naming.JAXBXmlNaming;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.castor.jaxb.reflection.info.FieldInfo;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.info.JaxbPackageNature.NamespaceInfo;
import org.castor.xml.XMLNaming;
import org.exolab.castor.mapping.CollectionHandler;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.loader.CollectionHandlers;
import org.exolab.castor.mapping.loader.TypeInfo;
import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A service class to build a XMLClassDescriptor instance from ClassInfo. It
 * takes the values of ClassInfo, adds Castor (JAXB) default values where
 * required and creates a ClassDescriptor from it.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
@Component
public final class ClassDescriptorBuilder {
    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * The naming service to use for build XML names.
     */
    @Autowired
    private XMLNaming xmlNaming;

    /**
     * Default constructor.
     */
    public ClassDescriptorBuilder() {
        super();
        xmlNaming = new JAXBXmlNaming();
    }

    /**
     * @return the XMLNaming in use.
     */
    public XMLNaming getXMLNaming() {
        return xmlNaming;
    }

    /**
     * To set the XMLNaming service to be used.
     * 
     * @param xmlNaming
     *            XMLNaming service to use
     */
    public void setXMLNaming(final XMLNaming xmlNaming) {
        this.xmlNaming = xmlNaming;
    }

    /**
     * Builds a XMLClassDescriptor from the class information collected in
     * ClassInfo.
     * 
     * @param classInfo
     *            all information collected about a certain class
     * @param saveMapKeys
     * @HACK no idea what this is good for...
     * @return the XMLClassDescriptor representing the class
     */
    public XMLClassDescriptor buildClassDescriptor(final ClassInfo classInfo,
            final boolean saveMapKeys) {
        if (classInfo == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument classInfo must not be null");
            throw e;
        }
        JAXBClassDescriptorImpl classDescriptor = new JAXBClassDescriptorImpl();
        JaxbClassNature jaxbClassNature = new JaxbClassNature(classInfo);

        classDescriptor.setJavaClass(jaxbClassNature.getType());
        classDescriptor.setXMLName(getXMLName(jaxbClassNature));
        classDescriptor.setNameSpacePrefix(getNamespacePrefix(jaxbClassNature));
        classDescriptor.setNameSpaceURI(getNamespaceURI(jaxbClassNature));
        
        List<JaxbFieldNature> fields = new ArrayList<JaxbFieldNature>();
        fields.addAll(jaxbClassNature.getFields());

        if (jaxbClassNature.getXmlTransient()) {
            classDescriptor.setTransientClass(true);
        }
        if (jaxbClassNature.getXmlAccessorOrder()) {
            XmlAccessOrder accessOrder = jaxbClassNature.getXmlAccessOrder();
            if (accessOrder.equals(XmlAccessOrder.ALPHABETICAL)) {
                Collections.sort(fields, new Comparator<JaxbFieldNature>() {

                    public int compare(JaxbFieldNature fieldNature1, JaxbFieldNature fieldNature2) {
                        return getName(fieldNature1).compareTo(getName(fieldNature2));
                    }

                    private String getName(JaxbFieldNature fieldNature) {
                        String name = "";
                        if (StringUtils.isNotEmpty(fieldNature.getElementName())) {
                            name = fieldNature.getElementName(); 
                        }
                        if (StringUtils.isNotEmpty(fieldNature.getAttributeName())) {
                            name = fieldNature.getAttributeName();
                        }
                        return name;
                    }
                });
            }
        }
        
        for (JaxbFieldNature jaxbFieldNature : fields) {
            LOG.info("Field info: " + jaxbFieldNature + " now used");
            XMLFieldDescriptor fieldDescriptor = buildFieldDescriptor(
                    jaxbFieldNature, jaxbClassNature.getXmlAccessType(),
                    saveMapKeys);
            classDescriptor.addFieldDescriptor(fieldDescriptor);
        }

        return classDescriptor;
    }

    /**
     * Creates a XMLFieldDescriptor for the information collected in FieldInfo.
     * Default for fields it to use it as Element. Every other case needs to be
     * specified using annotations.
     * 
     * @param jaxbFieldNature
     *            all information collected about a field
     * @param xmlAccessType
     *            the access type to use
     * @param saveMapKeys
     * @HACK no idea what this is good for...
     * @return the XMLFieldDescriptor representing the field
     */
    private XMLFieldDescriptor buildFieldDescriptor(
            final JaxbFieldNature jaxbFieldNature,
            final XmlAccessType xmlAccessType, final boolean saveMapKeys) {
        if (jaxbFieldNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument fieldInfo must not be null.");
            throw e;
        }
        NodeType nodeType = null;
        String xmlName = null;
        String fieldName = jaxbFieldNature.getFieldName();
        if (jaxbFieldNature.hasXmlAttribute()) {
            nodeType = NodeType.Attribute;
            xmlName = getXMLName(jaxbFieldNature); // fieldInfo.getAttributeName();
        } else if (jaxbFieldNature.hasXmlElement()) {
            nodeType = NodeType.Element;
            xmlName = getXMLName(jaxbFieldNature); // fieldInfo.getElementName();
        } else {
            if (LOG.isDebugEnabled()) {
                String message = "Field: " + jaxbFieldNature
                        + " not specified - use it as Element";
                LOG.debug(message);
            }
            nodeType = NodeType.Element;
            xmlName = getXMLName(jaxbFieldNature); // fieldInfo.getElementName();
        }
        
        JAXBFieldDescriptorImpl fieldDescriptor = new JAXBFieldDescriptorImpl(
                getType(jaxbFieldNature), fieldName, xmlName, nodeType);
        fieldDescriptor.setMultivalued(jaxbFieldNature.isMultivalue());
        if (jaxbFieldNature.isMultivalue()) {
            fieldDescriptor.setFieldType(jaxbFieldNature.getGenericType()
                    .getClass());
        }
        FieldHandler fieldHandler = buildFieldHandler(jaxbFieldNature,
                saveMapKeys);
        fieldDescriptor.setHandler(fieldHandler);
        
        if (jaxbFieldNature.hasXmlElementWrapper()) {
            String location = jaxbFieldNature.getElementWrapperName(); 
            fieldDescriptor.setLocationPath(location);
        }
        
        if (jaxbFieldNature.hasXmlTransient()) {
            fieldDescriptor.setTransient(true);
        }

        // TODO: refactor as it does not resolve class transitivity
        // TODO: problem is that we do not have informtion about the ClassInfo of the decöard type
        ClassInfo owningClassInfo = jaxbFieldNature.getFieldInfo().getParentClassInfo();
        if (owningClassInfo.hasNature(JaxbClassNature.class.getName())) {
            JaxbClassNature classNature = new JaxbClassNature(owningClassInfo);
            if (classNature.getXmlTransient()) {
                fieldDescriptor.setContainer(true);
            }
        }
        
        return fieldDescriptor;
    }

    /**
     * To create the 'right' field handler for the field. For single value
     * fields the field handler depends on the type... for multivalue fields
     * (which are recognized by field type or methods available) more
     * sophisticated algorithms are used...
     * 
     * @param jaxbFieldNature
     *            the field for which the handler is created
     * @param saveMapKeys
     * @HACK no idea what this is good for...
     * @return the field handler for the field
     */
    private FieldHandler buildFieldHandler(
            final JaxbFieldNature jaxbFieldNature, final boolean saveMapKeys) {
        if (jaxbFieldNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument fieldInfo must not be null.");
           throw e;
        }
        // TypeInfo typeInfo = buildTypeInfo(fieldInfo);
        JAXBFieldHandlerImpl fieldHandler = new JAXBFieldHandlerImpl();
        // fieldHandler.setType(type);
        // fieldHandler.setTypeFactory(typeFactoryClass, typeFactoryMethod);
        // fieldHandler.setXmlAdapter(xmlAdapter);
        if (jaxbFieldNature.isPureField()) {
            fieldHandler.setField(jaxbFieldNature.getField());
        } else {
            fieldHandler.setMethods(jaxbFieldNature.getMethodGet(),
                    jaxbFieldNature.getMethodSet());
        }
        // if (fieldInfo.getMethodAdd() != null) {
        // fieldHandler.setAddMethod(fieldInfo.getMethodAdd());
        // }
        // if (fieldInfo.getMethodCreate() != null) {
        // fieldHandler.setCreateMethod(fieldInfo.getMethodCreate());
        // }

        // if (fieldInfo.isCollection()
        // && saveMapKeys
        // &&
        // getCollectionCompatibilityKit().isMapCollection(fieldInfo.getFieldType()))
        // {
        // fieldHandlerImpl.setConvertFrom(new IdentityConvertor());
        // } // if isCollection
        // fieldHandler = fieldHandlerImpl;
        // FieldHandlerFactory fhFactory =
        // getHandlerFactory(fieldInfo.getFieldType());
        // if (fhFactory != null) {
        // GeneralizedFieldHandler gFieldHandler =
        // fhFactory.createFieldHandler(fieldInfo.getFieldType());
        // if (gFieldHandler != null) {
        // gFieldHandler.setFieldHandler(fieldHandlerImpl);
        // fieldHandler = gFieldHandler;
        // if (gFieldHandler.getFieldType() != null) {
        // fieldInfo.setFieldType(gFieldHandler.getFieldType());
        // }
        // }
        // } // if fhFactory != null
        // } catch (MappingException e) {
        // LOG.warn("Fatally failed to create FieldHandler with exception: " +
        // e);
        // throw new RuntimeException(e);
        // }
        return fieldHandler;
    }

    /**
     * Creates the TypeInfo for the field to create the correct FieldHandler.
     * For collection fields a collection handler is instantiated and for arrays
     * the type is set to the object type of the array elements.
     * 
     * @param jaxbFieldNature
     *            the field for which the TypeInfo should be created
     * @return the TypeInfo instance
     */
    private TypeInfo buildTypeInfo(final JaxbFieldNature jaxbFieldNature) {
        if (jaxbFieldNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "Argument fieldInfo must not be null.");
            throw e;
        }
        CollectionHandler collectionHandler = null;
        if (jaxbFieldNature.isMultivalue()) {
            if (LOG.isDebugEnabled()) {
                LOG
                        .debug(jaxbFieldNature
                                + " is multivalued and required special collection handler.");
            }
            try {
                collectionHandler = CollectionHandlers
                        .getHandler(getType(jaxbFieldNature));
            } catch (MappingException e) {
                // No collection found - continue without collection handler
            }
            if (getType(jaxbFieldNature).isArray()) {
                if (getType(jaxbFieldNature).getComponentType() == Byte.TYPE) {
                    collectionHandler = null;
                } else {
                    // fieldInfo.setFieldType(fieldInfo.getElementType());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(jaxbFieldNature + " creating TypeInfo.");
        }
        return new TypeInfo(getType(jaxbFieldNature), null, null, false, null,
                collectionHandler);
    }

    /**
     * To get the XML name for the class. This is either the name spectified in
     * the RootElememnt annotation of derived from the class name.
     * 
     * @param jaxbClassNature
     *            the ClassInfo for which a XML name is wanted
     * @return the XML name
     */
    private String getXMLName(final JaxbClassNature jaxbClassNature) {
        if (jaxbClassNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getXMLName(null) is illegal.");
            throw e;
        }
        if (jaxbClassNature.getRootElementName() == null
                || jaxbClassNature.getRootElementName().length() == 0) {
            return xmlNaming.toXMLName(jaxbClassNature.getClassName());
        } else {
            return jaxbClassNature.getRootElementName();
        }
    }

    /**
     * To get the namespace URI either defined in the RootElement annotation or
     * Castor default (null).
     * 
     * @param jaxbClassNature
     *            the source
     * @return the namespace exracted or a default value
     */
    private String getNamespaceURI(final JaxbClassNature jaxbClassNature) {
        if (jaxbClassNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getNamespaceURI(null) is illegal.");
            throw e;
        }
        if (jaxbClassNature.getRootElementNamespace() == null
                || jaxbClassNature.getRootElementNamespace().length() == 0) {
            return null; // currently the default
        } else {
            return jaxbClassNature.getRootElementNamespace();
        }
    }

    /**
     * To get the namespace prefix to use. If no prefix is specified null is
     * returned.
     * 
     * @param jaxbClassNature
     *            for which class
     * @return the namespace prefix found or null
     */
    private String getNamespacePrefix(final JaxbClassNature jaxbClassNature) {
        if (jaxbClassNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getNamespacePrefix(null) is illegal.");
            throw e;
        }
        if (jaxbClassNature.getRootElementNamespace() == null
                || jaxbClassNature.getRootElementNamespace().length() == 0) {
            return null;
        } else {
            JaxbPackageNature jaxbPackageNature = jaxbClassNature.getPackage();
            for (NamespaceInfo ni : jaxbPackageNature.getNamespaceInfos()) {
                if (jaxbClassNature.getRootElementNamespace().equals(
                        ni.getNamespaceURI())) {
                    return ni.getPrefix();
                }
            }
            return null;
        }
    }

    /**
     * To get the XML name for the class. This is either the name specified in
     * the {@link XmlRootElement} annotation of derived from the class name.
     * 
     * @param jaxbFieldNature
     *            the FieldInfo for which a XML name is wanted
     * @return the XML name
     */
    private String getXMLName(final JaxbFieldNature jaxbFieldNature) {
        if (jaxbFieldNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getXMLName(null) is illegal.");
            throw e;
        }
        String xmlName = null;
        if (jaxbFieldNature.getRootElementName() != null
                && jaxbFieldNature.getRootElementName().length() != 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + jaxbFieldNature + " is: "
                        + jaxbFieldNature.getRootElementName());
            }
            xmlName = jaxbFieldNature.getRootElementName();
        } else if (jaxbFieldNature.getElementName() != null
                && jaxbFieldNature.getElementName().length() != 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + jaxbFieldNature + " is: "
                        + jaxbFieldNature.getElementName());
            }
            xmlName = jaxbFieldNature.getElementName();
        } else if (jaxbFieldNature.getAttributeName() != null
                && jaxbFieldNature.getAttributeName().length() != 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + jaxbFieldNature + " is: "
                        + jaxbFieldNature.getAttributeName());
            }
            xmlName = jaxbFieldNature.getAttributeName();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML name for: " + jaxbFieldNature + " is: "
                        + jaxbFieldNature.getFieldName());
            }
            xmlName = jaxbFieldNature.getFieldName();
        }
        return xmlNaming.toXMLName(xmlName);
    }

    /**
     * To get the type for a field. The type of a field is either given by an
     * annotation or needs to be extracted from field/method.
     * 
     * @param jaxbFieldNature
     *            the FieldInfo to interpret
     * @return the type
     */
    private Class<?> getType(final JaxbFieldNature jaxbFieldNature) {
        if (jaxbFieldNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getType(null) is illegal.");
            throw e;
        }
        Class<?> type = null;
        if (jaxbFieldNature.hasXmlElement()) {
            if (jaxbFieldNature.getElementType() != null) {
                type = jaxbFieldNature.getElementType();
            } else {
                type = getTypeFromFieldOrMethod(jaxbFieldNature);
            }
        } else if (jaxbFieldNature.hasXmlAttribute()) {
            type = getTypeFromFieldOrMethod(jaxbFieldNature);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG
                        .debug("Neither Element nor attribute - use default method to detect type.");
            }
            type = getTypeFromFieldOrMethod(jaxbFieldNature);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Type for field: " + jaxbFieldNature + " is: " + type);
        }
        return type;
    }

    /**
     * If the type should be read from field or method but not from an
     * annotation information.
     * 
     * @param jaxbFieldNature
     *            to get field and method from
     * @return the type
     */
    private Class<?> getTypeFromFieldOrMethod(
            final JaxbFieldNature jaxbFieldNature) {
        if (jaxbFieldNature == null) {
            IllegalArgumentException e = new IllegalArgumentException(
                    "getType(null) is illegal.");
            throw e;
        }
        Class<?> type = null;
        if (jaxbFieldNature.getField() != null) {
            type = jaxbFieldNature.getField().getType();
        } else if (jaxbFieldNature.getMethodGet() != null) {
            type = jaxbFieldNature.getMethodGet().getReturnType();
        } else {
            ReflectionException e = new ReflectionException(jaxbFieldNature,
                    "Failed to get type of fieldInfo: " + jaxbFieldNature
                            + " no field and no get method known.");
            throw e;
        }
        return type;
    }
}
