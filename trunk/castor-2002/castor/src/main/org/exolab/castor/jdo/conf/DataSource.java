/*
 * Add code header here
 * $Id$ 
 */

package org.exolab.castor.jdo.conf;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
//import javax.sql.DataSource;

/**
 * @author 
 * @version $Revision$ $Date$
**/
public class DataSource implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private String vClassName;

    /**
     * 
    **/
    private javax.sql.DataSource vParams;


      //----------------/
     //- Constructors -/
    //----------------/

    public DataSource() {
    } //-- DataSource()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param deep 
    **/
    protected void validate(boolean deep) 
        throws org.exolab.castor.xml.ValidationException
    {
        
        //-- make sure vParams is not null
        if (this.vParams == null) {
            String err = "params is required in order to be valid.";
            throw new ValidationException(err);
        }
    } //-- void validate(boolean) 

    /**
     * 
     * @param deep 
    **/
    public boolean isValid(boolean deep) {
        try {
            validate(deep);
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid(boolean) 

    /**
     * 
    **/
    public String getClassName() {
        return this.vClassName;
    } //-- String getClassName() 

    /**
     * 
     * @param vClassName 
    **/
    public void setClassName(String vClassName) {
        this.vClassName = vClassName;
    } //-- void setClassName(String) 

    /**
     * 
    **/
    public javax.sql.DataSource getParams() {
        return this.vParams;
    } //-- javax.sql.DataSource getParams() 

    /**
     * 
     * @param vParams 
    **/
    public void setParams(javax.sql.DataSource vParams) {
        this.vParams = vParams;
    } //-- void setParams(javax.sql.DataSource) 


    public javax.sql.DataSource createParams()
        throws Exception
    {
        Object params;
        
        params = Class.forName( vClassName ).newInstance();
        if ( params instanceof javax.sql.DataSource )
            return (javax.sql.DataSource) params;
        else
            throw new Exception( "Data source class name does not extend javax.sql.DataSource" );
    }

}
