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
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * @version $Revision$ $Date$
**/
public class Mapping implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private java.lang.String vDescription;

    /**
     * 
    **/
    private java.util.Vector vIncludeList;

    /**
     * 
    **/
    private java.util.Vector vClassList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Mapping() {
        super();
        vIncludeList = new Vector();
        vClassList = new Vector();
    } //-- Mapping()


      //-----------/
     //- Methods -/
    //-----------/

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
     * @param vInclude
    **/
    public void addInclude(Include vInclude) 
        throws java.lang.IndexOutOfBoundsException
    {
        vIncludeList.addElement(vInclude);
    } //-- void addInclude(Include) 

    /**
     * @param index
    **/
    public Include getInclude(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vIncludeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Include) vIncludeList.elementAt(index);
    } //-- Include getInclude(int) 

    /**
    **/
    public Include[] getIncludes() {
        int size = vIncludeList.size();
        Include[] mArray = new Include[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Include) vIncludeList.elementAt(index);
        }
        return mArray;
    } //-- Include[] getInclude() 

    /**
     * @param vInclude
     * @param index
    **/
    public void setInclude(Include vInclude, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vIncludeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vIncludeList.setElementAt(vInclude, index);
    } //-- void setInclude(Include, int) 

    /**
    **/
    public int getIncludeCount() {
        return vIncludeList.size();
    } //-- int getIncludeCount() 

    /**
    **/
    public java.util.Enumeration enumerateIncludes() {
        return vIncludeList.elements();
    } //-- java.util.Enumeration enumerateInclude() 

    /**
     * @param index
    **/
    public Include removeInclude(int index) {
        Object obj = vIncludeList.elementAt(index);
        vIncludeList.removeElementAt(index);
        return (Include) obj;
    } //-- Include removeInclude(int) 

    /**
    **/
    public void removeAllInclude() {
        vIncludeList.removeAllElements();
    } //-- void removeAllInclude() 

    /**
     * @param vClassMapping
    **/
    public void addClassMapping(ClassMapping vClassMapping) 
        throws java.lang.IndexOutOfBoundsException
    {
        vClassList.addElement(vClassMapping);
    } //-- void addClassMapping(ClassMapping) 

    /**
     * @param index
    **/
    public ClassMapping getClassMapping(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vClassList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ClassMapping) vClassList.elementAt(index);
    } //-- ClassMapping getClassMapping(int) 

    /**
    **/
    public ClassMapping[] getClassMapping() {
        int size = vClassList.size();
        ClassMapping[] mArray = new ClassMapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ClassMapping) vClassList.elementAt(index);
        }
        return mArray;
    } //-- ClassMapping[] getClassMapping() 

    /**
     * @param vClassMapping
     * @param index
    **/
    public void setClassMapping(ClassMapping vClassMapping, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vClassList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vClassList.setElementAt(vClassMapping, index);
    } //-- void setClassMapping(ClassMapping, int) 

    /**
    **/
    public int getClassMappingCount() {
        return vClassList.size();
    } //-- int getClassMappingCount() 

    /**
    **/
    public java.util.Enumeration enumerateClassMapping() {
        return vClassList.elements();
    } //-- java.util.Enumeration enumerateClassMapping() 

    /**
     * @param index
    **/
    public ClassMapping removeClassMapping(int index) {
        Object obj = vClassList.elementAt(index);
        vClassList.removeElementAt(index);
        return (ClassMapping) obj;
    } //-- ClassMapping removeClassMapping(int) 

    /**
    **/
    public void removeAllClassMapping() {
        vClassList.removeAllElements();
    } //-- void removeAllClassMapping() 

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
    public static Mapping unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (Mapping) Unmarshaller.unmarshal(Mapping.class, reader);
    } //-- Mapping unmarshal(java.io.Reader) 

}
