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
public class FieldMapping implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private java.lang.String vCollection;

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
    private SqlInfo vSqlInfo;

    /**
     * 
    **/
    private XmlInfo vXmlInfo;

    /**
     * 
    **/
    private LdapInfo vLdapInfo;


      //----------------/
     //- Constructors -/
    //----------------/

    public FieldMapping() {
        super();
    } //-- FieldMapping()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getCollection() {
        return this.vCollection;
    } //-- java.lang.String getCollection() 

    /**
     * @param vCollection
    **/
    public void setCollection(java.lang.String vCollection) {
        this.vCollection = vCollection;
    } //-- void setCollection(java.lang.String) 

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
    **/
    public SqlInfo getSqlInfo() {
        return this.vSqlInfo;
    } //-- SqlInfo getSqlInfo() 

    /**
     * @param vSqlInfo
    **/
    public void setSqlInfo(SqlInfo vSqlInfo) {
        this.vSqlInfo = vSqlInfo;
    } //-- void setSqlInfo(SqlInfo) 

    /**
    **/
    public XmlInfo getXmlInfo() {
        return this.vXmlInfo;
    } //-- XmlInfo getXmlInfo() 

    /**
     * @param vXmlInfo
    **/
    public void setXmlInfo(XmlInfo vXmlInfo) {
        this.vXmlInfo = vXmlInfo;
    } //-- void setXmlInfo(XmlInfo) 

    /**
    **/
    public LdapInfo getLdapInfo() {
        return this.vLdapInfo;
    } //-- LdapInfo getLdapInfo() 

    /**
     * @param vLdapInfo
    **/
    public void setLdapInfo(LdapInfo vLdapInfo) {
        this.vLdapInfo = vLdapInfo;
    } //-- void setLdapInfo(LdapInfo) 

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
    public static FieldMapping unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (FieldMapping) Unmarshaller.unmarshal(FieldMapping.class, reader);
    } //-- FieldMapping unmarshal(java.io.Reader) 

}
