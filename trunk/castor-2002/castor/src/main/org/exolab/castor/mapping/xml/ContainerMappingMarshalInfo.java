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
public class ContainerMappingMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public ContainerMappingMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        gvr = new GroupValidationRule();
        BasicValidationRule bvr = null;
        rules = new ValidationRule[6];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[5];
        //-- vSetMethod
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vSetMethod", "set-method");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ContainerMapping.class.getMethod("getSetMethod", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ContainerMapping.class.getMethod("setSetMethod", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        bvr = new BasicValidationRule("set-method");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[0] = bvr;
        //-- vGetMethod
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vGetMethod", "get-method");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ContainerMapping.class.getMethod("getGetMethod", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ContainerMapping.class.getMethod("setGetMethod", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[1] = desc;
        
        bvr = new BasicValidationRule("get-method");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[1] = bvr;
        //-- vType
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vType", "type");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ContainerMapping.class.getMethod("getType", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ContainerMapping.class.getMethod("setType", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[2] = desc;
        
        bvr = new BasicValidationRule("type");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[2] = bvr;
        //-- vRequired
        desc = new SimpleMarshalDescriptor(boolean.class, "vRequired", "required");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ContainerMapping.class.getMethod("getRequired", emptyClassArgs));
            classArgs[0] = boolean.class;
            desc.setWriteMethod(ContainerMapping.class.getMethod("setRequired", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[3] = desc;
        
        bvr = new BasicValidationRule("required");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        rules[3] = bvr;
        //-- vName
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vName", "name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(ContainerMapping.class.getMethod("getName", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ContainerMapping.class.getMethod("setName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[4] = desc;
        
        bvr = new BasicValidationRule("name");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[4] = bvr;
        rules[5] = gvr;
        //-- initialize elements
        
        elements = new MarshalDescriptor[2];
        //-- vDescription
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vDescription", "description");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ContainerMapping.class.getMethod("getDescription", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(ContainerMapping.class.getMethod("setDescription", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[0] = desc;
        
        bvr = new BasicValidationRule("description");
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        gvr.addValidationRule(bvr);
        //-- vFieldList
        desc = new SimpleMarshalDescriptor(FieldMapping.class, "vFieldList", "field");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(ContainerMapping.class.getMethod("getFieldMapping", emptyClassArgs));
            classArgs[0] = FieldMapping.class;
            desc.setWriteMethod(ContainerMapping.class.getMethod("addFieldMapping", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setRequired(true);
        desc.setMultivalued(true);
        elements[1] = desc;
        
        bvr = new BasicValidationRule("field");
        bvr.setMinOccurs(1);
        gvr.addValidationRule(bvr);
    } //-- ContainerMappingMarshalInfo()


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
        return ContainerMapping.class;
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
