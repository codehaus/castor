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


package org.exolab.castor.jdo.engine;


import java.util.Hashtable;
import java.util.Enumeration;
import java.sql.Connection;
import java.sql.SQLException;
import org.odmg.Database;
import org.odmg.ObjectNotPersistentException;
import org.odmg.ObjectDeletedException;
import org.odmg.LockNotGrantedException;
import org.odmg.ClassNotPersistenceCapableException;
import org.odmg.TransactionNotInProgressException;
import org.odmg.TransactionAbortedException;
import org.odmg.ClassNotPersistenceCapableException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import javax.transaction.Status;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAResource;
import org.exolab.castor.jdo.ODMGSQLException;
import org.exolab.castor.jdo.DuplicatePrimaryKeyException;
import org.exolab.castor.jdo.TransactionAbortedReasonException;


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
final class TransactionContext
{


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


    /**
     * Collection of OIDs accessed during this transaction.
     * The OID is used as key and {@link ObjectEntry} is the value.
     * @see #addObjectEntry
     */
    private Hashtable   _oids = new Hashtable();


    /**
     * The transaction status.
     */
    private int         _status;


    /**
     * Lists all the connections opened for particular database engines
     * used in the lifetime of this transaction. The database engine
     * is used as the key to an open/transactional connection.
     */
    private Hashtable   _conns = new Hashtable();


    /**
     * If this transaction is managed by the transaction monitor
     * through the {@link XAResource} interface, it will have a
     * transaction identifier and will not attempt to commit/rollback
     * directly against the underlying database.
     */
    private Xid         _xid;


    /**
     * Create a new transaction context. This method is used by the
     * ODMG transaction model, see {@link TransactionImpl}.
     */
    public TransactionContext()
    {
	_status = Status.STATUS_ACTIVE;
    }


    /**
     * Create a new transaction context. This method is used by the
     * JTA transaction model, see {@link XAResourceImpl}.
     */
    public TransactionContext( Xid xid )
    {
	_xid = xid;
	_status = Status.STATUS_ACTIVE;
    }


    /**
     * Creates a new object in the database engine. Returns the
     * object's OID. If a primary key was available the object has an
     * OID and can be retrieved in queries running in the same
     * transaction. If no primary key was available, the object is
     * not visible in the transaction. When the transaction completes,
     * the object is persisted in the database and is available to
     * other transactions through the database engine.
     *
     * @param dbEngine The database engine used to persist the object
     * @param obj The object to persist
     * @return The object's OID
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws DuplicatePrimaryKeyException An object with the same
     *   primary key already exists in this transaction or outside it
     * @throws ClassNotPersistenceCapableException Persistence not
     *  supported for this class
     * @throws ODMGRuntimeException The object is already persisted or
     *   an error occured talking to the persistence engine
     */
    public synchronized OID create( DatabaseEngine dbEngine, Object obj, Object primKey )
    throws TransactionNotInProgressException, DuplicatePrimaryKeyException,
	   ClassNotPersistenceCapableException, ODMGRuntimeException
    {
	OID         oid;
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	// Create the object. This can only happen once for each object in
	// all transactions running on the same engine, so after creation
	// add a new entry for this object and use this object as the view
	oid = dbEngine.create( this, obj );
	entry = addObjectEntry( obj, oid, dbEngine );
	entry.created = true;
	return oid;
    }


