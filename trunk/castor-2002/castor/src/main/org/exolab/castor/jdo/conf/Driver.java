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
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

/**
 * @author <a href="http://castor.exolab.org">Castor-XML</a>
 * @version $Revision$ $Date$
**/
public class Driver implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private String vUrl;

    /**
     * 
    **/
    private String vClassName;

    /**
     * 
    **/
    private Vector vParamList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Driver() {
        vParamList = new Vector();
    } //-- Driver()


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
        if (deep) {
            for (int i = 0; i < vParamList.size(); i++) {
                Param vParam = (Param) vParamList.elementAt(i);
                vParam.validate(true);
            }
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
    public String getUrl() {
        return this.vUrl;
    } //-- String getUrl() 

    /**
     * 
     * @param vUrl 
    **/
    public void setUrl(String vUrl) {
        this.vUrl = vUrl;
    } //-- void setUrl(String) 

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
     * @param vParam 
    **/
    public void addParam(Param vParam) 
        throws java.lang.IndexOutOfBoundsException
    {
        vParamList.addElement(vParam);
    } //-- void addParam(Param) 

    /**
     * 
     * @param index 
    **/
    public Param getParam(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vParamList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Param) vParamList.elementAt(index);
    } //-- Param getParam(int) 

    /**
     * 
    **/
    public Param[] getParams() {
        int size = vParamList.size();
        Param[] mArray = new Param[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Param) vParamList.elementAt(index);
        }
        return mArray;
    } //-- Param[] getParam() 

    /**
     * 
     * @param vParam 
     * @param index 
    **/
    public void setParam(Param vParam, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vParamList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vParamList.setElementAt(vParam, index);
    } //-- void setParam(Param, int) 

    /**
     * 
    **/
    public int getParamCount() {
        return vParamList.size();
    } //-- int getParamCount() 

    /**
     * 
    **/
    public Enumeration listParams() {
        return vParamList.elements();
    } //-- Enumeration enumerateParam() 


}
