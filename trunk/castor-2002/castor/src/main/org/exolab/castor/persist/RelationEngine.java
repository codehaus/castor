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


import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Enumeration;
import org.exolab.castor.mapping.ClassDesc;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.RelationDesc;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.util.Messages;


/**
 * Implements the object cache engine sitting between a persistence engine
 * and persistence storage SPI.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
final class RelationEngine
    implements PersistenceEngine
{


    private PersistenceEngine  _engine;


    private PersistenceEngine  _topEngine;


    private PrintWriter        _logWriter;


    /**
     * Construct a new relations engine with the specified name, mapping
     * table and persistence engine.
     *
     * @param topEngine The top level persistence engine
     * @param mapResolver Provides mapping information for objects
     *  supported by this cache
     * @param factory Factory for creating persistence engines for each
     *  object described in the map
     * @param logWriter Log writer to use for cache and all its
     *  persistence engines
     * @throws MappingException Indicate that one of the mappings is
     *  invalid
     */
    RelationEngine( PersistenceEngine topEngine, MappingResolver mapResolver,
                    PersistenceFactory factory, PrintWriter logWriter )
        throws MappingException
    {
        Enumeration enum;
        ClassDesc  clsDesc;
        Persistence persist;
        
        _topEngine = ( topEngine == null ? this : topEngine );
        _logWriter = logWriter;
        enum = mapResolver.listDescriptors();
        while ( enum.hasMoreElements() ) {
            clsDesc = (ClassDesc) enum.nextElement();
        }
        _engine = new CacheEngine( _topEngine, mapResolver, factory, logWriter );
    }


    public Persistence getPersistence( Class type )
    {
        return _engine.getPersistence( type );
    }


    public ClassDesc getClassDesc( Class type )
    {
        return _engine.getClassDesc( type );
    }


    public OID load( TransactionContext tx, Class type, Object identity,
                     AccessMode accessMode, int timeout )
        throws ObjectNotFoundException, LockNotGrantedException,
               PersistenceException, ClassNotPersistenceCapableException
    {
        return _engine.load( tx, type, identity, accessMode, timeout );
    } 


    public OID fetch( TransactionContext tx, PersistenceQuery query, Object identity,
                      AccessMode accessMode, int timeout )
        throws ObjectNotFoundException, LockNotGrantedException,
               PersistenceException
    {
        return _engine.fetch( tx, query, identity, accessMode, timeout );
    }


    public OID create( TransactionContext tx, Object obj, Object identity )
        throws DuplicateIdentityException, PersistenceException,
               ClassNotPersistenceCapableException
    {
        RelationDesc[] relations;
        ClassDesc      clsDesc;

        clsDesc = _engine.getClassDesc( obj.getClass() );
        relations = _engine.getClassDesc( obj.getClass() ).getRelations();
        for ( int i = 0 ; i < relations.length ; ++i ) {
            Object related;
            Object relatedId;

            related = relations[ i ].getRelationField().getValue( obj );
            if ( related != null && ! tx.isPersistent( related ) ) {
                if ( relations[ i ].isAttached() )
                    relatedId = identity;
                else
                    relatedId = relations[ i ].getRelatedClassDesc().getIdentity().getValue( related );
                tx.create( this, related, relatedId );
            }
        }
        return _engine.create( tx, obj, identity );
    }
    
    
    public void delete( TransactionContext tx, Object obj, Object identity )
        throws PersistenceException
    {
        RelationDesc[] relations;

        relations = _engine.getClassDesc( obj.getClass() ).getRelations();
        for ( int i = 0 ; i < relations.length ; ++i ) {
            Object related;

            related = relations[ i ].getRelationField().getValue( obj );
            if ( related != null )
                tx.delete( related );
        }
        _engine.delete( tx, obj, identity );
    }


    public OID store( TransactionContext tx, Object obj,
                      Object identity, int timeout )
        throws LockNotGrantedException, ObjectDeletedException,
               ObjectModifiedException, DuplicateIdentityException,
               PersistenceException
    {
        return _engine.store( tx, obj, identity, timeout );
    }
    

    public void writeLock( TransactionContext tx, OID oid, int timeout )
        throws LockNotGrantedException, ObjectDeletedException, PersistenceException
    {
        _engine.writeLock( tx, oid, timeout );
    }


    public void copyObject( TransactionContext tx, OID oid, Object obj )
        throws PersistenceException
    {
        _engine.copyObject( tx, oid, obj );
    }


    public void updateObject( TransactionContext tx, OID oid, Object obj )
    {
        _engine.updateObject( tx, oid, obj );
    }


    public void releaseLock( TransactionContext tx, OID oid )
    {
        _engine.releaseLock( tx, oid );
    }


    public void forgetObject( TransactionContext tx, OID oid )
    {
        _engine.forgetObject( tx, oid );
    }


    public Hashtable getXATransactions()
    {
        return _engine.getXATransactions();
    }


}


