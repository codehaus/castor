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
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Properties;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.Persistent;
import org.exolab.castor.jdo.TimeStampable;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.TransactionAbortedException;
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


/**
 * Implements the object cache engine sitting between a persistence engine
 * and persistence storage SPI.
 *
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @author <a href="yip@intalio.com">Thomas Yip</a>
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
    private HashMap _typeInfo = new HashMap();


    /**
     * All the XA transactions running against this cache engine.
     */
    private HashMap _xaTx = new HashMap();

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
            TypeInfo baseInfo;
            Vector v;
            
            v = ClassMolder.resolve( (MappingLoader) mapResolver, this, factory, logInterceptor );
    
            Vector waitingForBase = new Vector();
            _typeInfo = new HashMap(); 
            enum = v.elements();
            while ( enum.hasMoreElements() ) {
                molder = (ClassMolder) enum.nextElement();
                if ( molder.getExtends() != null ) {
                    ClassMolder extend = molder.getExtends();
                    while ( extend.getExtends() != null ) {
                        extend = extend.getExtends();
                    }
                    if ( _typeInfo.containsKey( extend ) ) {
                        baseInfo = (TypeInfo)_typeInfo.get( extend );
                        _typeInfo.put( molder.getJavaClass(), baseInfo );
                    } else {
                        waitingForBase.add( molder );
                    }
                } else {
                    // | other cache type cause problem. have to disable it for now
                    LRU lru = LRU.create( /*molder.getCacheType() */LRU.CACHE_NONE, molder.getCacheParam() );

                    info = new TypeInfo( molder, new HashMap(), lru );

                    _typeInfo.put( molder.getJavaClass(), info );
                }
            }
            enum = waitingForBase.elements();
            while ( enum.hasMoreElements() ) {
                molder = (ClassMolder) enum.nextElement();
                ClassMolder extend = molder.getExtends();
                while ( extend.getExtends() != null ) {
                    extend = extend.getExtends();
                }
                baseInfo = (TypeInfo) _typeInfo.get( extend.getJavaClass() );
                if ( baseInfo != null ) {
                    info = new TypeInfo( molder, baseInfo );
                    _typeInfo.put( molder.getJavaClass(), info );
                } else {
                    throw new MappingException("Base class "+extend.getJavaClass().getName()+" of "+molder.getJavaClass().getName()+" not found!");
                }
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
            if ( !info.molder.isDependent() ) 
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

        OID        lockedOid;
        ObjectLock lock;
        TypeInfo   typeInfo;
        ObjectBin  result;
        Object[]   fields;
        Class      type;
        boolean    write;
        boolean    succeed;
        short      action;
    
        type = oid.getJavaClass();
        typeInfo = (TypeInfo) _typeInfo.get( type );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( Messages.format("persist.classNotPersistenceCapable", type.getName()) );

        ClassMolder molder = oid.getMolder();

        succeed = false;

        lock = null;

        try {

            if ( accessMode == AccessMode.Exclusive || accessMode == AccessMode.DbLocked ) 
                action = ObjectLock.ACTION_WRITE;
            else 
                action = ObjectLock.ACTION_READ;

            lock = typeInfo.acquire( oid, tx, action, timeout );

            lockedOid = lock.getOID();

            Object stamp = typeInfo.molder.load( tx, lockedOid, lock, object, accessMode );
            // proposal change: lockedOid parameter is not really neccesary.
            // we can added getOID() method in DepositBox. It make code a little
            // bit clear?
            // And should ClassMolder the one who set stamp?

            succeed = true;
            
            lockedOid.setStamp( stamp );

            if ( lockedOid != null ) 
                oid = lockedOid;

            if ( _logInterceptor != null )
                _logInterceptor.loading( typeInfo.javaClass, OID.flatten( oid.getIdentities() ) );
        } 
        /*
        catch ( ObjectDeletedWaitingForLockException except ) {
            // This is equivalent to object not existing
            throw new ObjectNotFoundException( Messages.format("persist.objectNotFound", type, OID.flatten(oid.getIdentities())) );
        } catch ( ObjectNotFoundException except ) {
            // Object was not found in persistent storge, must dump
            // it from the cache
            //except.printStackTrace();
            //typeInfo.delete( oid, tx );
            throw except;
        } catch ( LockNotGrantedException except ) {
            throw except;
        } catch ( PersistenceException except ) {
            // Report any error talking to the persistence engine
            //typeInfo.locks.destory( oid, tx );
            throw except;
        } */
        finally {
            if ( lock != null ) lock.confirm( tx, succeed );
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

        throw new RuntimeException("Please use load instead. Please report to castor-dev@exolab.org if you see this error message");
        /*
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
            throw new ObjectNotFoundException( Messages.format("persist.objectNotFound", query.getResultType(), OID.flatten(identities)) );
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
        */
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

        TypeInfo typeInfo;
        ObjectLock lock;
        Object[] fields;
        OID lockedOid;
        OID newoid;
        boolean succeed;


        typeInfo = (TypeInfo) _typeInfo.get( object.getClass() );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( Messages.format( "persist.classNotPersistenceCapable", object.getClass().getName()) );
            
        boolean write = true;   // just for readability

        lock = null;

        if ( ! OID.isIdsNull( oid.getIdentities() ) ) {

            lock = null;

            succeed = false;

            try {
                
                lock = typeInfo.acquire( oid, tx, ObjectLock.ACTION_CREATE, 0 );

                if ( _logInterceptor != null )
                    _logInterceptor.creating( typeInfo.javaClass, oid.getIdentities() );

                oid = lock.getOID();

                typeInfo.molder.create( tx, oid, lock, object );

                succeed = true;

                oid.setDbLock( true );
            
                return oid;
                // should catch some other exception if destory is not succeed
            } catch ( LockNotGrantedException except ) {
                // Someone else is using the object, definite duplicate key
                throw new DuplicateIdentityException( Messages.format( 
                    "persist.duplicateIdentity", object.getClass().getName(), 
                    OID.flatten(oid.getIdentities())) );
            } catch ( DuplicateIdentityException except ) {
                // we got a write lock and the persistence storage already
                // recorded. Should destory the lock
                //typeInfo.delete( oid, tx );
                throw except;
            } finally {
                if ( lock != null ) 
                    lock.confirm( tx, succeed );
            }
        } else {    // identity is null

            succeed = false;

            try {
                if ( _logInterceptor != null )
                    _logInterceptor.creating( typeInfo.javaClass, oid.getIdentities() );

                lock = typeInfo.acquire( oid, tx, ObjectLock.ACTION_CREATE, 0 );

                oid = lock.getOID();

                Object[] newids = typeInfo.molder.create( tx, oid, lock, object );

                succeed = true;

                oid.setDbLock( true );

                newoid = new OID( oid.getLockEngine(), oid.getMolder(), oid.getDepends(), newids );

                //lock.setOID( newoid );
                
                typeInfo.rename( oid, newoid, tx );

                return newoid;
            } catch ( LockNotGrantedException e ) {
                e.printStackTrace();
                throw new PersistenceException( Messages.format("persist.nested","Key Generator Failure. Duplicated Identity is generated!") );
            } finally {
                if ( lock != null )
                    lock.confirm( tx, succeed );
            }
        }
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
    public void delete( TransactionContext tx, OID oid, Object object )
            throws PersistenceException {
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object[]   fields;

        System.out.println("deleting "+oid+" by tx "+tx);

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );

        try {
            lock = typeInfo.assure( oid, tx, true );

            if ( _logInterceptor != null )
                _logInterceptor.removing( typeInfo.javaClass, OID.flatten( oid.getIdentities() ) );

            typeInfo.molder.delete( tx, oid );

        } catch ( LockNotGrantedException except ) {
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to delete object for which no lock was acquired" ) );
        }
    }

    public void markDelete( TransactionContext tx, OID oid, Object object, int timeout )
            throws PersistenceException, LockNotGrantedException {

        System.out.println("mark Delete "+oid+" by tx "+tx);
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object[]   fields;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );

        lock = typeInfo.upgrade( oid, tx, timeout );

        typeInfo.molder.markDelete( tx, oid, lock, object );
    }


    /**
     * Updates an existing object to this engine. The object must not be
     * persistent and must not have the identity of a persistent object.
     * The object's OID is returned. The OID is guaranteed to be unique
     * for this engine even if no identity was specified.
     * If the object implements TimeStampable interface, verify
     * the object's timestamp.
     *
     * @param tx The transaction context
     * @param object The object
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
     * @throws ObjectModifiedException Dirty checking mechanism may immediately
     *  report that the object was modified in the database during the long
     *  transaction.
     */
    public OID update( TransactionContext tx, OID oid, Object object, AccessMode accessMode, int timeout )
            throws ObjectNotFoundException, LockNotGrantedException, ObjectModifiedException,
                   PersistenceException, ClassNotPersistenceCapableException,
                   ObjectDeletedWaitingForLockException {

        TypeInfo   typeInfo;
        Object[]   identities;
        ObjectLock lock;
        boolean    write;
        boolean    succeed;

        // If the object is new, don't try to load it from the cache
        if ( ( object instanceof TimeStampable ) &&
                ( ( TimeStampable ) object ).jdoGetTimeStamp() == 0 ) {
            return null;
        }

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( Messages.format("persist.classNotPersistenceCapable", oid.getJavaClass().getName() ) );

        //ClassMolder molder = typeInfo.molder;
        //PersistenceException pexcept = null;
        //Object[] cache = null;

        //identities = typeInfo.molder.getIdentities( object );
        //oid = new OID( this, typeInfo.molder, identities );
        write = ( accessMode == AccessMode.Exclusive || accessMode == AccessMode.DbLocked );
        succeed = false;
        lock = null;
        try {
            // Create an OID to represent the object and see if we
            // have a lock (i.e. object is cached).

            // Object has been loaded before, must acquire lock
            // on it (write in exclusive mode)

            // [Yip] I rather limited update to always acquire read lock
            // (preferrably dblock), to avoid further concurrency problem.
            lock = typeInfo.acquire( oid, tx, ObjectLock.ACTION_UPDATE, timeout );

            /*
            if ( write && ! oid.isDbLock() ) {
                // Db-lock mode we always synchronize the object with
                // the database and obtain a lock on the object.
                if ( _logInterceptor != null )
                    _logInterceptor.loading( typeInfo.javaClass, OID.flatten( oid.getIdentities() ) );
            }*/
            oid = lock.getOID();

            typeInfo.molder.update( tx, oid, lock, object, accessMode );

            succeed = true;

            /*
            if ( accessMode == AccessMode.DbLocked )
                oid.setDbLock( true );
             */
            /*
            if ( accessMode == AccessMode.ReadOnly ) 
                typeInfo.release( oid, tx );
            */
            return oid;
        } catch ( ObjectModifiedException e ) {
            throw e;
        } catch ( ObjectDeletedWaitingForLockException except ) {
            // This is equivalent to object not existing
            throw new ObjectNotFoundException( Messages.format("persist.objectNotFound", oid.getJavaClass().getName(), OID.flatten(oid.getIdentities())) );
        } finally {
            if ( lock != null ) 
                lock.confirm( tx, succeed );
        }
    }


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


        TypeInfo   typeInfo;
        ObjectLock lock;
        boolean    modified;


        typeInfo = (TypeInfo) _typeInfo.get( object.getClass() );

        // Acquire a read lock first. Only if the object has been modified
        // do we need a write lock.

        oid = new OID( this, typeInfo.molder, oid.getIdentities() );
        // acquire read lock
        // getLockedField();
        // isPersistFieldChange()?
        // if no, return null
        // if yes, get flattened fields, 
        // acquire write lock
        // setLockedField( );
        // 
        lock = typeInfo.assure( oid, tx, false );

        oid = lock.getOID();

        modified = typeInfo.molder.store( tx, oid, lock, object );

        if ( modified )
            return oid;
        else
            return null;
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
            throws ObjectDeletedException, LockNotGrantedException, PersistenceException {

        ObjectLock lock;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        // Attempt to obtain a lock on the database. If this attempt
        // fails, release the lock and report the exception.

        try {
            typeInfo.upgrade( oid, tx, timeout );

            // typeInfo.engine.writeLock( tx, lock...);
        } catch ( IllegalStateException e ) {
            throw e;
        } catch ( ObjectDeletedWaitingForLockException e ) {
            throw new IllegalStateException("Object deleted waiting for lock?????????");
        } catch ( LockNotGrantedException e ) {
            throw e;
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
        typeInfo.upgrade( oid, tx, timeout );
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
        ObjectLock lock;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        try {
            lock = typeInfo.assure( oid, tx, true );
            typeInfo.molder.revertObject( tx, oid, lock, object );
        } catch ( LockNotGrantedException e ) {
            throw new IllegalStateException("Write Lock expected!");
        } catch ( PersistenceException except ) {
            //typeInfo.destory( oid, tx );
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
    public void updateCache( TransactionContext tx, OID oid, Object object ) {
        TypeInfo   typeInfo;
        Object[]   fields;
        ObjectLock lock;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getJavaClass() );
        try {
            lock = typeInfo.assure( oid, tx, false );
            typeInfo.molder.updateCache( tx, oid, lock, object );
        } catch ( LockNotGrantedException e ) {
            throw new IllegalStateException("Write Lock expected!");
        } catch ( PersistenceException except ) {
            except.printStackTrace();   // | to be deleted
            typeInfo.delete( oid, tx );
            //throw except;
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
        lock = typeInfo.release( oid, tx );
        lock.getOID().setDbLock( false );
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
            typeInfo.assure( oid, tx, true );
            
            typeInfo.delete( oid, tx );

            typeInfo.release( oid, tx );
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
    public HashMap getXATransactions()
    {
        return _xaTx;
    }


    /**
     * Provides information about an object of a specific type
     * (class). This information includes the object's descriptor and
     * lifecycle interceptor requesting notification about activities
     * that affect an object.
     */
    private class TypeInfo {

        /**
         * The molder for this class.
         */
        private final ClassMolder molder;

        /**
         * The Java class represented by this type info.
         */
        private final Class javaClass;

        /**
         * The Map contains all the in-used ObjectLock of the class type,
         * which keyed by the OID representing the object.
         * All extends classes share the same map as the base class.
         *
         */
        private final HashMap locks;
        
        /**
         * The Map contains all the freed ObjectLock of the class type,
         * which keyed by the OID representing the object. ObjectLock
         * put into cache maybe disposed by LRU mechanisum.
         * All extends classes share the same map as the base class.
         */
        private final LRU cache;

        /**
         * Constructor for creating base class info
         *
         * @param  molder is the classMolder of this type
         * @param  locks is the new HashMap which will be used
         *         for holding all the in-used ObjectLock
         * @param  cache is the new LRU which will be used to
         *         store and dispose freed ObjectLock
         *
         */
        private TypeInfo( ClassMolder molder, HashMap locks, LRU cache ) {
            this.javaClass = molder.getJavaClass();
            this.molder = molder;
            this.locks = locks;
            this.cache = cache;
        }

        /**
         * Constructor for creating extended class info
         * 
         * @param  molder is the classMolder of this type
         * @param  base is the TypeInfo of the base class of
         *         the molder's class
         */
        private TypeInfo( ClassMolder molder, TypeInfo base ) {
            this( molder, base.locks, base.cache );
        }

        /**
         * Acquire the object lock for transaction. After this method is call,
         * user must call {@link ObjectLock.confirm} exactly once.
         *
         * @param oid  the OID of the lock
         * @param tx   the transactionContext of the transaction to 
         *             acquire lock
         * @param lockAction   the inital action to be performed on
         *                     the lock
         * @param timeout      the time limit to acquire the lock
         */
        private ObjectLock acquire( OID oid, TransactionContext tx, short lockAction, 
                int timeout ) throws ObjectDeletedWaitingForLockException, 
                LockNotGrantedException, ObjectDeletedException {

            //System.out.println("LockEngine.acquire");
            ObjectLock entry = null;
            boolean newentry = false;
            boolean failed = true;
            // sync on "locks" is, unfortunately, necessary if we employ 
            // some LRU mechanism, especially if we allow NoCache, to avoid
            // duplicated LockEntry exist at the same time.
            synchronized( this ) {
                entry = (ObjectLock) locks.get( oid );
                if ( entry == null ) {
                    entry = (ObjectLock) cache.remove( oid );
                    if ( entry != null ) {
                        oid = entry.getOID();
                        locks.put( oid, entry );
                    }
                }
                if ( entry == null ) {
                    newentry = true;
                    entry = new ObjectLock( oid );
                    locks.put( oid, entry );
                } else {
                    oid = entry.getOID();
                }
                entry.enter();
            }
            // ObjectLock.acquire() may call Object.wait(), so a thread can not
            // been synchronized with ANY shared object before acquire(). 
            // So, it must be called outside synchronized( locks ) block.
            try {
                switch ( lockAction ) {
                case ObjectLock.ACTION_READ:
                    //(new Exception("acquire write lock on: "+entry)).printStackTrace();
                    System.out.println("acquire read lock: "+entry+" tx: "+tx);
                    entry.acquireLoadLock( tx, false, timeout );
                    break;
                case ObjectLock.ACTION_WRITE:
                    //(new Exception("acquire write lock on:"+entry)).printStackTrace();
                    System.out.println("acquire write lock: "+entry+" tx: "+tx);
                    entry.acquireLoadLock( tx, true, timeout );
                    break;
                case ObjectLock.ACTION_CREATE:
                    System.out.println("acquire create lock: "+entry+" tx: "+tx);
                    entry.acquireCreateLock( tx );
                    break;
                case ObjectLock.ACTION_UPDATE:
                    System.out.println("acquire update lock: "+entry+" tx: "+tx);
                    entry.acquireUpdateLock( tx, timeout );
                    break;
                default:
                    throw new IllegalArgumentException( "lockType "+lockAction+" is undefined!"); 
                }
                failed = false;
                return entry;
            } finally {
                synchronized( this ) {
                    entry.leave();
                    if ( failed ) {
                        // The need of this block may not be too obvious.
                        // At the very moment, if it happens, current thread 
                        // failed to acquire a lock. Then, another thread just
                        // release the lock right after. The released entry
                        // then will not be moved to cache because inLocksGap 
                        // isn't zero. So, there maybe a chance of memory 
                        // leak, as the entry was in "locks", but not in 
                        // "cache" as supposed. To avoid it from happening,
                        // we ensure here that the entry which should be move 
                        // to "cache" from "locks" is actually moved.
                        if ( entry.isDisposable() ) {
                            System.out.println("Lock "+toString()+" is disposed!");
                            locks.remove( oid );
                            cache.put( oid, entry );
                        }
                    }
                }
            }
        }

        /** 
         * Reaasure the lock which have been successfully acquired by the 
         * transaction.
         *
         * @param  oid  the OID of the lock
         * @param  tx   the transaction in action
         * @param  write  true if we want to upgrade or reassure a write lock
         *                false for read lock
         * @param  timeout  time limit
         *
         */
        private ObjectLock upgrade( OID oid, TransactionContext tx, int timeout ) 
                throws ObjectDeletedWaitingForLockException, LockNotGrantedException {

            ObjectLock entry = null;
            //System.out.print("upgrade tx: "+tx);
            synchronized ( this ) {
                entry = (ObjectLock) locks.get( oid );
                if ( entry == null ) 
                    throw new ObjectDeletedWaitingForLockException("Lock entry not found. Deleted?");
                    //throw new IllegalStateException("Lock, "+oid+", doesn't exist or no lock!");
                System.out.println(" lock "+entry);
                if ( !entry.hasLock( tx, false ) )
                    throw new IllegalStateException("Transaction does not hold the any lock on "+oid+"!");    
                oid = entry.getOID();
                entry.enter();
            }
            try {
                entry.upgrade( tx, timeout );
                return entry;
            } finally {
                synchronized ( this ) {
                    entry.leave();
                }
            }
        }

        private synchronized ObjectLock assure( OID oid, TransactionContext tx, boolean write ) 
                throws ObjectDeletedWaitingForLockException, LockNotGrantedException {

            System.out.print("assure "+(write?"write":"read") +" tx: "+tx);
            ObjectLock entry = (ObjectLock) locks.get( oid );
            if ( entry == null ) 
                throw new IllegalStateException("Lock, "+oid+", doesn't exist or no lock!");
            System.out.println(" lock: "+entry);
            if ( !entry.hasLock( tx, write ) )
                throw new IllegalStateException("Transaction "+tx+" does not hold the "+(write?"write":"read")+" lock: "+entry+"!");

            System.out.println(" entry: "+entry);
            return entry;
        }


        /**
         * Move the locked object from one OID to another OID for transaction
         * It is to be called by after create.
         *
         * @param orgoid  orginal OID before the object is created
         * @param newoid  new OID after the object is created
         * @param tx      the TransactionContext of the transaction in action
         *
         */
        private synchronized ObjectLock rename( OID orgoid, OID newoid, TransactionContext tx ) 
                throws LockNotGrantedException {

            ObjectLock entry, newentry;
            boolean write;

            entry = (ObjectLock) locks.get( orgoid );
            newentry = (ObjectLock) locks.get( newoid );

            // validate locks
            if ( orgoid == newoid ) 
                throw new LockNotGrantedException("Locks are the same");
            if ( entry == null ) 
                throw new LockNotGrantedException("Lock doesn't exsit!");
            if ( !entry.isExclusivelyOwned( tx ) ) 
                throw new LockNotGrantedException("Lock to be renamed is not own exclusively by transaction!");
            if ( entry.isEntered() ) 
                throw new LockNotGrantedException("Lock to be renamed is being acquired by another transaction!");
            if ( newentry != null ) 
                throw new LockNotGrantedException("Lock is already existed for the new oid.");

            entry = (ObjectLock) locks.remove( orgoid );
            entry.setOID( newoid );
            locks.put( newoid, entry );

            // copy oid status
            newoid.setDbLock( orgoid.isDbLock() );
            newoid.setStamp( orgoid.getStamp() );

            return newentry;
            // do we bother making sure cache does not has entry for orgoid
            // or newoid??
        }

        /**
         * Delete the object lock. It's called after the object is 
         * deleted from the persistence and the transaction committed.
         *
         * @param oid is the OID of the ObjectLock
         * @param tx is the transactionContext of transaction in action
         *
         */
        private ObjectLock delete( OID oid, TransactionContext tx ) {

            ObjectLock entry;
            synchronized( this ) {
                entry = (ObjectLock) locks.get( oid );

                if ( entry == null )
                    throw new IllegalStateException("No lock to destory!");
                entry.enter();
            }

            try {
                entry.delete(tx);
                return entry;
            } finally {
                synchronized( this ) {
                    entry.leave();
                    if ( entry.isDisposable() ) {
                        System.out.println("Lock "+toString()+" is disposed!");
                        locks.remove( oid );
                    }
                }
            }
        }

        /**
         * Release the object lock. It's called after the object is 
         * the transaction committed.
         *
         * @param oid is the OID of the ObjectLock
         * @param tx is the transactionContext of transaction in action
         *
         */
        private ObjectLock release( OID oid, TransactionContext tx ) {
            
            boolean failed = true;
            ObjectLock entry = null;
            synchronized( this ) {
                entry = (ObjectLock) locks.get( oid );

                if ( entry == null ) 
                    throw new IllegalStateException("No lock to release! "+oid+" for transaction "+tx);

                entry.enter();
            }
            try {
                entry.release(tx);
                failed = false;
                return entry;
            } finally {
                synchronized( this ) {
                    entry.leave();
                    if ( entry.isDisposable() ) {
                        System.out.println("Lock "+toString()+" is disposed!");
                        cache.put( oid, entry );
                        locks.remove( oid );
                    }
                }
            }
        }
    }
}
