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
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * @version $Revision$ $Date$
**/
public class XmlInfo implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private java.lang.String vNodeType;

    /**
     * 
    **/
    private java.lang.String vType;

    /**
     * 
    **/
    private java.lang.String vName;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlInfo() {
        super();
    } //-- XmlInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getNodeType() {
        return this.vNodeType;
    } //-- java.lang.String getNodeType() 

    /**
     * @param vNodeType
    **/
    public void setNodeType(java.lang.String vNodeType) {
        this.vNodeType = vNodeType;
    } //-- void setNodeType(java.lang.String) 

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
    public static XmlInfo unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (XmlInfo) Unmarshaller.unmarshal(XmlInfo.class, reader);
    } //-- XmlInfo unmarshal(java.io.Reader) 

}
