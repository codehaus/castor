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
public class ObjectMapping implements java.io.Serializable {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * 
    **/
    private String vExtends;

    /**
     * 
    **/
    private java.lang.String vType;

    /**
     * 
    **/
    private java.lang.String vDescription;

    /**
     * 
    **/
    private SqlTable vSqlTable;

    /**
     * 
    **/
    private XmlSchema vXmlSchema;

    /**
     * 
    **/
    private LdapEntry vLdapEntry;

    /**
     * 
    **/
    private Identity vIdentity;

    /**
     * 
    **/
    private java.util.Vector vFieldList;

    /**
     * 
    **/
    private java.util.Vector vContainerList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ObjectMapping() {
        super();
        vFieldList = new Vector();
        vContainerList = new Vector();
    } //-- ObjectMapping()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public String getExtends() {
        return this.vExtends;
    } //-- String getExtends() 

    /**
     * @param vExtends
    **/
    public void setExtends(String vExtends) {
        this.vExtends = vExtends;
    } //-- void setExtends(String) 

    /**
    **/
    public java.lang.String getType() {
        return this.vType;
    } //-- java.lang.String getType() 

    /**
     * @param vType
    **/
    public void setType(java.lang.String vType) {
        this.vType = vType;
    } //-- void setType(java.lang.String) 

    /**
    **/
    public java.lang.String getReferenceId() {
        return this.vType;
    } //-- java.lang.String getReferenceId() 

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
    **/
    public SqlTable getSqlTable() {
        return this.vSqlTable;
    } //-- SqlTable getSqlTable() 

    /**
     * @param vSqlTable
    **/
    public void setSqlTable(SqlTable vSqlTable) {
        this.vSqlTable = vSqlTable;
    } //-- void setSqlTable(SqlTable) 

    /**
    **/
    public XmlSchema getXmlSchema() {
        return this.vXmlSchema;
    } //-- XmlSchema getXmlSchema() 

    /**
     * @param vXmlSchema
    **/
    public void setXmlSchema(XmlSchema vXmlSchema) {
        this.vXmlSchema = vXmlSchema;
    } //-- void setXmlSchema(XmlSchema) 

    /**
    **/
    public LdapEntry getLdapEntry() {
        return this.vLdapEntry;
    } //-- LdapEntry getLdapEntry() 

    /**
     * @param vLdapEntry
    **/
    public void setLdapEntry(LdapEntry vLdapEntry) {
        this.vLdapEntry = vLdapEntry;
    } //-- void setLdapEntry(LdapEntry) 

    /**
    **/
    public Identity getIdentity() {
        return this.vIdentity;
    } //-- Identity getIdentity() 

    /**
     * @param vIdentity
    **/
    public void setIdentity(Identity vIdentity) {
        this.vIdentity = vIdentity;
    } //-- void setIdentity(Identity) 

    /**
     * @param vFieldMapping
    **/
    public void addFieldMapping(FieldMapping vFieldMapping) 
        throws java.lang.IndexOutOfBoundsException
    {
        vFieldList.addElement(vFieldMapping);
    } //-- void addFieldMapping(FieldMapping) 

    /**
     * @param index
    **/
    public FieldMapping getFieldMapping(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vFieldList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (FieldMapping) vFieldList.elementAt(index);
    } //-- FieldMapping getFieldMapping(int) 

    /**
    **/
    public FieldMapping[] getFieldMapping() {
        int size = vFieldList.size();
        FieldMapping[] mArray = new FieldMapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (FieldMapping) vFieldList.elementAt(index);
        }
        return mArray;
    } //-- FieldMapping[] getFieldMapping() 

    /**
     * @param vFieldMapping
     * @param index
    **/
    public void setFieldMapping(FieldMapping vFieldMapping, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vFieldList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vFieldList.setElementAt(vFieldMapping, index);
    } //-- void setFieldMapping(FieldMapping, int) 

    /**
    **/
    public int getFieldMappingCount() {
        return vFieldList.size();
    } //-- int getFieldMappingCount() 

    /**
    **/
    public java.util.Enumeration enumerateFieldMapping() {
        return vFieldList.elements();
    } //-- java.util.Enumeration enumerateFieldMapping() 

    /**
     * @param index
    **/
    public FieldMapping removeFieldMapping(int index) {
        Object obj = vFieldList.elementAt(index);
        vFieldList.removeElementAt(index);
        return (FieldMapping) obj;
    } //-- FieldMapping removeFieldMapping(int) 

    /**
    **/
    public void removeAllFieldMapping() {
        vFieldList.removeAllElements();
    } //-- void removeAllFieldMapping() 

    /**
     * @param vContainerMapping
    **/
    public void addContainerMapping(ContainerMapping vContainerMapping) 
        throws java.lang.IndexOutOfBoundsException
    {
        vContainerList.addElement(vContainerMapping);
    } //-- void addContainerMapping(ContainerMapping) 

    /**
     * @param index
    **/
    public ContainerMapping getContainerMapping(int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vContainerList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ContainerMapping) vContainerList.elementAt(index);
    } //-- ContainerMapping getContainerMapping(int) 

    /**
    **/
    public ContainerMapping[] getContainerMapping() {
        int size = vContainerList.size();
        ContainerMapping[] mArray = new ContainerMapping[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ContainerMapping) vContainerList.elementAt(index);
        }
        return mArray;
    } //-- ContainerMapping[] getContainerMapping() 

    /**
     * @param vContainerMapping
     * @param index
    **/
    public void setContainerMapping(ContainerMapping vContainerMapping, int index) 
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > vContainerList.size())) {
            throw new IndexOutOfBoundsException();
        }
        vContainerList.setElementAt(vContainerMapping, index);
    } //-- void setContainerMapping(ContainerMapping, int) 

    /**
    **/
    public int getContainerMappingCount() {
        return vContainerList.size();
    } //-- int getContainerMappingCount() 

    /**
    **/
    public java.util.Enumeration enumerateContainerMapping() {
        return vContainerList.elements();
    } //-- java.util.Enumeration enumerateContainerMapping() 

    /**
     * @param index
    **/
    public ContainerMapping removeContainerMapping(int index) {
        Object obj = vContainerList.elementAt(index);
        vContainerList.removeElementAt(index);
        return (ContainerMapping) obj;
    } //-- ContainerMapping removeContainerMapping(int) 

    /**
    **/
    public void removeAllContainerMapping() {
        vContainerList.removeAllElements();
    } //-- void removeAllContainerMapping() 

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
    public static ObjectMapping unmarshal(java.io.Reader reader) 
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (ObjectMapping) Unmarshaller.unmarshal(ObjectMapping.class, reader);
    } //-- ObjectMapping unmarshal(java.io.Reader) 

}
