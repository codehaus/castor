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

/**
 * @author 
 * @version $Revision$ $Date$
**/
public class Param implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private String vValue;

    /**
     * 
    **/
    private String vName;


      //----------------/
     //- Constructors -/
    //----------------/

    public Param() {
    } //-- Param()


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
    public String getValue() {
        return this.vValue;
    } //-- String getValue() 

    /**
     * 
     * @param vValue 
    **/
    public void setValue(String vValue) {
        this.vValue = vValue;
    } //-- void setValue(String) 

    /**
     * 
    **/
    public String getName() {
        return this.vName;
    } //-- String getName() 

    /**
     * 
     * @param vName 
    **/
    public void setName(String vName) {
        this.vName = vName;
    } //-- void setName(String) 


}
