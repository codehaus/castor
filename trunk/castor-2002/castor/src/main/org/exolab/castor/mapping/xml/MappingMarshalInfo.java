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
public class MappingMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public MappingMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        gvr = new GroupValidationRule();
        BasicValidationRule bvr = null;
        rules = new ValidationRule[1];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[0];
        rules[0] = gvr;
        //-- initialize elements
        
        elements = new MarshalDescriptor[3];
        //-- vDescription
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vDescription", "description");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(Mapping.class.getMethod("getDescription", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(Mapping.class.getMethod("setDescription", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(false);
        elements[0] = desc;
        
        bvr = new BasicValidationRule("description");
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        gvr.addValidationRule(bvr);
        //-- vIncludeList
        desc = new SimpleMarshalDescriptor(Include.class, "vIncludeList", "include");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(Mapping.class.getMethod("getInclude", emptyClassArgs));
            classArgs[0] = Include.class;
            desc.setWriteMethod(Mapping.class.getMethod("addInclude", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setMultivalued(true);
        elements[1] = desc;
        
        bvr = new BasicValidationRule("include");
        bvr.setMinOccurs(0);
        gvr.addValidationRule(bvr);
        //-- vClassList
        desc = new SimpleMarshalDescriptor(ClassMapping.class, "vClassList", "class");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(Mapping.class.getMethod("getClassMapping", emptyClassArgs));
            classArgs[0] = ClassMapping.class;
            desc.setWriteMethod(Mapping.class.getMethod("addClassMapping", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        desc.setRequired(true);
        desc.setMultivalued(true);
        elements[2] = desc;
        
        bvr = new BasicValidationRule("class");
        bvr.setMinOccurs(1);
        gvr.addValidationRule(bvr);
    } //-- MappingMarshalInfo()


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
        return Mapping.class;
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
