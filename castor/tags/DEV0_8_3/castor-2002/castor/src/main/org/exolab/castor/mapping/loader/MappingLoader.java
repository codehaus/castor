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
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.xml.MappingRoot;
import org.exolab.castor.mapping.xml.ClassMapping;
import org.exolab.castor.mapping.xml.FieldMapping;
import org.exolab.castor.mapping.xml.Container;
import org.exolab.castor.mapping.xml.Include;
import org.exolab.castor.util.Messages;


/**
 * Assists in the construction of descriptors. Can be used as a mapping
 * resolver to the engine. Engines will implement their own mapping
 * scheme typically by extending this class.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public abstract class MappingLoader
    implements MappingResolver
{


    /**
     * The suffix for the name of a compiled class.
     */
    private static final String CompiledSuffix = "Descriptor";


    /**
     * All class descriptors added so far, keyed by Java class.
     */
    private Hashtable  _clsDescs = new Hashtable();


    /**
     * Log writer to report progress. May be null.
     */
    private PrintWriter _logWriter;


    /**
     * The class loader to use.
     */
    private ClassLoader _loader;


    public static final ClassDescriptor NoDescriptor = new ClassDescriptorImpl( Class.class );


    /**
     * Constructs a new mapping helper. This constructor is used by
     * a derived class.
     *
     * @param loader The class loader to use, null for the default
     */
    protected MappingLoader( ClassLoader loader, PrintWriter logWriter )
    {
        if ( loader == null )
            loader = getClass().getClassLoader();
        _loader = loader;
        _logWriter = logWriter;
    }


    public ClassDescriptor getDescriptor( Class type )
    {
        return (ClassDescriptor) _clsDescs.get( type );
    }


    public Enumeration listDescriptors()
    {
        return _clsDescs.elements();
    }


    public Enumeration listJavaClasses()
    {
        return _clsDescs.keys();
    }


    /**
     * Returns the log writer. If not null, errors and other messages
     * should be directed to the log writer.
     */
    protected PrintWriter getLogWriter()
    {
        return _logWriter;
    }


    /**
     * Returns the Java class for the named type. The type name can
     * be one of the accepted short names (e.g. <tt>integer</tt>) or
     * the full Java class name (e.g. <tt>java.lang.Integer</tt>).
     * If the short name is used, the primitive type might be returned.
     */
    protected Class resolveType( String typeName )
        throws ClassNotFoundException
    {
        return Types.typeFromName( _loader, typeName );
    }


    /**
     * Loads the mapping from the specified mapping object. Calls {@link
     * #createDescriptor} to create each descriptor and {@link
     * #addDescriptor} to store it. Also loads all the included mapping
     * files.
     *
     * @param mapping The mapping information
     * @throws MappingException The mapping file is invalid
     */
    public void loadMapping( MappingRoot mapping )
        throws MappingException
    {
        Enumeration   enum;

        // Load the mapping for all the classes.
        enum = mapping.enumerateClassMapping();
        while ( enum.hasMoreElements() ) {
            ClassMapping    clsMap;
            ClassDescriptor clsDesc;

            clsMap = (ClassMapping) enum.nextElement();
            clsDesc = createDescriptor( clsMap );
            addDescriptor( clsDesc );
            // If the return value is NoDescriptor then the derived
            // class was not successful in constructing a descriptor.
            if ( clsDesc == NoDescriptor && _logWriter != null ) {
                _logWriter.println( Messages.format( "mapping.ignoringMapping", clsMap.getName() ) );
            }
        }

        enum = _clsDescs.elements();
        while ( enum.hasMoreElements() ) {
            ClassDescriptor clsDesc;

            clsDesc = (ClassDescriptor) enum.nextElement();
            if ( clsDesc != NoDescriptor  )
                resolveRelations( clsDesc );
        }
    }


    /**
     * Adds a class descriptor. Will throw a mapping exception if a
     * descriptor for this class already exists.
     *
     * @param clsDesc The descriptor to add
     * @throws MappingException A descriptor for this class already
     *  exists
     */
    protected void addDescriptor( ClassDescriptor clsDesc )
        throws MappingException
    {
        if ( _clsDescs.contains( clsDesc.getJavaClass() ) )
            throw new MappingException( "mapping.duplicateDescriptors", clsDesc.getJavaClass().getName() );
        _clsDescs.put( clsDesc.getJavaClass(), clsDesc );
    }


    protected void resolveRelations( ClassDescriptor clsDesc )
        throws MappingException
    {
        FieldDescriptor[] fields;

        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            ClassDescriptor   relDesc;

            relDesc = getDescriptor( fields[ i ].getFieldType() );
            if ( relDesc == NoDescriptor ) {
                // XXX Error message should come here
            } else if ( relDesc != null && fields[ i ] instanceof FieldDescriptorImpl ) {
		( (FieldDescriptorImpl) fields[ i ] ).setClassDescriptor( relDesc );
            }
        }
    }


    /**
     * Creates a new descriptor. The class mapping information is used
     * to create a new stock {@link ClassDescriptor}. Implementations may
     * extend this class to create a more suitable descriptor.
     *
     * @param clsMap The class mapping information
     * @throws MappingException An exception indicating why mapping for
     *  the class cannot be created
     */
    protected ClassDescriptor createDescriptor( ClassMapping clsMap )
        throws MappingException
    {
        FieldDescriptor[] fields;
        FieldDescriptor   identity;
        Enumeration       enum;
        Class             javaClass;
        ClassDescriptor   extend;
        Container         contMaps[];
        Vector            relations;
        ClassDescriptor   clsDesc;

        // See if we have a compiled descriptor.
        clsDesc = loadClassDescriptor( clsMap.getName() );
        if ( clsDesc != null )
            return clsDesc;

        // Obtain the Java class.
        try {
            javaClass = resolveType( clsMap.getName() );
        } catch ( ClassNotFoundException except ) {
            throw new MappingException( "mapping.classNotFound", clsMap.getName() );
        }

        // If this class extends another class, need to obtain the extended
        // class and make sure this class indeed extends it.
        if ( clsMap.getExtends() != null ) {
            try {
                extend = getDescriptor( resolveType( ( (ClassMapping)  clsMap.getExtends() ).getName() ) );
                if ( extend == null )
                    throw new MappingException( "mapping.extendsMissing",
                                                clsMap.getExtends(), javaClass.getName() );
                if ( extend == NoDescriptor )
                    throw new MappingException( "mapping.extendsNoMapping",
                                                clsMap.getExtends(), javaClass.getName() );
            } catch ( ClassNotFoundException except ) {
                throw new MappingException( except );
            }
        } else
            extend = null;
        
        // Get field descriptors first. Note: order must be preserved for fields,
        // but not for relations or container fields. Add all the container fields
        // in there.
        fields = createFieldDescs( javaClass, clsMap.getFieldMapping() );
        /*
        contMaps = clsMap.getContainer();
        if ( contMaps != null && contMaps.length > 0 ) {
            FieldDesc[] allFields;

            allFields = new FieldDesc[ fields.length + contMaps.length ];
            for ( int i = 0 ; i < fields.length ; ++i )
                allFields[ i ] = fields[ i ];
            for ( int i = 0 ; i < contMaps.length ; ++i )
                allFields[ i + fields.length ] = createContainerFieldDesc( javaClass, contMaps[ i ] );
            fields = allFields;
        }
        */

        // Make sure there are no two fields with the same name.
        // Crude but effective way of doing this.
        for ( int i = 0 ; i < fields.length ; ++i ) {
            for ( int j = i + 1 ; j < fields.length ; ++j ) {
                if ( fields[ i ].getFieldName().equals( fields[ j ].getFieldName() ) )
                    throw new MappingException( "The field " + fields[ i ].getFieldName() +
                                                " appears twice in the descriptor for " +
                                                javaClass.getName() );
            }
        }
        
        // Obtain the identity field from one of the above fields.
        // The identity field is removed from the list of fields.
        identity = null;
        if ( clsMap.getIdentity() != null ) {
            for ( int i = 0 ; i < fields.length ; ++i ) {
                if ( fields[ i ].getFieldName().equals( clsMap.getIdentity() ) ) {
                    identity = fields[ i ];
                    if ( identity instanceof FieldDescriptorImpl )
                        ( (FieldDescriptorImpl) identity ).setRequired( true );
                    if ( identity.getHandler() instanceof FieldHandlerImpl )
                        ( (FieldHandlerImpl) identity.getHandler() ).setRequired( true );
                    fields[ i ] = fields[ fields.length - 1 ];

                    // Remove identity field from list of fields.
                    FieldDescriptor[] newFields;

                    newFields = new FieldDescriptor[ fields.length - 1 ];
                    for ( int j = 0 ; j < fields.length - 1 ; ++j )
                        newFields[ j ] = fields[ j ];
                    fields = newFields;
                    break;
                }
            }
            if ( identity == null )
                throw new MappingException( "mapping.identityMissing", clsMap.getIdentity(),
                                            javaClass.getName() );
        }

        // Create the class descriptor.
        clsDesc = new ClassDescriptorImpl( javaClass, fields, identity, extend,
                                           AccessMode.getAccessMode( clsMap.getAccess() ) );
        return clsDesc;
    }


    /**
     * Create field descriptors. The class mapping information is used
     * to create descriptors for all the fields in the class, except
     * for container fields. Implementations may extend this method to
     * create more suitable descriptors, or create descriptors only for
     * a subset of the fields.
     *
     * @param javaClass The class to which the fields belong
     * @param fieldMaps The field mappings
     * @throws MappingException An exception indicating why mapping for
     *  the class cannot be created
     */
    protected FieldDescriptor[] createFieldDescs( Class javaClass, FieldMapping[] fieldMaps )
        throws MappingException
    {
        FieldDescriptor[] fields;
        
        if ( fieldMaps == null || fieldMaps.length == 0 )
            return new FieldDescriptor[ 0 ];
        fields = new FieldDescriptor[ fieldMaps.length ];
        for ( int i = 0 ; i < fieldMaps.length ; ++i ) {
            fields[ i ] = createFieldDesc( javaClass, fieldMaps[ i ] );
        }
        return fields;
    }


    /**
     * Create container field descriptor. The contained mapping is used
     * to create a single field descriptor which includes several field
     * descriptors for all contained fields.
     *
     * @param javaClass The class to which the field belongs
     * @param fieldMap The field mapping
     * @throws MappingException An exception indicating why mapping for
     *  the class cannot be created
     */
    /*
    protected ContainerFieldDesc createContainerFieldDesc( Class javaClass, Container fieldMap )
        throws MappingException
    {
        // Container type must be constructable.
        if ( ! Types.isConstructable( fieldType ) )
            throw new MappingException( "mapping.classNotConstructable", fieldType.getName() );
        // Create descriptors for all the fields.
        fields = createFieldDescs( fieldType, fieldMap.getFieldMapping() );
        return new ContainerFieldDesc( fieldMap.getName(), fieldType, handler, fields );
    }
    */
    
    
    /**
     * Creates a single field descriptor. The field mapping is used to
     * create a new stock {@link FieldDescriptor}. Implementations may
     * extend this class to create a more suitable descriptor.
     *
     * @param javaClass The class to which the field belongs
     * @param fieldMap The field mapping information
     * @return The field descriptor
     * @throws MappingException The field or its accessor methods are not
     *  found, not accessible, not of the specified type, etc
     */
    protected FieldDescriptor createFieldDesc( Class javaClass, FieldMapping fieldMap )
        throws MappingException
    {
        TypeInfo         typeInfo;
        Class            fieldType = null;
        Class            colType = null;
        FieldHandlerImpl handler;
        String           fieldName;
        Method           getMethod = null;
        Method           setMethod = null;

        // If the field type is supplied, grab it and use it to locate the
        // field/accessor. If the field is declared as a collection, grab
        // the collection type as well and use it to locate the field/accessor.
        // The field type and collector type are respectively used to create
        // the field TypeInfo.
        if ( fieldMap.getType() != null ) {
            try {
                fieldType = resolveType( fieldMap.getType() );
            } catch ( ClassNotFoundException except ) {
                throw new MappingException( "mapping.classNotFound", fieldMap.getType() );
            }
        }
        if ( fieldMap.getCollection() != null )
            colType = CollectionHandlers.getCollectionType( fieldMap.getCollection() );

        // First look up the get accessors
        if ( fieldMap.getGetMethod() != null ) {
            getMethod = findAccessor( javaClass, fieldMap.getGetMethod(),
                                      ( colType == null ? fieldType : colType ), true );
            if ( getMethod == null )
                throw new MappingException( "mapping.accessorNotFound",
                                            fieldMap.getGetMethod(), ( colType == null ? fieldType : colType ),
                                            javaClass.getName() );
            if ( fieldType == null && colType == null )
                fieldType = getMethod.getReturnType();
        } else if ( fieldMap.getName() != null ) {
            if ( colType != null && CollectionHandlers.isEnumerate( colType ) )
                getMethod = findAccessor( javaClass, "list" + capitalize( fieldMap.getName() ), Enumeration.class, true );
            if ( getMethod == null )
                getMethod = findAccessor( javaClass, "get" + capitalize( fieldMap.getName() ),
                                          ( colType == null ? fieldType : colType ), true );
            if ( getMethod != null && fieldType == null && colType == null )
                fieldType = getMethod.getReturnType();
        }

        // Second look up the set/add accessor
        if ( fieldMap.getSetMethod() != null ) {
            if ( colType != null && CollectionHandlers.isEnumerate( colType ) ) {
                setMethod = findAccessor( javaClass, fieldMap.getSetMethod(), fieldType, false );
                if ( setMethod == null )
                    throw new MappingException( "mapping.accessorNotFound",
                                                fieldMap.getSetMethod(), fieldType, javaClass.getName() );
            } else {
                setMethod = findAccessor( javaClass, fieldMap.getSetMethod(),
                                          ( colType == null ? fieldType : colType ), false );
                if ( setMethod == null )
                    throw new MappingException( "mapping.accessorNotFound",
                                                fieldMap.getSetMethod(), ( colType == null ? fieldType : colType ),
                                                javaClass.getName() );
            }
            if ( fieldType == null )
                fieldType = setMethod.getParameterTypes()[ 0 ];
        } else if ( fieldMap.getName() != null ) {
            if ( colType != null && CollectionHandlers.isEnumerate( colType ) )
                setMethod = findAccessor( javaClass, "add" + capitalize( fieldMap.getName() ), fieldType, false );
            if ( setMethod == null )
                setMethod = findAccessor( javaClass, "set" + capitalize( fieldMap.getName() ),
                                          ( colType == null ? fieldType : colType ), false );
            if ( setMethod != null && fieldType == null )
                fieldType = setMethod.getParameterTypes()[ 0 ];
        }

        // If accessors found, use them to construct field handler,
        // if not, access field directly
        if ( getMethod != null || setMethod != null ) {
            typeInfo = getTypeInfo( fieldType, colType, fieldMap );

            fieldName = fieldMap.getName();
            if ( fieldName == null )
                fieldName = ( getMethod == null ? setMethod.getName() : getMethod.getName() );
            handler = new FieldHandlerImpl( fieldName, getMethod, setMethod, typeInfo );
        } else {
            // No accessor, map field directly.
            Field field;
            
            fieldName = fieldMap.getName();
            field = findField( javaClass, fieldName, ( colType == null ? fieldType : colType ) );
            if ( field == null )
                throw new MappingException( "mapping.fieldNotAccessible", fieldName, javaClass.getName() );
            if ( fieldType == null )
                fieldType = field.getType();
            typeInfo = getTypeInfo( fieldType, colType, fieldMap );
            handler = new FieldHandlerImpl( field, typeInfo );
        }

        // If there is a create method, add it to the field handler
        if ( fieldMap.getCreateMethod() != null ) {
            try {
                Method method;

                method = javaClass.getMethod( fieldMap.getCreateMethod(), null );
                handler.setCreateMethod( method );
            } catch ( Exception except ) {
                // No such/access to method
                throw new MappingException( "mapping.createMethodNotFound",
                                            fieldMap.getCreateMethod(), javaClass.getName() );
            }
        } else if ( fieldMap.getName() != null && ! Types.isSimpleType( fieldType ) ) {
            try {
                Method method;

                method = javaClass.getMethod( "create" + capitalize( fieldMap.getName() ), null );
                handler.setCreateMethod( method );
            } catch ( Exception except ) { }
        }

        // If there is an has/delete method, add them to field handler
        if ( fieldMap.getName() != null ) {
            Method hasMethod = null;
            Method deleteMethod = null;

            try {
                hasMethod = javaClass.getMethod( "has" + capitalize( fieldMap.getName() ), null );
                if ( ( hasMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
                     ( hasMethod.getModifiers() & Modifier.STATIC ) != 0 )
                    hasMethod = null;
                try {
                    if ( ( hasMethod.getModifiers() & Modifier.PUBLIC ) == 0 ||
                         ( hasMethod.getModifiers() & Modifier.STATIC ) != 0 )
                        deleteMethod = null;
                    deleteMethod = javaClass.getMethod( "delete" + capitalize( fieldMap.getName() ), null );
                } catch ( Exception except ) { }
                handler.setHasDeleteMethod( hasMethod, deleteMethod );
            } catch ( Exception except ) { }
        }

        return new FieldDescriptorImpl( fieldName, typeInfo, handler, false );
    }


    protected TypeInfo getTypeInfo( Class fieldType, Class colType, FieldMapping fieldMap )
        throws MappingException
    {
        return new TypeInfo( Types.typeFromPrimitive( fieldType ), null, null,
                             fieldMap.getRequired(), null, colType );
    }


    /**
     * Returns the named field. Uses reflection to return the named
     * field and check the field type, if specified.
     *
     * @param javaClass The class to which the field belongs
     * @param fieldName The name of the field
     * @param fieldType The type of the field if known, or null
     * @return The field, null if not found
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
            else if ( Types.typeFromPrimitive( fieldType ) != Types.typeFromPrimitive( field.getType() ) )
                throw new MappingException( "mapping.fieldTypeMismatch", field, fieldType.getName() );
            return field;
        } catch ( NoSuchFieldException except ) {
        } catch ( SecurityException except ) {
        }
        return null;
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
     * @return The method, null if not found
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
                else if ( Types.typeFromPrimitive( fieldType ) != Types.typeFromPrimitive( method.getReturnType() ) )
                    throw new MappingException( "mapping.accessorReturnTypeMismatch",
                                                method, fieldType.getName() );
            } else {
                // Set method: look for the named method or prepend set to the
                // method name. If the field type is know, look up a suitable
                // method. If the fielf type is unknown, lookup the first
                // method with that name and one parameter.
                if ( fieldType != null ) {
                    try {
                        method = javaClass.getMethod( methodName, new Class[] { fieldType } );
                    } catch ( Exception except ) {
                        method = javaClass.getMethod( methodName, new Class[] { Types.typeFromPrimitive( fieldType ) } );
                    }
                } else {
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
                        return null;
                }
            }
            // Make sure method is public and not abstract/static.
            if ( ( method.getModifiers() & Modifier.PUBLIC ) == 0 ||
                 ( method.getModifiers() & Modifier.STATIC ) != 0 ) 
                throw new MappingException( "mapping.accessorNotAccessible",
                                            methodName, javaClass.getName() );
            return method;
        } catch ( MappingException except ) {
            throw except;
        } catch ( Exception except ) {
        }
        return null;
    }


    /**
     * Loads a class descriptor from a compiled class.
     *
     * @param clsName The class for which the descriptor is loaded
     * @return An instance of the class descriptor or null if not found
     */
    protected ClassDescriptor loadClassDescriptor( String clsName )
    {
        clsName = clsName + CompiledSuffix;
        try {
            Object obj;

            obj = resolveType( clsName ).newInstance();
            if ( obj instanceof ClassDescriptor )
                return (ClassDescriptor) obj;
            return null;
        } catch ( Exception except ) {
            return null;
        }
    }


    private String capitalize( String name )
    {
        char first;

        first = name.charAt( 0 );
        if ( Character.isUpperCase( first  ) )
            return name;
        return Character.toUpperCase( first ) + name.substring( 1 );
    }


}
