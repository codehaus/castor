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
public class SqlInfoMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public SqlInfoMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        gvr = new GroupValidationRule();
        BasicValidationRule bvr = null;
        rules = new ValidationRule[6];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[6];
        //-- vAccess
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vAccess", "access");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(SqlInfo.class.getMethod("getAccess", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(SqlInfo.class.getMethod("setAccess", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        bvr = new BasicValidationRule("access");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        rules[0] = bvr;
        //-- vManyTable
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vManyTable", "many-table");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(SqlInfo.class.getMethod("getManyTable", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(SqlInfo.class.getMethod("setManyTable", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[1] = desc;
        
        bvr = new BasicValidationRule("many-table");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[1] = bvr;
        //-- vManyKey
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vManyKey", "many-key");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(SqlInfo.class.getMethod("getManyKey", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(SqlInfo.class.getMethod("setManyKey", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[2] = desc;
        
        bvr = new BasicValidationRule("many-key");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[1] = bvr;
        //-- vDirty
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vDirty", "dirty");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(SqlInfo.class.getMethod("getDirty", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(SqlInfo.class.getMethod("setDirty", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[3] = desc;
        
        bvr = new BasicValidationRule("dirty");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        rules[2] = bvr;
        //-- vType
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vType", "type");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(SqlInfo.class.getMethod("getType", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(SqlInfo.class.getMethod("setType", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[4] = desc;
        
        bvr = new BasicValidationRule("type");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[3] = bvr;
        //-- vName
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vName", "name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(SqlInfo.class.getMethod("getName", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(SqlInfo.class.getMethod("setName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[5] = desc;
        
        bvr = new BasicValidationRule("name");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[4] = bvr;
        rules[5] = gvr;
        //-- initialize elements
        
        elements = new MarshalDescriptor[0];
    } //-- SqlInfoMarshalInfo()


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
        return SqlInfo.class;
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
