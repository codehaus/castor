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


/**
 * An object descriptor is used to describe the mapping between a Java
 * class and a target object (XML element, SQL table, LDAP namespace,
 * etc). The object descriptor uses field descriptors to describe the
 * mapping of each field and to provide access to them.
 * <p>
 * Engines will extend this class to provide additional functionality.
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class ObjectDesc
{


    /**
     * The Java type of this object.
     */
    private Class          _objType;


    /**    
     * The fields described for this object.
     */
    protected FieldDesc[]  _fields;


    /**
     * The descriptor of the object which this object extends,
     * or null if this is a top-level object.
     */
    private ObjectDesc     _extends;


    /**
     * The field of the identity for this object.
     */
    private FieldDesc      _identity;


    /**
     * Constructs a new descriptor for the specified object. When describing
     * inheritence, the descriptor of the parent object may be used and only
     * the fields added in this object must be supplied here.
     * 
     * @param objType The Java type of this object
     * @param fields The fields described for this object
     * @param identity The field of the identity (key) of this object,
     *   may be null
     * @param extend The descriptor of the object which this object extends,
     * or null if this is a top-level object
     * @throws MappingException The extended descriptor does not match
     *   a parent class of this object type
     */
    public ObjectDesc( Class objType, FieldDesc[] fields, FieldDesc identity, ObjectDesc extend )
	throws MappingException
    {
	if ( ! Types.isConstructable( objType ) )
	    throw new MappingException( "Type nor constuctable" );
	_objType = objType;
	if ( fields == null )
	    throw new IllegalArgumentException( "Argument 'fields' is null" );
	_fields = fields;
	_identity = identity;
	if ( extend != null ) {
	    FieldDesc[] allFields;

	    if ( ! extend.getObjectType().isAssignableFrom( objType ) )
		throw new MappingException( "The class " + objType.getName() +
					    " does not extend the class " + extend.getObjectType().getName() +
					    " supplied as the extended descriptor" );
	    _extends = extend;
	    allFields = new FieldDesc[ _fields.length + _extends._fields.length ];
	    System.arraycopy( _fields, 0, allFields, 0, _fields.length );
	    System.arraycopy( _extends._fields, 0, allFields, _fields.length, _extends._fields.length );
	    _fields = allFields;
	}
    }


    protected ObjectDesc( ObjectDesc objDesc )
    {
	_objType = objDesc._objType;
	_fields = objDesc._fields;
	_extends = objDesc._extends;
	_identity = objDesc._identity;
    }


    /**
     * Returns the Java type of this object.
     *
     * @return The Java type of this object
     */
    public Class getObjectType()
    {
	return _objType;
    }


    /**
     * Returns the fields described for this object. An array of field
     * descriptors is returned, allowing the set/get methods to be
     * called on each field against this object. The returned array
     * may be of size zero.
     *
     * @return The fields described for this object
     */
    public FieldDesc[] getFields()
    {
	return (FieldDesc[]) _fields.clone();
    }


    /**
     * Returns the descriptor of the object which this object extends,
     * or null if this is a top-level object.
     *
     * @return The descriptor of the extended object, or null
     */
    public ObjectDesc getExtends()
    {
	return _extends;
    }


    /**
     * Constructs a new object from the given class. Does not generate
     * any checked exceptions, since object creation has been proven
     * to work when creating descriptor from mapping.
     *
     * @return A new instance of this object
     */
    public Object createNew()
    {
	return Types.createNew( _objType );
    }

    
    /**
     * Returns the identity field for this object. Not all objects have an
     * identity field. Identity fields are used to persist objects or map them
     * in a shared space.
     *
     * @return The identity field of this object or null
     */
    public FieldDesc getIdentityField()
    {
	return _identity;
    }


    /**
     * Copy values from the source object to the target object.
     * Will copy all the fields, identity and related objects that are
     * specified in the object mapping.
     *
     * @param source The source object
     * @param target The target object
     */
    public void copyInto( Object source, Object target )
    {
        for ( int i = 0 ; i < _fields.length ; ++i ) {
	    _fields[ i ].copyInto( source, target );
        }
	if ( _identity != null )
	    _identity.copyInto( source, target );
        if ( getExtends() != null )
            getExtends().copyInto( source, target );
    } 


}


