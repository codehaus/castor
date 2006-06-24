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
 * Class Persistence.
 * 
 * @version $Revision$ $Date$
 */
public class Persistence implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _version
     */
    private java.lang.String _version = "1.0";

    /**
     * Field _persistenceUnitList
     */
    private java.util.ArrayList _persistenceUnitList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Persistence() 
     {
        super();
        setVersion("1.0");
        _persistenceUnitList = new java.util.ArrayList();
    } //-- org.castor.jpa.persistence.xml.Persistence()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPersistenceUnit
     * 
     * 
     * 
     * @param vPersistenceUnit
     */
    public void addPersistenceUnit(org.castor.jpa.persistence.xml.PersistenceUnit vPersistenceUnit)
        throws java.lang.IndexOutOfBoundsException
    {
        _persistenceUnitList.add(vPersistenceUnit);
    } //-- void addPersistenceUnit(org.castor.jpa.persistence.xml.PersistenceUnit) 

    /**
     * Method addPersistenceUnit
     * 
     * 
     * 
     * @param index
     * @param vPersistenceUnit
     */
    public void addPersistenceUnit(int index, org.castor.jpa.persistence.xml.PersistenceUnit vPersistenceUnit)
        throws java.lang.IndexOutOfBoundsException
    {
        _persistenceUnitList.add(index, vPersistenceUnit);
    } //-- void addPersistenceUnit(int, org.castor.jpa.persistence.xml.PersistenceUnit) 

    /**
     * Method clearPersistenceUnit
     * 
     */
    public void clearPersistenceUnit()
    {
        _persistenceUnitList.clear();
    } //-- void clearPersistenceUnit() 

    /**
     * Method enumeratePersistenceUnit
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumeratePersistenceUnit()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_persistenceUnitList.iterator());
    } //-- java.util.Enumeration enumeratePersistenceUnit() 

    /**
     * Method getPersistenceUnit
     * 
     * 
     * 
     * @param index
     * @return PersistenceUnit
     */
    public org.castor.jpa.persistence.xml.PersistenceUnit getPersistenceUnit(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _persistenceUnitList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.castor.jpa.persistence.xml.PersistenceUnit) _persistenceUnitList.get(index);
    } //-- org.castor.jpa.persistence.xml.PersistenceUnit getPersistenceUnit(int) 

    /**
     * Method getPersistenceUnit
     * 
     * 
     * 
     * @return PersistenceUnit
     */
    public org.castor.jpa.persistence.xml.PersistenceUnit[] getPersistenceUnit()
    {
        int size = _persistenceUnitList.size();
        org.castor.jpa.persistence.xml.PersistenceUnit[] mArray = new org.castor.jpa.persistence.xml.PersistenceUnit[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.castor.jpa.persistence.xml.PersistenceUnit) _persistenceUnitList.get(index);
        }
        return mArray;
    } //-- org.castor.jpa.persistence.xml.PersistenceUnit[] getPersistenceUnit() 

    /**
     * Method getPersistenceUnitCount
     * 
     * 
     * 
     * @return int
     */
    public int getPersistenceUnitCount()
    {
        return _persistenceUnitList.size();
    } //-- int getPersistenceUnitCount() 

    /**
     * Returns the value of field 'version'.
     * 
     * @return String
     * @return the value of field 'version'.
     */
    public java.lang.String getVersion()
    {
        return this._version;
    } //-- java.lang.String getVersion() 

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
     * Method removePersistenceUnit
     * 
     * 
     * 
     * @param vPersistenceUnit
     * @return boolean
     */
    public boolean removePersistenceUnit(org.castor.jpa.persistence.xml.PersistenceUnit vPersistenceUnit)
    {
        boolean removed = _persistenceUnitList.remove(vPersistenceUnit);
        return removed;
    } //-- boolean removePersistenceUnit(org.castor.jpa.persistence.xml.PersistenceUnit) 

    /**
     * Method setPersistenceUnit
     * 
     * 
     * 
     * @param index
     * @param vPersistenceUnit
     */
    public void setPersistenceUnit(int index, org.castor.jpa.persistence.xml.PersistenceUnit vPersistenceUnit)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _persistenceUnitList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _persistenceUnitList.set(index, vPersistenceUnit);
    } //-- void setPersistenceUnit(int, org.castor.jpa.persistence.xml.PersistenceUnit) 

    /**
     * Method setPersistenceUnit
     * 
     * 
     * 
     * @param persistenceUnitArray
     */
    public void setPersistenceUnit(org.castor.jpa.persistence.xml.PersistenceUnit[] persistenceUnitArray)
    {
        //-- copy array
        _persistenceUnitList.clear();
        for (int i = 0; i < persistenceUnitArray.length; i++) {
            _persistenceUnitList.add(persistenceUnitArray[i]);
        }
    } //-- void setPersistenceUnit(org.castor.jpa.persistence.xml.PersistenceUnit) 

    /**
     * Sets the value of field 'version'.
     * 
     * @param version the value of field 'version'.
     */
    public void setVersion(java.lang.String version)
    {
        this._version = version;
    } //-- void setVersion(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Persistence
     */
    public static org.castor.jpa.persistence.xml.Persistence unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.castor.jpa.persistence.xml.Persistence) Unmarshaller.unmarshal(org.castor.jpa.persistence.xml.Persistence.class, reader);
    } //-- org.castor.jpa.persistence.xml.Persistence unmarshal(java.io.Reader) 

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
