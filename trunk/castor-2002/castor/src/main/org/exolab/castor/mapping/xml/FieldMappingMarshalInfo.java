/*
 * Add code header here
 * $Id$ 
 */

package org.exolab.castor.mapping.xml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.lang.reflect.Method;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.GroupValidationRule;
import org.exolab.castor.xml.MarshalDescriptor;
import org.exolab.castor.xml.SimpleMarshalDescriptor;
import org.exolab.castor.xml.ValidationRule;

/**
 * @version $Revision$ $Date$
**/
public class FieldMappingMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private org.exolab.castor.xml.MarshalDescriptor[] elements;

    /**
     * 
    **/
    private org.exolab.castor.xml.MarshalDescriptor[] attributes;

    /**
     * 
    **/
    private org.exolab.castor.xml.SimpleMarshalDescriptor contentDesc;

    /**
     * 
    **/
    private org.exolab.castor.xml.GroupValidationRule gvr;

    /**
     * 
    **/
    private org.exolab.castor.xml.ValidationRule[] rules;


      //----------------/
     //- Constructors -/
    //----------------/

    public FieldMappingMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        gvr = new GroupValidationRule();
        BasicValidationRule bvr = null;
        rules = new ValidationRule[7];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[6];
        //-- vCollection
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vCollection", "collection");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getCollection", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setCollection", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        bvr = new BasicValidationRule("collection");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        rules[0] = bvr;
        //-- vSetMethod
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vSetMethod", "set-method");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getSetMethod", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setSetMethod", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[1] = desc;
        
        bvr = new BasicValidationRule("set-method");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[1] = bvr;
        //-- vGetMethod
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vGetMethod", "get-method");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getGetMethod", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setGetMethod", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[2] = desc;
        
        bvr = new BasicValidationRule("get-method");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[2] = bvr;
        //-- vType
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vType", "type");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getType", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setType", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[3] = desc;
        
        bvr = new BasicValidationRule("type");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[3] = bvr;
        //-- vRequired
        desc = new SimpleMarshalDescriptor(boolean.class, "vRequired", "required");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getRequired", emptyClassArgs));
            classArgs[0] = boolean.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setRequired", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[4] = desc;
        
        bvr = new BasicValidationRule("required");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        rules[4] = bvr;
        //-- vName
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vName", "name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getName", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[5] = desc;
        
        bvr = new BasicValidationRule("name");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[5] = bvr;
        rules[6] = gvr;
        //-- initialize elements
        
        elements = new MarshalDescriptor[4];
        //-- vDescription
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vDescription", "description");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getDescription", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setDescription", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[0] = desc;
        
        bvr = new BasicValidationRule("description");
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        gvr.addValidationRule(bvr);
        //-- vSqlInfo
        desc = new SimpleMarshalDescriptor(SqlInfo.class, "vSqlInfo", "sql-info");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getSqlInfo", emptyClassArgs));
            classArgs[0] = SqlInfo.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setSqlInfo", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[1] = desc;
        
        bvr = new BasicValidationRule("sql-info");
        bvr.setMaxOccurs(1);
        gvr.addValidationRule(bvr);
        //-- vXmlInfo
        desc = new SimpleMarshalDescriptor(XmlInfo.class, "vXmlInfo", "xml-info");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getXmlInfo", emptyClassArgs));
            classArgs[0] = XmlInfo.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setXmlInfo", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[2] = desc;
        
        bvr = new BasicValidationRule("xml-info");
        bvr.setMaxOccurs(1);
        gvr.addValidationRule(bvr);
        //-- vLdapInfo
        desc = new SimpleMarshalDescriptor(LdapInfo.class, "vLdapInfo", "ldap-info");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(FieldMapping.class.getMethod("getLdapInfo", emptyClassArgs));
            classArgs[0] = LdapInfo.class;
            desc.setWriteMethod(FieldMapping.class.getMethod("setLdapInfo", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[3] = desc;
        
        bvr = new BasicValidationRule("ldap-info");
        bvr.setMaxOccurs(1);
        gvr.addValidationRule(bvr);
    } //-- FieldMappingMarshalInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public org.exolab.castor.xml.ValidationRule[] getValidationRules() {
        return rules;
    } //-- org.exolab.castor.xml.ValidationRule[] getValidationRules() 

    /**
    **/
    public org.exolab.castor.xml.MarshalDescriptor[] getAttributeDescriptors() {
        return attributes;
    } //-- org.exolab.castor.xml.MarshalDescriptor[] getAttributeDescriptors() 

    /**
    **/
    public java.lang.Class getClassType() {
        return FieldMapping.class;
    } //-- java.lang.Class getClassType() 

    /**
    **/
    public org.exolab.castor.xml.MarshalDescriptor[] getElementDescriptors() {
        return elements;
    } //-- org.exolab.castor.xml.MarshalDescriptor[] getElementDescriptors() 

    /**
    **/
    public org.exolab.castor.xml.MarshalDescriptor getContentDescriptor() {
        return contentDesc;
    } //-- org.exolab.castor.xml.MarshalDescriptor getContentDescriptor() 

    /**
    **/
    public java.lang.String getNameSpacePrefix() {
        return null;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
    **/
    public java.lang.String getNameSpaceURI() {
        return null;
    } //-- java.lang.String getNameSpaceURI() 

}
