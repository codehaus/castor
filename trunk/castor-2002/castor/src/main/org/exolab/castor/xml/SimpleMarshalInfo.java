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

package org.exolab.castor.xml;

import org.exolab.castor.util.List;
import java.lang.reflect.Method;

/**
 * A simple (1) implementation of the MarshalInfo interface. This class
 * is primarily used by the Marshalling Framework to create information
 * about objects and classes when no other MarshalInfo class exists.
 * @author <a href="kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
 * <BR />
 * (1) OK, ok, so over time this class became not so simple,
 *  but there is no reason to change it's name...right?! 
 *   
**/
public class SimpleMarshalInfo implements MarshalInfo {
    
    
    /**
     * A MarshalInfo in which this MarshalInfo inherits from
    **/
    private MarshalInfo ancestor = null;

    /**
     * The Class that this MarshalInfo describes
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
     * The MarshalDescriptor for characters
    **/
    private MarshalDescriptor contentDescriptor = null;
    
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
     * Creates a MarshalInfo class used by the Marshalling Framework.
     * @param type the Class type with which this MarshalInfo handles.
    **/
    public SimpleMarshalInfo(Class type) {
        this(type, null);
    } //-- SimpleMarshalInfo
    

    /**
     * Creates a MarshalInfo class used by the Marshalling Framework.
     * @param type the Class type with which this MarshalInfo handles.
     * @param ancestor a MarshalInfo in which this MarshalInfo inherits from.
     * This may be null, and is so by default.
    **/
    public SimpleMarshalInfo(Class type, MarshalInfo ancestor) {
        this._class = type;
        this.ancestor = ancestor;
        attributeDescriptors = new List(3); 
        elementDescriptors = new List(5);
        validationRules = new List(7);
    } //-- SimpleMarshalInfo
    
    /**
     * Adds the given MarshalDescriptor to the list of descriptors. The
     * descriptor will be added to the appropriate list by calling
     * MarshalDescriptor#getDescriptorType() to determine it's type.
     * @param marshalDescriptor the MarshalDescriptor to add to the list
     * of descriptors.
    **/
    public void addMarshalDescriptor(MarshalDescriptor marshalDescriptor) {
        
        if (marshalDescriptor.getDescriptorType() == DescriptorType.attribute)
            attributeDescriptors.add(marshalDescriptor);
        else
            elementDescriptors.add(marshalDescriptor);
            
    } //-- addMarshalDescriptor

    /**
     * Adds the given MarshalDescriptor to the list of descriptors 
     * associated with attributes.
     * @param marshalDescriptor the MarshalDescriptor to add to the list
     * of attribute descriptors.
    **/
    public void addAttributeDescriptor(MarshalDescriptor marshalDescriptor) {
        attributeDescriptors.add(marshalDescriptor);
    } //-- addAttributeDescriptor

    /**
     * Adds the given MarshalDescriptor to the list of descriptors 
     * associated with elements.
     * @param marshalDescriptor the MarshalDescriptor to add to the list
     * of attribute descriptors.
    **/
    public void addElementDescriptor(MarshalDescriptor marshalDescriptor) {
        elementDescriptors.add(marshalDescriptor);
    } //-- addAttributeDescriptor
        
    /**
     * Adds the ValidationRule to the list of ValidationRules to 
     * use when validating instances of the class associated with this
     * MarshalInfo
     * @param validationRule the ValidationRule to add
    **/
    public void addValidationRule(ValidationRule validationRule) {
        validationRules.add(validationRule);
    } //-- setValidatorRule
    
    /**
     * Returns the Class that this MarshalInfo describes
     * @return the Class that this MarshalInfo describes
    **/
    public Class getClassType() {
        return _class;
    } //-- getClassType
    
    /**
     * Returns the set of attribute MarshalDescriptors
     * @return an array of MarshalDescriptors for all members that
     * should be marshalled as Attributes
    **/
    public MarshalDescriptor[] getAttributeDescriptors() {
        
        MarshalDescriptor[] mdArray  = null;
        MarshalDescriptor[] tmpArray = null;
        
        //-- calculate size and offset
        int size = attributeDescriptors.size();
        int offset = 0;
        if (ancestor != null) {
            tmpArray = ancestor.getAttributeDescriptors();
            size += tmpArray.length;
            offset = tmpArray.length;
        }
        mdArray = new MarshalDescriptor[size];
        if (tmpArray != null)
            System.arraycopy(tmpArray, 0, mdArray, 0, tmpArray.length);
            
        attributeDescriptors.toArray(mdArray, offset);
        return mdArray;
        
    } //-- getAttributeDescriptors
    
