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
public class LdapEntryMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public LdapEntryMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        gvr = new GroupValidationRule();
        BasicValidationRule bvr = null;
        rules = new ValidationRule[3];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[1];
        //-- vObjectClass
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vObjectClass", "object-class");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(LdapEntry.class.getMethod("getObjectClass", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(LdapEntry.class.getMethod("setObjectClass", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        bvr = new BasicValidationRule("object-class");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[1] = bvr;
        rules[2] = gvr;
        //-- initialize elements
        
        elements = new MarshalDescriptor[0];
    } //-- LdapEntryMarshalInfo()


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
        return LdapEntry.class;
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
