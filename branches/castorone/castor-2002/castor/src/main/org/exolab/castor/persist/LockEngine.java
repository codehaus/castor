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
 */


package org.exolab.castor.persist;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.Persistent;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.engine.CacheType;
import org.exolab.castor.jdo.engine.JDOClassDescriptor;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.ValidityException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.loader.MappingLoader;
import org.exolab.castor.mapping.loader.ClassDescriptorImpl;
import org.exolab.castor.mapping.loader.RelationDescriptor;
import org.exolab.castor.persist.KeyGeneratorFactoryRegistry;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.KeyGeneratorFactory;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.persist.spi.LogInterceptor;
import org.exolab.castor.persist.ObjectBin;
import org.exolab.castor.util.Messages;



// New ToDo for LockEngine
//  - DONE -- change oid to take ClassHelper
//  - DONE -- releaseLock is needed, cus Tx holds info about
//      object lock
//  - DONE -- What is the different between extends and depends (1 hr)
//      # really not much, just opposite
//  - DONE -- consider a way to implmement multi-columns primany key (1 hr)
//      Should be as easy as imagined
//  - DONE -- understand SQLEngine and how should ClassMolder create it (1 hr)
//      need to pass some factory all the way down, need to pass keyGen
//  - DONE -- unchange the removal of LockEngine (1 hr)
//  - DONE -- merge Load and CopyObject in TransactionContext (1 hr)
//  - DONE -- merge respective methods in LockEngine (1 hr)
//  - CANCELED -- make ClassMolder implements LockEngine 
//      and modify LockEngine? (2 hr)
//  - CANCELED -- change SQLEngine so it doesn't depend on ClassDescriptor (1 hr)
//  - DONE -- finish the constructor of FieldMolder to include 
//      Many-to-Many relation (and DatingService etc...) (2 hr)
//  - DONE -- copy code from ClassHandler to ClassMolder (3 hr)
//  - DONE -- still need thinking about FieldMolder of the way it 
//      interact with RelationCollection (2 hr)
//      (it requires knowing how to implement ClassMolder
//  - DONE -- LockEngine need revert, update and forget etc
//
//  - add logic, so if the related many-to-many isn't
//      lazy, it class helper is lazy (1 hr)
//  - the prime time, implmenets many-to-many, dependent, 
//      lazy in ClassMolder (8 hr) (7 hr)
//  - implement multi-column pk (2 hr)

