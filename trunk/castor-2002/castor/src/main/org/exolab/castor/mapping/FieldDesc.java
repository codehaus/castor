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


package org.exolab.castor.mapping;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;


/**
 * A field descriptor describes and is used to access the field in a
 * Java object. This class is a wrapper around the <tt>Field</tt>
 * class of the reflection package and provides additional information
 * about target name and type, read/write access, etc.
 * <p>
 * This class is used as the common parent for all derived classes
 * including {@link ContainedFieldDesc}, {@link ObjectFieldDesc}, etc.
 * <p>
 * Engines will extend this class to provide additional functionality.
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see ObjectDesc
 */
public abstract class FieldDesc
{


    /**
     * The Java field described and accessed through this descriptor.
     */
    private Field   _field;


    /**
     * The method used to obtain the value of this field. May be null.
     */
    private Method  _getMethod;


    /**
     * The method used to set the value of this field. May be null.
     */
    private Method  _setMethod;


    /**
     * The name of this field in the object. The field must have a
     * name, even if it set through accessor methods.
     */
    private String _fieldName;


    /**
     * The target name of this field (XML, SQL, etc). The field must
     * have a target name if it exists in the target.
     */
    private String _targetName;


    /**
     * The Java type of this field in the object.
     */
    private Class  _fieldType;


    /**
     * Returns true if the field is required. Required fields cannot
     * be null on output.
     */
    private boolean  _required;


    /**
     * True if the field is readable. Readable fields are set on input.
     */
    private boolean  _readable;


    /**
     * True if the field is writeable. Writeable fields are set on output.
     */
    private boolean  _writeable;


    /**
     * True if this field is an immutable type.
     */
    private boolean  _immutable;


    /**
     * Construct a new field descriptor for the specified field.
     * The field must be public, and may not be static or transient.
     * The field name and type are determined from the Java field.
     * The target name is optional, however fields that have no target
     * name cannot be used on input/output. The target type is
     * optional, and required only if different than the field type.
     *
     * @param field The field being described
     * @param targetName The target name of this field, if the field is
     *   to be set on input/output
     * @param required True if the field is required and may not be null
     *    on output
     * @param readable True if the field is readable
     * @param writeable True if the field is writeable
     * @throws MappingException If the field is not public, is static or
     *    transient
     */
    public FieldDesc( Field field, String targetName,
		      boolean required, boolean readable, boolean writeable )
	throws MappingException
    {
	if ( field.getModifiers() != Modifier.PUBLIC &&
	     field.getModifiers() != ( Modifier.PUBLIC | Modifier.VOLATILE ) )
	    throw new MappingException( "Cannot use the field " + field.getName() + " in class " +
					field.getDeclaringClass() + ": the field is not public, or is static/transient" );
	_field = field;
	_fieldName = field.getName();
	_fieldType = Types.typeFromPrimitive( field.getType() );
	_targetName = targetName;
	_required = required;
	_readable = readable;
	_writeable = writeable;
	_immutable = Types.isImmutable( _fieldType );
    }


