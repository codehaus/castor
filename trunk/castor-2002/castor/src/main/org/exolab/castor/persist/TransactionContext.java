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


import java.util.Hashtable;
import java.util.Enumeration;
import javax.transaction.Status;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAResource;
import org.exolab.castor.mapping.ObjectDesc;


/**
 * A transaction context is required in order to perform operations
 * against the database. The transaction context is mapped to {@link
 * org.odmg.Transaction} for the ODMG API and into {@link
 * javax.transaction.xa.XAResource} for XA databases. The only way
 * to begin a new transaction is through the creation of a new
 * transaction context. All database access must be performed
 * through a transaction context.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class TransactionContext
{


    /**
     * IMPLEMENTATION NOTES:
     *
     * An object is considered persistent only if it was queried or created
     * within the context of this transaction. An object is not persistent
     * if it was queried or created by another transactions. An object is
     * not persistent if it was queried with read-only access.
     *
     * A read lock is implicitly obtained on any object that is queried,
     * and a write lock on any object that is queried in exclusive mode,
     * created or deleted in this transaction. The lock can be upgraded to
     * a write lock.
     *
     * The validity of locks is dependent on the underlying persistence
     * engine the transaction mode. Without persistence engine locks
     * provide a strong locking mechanism, preventing objects from being
     * deleted or modified in one transaction while another transaction
     * is looking at them. With a persistent engine in exclusive mode, locks
     * exhibit the same behavior. With a persistent engine in read/write
     * mode or a persistent engine that does not support locking (e.g. LDAP)
     * an object may be deleted or modified concurrently through a direct
     * access mechanism.
     */


    /**
     * Defines the types of access allowed on an object. Determines
     * what locks to use when retrieving the object in a transaction.
     */
    public static class AccessMode
    {
	
	/**
	 * Object is retrieved for read-write, a read lock is acquired.
	 */    
	public static final int ReadWrite = 0;
	
	/**
	 * Object is retrieved for exclusive access, a write lock is acquired
	 * and the object is always synchronized with  persistent storage.
	 */
	public static final int Exclusive = 1;
	
	/**
	 * Object is retrieved for read only access, no lock is acquired and
	 * changes to the object are not reflected when the transaction commits.
	 */
	public static final int ReadOnly = 2;
	
    }


    /**
     * Set while transaction is waiting for a lock.
     *
     * @see #getWaitOnLock
     * @see ObjectLock
     */
    private ObjectLock  _waitOnLock;


    /**
     * Collection of objects accessed during this transaction.
     * The object is used as key and {@link ObjectEntry} is the value.
     * @see #addObjectEntry
     */
    private Hashtable   _objects = new Hashtable();


    private Hashtable   _caches = new Hashtable();


    /**
     * The transaction status.
     */
    private int         _status;


    private int         _lockTimeout = 30;


    private Xid         _xid;


    /**
     * Create a new transaction context. This method is used by the
     * explicit transaction model.
     */
    public TransactionContext()
    {
	_status = Status.STATUS_ACTIVE;
    }


    /**
     * Create a new transaction context. This method is used by the
     * XA transaction model, see {@link XAResourceImpl}.
     */
    public TransactionContext( Xid xid )
    {
	_xid = xid;
	_status = Status.STATUS_ACTIVE;
    }


    public int getLockTimeout()
    {
	return _lockTimeout;
    }


    public void setLockTimeout( int timeout )
    {
	_lockTimeout = ( timeout >= 0 ? timeout : 0 );
    }


    /**
     * Loads the object from the database based on the identity of
     * the object. If the object is loaded for read-only then no
     * lock is acquired and updates to the object are not reflected
     * at commit time. If the object is loaded for read-write then a
     * read lock is acquired (unless timeout or deadlock detected) and
     * the object is stored at commit time. The object is then
     * considered persistent and may be deleted or upgraded to write
     * lock. If the object is loaded for exclusive access then a write
     * lock is acquired and the object is synchronized with the
     * persistent copy.
     * <p>
     * This method may be called multiple times for an object in the
     * same transaction and will always behave as if called on the
     * object for the first time. On the first call within a transaction
     * the object will be loaded and true returned. On subsequent calls
     * the object will not be loaded and false returned.
     * <p>
     * Attempting to load the object twice in the same transaction, once
     * with exclusive lock and once with read-write lock will result in
     * an exception.
     *
     * @param cache The cache engine
     * @param obj The object to load
     * @param identity The object's identity
     * @param accessMode The access mode (see {@link AccessMode})
     * @return True if the object contents has been modified
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws ObjectNotFoundException The object was not found in
     *  persistent storage
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public synchronized boolean load( CacheEngine cache, Object obj,
				      Object identity, int accessMode )
	throws TransactionNotInProgressException, ObjectNotFoundException,
	       LockNotGrantedException, PersistenceException
    {
	ObjectEntry entry;
	OID         oid;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();

	// Handle the case where object has already been loaded in
	// the context of this transaction. The case where object
	// has been loaded in another transaction is handled by the
	// locking mechanism.
	entry = getObjectEntry( obj );
	if ( entry != null ) {
	    // If the object has been loaded in this transaction from a
	    // different engine this is an error. If the object has been
	    // deleted in this transaction, it cannot be re-loaded. If the
	    // object has been created in this transaction, it cannot be
	    // re-loaded but no error is reported.
	    if ( entry.cache != cache )
		throw new PersistenceException( "persist.multipleLoad", obj.getClass(), identity );
	    if ( entry.deleted )
		throw new ObjectNotFoundException( obj.getClass(), identity );
	    if ( obj.getClass() != entry.obj.getClass() )
		throw new PersistenceException( "persist.typeMismatch", obj.getClass(), entry.obj.getClass() );
	    if ( entry.created )
		return false;
	    if ( accessMode == AccessMode.Exclusive && ! entry.oid.isExclusive() ) {
		// If we are in exclusive mode and object has not been
		// loaded in exclusive mode before, then we have a
		// problem. We cannot return an object that is not
		// synchronized with the database, but we cannot
		// synchronize a live object.
		throw new PersistenceException( "persist.lockConflict",
						obj.getClass(), identity );
	    }
	    return false;
	}

	// Load (or reload) the object through the cache engine with the
	// requested lock. This might report failure (object no longer exists),
	// hold until a suitable lock is granted (or fail to grant), or
	// report error with the persistence engine.
	try {
	    oid = cache.load( this, obj.getClass(), identity,
			      ( accessMode == AccessMode.Exclusive ),
			      _lockTimeout );
	} catch ( ObjectNotFoundException except ) {
	    removeObjectEntry( obj );
	    throw except;
	} catch ( LockNotGrantedException except ) {
	    throw except;
	} catch ( ClassNotPersistenceCapableException except ) {
	    throw new PersistenceException( except );
	}

	// Need to copy the contents of this object from the cached
	// copy and deal with it based on the transaction semantics.
	// If the mode is read-only we release the lock and forget about
	// it in the contents of this transaction. Otherwise we record
	// the object in this transaction. 
	cache.copyObject( this, oid, obj );
	if ( entry == null ) {
	    if ( accessMode == AccessMode.ReadOnly ) {
		cache.releaseLock( this, oid );
	    } else {
		entry = addObjectEntry( obj, oid, cache );
	    }
	}
	return true;
    }


    /**
     * Perform a query using the query mechanism and in the specified
     * access mode. The query is performed in this transaction, and
     * the returned query results can only be used while this
     * transaction is open. It is assumed that the query mechanism is
     * compatible with the cache engine.
     * 
     * @param cache The cache engine
     * @param query A query against the cache engine
     * @param accessMode The access mode
     * @return A query result iterator
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws QueryException Could not process the query
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public synchronized QueryResults query( CacheEngine cache, Query query, int accessMode )
	throws TransactionNotInProgressException, QueryException,
	       PersistenceException
    {
	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();
	// Need to execute query at this point. This will result in a
	// new result set from the query, or an exception.
	query.execute( getConnection( cache ), ( accessMode == AccessMode.Exclusive ) );
	return new QueryResults( this, cache, query, accessMode );
    }


    /**
     * Creates a new object in persistent storage and returns the
     * object's OID. The object will be persisted only if the
     * transaction commits. If an identity is provided then duplicate
     * identity check happens in this method, if no identity is
     * provided then duplicate identity check occurs when the
     * transaction completes and the object is not visible in this
     * transaction.
     *
     * @param cache The cache engine
     * @param obj The object to persist
     * @param identity The object's identity (may be null)
     * @return The object's OID
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws DuplicateIdentityException An object with this identity
     *  already exists in persistent storage
     * @throws PersistenceException An error reported by the
     *  persistence engine
     * @throws ClassNotPersistenceCapableException The class is not
     *  persistent capable
     */
    public synchronized OID create( CacheEngine cache, Object obj, Object identity )
	throws TransactionNotInProgressException, DuplicateIdentityException,
	       ClassNotPersistenceCapableException, PersistenceException
    {
	OID         oid;
	ObjectEntry entry;
	ObjectDesc  objDesc;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();
	// Make sure the object has not beed persisted in this transaction.
	if ( getObjectEntry( obj ) != null )
	    throw new PersistenceException( "persist.objectAlreadyPersistent", obj, identity );
	objDesc = cache.getObjectDesc( obj.getClass() );
	if ( objDesc == null )
	    throw new ClassNotPersistenceCapableException( obj.getClass() );

	// Create the object. This can only happen once for each object in
	// all transactions running on the same engine, so after creation
	// add a new entry for this object and use this object as the view
	if ( identity != null && getObjectEntry( cache, new OID( objDesc, identity ) ) != null )
	    throw new DuplicateIdentityException( obj.getClass(), identity );
	oid = cache.create( this, obj, identity );
	entry = addObjectEntry( obj, oid, cache );
	entry.created = true;
	return oid;
    }


    /**
     * Deletes the object from persistent storage. The deletion will
     * take effect only if the transaction is committed, but the
     * object is no longer viewable for the current transaction and
     * locks for access from other transactions will block until this
     * transaction completes. A write lock is acquired in order to
     * assure the object can be deleted.
     *
     * @param obj The object to delete from persistent storage
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ObjectNotPersistentException The object has not been
     *  queried or created in this transaction
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public synchronized void delete( Object obj )
	throws TransactionNotInProgressException, ObjectNotPersistentException,
	       LockNotGrantedException, PersistenceException
    {
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE )
 	    throw new TransactionNotInProgressException();
	// Get the entry for this object, if it does not exist
	// the object has never been persisted in this transaction
	entry = getObjectEntry( obj );
	if ( entry == null )
	    throw new ObjectNotPersistentException( obj.getClass() );
	// Cannot delete same object twice
	if ( entry.deleted )
	    throw new PersistenceException( "persist.cannotDeleteTwice",
					    obj.getClass(), entry.oid.getIdentity() );
	// If the object has been created in this transaction and had no
	// identity we don't need to remove it from persistent storage
	if ( entry.oid.getIdentity() == null ) {
	    entry.cache.releaseLock( this, entry.oid );
	    removeObjectEntry( obj );
	    return;
	}
	// Must acquire a write lock on the object in order to delete it,
	// prevents object form being deleted while someone else is
	// looking at it.
	try {
	    entry.cache.writeLock( this, entry.oid, _lockTimeout );
	    // Mark object as deleted. This will prevent it from being viewed
	    // in this transaction and will handle it properly at commit time.
	    // The write lock will prevent it from being viewed in another
	    // transaction.
	    entry.deleted = true;
	} catch ( ObjectDeletedException except ) {
	    // Object has been deleted outside this transaction,
	    // forget about it
	    removeObjectEntry( obj );
	}
    }


    /**
     * Acquire a write lock on the object. Read locks are implicitly
     * available when the object is queried. A write lock is only
     * granted for objects that are created or deleted or for objects
     * loaded in exclusive mode - this method can obtain such a lock
     * explicitly. If the object already has a write lock in this
     * transaction or a read lock in this transaction but no read lock
     * in any other transaction, a write lock is obtained. If this
     * object has a read lock in any other transaction this method
     * will block until the other transaction will release its lock.
     * If the specified timeout has elapsed or a deadlock has been
     * detected, an exception will be thrown but the current lock will
     * be retained. 
     *
     * @param obj The object to lock
     * @param timeout Timeout waiting to acquire lock, specified in
     *  seconds, zero for no waiting, negative to use the default
     *  timeout for this transaction
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ObjectNotPersistentException The object has not been
     *  queried or created in this transaction
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public synchronized void writeLock( Object obj, int timeout )
	throws TransactionNotInProgressException, ObjectNotPersistentException,
	       LockNotGrantedException, PersistenceException
    {
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();
	// Get the entry for this object, if it does not exist
	// the object has never been persisted in this transaction
	entry = getObjectEntry( obj );
	if ( entry == null || entry.deleted )
	    throw new ObjectNotPersistentException( obj.getClass() );
	try {
	    entry.cache.writeLock( this, entry.oid, timeout );
	} catch ( ObjectDeletedException except ) {
	    // Object has been deleted outside this transaction,
	    // forget about it
	    removeObjectEntry( obj );
	    throw new ObjectNotPersistentException( obj.getClass() );
	} catch ( LockNotGrantedException except ) {
	    // Can't get lock, but may still keep running
	    throw except;
	}
    }


    /**
     * Releases the lock granted on the object. The object is removed
     * from this transaction and will not participate in transaction
     * commit/abort. Any changes done to the object are lost.
     *
     * @param obj The object to release the lock
     * @throws  TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ObjectNotPersistentException The object was not queried
     *   or created in this transaction
     * @throws PersistenceException An error occured talking to the
     *   persistence engine
     */
    public synchronized void release( Object obj )
	throws TransactionNotInProgressException, ObjectNotPersistentException,
	       PersistenceException
    {
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();
	// Get the entry for this object, if it does not exist
	// the object has never been persisted in this transaction
	entry = getObjectEntry( obj );
	if ( entry == null || entry.deleted )
	    throw new ObjectNotPersistentException( obj.getClass() );
	// Release the lock, forget about the object in this transaction
	entry.cache.releaseLock( this, entry.oid );
	removeObjectEntry( obj );
    }


    /**
     * Prepares the transaction prior to committing it. Indicates
     * whether the transaction is read-only (i.e. no modified objects),
     * can commit, or an error has occured and the transaction must
     * be rolled back. This method performs actual storage into the
     * persistence storage. Multiple calls to this method can be done,
     * and do not release locks, allowing <tt>checkpoint</tt> to
     * occur.
     *
     * @return True if the transaction can commit, false if the
     *   transaction is read only
     * @throws  TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws TransactionAbortedException The transaction has been
     *   aborted due to inconsistency, duplicate object identity, error
     *   with the persistence engine or any other reason
     */
    public synchronized boolean prepare()
	throws TransactionNotInProgressException, TransactionAbortedException
    {
	Enumeration enum;
	ObjectEntry entry;
	Object      obj;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();
	if ( _status == Status.STATUS_MARKED_ROLLBACK ) {
	    throw new TransactionAbortedException( "persist.markedRollback" );
	}

	// No objects in this transaction -- this is a read only transaction
	if ( _objects.size() == 0 ) {
	    _status = Status.STATUS_PREPARED;
	    return false;
	}

	try {
	    _status = Status.STATUS_PREPARING;
	    enum = _objects.elements();
	    while ( enum.hasMoreElements() ) {
		entry = (ObjectEntry) enum.nextElement();
		// If the object has been deleted, it is removed from the
		// underlying database. This call will detect duplicate
		// removal attempts. Otherwise the object is stored in
		// the database.
		if ( entry.deleted ) {
		    entry.cache.delete( this, entry.oid );
		} else {
		    Object     identity;
		    ObjectDesc objDesc;

		    // When storing the object it's OID might change
		    // if the primary identity has been changed
		    objDesc = entry.cache.getObjectDesc( entry.obj.getClass() );
		    identity = objDesc.getIdentityField().getValue( entry.obj );
		    if ( identity == null )
			throw new TransactionAbortedException( "persist.noIdentity", 
							       objDesc.getObjectType(), null );
		    entry.oid = entry.cache.store( this, entry.oid, entry.obj, identity, _lockTimeout );
		}
	    }
	    _status = Status.STATUS_PREPARED;
	    return true;
	} catch ( Exception except ) {
	    _status = Status.STATUS_MARKED_ROLLBACK;
	    // Any error is reported as transaction aborted
	    throw new TransactionAbortedException( except );
	}
    }


    /**
     * Commits all changes but does not closes the transaction and does
     * not releases any locks. All objects remain persistent, except for
     * objects deleted during the transaction. May be called any number
     * of times prior to committing/aborting the transaction.
     *
     * @throws  TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws TransactionAbortedException The transaction has been
     *   aborted due to inconsistency, duplicate object identity, error
     *   with the persistence engine or any other reason
     */
    public void checkpoint()
	throws TransactionNotInProgressException, TransactionAbortedException
    {
	Enumeration enum;
	ObjectEntry entry;

	if ( _xid != null )
	    throw new TransactionNotInProgressException( "persist.checkpointNotSupported" );
	// Never commit transaction that has been marked for rollback
	if ( _status == Status.STATUS_MARKED_ROLLBACK ) {
	    rollback();
	    throw new TransactionAbortedException( "persist.markedRollback" );
	}
	// Prepare the transaction
	prepare();

	try {
	    _status = Status.STATUS_COMMITTING;

	    // Go through all the connections opened in this transaction,
	    // commit and close them one by one.
	    commitConnections( true );

	    // Assuming all went well in the connection department,
	    // no deadlocks, etc. clean all the transaction locks with
	    // regards to the cache engine.
	    enum = _objects.elements();
	    while ( enum.hasMoreElements() ) {
		entry = (ObjectEntry) enum.nextElement();
		if ( entry.deleted ) {
		    // Object has been deleted inside transaction,
		    // engine must forget about it.
		    entry.cache.forgetObject( this, entry.oid );
		    removeObjectEntry( entry.obj );
		} else {
		    // Object has been created/accessed inside the
		    // transaction must retain the database lock
		    // (which is always write lock since object was
		    // just updated)
		    entry.cache.writeLock( this, entry.oid, 0 );
		    entry.created = false;
		}
	    }
	    _status = Status.STATUS_ACTIVE;
	} catch ( Exception except ) {
	    // Any error that happens, we're going to rollback the transaction.
	    _status = Status.STATUS_MARKED_ROLLBACK;
	    rollback();
	    throw new TransactionAbortedException( except );
	}
    }


    /**
     * Commits all changes and closes the transaction releasing all
     * locks on all objects. All objects are now transient. Must be
     * called after a call to {@link prepare} has returned successfully.
     *
     * @throws TransactionAbortedException The transaction has been
     *   aborted due to inconsistency, duplicate object identity, error
     *   with the persistence engine or any other reason
     * @throws IllegalStateException This method has been called
     *   without calling {@link #prepare} first
     */
    public synchronized void commit()
	throws TransactionNotInProgressException, TransactionAbortedException
    {
	Enumeration enum;
	ObjectEntry entry;

	// Never commit transaction that has been marked for rollback
	if ( _status == Status.STATUS_MARKED_ROLLBACK ) {
	    rollback();
	    throw new TransactionAbortedException( "persist.markedRollback" );
	}
	if ( _status != Status.STATUS_PREPARED )
	    throw new IllegalStateException( "persist.missingPrepare" );

	try {
	    _status = Status.STATUS_COMMITTING;

	    // Go through all the connections opened in this transaction,
	    // commit and close them one by one.
	    commitConnections( false );

	    // Assuming all went well in the connection department,
	    // no deadlocks, etc. clean all the transaction locks with
	    // regards to the cache engine.
	    enum = _objects.elements();
	    while ( enum.hasMoreElements() ) {
		entry = (ObjectEntry) enum.nextElement();
		if ( entry.deleted ) {
		    // Object has been deleted inside transaction,
		    // engine must forget about it.
		    entry.cache.forgetObject( this, entry.oid );
		} else {
		    // Object has been created/accessed inside the
		    // transaction, release its lock.
		    entry.cache.updateObject( this, entry.oid, entry.obj );
		    entry.cache.releaseLock( this, entry.oid );
		}
	    }
	    // Forget about all the objects in this transaction,
	    // and mark it as completed.
	    _objects.clear();
	    _caches.clear();
	    _status = Status.STATUS_COMMITTED;

	} catch ( Exception except ) {
	    // Any error that happens, we're going to rollback the transaction.
	    _status = Status.STATUS_MARKED_ROLLBACK;
	    rollback();
	    throw new TransactionAbortedException( except );
	}
    }


    /*
     * Rolls back all changes and closes the transaction releasing all
     * locks on all objects. All objects are now transient and
     * reverted to their previous values, if they were queried in this
     * transaction. This method may be called at any point during the
     * transaction.
     *
     * @throws  TransactionNotInProgressException Method called while
     *   transaction is not in progress
     */
    public synchronized void rollback()
	throws TransactionNotInProgressException
    {
	Enumeration enum;
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE && _status != Status.STATUS_PREPARED &&
	     _status != Status.STATUS_MARKED_ROLLBACK )
	    throw new TransactionNotInProgressException();

	// Go through all the connections opened in this transaction,
	// rollback and close them one by one.
	rollbackConnections();

	// Clean the transaction locks with regards to the
	// database engine
	enum = _objects.elements();
	while ( enum.hasMoreElements() ) {
	    entry = (ObjectEntry) enum.nextElement();
	    try {
		if ( entry.created ) {
		    // Object has been created in this transaction,
		    // it no longer exists, forgt about it in the engine.
		    entry.cache.forgetObject( this, entry.oid );
		} else {
		    // Object has been queried (possibly) deleted in this
		    // transaction, release the lock and revert to the old value.
		    entry.cache.copyObject( this, entry.oid, entry.obj );
		    entry.cache.releaseLock( this, entry.oid );
		}
	    } catch ( Exception except ) { }
	}
	// Forget about all the objects in this transaction,
	// and mark it as completed.
	_objects.clear();
	_caches.clear();
	_status = Status.STATUS_ROLLEDBACK;
    }


    public Object getConnection( CacheEngine cache )
	throws PersistenceException
    {
	return null;
    }


    /**
     * Returns the status of this transaction.
     */
    public int getStatus()
    {
	return _status;
    }


    /**
     * Returns true if the transaction is open.
     */
    public boolean isOpen()
    {
	return ( _status == Status.STATUS_ACTIVE || _status == Status.STATUS_MARKED_ROLLBACK );
    }


    protected void commitConnections( boolean keepOpen )
	throws PersistenceException
    {
    }


    protected void rollbackConnections()
    {
    }


    protected Xid getXid()
    {
	return _xid;
    }


    /**
     * Indicates which lock this transaction is waiting for. When a
     * transaction attempts to acquire a lock it must indicate which
     * lock it attempts to acquire in order to perform dead-lock
     * detection. This method is called by {@link DatabaseEngine}
     * before entering the temporary lock-acquire state.
     *
     * @param lock The lock which this transaction attempts to acquire
     */
    void setWaitOnLock( ObjectLock lock )
    {
	_waitOnLock = lock;
    }


    /**
     * Returns the lock which this transaction attempts to acquire.
     *
     * @return The lock which this transaction attempts to acquire
     */
    ObjectLock getWaitOnLock()
    {
	return _waitOnLock;
    }


    /**
     * Adds a new entry recording the use of the object in this
     * transaction. This is a copy of the object that is only visible
     * (or deleted) in the context of this transaction. The object is
     * not persisted if it has not been recorded in this transaction.
     *
     * @param obj The object to record
     * @param oid The object's OID
     * @param cache The cache engine used to create this object
     */
    ObjectEntry addObjectEntry( Object obj, OID oid, CacheEngine cache )
    {
	ObjectEntry entry;
	Hashtable   cacheOids;
 
	entry = new ObjectEntry();
	entry.cache = cache;
	entry.oid = oid;
	entry.obj = obj;
	_objects.put( obj, entry );
	cacheOids = (Hashtable) _caches.get( cache );
	if ( cacheOids == null ) {
	    cacheOids = new Hashtable();
	    _caches.put( cache, cacheOids );
	}
	cacheOids.put( oid, entry );
	return entry;
    }


    ObjectEntry getObjectEntry( CacheEngine cache, OID oid )
    {
	Hashtable cacheOids;

	cacheOids = (Hashtable) _caches.get( cache );
	if ( cacheOids == null )
	    return null;
	return (ObjectEntry) cacheOids.get( oid );
    }


    /**
     * Returns the entry for the object from the object. If the entry
     * does not exist, the object is not persistent in this
     * transaction. An entry will be returned even if the object has
     * been deleted in this transaction.
     *
     * @param obj The object to locate
     * @return The object's entry or null if not persistent
     */
    ObjectEntry getObjectEntry( Object obj )
    {
	return (ObjectEntry) _objects.get( obj );
    }


    /**
     * Removes the entry for an object and returns it. The object is
     * no longer part of the transaction.
     *
     * @param obj The object to remove
     * @return The removed entry
     */
    private ObjectEntry removeObjectEntry( Object obj )
    {
	ObjectEntry entry;

	entry = (ObjectEntry) _objects.remove( obj );
	if ( entry == null )
	    return null;
	( (Hashtable) _caches.get( entry.cache ) ).remove( entry.oid );
	return entry;
    }


    /**
     * A transaction records all objects accessed during the lifetime
     * of the transaction in this record (queries and created). A
     * single entry exist for each object accessible using the object
     * or it's OID as identities. The entry records the database engine used
     * to persist the object, the object's OID, the object itself, and
     * whether the object has been deleted in this transaction,
     * created in this transaction, or already prepared. Objects
     * identified as read only are not update when the transaction
     * commits.
     */
    static class ObjectEntry
    {

	CacheEngine  cache;

	OID          oid;

	Object       obj;

	boolean      deleted;

	boolean      created;

    }

    
}
