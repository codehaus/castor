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


import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.AccessMode;

import org.exolab.castor.xml.*;

import org.exolab.castor.util.List;

/**
 * An implementation of XMLClassDescriptor
 * @author <a href="kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
 * <b>Note:</b>This used to be SimpleMarshalInfo.
 */
public class XMLClassDescriptorImpl 
    implements XMLClassDescriptor 
{

    /**
     * The Class that this ClassDescriptor describes
    **/
    private Class _class = null;
    
    /**
     * The class name of the Class this marshal info describes.
     * Used when _class == null, or for String expressions
    **/
    private String _className = null;
    
    /**
     * The set of attribute descriptors
    **/
    private List attributeDescriptors = null;
    
    /**
     * The XMLFieldDescriptor for text data
    **/
    private XMLFieldDescriptor contentDescriptor = null;
    
    /**
     * The ValidationRule to use when performing validation
     * of instances of the Class associated with this MarshalInfo
    **/
    private List validationRules = null;

    /**
     * The set of element descriptors
    **/
    private List elementDescriptors = null;

    
    /**
     * The namespace prefix that is to be used when marshalling
    **/
    private String nsPrefix = null;
    
    /**
     * The namespace URI used for both Marshalling and Unmarshalling
    **/
    private String nsURI = null;
    

    /**
     * The name of the XML element.
     */
    private String  _xmlName;


    /**
     * The descriptor of the class which this class extends,
     * or null if this is a top-level class.
     */
    private ClassDescriptor     _extends;


    /**
     * The field of the identity for this class.
     */
    private FieldDescriptor    _identity;


    /**
     * The access mode specified for this class.
     */
    private AccessMode         _accessMode;

    //----------------/
    //- Constructors -/
    //----------------/
    
    /**
     * Creates an XMLClassDescriptor class used by the Marshalling Framework.
     * @param type the Class type with which this ClassDescriptor describes.
    **/
    public XMLClassDescriptorImpl(Class type) {
        this(type, null);
    } //-- XMLClassDescriptorImpl

    /**
     * Creates an XMLClassDescriptor class used by the Marshalling Framework.
     * @param type the Class type with which this ClassDescriptor describes.
    **/
    public XMLClassDescriptorImpl(Class type, String xmlName) {
        this._class = type;
        this._xmlName = xmlName;
        attributeDescriptors = new List(7);
        elementDescriptors = new List(7);
        validationRules = new List();
    } //-- XMLClassDescriptorImpl

    //------------------/
    //- Public Methods -/
    //------------------/
    
    /**
     * Adds the given XMLFieldDescriptor to the list of descriptors. The
     * descriptor will be added to the appropriate list by calling
     * XMLFieldDescriptor#getNodeType() to determine it's type.
     * @param descriptor the XMLFieldDescriptor to add 
    **/
    public void addFieldDescriptor(XMLFieldDescriptor descriptor) {
        
        NodeType nodeType = descriptor.getNodeType();
        switch(nodeType.getType()) {
            case NodeType.ATTRIBUTE:
                attributeDescriptors.add(descriptor);
                break;
            case NodeType.TEXT:
                contentDescriptor = descriptor;
                break;
            default:
                elementDescriptors.add(descriptor);
                break;
        }
            
    } //-- addFieldDescriptor


    /**
     * Returns the set of XMLFieldDescriptors for all members
     * that should be marshalled as XML attributes.
     * @return an array of XMLFieldDescriptors for all members
     * that should be marshalled as XML attributes.
    **/
    public XMLFieldDescriptor[]  getAttributeDescriptors() {
        XMLFieldDescriptor[] fields 
            = new XMLFieldDescriptor[attributeDescriptors.size()];
        attributeDescriptors.toArray(fields);
        return fields;
    } // getAttributeDescriptors
    
    /**
     * Returns the XMLFieldDescriptor for the member
     * that should be marshalled as text content.
     * @return the XMLFieldDescriptor for the member
     * that should be marshalled as text content.
    **/
    public XMLFieldDescriptor getContentDescriptor() {
        return contentDescriptor;
    } // getContentDescriptor
    
    /**
     * Returns the set of XMLFieldDescriptors for all members
     * that should be marshalled as XML elements.
     * @return an array of XMLFieldDescriptors for all members
     * that should be marshalled as XML elements.
    **/
    public XMLFieldDescriptor[]  getElementDescriptors() {
        XMLFieldDescriptor[] fields 
            = new XMLFieldDescriptor[elementDescriptors.size()];
        elementDescriptors.toArray(fields);
        return fields;
    } // getElementDescriptors

    /**
     * @return the namespace prefix to use when marshalling as XML.
    **/
    public String getNameSpacePrefix() {
        return nsPrefix;
    } //-- getNameSpacePrefix
    
    /**
     * @return the namespace URI used when marshalling and unmarshalling as XML.
    **/
    public String getNameSpaceURI() {
        return nsURI;
    } //-- getNameSpaceURI

    /**
     * Returns the XML Name for the Class being described.
     *
     * @return the XML name.
    **/
    public String getXMLName() {
        return _xmlName;
    } //-- getXMLName   
    
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
     * Sets the XML name for the Class described by this XMLClassDescriptor
     *
     * @param xmlName the XML name for the Class described by this 
     * XMLClassDescriptor
    **/
    public void setXMLName(String xmlName) {
        this._xmlName = xmlName;
    } //-- setXMLName
    
    public String toString()
    {
        return super.toString() + " AS " + _xmlName;
    }

    //-------------------------------------/
    //- Implementation of ClassDescriptor -/
    //-------------------------------------/
    
    /**
     * Returns the Java class represented by this descriptor.
     *
     * @return The Java class
     */
    public Class getJavaClass() {
        return _class;
    } //-- getJavaClass


    /**
     * Returns a list of fields represented by this descriptor.
     *
     * @return A list of fields
     */
    public FieldDescriptor[] getFields() {
        int size = attributeDescriptors.size();
        size += elementDescriptors.size();
        if (contentDescriptor != null) ++size;
        
        FieldDescriptor[] fields = new FieldDescriptor[size];
        int c = 0;
        for (int i = 0; i < attributeDescriptors.size(); i++)
            fields[c++] = (FieldDescriptor) attributeDescriptors.get(i);
            
        for (int i = 0; i < elementDescriptors.size(); i++)
            fields[c++] = (FieldDescriptor) elementDescriptors.get(i);
            
        if (contentDescriptor != null) 
            fields[c] = contentDescriptor;
            
        return fields;
    } //-- getFields
    


    /**
     * Returns the class descriptor of the class extended by this class.
     *
     * @return The extended class descriptor
     */
    public ClassDescriptor getExtends() {
        return _extends;
    } //-- getExtends


    /**
     * Returns the identity field, null if this class has no identity.
     *
     * @return The identity field
     */
    public FieldDescriptor getIdentity() {
        return _identity;
    } //-- getIdentity


    /**
     * Returns the access mode specified for this class.
     *
     * @return The access mode
     */
    public AccessMode getAccessMode() {
        return _accessMode;
    } //-- getAccessMode
    
} //-- XMLClassDescriptor


