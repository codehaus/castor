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
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;
import org.exolab.castor.mapping.xml.ContainerMapping;
import org.exolab.castor.mapping.xml.LdapInfo;


/**
 * Assists in the construction of descriptors. Can be used as a mapping
 * resolver to the engine. Engines will implement their own mapping
 * scheme typically by extending this class.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public abstract class MappingHelper
    implements MappingResolver
{


    /**
     * All class descriptors added so far, keyed by Java class.
     */
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


    /**
     * Adds a class descriptor. Will throw a mapping exception if a
     * descriptor for this class already exists.
     *
     * @param clsDesc The descriptor to add
     * @throws MappingException A descriptor for this class already
     *  exists
     */
    protected void addDescriptor( ClassDesc clsDesc )
	throws MappingException
    {
	if ( _clsDescs.contains( clsDesc.getJavaClass() ) )
	    throw new MappingException( "mapping.duplicateDescriptors", clsDesc.getJavaClass() );
	_clsDescs.put( clsDesc.getJavaClass(), clsDesc );
    }


    /**
     * Creates a new descriptor. The class mapping information is used
     * to create a new stock {@link ClassDesc}. Implementations may
     * extend this class to create a more suitable descriptor.
     *
     * @param loader The class loader to use
     * @param clsMap The class mapping information
     * @throws MappingException An exception indicating why mapping for
     *  the class cannot be created
     */
    protected ClassDesc createDescriptor( ClassLoader loader, ClassMapping clsMap )
        throws MappingException
    {
	FieldDesc[]      fields;
	FieldDesc        identity;
	Enumeration      enum;
	Class            javaClass;
	ClassDesc        extend;
	ContainerMapping contMaps[];

	// Obtain the Java class.
	try {
	    javaClass = loader.loadClass( clsMap.getClassName() );
	} catch ( ClassNotFoundException except ) {
	    throw new MappingException( "mapping.classNotFound", clsMap.getClassName(), loader );
	}

	// If this class extends another class, need to obtain the extended
	// class and make sure this class indeed extends it.
	if ( clsMap.getExtends() != null ) {
	    try {
		extend = getDescriptor( loader.loadClass( clsMap.getExtends() ) );
		if ( extend == null )
		    throw new MappingException( "mapping.extendsMissing",
						clsMap.getExtends(), javaClass.getName() );
	    } catch ( ClassNotFoundException except ) {
		throw new MappingException( except );
	    }
	} else
	    extend = null;

	// Create all the field descriptors followed by all the container
	// field descriptors. Order is preserved for fields, but not for
	// container fields.
	fields = createFieldDescs( loader, javaClass, clsMap.getFieldMapping() );
	contMaps = clsMap.getContainerMapping();
	if ( contMaps != null && contMaps.length > 0 ) {
	    FieldDesc[] allFields;

	    allFields = new FieldDesc[ fields.length + contMaps.length ];
	    for ( int i = 0 ; i < fields.length ; ++i )
		allFields[ i ] = fields[ i ];
	    for ( int i = 0 ; i < contMaps.length ; ++i )
		allFields[ i + fields.length ] = createContainerFieldDesc( loader, javaClass, contMaps[ i ] );
	    fields = allFields;
	}

	// Obtain the identity field from one of the above fields.
	identity = null;
	if ( clsMap.getIdentity() != null ) {
	    for ( int i = 0 ; i < fields.length ; ++i ) {
		if ( fields[ i ].getFieldName().equals( clsMap.getIdentity().getFieldRef() ) ) {
		    identity = fields[ i ];
		    break;
		}
	    }
	    if ( identity == null )
		throw new MappingException( "mapping.identityMissing", clsMap.getIdentity().getFieldRef(),
					    javaClass.getName() );
	}
	return new ClassDesc( javaClass, fields, identity, extend );
    }


    /**
     * Create field descriptors. The class mapping information is used
     * to create descriptors for all the fields in the class, except
     * for container fields. Implementations may extend this method to
     * create more suitable descriptors, or create descriptors only for
     * a subset of the fields.
     *
     * @param loader The class loader to use
     * @param javaClass The class to which the fields belong
     * @param fieldMaps The field mappings
     * @throws MappingException An exception indicating why mapping for
     *  the class cannot be created
     */
    protected FieldDesc[] createFieldDescs( ClassLoader loader, Class javaClass, FieldMapping[] fieldMaps )
	throws MappingException
    {
	FieldDesc[] fields;

	if ( fieldMaps == null || fieldMaps.length == 0 )
	    return new FieldDesc[ 0 ];
	fields = new FieldDesc[ fieldMaps.length ];
	for ( int i = 0 ; i < fieldMaps.length ; ++i ) {
	    fields[ i ] = createFieldDesc( loader, javaClass, fieldMaps[ i ] );
	}
	return fields;
    }


    /**
     * Create container field descriptor. The contained mapping is used
     * to create a single field descriptor which includes several field
     * descriptors for all contained fields.
     *
     * @param loader The class loader to use
     * @param javaClass The class to which the field belongs
     * @param fieldMap The field mapping
     * @throws MappingException An exception indicating why mapping for
     *  the class cannot be created
     */
    protected ContainerFieldDesc createContainerFieldDesc( ClassLoader loader, Class javaClass,
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
		throw new MappingException( "mapping.classNotFound", fieldMap.getType(), loader );
	    }
	} else
	    fieldType = null;

	if ( fieldMap.getGetMethod() != null ||
	     fieldMap.getSetMethod() != null ) {
	    // Accessors, map field using either methods.
	    Method getMethod = null;
	    Method setMethod = null;

	    if ( fieldMap.getGetMethod() != null ) {
		getMethod = findAccessor( javaClass, fieldMap.getGetMethod(), fieldType, true );
		// Use return type for parameter type checking, if known
		fieldType = getMethod.getReturnType();
	    }
	    if ( fieldMap.getSetMethod() != null ) {
		setMethod = findAccessor( javaClass, fieldMap.getSetMethod(), fieldType, true );
		if ( fieldType == null )
		    fieldType = setMethod.getParameterTypes()[ 0 ];
	    }
	    container = new FieldDesc( fieldMap.getName(), fieldType, getMethod, setMethod,
				       fieldMap.getRequired() );
	} else {
	    // No accessor, map field directly.
	    Field field;

	    field = findField( javaClass, fieldMap.getName(), fieldType );
	    fieldType = field.getType();
	    container = new FieldDesc( field, fieldMap.getRequired() );
	}

	// Container type must be constructable.
	if ( ! Types.isConstructable( fieldType ) )
	    throw new MappingException( "mapping.classNotConstructable", fieldType.getName() );
	// Create descriptors for all the fields.
	fields = createFieldDescs( loader, fieldType, fieldMap.getFieldMapping() );
	return new ContainerFieldDesc( container, fields );
    }


    /**
     * Creates a single field descriptor. The field mapping is used to
     * create a new stock {@link FieldDesc}. Implementations may
     * extend this class to create a more suitable descriptor.
     *
     * @param loader The class loader to use
     * @param javaClass The class to which the field belongs
     * @param fieldMap The field mapping information
     * @return The field descriptor
     * @throws MappingException The field or its accessor methods are not
     *  found, not accessible, not of the specified type, etc
     */
    protected FieldDesc createFieldDesc( ClassLoader loader, Class javaClass, FieldMapping fieldMap )
	throws MappingException
    {
	Class fieldType;

	// If field type supplied in mapping, use it
	if ( fieldMap.getType() != null ) {
	    try {
		 fieldType = Types.typeFromName( loader, fieldMap.getType() );
	    } catch ( ClassNotFoundException except ) {
		throw new MappingException( "mapping.classNotFound", fieldMap.getType(), loader );
	    }
	} else
	    fieldType = null;

	if ( fieldMap.getGetMethod() != null ||
	     fieldMap.getSetMethod() != null ) {
	    // Accessors, map field using either methods.
	    Method getMethod = null;
	    Method setMethod = null;

	    if ( fieldMap.getGetMethod() != null ) {
		getMethod = findAccessor( javaClass, fieldMap.getGetMethod(), fieldType, true );
		// Use return type for parameter type checking, if known
		fieldType = getMethod.getReturnType();
	    }
	    if ( fieldMap.getSetMethod() != null ) {
		setMethod = findAccessor( javaClass, fieldMap.getGetMethod(), fieldType, true );
		if ( fieldType == null )
		    fieldType = setMethod.getParameterTypes()[ 0 ];
	    }
	    return new FieldDesc( fieldMap.getName(), fieldType, getMethod, setMethod,
				  fieldMap.getRequired() );
	} else {
	    // No accessor, map field directly.
	    Field field;

	    field = findField( javaClass, fieldMap.getName(), fieldType );
	    return new FieldDesc( field, fieldMap.getRequired() );
	}
    }


    /**
     * Returns the named field. Uses reflection to return the named
     * field and check the field type, if specified.
     *
     * @param javaClass The class to which the field belongs
     * @param fieldName The name of the field
     * @param fieldType The type of the field if known, or null
     * @return The field
     * @throws MappingException The field is not accessible or is not of the
     *  specified type
     */
    private Field findField( Class javaClass, String fieldName, Class fieldType )
	throws MappingException
    {
	Field field;

	try {
	    // Look up the field based on its name, make sure it's only modifier
	    // is public. If a type was specified, match the field type.
	    field = javaClass.getField( fieldName );
	    if ( field.getModifiers() != Modifier.PUBLIC &&
		 field.getModifiers() != ( Modifier.PUBLIC | Modifier.VOLATILE ) )
		throw new MappingException( "mapping.fieldNotAccessible", fieldName, javaClass.getName() );
	    if ( fieldType == null )
		fieldType = Types.typeFromPrimitive( field.getType() );
	    else if ( fieldType != Types.typeFromPrimitive( field.getType() ) )
		throw new MappingException( "mapping.fieldTypeMismatch", field, fieldType.getName() );
	    return field;
	} catch ( NoSuchFieldException except ) {
	} catch ( SecurityException except ) {
	}
	// No such/access to field
	throw new MappingException( "mapping.fieldNotAccessible", fieldName, javaClass.getName() );
    }


    /**
     * Returns the named accessor. Uses reflection to return the named
     * accessor and check the return value or parameter type, if
     * specified.
     *
     * @param javaClass The class to which the field belongs
     * @param methodName The name of the accessor method
     * @param fieldType The type of the field if known, or null
     * @param isGetMethod True if get method, false if set method
     * @return The method
     * @throws MappingException The method is not accessible or is not of the
     *  specified type
     */
    private static Method findAccessor( Class javaClass, String methodName,
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
		method = javaClass.getMethod( methodName, new Class[ 0 ] );
		if ( fieldType == null )
		    fieldType = Types.typeFromPrimitive( method.getReturnType() );
		else if ( fieldType != Types.typeFromPrimitive( method.getReturnType() ) )
		    throw new MappingException( "mapping.accessorReturnTypeMismatch",
						method, fieldType.getName() );
	    } else {
		// Set method: look for the named method or prepend set to the
		// method name. If the field type is know, look up a suitable
		// method. If the fielf type is unknown, lookup the first
		// method with that name and one parameter.
		if ( fieldType != null )
		    method = javaClass.getMethod( methodName, new Class[] { fieldType } );
		else {
		    methods = javaClass.getMethods();
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
		throw new MappingException( "mapping.accessorNotAccessible",
					    methodName, javaClass.getName() );
	    return method;
	} catch ( NoSuchMethodException except ) {
	} catch ( SecurityException except ) {
	}
	// No such/access to method
	throw new MappingException( "mapping.accessorNotAccessible",
				    methodName, javaClass.getName() );
    }


}
