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
    private java.util.Vector vObjectList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Mapping() {
        super();
        vIncludeList = new Vector();
        vObjectList = new Vector();
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
    public Include[] getInclude() {
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
    public java.util.Enumeration enumerateInclude() {
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
     * @param vObjectMapping
    **/
    public void addObjectMapping(ObjectMapping vObjectMapping) 
        throws java.lang.IndexOutOfBoundsException
    {
        vObjectList.addElement(vObjectMapping);
    } //-- void addObjectMapping(ObjectMapping) 

    /**
     * @param index
    **/
    public ObjectMapping getObjectMapping(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vObjectList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ObjectMapping) vObjectList.elementAt(index);
    } //-- ObjectMapping getObjectMapping(int) 

    /**
    **/
    public ObjectMapping[] getObjectMapping() {
        int size = vObjectList.size();
        ObjectMapping[] mArray = new ObjectMapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ObjectMapping) vObjectList.elementAt(index);
        }
        return mArray;
    } //-- ObjectMapping[] getObjectMapping() 

    /**
     * @param vObjectMapping
     * @param index
    **/
    public void setObjectMapping(ObjectMapping vObjectMapping, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vObjectList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vObjectList.setElementAt(vObjectMapping, index);
    } //-- void setObject(ObjectMapping, int) 

    /**
    **/
    public int getObjectMappingCount() {
        return vObjectList.size();
    } //-- int getObjectMappingCount() 

    /**
    **/
    public java.util.Enumeration enumerateObjectMapping() {
        return vObjectList.elements();
    } //-- java.util.Enumeration enumerateObjectMapping() 

    /**
     * @param index
    **/
    public ObjectMapping removeObjectMapping(int index) {
        Object obj = vObjectList.elementAt(index);
        vObjectList.removeElementAt(index);
        return (ObjectMapping) obj;
    } //-- ObjectMapping removeObjectMapping(int) 

    /**
    **/
    public void removeAllObjectMapping() {
        vObjectList.removeAllElements();
    } //-- void removeAllObjectMapping() 

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
