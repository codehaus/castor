/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.xml.util;


import org.exolab.castor.mapping.*;
import org.exolab.castor.xml.*;

/**
 * XML field descriptor. Wraps {@link FieldDescriptor} and adds
 * XML-related information, type conversion, etc.
 *
 * @author <a href="kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
 */
public class XMLFieldDescriptorImpl 
    implements XMLFieldDescriptor
{


    private static final String NULL_CLASS_ERR
        = "The 'type' argument passed to the constructor of "
         + "XMLFieldDescriptorImpl may not be null.";
        
    private static final String NULL_FIELD_NAME_ERR
        = "The 'fieldName' argument passed to the constructor of "
         + "XMLFieldDescriptorImpl may not be null.";

    /**
     * Flag to indicate that objects should be added
     * to their as soon as they are created, but before they
     * are finished being populated.
    **/
    private boolean incremental = false;
    
    /**
     * A flag to indicate that the Object described by this
     * descriptor is multivalued
    **/
    private boolean multivalued = false;
    
    /**
     * The namespace prefix that is to be used when marshalling
    **/
    private String nsPrefix = null;
    
    /**
     * The namespace URI used for both marshalling and unmarshalling
    **/
    private String nsURI = null;
    
    /**
     * The type class descriptor, if this field is of a type
     * known by a descriptor.
     */
    private XMLClassDescriptor  _classDescriptor;
    
    
    /**
     * The Java (programmatic) name of the field being described
    **/
    private String _fieldName = null;
    
    /**
     * The Class type of described field
    **/
    private Class _fieldType = null;
    
    /**
     * The field handler for get/set field value.
     */
    private FieldHandler  _handler = null;
    
    /**
     * True if the field type is immutable.
     */
    private boolean _immutable = false;
    
    /**
     * The node type (attribute, element, text).
     */
    private NodeType _nodeType = null;
    
    /**
     * True if the field is a reference to another Object in the hierarchy.
    **/
    public boolean _isReference = false;
    
    /**
     * indicates a required field when true
    **/
    public boolean _required = false;
    
    /**
     * True if the field is transient and should not be saved/stored.
     */
    private boolean _transient = false;
    
    /**
     * The XML name of the field.
     */
    private String _xmlName    = null;


    
    private FieldValidator _validator = null;
    
    //----------------/
    //- Constructors -/
    //----------------/
    
    
    public XMLFieldDescriptorImpl
        (Class fieldType, String fieldName, String xmlName, NodeType nodeType) 
    {
        
        if (fieldType == null) 
            throw new IllegalArgumentException(NULL_CLASS_ERR);
            
        if (fieldName == null)
            throw new IllegalArgumentException(NULL_FIELD_NAME_ERR);
        
        this._fieldType  = fieldType;
        this._fieldName  = fieldName;
        this._xmlName    = xmlName;
        this._nodeType   = nodeType;
        this._nodeType = ( nodeType == null ? NodeType.Attribute : nodeType );
    } //-- XMLFieldDescriptorImpl
    
    
    /**
     * Construct a new field descriptor for the specified field. This is
     * an XML field descriptor wrapping a field descriptor and adding XML
     * related properties and methods.
     *
     * @param fieldDesc The field descriptor
     * @param xmlName The XML name of the field
     * @param nodeType The node type of this field
     * @throws MappingException Invalid mapping information
     */
    public XMLFieldDescriptorImpl
        ( FieldDescriptor fieldDesc, String xmlName, NodeType nodeType )
        throws MappingException
    {

        this._handler         = fieldDesc.getHandler();
        this._fieldName       = fieldDesc.getFieldName();
        this._fieldType       = fieldDesc.getFieldType();
        this._transient       = fieldDesc.isTransient();
        this._immutable       = fieldDesc.isImmutable();
        this._required        = fieldDesc.isRequired();
        this.multivalued      = fieldDesc.isMultivalued();
        
        ClassDescriptor cd    = fieldDesc.getClassDescriptor();
        if (cd != null) {
            if (cd instanceof XMLClassDescriptor)
                this._classDescriptor = (XMLClassDescriptor)cd;
            else
                this._classDescriptor = new XMLClassDescriptorAdapter(cd, null);
        }
        
        //-- check for instances of java.util.Date
        if (java.util.Date.class.isAssignableFrom(_fieldType)) {
            if (!(_handler instanceof DateFieldHandler)) {
                _handler = new DateFieldHandler(_handler);
            }
        }
        
        //-- handle xml name
        if ( xmlName == null )
            xmlName = getFieldName();
        _xmlName = xmlName;
        
        _nodeType = ( nodeType == null ? NodeType.Attribute : nodeType );
    } //-- XMLFieldDescriptorImpl

    //------------------/
    //- Public Methods -/
    //------------------/
    
    /**
     * Returns the class descriptor related to the field type. If the
     * field type is a class for which a descriptor exists, this
     * descriptor is returned. If the field type is a class for which
     * no mapping is provided, null is returned.
     *
     * @return The class descriptor of the field type, or null
     */
    public ClassDescriptor getClassDescriptor() {
        return _classDescriptor;
    } //-- getClassDescriptor
    
    /**
     * Return the collection handler of this field type. Returns null
     * if the field is not a collection.
     *
     * @return The collection handler
     */
    public CollectionHandler getCollectionHandler() {
        return null;
    } //-- getCollectionHandler
    
    /**
     * Returns the name of the field.
     *
     * @return Field name
     */
    public String getFieldName() {
        return _fieldName;
    } //-- getFieldName

    /**
     * Returns the Java type of the field.
     *
     * @return Field type
     */
    public Class getFieldType() {
        return _fieldType;
    } //-- getFieldType
    
    /**
     * Returns the handler of the field. In order to persist or marshal
     * a field descriptor will be associated with a handler.
     *
     * @return The field handler
     */
    public FieldHandler getHandler() {
        return _handler;
    } //-- getHandler


    /**
     * Returns the XML Name for the field being described.
     *
     * @return the XML name.
    **/
    public String getXMLName() {
        return _xmlName;
    } //-- getXMLName

    /**
     * Return the "suggested" namespace prefix to use when marshalling
     * as XML.
     *
     * @return the "suggested" namespace prefix.
    **/
    public String getNameSpacePrefix() {
        return nsPrefix;
    } //-- getNameSpacePrefix
    
    /**
     * Returns the namespace URI to be used when marshalling and 
     * unmarshalling as XML.
     * 
     * @return the namespace URI.
    **/
    public String getNameSpaceURI() {
        return nsURI;
    } //-- getNameSpaceURI

    public NodeType getNodeType()
    {
        return _nodeType;
    }

    /**
     * Returns a specific validator for the field described by
     * this descriptor. A null value may be returned
     * if no specific validator exists. 
     *
     * @return the type validator for the described field
    **/
    public FieldValidator getValidator() {
        return _validator;
    } //-- getValidator


    /**
     * Returns true if the field type is immutable.
     *
     * @return True if the field type is immutable
     */
    public boolean isImmutable() {
        return _immutable;
    } //-- isImmutable

    /**
     * Returns the incremental flag which when true indicates that this
     * member may be safely added before the unmarshaller is finished
     * unmarshalling it.
     * @return true if the Object can safely be added before the unmarshaller
     * is finished unmarshalling the Object.
    **/
    public boolean isIncremental() {
        return incremental;
    } //-- isIncremental
    
    /**
     * Returns true if the Object described by this descriptor can
     * contain more than one value
     * @return true if the Object described by this descriptor can
     * contain more than one value
    **/
    public boolean isMultivalued() {
        return multivalued;
    } //-- isMultivalued
    
    /**
     * Returns true if the field described by this descriptor is
     * a reference (ie. IDREF) to another object in the 
     * "Object Model" (XML tree)
    **/
    public boolean isReference() {
        return this._isReference;
    } //-- isReference
    
    /**
     * Returns true if the field described by this descriptor is a required
     * field
     * @return true if the field described by this descriptor is a required
     * field
    **/
    public boolean isRequired() {
        return _required;
    } //-- isRequired
    
    /**
     * Returns true if the field is transient. Transient fields are
     * never persisted or marshalled.
     *
     * @return True if transient field
     */
    public boolean isTransient() {
        return _transient;
    } //-- isTransient

    /**
     * Returns true if this descriptor can be used to handle elements
     * or attributes with the given XML name. By default this method
     * simply compares the given XML name with the internal XML name.
     * This method can be overridden to provide more complex matching.
     * @param xmlName the XML name to compare
     * @return true if this descriptor can be used to handle elements
     * or attributes with the given XML name.
    **/
    public boolean matches(String xmlName) {
        if (xmlName != null) {
            return xmlName.equals(this._xmlName);
        }
        return false;
    } //-- matches

    /**
     * Sets the XMLClassDescriptor for the described field
     *
     * @param classDescriptor the XMLClassDescriptor for the described field.
     */
    public void setClassDescriptor(XMLClassDescriptor classDescriptor) {
        this._classDescriptor = classDescriptor;
    } //-- setClassDescriptor


    /**
     * Sets the FieldHandler for the field being described
     * by this FieldDescriptor
     *
     * @param handler the FieldHandler for the field being described
     * by this FieldDescriptor
    **/
    public void setHandler(FieldHandler handler) {
        this._handler = handler;
    } //-- setHandler
    
    /**
     * Sets the incremental flag which indicates whether this member
     * can be added before the unmarshaller is finished unmarshalling it.
     * @param incremental the boolean which if true indicated that this
     * member can safely be added before the unmarshaller is finished 
     * unmarshalling it.
    **/
    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    } //-- setIncremental
    
    /**
     * Sets the immutable flag which indicates that changes
     * to this Field result in a new Object to be created, such
     * as java.lang.String. It serves to identify fields which
     * should not be constructed...until after all the data is
     * available.
     * @param immutable the boolean which if true indicated that this
     * described field is immutable
    **/
    public void setImmutable(boolean immutable) {
        this._immutable = immutable;
    } //-- setImmutable
    
    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    } //-- setMultivalued
    
    /**
     * Sets the flag indicating that the field described by this 
     * descriptor is a reference to another field in the object model.
     * 
     * @param isReference, true if the field is a reference to another field.
    **/
    public void setReference(boolean isReference) {
        this._isReference = isReference;
    } //-- setReference
    
    /**
     * Sets the namespace prefix used when marshalling as XML.
     * @param nsPrefix the namespace prefix used when marshalling
     * the "described" object
    **/
    public void setNameSpacePrefix(String nsPrefix) {
        this.nsPrefix = nsPrefix;
    } //-- setNameSpacePrefix
    
    /**
     * Sets the namespace URI used when marshalling and unmarshalling as XML.
     * @param nsURI the namespace URI used when marshalling and
     * unmarshalling the "described" Object.
    **/
    public void setNameSpaceURI(String nsURI) {
        this.nsURI = nsURI;
    } //-- setNameSpaceURI
    
    /**
     * Sets the XML node type for the described field
     *
     * @param nodeType the NodeType for the described field
    **/
    public void setNodeType(NodeType nodeType) {
        this._nodeType = ( nodeType == null ? NodeType.Attribute : nodeType );        
    } //-- setNodeType
    
    /**
     * Sets the whether or not the described field is required
     * @param required the flag indicating whether or not the
     * described field is required
    **/
    public void setRequired(boolean required) {
        this._required = required;
    } //-- setRequired

    /**
     * Sets whether or not the describled field is transient
     * @param isTransient the flag indicating whether or not the
     * described field is "transient".
     * @see #isTransient
    **/
    public void setTransient(boolean isTransient) {
        _transient = isTransient;
    } //-- isTransient
    
    public void setValidator(FieldValidator validator) {
        
        //-- remove reference from current FieldValidator
        if (_validator != null) {
            _validator.setDescriptor((XMLFieldDescriptor)null);
        }
        
        this._validator = validator;
        
        if (_validator != null) {
            _validator.setDescriptor((XMLFieldDescriptor)this);
        }
            
    } //-- setValidator
    
    public String toString()
    {
        return _fieldName + " AS " + _xmlName;
    }


} //-- XMLFieldDescriptor

