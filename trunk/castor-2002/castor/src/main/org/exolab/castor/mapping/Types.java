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


import java.lang.reflect.Modifier;
import java.util.Date;
import java.io.Serializable;
import org.exolab.castor.util.Messages;


/**
 * Type information. Can be used to map between short type names (such
 * as 'int') and actual Java types (java.lang.Integer), to determine
 * whether a type is simple (i.e. maps to a single XML attribute, SQL
 * column, etc), as well as to create a new instance of a type.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public abstract class Types
{


    /**
     * Returns the class name based on the supplied type name. The type name
     * can be a short name (e.g. int, byte) or any other Java class (e.g.
     * myapp.Product). If a short type name is used, the proper class will
     * be returned. If a Java class name is used, the class will be loaded and
     * returned through the supplied class loader.
     *
     * @param loader The class loader to use
     * @param typeName The type name
     * @return The type class
     * @throws ClassNotFoundException The specified class could not be found
     */
    public static Class typeFromName( ClassLoader loader, String typeName )
	throws ClassNotFoundException
    {
	for ( int i = 0 ; i < _typeInfos.length ; ++i ) {
	    if ( typeName.equals( _typeInfos[ i ].shortName ) )
		return _typeInfos[ i ].javaType;
	}
	return loader.loadClass( typeName );
    }


    /**
     * Maps from a primitive Java type to a Java class. Returns the same class
     * if <tt>type</tt> is not a primitive. The following conversion applies:
     * <pre>
     * From            To
     * --------------  ---------------
     * Boolean.TYPE    Boolean.class
     * Byte.TYPE       Byte.class
     * Character.TYPE  Character.class
     * Short.TYPE      Short.class
     * Integer.TYPE    Integer.class
     * Long.TYPE       Long.class
     * Float.TYPE      Float.class
     * Double.TYPE     Double.class
     * </pre>
     *
     * @param type The Java type (primitive or not)
     * @return A comparable non-primitive Java type
     */
    public static Class typeFromPrimitive( Class type )
    {
	for ( int i = 0 ; i < _typeInfos.length ; ++i ) {
	    if ( _typeInfos[ i ].primitive == type )
		return _typeInfos[ i ].javaType;
	}
	return type;
    }


    /**
     * Returns true if the Java type is represented as a simple type.
     * A simple can be described with a single XML attribute value,
     * a single SQL column, a single LDAP attribute value, etc.
     * The following types are considered simple:
     * <ul>
     * <li>All primitive types
     * <li>String
     * <li>Date
     * <li>byte/char arrays
     * <li>BigDecimal
     * </ul>
     *
     * @param type The Java type
     * @return True if a simple type
     */
    public static boolean isSimpleType( Class type )
    {
	for ( int i = 0 ; i < _typeInfos.length ; ++i ) {
	    if ( _typeInfos[ i ].javaType == type )
		return true;
	}
	return false;
    }


    /**
     * Constructs a new object from the given class. Does not generate any
     * checked exceptions, since object creation has been proven to work
     * when creating descriptor from mapping.
     */
    public static Object createNew( Class type )
    {
	try {
	    return type.newInstance();
	} catch ( IllegalAccessException except ) {
	    // This should never happen unless  bytecode changed all of a sudden
	    throw new RuntimeException( Messages.format( "castor.reflect.cannotConstruct", type ) );
	} catch ( InstantiationException except ) {
	    // This should never happen unless  bytecode changed all of a sudden
	    throw new RuntimeException( Messages.format( "castor.reflect.cannotConstruct", type ) );
	}
    }


    /**
     * Returns true if the objects of this class are constructable.
     * The class must be publicly available and have a default public
     * constructor.
     *
     * @param type The Java type
     * @return True if constructable
     */
    public static boolean isConstructable( Class type )
    {
	try {
	    if ( ( type.getModifiers() & Modifier.PUBLIC ) == 0 )
		return false;
	    if ( ( type.getModifiers() & ( Modifier.ABSTRACT | Modifier.INTERFACE ) ) != 0 )
		return false;
	    if ( ( type.getConstructor( new Class[0] ).getModifiers() & Modifier.PUBLIC ) != 0 )
	       return true;
	} catch ( NoSuchMethodException except ) {
	} catch ( SecurityException except ) {
	}
	return false;
    }


    /**
     * Returns true if the Java type implements the {@link Serializable}
     * interface.
     *
     * @param type The Java type
     * @return True if declared as serializable
     */
    public static boolean isSerializable( Class type )
    {
	return ( Serializable.class.isAssignableFrom( type ) );
    }


    /**
     * Returns true if the Java type is immutable. Immutable objects are
     * not copied.
     *
     * @param type The Java type
     * @return True if immutable type
     */
    public static boolean isImmutable( Class type )
    {
	for ( int i = 0 ; i < _typeInfos.length ; ++i ) {
	    if ( _typeInfos[ i ].javaType == type )
		return _typeInfos[ i ].immutable;
	}
	return false;
    }


    public static class TypeInfo
    {

	public final String  shortName;

	public final Class   primitive;

	public final Class   javaType;

	public final boolean immutable;

	TypeInfo( String shortName, Class primitive, Class javaType, boolean immutable )
	{
	    this.shortName = shortName;
	    this.primitive = primitive;
	    this.javaType  = javaType;
	    this.immutable = immutable;
	}

    }

    
    /**
     * List of all the simple types supported by Castor.
     */
    static TypeInfo[] _typeInfos = new TypeInfo[] {
	new TypeInfo( "other",       null,                      java.lang.Object.class,     false ),
	new TypeInfo( "string",      null,                      java.lang.String.class,     true ),
	new TypeInfo( "integer",     java.lang.Integer.TYPE,    java.lang.Integer.class,    true ),
	new TypeInfo( "long",        java.lang.Long.TYPE,       java.lang.Long.class,       true ),
	new TypeInfo( "boolean",     java.lang.Boolean.TYPE,    java.lang.Boolean.class,    true ),
	new TypeInfo( "double",      java.lang.Double.TYPE,     java.lang.Double.class,     true ),
	new TypeInfo( "float",       java.lang.Float.TYPE,      java.lang.Float.class,      true ),
	new TypeInfo( "big-decimal", null,                      java.math.BigDecimal.class, true ),
	new TypeInfo( "byte",        java.lang.Byte.TYPE,       java.lang.Byte.class,       true ),
	new TypeInfo( "date",        null,                      java.util.Date.class,       true ),
	new TypeInfo( "short",       java.lang.Short.TYPE,      java.lang.Short.class,      true ),
	new TypeInfo( "char",        java.lang.Character.TYPE,  java.lang.Character.class,  true ),
	new TypeInfo( "bytes",       null,                      byte[].class,               false ),
	new TypeInfo( "chars",       null,                      char[].class,               false ),
	/*
	new TypeInfo( Stream,     "stream",      java.io.InputStream.class,  null ),
	new TypeInfo( Reader,     "reader",      java.io.Reader.class,       null ),
	new TypeInfo( XML,        "xml",         org.w3c.dom.Document.class, org.w3c.dom.Element.class ),
	new TypeInfo( Serialized, "ser",         java.io.Serializable.class, null )
	*/
    };

    
}


