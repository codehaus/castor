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
import org.exolab.castor.mapping.IntegrityException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.desc.Types;
import org.exolab.castor.mapping.desc.IndirectFieldHandler;
import org.exolab.castor.util.Messages;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see ClassDesc
 */
public class ClassHandler
{


    private ClassDescriptor   _clsDesc;


    private ClassHandler      _extends;


    private FieldInfo[]       _fields;


    private RelationHandler[] _relations;


    private FieldInfo         _identity;


    private FieldHandler      _relIdentity;


    ClassHandler( ClassDescriptor clsDesc, MappingResolver mapResolver )
    {
        _clsDesc = clsDesc;
        if ( _clsDesc.getExtends() != null )
            _extends = new ClassHandler( _clsDesc.getExtends(), mapResolver );
        if ( _clsDesc.getIdentity() != null ) {
            _identity = new FieldInfo( _clsDesc.getIdentity() );
        }

        Vector            rels;
        Vector            fields;
        FieldDescriptor[] descs;

        rels = new Vector();
        fields = new Vector();
        descs = clsDesc.getFields();
        for ( int i = 0 ; i < descs.length ; ++i ) {
            ClassDescriptor relDesc;

            relDesc = mapResolver.getDescriptor( descs[ i ].getFieldType() );
            if ( relDesc == null )
                fields.addElement( new FieldInfo( descs[ i ] ) );
            else {
                FieldHandler handler;
                if ( descs[ i ].getHandler() instanceof IndirectFieldHandler )
                    handler = ( (IndirectFieldHandler) descs[ i ].getHandler() ).getHandler();
                else
                    handler = descs[ i ].getHandler();
                rels.addElement( new RelationHandler( handler, new ClassHandler( relDesc, mapResolver ) ) );
            }
        }
        _fields = new FieldInfo[ fields.size() ];
        fields.copyInto( _fields );
        _relations = new RelationHandler[ rels.size() ];
        rels.copyInto( _relations );
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
    public void copyInto( Object source, Object target, FetchContext ctx )
        throws PersistenceException
    {
        // Copy/clone all the fields.
        if ( _extends != null )
            _extends.copyInto( source, target, ctx );

        for ( int i = 0 ; i < _fields.length ; ++i )
            copyField( _fields[ i ], source, target );
        if ( _identity != null )
            copyField( _identity, source, target );

        for ( int i = 0 ; i < _relations.length ; ++i ) {
            Object relSource;
            Object relTarget;
            
            relSource = _relations[ i ].getRelated( source );
            if ( ctx == null ) {
                relTarget = _relations[ i ].newInstance();
                _relations[ i ].setRelated( target, relTarget );
                copyField( _relations[ i ].getRelatedHandler()._identity, relSource, relTarget );
            } else {
                relTarget = ctx.fetch( _relations[ i ].getRelatedHandler(),
                                       _relations[ i ].getIdentity( relSource ) );
                _relations[ i ].setRelated( target, relTarget );
            }
        }

        /*
        for ( int i = 0 ; i < _relations.length ; ++i ) {
            if ( _relations[ i ].isAttached() ) {
                // Attached relation -- need to copy the entire object and
                // make sure related object's identity points back to the
                // object.
                Object relSource;
                Object relTarget;

                relSource = _relations[ i ].getRelationField().getValue( source );
                relTarget = _relations[ i ].getRelatedClass().getHandler().newInstance();
                _relations[ i ].getRelatedClass().getHandler().copyInto( relSource, relTarget, ctx );
                _relations[ i ].getRelationField().setValue( target, relTarget );
                _relations[ i ].getRelatedClass().getIdentity().getHandler().setValue( relTarget, target );
            } else {
                // Detached relation -- 
                Object relSource;
                Object relTarget;

                relSource = _relations[ i ].getRelationField().getValue( source );
                if ( ctx == null ) {
                    relTarget = _relations[ i ].getRelatedClass().getHandler().newInstance();
                    _relations[ i ].getRelationField().setValue( target, relTarget );
                    _relations[ i ].getRelatedClass().getIdentity().getHandler().copyInto( relSource, relTarget );
                } else {
                    relTarget = ctx.fetch( _relations[ i ].getRelatedClass(),
                                           _relations[ i ].getRelatedClass().getIdentity().getHandler().getValue( relSource ) );
                    _relations[ i ].getRelationField().setValue( target, relTarget );
                }
            }
        }
        */
    }


    /**
     * Used by {@link #copyField(Object,Object)} to copy a single field.
     */
    private void copyField( FieldInfo field, Object source, Object target )
    {
        // Immutable objects are copied verbatim. Cloneable objects are
        // cloned, all other fields must be serializable and are
        // serialized.
        if ( field.immutable )
            field.handler.setValue( target, field.handler.getValue( source ) );
        else {
            try {
                ByteArrayOutputStream ba;
                ObjectOutputStream    os;
                ObjectInputStream     is;
                
                ba = new ByteArrayOutputStream();
                os = new ObjectOutputStream( ba );
                os.writeObject( field.handler.getValue( source ) );
                os.flush();
                is = new ObjectInputStream( new ByteArrayInputStream( ba.toByteArray() ) );
                field.handler.setValue( target, is.readObject() );
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
    public boolean isModified( Object object, Object original )
    {
        if ( _extends != null && _extends.isModified( object, original ) )
            return true;

        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( isModified( _fields[ i ], object, original ) )
                return true;
        }
        /*
        for ( int i = 0 ; i < _relations.length ; ++i ) {
            if ( _relations[ i ].isModified( object, original ) )
                return true;
        }
        */
        return false;
    }


    /**
     * Used by {@link #isModified(Object,Object)} to check a single field.
     */
    public boolean isModified( FieldInfo field, Object object, Object original )
    {
        Object value;

        value = field.handler.getValue( object );
        if ( value == null )
            return ( field.handler.getValue( original ) == null );
        else
            return value.equals( field.handler.getValue( original ) );
    }


    public void checkIntegrity( Object object )
        throws IntegrityException
    {
        // Handle fields in the parent class.
        if ( _extends != null )
            _extends.checkIntegrity( object );
        
        // Object cannot be saved if one of the required fields is null
        for ( int i = 0 ; i < _fields.length ; ++i )
            _fields[ i ].handler.checkIntegrity( object );

        // XXX Check integrity on relations here
    }


    public String toString()
    {
        return _clsDesc.toString();
    }


    static class FieldInfo
    {

        final FieldHandler  handler;

        final Class         fieldType;

        final String        fieldName;

        final boolean       immutable;

        final boolean       required;

        FieldInfo( FieldDescriptor fieldDesc )
        {
            fieldName = fieldDesc.getFieldName();
            fieldType = fieldDesc.getFieldType();
            handler = fieldDesc.getHandler();
            immutable = fieldDesc.isImmutable();
            required = fieldDesc.isRequired();
        }

    }


}





