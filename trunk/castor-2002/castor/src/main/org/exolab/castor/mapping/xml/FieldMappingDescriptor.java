/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.8 (20000324)</a>,
 * using an XML Schema.
 * $Id
 */

package org.exolab.castor.mapping.xml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;

/**
 * 
 * @version $Revision$ $Date$
**/
public class FieldMappingDescriptor implements org.exolab.castor.xml.XMLClassDescriptor {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    private org.exolab.castor.xml.XMLFieldDescriptor[] elements;

    private org.exolab.castor.xml.XMLFieldDescriptor[] attributes;

    private org.exolab.castor.xml.util.XMLFieldDescriptorImpl contentDesc;

    private java.lang.String nsPrefix;

    private java.lang.String nsURI;

    private java.lang.String xmlName;


      //----------------/
     //- Constructors -/
    //----------------/

    public FieldMappingDescriptor() {
        xmlName = "field";
        XMLFieldDescriptorImpl desc = null;
        XMLFieldHandler handler = null;
        //-- initialize attribute descriptors
        
        attributes = new XMLFieldDescriptorImpl[7];
        //-- _collection
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_collection", "collection", NodeType.Attribute);
        desc.setImmutable(true);
        desc.setHandler( new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getCollection();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setCollection( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        attributes[0] = desc;
        
        //-- _setMethod
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_setMethod", "set-method", NodeType.Attribute);
        desc.setHandler( new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getSetMethod();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setSetMethod( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new java.lang.String();
            }
        } );
        attributes[1] = desc;
        
        //-- _createMethod
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_createMethod", "create-method", NodeType.Attribute);
        desc.setHandler( new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getCreateMethod();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setCreateMethod( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new java.lang.String();
            }
        } );
        attributes[2] = desc;
        
        //-- _getMethod
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_getMethod", "get-method", NodeType.Attribute);
        desc.setHandler( new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getGetMethod();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setGetMethod( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new java.lang.String();
            }
        } );
        attributes[3] = desc;
        
        //-- _type
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_type", "type", NodeType.Attribute);
        desc.setHandler( new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getType();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setType( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new java.lang.String();
            }
        } );
        attributes[4] = desc;
        
        //-- _required
        desc = new XMLFieldDescriptorImpl(boolean.class, "_required", "required", NodeType.Attribute);
        desc.setHandler( new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return new Boolean(target.getRequired());
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setRequired( ((Boolean)value).booleanValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        attributes[5] = desc;
        
        //-- _name
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_name", "name", NodeType.Attribute);
        desc.setHandler( new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getName();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setName( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new java.lang.String();
            }
        } );
        attributes[6] = desc;
        
        //-- initialize element descriptors
        
        elements = new XMLFieldDescriptorImpl[4];
        //-- _description
        desc = new XMLFieldDescriptorImpl(Description.class, "_description", "description", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getDescription();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setDescription( (Description) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new Description();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        elements[0] = desc;
        
        //-- _sqlInfo
        desc = new XMLFieldDescriptorImpl(SqlInfo.class, "_sqlInfo", "sql-info", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getSqlInfo();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setSqlInfo( (SqlInfo) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new SqlInfo();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        elements[1] = desc;
        
        //-- _xmlInfo
        desc = new XMLFieldDescriptorImpl(XmlInfo.class, "_xmlInfo", "xml-info", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getXmlInfo();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setXmlInfo( (XmlInfo) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new XmlInfo();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        elements[2] = desc;
        
        //-- _ldapInfo
        desc = new XMLFieldDescriptorImpl(LdapInfo.class, "_ldapInfo", "ldap-info", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                FieldMapping target = (FieldMapping) object;
                return target.getLdapInfo();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FieldMapping target = (FieldMapping) object;
                    target.setLdapInfo( (LdapInfo) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return new LdapInfo();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        elements[3] = desc;
        
    } //-- org.exolab.castor.mapping.xml.FieldMappingDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public org.exolab.castor.mapping.AccessMode getAccessMode() {
        return null;
    } //-- org.exolab.castor.mapping.AccessMode getAccessMode() 

    /**
    **/
    public org.exolab.castor.xml.XMLFieldDescriptor[] getAttributeDescriptors() {
        return attributes;
    } //-- org.exolab.castor.xml.XMLFieldDescriptor[] getAttributeDescriptors() 

    /**
    **/
    public org.exolab.castor.xml.XMLFieldDescriptor getContentDescriptor() {
        return contentDesc;
    } //-- org.exolab.castor.xml.XMLFieldDescriptor getContentDescriptor() 

    /**
    **/
    public org.exolab.castor.xml.XMLFieldDescriptor[] getElementDescriptors() {
        return elements;
    } //-- org.exolab.castor.xml.XMLFieldDescriptor[] getElementDescriptors() 

    /**
    **/
    public org.exolab.castor.mapping.ClassDescriptor getExtends() {
        return null;
    } //-- org.exolab.castor.mapping.ClassDescriptor getExtends() 

    /**
    **/
    public org.exolab.castor.mapping.FieldDescriptor[] getFields() {
        int size = attributes.length + elements.length;
        if (contentDesc != null) ++size;
        
        FieldDescriptor[] fields = new FieldDescriptor[size];
        int c = 0;
        for (int i = 0; i < attributes.length; i++)
            fields[c++] = attributes[i];
        
        for (int i = 0; i < elements.length; i++)
            fields[c++] = elements[i];
        
        if (contentDesc != null) fields[c] = contentDesc;
        
        return fields;
    } //-- org.exolab.castor.mapping.FieldDescriptor[] getFields() 

    /**
    **/
    public org.exolab.castor.mapping.FieldDescriptor getIdentity() {
        return null;
    } //-- org.exolab.castor.mapping.FieldDescriptor getIdentity() 

    /**
    **/
    public java.lang.Class getJavaClass() {
        return org.exolab.castor.mapping.xml.FieldMapping.class;
    } //-- java.lang.Class getJavaClass() 

    /**
    **/
    public java.lang.String getNameSpacePrefix() {
        return nsPrefix;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
    **/
    public java.lang.String getNameSpaceURI() {
        return nsURI;
    } //-- java.lang.String getNameSpaceURI() 

    /**
    **/
    public java.lang.String getXMLName() {
        return xmlName;
    } //-- java.lang.String getXMLName() 

}
