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


package org.exolab.castor.mapping.loader;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.TypeConvertor;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ValidityException;
import org.exolab.castor.util.Messages;


/**
 * A field handler that knows how to get/set the values of a field
 * directly or through the get/set methods. Uses reflection.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public final class FieldHandlerImpl
    implements FieldHandler
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
     * The method used to create a new instance of the field.
     */
    private Method  _createMethod;


    /**
     * The Java field name.
     */
    private String  _fieldName;


    /**
     * The Java field type.
     */
    private Class  _fieldType;


    /**
     * True if this field is an immutable type.
     */
    private boolean  _immutable;


    /**
     * The default value for primitive fields. Will be set if the field is null.
     */
    private Object   _default;


    /**
     * Returns true if the field is required. Required fields cannot
     * be null on output.
     */
    private boolean  _required;


    /**
     * Convertor to apply when setting the value of the field. Converts from
     * the value to the field type. Null if no convertor is required.
     */
    private TypeConvertor  _convertTo;


    /**
     * Convertor to apply when reading the value of the field. Converts from
     * the field type to the return value. Null if no convertor is required.
     */
    private TypeConvertor  _convertFrom;


    /**
     * Construct a new field handler for the specified field. The field must
     * be public, and may not be static or transient. The field name is
     * determined from the Java field, the type from the type information.
     *
     * @param field The field being described
     * @param typeInfo Type information
     * @throws MappingException If the field is not public, is static or
     *    transient
     */
    public FieldHandlerImpl( Field field, TypeInfo typeInfo )
        throws MappingException
    {
        if ( field.getModifiers() != Modifier.PUBLIC &&
             field.getModifiers() != ( Modifier.PUBLIC | Modifier.VOLATILE ) )
            throw new MappingException( "mapping.fieldNotAccessible", field.getName(),
                                        field.getDeclaringClass().getName() );
        _field = field;
        _fieldName = field.getName();
        _fieldType = Types.typeFromPrimitive( typeInfo.getFieldType() );
        _immutable = typeInfo.isImmutable();
        _required = typeInfo.isRequired();
        _default = typeInfo.getDefaultValue();
        _convertTo = typeInfo.getConvertorTo();
        _convertFrom = typeInfo.getConvertorFrom();
    }


    /**
     * Construct a new field handler for the specified field that
     * is accessed through the accessor methods (get/set). The accessor
     * methods must be public and not static. The field name is
     * required for descriptive purposes. The field type must match
     * the return value of the get method and the single parameter of
     * the set method. Either get or set methods are optional.
     *
     * @param fieldName The field being described
     * @param getMethod The method used to retrieve the field value,
     *  must accept no parameters and have a return type castable to
     *  the field type
     * @param setMethod The method used to set the field value, must
     *  accept a single paramater that is castable to the field type
     * @param typeInfo Type information
     * @throws MappingException If the get or set method are not public,
     *   are static, or do not specify the proper types
     *
     */
    public FieldHandlerImpl( String fieldName, Method getMethod, Method setMethod, TypeInfo typeInfo )
        throws MappingException
    {
        if ( fieldName == null )
            throw new IllegalArgumentException( "Argument 'fieldName' is null" );
        _fieldName = fieldName;
        if ( getMethod == null && setMethod == null )
            throw new IllegalArgumentException( "Both arguments 'getMethod' and 'setMethod' are null" );
        
        if ( setMethod != null ) {
            if ( ( setMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
                 ( setMethod.getModifiers() & Modifier.STATIC ) != 0 )
                throw new MappingException( "mapping.accessorNotAccessible",
                                            setMethod, setMethod.getDeclaringClass().getName() );
            _setMethod = setMethod;
        }

        if ( getMethod != null ) {
            if ( ( getMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
                 ( getMethod.getModifiers() & Modifier.STATIC ) != 0 ) 
                throw new MappingException( "mapping.accessorNotAccessible",
                                            getMethod, getMethod.getDeclaringClass().getName() );
            _getMethod = getMethod;
        }

        _fieldType = Types.typeFromPrimitive( typeInfo.getFieldType() );
        _immutable = typeInfo.isImmutable();
        _required = typeInfo.isRequired();
        _default = typeInfo.getDefaultValue();
        _convertTo = typeInfo.getConvertorTo();
        _convertFrom = typeInfo.getConvertorFrom();
    }


    public Object getValue( Object object )
    {
        Object value;

        try {
            // Get the field value
            if ( _field != null )
                value = _field.get( object );
            else
                value = _getMethod.invoke( object, null );
        } catch ( IllegalAccessException except ) {
            // This should never happen
            throw new IllegalStateException( Messages.format( "mapping.schemaChangeNoAccess", toString() ) );
        } catch ( InvocationTargetException except ) {
            // This should never happen
            throw new IllegalStateException( Messages.format( "mapping.schemaChangeInvocation",
                                                              toString(), except.getMessage() ) );
        }

        // If there is a convertor, apply it
        if ( _convertFrom == null )
            return value;
        try {
            return _convertFrom.convert( value );
        } catch ( ClassCastException except ) {
            throw new IllegalArgumentException( Messages.format( "mapping.wrongConvertor",  value.getClass().getName() ) );
        }
    }
    

    public void setValue( Object object, Object value )
    {
        // If there is a default value, use it for a null field.
        // Otherwise, if there is a convertor, apply conversion.
        if ( value == null && _default != null )
            value = _default;
        else if ( _convertTo != null ) {
            try {
                value = _convertTo.convert( value );
            } catch ( ClassCastException except ) {
                throw new IllegalArgumentException( Messages.format( "mapping.wrongConvertor", value.getClass().getName() ) );
            }
        }

        try {
            if ( _field != null )
                _field.set( object, value );
            else
                _setMethod.invoke( object, new Object[] { value } );
            // If the field has no set method, ignore it.
            // If this is a problem, identity it someplace else.
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


    public void checkValidity( Object object )
        throws ValidityException
    {
        try {
            if ( _required ) {
                if ( ( _field != null && _field.get( object ) == null ) ||
                     ( _field == null && _getMethod.invoke( object, null ) == null ) )
                throw new ValidityException( "mapping.requiredField",
                                             object.getClass().getName(), _fieldName );
            }
        } catch ( IllegalAccessException except ) {
            // This should never happen
            throw new IllegalStateException( Messages.format( "mapping.schemaChangeNoAccess", toString() ) );
        } catch ( InvocationTargetException except ) {
            // This should never happen
            throw new IllegalStateException( Messages.format( "mapping.schemaChangeInvocation",
                                                              toString(), except.getMessage() ) );
        }
    }


    public Object newInstance( Object object )
        throws IllegalStateException
    {
        if ( _immutable )
            throw new IllegalStateException( Messages.format( "mapping.classNotConstructable", _fieldType ) );
        // If we have a create method and parent object, call the create method.
        if ( _createMethod != null && object != null ) {
            try {
                return _createMethod.invoke( object, new Object[ 0 ] );
            } catch ( IllegalAccessException except ) {
                // This should never happen
                throw new IllegalStateException( Messages.format( "mapping.schemaChangeNoAccess", toString() ) );
            } catch ( InvocationTargetException except ) {
                // This should never happen
                throw new IllegalStateException( Messages.format( "mapping.schemaChangeInvocation",
                                                                  toString(), except.getMessage() ) );
            }
        }
        return Types.newInstance( _fieldType );
    }


    /**
     * Mutator method used by {@link MappingLoader}.
     */
    void setRequired( boolean required )
    {
        _required = required;
    }


    /**
     * Mutator method used by {@link MappingLoader}.
     */
    void setCreateMethod( Method method )
        throws MappingException
    {
        if ( ( method.getModifiers() & Modifier.PUBLIC ) == 0 ||
             ( method.getModifiers() & Modifier.STATIC ) != 0 ) 
            throw new MappingException( "mapping.accessorNotAccessible",
                                        method, method.getDeclaringClass().getName() );
        if ( method.getParameterTypes().length != 1 )
            throw new MappingException( "mapping.createMethodNoParam",
                                        method, method.getDeclaringClass().getName() );
        _createMethod = method;
    }


    public String toString()
    {
        return _fieldName + "(" + _fieldType.getName() + ")";
    }
    

}

