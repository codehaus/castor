/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.8 (20000324)</a>,
 * using an XML Schema.
 * $Id
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
 * 
 * @version $Revision$ $Date$
**/
public class XmlInfo implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    private java.lang.String _nodeType;

    private java.lang.String _type;

    private java.lang.String _name;


      //----------------/
     //- Constructors -/
    //----------------/

    public XmlInfo() {
        super();
    } //-- org.exolab.castor.mapping.xml.XmlInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getName() {
        return this._name;
    } //-- java.lang.String getName() 

    /**
    **/
    public java.lang.String getNodeType() {
        return this._nodeType;
    } //-- java.lang.String getNodeType() 

    /**
    **/
    public java.lang.String getType() {
        return this._type;
    } //-- java.lang.String getType() 

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
     * 
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
     * 
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
     * 
     * @param _name
    **/
    public void setName(java.lang.String _name) {
        this._name = _name;
    } //-- void setName(java.lang.String) 

    /**
     * 
     * @param _nodeType
    **/
    public void setNodeType(java.lang.String _nodeType) {
        this._nodeType = _nodeType;
    } //-- void setNodeType(java.lang.String) 

    /**
     * 
     * @param _type
    **/
    public void setType(java.lang.String _type) {
        this._type = _type;
    } //-- void setType(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static org.exolab.castor.mapping.xml.XmlInfo unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.exolab.castor.mapping.xml.XmlInfo) Unmarshaller.unmarshal(org.exolab.castor.mapping.xml.XmlInfo.class, reader);
    } //-- org.exolab.castor.mapping.xml.XmlInfo unmarshal(java.io.Reader) 

    /**
    **/
    public void validate() 
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator.validate(this, null);
    } //-- void validate() 

}
