/*
 * Add code header here
 * $Id$ 
 */

package org.exolab.castor.mapping.xml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * @version $Revision$ $Date$
**/
public class ContainerMapping implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private java.lang.String vSetMethod;

    /**
     * 
    **/
    private java.lang.String vGetMethod;

    /**
     * 
    **/
    private java.lang.String vType;

    /**
     * 
    **/
    private boolean vRequired;

    /**
     * 
    **/
    private java.lang.String vName;

    /**
     * 
    **/
    private java.lang.String vDescription;

    /**
     * 
    **/
    private java.util.Vector vFieldList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ContainerMapping() {
        super();
        vFieldList = new Vector();
    } //-- ContainerMapping()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getSetMethod() {
        return this.vSetMethod;
    } //-- java.lang.String getSetMethod() 

    /**
     * @param vSetMethod
    **/
    public void setSetMethod(java.lang.String vSetMethod) {
        this.vSetMethod = vSetMethod;
    } //-- void setSetMethod(java.lang.String) 

    /**
    **/
    public java.lang.String getGetMethod() {
        return this.vGetMethod;
    } //-- java.lang.String getGetMethod() 

    /**
     * @param vGetMethod
    **/
    public void setGetMethod(java.lang.String vGetMethod) {
        this.vGetMethod = vGetMethod;
    } //-- void setGetMethod(java.lang.String) 

    /**
    **/
    public java.lang.String getType() {
        return this.vType;
    } //-- java.lang.String getType() 

    /**
     * @param vType
    **/
    public void setType(java.lang.String vType) {
        this.vType = vType;
    } //-- void setType(java.lang.String) 

    /**
    **/
    public boolean getRequired() {
        return this.vRequired;
    } //-- boolean getRequired() 

    /**
     * @param vRequired
    **/
    public void setRequired(boolean vRequired) {
        this.vRequired = vRequired;
    } //-- void setRequired(boolean) 

    /**
    **/
    public java.lang.String getName() {
        return this.vName;
    } //-- java.lang.String getName() 

    /**
     * @param vName
    **/
    public void setName(java.lang.String vName) {
        this.vName = vName;
    } //-- void setName(java.lang.String) 

    /**
    **/
    public java.lang.String getDescription() {
        return this.vDescription;
    } //-- java.lang.String getDescription() 

    /**
     * @param vDescription
    **/
    public void setDescription(java.lang.String vDescription) {
        this.vDescription = vDescription;
    } //-- void setDescription(java.lang.String) 

    /**
     * @param vFieldMapping
    **/
    public void addFieldMapping(FieldMapping vFieldMapping) 
        throws java.lang.IndexOutOfBoundsException
    {
        vFieldList.addElement(vFieldMapping);
    } //-- void addFieldMapping(FieldMapping) 

    /**
     * @param index
    **/
    public FieldMapping getFieldMapping(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vFieldList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (FieldMapping) vFieldList.elementAt(index);
    } //-- FieldMapping getFieldMapping(int) 

    /**
    **/
    public FieldMapping[] getFieldMapping() {
        int size = vFieldList.size();
        FieldMapping[] mArray = new FieldMapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (FieldMapping) vFieldList.elementAt(index);
        }
        return mArray;
    } //-- FieldMapping[] getFieldMapping() 

    /**
     * @param vFieldMapping
     * @param index
    **/
    public void setFieldMapping(FieldMapping vFieldMapping, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vFieldList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vFieldList.setElementAt(vFieldMapping, index);
    } //-- void setFieldMapping(FieldMapping, int) 

    /**
    **/
    public int getFieldMappingCount() {
        return vFieldList.size();
    } //-- int getFieldMappingCount() 

    /**
    **/
    public java.util.Enumeration enumerateFieldMapping() {
        return vFieldList.elements();
    } //-- java.util.Enumeration enumerateFieldMapping() 

    /**
     * @param index
    **/
    public FieldMapping removeFieldMapping(int index) {
        Object obj = vFieldList.elementAt(index);
        vFieldList.removeElementAt(index);
        return (FieldMapping) obj;
    } //-- FieldMapping removeFieldMapping(int) 

    /**
    **/
    public void removeAllFieldMapping() {
        vFieldList.removeAllElements();
    } //-- void removeAllFieldMapping() 

    /**
    **/
    public void validate() 
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator.validate(this, null);
    } //-- void validate() 

    /**
    **/
    public boolean isValid() {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * @param out
    **/
    public void marshal(java.io.Writer out) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        //-- we must have a valid element before marshalling
        //validate(false);
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        //-- we must have a valid element before marshalling
        //validate(false);
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
     * @param reader
    **/
    public static ContainerMapping unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (ContainerMapping) Unmarshaller.unmarshal(ContainerMapping.class, reader);
    } //-- ContainerMapping unmarshal(java.io.Reader) 

}
