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
import org.exolab.castor.util.Messages;


/**
 * A field descriptor describes and is used to access the field in a
 * Java object. This class is a wrapper around <tt>java.lang.reflect.Field</tt>
 * and provides additional information about name and type, read/write access,
 * etc.
 * <p>
 * This class is used as the common parent for all derived classes
 * including {@link ContainerFieldDesc}.
 * <p>
 * Engines will extend this class to provide additional functionality.
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see ClassDesc
 */
public class FieldDesc
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
     * The Java class of this field.
     */
    private Class  _fieldType;


    /**
     * Returns true if the field is required. Required fields cannot
     * be null on output.
     */
    private boolean  _required;


    /**
     * True if this field is an immutable type.
     */
    private boolean  _immutable;


    /**
     * The default value for primitive fields. Will be set if the field is null.
     */
    private Object   _default;


    /**
     * Construct a new field descriptor for the specified field.
     * The field must be public, and may not be static or transient.
     * The field name and type are determined from the Java field.
     *
     * @param field The field being described
     * @param required True if the field is required and may not be null
     *    on output
     * @throws MappingException If the field is not public, is static or
     *    transient
     */
    public FieldDesc( Field field, boolean required )
	throws MappingException
    {
	if ( field.getModifiers() != Modifier.PUBLIC &&
	     field.getModifiers() != ( Modifier.PUBLIC | Modifier.VOLATILE ) )
	    throw new MappingException( "mapping.fieldNotAccessible", field.getName(),
					field.getDeclaringClass().getName() );
	_field = field;
	_fieldName = field.getName();
	_fieldType = Types.typeFromPrimitive( field.getType() );
	_required = required;
	_immutable = Types.isImmutable( _fieldType );
	_default = Types.getDefault( _fieldType );
    }


    /**
     * Construct a new field descriptor for the specified field that
     * is accessed through the accessor methods (get/set). The accessor
     * methods must be public and not static. The field name is
     * required for descriptive purposes. The field type must match
     * the return value of the get method and the single parameter of
     * the set method. Either get or set methods are optional.
     *
     * @param fieldName The field being described
     * @param fieldType The field type being described
     * @param getMethod The method used to retrieve the field value,
     *  must accept no parameters and have a return type castable to
     *  the field type
     * @param setMethod The method used to set the field value, must
     *  accept a single paramater that is castable to the field type
     * @param required True if the field is required and may not be null
     *    on output
     * @throws MappingException If the get or set method are not public,
     *   are static, or do not specify the proper types
     *
     */
    public FieldDesc( String fieldName, Class fieldType,
		      Method getMethod, Method setMethod, boolean required )
	throws MappingException
    {
	this( fieldName, fieldType, required );
	if ( fieldName == null )
	    throw new IllegalArgumentException( "Argument 'fieldName' is null" );
	if ( getMethod == null && setMethod == null )
	    throw new IllegalArgumentException( "Both arguments 'getMethod' and 'setMethod' are null" );
	
	if ( getMethod != null ) {
	    if ( ( getMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
		 ( getMethod.getModifiers() & Modifier.STATIC ) != 0 ) 
		throw new MappingException( "mapping.accessorNotAccessible",
					    getMethod, getMethod.getDeclaringClass().getName() );
	    if ( ! _fieldType.isAssignableFrom( getMethod.getReturnType() ) )
		throw new MappingException( "mapping.accessorReturnTypeMismatch",
					    getMethod, fieldType.getName() );
	    _getMethod = getMethod;
	    
	}
	if ( setMethod != null ) {
	    if ( ( setMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
		 ( setMethod.getModifiers() & Modifier.STATIC ) != 0 )
		throw new MappingException( "mapping.accessorNotAccessible",
					    setMethod, setMethod.getDeclaringClass().getName() );
	    if ( setMethod.getParameterTypes().length != 1 &&
		 ! setMethod.getParameterTypes()[ 0 ].isAssignableFrom( _fieldType ) )
		throw new MappingException( "mapping.accessorParameterMismatch",
					    setMethod, fieldType.getName() );
	    _setMethod = setMethod;
	}
    }


    /**
     * Constructor used by derived classes.
     *
     * @param fieldName The field being described
     * @param fieldType The field type being described
     *   to be set on input/output
     * @param required True if the field is required and may not be null
     *    on output
     * @throws MappingException If the field is not public, is static or
     *    transient
     */
    protected FieldDesc( String fieldName, Class fieldType, boolean required )
    {
	_fieldName = fieldName;
	_fieldType = Types.typeFromPrimitive( fieldType );
	_required = required;
	_immutable = Types.isImmutable( _fieldType );
	_default = Types.getDefault( _fieldType );
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
	_required = desc._required;
	_immutable = desc._immutable;
	_default = desc._default;
    }


    /**
     * Returns the name of this field. The field must have a name,
     * even if it set through accessor methods.
     *
     * @return The field name
     */
    public String getFieldName()
    {
	return _fieldName;
    }


    /**
     * Returns the Java type of this field.
     *
     * @return The field type
     */
    public Class getFieldType()
    {
	return _fieldType;
    }


    /**
     * Returns true if the field is required. Required fields cannot
     * be null.
     *
     * @return True if the field is required
     */
    public boolean isRequired()
    {
	return _required;
    }


    /**
     * Returns the get method. The get method is used to obtain the
     * value of the field.
     *
     * @return The get method, or null
     */
    public Method getGetMethod()
    {
	return _getMethod;
    }


    /**
     * Returns the set method. The method used to set the value of
     * the field.
     *
     * @return The set method, or null
     */
    public Method getSetMethod()
    {
	return _setMethod;
    }


    /**
     * Returns the value of the field in the object.
     *
     * @param obj The object
     * @return The value of the field
     */
    public Object getValue( Object obj )
    {
	try {
	    if ( _field != null )
		return _field.get( obj );
	    else if ( _getMethod != null )
		return _getMethod.invoke( obj, null );
	    // If field has no get method, we return the default value.
	    else
		return _default;
	} catch ( IllegalAccessException except ) {
	    // This should never happen
	    throw new IllegalStateException( Messages.format( "mapping.schemaChangeNoAccess", toString() ) );
	} catch ( InvocationTargetException except ) {
	    // This should never happen
	    throw new IllegalStateException( Messages.format( "mapping.schemaChangeInvocation",
							      toString(), except.getMessage() ) );
	}
    }
    

    /**
     * Sets the value of the field in the object.
     *
     * @param obj The object
     * @param value The new value
     */
    public void setValue( Object obj, Object value )
    {
	// If there is a default value, use it for a required field.
	if ( value == null && _required && _default != null )
	    value = _default;
	try {
	    if ( _field != null )
		_field.set( obj, value );
	    else if ( _setMethod != null )
		_setMethod.invoke( obj, new Object[] { value } );
	    // If the field has no set method, ignore it.
	} catch ( IllegalArgumentException except ) {
	    // Graceful way of dealing with unwrapping exception
	    if ( value == null )
		throw new IllegalArgumentException( Messages.format( "mapping.typeConversionNull", toString() ) );
	    else
		throw new IllegalArgumentException( Messages.format( "mapping.typeConversion",
								     toString(), value.getClass().getName() ) );
	} catch ( IllegalAccessException except ) {
	    // This should never happen
	    throw new IllegalStateException( Messages.format( "mapping.schemaChangeNoAccess", toString() ) );
	} catch ( InvocationTargetException except ) {
	    // This should never happen
	    throw new IllegalStateException( Messages.format( "mapping.schemaChangeInvocation",
							      toString(), except.getMessage() ) );
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


    /**
     * Determines if the field can be stored. Returns null if the field
     * can be stored, or a message indicating the reason why the field
     * cannot be stored. For example, if a required field is null.
     * The message name can be used to look up the appropriate message
     * text and should be formatted with an argument specifying the class name.
     *
     * @param obj The object
     * @return Null if can store, otherwise a message indicate why
     *  the field cannot be stored
     */
    public String canStore( Object obj )
    {
	if ( getValue( obj ) == null && _required )
	    return "mapping.requiredField";
	return null;
    }


    /**
     * Determines if the field has been modified from its original
     * cached value. Returns true if the field has been modified.
     *
     * @param obj The object
     * @param cached The cached copy
     * @return True if the field has been modified
     */
    public boolean isModified( Object obj, Object cached )
    {
	Object value;

	value = getValue( obj );
	if ( value == null )
	    return ( getValue( cached ) == null );
	else
	    return ( value.equals( getValue( cached ) ) );
    }


    public String toString()
    {
        return "field " + _fieldName + "(" + _fieldType.getName() + ")";
    }
    

}

