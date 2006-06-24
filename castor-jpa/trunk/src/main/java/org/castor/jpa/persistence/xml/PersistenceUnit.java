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
 * Class PersistenceUnit.
 * 
 * @version $Revision$ $Date$
 */
public class PersistenceUnit implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Name used in code to reference this persistence unit.
     *  
     */
    private java.lang.String _name;

    /**
     * Type of transactions used by EntityManagers from this
     *  persistence unit.
     *  
     */
    private org.castor.jpa.persistence.xml.types.PersistenceUnitTransactionType _transactionType;

    /**
     * Textual description of this persistence
     *  unit.
     *  
     */
    private java.lang.String _description;

    /**
     * Provider class that supplies
     *  EntityManagers for this persistence unit.
     *  
     */
    private java.lang.String _provider;

    /**
     * The container-specific name of the JTA
     *  datasource to use.
     *  
     */
    private java.lang.String _jtaDataSource;

    /**
     * The container-specific name of a non-JTA
     *  datasource to use.
     *  
     */
    private java.lang.String _nonJtaDataSource;

    /**
     * File containing mapping information.
     *  Loaded as a resource by the persistence
     *  provider.
     *  
     */
    private java.util.ArrayList _mappingFileList;

    /**
     * Jar file that should be scanned for
     *  entities. Not applicable to Java SE
     *  persistence units.
     *  
     */
    private java.util.ArrayList _jarFileList;

    /**
     * Class to scan for annotations. It should
     *  be annotated with either @Entity,
     *  @Embeddable or @MappedSuperclass.
     *  
     */
    private java.util.ArrayList _clazzList;

    /**
     * When set to true then only listed classes
     *  and jars will be scanned for persistent
     *  classes, otherwise the enclosing jar or
     *  directory will also be scanned. Not
     *  applicable to Java SE persistence units.
     *  
     */
    private boolean _excludeUnlistedClasses = false;

    /**
     * keeps track of state for field: _excludeUnlistedClasses
     */
    private boolean _has_excludeUnlistedClasses;

    /**
     * A list of vendor-specific properties.
     *  
     */
    private org.castor.jpa.persistence.xml.Properties _properties;


      //----------------/
     //- Constructors -/
    //----------------/

    public PersistenceUnit() 
     {
        super();
        _mappingFileList = new java.util.ArrayList();
        _jarFileList = new java.util.ArrayList();
        _clazzList = new java.util.ArrayList();
    } //-- org.castor.jpa.persistence.xml.PersistenceUnit()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addClazz
     * 
     * 
     * 
     * @param vClazz
     */
    public void addClazz(java.lang.String vClazz)
        throws java.lang.IndexOutOfBoundsException
    {
        _clazzList.add(vClazz);
    } //-- void addClazz(java.lang.String) 

    /**
     * Method addClazz
     * 
     * 
     * 
     * @param index
     * @param vClazz
     */
    public void addClazz(int index, java.lang.String vClazz)
        throws java.lang.IndexOutOfBoundsException
    {
        _clazzList.add(index, vClazz);
    } //-- void addClazz(int, java.lang.String) 

    /**
     * Method addJarFile
     * 
     * 
     * 
     * @param vJarFile
     */
    public void addJarFile(java.lang.String vJarFile)
        throws java.lang.IndexOutOfBoundsException
    {
        _jarFileList.add(vJarFile);
    } //-- void addJarFile(java.lang.String) 

    /**
     * Method addJarFile
     * 
     * 
     * 
     * @param index
     * @param vJarFile
     */
    public void addJarFile(int index, java.lang.String vJarFile)
        throws java.lang.IndexOutOfBoundsException
    {
        _jarFileList.add(index, vJarFile);
    } //-- void addJarFile(int, java.lang.String) 

    /**
     * Method addMappingFile
     * 
     * 
     * 
     * @param vMappingFile
     */
    public void addMappingFile(java.lang.String vMappingFile)
        throws java.lang.IndexOutOfBoundsException
    {
        _mappingFileList.add(vMappingFile);
    } //-- void addMappingFile(java.lang.String) 

    /**
     * Method addMappingFile
     * 
     * 
     * 
     * @param index
     * @param vMappingFile
     */
    public void addMappingFile(int index, java.lang.String vMappingFile)
        throws java.lang.IndexOutOfBoundsException
    {
        _mappingFileList.add(index, vMappingFile);
    } //-- void addMappingFile(int, java.lang.String) 

    /**
     * Method clearClazz
     * 
     */
    public void clearClazz()
    {
        _clazzList.clear();
    } //-- void clearClazz() 

    /**
     * Method clearJarFile
     * 
     */
    public void clearJarFile()
    {
        _jarFileList.clear();
    } //-- void clearJarFile() 

    /**
     * Method clearMappingFile
     * 
     */
    public void clearMappingFile()
    {
        _mappingFileList.clear();
    } //-- void clearMappingFile() 

    /**
     * Method deleteExcludeUnlistedClasses
     * 
     */
    public void deleteExcludeUnlistedClasses()
    {
        this._has_excludeUnlistedClasses= false;
    } //-- void deleteExcludeUnlistedClasses() 

    /**
     * Method enumerateClazz
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateClazz()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_clazzList.iterator());
    } //-- java.util.Enumeration enumerateClazz() 

    /**
     * Method enumerateJarFile
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateJarFile()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_jarFileList.iterator());
    } //-- java.util.Enumeration enumerateJarFile() 

    /**
     * Method enumerateMappingFile
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateMappingFile()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_mappingFileList.iterator());
    } //-- java.util.Enumeration enumerateMappingFile() 

    /**
     * Method getClazz
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String getClazz(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _clazzList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_clazzList.get(index);
    } //-- java.lang.String getClazz(int) 

    /**
     * Method getClazz
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String[] getClazz()
    {
        int size = _clazzList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_clazzList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getClazz() 

    /**
     * Method getClazzCount
     * 
     * 
     * 
     * @return int
     */
    public int getClazzCount()
    {
        return _clazzList.size();
    } //-- int getClazzCount() 

    /**
     * Returns the value of field 'description'. The field
     * 'description' has the following description: Textual
     * description of this persistence
     *  unit.
     *  
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'excludeUnlistedClasses'. The
     * field 'excludeUnlistedClasses' has the following
     * description: When set to true then only listed classes
     *  and jars will be scanned for persistent
     *  classes, otherwise the enclosing jar or
     *  directory will also be scanned. Not
     *  applicable to Java SE persistence units.
     *  
     * 
     * @return boolean
     * @return the value of field 'excludeUnlistedClasses'.
     */
    public boolean getExcludeUnlistedClasses()
    {
        return this._excludeUnlistedClasses;
    } //-- boolean getExcludeUnlistedClasses() 

    /**
     * Method getJarFile
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String getJarFile(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _jarFileList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_jarFileList.get(index);
    } //-- java.lang.String getJarFile(int) 

    /**
     * Method getJarFile
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String[] getJarFile()
    {
        int size = _jarFileList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_jarFileList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getJarFile() 

    /**
     * Method getJarFileCount
     * 
     * 
     * 
     * @return int
     */
    public int getJarFileCount()
    {
        return _jarFileList.size();
    } //-- int getJarFileCount() 

    /**
     * Returns the value of field 'jtaDataSource'. The field
     * 'jtaDataSource' has the following description: The
     * container-specific name of the JTA
     *  datasource to use.
     *  
     * 
     * @return String
     * @return the value of field 'jtaDataSource'.
     */
    public java.lang.String getJtaDataSource()
    {
        return this._jtaDataSource;
    } //-- java.lang.String getJtaDataSource() 

    /**
     * Method getMappingFile
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String getMappingFile(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _mappingFileList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_mappingFileList.get(index);
    } //-- java.lang.String getMappingFile(int) 

    /**
     * Method getMappingFile
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String[] getMappingFile()
    {
        int size = _mappingFileList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_mappingFileList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getMappingFile() 

    /**
     * Method getMappingFileCount
     * 
     * 
     * 
     * @return int
     */
    public int getMappingFileCount()
    {
        return _mappingFileList.size();
    } //-- int getMappingFileCount() 

    /**
     * Returns the value of field 'name'. The field 'name' has the
     * following description: Name used in code to reference this
     * persistence unit.
     *  
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'nonJtaDataSource'. The field
     * 'nonJtaDataSource' has the following description: The
     * container-specific name of a non-JTA
     *  datasource to use.
     *  
     * 
     * @return String
     * @return the value of field 'nonJtaDataSource'.
     */
    public java.lang.String getNonJtaDataSource()
    {
        return this._nonJtaDataSource;
    } //-- java.lang.String getNonJtaDataSource() 

    /**
     * Returns the value of field 'properties'. The field
     * 'properties' has the following description: A list of
     * vendor-specific properties.
     *  
     * 
     * @return Properties
     * @return the value of field 'properties'.
     */
    public org.castor.jpa.persistence.xml.Properties getProperties()
    {
        return this._properties;
    } //-- org.castor.jpa.persistence.xml.Properties getProperties() 

    /**
     * Returns the value of field 'provider'. The field 'provider'
     * has the following description: Provider class that supplies
     *  EntityManagers for this persistence unit.
     *  
     * 
     * @return String
     * @return the value of field 'provider'.
     */
    public java.lang.String getProvider()
    {
        return this._provider;
    } //-- java.lang.String getProvider() 

    /**
     * Returns the value of field 'transactionType'. The field
     * 'transactionType' has the following description: Type of
     * transactions used by EntityManagers from this
     *  persistence unit.
     *  
     * 
     * @return PersistenceUnitTransactionType
     * @return the value of field 'transactionType'.
     */
    public org.castor.jpa.persistence.xml.types.PersistenceUnitTransactionType getTransactionType()
    {
        return this._transactionType;
    } //-- org.castor.jpa.persistence.xml.types.PersistenceUnitTransactionType getTransactionType() 

    /**
     * Method hasExcludeUnlistedClasses
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasExcludeUnlistedClasses()
    {
        return this._has_excludeUnlistedClasses;
    } //-- boolean hasExcludeUnlistedClasses() 

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
     * Method removeClazz
     * 
     * 
     * 
     * @param vClazz
     * @return boolean
     */
    public boolean removeClazz(java.lang.String vClazz)
    {
        boolean removed = _clazzList.remove(vClazz);
        return removed;
    } //-- boolean removeClazz(java.lang.String) 

    /**
     * Method removeJarFile
     * 
     * 
     * 
     * @param vJarFile
     * @return boolean
     */
    public boolean removeJarFile(java.lang.String vJarFile)
    {
        boolean removed = _jarFileList.remove(vJarFile);
        return removed;
    } //-- boolean removeJarFile(java.lang.String) 

    /**
     * Method removeMappingFile
     * 
     * 
     * 
     * @param vMappingFile
     * @return boolean
     */
    public boolean removeMappingFile(java.lang.String vMappingFile)
    {
        boolean removed = _mappingFileList.remove(vMappingFile);
        return removed;
    } //-- boolean removeMappingFile(java.lang.String) 

    /**
     * Method setClazz
     * 
     * 
     * 
     * @param index
     * @param vClazz
     */
    public void setClazz(int index, java.lang.String vClazz)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _clazzList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _clazzList.set(index, vClazz);
    } //-- void setClazz(int, java.lang.String) 

    /**
     * Method setClazz
     * 
     * 
     * 
     * @param clazzArray
     */
    public void setClazz(java.lang.String[] clazzArray)
    {
        //-- copy array
        _clazzList.clear();
        for (int i = 0; i < clazzArray.length; i++) {
            _clazzList.add(clazzArray[i]);
        }
    } //-- void setClazz(java.lang.String) 

    /**
     * Sets the value of field 'description'. The field
     * 'description' has the following description: Textual
     * description of this persistence
     *  unit.
     *  
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'excludeUnlistedClasses'. The field
     * 'excludeUnlistedClasses' has the following description: When
     * set to true then only listed classes
     *  and jars will be scanned for persistent
     *  classes, otherwise the enclosing jar or
     *  directory will also be scanned. Not
     *  applicable to Java SE persistence units.
     *  
     * 
     * @param excludeUnlistedClasses the value of field
     * 'excludeUnlistedClasses'.
     */
    public void setExcludeUnlistedClasses(boolean excludeUnlistedClasses)
    {
        this._excludeUnlistedClasses = excludeUnlistedClasses;
        this._has_excludeUnlistedClasses = true;
    } //-- void setExcludeUnlistedClasses(boolean) 

    /**
     * Method setJarFile
     * 
     * 
     * 
     * @param index
     * @param vJarFile
     */
    public void setJarFile(int index, java.lang.String vJarFile)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _jarFileList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _jarFileList.set(index, vJarFile);
    } //-- void setJarFile(int, java.lang.String) 

    /**
     * Method setJarFile
     * 
     * 
     * 
     * @param jarFileArray
     */
    public void setJarFile(java.lang.String[] jarFileArray)
    {
        //-- copy array
        _jarFileList.clear();
        for (int i = 0; i < jarFileArray.length; i++) {
            _jarFileList.add(jarFileArray[i]);
        }
    } //-- void setJarFile(java.lang.String) 

    /**
     * Sets the value of field 'jtaDataSource'. The field
     * 'jtaDataSource' has the following description: The
     * container-specific name of the JTA
     *  datasource to use.
     *  
     * 
     * @param jtaDataSource the value of field 'jtaDataSource'.
     */
    public void setJtaDataSource(java.lang.String jtaDataSource)
    {
        this._jtaDataSource = jtaDataSource;
    } //-- void setJtaDataSource(java.lang.String) 

    /**
     * Method setMappingFile
     * 
     * 
     * 
     * @param index
     * @param vMappingFile
     */
    public void setMappingFile(int index, java.lang.String vMappingFile)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _mappingFileList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _mappingFileList.set(index, vMappingFile);
    } //-- void setMappingFile(int, java.lang.String) 

    /**
     * Method setMappingFile
     * 
     * 
     * 
     * @param mappingFileArray
     */
    public void setMappingFile(java.lang.String[] mappingFileArray)
    {
        //-- copy array
        _mappingFileList.clear();
        for (int i = 0; i < mappingFileArray.length; i++) {
            _mappingFileList.add(mappingFileArray[i]);
        }
    } //-- void setMappingFile(java.lang.String) 

    /**
     * Sets the value of field 'name'. The field 'name' has the
     * following description: Name used in code to reference this
     * persistence unit.
     *  
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'nonJtaDataSource'. The field
     * 'nonJtaDataSource' has the following description: The
     * container-specific name of a non-JTA
     *  datasource to use.
     *  
     * 
     * @param nonJtaDataSource the value of field 'nonJtaDataSource'
     */
    public void setNonJtaDataSource(java.lang.String nonJtaDataSource)
    {
        this._nonJtaDataSource = nonJtaDataSource;
    } //-- void setNonJtaDataSource(java.lang.String) 

    /**
     * Sets the value of field 'properties'. The field 'properties'
     * has the following description: A list of vendor-specific
     * properties.
     *  
     * 
     * @param properties the value of field 'properties'.
     */
    public void setProperties(org.castor.jpa.persistence.xml.Properties properties)
    {
        this._properties = properties;
    } //-- void setProperties(org.castor.jpa.persistence.xml.Properties) 

    /**
     * Sets the value of field 'provider'. The field 'provider' has
     * the following description: Provider class that supplies
     *  EntityManagers for this persistence unit.
     *  
     * 
     * @param provider the value of field 'provider'.
     */
    public void setProvider(java.lang.String provider)
    {
        this._provider = provider;
    } //-- void setProvider(java.lang.String) 

    /**
     * Sets the value of field 'transactionType'. The field
     * 'transactionType' has the following description: Type of
     * transactions used by EntityManagers from this
     *  persistence unit.
     *  
     * 
     * @param transactionType the value of field 'transactionType'.
     */
    public void setTransactionType(org.castor.jpa.persistence.xml.types.PersistenceUnitTransactionType transactionType)
    {
        this._transactionType = transactionType;
    } //-- void setTransactionType(org.castor.jpa.persistence.xml.types.PersistenceUnitTransactionType) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return PersistenceUnit
     */
    public static org.castor.jpa.persistence.xml.PersistenceUnit unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.castor.jpa.persistence.xml.PersistenceUnit) Unmarshaller.unmarshal(org.castor.jpa.persistence.xml.PersistenceUnit.class, reader);
    } //-- org.castor.jpa.persistence.xml.PersistenceUnit unmarshal(java.io.Reader) 

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
