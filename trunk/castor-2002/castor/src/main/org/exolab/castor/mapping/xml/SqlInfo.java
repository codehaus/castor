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
public class SqlInfo implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private java.lang.String vAccess;

    /**
     * 
    **/
    private java.lang.String vForKey;

    /**
     * 
    **/
    private java.lang.String vDirty;

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

    public SqlInfo() {
        super();
    } //-- SqlInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getAccess() {
        return this.vAccess;
    } //-- java.lang.String getAccess() 

    /**
     * @param vAccess
    **/
    public void setAccess(java.lang.String vAccess) {
        this.vAccess = vAccess;
    } //-- void setAccess(java.lang.String) 

    /**
    **/
    public java.lang.String getForKey() {
        return this.vForKey;
    } //-- java.lang.String getForKey() 

    /**
     * @param vForKey
    **/
    public void setForKey(java.lang.String vForKey) {
        this.vForKey = vForKey;
    } //-- void setForKey(java.lang.String) 

    /**
    **/
    public java.lang.String getDirty() {
        return this.vDirty;
    } //-- java.lang.String getDirty() 

    /**
     * @param vDirty
    **/
    public void setDirty(java.lang.String vDirty) {
        this.vDirty = vDirty;
    } //-- void setDirty(java.lang.String) 

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
    public static SqlInfo unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (SqlInfo) Unmarshaller.unmarshal(SqlInfo.class, reader);
    } //-- SqlInfo unmarshal(java.io.Reader) 

}
