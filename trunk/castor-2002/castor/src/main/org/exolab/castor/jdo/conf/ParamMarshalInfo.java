/*
 * Add code header here
 * $Id$ 
 */

package org.exolab.castor.jdo.conf;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.lang.reflect.Method;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalDescriptor;
import org.exolab.castor.xml.SimpleMarshalDescriptor;

/**
 * @author <a href="http://castor.exolab.org">Castor-XML</a>
 * @version $Revision$ $Date$
**/
public class ParamMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private MarshalDescriptor[] elements;

    /**
     * 
    **/
    private MarshalDescriptor[] attributes;

    /**
     * 
    **/
    private SimpleMarshalDescriptor contentDesc;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParamMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[2];
        //-- vValue
        desc = new SimpleMarshalDescriptor(String.class, "vValue", "value");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(Param.class.getMethod("getValue", emptyClassArgs));
            classArgs[0] = String.class;
            desc.setWriteMethod(Param.class.getMethod("setValue", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        //-- vName
        desc = new SimpleMarshalDescriptor(String.class, "vName", "name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(Param.class.getMethod("getName", emptyClassArgs));
            classArgs[0] = String.class;
            desc.setWriteMethod(Param.class.getMethod("setName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[1] = desc;
        
        //-- initialize elements
        
        elements = new MarshalDescriptor[0];
    } //-- ParamMarshalInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
    **/
    public MarshalDescriptor[] getAttributeDescriptors() {
        return attributes;
    } //-- MarshalDescriptor[] getAttributeDescriptors() 

    /**
     * 
    **/
    public Class getClassType() {
        return Param.class;
    } //-- Class getClassType() 

    /**
     * 
    **/
    public MarshalDescriptor[] getElementDescriptors() {
        return elements;
    } //-- MarshalDescriptor[] getElementDescriptors() 

    /**
     * 
    **/
    public Method getValidateMethod() {
        return null;
    } //-- Method getValidateMethod() 

    /**
     * 
    **/
    public MarshalDescriptor getContentDescriptor() {
        return contentDesc;
    } //-- MarshalDescriptor getContentDescriptor() 

    /**
     * 
    **/
    public String getNameSpacePrefix() {
        return null;
    } //-- String getNameSpacePrefix() 

    /**
     * 
    **/
    public String getNameSpaceURI() {
        return null;
    } //-- String getNameSpaceURI() 

    public org.exolab.castor.xml.ValidationRule[] getValidationRules()
    {
        return null;
    }

}
