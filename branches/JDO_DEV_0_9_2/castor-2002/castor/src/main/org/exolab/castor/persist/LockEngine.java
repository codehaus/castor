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
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.persist;

import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
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
import org.exolab.castor.jdo.conf.Database;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.loader.MappingLoader;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.Connector;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.persist.LogInterceptor;
import org.exolab.castor.util.Messages;


/**
 * LockEngine is a gateway for a data store that implements the interfaces 
 * int the {@link org.exolab.castor.persist.spi}.
 * <p>
 * It mantains dirty checking cache state and lock, and provides a thread safe 
 * enviroment for transactions. LockEngine garantees that no two conflicting 
 * operations will be let running concurrently for the same object. 
 * <p>
 * For example, it ensures that exactly one transaction may read (load) exclusively
 * on one object; transaction can not deleted an object while the other is 
 * reading it, etc...
 * <p>
 * It also provides caching for a persistence storage. Different {@link LRU} mechanisms
 * can be specified. 
 * <p>
 * User should not create more than one instance of LockEngine for each persistent 
 * storage. So that object can be properly locked and ObjectModifiedException can
 * be avoided.
 * <p>
 * However, if more than one instance of LockEngine or some other external 
 * application run concurrently, if the {@link Persistence} supports dirty checking,
 * like a fully complaint JDBC Relational Database, proper 
 * ObjectModifiedException will be thrown to ensure data consistency.
 *
 *
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @author <a href="yip@intalio.com">Thomas Yip</a>
 * @version $Revision$ $Date$
 */
public final class LockEngine /*implements TransactionContextListener*/ {


    /**
     * IMPLEMENTATION NOTES:
     *
     * An object may be persistent in multiple caches at any given
     * time. There is no way to load an object from multiple caches,
     * but an object can be loaded in one engine and then made
     * persistent in another. The engines are totally independent and
     * no conflicts should occur.
     *
     * Each class hierarchy gets its own cache, so caches can be
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
     * All the connection keyed by TransactionContext
     */
    private HashMap _conn = new HashMap();

    
    /**
     * Used by the constructor when creating handlers to temporarily
     * hold the persistence factory for use by {@link #addClassHandler}.
     */
    private PersistenceFactory _factory;


    /**
     * The log interceptor used to trace persistence operations. May be null.
     */
    private LogInterceptor     _logInterceptor;


    private Connector          _connector;

    /**
     * The map is keyed by the TransactionContext and valued by the 
     * connection used for the specify transaction.
     */
    private Map                _conns;

