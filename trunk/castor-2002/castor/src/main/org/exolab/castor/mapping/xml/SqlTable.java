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
public class SqlTable implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    private java.lang.String _tableName;


      //----------------/
     //- Constructors -/
    //----------------/

    public SqlTable() {
        super();
    } //-- org.exolab.castor.mapping.xml.SqlTable()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getTableName() {
        return this._tableName;
    } //-- java.lang.String getTableName() 

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
     * @param _tableName
    **/
    public void setTableName(java.lang.String _tableName) {
        this._tableName = _tableName;
    } //-- void setTableName(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static org.exolab.castor.mapping.xml.SqlTable unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.exolab.castor.mapping.xml.SqlTable) Unmarshaller.unmarshal(org.exolab.castor.mapping.xml.SqlTable.class, reader);
    } //-- org.exolab.castor.mapping.xml.SqlTable unmarshal(java.io.Reader) 

    /**
    **/
    public void validate() 
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator.validate(this, null);
    } //-- void validate() 

}
