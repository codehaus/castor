/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package org.castor.jpa.persistence.xml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * A list of vendor-specific properties.
 *  
 * 
 * @version $Revision$ $Date$
 */
public class Properties implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * A name-value pair.
     *  
     */
    private java.util.ArrayList _propertyList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Properties() 
     {
        super();
        _propertyList = new java.util.ArrayList();
    } //-- org.castor.jpa.persistence.xml.Properties()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addProperty
     * 
     * 
     * 
     * @param vProperty
     */
    public void addProperty(org.castor.jpa.persistence.xml.Property vProperty)
        throws java.lang.IndexOutOfBoundsException
    {
        _propertyList.add(vProperty);
    } //-- void addProperty(org.castor.jpa.persistence.xml.Property) 

    /**
     * Method addProperty
     * 
     * 
     * 
     * @param index
     * @param vProperty
     */
    public void addProperty(int index, org.castor.jpa.persistence.xml.Property vProperty)
        throws java.lang.IndexOutOfBoundsException
    {
        _propertyList.add(index, vProperty);
    } //-- void addProperty(int, org.castor.jpa.persistence.xml.Property) 

    /**
     * Method clearProperty
     * 
     */
    public void clearProperty()
    {
        _propertyList.clear();
    } //-- void clearProperty() 

    /**
     * Method enumerateProperty
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateProperty()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_propertyList.iterator());
    } //-- java.util.Enumeration enumerateProperty() 

    /**
     * Method getProperty
     * 
     * 
     * 
     * @param index
     * @return Property
     */
    public org.castor.jpa.persistence.xml.Property getProperty(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _propertyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.castor.jpa.persistence.xml.Property) _propertyList.get(index);
    } //-- org.castor.jpa.persistence.xml.Property getProperty(int) 

    /**
     * Method getProperty
     * 
     * 
     * 
     * @return Property
     */
    public org.castor.jpa.persistence.xml.Property[] getProperty()
    {
        int size = _propertyList.size();
        org.castor.jpa.persistence.xml.Property[] mArray = new org.castor.jpa.persistence.xml.Property[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.castor.jpa.persistence.xml.Property) _propertyList.get(index);
        }
        return mArray;
    } //-- org.castor.jpa.persistence.xml.Property[] getProperty() 

    /**
     * Method getPropertyCount
     * 
     * 
     * 
     * @return int
     */
    public int getPropertyCount()
    {
        return _propertyList.size();
    } //-- int getPropertyCount() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeProperty
     * 
     * 
     * 
     * @param vProperty
     * @return boolean
     */
    public boolean removeProperty(org.castor.jpa.persistence.xml.Property vProperty)
    {
        boolean removed = _propertyList.remove(vProperty);
        return removed;
    } //-- boolean removeProperty(org.castor.jpa.persistence.xml.Property) 

    /**
     * Method setProperty
     * 
     * 
     * 
     * @param index
     * @param vProperty
     */
    public void setProperty(int index, org.castor.jpa.persistence.xml.Property vProperty)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _propertyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _propertyList.set(index, vProperty);
    } //-- void setProperty(int, org.castor.jpa.persistence.xml.Property) 

    /**
     * Method setProperty
     * 
     * 
     * 
     * @param propertyArray
     */
    public void setProperty(org.castor.jpa.persistence.xml.Property[] propertyArray)
    {
        //-- copy array
        _propertyList.clear();
        for (int i = 0; i < propertyArray.length; i++) {
            _propertyList.add(propertyArray[i]);
        }
    } //-- void setProperty(org.castor.jpa.persistence.xml.Property) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Properties
     */
    public static org.castor.jpa.persistence.xml.Properties unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.castor.jpa.persistence.xml.Properties) Unmarshaller.unmarshal(org.castor.jpa.persistence.xml.Properties.class, reader);
    } //-- org.castor.jpa.persistence.xml.Properties unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