    /**
     * Returns the set of element MarshalDescriptors
     * @return an array of MarshalDescriptors for all members that
     * should be marshalled as Elements
    **/
    public MarshalDescriptor[]  getElementDescriptors() {
        MarshalDescriptor[] mdArray  = null;
        MarshalDescriptor[] tmpArray = null;
        
        //-- calculate size and offset
        int size = elementDescriptors.size();
        int offset = 0;
        if (ancestor != null) {
            tmpArray = ancestor.getElementDescriptors();
            size += tmpArray.length;
            offset = tmpArray.length;
        }
        mdArray = new MarshalDescriptor[size];
        if (tmpArray != null)
            System.arraycopy(tmpArray, 0, mdArray, 0, tmpArray.length);
            
        elementDescriptors.toArray(mdArray, offset);
        return mdArray;
    } //-- getElementDescriptors

    /**
     * Returns the descriptor for dealing with Text content
     * @return the MarshalDescriptor for dealing with Text content
    **/
    public MarshalDescriptor getContentDescriptor() {
        if (contentDescriptor == null) {
            if (ancestor != null) 
                return ancestor.getContentDescriptor();
        }
        return contentDescriptor;
    } //-- getContentDescriptor
   
    
    /**
     * @return the namespace prefix to use when marshalling as XML.
    **/
    public String getNameSpacePrefix() {
        if (nsPrefix == null) {
            if (ancestor != null) 
                return ancestor.getNameSpacePrefix();
        }
        return nsPrefix;
    } //-- getNameSpacePrefix
    
    /**
     * @return the namespace URI used when marshalling and unmarshalling as XML.
    **/
    public String getNameSpaceURI() {
        if (nsURI == null) {
            if (ancestor != null)
                return ancestor.getNameSpaceURI();
        }
        return nsURI;
    } //-- getNameSpaceURI
   
    /**
     * Returns the ValidationRule used for validating the instances
     * of the class associated with this MarshalInfo
     * @return the ValidationRule used for validating the instances
     * of the class associated with this MarshalInfo
    **/
    public ValidationRule[] getValidationRules() {
        ValidationRule[] vrArray  = null;
        ValidationRule[] tmpArray = null;
        
        //-- calculate size and offset
        int size = elementDescriptors.size();
        int offset = 0;
        if (ancestor != null) {
            tmpArray = ancestor.getValidationRules();
            size += tmpArray.length;
            offset = tmpArray.length;
        }
        vrArray = new ValidationRule[size];
        if (tmpArray != null)
            System.arraycopy(tmpArray, 0, vrArray, 0, tmpArray.length);
            
        validationRules.toArray(vrArray, offset);
        return vrArray;
    } //-- getValidationRules
    
    /**
     * Sets the MarshalDescriptor for handling Text content
     * @param marshalDescriptor, the MarshalDescriptor for handling 
     * text content
    **/
    public void setContentDescriptor(MarshalDescriptor marshalDescriptor) {
        this.contentDescriptor = marshalDescriptor;
    } //-- setContentDescriptor
   
    
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
    
    //---------------------/
    //- Protected Methods -/
    //---------------------/
    
    /**
     * Sets the class name of the Class that this MarshalInfo describes. 
     * This value can be either the name of the class or a string expression, 
     * that when evaluated yeilds the proper class name. 
     * Please refer to MarshalExpr. 
     * <BR />
     * The expression uses a similiar syntax to XPath, and is denoted using
     * the same syntax as the W3C's XSLT 1.0 attribute value templates
     * <PRE>
     * examples:
     *   "com.exoffice.xml.Foo" is a String literal and will be used to
     *    instantiate the class com.exoffice.xml.Foo
     *
     *   "com.exoffice.xml.{@name}" will be evaluated to return a class
     *   name derived from the name attribute of the current element being
     *   unmarshalled. if {@name} evaluates to "Bar" then the class 
     *   com.exoffice.xml.Bar will be instantiated.
     * </PRE>
     * @param className the name of the Class that this MarshalInfo describes.
    **/
    protected void setClassName(String className) {
        this._className = className;
    }
    
} //-- MarshalInfo
