/*
 * Add code header here
 * $Id$ 
 */

package xml;

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
public class ProductGroup implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private java.lang.String vId;

    /**
     * 
    **/
    private java.lang.String vName;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProductGroup() {
    } //-- ProductGroup()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
    **/
    public void validate() 
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator.validate(this, null);
    } //-- void validate() 

    /**
     * 
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
    **/
    public java.lang.String getId() {
        return this.vId;
    } //-- java.lang.String getId() 

    /**
     * 
     * @param vId 
    **/
    public void setId(java.lang.String vId) {
        this.vId = vId;
    } //-- void setId(java.lang.String) 

    /**
     * 
    **/
    public java.lang.String getReferenceId() {
        return this.vId;
    } //-- java.lang.String getReferenceId() 

    /**
     * 
    **/
    public java.lang.String getName() {
        return this.vName;
    } //-- java.lang.String getName() 

    /**
     * 
     * @param vName 
    **/
    public void setName(java.lang.String vName) {
        this.vName = vName;
    } //-- void setName(java.lang.String) 

}


