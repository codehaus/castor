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
public class DriverMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public DriverMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[2];
        //-- vUrl
        desc = new SimpleMarshalDescriptor(String.class, "vUrl", "url");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(Driver.class.getMethod("getUrl", emptyClassArgs));
            classArgs[0] = String.class;
            desc.setWriteMethod(Driver.class.getMethod("setUrl", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        //-- vClassName
        desc = new SimpleMarshalDescriptor(String.class, "vClassName", "class-name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(Driver.class.getMethod("getClassName", emptyClassArgs));
            classArgs[0] = String.class;
            desc.setWriteMethod(Driver.class.getMethod("setClassName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[1] = desc;

        //-- initialize elements
        
        elements = new MarshalDescriptor[1];
        //-- vParamList
        desc = new SimpleMarshalDescriptor(Param.class, "vParamList", "param");
        desc.setDescriptorType(DescriptorType.element);
        desc.setMultivalued( true );
        try {
            desc.setReadMethod(Driver.class.getMethod("getParams", emptyClassArgs));
            classArgs[0] = Param.class;
            desc.setWriteMethod(Driver.class.getMethod("addParam", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        elements[0] = desc;
        
    } //-- DriverMarshalInfo()


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
        return Driver.class;
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
