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
import org.exolab.castor.mapping.IntegrityException;
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
final class CacheEngine
    implements PersistenceEngine
{


    /**
     * IMPLEMENTATION NOTES:
     *
     * An object may be persistent in multiple caches at any given
     * time. There is no way to load an object from multiple caches,
     * but an object can be loaded in one engine and then made
     * persistent in another. The engines are totally independent and
     * no conflicts should occur.
     *
     */


    /**
     * Mapping of type information to object types. The object's class is used
     * as the key and {@link TypeInfo} is the value. {@link TypeInfo} provides
     * sufficient information to persist the object, manipulated it in memory
     * and invoke the object's interceptor.
     */
    private Hashtable _typeInfo = new Hashtable();


    /**
     * Mapping of OIDs to objects. The object is used as the key, and
     * {@link OID} is the value.
     */
    private Hashtable  _oids = new Hashtable();


    /**
     * Mapping of object locks to OIDs. The {@link OID} is used as the
     * key, and {@link ObjectLock} is the value. There is one lock per OID.
     */
    private Hashtable _locks = new Hashtable();


    /**
     * All the XA transactions running against this cache engine.
     */
    private Hashtable _xaTx = new Hashtable();


    /**
     * The log writer used to trace persistence operations. May be null.
     */
    private PrintWriter        _logWriter;


    /**
     * Construct a new cache engine with the specified name, mapping
     * table and persistence engine.
     *
     * @param mapResolver Provides mapping information for objects
     *  supported by this cache
     * @param factory Factory for creating persistence engines for each
     *  object described in the map
     * @param logWriter Log writer to use for cache and all its
     *  persistence engines
     * @throws MappingException Indicate that one of the mappings is
     *  invalid
     */
    CacheEngine( MappingResolver mapResolver,
                 PersistenceFactory factory, PrintWriter logWriter )
        throws MappingException
    {
        Enumeration enum;
        ClassDesc  clsDesc;
        Persistence persist;
        
        _logWriter = logWriter;
        enum = mapResolver.listDescriptors();
        while ( enum.hasMoreElements() ) {
            clsDesc = (ClassDesc) enum.nextElement();
            persist = factory.getPersistence( clsDesc, logWriter );
            if ( persist != null )
                _typeInfo.put( clsDesc.getJavaClass(), new TypeInfo( persist, clsDesc ) );
            else if ( _logWriter != null ) {
                _logWriter.println( Messages.format( "persist.noEngine", clsDesc.getJavaClass() ) );
            }
        }
    }


    public Persistence getPersistence( Class type )
    {
        TypeInfo typeInfo;
        
        typeInfo = (TypeInfo) _typeInfo.get( type );
        if ( typeInfo == null )
            return null;
        else
            return typeInfo.persist;
    }


    public ClassDesc getClassDesc( Class type )
    {
        TypeInfo typeInfo;
        
        typeInfo = (TypeInfo) _typeInfo.get( type );
        if ( typeInfo == null )
            return null;
        else
            return typeInfo.clsDesc;
    }


    public PrintWriter getLogWriter()
    {
        return _logWriter;
    }


    /**
     * Loads an object of the specified type and identity from
     * persistent storage. In exclusive mode the object is always
     * loaded and a write lock is obtained on the object, preventing
     * concurrent updates. In non-exclusive mode the object is either
     * loaded or obtained from the cache with a read lock. The object's
     * OID is always returned, this OID must be used in subsequent
     * operations on the object. Must call {@link #acquire} to obtain
     * the object.
     *
     * @param tx The transaction context
     * @param type The type of the object to load
     * @param identity The identity of the object to load
     * @param accessMode The desired access mode
     * @param timeout The timeout waiting to acquire a lock on the
     *  object (specified in seconds)
     * @return The object's OID
     * @throws ObjectNotFoundException The object was not found in
     *  persistent storage
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws PersistenceException An error reported by the
     *  persistence engine
     * @throws ClassNotPersistenceCapableException The class is not
     *  persistent capable
     */
    public OID load( TransactionContext tx, Class type, Object identity,
                     AccessMode accessMode, int timeout )
        throws ObjectNotFoundException, LockNotGrantedException,
               PersistenceException, ClassNotPersistenceCapableException
    {
        Object     obj;
        OID        oid;
        ObjectLock lock;
        TypeInfo   typeInfo;
        
        typeInfo = (TypeInfo) _typeInfo.get( type );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( type );
        
        // Create an OID to represent the object and see if we
        // have a lock (i.e. object is cached).
        oid = new OID( typeInfo.clsDesc, identity );
        lock = getLock( oid );
        
        if ( lock != null ) {
            
            // Object has been loaded before, must acquire lock
            // on it (write in exclusive mode)
            try {
                obj = lock.acquire( tx, accessMode == AccessMode.Exclusive, timeout );
            } catch ( ObjectDeletedWaitingForLockException except ) {
                // This is equivalent to object not existing
                throw new ObjectNotFoundException( type, identity );
            }
            // Get the actual OID of the object, this one contains the
            // object's stamp that will be used for dirty checking.
            oid = getOID( obj );
            
            // XXX Problem, obj might be parent class but attempting
            //     to load derived class, will still return parent class
            //     Need to solve by swapping to a new object
            
            if ( accessMode == AccessMode.Exclusive && ! oid.isExclusive() ) {
                // Exclusive mode we always synchronize the object with
                // the database and obtain a lock on the object.
                try {
                    if ( _logWriter != null )
                        _logWriter.println( "PE: Loading " + obj.getClass().getName() +
                                            " (" + identity + ")" );
                    oid.setStamp( typeInfo.persist.load( tx.getConnection( this ),
                                                         new RelationContext( tx, this ),
                                                         obj, identity, accessMode ) );
                    oid.setExclusive( true );
                } catch ( ObjectNotFoundException except ) {
                    // Object was not found in persistent storge, must dump
                    // it from the cache
                    removeLock( oid );
                    removeOID( obj );
                    lock.delete( tx );
                    throw except;
                } catch ( PersistenceException except ) {
                    // Report any error talking to the persistence engine
                    removeLock( oid );
                    removeOID( obj );
                    lock.delete( tx );
                    throw except;
                }
                // At this point the object is known to exist in
                // persistence storage and we have a write lock on it.
                return oid;
            } else {
                // Non-exclusive mode, we do not attempt to touch the database
                // at this point, simply return the object's oid.
                return oid;
            }
            
        } else {
            
            // Object has not been loaded yet, or cleared from the cache.
            // The object is now loaded and a lock is acquired.
            obj = typeInfo.clsDesc.newInstance();
            typeInfo.clsDesc.getIdentity().setValue( obj, identity );
            try {
                if ( _logWriter != null )
                    _logWriter.println( "PE: Loading " + obj.getClass().getName() + " ("
                                        + identity + ")" );
                oid.setStamp( typeInfo.persist.load( tx.getConnection( this ),
                                                     new RelationContext( tx, this ),
                                                     obj, identity, accessMode ) );
            } catch ( ObjectNotFoundException except ) {
                // Object was not found in persistent storge
                throw except;
            } catch ( PersistenceException except ) {
                // Report any error talking to the persistence engine
                throw except;
            }
            // Create a lock for the object, register the lock and OID.
            // The lock is created for read or write depending on the
            // mode.
            lock = new ObjectLock( obj );
            try {
                lock.acquire( tx, accessMode == AccessMode.Exclusive, 0 );
            } catch ( Exception except ) {
                // This should never happen since we just created the lock
            }
            if ( accessMode == AccessMode.Exclusive )
                oid.setExclusive( true );
            setLock( oid, lock );
            setOID( obj, oid );
            return oid;
        }
    } 


    /**
     * Loads an object of the specified type and identity from the
     * query results. In exclusive mode the object is always loaded
     * and a write lock is obtained on the object, preventing
     * concurrent updates. In non-exclusive mode the object is either
     * loaded or obtained from the cache with a read lock. The object's
     * OID is always returned, this OID must be used in subsequent
     * operations on the object. Must call {@link #acquire} to obtain
     * the object.
     *
     * @param tx The transaction context
     * @param query The query persistence engine
     * @param identity The identity of the object to load
     * @param accessMode The desired access mode
     * @param timeout The timeout waiting to acquire a lock on the
     *  object (specified in seconds)
     * @return The object's OID
     * @throws ObjectNotFoundException The object was not found in
     *  persistent storage
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public OID fetch( TransactionContext tx, PersistenceQuery query, Object identity,
                      AccessMode accessMode, int timeout )
        throws ObjectNotFoundException, LockNotGrantedException,
               PersistenceException
    {
        Object     obj;
        OID        oid;
        ObjectLock lock;
        TypeInfo   typeInfo;
        
        typeInfo = (TypeInfo) _typeInfo.get( query.getResultType() );
        // Create an OID to represent the object and see if we
        // have a lock (i.e. object is cached).
        oid = new OID( typeInfo.clsDesc, identity );
        lock = getLock( oid );
        
        if ( lock != null ) {

            // Object has been loaded before, must acquire lock on it
            try {
                obj = lock.acquire( tx, false, timeout );
            } catch ( ObjectDeletedWaitingForLockException except ) {
                // This is equivalent to object not existing
                throw new ObjectNotFoundException( query.getResultType(), identity );
            }
            // Get the actual OID of the object, this one contains the
            // object's stamp that will be used for dirty checking.
            oid = getOID( obj );
            
            // XXX Problem, obj might be parent class but attempting
            //     to load derived class, will still return parent class
            //     Need to solve by swapping to a new object
            
            if ( accessMode == AccessMode.Exclusive && ! oid.isExclusive() ) {
                // Exclusive mode we always synchronize the object with
                // the database and obtain a lock on the object.
                try {
                    oid.setStamp( query.fetch( obj ) );
                    oid.setExclusive( true );
                } catch ( ObjectNotFoundException except ) {
                    // Object was not found in persistent storge, must dump
                    // it from the cache
                    removeLock( oid );
                    removeOID( obj );
                    lock.delete( tx );
                    throw except;
                } catch ( PersistenceException except ) {
                    // Report any error talking to the persistence engine
                    removeLock( oid );
                    removeOID( obj );
                    lock.delete( tx );
                    throw except;
                }
                // At this point the object is known to exist in
                // persistence storage and we have a write lock on it.
                return oid;
            } else {
                // Non-exclusive mode, we do not attempt to touch the database
                // at this point, simply return the object's oid.
                return oid;
            }
            
        } else {
            
            // Object has not been loaded yet, or cleared from the cache.
            // The object is now loaded from the query and a lock is acquired.
            obj = typeInfo.clsDesc.newInstance();
            typeInfo.clsDesc.getIdentity().setValue( obj, identity );
            try {
                oid.setStamp( query.fetch( obj ) );
            } catch ( PersistenceException except ) {
                // Report any error talking to the persistence engine
                throw except;
            }
            // Create a lock for the object, register the lock and OID.
            // The lock is created for read or write depending on the
            // mode.
            lock = new ObjectLock( obj );
            try {
                lock.acquire( tx, accessMode == AccessMode.Exclusive, 0 );
            } catch ( Exception except ) {
                // This should never happen since we just created the lock
            }
            if ( accessMode == AccessMode.Exclusive )
                oid.setExclusive( true );
            setLock( oid, lock );
            setOID( obj, oid );
            return oid;
        }
    }


    /**
     * Creates a new object in this engine. The object must not be
     * persistent and must have a unique identity within this engine.
     * If the identity is specified the object is created in
     * persistent storage immediately with the identity. If the
     * identity is not specified, the object is created only when the
     * transaction commits. The object's OID is returned. The OID is
     * guaranteed to be unique for this engine even if no identity was
     * specified.
     *
     * @param tx The transaction context
     * @param obj The newly created object
     * @param identity The identity of the object, or null
     * @return The object's OID
     * @throws DuplicateIdentityException An object with this identity
     *  already exists in persistent storage
     * @throws PersistenceException An error reported by the
     *  persistence engine
     * @throws ClassNotPersistenceCapableException The class is not
     *  persistent capable
     */
    public OID create( TransactionContext tx, Object obj, Object identity )
        throws DuplicateIdentityException, PersistenceException,
               ClassNotPersistenceCapableException
    {
        OID        oid;
        ObjectLock lock;
        Object     locked;
        TypeInfo   typeInfo;
        
        typeInfo = (TypeInfo) _typeInfo.get( obj.getClass() );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( obj.getClass() );

        // Create all the dependent objects first. Must perform that
        // operation on all descendent classes.
        RelationDesc[] relations;
        ClassDesc      clsDesc;

        clsDesc = typeInfo.clsDesc;
        while ( clsDesc != null ) {
            relations = clsDesc.getRelations();
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
            clsDesc = clsDesc.getExtends();
        }

        // Must prevent concurrent attempt to create the same object
        // Best way to do that is through the type
        synchronized ( typeInfo ) {
            oid = new OID( typeInfo.clsDesc, identity );
            if ( identity != null ) {
                // If the object has a known identity at creation time, perform
                // duplicate identity check. Otherwise, create the object in
                // persistent storage acquiring a lock on the object.
                lock = getLock( oid );
                if ( lock != null ) {
                    try {
                        locked = lock.acquire( tx, true, 0 );
                    } catch ( LockNotGrantedException except ) {
                        // Someone else is using the object, definite duplicate key
                        throw new DuplicateIdentityException( obj.getClass(), identity );
                    }
                    // Dump the memory image of the object, it might have been deleted
                    // from persistent storage
                    removeLock( oid );
                    removeOID( obj );
                    lock.delete( tx );
                }
                if ( _logWriter != null )
                    _logWriter.println( "PE: Creating " + obj.getClass().getName() + " ("
                                        + identity + ")" );
                oid.setStamp( typeInfo.persist.create( tx.getConnection( this ), obj, identity ) );
                oid.setExclusive( true );
            }

            try {
                typeInfo.clsDesc.canStore( obj );
            } catch ( IntegrityException except ) {
                throw new PersistenceException( except );
            }
            
            // Copy the contents of the object we just created into the
            // cache engine. This copy will be deleted if the transaction
            // ends up rolling back.
            locked = typeInfo.clsDesc.newInstance();
            typeInfo.clsDesc.copyInto( obj, locked, null );
            typeInfo.clsDesc.getIdentity().copyInto( obj, locked );
            lock = new ObjectLock( locked );
            try {
                lock.acquire( tx, true, 0 );
            } catch ( Exception except ) {
                // This should never happen since we just created the lock
            }
            oid.setExclusive( true );
            setLock( oid, lock );
            setOID( locked, oid );
        }
        return oid;
    }
    
    
    /**
     * Called at transaction commit time to delete the object. Object
     * deletion occurs in three distinct steps:
     * <ul>
     * <li>A write lock is obtained on the object to assure it can be
     *     deleted and the object is marked for deletion in the
     *     transaction context
     * <li>As part of transaction preparation the object is deleted
     *     from persistent storage using this method
     * <li>The object is removed from the cache when the transaction
     *     completes with a call to {@link #forgetObject}
     * </ul>
     *
     * @param tx The transaction context
     * @param obj The object to delete
     * @param identity The object's identity
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public void delete( TransactionContext tx, Object obj, Object identity )
        throws PersistenceException
    {
        ObjectLock lock;
        TypeInfo   typeInfo;
        OID        oid;
        
        typeInfo = (TypeInfo) _typeInfo.get( obj.getClass() );
        oid = new OID( typeInfo.clsDesc, identity );
        // Get the lock from the OID. Assure the object has a write
        // lock -- since this was done during the transaction, we
        // don't wait to acquire the lock.
        lock = getLock( oid );
        if ( lock == null )
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to delete object for which no lock was acquired" ) );
        try {
            obj = lock.acquire( tx, true, 0 );
        } catch ( LockNotGrantedException except ) {
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to delete object for which no lock was acquired" ) );
        }

        // Delete all the related objects as well. Detached objects are not
        // deleted when the primary object is deleted, but attached objects
        // are. These objects exist in persistent storage, but not in the cache.
        ClassDesc      clsDesc;
        RelationDesc[] relations;

        clsDesc = typeInfo.clsDesc;
        while ( clsDesc != null ) {
            relations = clsDesc.getRelations();
            for ( int i = 0 ; i < relations.length ; ++i ) {
                if ( relations[ i ].isAttached() ) {
                    Object   related;
                    TypeInfo relTypeInfo;
                    
                    related = relations[ i ].getRelationField().getValue( obj );
                    if ( related != null ) {
                        relTypeInfo = (TypeInfo) _typeInfo.get( related.getClass() );
                        if ( _logWriter != null )
                            _logWriter.println( "PE: Deleting " + related.getClass().getName() + " ("
                                                + oid.getIdentity() + ")" );
                        relTypeInfo.persist.delete( tx.getConnection( this ), related, oid.getIdentity() );
                    }
                }
            }
            clsDesc = clsDesc.getExtends();
        }

        if ( _logWriter != null )
            _logWriter.println( "PE: Deleting " + obj.getClass().getName() + " ("
                                + oid.getIdentity() + ")" );
        typeInfo.persist.delete( tx.getConnection( this ), obj, oid.getIdentity() );
    }
    

    /**
     * Called at transaction commit to store an object that has been
     * loaded during the transaction. If the object has been created
     * in this transaction but without an identity, the object will
     * be created in persistent storage. Otherwise the object will be
     * stored and dirty checking might occur in order to determine
     * whether the object is valid. The object's OID might change
     * during this process.
     *
     * @param tx The transaction context
     * @param obj The object to store
     * @param identity The object's identity
     * @param timeout The timeout waiting to acquire a lock on the
     *  object (specified in seconds)
     * @return The object's OID
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws ObjectDeletedException The object has been deleted from
     *  persistent storage
     * @throws ObjectModifiedException The object has been modified in
     *  persistent storage since it was loaded, the memory image is
     *  no longer valid
     * @throws DuplicateIdentityException An object with this identity
     *  already exists in persistent storage
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public OID store( TransactionContext tx, Object obj,
                      Object identity, int timeout )
        throws LockNotGrantedException, ObjectDeletedException,
               ObjectModifiedException, DuplicateIdentityException,
               PersistenceException
    {
        Object     locked;
        ObjectLock lock;
        Object     oldIdentity;
        TypeInfo   typeInfo;
        OID        oid;
        boolean    sameIdentity;
        
        typeInfo = (TypeInfo) _typeInfo.get( obj.getClass() );
        oid = new OID( typeInfo.clsDesc, identity );
        lock = getLock( oid );
        if ( lock == null || ! lock.hasLock( tx, false ) )
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to store object for which no lock was acquired" ) );
        
        // Must acquire a write lock on the object in order to proceed to
        // storing the object. Will wait until another transaction releases
        // its lock on the object.
        locked = lock.acquire( tx, true, timeout );
        // Get the real OID with the exclusive and stamp info.
        oid = getOID( locked );
        
        // If the object has a identity, it was retrieved/created before and
        // need only be stored. If the object has no identity, the object must
        // be created at this point.
        oldIdentity = oid.getIdentity();
        if ( oldIdentity == null ) {
            // The object has no old identity. This is an object that was
            // created during this transaction and must now be created in
            // persistent storage. A new OID is required to check for
            // duplicate identity.
            try {
                return create( tx, obj, identity );
            } catch ( ClassNotPersistenceCapableException except ) {
                throw new PersistenceException( "persist.internal", except.toString() );
            }
        } else {
            sameIdentity = identity.equals( oldIdentity );

            // Check if object has been modified, and whether it can be stored.
            if ( sameIdentity || ! typeInfo.clsDesc.isModified( obj, locked ) )
                return oid;
            try {
                typeInfo.clsDesc.canStore( obj );
            } catch ( IntegrityException except ) {
                throw new PersistenceException( except );
            }
            
            // The object has an old identity, it existed before, one need
            // to store the new contents.
            if ( _logWriter != null )
                _logWriter.println( "PE: Storing " + obj.getClass().getName() + " (" +
                                    identity + ")" );
            if ( oid.isExclusive() )
                oid.setStamp( typeInfo.persist.store( tx.getConnection( this ),
                                                      obj, oldIdentity, null, null ) );
            else
                oid.setStamp( typeInfo.persist.store( tx.getConnection( this ),
                                                      obj, oldIdentity, locked, oid.getStamp() ) );
            oid.setExclusive( false );

            if ( ! sameIdentity ) {
                // The object identity has changed, need to modify the identity
                // and then store the new object value. This requires a new OID.
                // If the transaction is rolledback, both old and new OID will be
                // removed.
                typeInfo.persist.changeIdentity( tx.getConnection( this ),
                                                 obj, oldIdentity, identity );
                removeLock( removeOID( locked ) );
                oid = new OID( typeInfo.clsDesc, identity );
                if ( getLock( oid ) != null )
                    throw new DuplicateIdentityException( obj.getClass(), identity );
                removeLock( removeOID( locked ) );
                setLock( oid, lock );
                setOID( locked, oid );
            }
        }
        return oid;
    }
    

    /**
     * Acquire a write lock on the object. A write lock assures that
     * the object exists and can be stored/deleted when the
     * transaction commits. It prevents any concurrent updates to the
     * object from this point on. However, it does not guarantee that
     * the object has not been modified in the course of the
     * transaction. For that the object must be loaded with exclusive
     * access.
     *
     * @param tx The transaction context
     * @param oid The object's OID
     * @param timeout The timeout waiting to acquire a lock on the
     *  object (specified in seconds)
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws ObjectDeletedException The object has been deleted from
     *  persistent storage
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public void writeLock( TransactionContext tx, OID oid, int timeout )
        throws LockNotGrantedException, ObjectDeletedException, PersistenceException
    {
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object     obj;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        lock = getLock( oid );
        if ( lock == null || ! lock.hasLock( tx, false ) )
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to lock object for which no lock was acquired" ) );
        
        // Attempt to obtain a lock on the database. If this attempt
        // fails, release the lock and report the exception.
        obj = lock.acquire( tx, true, timeout );
        try {
            typeInfo.persist.writeLock( tx.getConnection( this ), obj, oid.getIdentity() );
        } catch ( ObjectDeletedException except ) {
            removeLock( oid );
            removeOID( obj );
            lock.delete( tx );
            throw except;
        } catch ( PersistenceException except ) {
            removeLock( oid );
            removeOID( obj );
            lock.delete( tx );
            throw except;
        }
    }


    /**
     * Obtain a copy of the cached object give the object's OID. The
     * cached object is copied into the supplied object without
     * affecting the locks. This method is generally called after a
     * successful return from {@link #load}, the transaction is known
     * to have a read lock on the object.
     *
     * @param tx The transaction context
     * @param oid The object's oid
     * @param obj The object into which to copy
     * @throws PersistenceException An error reported by the
     *  persistence engine obtaining a dependent object
     */
    public void copyObject( TransactionContext tx, OID oid, Object obj )
        throws PersistenceException
    {
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object     locked;
        
        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        lock = getLock( oid );
        if ( lock == null )
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to copy object which is not persistent" ) );
        
        // Acquire a read lock on the object. This method is generarlly
        // called after a successful return from load(), so we don't
        // want to wait for the lock.
        try {
            locked = lock.acquire( tx, false, 0 );
            if ( ! obj.getClass().isAssignableFrom( locked.getClass() ) )
                throw new IllegalArgumentException( Messages.format( "persist.typeMismatch",
                                                                     obj.getClass(), locked.getClass() ) );
        } catch ( LockNotGrantedException except ) {
            // If this transaction has no write lock on the object,
            // something went foul.
            if ( _logWriter != null )
                _logWriter.println( Messages.format( "persist.internal", "copyObject: " + except.toString() ) );
            throw new IllegalStateException( except.toString() );
        }
        typeInfo.clsDesc.copyInto( locked, obj, new RelationContext( tx, this ) );
        typeInfo.clsDesc.getIdentity().copyInto( locked, obj );
    }


    /**
     * Update the cached object with changes done to its copy. The
     * supplied object is copied into the cached object using a write
     * lock. This method is generally called after a successful return
     * from {@link #store} and is assumed to have obtained a write
     * lock.
     *
     * @param tx The transaction context
     * @param oid The object's oid
     * @param obj The object to copy from
     */
    public void updateObject( TransactionContext tx, OID oid, Object obj )
    {
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object     locked;
        
        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        lock = getLock( oid );
        if ( lock == null )
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to copy object which is not persistent" ) );
        // Acquire a write lock on the object. This method is always
        // called after a successful return from store(), so we don't
        // need to wait for the lock
        try {
            locked = lock.acquire( tx, true, 0 );
            typeInfo.clsDesc.copyInto( obj, locked, null );
            typeInfo.clsDesc.getIdentity().copyInto( obj, locked );
        } catch ( LockNotGrantedException except ) {
            // If this transaction has no write lock on the object,
            // something went foul.
            if ( _logWriter != null )
                _logWriter.println( Messages.format( "persist.internal", "updateObject: " + except.toString() ) );
            throw new IllegalStateException( except.toString() );
        } catch ( PersistenceException except ) {
            // This should never happen
        }
    }


    /**
     * Called at transaction commit or rollback to release all locks
     * held on the object. Must be called for all objects that were
     * queried but not created within the transaction.
     *
     * @param tx The transaction context
     * @param oid The object OID
     */
    public void releaseLock( TransactionContext tx, OID oid )
    {
        ObjectLock lock;
        TypeInfo   typeInfo;
        
        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        oid.setExclusive( false );
        lock = getLock( oid );
        lock.release( tx );
    }


    /**
     * Called at transaction commit or rollback to forget an object
     * and release its locks. Must be called for all objects that were
     * created when the transaction aborts, and for all objects that
     * were deleted when the transaction completes. The transaction is
     * known to have a write lock on the object.
     *
     * @param tx The transaction context
     * @param oid The object OID
     */
    public void forgetObject( TransactionContext tx, OID oid )
    {
        ObjectLock lock;
        Object     obj;
        TypeInfo   typeInfo;
        
        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        lock = getLock( oid );
        try {
            obj = lock.acquire( tx, true, 0 );
            removeLock( oid );
            removeOID( obj );
            lock.delete( tx );
        } catch ( LockNotGrantedException except ) {
            // If this transaction has no write lock on the object,
            // something went foul.
            if ( _logWriter != null )
                _logWriter.println( Messages.format( "persist.internal", "forgetObject: " + except.toString() ) );
            throw new IllegalStateException( except.toString() );
        }
    }


    /**
     * Returns an association between Xid and transactions contexts.
     * The association is shared between all transactions open against
     * this cache engine through the XAResource interface.
     */
    public Hashtable getXATransactions()
    {
        return _xaTx;
    }


    private ObjectLock getLock( OID oid )
    {
        return (ObjectLock) _locks.get( oid );
    }


    private void setLock( OID oid, ObjectLock lock )
    {
        _locks.put( oid, lock );
    }


    private ObjectLock removeLock( OID oid )
    {
        return (ObjectLock) _locks.remove( oid );
    }


    private OID getOID( Object obj )
    {
        return (OID) _oids.get( obj );
    }


    private void setOID( Object obj, OID oid )
    {
        _oids.put( obj, oid );
    }


    private OID removeOID( Object obj )
    {
        return (OID) _oids.remove( obj );
    }


    /**
     * Provides information about an object of a specific type
     * (class). This information includes the object's descriptor and
     * lifecycle interceptor requesting notification about activities
     * that affect an object.
     */
    static class TypeInfo
    {
        
        ClassDesc   clsDesc;
        
        Persistence  persist;
        
        TypeInfo( Persistence persist, ClassDesc clsDesc )
        {
            this.persist = persist;
            this.clsDesc = clsDesc;
        }
        
    }
    
    
}


