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
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.types;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.XMLFieldHandler;
import org.exolab.castor.xml.TypeValidator;

import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.exolab.castor.mapping.ValidityException;
/**
 * The Duration Descriptor
 * @author <a href="kvisco@intalio.com">Keith Visco</a>
 * @author <a href="blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision$ $Date$
 *
 */
public class DurationDescriptor
    implements XMLClassDescriptor
{


    /**
     * Used for returning no attribute and no element fields
    **/
    private static final XMLFieldDescriptor[] _noFields
        = new XMLFieldDescriptor[0];

    /**
     * The TypeValidator to use for validation of the described class
    **/
    private TypeValidator validator = null;

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
    private static final String _xmlName = "duration";

    private static XMLFieldDescriptorImpl _contentDescriptor = null;

    private static FieldDescriptor[] _fields = null;

    //----------------/
    //- Constructors -/
    //----------------/

    public DurationDescriptor() {
        super();
        if (_contentDescriptor == null) {
            _contentDescriptor = new XMLFieldDescriptorImpl(String.class,
                "content", "content", NodeType.Text);
            //-- setHandler
            _contentDescriptor.setHandler(new DurationFieldHandler());
        }

        if (_fields == null) {
            _fields = new FieldDescriptor[1];
            _fields[0] = _contentDescriptor;
        }

    } //-- DurationDescriptor

    //------------------/
    //- Public Methods -/
    //------------------/

    /**
     * Returns the set of XMLFieldDescriptors for all members
     * that should be marshalled as XML attributes.
     * @return an array of XMLFieldDescriptors for all members
     * that should be marshalled as XML attributes.
    **/
    public XMLFieldDescriptor[]  getAttributeDescriptors() {
        return _noFields;
    } // getAttributeDescriptors

    /**
     * Returns the XMLFieldDescriptor for the member
     * that should be marshalled as text content.
     * @return the XMLFieldDescriptor for the member
     * that should be marshalled as text content.
    **/
    public XMLFieldDescriptor getContentDescriptor() {
        return _contentDescriptor;
    } // getContentDescriptor

    /**
     * Returns the set of XMLFieldDescriptors for all members
     * that should be marshalled as XML elements.
     * @return an array of XMLFieldDescriptors for all members
     * that should be marshalled as XML elements.
    **/
    public XMLFieldDescriptor[]  getElementDescriptors() {
        return _noFields;
    } // getElementDescriptors

    /**
     * Returns the XML field descriptor matching the given
     * xml name and nodeType. If NodeType is null, then
     * either an AttributeDescriptor, or ElementDescriptor
     * may be returned. Null is returned if no matching
     * descriptor is available.
     *
     * @param name the xml name to match against
     * @param nodeType, the NodeType to match against, or null if
     * the node type is not known.
     * @return the matching descriptor, or null if no matching
     * descriptor is available.
     *
    **/
    public XMLFieldDescriptor getFieldDescriptor
        (String name, NodeType nodeType)
    {
        return null;

    } //-- getFieldDescriptor

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
     * Returns a specific validator for the class described by
     * this ClassDescriptor. A null value may be returned
     * if no specific validator exists.
     *
     * @return the type validator for the class described by this
     * ClassDescriptor.
    **/
    public TypeValidator getValidator() {
        return validator;
    } //-- getValidator

    /**
     * Returns the XML Name for the Class being described.
     *
     * @return the XML name.
    **/
    public String getXMLName() {
        return _xmlName;
    } //-- getXMLName

    /**
     * Returns the String representation of this XMLClassDescriptor
     * @return the String representation of this XMLClassDescriptor
    **/
    public String toString() {

        String str = super.toString() +
            "; descriptor for class: duration";

        //-- add xml name
        str += "; xml name: " + _xmlName;

        return str;
    } //-- toString


    //-------------------------------------/
    //- Implementation of ClassDescriptor -/
    //-------------------------------------/

    /**
     * Returns the Java class represented by this descriptor.
     *
     * @return The Java class
     */
    public Class getJavaClass() {
        return Duration.class;
    } //-- getJavaClass


    /**
     * Returns a list of fields represented by this descriptor.
     *
     * @return A list of fields
     */
    public FieldDescriptor[] getFields() {
        return _fields;
    } //-- getFields



    /**
     * Returns the class descriptor of the class extended by this class.
     *
     * @return The extended class descriptor
     */
    public ClassDescriptor getExtends() {
        return null;
    } //-- getExtends


    /**
     * Returns the identity field, null if this class has no identity.
     *
     * @return The identity field
     */
    public FieldDescriptor getIdentity() {
        return null;
    } //-- getIdentity


    /**
     * Returns the access mode specified for this class.
     *
     * @return The access mode
     */
    public AccessMode getAccessMode() {
        return null;
    } //-- getAccessMode


    /**
     * <p>Returns true if the given object represented by this XMLClassDescriptor
     * can accept a member whose name is given.
     * An XMLClassDescriptor can accept a field if it contains a descriptor that matches
     * the given name and if the given object can hold this field (i.e a value is not already set for
     * this field).
     * <p>This is mainly used for container object (that can contains other object), in this particular case
     * the implementation will return null.
     * @param fieldName the name of the field to check
     * @param object the object represented by this XMLCLassDescriptor
     * @return true if the given object represented by this XMLClassDescriptor
     * can accept a member whose name is given.
     */
    public boolean canAccept(String fieldName, Object object) {
         return false;
    }

    /**
     * A specialized FieldHandler for the XML Schema
     * TimeDuration related types
     * @author <a href="blandin@intalio.com">Arnaud Blandin</a>
     * @version $Revision $ $Date $
    **/
    class DurationFieldHandler extends XMLFieldHandler {

        //----------------/
        //- Constructors -/
        //----------------/

        /**
         * Creates a new TimeDurationFieldHandler
        **/
        public DurationFieldHandler() {
            super();
        } //-- TimeFieldHandler

        //------------------/
        //- Public Methods -/
        //------------------/

        /**
         * Returns the value of the field associated with this
         * descriptor from the given target object.
         * @param target the object to get the value from
         * @return the value of the field associated with this
         * descriptor from the given target object.
        **/
        public Object getValue(Object target)
            throws java.lang.IllegalStateException
        {
            Object td = null;
            if (target instanceof Duration)
                td = (Duration) target;
            return td;
        } //-- getValue

        /**
         * Sets the value of the field associated with this descriptor.
         * @param target the object in which to set the value
         * @param value the value of the field
        **/
        public void setValue(Object target, Object value)
            throws java.lang.IllegalStateException
        {


            if (! (target instanceof Duration) ) {
               String err = "DurationDescriptor#setValue: expected Duration, received instead:"
                            + target.getClass();
               throw new IllegalStateException(err);
            }

            Duration time = (Duration) target;

            if (value == null) {
                String err = "DurationDescriptor#setValue: null value";
                throw new IllegalStateException(err);
            }

            //-- update current instance of time with new time
            try {
                Duration temp = Duration.parseDuration(value.toString()) ;
                time.setYear(temp.getYear());
                time.setMonth(temp.getMonth());
                time.setDay(temp.getDay());
                time.setHour(temp.getHour());
                time.setMinute(temp.getMinute());
                time.setSeconds(temp.getSeconds());
            }
            catch (java.text.ParseException ex) {
                throw new IllegalStateException();
            }

        } //-- setValue

        public void resetValue(Object target)
            throws java.lang.IllegalStateException
        {
        }


        /**
         * Checks the field validity. Returns successfully if the field
         * can be stored, is valid, etc, throws an exception otherwise.
         *
         * @param object The object
         * @throws ValidityException The field is invalid, is required and
         *  null, or any other validity violation
         * @throws IllegalStateException The Java object has changed and
         *  is no longer supported by this handler, or the handler
         *  is not compatiable with the Java object
         */
        public void checkValidity( Object object )
            throws ValidityException, IllegalStateException
        {
            // nothing to do?
        } //-- checkValidity


        /**
         * Creates a new instance of the object described by this field.
         *
         * @param parent The object for which the field is created
         * @return A new instance of the field's value
         * @throws IllegalStateException This field is a simple type and
         *  cannot be instantiated
         */
        public Object newInstance( Object parent )
            throws IllegalStateException
        {
            return new Duration();
        } //-- newInstance


    } //-- DurationFieldHandler


} //-- DurationDescriptor


