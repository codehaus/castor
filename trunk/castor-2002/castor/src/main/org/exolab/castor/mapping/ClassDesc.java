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


import org.exolab.castor.persist.RelationContext;
import org.exolab.castor.persist.PersistenceException;


/**
 * An class descriptor is used to describe the mapping between a Java
 * class and a target type (XML element, SQL table, LDAP namespace,
 * etc). The class descriptor uses field descriptors to describe the
 * mapping of each field and to provide access to them.
 * <p>
 * Engines will extend this class to provide additional functionality.
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class ClassDesc
{


    /**
     * The Java class for this descriptor.
     */
    private Class          _javaClass;


    /**    
     * The fields described for this class.
     */
    protected FieldDesc[]  _fields;


    /**
     * The descriptor of the class which this class extends,
     * or null if this is a top-level class.
     */
    private ClassDesc      _extends;


    /**
     * The field of the identity for this class.
     */
    private FieldDesc      _identity;


    /**
     * Related identity field. Used for relations where the identity
     * value is obtained from another object to which this identity
     * field points. The related identity field is mapped against that
     * other object.
     */
    private FieldDesc      _relIdentity;


    /**
     * A list of all the relations.
     */
    private RelationDesc[]  _relations;


    /**
     * The access mode for this class.
     */
    private AccessMode     _accessMode;


    /**
     * Signals that no descriptor was generated for the class. Mapping
     * helpers will return this value when the mapping does not
     * contain enough information to map the class.
     */
    public static final ClassDesc NoDescriptor = new ClassDesc();


    /**
     * Constructs a new descriptor for the specified class. When describing
     * inheritence, the descriptor of the parent class should be used and only
     * the fields added in this object must be supplied here.
     * 
     * @param javaClass The Java type of this class
     * @param fields The fields described for this class
     * @param relations Descriptors all relations (may be null)
     * @param identity The field of the identity (key) of this class,
     *   may be null
     * @param extend The descriptor of the class which this class extends,
     * @param accessMode The access mode for this class (null is shared)
     * or null if this is a top-level class
     * @throws MappingException The extended descriptor does not match
     *   a parent class of this type
     */
    public ClassDesc( Class javaClass, FieldDesc[] fields, RelationDesc[] relations,
                      FieldDesc identity, ClassDesc extend, AccessMode accessMode )
        throws MappingException
    {
        if ( ! Types.isConstructable( javaClass ) )
            throw new MappingException( "mapping.classNotConstructable", javaClass.getName() );
        _javaClass = javaClass;
        if ( fields == null )
            throw new IllegalArgumentException( "Argument 'fields' is null" );
        setFields( fields );
        setRelations( relations );

        if ( extend != null ) {
            if ( ! extend.getJavaClass().isAssignableFrom( javaClass ) )
                throw new MappingException( "mapping.classDoesNotExtend",
                                            _javaClass.getName(), extend._javaClass.getName() );
            _extends = extend;
            _identity = ( identity == null ? _extends._identity : identity );
        } else
            _identity = identity;
        _accessMode = ( accessMode == null ? AccessMode.Shared : accessMode );
    }
    
    
    protected ClassDesc( ClassDesc clsDesc )
    {
        _javaClass = clsDesc._javaClass;
        _fields = (FieldDesc[]) clsDesc._fields.clone();
        _extends = clsDesc._extends;
        _identity = clsDesc._identity;
        _accessMode = clsDesc._accessMode;
        _relIdentity = clsDesc._relIdentity;
        _relations = (RelationDesc[]) clsDesc._relations.clone();
    }


    private ClassDesc()
    {
    }


    /**
     * Returns the Java class of this descriptor.
     *
     * @return The Java class of this descriptor
     */
    public Class getJavaClass()
    {
        return _javaClass;
    }


    /**
     * Returns the fields described for this class. An array of field
     * descriptors is returned, allowing the set/get methods to be
     * called on each field against an object of this class. The returned
     * array may be of size zero.
     *
     * @return The fields described for this class
     */
    public FieldDesc[] getFields()
    {
        return _fields;
    }


    /**
     * Returns the descriptor of the class which this class extends.
     * Returns null if this is a top-level class.
     *
     * @return The descriptor of the extended class, or null
     */
    public ClassDesc getExtends()
    {
        return _extends;
    }


    /**
     * Returns all the relation descriptors for this class.
     * The returned array may be of size zero.
     *
     * @return All the relation descriptors
     */
    public RelationDesc[] getRelations()
    {
        return _relations;
    }


    /**
     * Returns the default access mode for this class.
     *
     * @return The default access mode
     */
    public AccessMode getAccessMode()
    {
        return _accessMode;
    }


    /**
     * Constructs a new object of this class. Does not generate any
     * exceptions, since object creation has been proven to work when
     * creating descriptor from mapping.
     *
     * @return A new instance of this class
     */
    public Object newInstance()
    {
        return Types.newInstance( _javaClass );
    }

    
    /**
     * Returns the identity field for this class. Not all classes have an
     * identity field. Identity fields are used to persist objects, manage
     * relations and identity uniqueness.
     *
     * @return The identity field of this class, or null
     */
    public FieldDesc getIdentity()
    {
        return _identity;
    }


    /**
     * Returns the indentity value of this field. This method is used with
     * regards to attached relation, where the actual identity value is
     * obtained from the object to which this field relates and which this
     * field's identity points to. This method will work for both regular
     * descriptors and attached relations.
     *
     * @param obj The object
     * @return The identity value
     */
    public Object getIdentity( Object obj )
    {
        if ( _relIdentity == null )
            return _identity.getValue( obj );
        else {
            // Get the related object and it's identity;
            obj = _identity.getValue( obj );
            return _relIdentity.getValue( obj );
        }
    }


    /**
     * Copy values from the source object to the target object. Will
     * copy all the fields, and related objects that are specified in
     * the object mapping, but not copy the identity field.
     * <p>
     * The relation context determines how related fields are copied.
     * When copying into the cache, no relation context is necessary
     * and the cache will only contain a copy of the relation using
     * the identity field. When copying from the cache, the relation
     * context is required and will be used to obtain an object
     * matching the cached identity field.
     *
     * @param source The source object
     * @param target The target object
     * @param rctx The relation context, or null
     * @throws PersistenceException An error loading the related object
     */
    public void copyInto( Object source, Object target, RelationContext rctx )
        throws PersistenceException
    {
        // Copy/clone all the fields.
        if ( _extends != null )
            _extends.copyInto( source, target, rctx );
        for ( int i = 0 ; i < _fields.length ; ++i )
            _fields[ i ].copyInto( source, target );
        if ( _identity != null )
            _identity.copyInto( source, target );

        for ( int i = 0 ; i < _relations.length ; ++i ) {
            if ( _relations[ i ].isAttached() ) {
                // Attached relation -- need to copy the entire object and
                // make sure related object's identity points back to the
                // object.
                Object relSource;
                Object relTarget;

                relSource = _relations[ i ].getRelationField().getValue( source );
                relTarget = _relations[ i ].getRelatedClassDesc().newInstance();
                _relations[ i ].getRelatedClassDesc().copyInto( relSource, relTarget, rctx );
                _relations[ i ].getRelationField().setValue( target, relTarget );
                _relations[ i ].getRelatedClassDesc().getIdentity().setValue( relTarget, target );
            } else {
                // Detached relation -- 
                Object relSource;
                Object relTarget;

                relSource = _relations[ i ].getRelationField().getValue( source );
                if ( rctx == null ) {
                    relTarget = _relations[ i ].getRelatedClassDesc().newInstance();
                    _relations[ i ].getRelationField().setValue( target, relTarget );
                    _relations[ i ].getRelatedClassDesc().getIdentity().copyInto( relSource, relTarget );
                } else {
                    relTarget = rctx.fetch( _relations[ i ].getRelatedClassDesc(),
                                            _relations[ i ].getRelatedClassDesc().getIdentity().getValue( relSource ) );
                    _relations[ i ].getRelationField().setValue( target, relTarget );
                }
            }
        }
    } 


    /**
     * Determines if the object can be stored. Returns successfully if
     * the object can be stored. Throws an exception if a required
     * field is null, the identity is null, a relation is broken, etc.
     *
     * @param obj The object
     * @throws IntegrityException Cannot store object due to
     *  integrity violation
     */
    public void canStore( Object obj )
        throws IntegrityException
    {
        // Handle fields in the parent class.
        if ( _extends != null )
            _extends.canStore( obj );

        // Object cannot be saved if one of the required fields is null
        for ( int i = 0 ; i < _fields.length ; ++i )
            _fields[ i ].canStore( obj );
        // Object cannot be saves without identity
        if ( _identity == null )
            throw new IntegrityException( "mapping.noIdentity",
                                          obj.getClass().getName() );
        else
            _identity.canStore( obj );

        // Object cannot be saved if one of the relations is broken.
        for ( int i = 0 ; i < _relations.length ; ++i )
            _relations[ i ].canStore( obj );
    }


    /**
     * Determines if the object has been modified from its original
     * cached value. Returns true if the object has been modified.
     * This method does not check whether the identity has been
     * modified.
     *
     * @param obj The object
     * @param cached The cached copy
     * @return True if the object has been modified
     */
    public boolean isModified( Object obj, Object cached )
    {
        if ( _extends != null && _extends.isModified( obj, cached ) )
            return true;
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[ i ].isModified( obj, cached ) )
                return true;
        }
        for ( int i = 0 ; i < _relations.length ; ++i ) {
            if ( _relations[ i ].isModified( obj, cached ) )
                return true;
        }
        return false;
    }
    
    
    public String toString()
    {
        return "Mapping for class " + _javaClass.getName();
    }


    /**
     * Mutator method can only be used by {@link MappingHelper}.
     */
    final protected void setFields( FieldDesc[] fields )
    {
        _fields = ( fields == null ? new FieldDesc[ 0 ] : (FieldDesc[]) fields.clone() );
    }


    /**
     * Mutator method can only be used by {@link MappingHelper}.
     */
    final void setRelations( RelationDesc[] relations )
    {
        _relations = ( relations == null ? new RelationDesc[ 0 ] : (RelationDesc[]) relations.clone() );
    }


    /**
     * Mutator method can only be used by {@link MappingHelper}.
     */
    final protected void setRelatedIdentity( FieldDesc identity )
    {
        if ( identity == null )
            throw new IllegalArgumentException( "Argument 'identity' is null" );
        _relIdentity = identity;
    }


}


