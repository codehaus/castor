/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.8.12</a>, using an
 * XML Schema.
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
import org.exolab.castor.mapping.xml.types.AccessType;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision$ $Date$
**/
public class ClassMapping implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _name;

    private java.lang.String _extends;

    private java.lang.Object _depends;

    private java.lang.String _identity;

    private org.exolab.castor.mapping.xml.types.AccessType _access;

    private java.lang.Object _keyGenerator;

    private boolean _autoComplete;

    /**
     * keeps track of state for field: _autoComplete
    **/
    private boolean _has_autoComplete;

    private java.lang.String _description;

    private CacheTypeMapping _cacheTypeMapping;

    private MapTo _mapTo;

    private java.util.Vector _fieldMappingList;

    private java.util.Vector _containerList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ClassMapping() {
        super();
        _fieldMappingList = new Vector();
        _containerList = new Vector();
    } //-- org.exolab.castor.mapping.xml.ClassMapping()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vContainer
    **/
    public void addContainer(Container vContainer)
        throws java.lang.IndexOutOfBoundsException
    {
        _containerList.addElement(vContainer);
    } //-- void addContainer(Container) 

    /**
     * 
     * @param vFieldMapping
    **/
    public void addFieldMapping(FieldMapping vFieldMapping)
        throws java.lang.IndexOutOfBoundsException
    {
        _fieldMappingList.addElement(vFieldMapping);
    } //-- void addFieldMapping(FieldMapping) 

    /**
    **/
    public void deleteAutoComplete()
    {
        this._has_autoComplete= false;
    } //-- void deleteAutoComplete() 

    /**
    **/
    public java.util.Enumeration enumerateContainer()
    {
        return _containerList.elements();
    } //-- java.util.Enumeration enumerateContainer() 

    /**
    **/
    public java.util.Enumeration enumerateFieldMapping()
    {
        return _fieldMappingList.elements();
    } //-- java.util.Enumeration enumerateFieldMapping() 

    /**
    **/
    public org.exolab.castor.mapping.xml.types.AccessType getAccess()
    {
        return this._access;
    } //-- org.exolab.castor.mapping.xml.types.AccessType getAccess() 

    /**
    **/
    public boolean getAutoComplete()
    {
        return this._autoComplete;
    } //-- boolean getAutoComplete() 

    /**
    **/
    public CacheTypeMapping getCacheTypeMapping()
    {
        return this._cacheTypeMapping;
    } //-- CacheTypeMapping getCacheTypeMapping() 

    /**
     * 
     * @param index
    **/
    public Container getContainer(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _containerList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Container) _containerList.elementAt(index);
    } //-- Container getContainer(int) 

    /**
    **/
    public Container[] getContainer()
    {
        int size = _containerList.size();
        Container[] mArray = new Container[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Container) _containerList.elementAt(index);
        }
        return mArray;
    } //-- Container[] getContainer() 

    /**
    **/
    public int getContainerCount()
    {
        return _containerList.size();
    } //-- int getContainerCount() 

    /**
    **/
    public java.lang.Object getDepends()
    {
        return this._depends;
    } //-- java.lang.Object getDepends() 

    /**
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
    **/
    public java.lang.String getExtends()
    {
        return this._extends;
    } //-- java.lang.String getExtends() 

    /**
     * 
     * @param index
    **/
    public FieldMapping getFieldMapping(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fieldMappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (FieldMapping) _fieldMappingList.elementAt(index);
    } //-- FieldMapping getFieldMapping(int) 

    /**
    **/
    public FieldMapping[] getFieldMapping()
    {
        int size = _fieldMappingList.size();
        FieldMapping[] mArray = new FieldMapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (FieldMapping) _fieldMappingList.elementAt(index);
        }
        return mArray;
    } //-- FieldMapping[] getFieldMapping() 

    /**
    **/
    public int getFieldMappingCount()
    {
        return _fieldMappingList.size();
    } //-- int getFieldMappingCount() 

    /**
    **/
    public java.lang.String getIdentity()
    {
        return this._identity;
    } //-- java.lang.String getIdentity() 

    /**
    **/
    public java.lang.Object getKeyGenerator()
    {
        return this._keyGenerator;
    } //-- java.lang.Object getKeyGenerator() 

    /**
    **/
    public MapTo getMapTo()
    {
        return this._mapTo;
    } //-- MapTo getMapTo() 

    /**
    **/
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
    **/
    public boolean hasAutoComplete()
    {
        return this._has_autoComplete;
    } //-- boolean hasAutoComplete() 

    /**
    **/
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
     * 
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
    **/
    public void removeAllContainer()
    {
        _containerList.removeAllElements();
    } //-- void removeAllContainer() 

    /**
    **/
    public void removeAllFieldMapping()
    {
        _fieldMappingList.removeAllElements();
    } //-- void removeAllFieldMapping() 

    /**
     * 
     * @param index
    **/
    public Container removeContainer(int index)
    {
        Object obj = _containerList.elementAt(index);
        _containerList.removeElementAt(index);
        return (Container) obj;
    } //-- Container removeContainer(int) 

    /**
     * 
     * @param index
    **/
    public FieldMapping removeFieldMapping(int index)
    {
        Object obj = _fieldMappingList.elementAt(index);
        _fieldMappingList.removeElementAt(index);
        return (FieldMapping) obj;
    } //-- FieldMapping removeFieldMapping(int) 

    /**
     * 
     * @param _access
    **/
    public void setAccess(org.exolab.castor.mapping.xml.types.AccessType _access)
    {
        this._access = _access;
    } //-- void setAccess(org.exolab.castor.mapping.xml.types.AccessType) 

    /**
     * 
     * @param _autoComplete
    **/
    public void setAutoComplete(boolean _autoComplete)
    {
        this._autoComplete = _autoComplete;
        this._has_autoComplete = true;
    } //-- void setAutoComplete(boolean) 

    /**
     * 
     * @param _cacheTypeMapping
    **/
    public void setCacheTypeMapping(CacheTypeMapping _cacheTypeMapping)
    {
        this._cacheTypeMapping = _cacheTypeMapping;
    } //-- void setCacheTypeMapping(CacheTypeMapping) 

    /**
     * 
     * @param vContainer
     * @param index
    **/
    public void setContainer(Container vContainer, int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _containerList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _containerList.setElementAt(vContainer, index);
    } //-- void setContainer(Container, int) 

    /**
     * 
     * @param containerArray
    **/
    public void setContainer(Container[] containerArray)
    {
        //-- copy array
        _containerList.removeAllElements();
        for (int i = 0; i < containerArray.length; i++) {
            _containerList.addElement(containerArray[i]);
        }
    } //-- void setContainer(Container) 

    /**
     * 
     * @param _depends
    **/
    public void setDepends(java.lang.Object _depends)
    {
        this._depends = _depends;
    } //-- void setDepends(java.lang.Object) 

    /**
     * 
     * @param _description
    **/
    public void setDescription(java.lang.String _description)
    {
        this._description = _description;
    } //-- void setDescription(java.lang.String) 

    /**
     * 
     * @param _extends
    **/
    public void setExtends(java.lang.String _extends)
    {
        this._extends = _extends;
    } //-- void setExtends(java.lang.String) 

    /**
     * 
     * @param vFieldMapping
     * @param index
    **/
    public void setFieldMapping(FieldMapping vFieldMapping, int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fieldMappingList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _fieldMappingList.setElementAt(vFieldMapping, index);
    } //-- void setFieldMapping(FieldMapping, int) 

    /**
     * 
     * @param fieldMappingArray
    **/
    public void setFieldMapping(FieldMapping[] fieldMappingArray)
    {
        //-- copy array
        _fieldMappingList.removeAllElements();
        for (int i = 0; i < fieldMappingArray.length; i++) {
            _fieldMappingList.addElement(fieldMappingArray[i]);
        }
    } //-- void setFieldMapping(FieldMapping) 

    /**
     * 
     * @param _identity
    **/
    public void setIdentity(java.lang.String _identity)
    {
        this._identity = _identity;
    } //-- void setIdentity(java.lang.String) 

    /**
     * 
     * @param _keyGenerator
    **/
    public void setKeyGenerator(java.lang.Object _keyGenerator)
    {
        this._keyGenerator = _keyGenerator;
    } //-- void setKeyGenerator(java.lang.Object) 

    /**
     * 
     * @param _mapTo
    **/
    public void setMapTo(MapTo _mapTo)
    {
        this._mapTo = _mapTo;
    } //-- void setMapTo(MapTo) 

    /**
     * 
     * @param _name
    **/
    public void setName(java.lang.String _name)
    {
        this._name = _name;
    } //-- void setName(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static org.exolab.castor.mapping.xml.ClassMapping unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.exolab.castor.mapping.xml.ClassMapping) Unmarshaller.unmarshal(org.exolab.castor.mapping.xml.ClassMapping.class, reader);
    } //-- org.exolab.castor.mapping.xml.ClassMapping unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator.validate(this, null);
    } //-- void validate() 

}