    /**
     * Construct a new field descriptor for the specified field that
     * is accessed through the accessor methods (get/set). The accessor
     * methods must be public and not static. The field name is
     * required for descriptive purposes. The field type must match
     * the return value of the get method and the single parameter of
     * the set method. A set method is mandatory for readable fields,
     * a get method is mandatory for writable fields. The target name
     * is optional, however fields that have no target name cannot be
     * used on input/output. The target type is optional, and required
     * only if different than the field type.
     *
     * @param fieldName The field being described
     * @param fieldType The field type being described
     * @param getMethod The method used to retrieve the field value,
     *   required if the field is writeable, must accept no parameters
     *   and have a return type castable to the field type
     * @param setMethod The method used to set the field value, required
     *   if the field is readable, must accept a single paramater that
     *   is castable to the field type
     * @param targetName The target name of this field, if the field is
     *   to be set on input/output
     * @param required True if the field is required and may not be null
     *    on output
     * @param readable True if the field is readable
     * @param writeable True if the field is writeable
     * @throws MappingException If the get or set method are not public,
     *   are static, do not specify the proper types, or are missing
     *
     */
    public FieldDesc( String fieldName, Class fieldType,
		      Method getMethod, Method setMethod, String targetName,
		      boolean required, boolean readable, boolean writeable )
	throws MappingException
    {
	this( fieldName, fieldType, targetName, required, readable, writeable );
	if ( fieldName == null )
	    throw new IllegalArgumentException( "Argument 'fieldName' is null" );
	
	if ( getMethod == null ) {
	    if ( writeable )
		throw new MappingException( "Field is designated writeable, writeable fields must have a get method" );
	} else {
	    if ( ( getMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
		 ( getMethod.getModifiers() & Modifier.STATIC ) != 0 ) 
		throw new MappingException( "Field accessor " + getMethod.getName() +
					    " is not public, or is static/abstract" );
	    if ( ! _fieldType.isAssignableFrom( getMethod.getReturnType() ) )
		throw new MappingException( "The field type and return type of the accessor " +
					    getMethod.getName() + " are not the same" );
	    _getMethod = getMethod;
	    
	}
	if ( setMethod == null ) {
	    if ( readable )
		throw new MappingException( "Field is designated readable, readable fields must have a set method" );
	} else {
	    if ( ( setMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
		 ( setMethod.getModifiers() & Modifier.STATIC ) != 0 )
		throw new MappingException( "Field accessor " + setMethod.getName() +
					    " is not public, or is static/abstract" );
	    if ( setMethod.getParameterTypes().length != 1 &&
		 ! setMethod.getParameterTypes()[ 0 ].isAssignableFrom( _fieldType ) )
		throw new MappingException( "The field type and parameter types of the accessor " +
					    setMethod.getName() + " are not the same" );
	    _setMethod = setMethod;
	}
    }


    /**
     * Constructor used by derived classes.
     *
     * @param fieldName The field being described
     * @param fieldType The field type being described
     * @param targetName The target name of this field, if the field is
     *   to be set on input/output
     * @param required True if the field is required and may not be null
     *    on output
     * @param readable True if the field is readable
     * @param writeable True if the field is writeable
     * @throws MappingException If the field is not public, is static or
     *    transient
     */
    protected FieldDesc( String fieldName, Class fieldType, String targetName,
			 boolean required, boolean readable, boolean writeable )
    {
	_fieldName = fieldName;
	_fieldType = Types.typeFromPrimitive( fieldType );
	_targetName = targetName;
	_required = required;
	_readable = readable;
	_writeable = writeable;
	_immutable = Types.isImmutable( _fieldType );
    }


    /**
     * Used by derived classes to construct a different descriptor based
     * on an existing field descriptor.
     */
    protected FieldDesc( FieldDesc desc )
    {
	_field = desc._field;
	_fieldName = desc._fieldName;
	_fieldType = desc._fieldType;
	_getMethod = desc._getMethod;
	_setMethod = desc._setMethod;
	_targetName = desc._targetName;
	_required = desc._required;
	_readable = desc._readable;
	_writeable = desc._writeable;
	_immutable = desc._immutable;
    }


    /**
     * Returns the name of this field in the object. The field must
     * have a name, even if it set through accessor methods.
     *
     * @return The Java field name
     */
    public String getFieldName()
    {
	return _fieldName;
    }


    /**
     * Returns the target name of this field (XML, SQL, etc). The
     * field must have a target name if it exists in the target.
     * Some fields (e.g. object fields) do not exist in the target.
     *
     * @return The target field name, null if the field does not
     *   exist in the target
     */
    public String getTargetName()
    {
	return _targetName;
    }


    /**
     * Returns the Java type of this field in the object.
     *
     * @return The object field type
     */
    public Class getFieldType()
    {
	return _fieldType;
    }


    /**
     * Returns true if the field is required. Required fields cannot
     * be null and an attempt to output a required field with the
     * value of null will result in a {@link IllegalArgumentException}.
     *
     * @return True if the field is required
     */
    public boolean isRequired()
    {
	return _required;
    }


    /**
     * Returns true if the field is readable. Readable fields are
     * set on output. Some fields (e.g. primary key) must be readable.
     * An attempt to call {@link #setValue} on a non-readable field
     * will result in a {@link IllegalStateException}.
     *
     * @return True if field is readable
     */
    public boolean isReadable()
    {
	return _readable;
    }


    /**
     * Returns true if the field is writeable. Writeable fields are
     * set on output. Some fields (e.g. primary key) must be writeable.
     * An attempt to call {@link #getValue} on a non-writeable field
     * will result in a {@link IllegalStateException}.
     *
     * @return True if field is writeable
     */
    public boolean isWriteable()
    {
	return _writeable;
    }


    /**
     * Returns the get method, the method used to obtain the value.
     * Fields that are not writable need not have a get method.
     *
     * @return The get method, or null
     */
    public Method getGetMethod()
    {
	return _getMethod;
    }


    /**
     * Returns the set method, the method used to set the value.
     * Fields that are not readable need not have a set method.
     *
     * @return The set method, or null
     */
    public Method getSetMethod()
    {
	return _setMethod;
    }


    /**
     * Returns the value of the field from the specified object.
     *
     * @param obj The object
     * @return The value of the field
     */
    public Object getValue( Object obj )
    {
	try {
	    if ( _getMethod == null )
		return _field.get( obj );
	    else
		return _getMethod.invoke( obj, null );
	} catch ( IllegalAccessException except ) {
	    // This should never happen
	    throw new RuntimeException( "Schema change: " + _fieldName + " is no longer accessible" );
	} catch ( InvocationTargetException except ) {
	    // This should never happen
	    throw new RuntimeException( "Java schema change: " + _fieldName + " invocation error" );
	}
    }
    

    /**
     * Sets the value of the field on the specified object.
     *
     * @param obj The object
     * @param value The new value
     */
    public void setValue( Object obj, Object value )
    {
	try {
	    if ( _setMethod == null )
		_field.set( obj, value );
	    else
		_setMethod.invoke( obj, new Object[] { value } );
	} catch ( IllegalArgumentException except ) {
	    // Graceful way of dealing with unwrapping exception
	    if ( value == null ) {
		throw new RuntimeException( "Type conversion error: could not set null to field " +
					    toString() + " of type " + _fieldType.getName() +
					    "; original error: " + except.getMessage() );
	    } else {
		throw new RuntimeException( "Type conversion error: failed to set value of type " +
					    value.getClass().getName() + " in field " + toString() +
					    " of type " + _fieldType.getName() + "; original error: " +
					    except.getMessage() );
	    }
	} catch ( InvocationTargetException except ) {
	    // This should never happen
	    throw new RuntimeException( "Java schema change: " + _fieldName + " invocation error" );
	} catch ( IllegalAccessException except ) {
	    // This should never happen
	    throw new RuntimeException( "Schema change: " + _fieldName + " is no longer accessible" );
	}
    }


    /**
     * Copy the field from the source object to the target object.
     * Immutable objects are shared between the objects, non-immutable
     * objects are copied through serialization. <tt>source</tt> and
     * <tt>target</tt> specify the object on which the field value is
     * to be copied.
     *
     * @param source The source object
     * @param source The target object
     */
    public void copyInto( Object source, Object target )
    {
	if ( _immutable )
	    setValue( target, getValue( source ) );
	else
	    // XXX Need to perform cloning or serialization here
	    setValue( target, getValue( source ) );
    }


    public String toString()
    {
	if ( _targetName != null )
	    return "Field " + _fieldName + " as " + _targetName;
	else
	    return "Field " + _fieldName + " (no target)";
    }
    

}

