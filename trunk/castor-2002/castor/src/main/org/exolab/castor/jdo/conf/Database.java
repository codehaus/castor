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
public class Database implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private String vDbName;

    /**
     * 
    **/
    private Driver vDriver;

    /**
     * 
    **/
    private DataSource vDataSource;

    /**
     * 
    **/
    private DataSourceRef vDataSourceRef;

    /**
     * 
    **/
    private Vector vMappingList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Database() {
        vMappingList = new Vector();
    } //-- Database()


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
    public String getDbName() {
        return this.vDbName;
    } //-- String getDbName() 

    /**
     * 
     * @param vDbName 
    **/
    public void setDbName(String vDbName) {
        this.vDbName = vDbName;
    } //-- void setDbName(String) 

    /**
     * 
     * @param vDriver 
    **/
    public void setDriver(Driver vDriver) 
    {
        this.vDriver = vDriver;
    } //-- void setDriver(Driver) 

    /**
     * 
    **/
    public Driver getDriver() 
    {
        return vDriver;
    } //-- Driver getDriver() 


    /**
     * 
     * @param vDataSource 
    **/
    public void setDataSource(DataSource vDataSource) 
    {
        this.vDataSource = vDataSource;
    } //-- void setDataSource(DataSource) 

    /**
     * 
    **/
    public DataSource getDataSource() 
    {
        return vDataSource;
    } //-- DataSource getDataSource() 

    /**
     * 
     * @param vDataSourceRef
    **/
    public void setDataSourceRef(DataSourceRef vDataSourceRef) 
    {
        this.vDataSourceRef = vDataSourceRef;
    } //-- void setDataSourceRef(DataSourceRef) 

    /**
     * 
    **/
    public DataSourceRef getDataSourceRef() 
    {
        return vDataSourceRef;
    } //-- DataSourceRef getDataSourceRef() 

    /**
     * 
     * @param vMapping 
    **/
    public void addMapping(Mapping vMapping) 
        throws java.lang.IndexOutOfBoundsException
    {
        vMappingList.addElement(vMapping);
    } //-- void addMapping(Mapping) 

    /**
     * 
     * @param index 
    **/
    public Mapping getMapping(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vMappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Mapping) vMappingList.elementAt(index);
    } //-- Mapping getMapping(int) 

    /**
     * 
    **/
    public Mapping[] getMappings() {
        int size = vMappingList.size();
        Mapping[] mArray = new Mapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Mapping) vMappingList.elementAt(index);
        }
        return mArray;
    } //-- Mapping[] getMappings() 

    /**
     * 
     * @param vMapping 
     * @param index 
    **/
    public void setMapping(Mapping vMapping, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vMappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vMappingList.setElementAt(vMapping, index);
    } //-- void setMapping(Mapping, int) 

    /**
     * 
    **/
    public int getMappingCount() {
        return vMappingList.size();
    } //-- int getMappingCount() 

    /**
     * 
    **/
    public Enumeration listMappings() {
        return vMappingList.elements();
    } //-- Enumeration listMappings() 

}