/**
 * Implements the object cache engine sitting between a persistence engine
 * and persistence storage SPI.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public final class LockEngine {


    /**
     * IMPLEMENTATION NOTES:
     *
     * An object may be persistent in multiple caches at any given
     * time. There is no way to load an object from multiple caches,
     * but an object can be loaded in one engine and then made
     * persistent in another. The engines are totally independent and
     * no conflicts should occur.
     *
     * For efficiency all objects are cached as an array of fields.
     * Each field holds the cached field value, the identity or a
     * related object, or a vector holding multiple identities of
     * related objects.
     *
     * Each class heirarchy gets its own cache, so caches can be
     * controlled on a class-by-class basis.
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
     * All the XA transactions running against this cache engine.
     */
    private Hashtable _xaTx = new Hashtable();

    /**
     * Used by the constructor when creating handlers to temporarily
     * hold the persistence factory for use by {@link #addClassHandler}.
     */
    private PersistenceFactory _factory;


    /**
     * The log interceptor used to trace persistence operations. May be null.
     */
    private LogInterceptor     _logInterceptor;


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
    LockEngine( MappingResolver mapResolver,
            PersistenceFactory factory, LogInterceptor logInterceptor )
            throws MappingException {
        try {
            ClassMolder molder;
            Enumeration enum;
            TypeInfo info;
            Vector v;
            
            v = ClassMolder.resolve( (MappingLoader) mapResolver, this, factory, logInterceptor );
    
            _typeInfo = new Hashtable(); 
            enum = v.elements();
            while ( enum.hasMoreElements() ) {
                molder = (ClassMolder) enum.nextElement();
                info = new TypeInfo( molder );
                _typeInfo.put( molder.getJavaClass(), info );
            }
            

            _logInterceptor = logInterceptor;
    
            _factory = factory;
        } catch ( ClassNotFoundException e ) {
        	throw new MappingException("Declared Class not found!" );
        }
    }

    public ClassMolder getClassMolder( Class cls ) {
        TypeInfo info = (TypeInfo)_typeInfo.get( cls );
        if ( info != null ) {
            return info.molder;
        }
        return null;
    }

    public Persistence getPersistence( Class cls ) {
        ClassMolder molder = getClassMolder( cls );
        if ( molder != null )
            return molder.getPersistence();
        return null;
    }
    /**
     * Loads an object of the specified type and identity from
     * persistent storage. In exclusive mode the object is always
     * loaded and a write lock is obtained on the object, preventing
     * concurrent updates. In non-exclusive mode the object is either
     * loaded or obtained from the cache with a read lock. The object's
     * OID is always returned, this OID must be used in subsequent
     * operations on the object. Must call {@link #copyObject} to obtain
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
    public OID load( TransactionContext tx, OID oid, Object object, AccessMode accessMode, int timeout )
            throws ObjectNotFoundException, LockNotGrantedException, PersistenceException, 
            ClassNotPersistenceCapableException, ObjectDeletedWaitingForLockException {


        TypeInfo   typeInfo;
        ObjectBin  result;
        ObjectLock lock;
        Class      type;
        boolean    write;
    
        type = oid.getJavaClass();
        typeInfo = (TypeInfo) _typeInfo.get( type );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableExceptionImpl( type );

        ClassMolder molder = oid.getMolder();
        PersistenceException pexcept = null;
        Object[] cache = null;
        try {
            write = ( accessMode == AccessMode.Exclusive || accessMode == AccessMode.DbLocked );

            oid = typeInfo.locks.acquire( oid, tx, write, timeout, null );

            if ( _logInterceptor != null )
                _logInterceptor.loading( typeInfo.javaClass, OID.flatten( oid.getIdentities() ) );

            molder.load( tx, oid, object, accessMode );

            if ( accessMode == AccessMode.DbLocked )
                oid.setDbLock( true );
        } catch ( ObjectDeletedWaitingForLockException except ) {
            // This is equivalent to object not existing
            throw new ObjectNotFoundExceptionImpl( type, OID.flatten( oid.getIdentities() ) );
        } catch ( ObjectNotFoundException except ) {
            // Object was not found in persistent storge, must dump
            // it from the cache
            //typeInfo.locks.destory( oid, tx );
            throw except;
        } catch ( PersistenceException except ) {
            // Report any error talking to the persistence engine
            //typeInfo.locks.destory( oid, tx );
            throw except;
        }
        // Non-exclusive mode, we do not attempt to touch the database
        // at this point, simply return the object's oid.

        if ( accessMode == AccessMode.ReadOnly ) {
            typeInfo.locks.release( oid, tx );
        }
        return oid;
    }

    /**
     * Loads an object of the specified type and identity from the
     * query results. In exclusive mode the object is always loaded
     * and a write lock is obtained on the object, preventing
     * concurrent updates. In non-exclusive mode the object is either
     * loaded or obtained from the cache with a read lock. The object's
     * OID is always returned, this OID must be used in subsequent
     * operations on the object. Must call {@link #copyObject} to obtain
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
    public ObjectBin fetch( TransactionContext tx, PersistenceQuery query, Object[] identities,
                      AccessMode accessMode, int timeout )
            throws ObjectNotFoundException, LockNotGrantedException,
            PersistenceException {

        ObjectBin   result;
        OID        oid;
        ObjectLock lock;
        TypeInfo   typeInfo;
        boolean    writeLock;

        typeInfo = (TypeInfo) _typeInfo.get( query.getResultType() );

        // XXX Problem, obj might be parent class but attempting
        //     to load derived class, will still return parent class
        //     Need to solve by swapping to a new object

            // In db-lock mode we always synchronize the object with
            // the database and obtain a lock on the object.
        oid = new OID( this, typeInfo.molder, identities);
        try {
            boolean write = (accessMode==AccessMode.DbLocked || accessMode==AccessMode.Exclusive);
            oid = typeInfo.locks.acquire( oid, tx, write, timeout, null );

            if ( _logInterceptor != null )
                _logInterceptor.loading( typeInfo.javaClass, OID.flatten( identities ) );

            //result = query.fetch( tx, oid, accessMode, query );
            result = null;
            if ( accessMode == AccessMode.DbLocked )
                oid.setDbLock( true );
        } catch ( ObjectDeletedWaitingForLockException except ) {
            // This is equivalent to object not existing
            throw new ObjectNotFoundExceptionImpl( query.getResultType(), OID.flatten( identities ) );
        //} catch ( ObjectNotFoundException except ) {
            // Object was not found in persistent storge, must dump
            // it from the cache
        //    typeInfo.locks.destory( oid, tx );
        //    throw except;
        } catch ( PersistenceException except ) {
            // Report any error talking to the persistence engine
            typeInfo.locks.destory( oid, tx );
            throw except;
        }
        // At this point the object is known to exist in
        // persistence storage and we have a write lock on it.
        //return oid;
        return result;
            // Non-exclusive mode, we do not attempt to touch the database
            // at this point, simply return the object's oid.
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
     * @param object The newly created object
     * @param identity The identity of the object, or null
     * @return The object's OID
     * @throws DuplicateIdentityException An object with this identity
     *  already exists in persistent storage
     * @throws PersistenceException An error reported by the
     *  persistence engine
     * @throws ClassNotPersistenceCapableException The class is not
     *  persistent capable
     */
    public OID create( TransactionContext tx, OID oid, Object object )
            throws DuplicateIdentityException, PersistenceException,
            ClassNotPersistenceCapableException {
        ObjectLock lock;
        OID newoid;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( object.getClass() );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableExceptionImpl( object.getClass() );

        // Must prevent concurrent attempt to create the same object
        // Best way to do that is through the type
        synchronized ( typeInfo ) {
            
            oid.setDbLock( true );

            if ( ! OID.isIdsNull( oid.getIdentities() ) ) {
                try {
                    //typeInfo.locks.destory( oid, tx );

                    newoid = typeInfo.locks.acquire( oid, tx, true, 0, null );

                    if ( _logInterceptor != null )
                            _logInterceptor.creating( typeInfo.javaClass, oid.getIdentities() );

                    // Is it creating after delete?
                    if ( ! tx.getObjectEntry( object ).deleted ) {
                        // Create the object in persistent storage acquiring a lock on the object.
                        // If no identity was given for the object, this method will attempt to
                        // create an identity using a key generator.
                        oid = typeInfo.molder.create( tx, newoid, object );

                        if ( OID.isIdsNull( oid.getIdentities() ) ) {
                            if ( OID.isIdsNull( oid.getMolder().getIdentities( object ) ) ) {
                                throw new PersistenceException("LockEngine.create(): both oid.getIdentity() and molder.getId( object ) is null after create!");
                            } else {
                                throw new PersistenceException("LockEngine.create(): oid.getIdentity() is null after create!");
                            }
                        }

                    } else {
                        // | hum what if the object is deleted beforehand??
                    }

                // should catch some other exception if destory is not succeed
                // LockNotGranted won't happen, cus it's a new lock
                } catch ( LockNotGrantedException except ) {
                    // Someone else is using the object, definite duplicate key
                    throw new DuplicateIdentityExceptionImpl( object.getClass(), OID.flatten( oid.getIdentities() ) );
                } 
                // Dump the memory image of the object, it might have been deleted
                // from persistent storage
                //typeInfo.cache.removeLock( oid );
                //lock.delete( tx );
            } else {
                try {
                    if ( _logInterceptor != null )
                            _logInterceptor.creating( typeInfo.javaClass, oid.getIdentities() );
                    typeInfo.locks.acquire( oid, tx, true, 0, null );

                    newoid = typeInfo.molder.create( tx, oid, object );

                    if ( newoid.getIdentities() == null ) {
                        if ( newoid.getMolder().getIdentities( object ) == null ) {
                            throw new PersistenceException("LockEngine.create(): both oid.getIdentity() and molder.getId( object ) is null after create!");
                        } else {
                            throw new PersistenceException("LockEngine.create(): oid.getIdentity() is null after create!");
                        }
                    }

                    if ( !newoid.equals( oid ) ) {
                        typeInfo.locks.destory( oid, tx );
                        oid = newoid;
                    }
                } catch ( LockNotGrantedException e ) {
                    // shouldn't happen, unless keygenerator failed
                }
            }
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
     * @param type The object type
     * @param identity The object's identity
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public void delete( TransactionContext tx, OID oid )
            throws PersistenceException {
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object[]   fields;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );

        try {
            oid = typeInfo.locks.acquire( oid, tx, true, 0, false, null );

            if ( _logInterceptor != null )
                _logInterceptor.removing( typeInfo.javaClass, OID.flatten( oid.getIdentities() ) );

            typeInfo.molder.delete( tx, oid );

            //typeInfo.locks.destory( oid, tx );

        } catch ( LockNotGrantedException except ) {
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to delete object for which no lock was acquired" ) );
        }
    }



    void markDelete( TransactionContext tx, OID oid, Object object, int timeout )
        throws PersistenceException
    {
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object[]   fields;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );

        try {
            oid = typeInfo.locks.acquire( oid, tx, true, 0, false, null );

            oid.getMolder().markDelete( tx, oid, null );
        } catch ( LockNotGrantedException except ) {
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to delete object for which no lock was acquired" ) );
        }                
    }


    /**
     * Updates an existing object to this engine. The object must not be
     * persistent and must not have the identity of a persistent object.
     * The object's OID is returned. The OID is guaranteed to be unique
     * for this engine even if no identity was specified.
     *
     * @param tx The transaction context
     * @param object The object to update
     * @param identity The identity of the object, or null
     * @param db The database in which the object was created
     * @return The object's OID
     * @throws PersistenceException An error reported by the
     *  persistence engine
     * @throws ClassNotPersistenceCapableException The class is not
     *  persistent capable
     */
     /*
    public OID update( TransactionContext tx, PersistenceInfoGroup scope, ClassMolder molder, Object object )
            throws DuplicateIdentityException, PersistenceException,
            ClassNotPersistenceCapableException {
        OID        oid;
        ObjectLock lock;
        ObjectBin  result;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( object.getClass() );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableExceptionImpl( object.getClass() );

        // Must prevent concurrent attempt to create the same object
        // Best way to do that is through the type
        synchronized ( typeInfo ) {
            // XXX If identity is null need to fine a way to determine it
            if ( identity == null )
                throw new PersistenceExceptionImpl( "persist.noIdentity" );

            try {

                oid = typeInfo.locks.acquire( new OID( typeInfo.molder, identity), 
                    tx, true, 0 );
                //result = 
                //typeInfo.molder.update( tx, oid, object );   
            } catch ( LockNotGrantedException except ) {
                // Someone else is using the object, definite duplicate key
                throw new DuplicateIdentityExceptionImpl( object.getClass(), identity );
            } 
        }
        return oid;
        //result;
    } */


    /**
     * Called at transaction commit to store an object that has been
     * loaded during the transaction. If the object has been created
     * in this transaction but without an identity, the object will
     * be created in persistent storage. Otherwise the object will be
     * stored and dirty checking might occur in order to determine
     * whether the object is valid. The object's OID might change
     * during this process, and the new OID will be returned. If the
     * object was not stored (not modified), null is returned.
     *
     * @param tx The transaction context
     * @param object The object to store
     * @param identity The object's identity
     * @param timeout The timeout waiting to acquire a lock on the
     *  object (specified in seconds)
     * @return The object's OID if stored, null if ignored
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
    public OID store( TransactionContext tx, OID oid, Object object, int timeout ) 
            throws LockNotGrantedException, ObjectDeletedException,
            ObjectModifiedException, DuplicateIdentityException,
            PersistenceException {
        System.out.println("LockEngine.store() "+oid);
        Object[]   fields;
        Object[]   original;
        ObjectLock lock;
        Object[]   oldIdentities;
        TypeInfo   typeInfo;
        short      modified;

        typeInfo = (TypeInfo) _typeInfo.get( object.getClass() );

        // Acquire a read lock first. Only if the object has been modified
        // do we need a write lock.

        try {
            oid = new OID( this, typeInfo.molder, oid.getIdentities() );
            typeInfo.locks.acquire( oid, tx, false, 0, false, null );
            typeInfo.molder.store( tx, oid, object );
            return oid;
        } catch ( ClassNotPersistenceCapableException except ) {
            throw new PersistenceExceptionImpl( "persist.internal", except.toString() );
        }
    }

    public boolean hasLock( TransactionContext tx, OID oid, boolean write ) {
        return true;
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
            throws LockNotGrantedException, PersistenceException {
        ObjectLock lock;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        // Attempt to obtain a lock on the database. If this attempt
        // fails, release the lock and report the exception.
    
        try {
            typeInfo.locks.acquire( oid, tx, true, timeout, false, null );
        //    typeInfo.molder.writeLock( tx, oid, null );
        } catch ( LockNotGrantedException e ) {
            throw e;
        //} catch ( ObjectDeletedException except ) {
        //    typeInfo.locks.destory( oid, tx );
        //    throw except;
        } catch ( PersistenceException except ) {
            typeInfo.locks.destory( oid, tx );
            throw except;
        }
    }


    /**
     * Acquire a write lock on the object in memory. A soft lock prevents
     * other threads from changing the object, but does not acquire a lock
     * on the database.
     *
     * @param tx The transaction context
     * @param oid The object's OID
     * @param timeout The timeout waiting to acquire a lock on the
     *  object (specified in seconds)
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws ObjectDeletedException The object has been deleted from
     *  persistent storage
     */
    public void softLock( TransactionContext tx, OID oid, int timeout )
            throws LockNotGrantedException {
        ObjectLock lock;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        typeInfo.locks.upgrade( oid, tx );
    }

    /**
     * Reverts an object to the cached copy given the object's OID.
     * The cached object is copied into the supplied object without
     * affecting the locks, loading relations or emitting errors.
     * This method is used during the rollback phase.
     *
     * @param tx The transaction context
     * @param oid The object's oid
     * @param object The object into which to copy
     * @throws PersistenceException An error reported by the
     *  persistence engine obtaining a dependent object
     */
    public void revertObject( TransactionContext tx, OID oid, Object object )
            throws PersistenceException {
        TypeInfo   typeInfo;
        Object[]   fields;
        ObjectLock lock;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        try {
            typeInfo.locks.acquire( oid, tx, true, 0, false, null );
            typeInfo.molder.revertObject( tx, oid, object );
        } catch ( LockNotGrantedException e ) {
            throw new IllegalStateException("Write Lock expected!");
        } catch ( PersistenceException except ) {
            typeInfo.locks.destory( oid, tx );
            throw except;
        }
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
     * @param object The object to copy from
     */
    public void updateObject( TransactionContext tx, OID oid, Object object ) {
        TypeInfo   typeInfo;
        Object[]   fields;
        ObjectLock lock;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        try {
            typeInfo.locks.upgrade( oid, tx );
            typeInfo.molder.updateCache( tx, oid, object );
        } catch ( LockNotGrantedException e ) {
            throw new IllegalStateException("Write Lock expected!");
        } catch ( PersistenceException except ) {
            typeInfo.locks.destory( oid, tx );
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
    public void releaseLock( TransactionContext tx, OID oid ) {
        ObjectLock lock;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        oid.setDbLock( false );
        typeInfo.locks.release( oid, tx );
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
    public void forgetObject( TransactionContext tx, OID oid ) {
        ObjectLock lock;
        Object[]   fields;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        //lock = typeInfo.locks.aquire( oid, tx );
        try {
            //oid = new OID( typeInfo.molder, identity);
            typeInfo.locks.acquire( oid, tx, true, 0, null );
            
            typeInfo.locks.destory( oid, tx );
        } catch ( LockNotGrantedException except ) {
            // If this transaction has no write lock on the object,
            // something went foul.
            if ( _logInterceptor != null )
                _logInterceptor.message( Messages.format( "persist.internal", "forgetObject: " + except.toString() ) );
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

    /**
     * Provides information about an object of a specific type
     * (class). This information includes the object's descriptor and
     * lifecycle interceptor requesting notification about activities
     * that affect an object.
     */
    class TypeInfo {
        /**
         * The Java class represented by this type info.
         */
        final Class        javaClass;

        /**
         * The molder for this class.
         */
        final ClassMolder molder;

        final Locks    locks;

        TypeInfo( ClassMolder molder ) {
            this.molder = molder;
            this.locks = new Locks( molder );
            this.javaClass = molder.getJavaClass();
        }
    }

    interface CacheBin {
        public Object getObject();
        public void setObject( Object o );
    }
    interface Operator {
        public void execute( CacheBin cache );
    }
    class Locks {

        Hashtable locks;
        //LRU cache;            
        CacheHolder holder;
        int counter;

        class CacheEntry implements CacheBin {
            private Object cached;
            private final ObjectLock lock;
            int count;
            CacheEntry( ObjectLock lock ) {
                if ( lock == null ) throw new IllegalArgumentException("lock can't be null");
                this.lock = lock;
            }
            public Object getObject() {
                return cached;
            }
            public void setObject( Object o ) {
                cached = o;
            }
        }

        Locks( CacheHolder holder ) {
            locks = new Hashtable();
            this.holder = holder;
        }
        OID acquire( OID oid, TransactionContext tx, boolean readOnly, int timeout, Operator ops  ) 
                throws ObjectDeletedWaitingForLockException, LockNotGrantedException {
            return acquire( oid, tx, readOnly, timeout, true, ops );
        }
        // this complicated synchronized code is designed to make sure
        // and cache will be released  
        OID acquire( OID oid, TransactionContext tx, boolean readOnly, 
                int timeout, boolean createNew, Operator ops ) 
                throws ObjectDeletedWaitingForLockException, LockNotGrantedException {
            OID result;
            CacheEntry entry;
            synchronized( locks ) {
                entry = (CacheEntry) locks.get( oid );
                if ( entry == null ) {
                    //entry = (CacheEntry) cache.get( oid );
                    //if ( entry.lock != null ) {
                    //    locks.put( oid, entry );
                    //} else 
                    if ( createNew ) {
                        entry = new CacheEntry( new ObjectLock( oid ) );
                        locks.put( oid, entry );
                    } else 
                        throw new IllegalStateException("Lock doesn't exist!");
                }
                entry.count++;
            }
            synchronized( entry.lock ) {
                boolean failed = true;
                try {
                    entry.lock.acquire( tx, readOnly, timeout );
                    holder.hold( oid );
                    if ( ops != null )
                        ops.execute( entry );
                    failed = false;
                    return entry.lock.getOID();
                } finally {
                    if ( failed ) {
                        synchronized( locks ) {
                            entry.count--;
                            if ( entry.count==0 && entry.lock.isFree() ) {
                                locks.remove( oid );
                            }
                        }
                    }
                }
            }
        }
        OID upgrade( OID oid, TransactionContext tx ) 
                throws ObjectDeletedWaitingForLockException, LockNotGrantedException {
            OID result;
            CacheEntry entry;
            synchronized( locks ) {
                entry = (CacheEntry) locks.get( oid );
                if ( entry == null || !entry.lock.hasLock( tx, false ) ) {
                    throw new IllegalStateException("Lock doesn't exist or no lock!");
                } 
                entry.count++;
            }
            synchronized( entry.lock ) {
                try {
                    entry.lock.acquire( tx, true, 0 );
                    return entry.lock.getOID();
                } finally {
                    synchronized( locks ) {
                        entry.count--;
                    }
                }
            }
        }
        void destory( OID oid, TransactionContext tx ) {
            // is two sync block better than one?
            synchronized( locks ) {
                CacheEntry entry = (CacheEntry) locks.remove( oid );
                if ( entry.lock == null )
                    throw new IllegalStateException("No lock to destory!");
                entry.lock.delete(tx);
                locks.remove( oid );
                holder.destory( oid );
            }
        }
        void release( OID oid, TransactionContext tx ) {
            // is two sync block better/faster than one?
            CacheEntry entry;
            synchronized( locks ) {
                entry = (CacheEntry) locks.get( oid );
                if ( entry.lock == null )
                    throw new IllegalStateException("No lock to release!");
                
                entry.lock.release(tx);
                holder.release( oid );
                if ( entry.count == 0 && entry.lock.isFree() ) {
                    Object o = locks.remove( oid );
                    //cache.put( oid, o );
                }
            }
        }
    }

}