    /**
     * Construct a new cache engine with the specified mapping table, 
     * persistence engine and the log interceptor.
     *
     * @param mapResolver Provides mapping information for objects
     *  supported by this cache
     * @param factory Factory for creating persistence engines for each
     *  object described in the map
     * @param logInterceptor Log interceptor to use for cache and all its
     *  persistence engines
     * @throws MappingException Indicate that one of the mappings is
     *  invalid
     */
    public LockEngine( PersistenceFactory factory, 
            org.exolab.castor.jdo.conf.Database conf,
            LogInterceptor logInterceptor )
            throws MappingException, PersistenceException {

        _logInterceptor = logInterceptor;

        _factory = factory;

        _connector = factory.getConnector( conf );

        /*
        try {
            Vector v = ClassMolder.resolve( (MappingLoader) mapResolver, this, factory, logInterceptor );
    
            _typeInfo = new HashMap();
            Enumeration enum = v.elements();

            HashSet processedClasses = new HashSet();
            HashSet freshClasses = new HashSet();
            // copy things into an arraylist
            while ( enum.hasMoreElements() )
                freshClasses.add( enum.nextElement() );

            // iterates through all the ClassMolders in the LockEngine.
            // We first create a TypeInfo for all the base class (ie not extends
            // other class) in the first iteration.
            int counter = 0;
            do {
                counter = freshClasses.size();
                Iterator itor = freshClasses.iterator();
                while ( itor.hasNext() ) {
                    ClassMolder molder = (ClassMolder) itor.next();
                    ClassMolder extend = molder.getExtends();

                    if ( extend == null ) {
                        // create new LRU for the base type
                        LRU lru = LRU.create( molder.getCacheType(), molder.getCacheParam() );
                        TypeInfo info = new TypeInfo( molder, new HashMap(), lru ); 
                        _typeInfo.put( molder.getName(), info );
                        itor.remove();
                        processedClasses.add( molder );

                    } else if ( processedClasses.contains( molder.getExtends() ) ) {
                        // use the base type to construct the new type
                        TypeInfo baseInfo = (TypeInfo)_typeInfo.get( extend.getName() );
                        _typeInfo.put( molder.getName(), new TypeInfo( molder, baseInfo ) );
                        itor.remove();
                        processedClasses.add( molder );

                    } else {
                        // do nothing and wait for the next turn
                    }

                }
            } while ( freshClasses.size() > 0 && counter != freshClasses.size() );

            // error if there is molder left.
            if ( freshClasses.size() > 0 ) {
                Iterator itor = freshClasses.iterator();
                while ( itor.hasNext() ) {
                    ClassMolder molder = (ClassMolder)itor.next();
                    _logInterceptor.message("The base class, "
                        + (molder.getExtends().getName())
                        + ", of the extends class ," + molder.getName() 
                        + " can not be resolved! ");
                }
                throw new MappingException("Some base class can not be resolved!");
            }
            /*
            while ( enum.hasMoreElements() ) {
                molder = (ClassMolder) enum.nextElement();
                if ( molder.getExtends() != null ) {
                    ClassMolder extend = molder.getExtends();
                    while ( extend.getExtends() != null ) {
                        extend = extend.getExtends();
                    }
                    // ssa, FIXME : Is that part still necessary ?
//                    if ( _typeInfo.containsKey( extend.getName() ) ) {
                    if ( false ) {
                        baseInfo = (TypeInfo)_typeInfo.get( extend.getName() );
                        _typeInfo.put( molder.getName(), baseInfo );
                    } else {
                        waitingForBase.add( molder );
                    }
                } else {
                    LRU lru = LRU.create( molder.getCacheType(), molder.getCacheParam() );

                    info = new TypeInfo( molder, new HashMap(), lru ); 

                    _typeInfo.put( molder.getName(), info );
                }
            }
            // we then iterate through all extended classes in which the 
            // using the base typeInfo.
            enum = waitingForBase.elements();
            while ( enum.hasMoreElements() ) {
                molder = (ClassMolder) enum.nextElement();
                ClassMolder extend = molder.getExtends();
                while ( extend.getExtends() != null ) {
                    extend = extend.getExtends();
                }
                baseInfo = (TypeInfo) _typeInfo.get( extend.getName() );
                if ( baseInfo != null ) {
                    info = new TypeInfo( molder, baseInfo );
                    _typeInfo.put( molder.getName(), info );
                } else {
                    throw new MappingException("Base class "+extend.getName()+" of "+molder.getName()+" not found!");
                }
            } 

        } catch ( ClassNotFoundException e ) {
            throw new MappingException("Declared Class not found!" );
        } */
    }

    /**
     * Get classMolder which represents the given java data object class
     * Dependent class will not be returned to avoid persistenting 
     * a dependent class without 
     */
     /*
    public Resolver getResolver( Class cls ) {
        TypeInfo info = (TypeInfo)_typeInfo.get( cls.getName() );
        if ( info != null ) {
            if ( !info.molder.isDependent() ) 
                return info.molder;
        }
        return null;
    }*/

    public void commitConnection( TransactionContext tx ) 
            throws PersistenceException {
    }

    public void rollbackConnection( TransactionContext tx ) {
    }

    public void closeConnection( TransactionContext tx ) 
            throws PersistenceException {
    }

    /**
     * This method is used by the bridge layer to insert an prefetched
     * entity into the LockEngine. Each inserted entity is read-locked. 
     * If the entity has already locked by this transaction or any 
     * other transaction, calling this method result in no effect.
     */
    public void addEntity( TransactionContext tx, Entity entity ) {
    }

