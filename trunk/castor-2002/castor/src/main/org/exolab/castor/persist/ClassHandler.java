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


package org.exolab.castor.persist;


import java.util.Vector;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.ValidityException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.loader.IndirectFieldHandler;
import org.exolab.castor.util.Messages;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see ClassDescriptor
 */
public class ClassHandler
{


    private ClassDescriptor   _clsDesc;


    private ClassHandler      _extends;


    private FieldInfo[]       _fields;


    private FieldInfo         _identity;


    private FieldHandler      _relIdentity;


    private RelationHandler[] _relations;


    ClassHandler( ClassDescriptor clsDesc )
    {
        _clsDesc = clsDesc;
    }


    void normalize( CacheEngine cache )
        throws MappingException
    {
        Vector            rels;
        Vector            fields;

        if ( _clsDesc.getExtends() != null ) {
            _extends = new ClassHandler( _clsDesc.getExtends() );
            _extends.normalize( cache );
        }

        if ( _clsDesc.getIdentity() != null )
            _identity = new FieldInfo( _clsDesc.getIdentity(), null );
        rels = new Vector();
        fields = new Vector();
        addFields( cache, _clsDesc, fields, rels );

        _fields = new FieldInfo[ fields.size() ];
        fields.copyInto( _fields );
        _relations = new RelationHandler[ rels.size() ];
        rels.copyInto( _relations );
    }


    private void addFields( CacheEngine cache, ClassDescriptor clsDesc, Vector fields, Vector rels )
        throws MappingException
    {
        FieldDescriptor[] descs;

        if ( clsDesc.getExtends() != null )
            addFields( cache, clsDesc.getExtends(), fields, rels );
        descs = clsDesc.getFields();
        for ( int i = 0 ; i < descs.length ; ++i ) {
            ClassHandler clsHandler;

            clsHandler = cache.addClassHandler( descs[ i ].getFieldType() );
            if ( clsHandler == null ) {
                fields.addElement( new FieldInfo( descs[ i ], null ) );
                rels.addElement( null );
            } else {
                FieldHandler    handler;
                RelationHandler relHandler;

                if ( descs[ i ].getHandler() instanceof IndirectFieldHandler )
                    handler = ( (IndirectFieldHandler) descs[ i ].getHandler() ).getHandler();
                else
                    handler = descs[ i ].getHandler();
                relHandler = new RelationHandler( descs[ i ].getFieldName(), handler, clsHandler );
                fields.addElement( new FieldInfo( descs[ i ], relHandler ) );
                rels.addElement( relHandler );
            }
        }
    }


    public int getFieldCount()
    {
        return _fields.length;
    }

                      
    /**
     * Returns the access mode for this class.
     *
     * @return The access mode
     */
    public AccessMode getAccessMode()
    {
        return _clsDesc.getAccessMode();
    }


    /**
     * Returns the suitable access mode. If <tt>txMode</tt> is null,
     * return the access mode defined for the object. Otherwise, the
     * following rules apply (in that order):
     * <ul>
     * <li>If the class is defined as read/only the access mode is
     *  read/only
     * <li>If the transaction is defined as read/only the access mode
     *  is read/only
     * <li>If the class is defined as exclusive the access mode is
     *  exclusive
     * <li>If the transaction is defined as exclusive the access mode
     *  is exclusive
     * <li>The transaction mode is used
     * </ul>
     *
     * @param txMode The transaction mode, or null
     * @return The suitable access mode
     */
    public AccessMode getAccessMode( AccessMode txMode )
    {
        AccessMode clsMode;

        if ( txMode == null )
            return getAccessMode();
        clsMode = getAccessMode();
        if ( clsMode == AccessMode.ReadOnly || txMode == AccessMode.ReadOnly )
            return AccessMode.ReadOnly;
        if ( clsMode == AccessMode.Exclusive || txMode == AccessMode.Exclusive )
            return AccessMode.Exclusive;
        return txMode;
    }


    public Class getJavaClass()
    {
        return _clsDesc.getJavaClass();
    }


    public ClassDescriptor getDescriptor()
    {
        return _clsDesc;
    }


    public RelationHandler[] getRelations()
    {
        return _relations;
    }


    public ClassHandler getExtends()
    {
        return _extends;
    }


    public FieldHandler[] getFields()
    {
        FieldHandler[] fields;

        fields = new FieldHandler[ _fields.length ];
        for ( int i = 0 ; i < _fields.length ; ++i )
            fields[ i ] = _fields[ i ].handler;
        return fields;
    }


    /**
     * Constructs a new object of this class. Does not generate any
     * exceptions, since object creation has been proven to work when
     * creating descriptor from mapping.
     *
     * @return A new instance of this class
     * @throws IllegalStateException The Java object has changed and
     *  is no longer supported by this descriptor, or the descriptor
     *  is not compatiable with the Java object
     */
    public Object newInstance()
        throws IllegalStateException
    {
        return Types.newInstance( _clsDesc.getJavaClass() );
    }

    
    public Object getIdentity( Object object )
    {
        if ( _relIdentity == null )
            return _identity.handler.getValue( object );
        else {
            // Get the related object and it's identity;
            object = _identity.handler.getValue( object );
            return _relIdentity.getValue( object );
        }
    }


