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


package org.exolab.castor.jdo.engine;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.Types;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class PrimaryKeyDesc
{


    /**
     * The field descriptors for the primary key fields.
     * These fields are relevant to the primary key object,
     * not the table object holding the primary key.
     */
    private JDOFieldDesc[]  _fields;


    /**
     * The name of the column if the primary key is a primitive
     * data type.
     */
    private String           _sqlName;


    /**
     * The class of the primary key object. Used for constructing
     * new primary keys. This might be a complex object or a
     * primitive, but never an array.
     */
    private Class            _primKeyClass;


    private boolean           _primitive;


    /**
     * Constructs a primary key descriptor for a single column external primary
     * key. The primary key class and column name must be specified. The primary
     * key class must be a Java primitive or a string.
     *
     * @param primKeyClass The primary key class
     * @param sqlName The name of the primary key field
     * @throws MappingException The primary key class is not a primitive or a
     *   string, or <tt>sqlName</tt> is null
     */
    public PrimaryKeyDesc( Class primKeyClass, String sqlName )
	throws MappingException
    {
	// Object creation test.
	if ( primKeyClass == null )
	    throw new NullPointerException( "Argument 'primKeyClass' is null" );
	if ( primKeyClass.isArray() )
	    throw new MappingException( "Primary key cannot be an array" );
	if ( ! Types.isSimpleType( primKeyClass ) )
	    throw new MappingException( "This constructor can only be used with single-column primary key classes" );
	_primKeyClass = Types.typeFromPrimitive( primKeyClass );
	if ( sqlName == null )
	    throw new MappingException( "Primitive primary key requires sql name" );
	_sqlName = sqlName;
	_primitive = true;
    }



    /**
     * Constructs a primary key descriptor for a single/multi column external
     * primary key. The primary key class and field set must be specified.
     * The primary key class must be a non-primitive constructable class.
     * The described fields are used to access the contents of the primary key.
     *
     * @param primKeyClass The primary key class
     * @param fields The relevant fields for the primary key
     * @throws MappingException The primary key class is not a constructable
     *   class, is a primitive, or the field list is not valid
     */
    public PrimaryKeyDesc( Class primKeyClass, JDOFieldDesc[] fields )
	throws MappingException
    {
	if ( fields == null || fields.length == 0 )
	    throw new MappingException( "Primary key must have at least one field" );
	_fields = fields;

	// Object creation test.
	if ( primKeyClass == null )
	    throw new NullPointerException( "Argument 'primKeyClass' is null" );
	if ( primKeyClass.isArray() )
	    throw new MappingException( "Primary key cannot be an array" );
	if ( Types.isSimpleType( primKeyClass ) ) {
	    _primitive = true;
	    _sqlName = fields[ 0 ].getSQLName();
	    _primKeyClass = Types.typeFromPrimitive( primKeyClass );
	    if ( _fields.length != 1 )
		throw new MappingException( "Primary key is primitive, can only have one field" );
	} else {
	    if ( ! Types.isConstructable( primKeyClass ) )
		throw new MappingException( "Primary key class is not constructable" );
	    _primitive = false;
	    _primKeyClass = primKeyClass;
	}
    }


    /**
     * Constructs a new primary key object. Does not generate any
     * checked exceptions, since object creation has been proven to
     * work in the constructor tests.
     */
    public Object createNew()
    {
	return Types.createNew( _primKeyClass );
    }


    /**
     * Returns a list of all the field descriptors for the primary key.
     */
    public JDOFieldDesc[] getJDOFields()
    {
	if ( _fields == null )
	    return null;
	else
	    return (JDOFieldDesc[]) _fields.clone();
    }


    /**
     * Returns the number of fields represented by the primary key.
     */
    public int getFieldCount()
    {
	return _fields.length;
    }


    /**
     * Returns the class type of the primary key.
     */
    public Class getPrimaryKeyType()
    {
	return _primKeyClass;
    }


    public String getSQLName()
    {
	return _sqlName;
    }


    /**
     * Returns true if the primary key is a primitive data type.
     * If the primary key is a primitive data type there are no field
     * descriptors, the method {@link #createNew} cannot be called,
     * and {@link #getSqlName} returns the field's sql name.
     */
    public boolean isPrimitive()
    {
	return _primitive;
    }


    /**
     * Tests equality of two primary key objects by comparing the fields
     * in both primary keys. Returns true if both primary key's relevant
     * fields are equal.
     *
     * @param pk1 A primary key
     * @param pk2 A primary key
     * @return True if both primary key are identical
     */
    public boolean equals( Object pk1, Object pk2 )
    {
	int i;

	if ( pk1 == null || pk2 == null )
	    return false;
	if ( _primitive )
	    return ( pk1.equals( pk2 ) );
	for ( i = 0 ; i < _fields.length ; ++i ) {
	    if ( ! _fields[ i ].getValue( pk1 ).equals( _fields[ i ].getValue( pk2 ) ) ) {
		return false;
	    }
	}
	return true;
    }


}