    /**
     * This method is used by the bridge layer to insert an list of
     * entities' identities into the LockEngine. Each inserted list
     * will be read-locked. If the list has already locked by this
     * transaction or any other transaciton, calling this method 
     * results in no effect.
     */
    public void addRelated( TransactionContext tx, EntityInfo info, 
            Object foreignId, EntityFieldInfo fieldInfo, List list ) {
        // should I group foreignId, fieldInfo, info and list into
        // an object? And, call it "ManyRelation"?
    }


    public Persistence getPersistence( Class cls ) {
        TypeInfo type = (TypeInfo) _typeInfo.get( cls.getName() );
        if ( type != null )
            return type.persist;

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
     * @param oid The identity of the object to load
     * @param object The type of the object to load
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
    public void load( TransactionContext tx, Entity entity, AccessMode accessMode, int timeout )
            throws ObjectNotFoundException, LockNotGrantedException, PersistenceException,
            ClassNotPersistenceCapableException, ObjectDeletedWaitingForLockException {

        OID        lockedOid;
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object[]   fields;
        boolean    write;
        boolean    succeed;
        short      action;

        typeInfo = (TypeInfo) _typeInfo.get( entity.info );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( 
                Messages.format("persist.classNotPersistenceCapable", entity.info ) );

        //AccessMode accessMode = typeInfo.molder.getAccessMode( suggestedAccessMode );

        succeed = false;

        lock = null;

        try {

            if ( accessMode == AccessMode.Exclusive || accessMode == AccessMode.DbLocked )
                action = ObjectLock.ACTION_WRITE;
            else
                action = ObjectLock.ACTION_READ;

            lock = typeInfo.acquire( entity.identity, tx, action, timeout );

            //lockedOid = lock.getOID();
            //Object stamp = 
            typeInfo.persist.load( tx, _conns.get( tx ), entity, accessMode );

            // proposal change: lockedOid parameter is not really neccesary.
            // we can added getOID() method in DepositBox. It make code a little
            // bit clear?
            // And should ClassMolder the one who set stamp?

            succeed = true;

            //lockedOid.setStamp( stamp );

            //if ( lockedOid != null )
            //    oid = lockedOid;

            if ( _logInterceptor != null )
                _logInterceptor.loading( entity.info, entity.identity );
        } catch ( ObjectDeletedWaitingForLockException except ) {
            // This is equivalent to object does not exist
            throw new ObjectNotFoundException( 
            Messages.format("persist.objectNotFound", entity.info, entity.identity ));
        } finally {
            if ( lock != null ) lock.confirm( tx, succeed );
        }
        //return oid;
    }

    /**
     * Creates a new object in the persistence storage. The object must not 
     * be persistent and must have a unique identity within this engine.
     * If the identity is specified the object is created in
     * persistent storage immediately with the identity. If the
     * identity is not specified, the object is created only when the
     * transaction commits. The object's OID is returned. The OID is
     * guaranteed to be unique for this engine even if no identity was
     * specified.
     *
     * @param tx The transaction context
     * @param oid The identity of the object, or null
     * @param object The newly created object
     * @return The object's OID
     * @throws DuplicateIdentityException An object with this identity
     *  already exists in persistent storage
     * @throws PersistenceException An error reported by the
     *  persistence engine
     * @throws ClassNotPersistenceCapableException The class is not
     *  persistent capable
     */
    public void create( TransactionContext tx, Entity entity )
            throws DuplicateIdentityException, PersistenceException,
            ClassNotPersistenceCapableException {

        TypeInfo typeInfo;
        ObjectLock lock;
        Object[] fields;
        OID lockedOid;
        OID newoid;
        boolean succeed;

        typeInfo = (TypeInfo) _typeInfo.get( entity.info );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( 
            Messages.format( "persist.classNotPersistenceCapable", entity.info) );
            
        boolean write = true;   // just for readability

        lock = null;

        if ( entity != null ) {

            lock = null;

            succeed = false;

            try {

                lock = typeInfo.acquire( entity.identity, tx, ObjectLock.ACTION_CREATE, 0 );

                if ( _logInterceptor != null )
                    _logInterceptor.creating( entity.info, entity.identity );

                //oid = lock.getOID();

                typeInfo.persist.create( tx, _conns.get( tx ), entity );

                succeed = true;

                //oid.setDbLock( true );
            
                //return oid;
                // should catch some other exception if destory is not succeed
            } catch ( LockNotGrantedException except ) {
                // Someone else is using the object, definite duplicate key
                throw new DuplicateIdentityException( Messages.format( 
                    "persist.duplicateIdentity", entity.info, 
                    entity.identity ) );
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
                    _logInterceptor.creating( entity.info, entity.identity );

                //oid = lock.getOID();

                //Object newids = 
                typeInfo.persist.create( tx, _conns.get( tx ), entity );

                lock = typeInfo.acquire( entity.identity, tx, ObjectLock.ACTION_CREATE, 0 );

                succeed = true;

                //oid.setDbLock( true );

                //newoid = new OID( oid.getLockEngine(), oid.getMolder(), oid.getDepends(), newids );

                //typeInfo.rename( oid, newoid, tx );

                //return newoid;
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
     * @param oid The object's identity
     * @param object The object type
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public void delete( TransactionContext tx, Entity entity )
            throws PersistenceException {
        ObjectLock lock;
        TypeInfo   typeInfo;
        Object[]   fields;

        typeInfo = (TypeInfo) _typeInfo.get( entity.info );

        try {
            lock = typeInfo.assure( entity.identity, tx, true );

            if ( _logInterceptor != null )
                _logInterceptor.removing( entity.info, entity.identity );

            typeInfo.persist.delete( tx, _conns.get( tx ), entity );

        } catch ( LockNotGrantedException except ) {
            throw new IllegalStateException( Messages.format( "persist.internal",
                                                              "Attempt to delete object for which no lock was acquired" ) );
        }
    }

    /*
    public void markDelete( TransactionContext tx, OID oid, Object object, int timeout )
            throws PersistenceException, LockNotGrantedException {

        ObjectLock lock;
        TypeInfo   typeInfo;
        Object[]   fields;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getName() );

        lock = typeInfo.upgrade( oid, tx, timeout );

        typeInfo.molder.markDelete( tx, oid, lock, object );
    }*/


    /**
     * Updates an existing object to this engine. The object must not be
     * persistent and must not have the identity of a persistent object.
     * The object's OID is returned. The OID is guaranteed to be unique
     * for this engine even if no identity was specified.
     * If the object implements TimeStampable interface, verify
     * the object's timestamp.
     *
     * @param tx The transaction context
     * @param oid The object's identity
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
     /*
    public void update( TransactionContext tx, Entity entity, AccessMode suggestedAccessMode, int timeout )
            throws ObjectNotFoundException, LockNotGrantedException, ObjectModifiedException,
                   PersistenceException, ClassNotPersistenceCapableException,
                   ObjectDeletedWaitingForLockException {

        TypeInfo    typeInfo;
        Object      identity;
        ObjectLock  lock;
        boolean     succeed;
        // [oleg] these variables are not used
        //boolean    write;
        //AccessMode accessMode;

        // If the object is new, don't try to load it from the cache

        typeInfo = (TypeInfo) _typeInfo.get( entity.identity );
        if ( typeInfo == null )
            throw new ClassNotPersistenceCapableException( 
            Messages.format("persist.classNotPersistenceCapable", entity.info ) );

        //accessMode = typeInfo.molder.getAccessMode( suggestedAccessMode );
        //write = ( accessMode == AccessMode.Exclusive || accessMode == AccessMode.DbLocked );
        succeed = false;
        lock = null;
        try {
            lock = typeInfo.acquire( entity.identity, tx, ObjectLock.ACTION_UPDATE, timeout );

            *//*
            if ( write && ! oid.isDbLock() ) {
                // Db-lock mode we always synchronize the object with
                // the database and obtain a lock on the object.
                if ( _logInterceptor != null )
                    _logInterceptor.loading( typeInfo.javaClass, OID.flatten( oid.getIdentities() ) );
            }*/
            //oid = lock.getOID();

            /*
            //typeInfo.molder.update( tx, oid, lock, object, suggestedAccessMode );
            if ( lock.getObject( tx ) == null ) {
                Entity newEntity = new Entity();
                newEntity.identity = entitiy.identity;
                persist.load( tx, _conns.get( tx ), newEntity, sugguestedAccessMode );
                succeed = true;

                lock.setObject( tx, newEntity );

                if ( !entity.equals( newEntity ) )
                    // | add error message here
                    throw new ObjectModifiedException();
            } else {
                if ( !entity.equals( lock.getObject() ) )
                    throw new ObjectModifiedException();

            }

            *//*
            if ( accessMode == AccessMode.DbLocked )
                oid.setDbLock( true );
             */
            /*
            if ( accessMode == AccessMode.ReadOnly )
                typeInfo.release( oid, tx );
            */
            /*
            //return oid;
        } catch ( ObjectModifiedException e ) {
            throw e;
        } catch ( ObjectDeletedWaitingForLockException except ) {
            // This is equivalent to object not existing
            throw new ObjectNotFoundException( Messages.format("persist.objectNotFound", entity.info, entity.identity) );
        } finally {
            if ( lock != null )
                lock.confirm( tx, succeed );
        }
    }*/


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
     * @param oid The object's identity
     * @param object The object to store
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
     /*
    public void preStore( TransactionContext tx, Entity entity, int timeout ) 
            throws LockNotGrantedException, PersistenceException {


        TypeInfo    typeInfo;
        ObjectLock  lock = null;
        boolean     modified;

        typeInfo = (TypeInfo) _typeInfo.get( entity.info );

        // Acquire a read lock first. Only if the object has been modified
        // do we need a write lock.

        // 092: oid = new OID( this, typeInfo.molder, oid.getIdentity() );

        // acquire read lock
        // getLockedField();
        // isPersistFieldChange()?
        // if no, return null
        // if yes, get flattened fields, 
        // acquire write lock
        // setLockedField( );
        try {
            lock = typeInfo.assure( entity.identity, tx, false );

            //oid = lock.getOID();

            modified = typeInfo.molder.preStore( tx, oid, lock, object, timeout );
        } catch ( LockNotGrantedException e ) {
            throw e;
        } catch ( ObjectModifiedException e ) {
            lock.invalidate( tx );
            throw e;
        } catch ( ObjectDeletedException e ) {
            lock.delete( tx );
            throw e;
        }

        if ( modified )
            return oid;
        else
            return null;
    }*/

    public void store( TransactionContext tx, Entity entity ) 
            throws LockNotGrantedException, ObjectDeletedException,
            ObjectModifiedException, DuplicateIdentityException,
            PersistenceException {

        ObjectLock lock = null;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( entity.info );
        // Attempt to obtain a lock on the database. If this attempt
        // fails, release the lock and report the exception.

        try {
            lock = typeInfo.assure( entity.identity, tx, true );

            if ( _logInterceptor != null )
                _logInterceptor.storing( entity.info, entity.identity );

            typeInfo.persist.store( tx, _conns.get( tx ), entity, (Entity) lock.getObject( tx ) );

            lock.checkin( tx, entity );

        } catch ( ObjectModifiedException e ) {
            lock.invalidate( tx );
            throw e;
        } catch ( DuplicateIdentityException e ) {
            throw e;
        } catch ( LockNotGrantedException e ) {
            throw e;
        } catch ( PersistenceException e ) {
            lock.invalidate( tx );
            throw e;
        } 
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

        typeInfo = (TypeInfo) _typeInfo.get( oid.getName() );
        // Attempt to obtain a lock on the database. If this attempt
        // fails, release the lock and report the exception.

        try {
            typeInfo.upgrade( oid, tx, timeout );

            //typeInfo.persist.writeLock( tx, lock...);
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

        typeInfo = (TypeInfo) _typeInfo.get( oid.getName() );
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
     /*
    public void revertObject( TransactionContext tx, OID oid, Object object )
            throws PersistenceException {
        TypeInfo   typeInfo;
        ObjectLock lock;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getName() );
        try {
            lock = typeInfo.assure( oid, tx, false );
            typeInfo.molder.revertObject( tx, oid, lock, object );
        } catch ( LockNotGrantedException e ) {
            throw new IllegalStateException("Write Lock expected!");
        } catch ( PersistenceException except ) {
            //typeInfo.destory( oid, tx );
            throw except;
        }
    } */

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
     /*
    public void updateCache( TransactionContext tx, OID oid, Object object ) {
        TypeInfo   typeInfo;
        Object[]   fields;
        ObjectLock lock;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getName() );
        try {
            lock = typeInfo.assure( oid, tx, true );
            typeInfo.molder.updateCache( tx, oid, lock, object );
        } catch ( LockNotGrantedException e ) {
            throw new IllegalStateException("Write Lock expected!");
        } catch ( PersistenceException except ) {
            typeInfo.delete( oid, tx );
        }
    } */

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
        typeInfo = (TypeInfo) _typeInfo.get( oid.getName() );
        lock = typeInfo.release( oid, tx );
        // 092: lock.getOID().setDbLock( false );
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
     /*
    public void forgetObject( TransactionContext tx, OID oid ) {
        ObjectLock lock;
        Object[]   fields;
        TypeInfo   typeInfo;

        typeInfo = (TypeInfo) _typeInfo.get( oid.getName() );
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
    } */

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
     * (class's full name). This information includes the object's descriptor and
     * lifecycle interceptor requesting notification about activities
     * that affect an object.
     */
    private class TypeInfo {

        /**
         * The molder for this class.
         */
        private final Persistence persist;

        /**
         * The full qualified name of the Java class represented by this type info.
         */
        private final String name;

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
        private TypeInfo( String name, Persistence persist, HashMap locks, LRU cache ) {
            this.name = name;
            this.persist = persist;
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
         /*
        private TypeInfo( ClassMolder molder, TypeInfo base ) {
            this( molder, base.locks, base.cache );
        }*/

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
        private ObjectLock acquire( Object oid, TransactionContext tx, short lockAction, 
                int timeout ) throws ObjectDeletedWaitingForLockException, 
                LockNotGrantedException, ObjectDeletedException {
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
                    entry.acquireLoadLock( tx, false, timeout );
                    break;

                case ObjectLock.ACTION_WRITE:
                    entry.acquireLoadLock( tx, true, timeout );
                    break;

                case ObjectLock.ACTION_CREATE:
                    entry.acquireCreateLock( tx );
                    break;

                case ObjectLock.ACTION_UPDATE:
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
                            locks.remove( oid );
                            cache.put( oid, entry );
                        }
                    }
                }
            }
        }

        /**
         * Upgrade the lock to write lock.

         * @param  oid  the OID of the lock
         * @param  tx   the transaction in action
         * @param  timeout  time limit
         */
        private ObjectLock upgrade( Object oid, TransactionContext tx, int timeout ) 
                throws ObjectDeletedWaitingForLockException, LockNotGrantedException {
            ObjectLock entry = null;
            synchronized ( this ) {
                entry = (ObjectLock) locks.get( oid );
                if ( entry == null ) 
                    throw new ObjectDeletedWaitingForLockException("Lock entry not found. Deleted?");
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
        private synchronized ObjectLock assure( Object oid, TransactionContext tx, boolean write ) 
                throws ObjectDeletedWaitingForLockException, LockNotGrantedException {
            ObjectLock entry = (ObjectLock) locks.get( oid );
            if ( entry == null ) 
                throw new IllegalStateException("Lock, "+oid+", doesn't exist or no lock!");
            if ( !entry.hasLock( tx, write ) )
                throw new IllegalStateException("Transaction "+tx+" does not hold the "+(write?"write":"read")+" lock: "+entry+"!");
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
         /*
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
        } */

        /**
         * Delete the object lock. It's called after the object is 
         * deleted from the persistence and the transaction committed.
         *
         * @param oid is the OID of the ObjectLock
         * @param tx is the transactionContext of transaction in action
         *
         */
        private ObjectLock delete( Object oid, TransactionContext tx ) {
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
        private ObjectLock release( Object oid, TransactionContext tx ) {
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
                        cache.put( oid, entry );
                        locks.remove( oid );
                    }
                }
            }
        }
        
    }
}