    public void setIdentity( Object object, Object identity )
    {
        _identity.handler.setValue( object, identity );
    }


    /**
     * Copy values from the source object to the target object. Will
     * copy all the fields, and related objects that are specified in
     * the object mapping.
     * <p>
     * The fetch context determines how related fields are copied.
     * When copying into the cache, no fetch context is necessary
     * and the cache will only contain a copy of the relation using
     * the identity field. When copying from the cache, the fetch
     * context is required and will be used to obtain an object
     * matching the cached identity field.
     *
     * @param source The source object
     * @param target The target object
     * @param ctx The fetch context, or null
     * @throws PersistenceException An error fetching the related object
     */
    public void copyInto( Object[] fields, Object target, FetchContext ctx )
        throws PersistenceException
    {
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[ i ].relation == null )
                _fields[ i ].handler.setValue( target, copyValue( _fields[ i ], fields[ i ] ) );
            else {
                Object relSource;
                Object relTarget;
            
                if ( fields[ i ] == null )
                    _fields[ i ].relation.setRelated( target, null );
                else {
                    relTarget = ctx.fetch( _fields[ i ].relation.getRelatedHandler(),
                                           fields[ i ] );
                    if ( relTarget == null )
                        throw new ObjectNotFoundException( _fields[ i ].relation.getRelatedHandler().getJavaClass(),
                                                           fields[ i ] );
                    _fields[ i ].relation.setRelated( target, relTarget );
                }
            }
        }
    }


    public void copyInto( Object source, Object[] fields )
        throws PersistenceException
    {
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[ i ].relation == null )
                fields[ i ] = copyValue( _fields[ i ], _fields[ i ].handler.getValue( source ) );
            else {
                fields[ i ] = copyValue( _fields[ i ].relation.getRelatedHandler()._identity, 
                                                       _fields[ i ].relation.getIdentity( _fields[ i ].relation.getRelated( source ) ) );
            }
        }
    }


    /**
     * Used by {@link #copyField(Object,Object)} to copy a single field.
     */
    private Object copyValue( FieldInfo field, Object source )
    {
        // Immutable objects are copied verbatim. Cloneable objects are
        // cloned, all other fields must be serializable and are
        // serialized.
        if ( field.immutable )
            return source;
        else {
            try {
                ByteArrayOutputStream ba;
                ObjectOutputStream    os;
                ObjectInputStream     is;
                
                ba = new ByteArrayOutputStream();
                os = new ObjectOutputStream( ba );
                os.writeObject( source );
                os.flush();
                is = new ObjectInputStream( new ByteArrayInputStream( ba.toByteArray() ) );
                return is.readObject();
            } catch ( IOException except ) {
                throw new IllegalStateException( Messages.format( "mapping.schemaNotSerializable",
                                                                  field.fieldType.getName(), except.getMessage() ) );
            } catch ( ClassNotFoundException except ) {
                throw new IllegalStateException( Messages.format( "mapping.schemaNotSerializable",
                                                                  field.fieldType.getName(), except.getMessage() ) );
            }
        }
    }


    /**
     * Determines if the object has been modified from its original
     * value. Returns true if the object has been modified. This
     * method does not check whether the identity has been modified.
     *
     * @param object The object
     * @param cached The cached copy
     * @return True if the object has been modified
     */
    public boolean isModified( Object object, Object[] original )
    {
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( isModified( _fields[ i ], object, original[ i ] ) )
                return true;
        }
        return false;
    }


    /**
     * Used by {@link #isModified(Object,Object[])} to check a single field.
     */
    public boolean isModified( FieldInfo field, Object object, Object original )
    {
        Object value;

        value = field.handler.getValue( object );
        if ( value == null )
            return ( original == null );
        else
            return value.equals( original );
    }


    public void checkValidity( Object object )
        throws ValidityException
    {
        // Object cannot be saved if one of the required fields is null
        for ( int i = 0 ; i < _fields.length ; ++i )
            _fields[ i ].handler.checkValidity( object );
    }


    public String toString()
    {
        return _clsDesc.toString();
    }


    final static class FieldInfo
    {

        final FieldHandler  handler;

        final Class         fieldType;

        final String        fieldName;

        final boolean       immutable;

        final boolean       required;

        final RelationHandler relation;

        FieldInfo( FieldDescriptor fieldDesc, RelationHandler relation )
        {
            this.fieldName = fieldDesc.getFieldName();
            this.fieldType = fieldDesc.getFieldType();
            this.handler = fieldDesc.getHandler();
            this.immutable = fieldDesc.isImmutable();
            this.required = fieldDesc.isRequired();
            this.relation = relation;
        }

    }


}





