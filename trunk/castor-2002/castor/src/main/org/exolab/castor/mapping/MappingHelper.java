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
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.exolab.castor.mapping.xml.Mapping;
import org.exolab.castor.mapping.xml.ObjectMapping;
import org.exolab.castor.mapping.xml.FieldMapping;
import org.exolab.castor.mapping.xml.ContainerMapping;
import org.exolab.castor.mapping.xml.LdapInfo;


/**
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public abstract class MappingHelper
    implements MappingResolver
{


    private Hashtable  _clsDescs = new Hashtable();


    public ClassDesc getDescriptor( Class type )
    {
	return (ClassDesc) _clsDescs.get( type );
    }


    public Enumeration listDescriptors()
    {
	return _clsDescs.elements();
    }


    public Enumeration listObjectTypes()
    {
	return _clsDescs.keys();
    }


    protected void addDescriptor( ClassDesc clsDesc )
	throws MappingException
    {
	if ( _clsDescs.contains( clsDesc.getObjectType() ) )
	    throw new MappingException( "Mapping already exists" );
	_clsDescs.put( clsDesc.getObjectType(), clsDesc );
    }


    protected ClassDesc createDescriptor( ClassLoader loader, ObjectMapping objMap )
        throws MappingException
    {
	FieldDesc[]      fields;
	FieldDesc        identity;
	Enumeration      enum;
	Class            objType;
	ClassDesc       extend;
	ContainerMapping contMaps[];

	if ( objMap.getExtends() != null ) {
	    try {
		extend = getDescriptor( loader.loadClass( objMap.getExtends() ) );
		if ( extend == null )
		    throw new MappingException( "Extend not found" );
	    } catch ( ClassNotFoundException except ) {
		throw new MappingException( except.toString() );
	    }
	} else
	    extend = null;
	try {
	    objType = loader.loadClass( objMap.getType() );
	} catch ( ClassNotFoundException except ) {
	    throw new MappingException( except.toString() );
	}

	fields = createFieldDescs( loader, objType, objMap.getFieldMapping() );
	contMaps = objMap.getContainerMapping();
	if ( contMaps != null && contMaps.length > 0 ) {
	    FieldDesc[] allFields;

	    allFields = new FieldDesc[ fields.length + contMaps.length ];
	    System.arraycopy( fields, 0, allFields, 0, fields.length );
	    for ( int i = 0 ; i < contMaps.length ; ++i )
		allFields[ i + fields.length ] = createContainerFieldDesc( loader, objType, contMaps[ i ] );
	    fields = allFields;
	}

	identity = null;
	if ( objMap.getIdentity() != null ) {
	    for ( int i = 0 ; i < fields.length ; ++i ) {
		if ( fields[ i ].getFieldName().equals( objMap.getIdentity().getFieldRef() ) ) {
		    identity = fields[ i ];
		    break;
		}
	    }
	    if ( identity == null )
		throw new MappingException( "Identity field not found" );
	}
	return new ClassDesc( objType, fields, identity, extend );
    }


    protected FieldDesc[] createFieldDescs( ClassLoader loader, Class objType, FieldMapping[] fieldMaps )
	throws MappingException
    {
	FieldDesc[] fields;

	if ( fieldMaps == null || fieldMaps.length == 0 )
	    return new FieldDesc[ 0 ];
	fields = new FieldDesc[ fieldMaps.length ];
	for ( int i = 0 ; i < fieldMaps.length ; ++i ) {
	    fields[ i ] = createFieldDesc( loader, objType, fieldMaps[ i ] );
	}
	return fields;
    }


    protected ContainerFieldDesc createContainerFieldDesc( ClassLoader loader, Class objType,
							   ContainerMapping fieldMap )
	throws MappingException
    {
	FieldDesc   container;
	Class       fieldType;
	FieldDesc[] fields;

	// If field type supplied in mapping, use it
	if ( fieldMap.getType() != null ) {
	    try {
		 fieldType = Types.typeFromName( loader, fieldMap.getType() );
	    } catch ( ClassNotFoundException except ) {
		throw new MappingException( except.toString() );
	    }
	} else
	    fieldType = null;

	if ( fieldMap.getGetMethod() != null ||
	     fieldMap.getSetMethod() != null ) {
	    Method getMethod;
	    Method setMethod;

	    getMethod = findAccessor( objType, fieldMap.getGetMethod(), fieldType, true );
	    fieldType = getMethod.getReturnType();
	    setMethod = findAccessor( objType, fieldMap.getGetMethod(), fieldType, true );
	    container = new FieldDesc( fieldMap.getName(), fieldType, getMethod, setMethod,
				   fieldMap.getRequired(), true, true );
	} else {
	    Field field;

	    field = findField( objType, fieldMap.getName(), fieldType );
	    fieldType = field.getType();
	    container = new FieldDesc( field, fieldMap.getRequired(), true, true );
	}
	if ( ! Types.isConstructable( fieldType ) )
	    throw new MappingException( "Unconstructable" );
	fields = createFieldDescs( loader, fieldType, fieldMap.getFieldMapping() );
	if ( fields.length == 0 )
	    throw new MappingException( "Zero fields" );
	return new ContainerFieldDesc( container, fields );
    }


    /**
     * Creates and returns a field descriptor for the specified field mapping.
     *
     * @param loader The class loader to use
     * @param objType The type of the object to which the field belongs
     * @param fieldMap The field mapping information
     * @return The field descriptor
     * @throws MappingException The field or its accessor methods are not
     *  found, not accessible, not of the specified type, etc
     */
    protected FieldDesc createFieldDesc( ClassLoader loader, Class objType, FieldMapping fieldMap )
	throws MappingException
    {
	Class fieldType;

	// If field type supplied in mapping, use it
	if ( fieldMap.getType() != null ) {
	    try {
		 fieldType = Types.typeFromName( loader, fieldMap.getType() );
	    } catch ( ClassNotFoundException except ) {
		throw new MappingException( "Class not found" );
	    }
	} else
	    fieldType = null;

	if ( fieldMap.getGetMethod() != null ||
	     fieldMap.getSetMethod() != null ) {
	    Method getMethod;
	    Method setMethod;

	    getMethod = findAccessor( objType, fieldMap.getGetMethod(), fieldType, true );
	    fieldType = getMethod.getReturnType();
	    setMethod = findAccessor( objType, fieldMap.getGetMethod(), fieldType, true );
	    return new FieldDesc( fieldMap.getName(), fieldType, getMethod, setMethod,
				  fieldMap.getRequired(), true, true );
	} else {
	    Field field;

	    field = findField( objType, fieldMap.getName(), fieldType );
	    return new FieldDesc( field, fieldMap.getRequired(), true, true );
	}
    }


    /**
     * Find and return the named field in the object and make sure that field
     * is accessible.
     *
     * @param objType The type of the object to which the field belongs
     * @param fieldName The name of the field
     * @param fieldType The type of the field if known, or null
     * @return The field
     * @throws MappingException The field is not accessible or is not of the
     *  specified type
     */
    private Field findField( Class objType, String fieldName, Class fieldType )
	throws MappingException
    {
	Field field;

	try {
	    // Look up the field based on its name, make sure it's only modifier
	    // is public. If a type was specified, match the field type.
	    field = objType.getField( fieldName );
	    if ( field.getModifiers() != Modifier.PUBLIC &&
		 field.getModifiers() != ( Modifier.PUBLIC | Modifier.VOLATILE ) )
		throw new MappingException( "Field " + fieldName +
					    " is not public, or is static/transient/final" );
	    if ( fieldType == null ) {
		fieldType = Types.typeFromPrimitive( field.getType() );
	    } else {
		if ( fieldType != Types.typeFromPrimitive( field.getType() ) )
		    throw new MappingException( "Field " + fieldName +
						" is not of specified type " + fieldType );
	    }
	    return field;
	} catch ( NoSuchFieldException except ) {
	} catch ( SecurityException except ) {
	}
	// No such/access to field
	throw new MappingException( "Field " + fieldName +
				    " does not exist or is not accessible in " +
				    objType.getName() );
    }


    /**
     * Find and return the named accessor in the object and make sure that
     * accessor
     * is accessible.
     *
     * @param objType The type of the object to which the field belongs
     * @param methodName The name of the accessor method
     * @param fieldType The type of the field if known, or null
     * @param getMethod True if get method, false if set method
     * @return The method
     * @throws MappingException The method is not accessible or is not of the
     *  specified type
     */
    public static Method findAccessor( Class objType, String methodName,
				       Class fieldType, boolean getMethod )
	throws MappingException
    {
	Method   method;
	Method[] methods;
	int      i;

	try {
	    if ( getMethod ) {
		// Get method: look for the named method or prepend get to the
		// method name. Look up the field and potentially check the
		// return type.
		method = objType.getMethod( methodName, new Class[ 0 ] );
		if ( fieldType == null ) {
		    fieldType = Types.typeFromPrimitive( method.getReturnType() );
		} else {
		    if ( fieldType != Types.typeFromPrimitive( method.getReturnType() ) )
			throw new MappingException( "Field accessor " + methodName +
						    " is not of specified type " + fieldType );
		}
	    } else {
		// Set method: look for the named method or prepend set to the
		// method name. If the field type is know, look up a suitable
		// method. If the fielf type is unknown, lookup the first
		// method with that name and one parameter.
		if ( fieldType != null ) {
		    method = objType.getMethod( methodName, new Class[] { fieldType } );
		} else {
		    methods = objType.getMethods();
		    method = null;
		    for ( i = 0 ; i < methods.length ; ++i ) {
			if ( methods[ i ].getName().equals( methodName ) &&
			     methods[ i ].getParameterTypes().length == 1 ) {
			    method = methods[ i ];
			    break;
			}
		    }
		    if ( method == null )
			throw new NoSuchMethodException();
		}
	    }
	    // Make sure method is public and not abstract/static.
	    if ( method.getModifiers() != Modifier.PUBLIC &&
		 method.getModifiers() != ( Modifier.PUBLIC | Modifier.SYNCHRONIZED ) )
		throw new MappingException( "Field accessor " + methodName +
					    " is not public, or is static/abstract" );
	    return method;
	} catch ( NoSuchMethodException except ) {
	} catch ( SecurityException except ) {
	}
	// No such/access to method
	throw new MappingException( "Field accessor " + methodName +
				    " does not exist or is not accessible in " +
				    objType.getName() );
    }


}
