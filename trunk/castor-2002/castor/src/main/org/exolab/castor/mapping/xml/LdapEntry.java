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
public class LdapEntry implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/


    /**
     * 
    **/
    private java.lang.String vObjectClass;


      //----------------/
     //- Constructors -/
    //----------------/

    public LdapEntry() {
        super();
    } //-- LdapEntry()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getObjectClass() {
        return this.vObjectClass;
    } //-- java.lang.String getObjectClass() 

    /**
     * @param vObjectClass
    **/
    public void setObjectClass(java.lang.String vObjectClass) {
        this.vObjectClass = vObjectClass;
    } //-- void setObjectClass(java.lang.String) 

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
    public static LdapEntry unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (LdapEntry) Unmarshaller.unmarshal(LdapEntry.class, reader);
    } //-- LdapEntry unmarshal(java.io.Reader) 

}
