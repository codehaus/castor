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
public class ClassMappingMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public ClassMappingMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        gvr = new GroupValidationRule();
        BasicValidationRule bvr = null;
        rules = new ValidationRule[3];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[3];
        //-- vExtends
        desc = new SimpleMarshalDescriptor(String.class, "vExtends", "extends");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getExtends", emptyClassArgs));
            classArgs[0] = String.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setExtends", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        bvr = new BasicValidationRule("extends");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        rules[0] = bvr;
        //-- vClassName
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vClassName", "class-name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getClassName", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setClassName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[1] = desc;
        
        bvr = new BasicValidationRule("class-name");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        rules[1] = bvr;
        rules[2] = gvr;

        //-- vAccessMode
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vAccessMode", "access");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getAccessMode", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setAccessMode", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[2] = desc;
        
        bvr = new BasicValidationRule("access");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        rules[1] = bvr;
        rules[2] = gvr;
        //-- initialize elements
        
        elements = new MarshalDescriptor[7];
        //-- vDescription
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vDescription", "description");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getDescription", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setDescription", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[0] = desc;
        
        bvr = new BasicValidationRule("description");
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        gvr.addValidationRule(bvr);
        //-- vSqlTable
        desc = new SimpleMarshalDescriptor(SqlTable.class, "vSqlTable", "sql-table");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getSqlTable", emptyClassArgs));
            classArgs[0] = SqlTable.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setSqlTable", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[1] = desc;
        
        bvr = new BasicValidationRule("sql-table");
        bvr.setMaxOccurs(1);
        gvr.addValidationRule(bvr);
        //-- vXmlSchema
        desc = new SimpleMarshalDescriptor(XmlSchema.class, "vXmlSchema", "xml-schema");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getXmlSchema", emptyClassArgs));
            classArgs[0] = XmlSchema.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setXmlSchema", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[2] = desc;
        
        bvr = new BasicValidationRule("xml-schema");
        bvr.setMaxOccurs(1);
        gvr.addValidationRule(bvr);
        //-- vLdapEntry
        desc = new SimpleMarshalDescriptor(LdapEntry.class, "vLdapEntry", "ldap-entry");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getLdapEntry", emptyClassArgs));
            classArgs[0] = LdapEntry.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setLdapEntry", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[3] = desc;
        
        bvr = new BasicValidationRule("ldap-entry");
        bvr.setMaxOccurs(1);
        gvr.addValidationRule(bvr);
        //-- vIdentity
        desc = new SimpleMarshalDescriptor(Identity.class, "vIdentity", "identity");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getIdentity", emptyClassArgs));
            classArgs[0] = Identity.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("setIdentity", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[4] = desc;
        
        bvr = new BasicValidationRule("identity");
        bvr.setMaxOccurs(1);
        gvr.addValidationRule(bvr);
        //-- vFieldList
        desc = new SimpleMarshalDescriptor(FieldMapping.class, "vFieldList", "field");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getFieldMapping", emptyClassArgs));
            classArgs[0] = FieldMapping.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("addFieldMapping", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setRequired(true);
        desc.setMultivalued(true);
        elements[5] = desc;
        
        bvr = new BasicValidationRule("field");
        bvr.setMinOccurs(0);
        gvr.addValidationRule(bvr);
        //-- vContainerList
        desc = new SimpleMarshalDescriptor(ContainerMapping.class, "vContainerList", "container");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ClassMapping.class.getMethod("getContainerMapping", emptyClassArgs));
            classArgs[0] = ContainerMapping.class;
            desc.setWriteMethod(ClassMapping.class.getMethod("addContainerMapping", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setRequired(true);
        desc.setMultivalued(true);
        elements[6] = desc;
        
        bvr = new BasicValidationRule("container");
        bvr.setMinOccurs(0);
        gvr.addValidationRule(bvr);
    } //-- ClassMappingMarshalInfo()


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
        return Object.class;
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
