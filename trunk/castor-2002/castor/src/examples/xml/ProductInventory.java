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
public class ProductInventory implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private int vQuantity;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProductInventory() {
    } //-- ProductInventory()


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
    public int getQuantity() {
        return this.vQuantity;
    } //-- int getQuantity() 

    /**
     * 
     * @param vQuantity 
    **/
    public void setQuantity(int vQuantity) {
        this.vQuantity = vQuantity;
    } //-- void setQuantity(int) 


}