    /**
     * Deletes the object from persistent storage. A write lock is
     * acquired in order to delete the object. If a write lock is
     * granted the object is no longer visible to queries running in
     * the same transaction. Queries running in other transactions
     * will wait until this transaction aborts/commits before being
     * able to determine whether the object is visible.
     *
     * @param obj The object to delete from persistent storage
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ObjectNotPersistentException The object was not queried
     *   or created in this transaction
     * @throws LockNotGrantedException Attempt to acquire the lock
     *   timed out or a deadlock has been detected
     * @throws ODMGRuntimeException An error occured talking to the
     *   persistence engine
     */
    public synchronized void delete( Object obj )
	throws TransactionNotInProgressException, ObjectNotPersistentException,
	       LockNotGrantedException, ODMGRuntimeException
    {
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE )
 	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	// Get the entry for this object, if it does not exist
	// the object has never been persisted in this transaction
	entry = getObjectEntry( obj );
	if ( entry == null || entry.readOnly )
	    throw new ObjectNotPersistentExceptionImpl( obj );
	// Cannot delete same object twice
	if ( entry.deleted )
	    throw new ObjectNotPersistentExceptionImpl( "This object has already been deleted in this transaction" );
	if ( entry.readOnly )
	// Must acquire a write lock on the object in order to delete it,
	// prevents object form being deleted while someone else is
	// looking at it.
	entry.dbEngine.writeLock( this, entry.oid );
	// Mark object as deleted. This will prevent it from being viewed
	// in this transaction and will handle it properly at commit time.
	// The write lock will prevent it from being viewed in another
	// transaction.
	entry.deleted = true;
    }


    /**
     * Attempt to lock the object. A read lock is granted if the
     * object is not write locked by another transaction. A write lock
     * is granted if the object is not read or write lock by another
     * transaction. If the object has been read locked by this
     * transaction the lock will be upgraded to a write lock only if
     * the object is not read locked by another transaction. If a lock
     * cannot be granted this method will block until the lock is
     * available, the specified timeout has elapsed or a deadlock has
     * been detected.
     *
     * @param obj The object to acquire the lock for
     * @param write Type of lock to acquire, true for a write lock,
     *   false for a read lock
     * @param timeout Timeout waiting to acquire a lock, specified in
     *   milliseconds
     * @throws  TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ObjectNotPersistentException The object was not queried
     *   or created in this transaction
     * @throws LockNotGrantedException Attempt to acquire the lock
     *   timed out or a deadlock has been detected
     */
    public synchronized void lock( Object obj, boolean write, int timeout )
	throws TransactionNotInProgressException, ObjectNotPersistentException,
	       LockNotGrantedException
    {
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	// Get the entry for this object, if it does not exist
	// the object has never been persisted in this transaction
	entry = getObjectEntry( obj );
	if ( entry == null || entry.readOnly )
	    throw new ObjectNotPersistentExceptionImpl( obj );
	// At this point read lock is available on the object (otherwise the
	// object is not persistent), so we only need to acquire a write lock.
	if ( write ) {
	    try {
		entry.dbEngine.writeLock( this, entry.oid );
	    } catch ( ObjectDeletedException except ) {
		throw except;
	    } catch ( LockNotGrantedException except ) {
		throw except;
	    } catch ( ODMGRuntimeException except ) {
		// Any exception other than delete/lock is a lock problem
		throw new LockNotGrantedException( "Lock not granted for the following reason: " +
						   except.toString() );
	    }
	}
    }


    public synchronized void acquire( DatabaseEngine dbEngine, Object obj, Object primKey,
				      boolean write, int timeout )
	throws TransactionNotInProgressException, ObjectNotPersistentException,
	       LockNotGrantedException
    {
	ObjectEntry entry;
	OID         oid;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	// Get the entry for this object, if it exists in this
	// transaction, simply acquire the lock.
	entry = getObjectEntry( obj );
	if ( entry != null ) {
	    entry.readOnly = false;
	    if ( write )
		lock( obj, write, timeout );
	    return;
	}

	// Object not persistent in this transaction, but might be
	// know to the database engine.
	oid = new OID( dbEngine, dbEngine.getObjectDesc( obj.getClass() ), primKey );
	

	if ( entry == null || entry.readOnly )
	    throw new ObjectNotPersistentExceptionImpl( obj );
	// At this point read lock is available on the object (otherwise the
	// object is not persistent), so we only need to acquire a write lock.
	if ( write ) {
	    try {
		entry.dbEngine.writeLock( this, entry.oid );
	    } catch ( ObjectDeletedException except ) {
		throw except;
	    } catch ( LockNotGrantedException except ) {
		throw except;
	    } catch ( ODMGRuntimeException except ) {
		// Any exception other than delete/lock is a lock problem
		throw new LockNotGrantedException( "Lock not granted for the following reason: " +
						   except.toString() );
	    }
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
     * @throws ODMGRuntimeException An error occured talking to the
     *   persistence engine
     */
    public synchronized void unlock( Object obj )
	throws TransactionNotInProgressException, ObjectNotPersistentException,
	       ODMGRuntimeException
    {
	ObjectEntry entry;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	// For the sake of good coding, report if the object is no persistent
	entry = getObjectEntry( obj );
	if ( entry == null || entry.readOnly )
	    throw new ObjectNotPersistentExceptionImpl( obj );
	// Release the lock, forget about the object in this transaction
	entry.dbEngine.releaseLock( this, entry.oid );
	removeObjectEntry( obj );
    }


    /**
     * Loads the object from the database based on the primary key and
     * object type. The object is returned and becomes part of the
     * current transaction. Changes to the object will be reflected in
     * the database when the transaction commits. Typically a read
     * lock will be acquired on the object. If exclusive access is
     * requested, a write lock will be acquired on the object and the
     * object will be synchronized with the database contents prior to
     * acquiring the lock. The semantics of acquiring a lock are
     * similar to {@link #lock}.
     *
     * @param dbEngine The database to retrieve the object from
     * @param type The object's type
     * @param primKey The object's primary key
     * @param openMode The query mode is either read/write, exclusive
     *   or read only
     * @param timeout Timeout waiting to acquire a lock, specified in
     *   milliseconds
     * @return The object, null if the object was not found
     * @throws  TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ODMGRuntimeException An error occured talking to the
     *   persistence engine
     * @throws LockNotGrantedException Timeout or deadlock occured waiting
     *   to obtain lock on object
     * @see #lock
     */
    public synchronized Object load( DatabaseEngine dbEngine, Class type, Object primKey,
			int openMode, int timeout )
	throws TransactionNotInProgressException, LockNotGrantedException,
	       LockNotGrantedException, ODMGException
    {
	Object      obj;
	ObjectEntry entry;
	OID         oid;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	// Load the object through the engine acquiring the proper lock
	// Return null if object not found, otherwise we have a read/write lock
	oid = dbEngine.load( this, type, primKey,
			     ( openMode == Database.OPEN_EXCLUSIVE ), timeout );
	if ( oid == null )
	    return null;

	// It is possible that we already retrieved this object in this
	// transaction, in which case we look up the retrieve record and
	// decide whether to return it or not. Read/only status must be
	// updated according to last access in this transaction.
	entry = getObjectEntry( oid );
	if ( entry != null ) {
	    if ( entry.deleted )
		return null;
	    if ( openMode != Database.OPEN_READ_ONLY )
		entry.readOnly = false;
	    return entry.obj;
	}

	// Need to grab the object from the engine and create a new entry
	// for it. Then need to create a copy of the object and return
	// that copy. With read/only, the read lock is released immediately
	// after query. The object will not be stored unless a subsequent
	// call in a different access mode attempt to re-acquire the lock.
	obj = dbEngine.copyInto( this, oid, null );
	entry = addObjectEntry( obj, oid, dbEngine );
	if ( openMode == Database.OPEN_READ_ONLY ) {
	    entry.readOnly = true;
	    entry.dbEngine.releaseLock( this, entry.oid );
	}	    
	return obj;
    }


    // XXX NOT FULLY IMPLEMENTED
    public synchronized Object query( DatabaseEngine dbEngine, Class type, String sql,
				      Object[] values, int openMode, int timeout )
	throws TransactionNotInProgressException, LockNotGrantedException,
	       LockNotGrantedException, ODMGException
    {
	Object      obj;
	ObjectEntry entry;
	OID         oid;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	// Load the object through the engine acquiring the proper lock
	// Return null if object not found, otherwise we have a read/write lock
	oid = dbEngine.query( this, type, sql, values,
			      ( openMode == Database.OPEN_EXCLUSIVE ), timeout );
	if ( oid == null )
	    return null;

	// It is possible that we already retrieved this object in this
	// transaction, in which case we look up the retrieve record and
	// decide whether to return it or not.
	entry = getObjectEntry( oid );
	if ( entry != null ) {
	    if ( entry.deleted )
		return null;
	    if ( openMode != Database.OPEN_READ_ONLY )
		entry.readOnly = false;
	    return entry.obj;
	}

	// Need to grab the object from the engine and create a new entry
	// for it. Then need to create a copy of the object and return
	// that copy. With read/only, the read lock is released immediately
	// after query. The object will not be stored unless a subsequent
	// call in a different access mode attempt to re-acquire the lock.
	obj = dbEngine.copyInto( this, oid, null );
	entry = addObjectEntry( obj, oid, dbEngine );
	if ( openMode == Database.OPEN_READ_ONLY ) {
	    entry.readOnly = true;
	    entry.dbEngine.releaseLock( this, entry.oid );
	}	    
	return obj;
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
     *   aborted due to inconsistency, duplicate object key, error
     *   with the persistence engine or any other reason
     */
    public synchronized boolean prepare()
	throws TransactionNotInProgressException, TransactionAbortedException
    {
	Enumeration enum;
	ObjectEntry entry;
	Object      obj;
	Connection  conn;
	boolean     readOnly;

	if ( _status != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );
	if ( _status == Status.STATUS_MARKED_ROLLBACK ) {
	    throw new TransactionAbortedException( "Transaction was marked for rollback" );
	}

	try {
	    _status = Status.STATUS_PREPARING;
	    readOnly = true;
	    enum = _objects.elements();
	    while ( enum.hasMoreElements() ) {
		entry = (ObjectEntry) enum.nextElement();
		// If the object has been deleted, it is removed from the
		// underlying database. This call will detect duplicate
		// removal attempts. Otherwise the object is stored in
		// the database.
		if ( ! entry.readOnly ) {
		    if ( entry.deleted ) {
			entry.dbEngine.delete( this, entry.oid );
		    } else {
			// When storing the object it's OID might change
			// if the primary key has been changed
			entry.oid = entry.dbEngine.store( this, entry.oid, entry.obj );
		    }
		}
		// At least one object has been prepared, this is not
		// a read only transaction
		readOnly = false;
	    }
	    _status = Status.STATUS_PREPARED;

	} catch ( Exception except ) {
	    _status = Status.STATUS_MARKED_ROLLBACK;
	    // Any error is reported as transaction aborted
	    throw new TransactionAbortedReasonException( except );
	}
	return ( ! readOnly );
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
     *   aborted due to inconsistency, duplicate object key, error
     *   with the persistence engine or any other reason
     */
    public void checkpoint()
	throws TransactionNotInProgressException, TransactionAbortedReasonException
    {
	Enumeration enum;
	ObjectEntry entry;
	Connection  conn;

	if ( _xid != null )
	    throw new ODMGRuntimeException( "Check points not supported for XA resources" );
	// Never commit transaction that has been marked for rollback
	if ( _status == Status.STATUS_MARKED_ROLLBACK ) {
	    rollback();
	    throw new TransactionAbortedException( "Transaction was marked for rollback and has been rolled back" );
	}
	// Prepare the transaction
	prepare();

	try {
	    _status = Status.STATUS_COMMITTING;

	    // Go through all the connections opened in this transaction,
	    // commit and close them one by one.
	    enum = _conns.elements();
	    while ( enum.hasMoreElements() ) {
		conn = (Connection) enum.nextElement();
		// Checkpoint can only be done if transaction is not running
		// under transaction monitor
		conn.commit();
		conn.setAutoCommit( false );
	    }

	    // Assuming all went well in the RDBMS department, no deadlocks,
	    // etc. clean all the transaction locks with regards to the
	    // database engine.
	    enum = _objects.elements();
	    while ( enum.hasMoreElements() ) {
		entry = (ObjectEntry) enum.nextElement();
		if ( ! entry.readOnly ) {
		    if ( entry.deleted ) {
			// Object has been deleted inside transaction,
			// engine must forget about it.
			entry.dbEngine.forgetObject( this, entry.oid );
		    } else {
			// Object has been created/accessed inside the
			// transaction must retain the database lock.

			// XXX Do we need to acquire write or read lock?
			entry.dbEngine.writeLock( this, entry.oid );
		    }
		}
	    }
	    _objects.clear();
	    _oids.clear();
	    _status = Status.STATUS_COMMITTED;

	} catch ( Exception except ) {
	    // Any error that happens, we're going to rollback the transaction.
	    _status = Status.STATUS_MARKED_ROLLBACK;
	    rollback();
	    throw new TransactionAbortedReasonException( except );
	}
	_status = Status.STATUS_ACTIVE;
    }


    /**
     * Commits all changes and closes the transaction releasing all
     * locks on all objects. All objects are now transient. Must be
     * called after a call to {@link prepare} has returned successfully.
     *
     * @throws TransactionAbortedException The transaction has been
     *   aborted due to inconsistency, duplicate object key, error
     *   with the persistence engine or any other reason
     * @throws IllegalStateException This method has been called
     *   without calling {@link #prepare} first
     */
    public synchronized void commit()
	throws TransactionAbortedReasonException, IllegalStateException
    {
	Enumeration enum;
	ObjectEntry entry;
	Connection  conn;

	// Never commit transaction that has been marked for rollback
	if ( _status == Status.STATUS_MARKED_ROLLBACK ) {
	    rollback();
	    throw new TransactionAbortedException( "Transaction was marked for rollback and has been rolled back" );
	}
	if ( _status != Status.STATUS_PREPARED )
	    throw new IllegalStateException( "Transaction has not been prepared prior to calling commit" );

	try {
	    _status = Status.STATUS_COMMITTING;

	    if ( _xid == null ) {
		// Go through all the connections opened in this transaction,
		// commit and close them one by one.
		enum = _conns.elements();
		while ( enum.hasMoreElements() ) {
		    conn = (Connection) enum.nextElement();
		    conn.commit();
		    conn.close();
		}
	    }
	    _conns.clear();

	    // Assuming all went well in the RDBMS department, no deadlocks,
	    // etc. clean all the transaction locks with regards to the
	    // database engine.
	    enum = _objects.elements();
	    while ( enum.hasMoreElements() ) {
		entry = (ObjectEntry) enum.nextElement();
		if ( ! entry.readOnly ) {
		    if ( entry.deleted ) {
			// Object has been deleted inside transaction,
			// engine must forget about it.
			entry.dbEngine.forgetObject( this, entry.oid );
		    } else {
			// Object has been created/accessed inside the
			// transaction, release its lock.
			entry.dbEngine.update( this, entry.oid, entry.obj );
			entry.dbEngine.releaseLock( this, entry.oid );
		    }
		}
	    }
	    _objects.clear();
	    _oids.clear();
	    _status = Status.STATUS_COMMITTED;

	} catch ( Exception except ) {
	    // Any error that happens, we're going to rollback the transaction.
	    _status = Status.STATUS_MARKED_ROLLBACK;
	    rollback();
	    throw new TransactionAbortedReasonException( except );
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
    {
	Enumeration enum;
	ObjectEntry entry;
	Connection  conn;

	if ( _status != Status.STATUS_ACTIVE && _status != Status.STATUS_PREPARED &&
	     _status != Status.STATUS_MARKED_ROLLBACK )
	    throw new TransactionNotInProgressException( "Transaction has been closed" );

	if ( _xid == null ) {
	    // Go through all the connections opened in this transaction,
	    // rollback and close them one by one. Ignore errors.
	    enum = _conns.elements();
	    while ( enum.hasMoreElements() ) {
		conn = (Connection) enum.nextElement();
		try {
		    conn.rollback();
		    conn.close();
		} catch ( SQLException except ) { }
	    }
	}
	_conns.clear();

	// Clean the transaction locks with regards to the
	// database engine
	enum = _objects.elements();
	while ( enum.hasMoreElements() ) {
	    entry = (ObjectEntry) enum.nextElement();
	    try {
		if ( ! entry.readOnly ) {
		    if ( entry.created ) {
			// Object has been created in this transaction,
			// it no longer exists, forgt about it in the engine.
			entry.dbEngine.forgetObject( this, entry.oid );
		    } else {
			// Object has been queried (possibly) deleted in this
			// transaction, release the lock and revert to the old value.
			entry.dbEngine.copyInto( this, entry.oid, entry.obj );
			entry.dbEngine.releaseLock( this, entry.oid );
		    }
		}
	    } catch ( Exception except ) { }
	}
	_objects.clear();
	_oids.clear();
	_status = Status.STATUS_ROLLEDBACK;
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
     * Obtains a connection for the underlying engine. The connection
     * is unique to this transaction. If the engine has requested a
     * connection once during this transaction, the same connection
     * will be returned. Otherwise, a new connection will be created
     * and returned.
     *
     * @param dbEngine The database engine requesting the connection
     * @return An open JDBC connection in a transaction context
     * @throws ODMGRuntimeException An error has occured attempting
     *   to obtain a new connection
     */
    Connection getConnection( DatabaseEngine dbEngine )
	throws ODMGSQLException
    {
	Connection conn;

	conn = (Connection) _conns.get( dbEngine );
	if ( conn == null ) {
	    try {
		// Get a new connection from the engine. Since the
		// engine has no transaction association, we must do
		// this sort of round trip. An attempt to have the
		// transaction association in the engine inflates the
		// code size in other places.
		conn = dbEngine.createConnection();
		if ( _xid == null )
		    conn.setAutoCommit( false );
		_conns.put( dbEngine, conn );
	    } catch ( SQLException except ) {
		throw new ODMGSQLException( except );
	    }
	}
	return conn;
    }


    /**
     * Adds a new entry recording the use of the object in this
     * transaction. This is a copy of the object that is only visible
     * (or deleted) in the context of this transaction. The object is
     * not persisted if it has not been recorded in this transaction.
     *
     * @param obj The object to record
     * @param oid The object's OID
     * @param dbEngine The database engine used to create this object
     */
    private ObjectEntry addObjectEntry( Object obj, OID oid, DatabaseEngine dbEngine )
    {
	ObjectEntry entry;
 
	entry = new ObjectEntry();
	entry.dbEngine = dbEngine;
	entry.oid = oid;
	entry.obj = obj;
	_objects.put( obj, entry );
	_oids.put( oid, entry );
	return entry;
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
    private ObjectEntry getObjectEntry( Object obj )
    {
	return (ObjectEntry) _objects.get( obj );
    }


    /**
     * Returns the entry for the object from the OID. If the entry
     * does not exist, the object is not persistent in this
     * transaction. An entry will be returned even if the object has
     * been deleted in this transaction.
     *
     * @param oid The object's OID
     * @return The object's entry or null if not persistent
     */
    private ObjectEntry getObjectEntry( OID oid )
    {
	return (ObjectEntry) _oids.get( oid );
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
	if ( entry != null ) {
	    _oids.remove( entry.oid );
	    return entry;
	}
	return null;
    }


    /**
     * A transaction records all objects accessed during the lifetime
     * of the transaction in this record (queries and created). A
     * single entry exist for each object accessible using the object
     * or it's OID as keys. The entry records the database engine used
     * to persist the object, the object's OID, the object itself, and
     * whether the object has been deleted in this transaction,
     * created in this transaction, or already prepared. Objects
     * identified as read only are not update when the transaction
     * commits.
     */
    private static class ObjectEntry
    {

	DatabaseEngine dbEngine;

	OID            oid;

	Object         obj;

	boolean        deleted;

	boolean        created;

	boolean        readOnly;

    }


}
