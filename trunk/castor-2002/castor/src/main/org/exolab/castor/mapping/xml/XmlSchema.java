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
public class XmlSchema implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private java.lang.String vPrefix;

    /**
     * 
    **/
    private java.lang.String vSystemId;

    /**
     * 
    **/
    private java.lang.String vPublicId;

    /**
     * 
    **/
    private java.lang.String vNamespace;

    /**
     * 
    **/
    private java.lang.String vXmlName;

      //----------------/
     //- Constructors -/
    //----------------/

    public XmlSchema() {
        super();
    } //-- XmlSchema()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getPrefix() {
        return this.vPrefix;
    } //-- java.lang.String getPrefix() 

    /**
     * @param vPrefix
    **/
    public void setPrefix(java.lang.String vPrefix) {
        this.vPrefix = vPrefix;
    } //-- void setPrefix(java.lang.String) 

    /**
    **/
    public java.lang.String getXmlName() {
        return this.vXmlName;
    } //-- java.lang.String getXmlName() 

    /**
     * @param vXmlName
    **/
    public void setXmlName(java.lang.String vXmlName) {
        this.vXmlName = vXmlName;
    } //-- void setXmlName(java.lang.String) 


    /**
    **/
    public java.lang.String getSystemId() {
        return this.vSystemId;
    } //-- java.lang.String getSystemId() 

    /**
     * @param vSystemId
    **/
    public void setSystemId(java.lang.String vSystemId) {
        this.vSystemId = vSystemId;
    } //-- void setSystemId(java.lang.String) 

    /**
    **/
    public java.lang.String getPublicId() {
        return this.vPublicId;
    } //-- java.lang.String getPublicId() 

    /**
     * @param vPublicId
    **/
    public void setPublicId(java.lang.String vPublicId) {
        this.vPublicId = vPublicId;
    } //-- void setPublicId(java.lang.String) 

    /**
    **/
    public java.lang.String getNamespace() {
        return this.vNamespace;
    } //-- java.lang.String getNamespace() 

    /**
     * @param vNamespace
    **/
    public void setNamespace(java.lang.String vNamespace) {
        this.vNamespace = vNamespace;
    } //-- void setNamespace(java.lang.String) 

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
    public static XmlSchema unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (XmlSchema) Unmarshaller.unmarshal(XmlSchema.class, reader);
    } //-- XmlSchema unmarshal(java.io.Reader) 

}
