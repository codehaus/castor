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


package org.exolab.castor.dax.engine;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPAttribute;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.Types;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DAXContainedFieldDesc
    extends DAXFieldDesc
{


    /**
     * The field in the parent object that should be used to
     * access this object.
     */
    protected FieldDesc   _containerField;


    /**
     * A field in this object that references the parent object.
     * Must be set when creating a new object of this type.
     */
    protected FieldDesc   _parentRefField;


    /**
     * Constructs a new contained field descriptor. A contained field
     * descriptor is created for each field in the contained object.
     * The actual field descriptor is passed, along with the field in the
     * parent object (contained field) used to access the contained object.
     *
     * @param fieldDesc The descriptor of this field
     * @param objectDesc The descriptor of this field's object type
     * @param containerField The field in the parent object used to access
     *   this contained object
     * @throws MappingException This should never happen
     */
    public DAXContainedFieldDesc( DAXFieldDesc fieldDesc, FieldDesc containerField )
	throws MappingException
    {
	this( fieldDesc, containerField, null );
    }


    public DAXContainedFieldDesc( DAXFieldDesc fieldDesc, FieldDesc containerField,
				  FieldDesc parentRefField )
	throws MappingException
    {
	super( fieldDesc, fieldDesc.getLdapName() );
	if ( containerField == null )
	    throw new IllegalArgumentException( "Argument 'containerField' is null" );
	if ( ! Types.isConstructable( containerField.getFieldType() ) )
	    throw new MappingException( "The field type " + containerField.getFieldType().getName() +
					" is not a consturctable class" );
	_containerField = containerField;
	_parentRefField = parentRefField;
    }


    FieldDesc getContainerField()
    {
	return _containerField;
    }


    public Object getValue( Object obj )
    {
	// This is always called with an instance of the parent
	// object, so we have to obtain the contained object
	// first before retrieving the field from it.
	obj = _containerField.getValue( obj );
	if ( obj == null )
	    return null;
	return super.getValue( obj );
    }


    public LDAPAttribute getAttribute( Object obj )
    {
	// This is always called with an instance of the parent
	// object, so we have to obtain the contained object
	// first before retrieving the field from it.
	obj = _containerField.getValue( obj );
	if ( obj == null )
	    return null;
	return super.getAttribute( obj );
    }


    public void setValue( Object obj, Object value )
    {
	Object self;

	// This is always called with an instance of the parent
	// object, so we have to obtain the contained object
	// first before retrieving the field from it.
	self = _containerField.getValue( obj );
	if ( self == null ) {
	    // If the contained object does not exist, it is
	    // created at this point.
	    self = Types.createNew( _containerField.getFieldType() );
	    if ( _parentRefField != null )
		_parentRefField.setValue( self, obj );
	    _containerField.setValue( obj, self );
	}
	super.setValue( self, value );
    }


    public void setValue( Object obj, LDAPEntry entry )
    {
	Object self;

	// This is always called with an instance of the parent
	// object, so we have to obtain the contained object
	// first before retrieving the field from it.
	self = _containerField.getValue( obj );
	if ( self == null ) {
	    // If the contained object does not exist, it is
	    // created at this point.
	    self = Types.createNew( _containerField.getFieldType() );
	    if ( _parentRefField != null )
		_parentRefField.setValue( self, obj );
	    _containerField.setValue( obj, self );
	}
	super.setValue( self, entry );
    }


    public String toString()
    {
	return super.toString() + " on " + _containerField.getFieldName();
    }



}

