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
public class XmlSchemaMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public XmlSchemaMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        gvr = new GroupValidationRule();
        BasicValidationRule bvr = null;
        rules = new ValidationRule[5];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[5];
        //-- vPrefix
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vPrefix", "prefix");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(XmlSchema.class.getMethod("getPrefix", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(XmlSchema.class.getMethod("setPrefix", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        bvr = new BasicValidationRule("prefix");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[0] = bvr;
        //-- vSystemId
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vSystemId", "system-id");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(XmlSchema.class.getMethod("getSystemId", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(XmlSchema.class.getMethod("setSystemId", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[1] = desc;
        
        bvr = new BasicValidationRule("system-id");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        rules[1] = bvr;
        //-- vPublicId
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vPublicId", "public-id");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(XmlSchema.class.getMethod("getPublicId", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(XmlSchema.class.getMethod("setPublicId", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[2] = desc;
        
        bvr = new BasicValidationRule("public-id");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        rules[2] = bvr;
        //-- vNamespace
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vNamespace", "namespace");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(XmlSchema.class.getMethod("getNamespace", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(XmlSchema.class.getMethod("setNamespace", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[3] = desc;
        
        bvr = new BasicValidationRule("namespace");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new StringValidator());
        rules[3] = bvr;
        rules[4] = gvr;
        //-- vXmlName
        desc = new SimpleMarshalDescriptor(java.lang.String.class, "vXmlName", "xml-name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(XmlSchema.class.getMethod("getXmlName", emptyClassArgs));
            classArgs[0] = java.lang.String.class;
            desc.setWriteMethod(XmlSchema.class.getMethod("setXmlName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[4] = desc;
        
        bvr = new BasicValidationRule("xml-name");
        bvr.setAsAttributeRule();
        bvr.setMaxOccurs(1);
        bvr.setTypeValidator(new NameValidator(NameValidator.NMTOKEN));
        rules[0] = bvr;
        //-- initialize elements
        
        elements = new MarshalDescriptor[0];
    } //-- XmlSchemaMarshalInfo()


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
        return XmlSchema.class;
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
