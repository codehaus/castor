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
//import javax.sql.DataSource;

/**
 * @author 
 * @version $Revision$ $Date$
**/
public class DataSourceMarshalInfo implements org.exolab.castor.xml.MarshalInfo {


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

    public DataSourceMarshalInfo() {
        SimpleMarshalDescriptor desc = null;
        Class[] emptyClassArgs = new Class[0];
        Class[] classArgs = new Class[1];
        //-- initialize attributes
        
        attributes = new MarshalDescriptor[1];
        //-- vClassName
        desc = new SimpleMarshalDescriptor(String.class, "vClassName", "class-name");
        desc.setDescriptorType(DescriptorType.attribute);
        try {
            desc.setReadMethod(DataSource.class.getMethod("getClassName", emptyClassArgs));
            classArgs[0] = String.class;
            desc.setWriteMethod(DataSource.class.getMethod("setClassName", classArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        attributes[0] = desc;
        
        //-- initialize elements
        
        elements = new MarshalDescriptor[1];
        //-- vParams
        desc = new SimpleMarshalDescriptor(javax.sql.DataSource.class, "vParams", "params");
        desc.setDescriptorType(DescriptorType.element);
        try {
            desc.setReadMethod(DataSource.class.getMethod("getParams", emptyClassArgs));
            classArgs[0] = javax.sql.DataSource.class;
            desc.setWriteMethod(DataSource.class.getMethod("setParams", classArgs));
            desc.setCreateMethod(DataSource.class.getMethod("createParams", emptyClassArgs));
        }
        catch(java.lang.NoSuchMethodException nsme) {};
        
        elements[0] = desc;
        
    } //-- DataSourceMarshalInfo()


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
        return DataSource.class;
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
