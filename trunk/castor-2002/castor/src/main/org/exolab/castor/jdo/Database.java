
package org.exolab.castor.jdo;


/**
 *
 */
public interface Database
{


    /**
     * Returns a new OQL query.
     */
    public OQLQuery getOQLQuery();


    /**
     * Returns a new OQL query using the specified statement.
     */
    public OQLQuery getOQLQuery( String oql )
        throws QueryException;


    /**
     * Closes the database.
     *
     * @throws ODMGException Unable to close the database
     */
    public void close()
        throws PersistenceException;


    /**
     * Creates a new object in persistent storage and returns the.
     * The object will be persisted only if the transaction commits.
     * If the object has an identity then duplicate identity check
     * happens in this method, if the identity is null,  duplicate
     * identity check occurs when the transaction completes and the
     * object is not visible in this transaction.
     *
     * @param object The object to create
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws DuplicateIdentityException An object with this identity
     *  already exists in persistent storage
     * @throws ClassNotPersistenceCapableException The class is not
     *  persistent capable
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public void create( Object object )
        throws ClassNotPersistenceCapableException, DuplicateIdentityException,
               TransactionNotInProgressException, PersistenceException;


    /**
     * Removes the object from persistent storage. The deletion will
     * take effect only if the transaction is committed, but the
     * object is no longer viewable for the current transaction and
     * locks for access from other transactions will block until this
     * transaction completes.
     *
     * @param object The object to remove
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ObjectNotPersistentException The object has not been
     *  queried or created in this transaction
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public void remove( Object object )
        throws ObjectNotPersistentException, LockNotGrantedException, 
               TransactionNotInProgressException, PersistenceException;


    /**
     * Start a transaction.
     * Calling <code>begin</code> multiple times on the same transaction object,
     * without an intervening call to <code>commit</code> or <code>abort</code>,
     * causes the exception <code>TransactionInProgressException</code> to be thrown
     * on the second and subsequent calls. Operations executed before a transaction
     * has been opened, or before reopening after a transaction is aborted or committed,
     * have undefined results;
     * these may throw a <code>TransactionNotInProgressException</code> exception.
     */
    public void begin();


    /**
     * Commit and close the transaction.
     * Calling <code>commit</code> commits to the database all persistent object
     * modifications within the transaction and releases any locks held by the transaction.
     * A persistent object modification is an update of any field of an existing
     * persistent object, or an update or creation of a new named object in the database.
     * If a persistent object modification results in a reference from an existing
     * persistent object to a transient object, the transient object is moved to the
     * database, and all references to it updated accordingly. Note that the act of
     * moving a transient object to the database may create still more persistent
     * references to transient objects, so its referents must be examined and moved as well.
     * This process continues until the database contains no references to transient objects,
     * a condition that is guaranteed as part of transaction commit.
     * Committing a transaction does not remove from memory transient objects created
     * during the transaction
     */
    public void commit()
        throws TransactionNotInProgressException, TransactionAbortedException, PersistenceException;


    /**
     * Abort and close the transaction.
     * Calling abort abandons all persistent object modifications and releases the
     * associated locks.
     * Aborting a transaction does not restore the state of modified transient objects
     */
    public void rollback()
        throws TransactionNotInProgressException, PersistenceException;


    /**
     * Commit the transaction, but reopen the transaction, retaining all locks.
     * Calling <code>checkpoint</code> commits persistent object modifications made
     * within the transaction since the last checkpoint to the database.
     * The transaction retains all locks it held on those objects at the time the
     * checkpoint was invoked.
     */
    public void checkpoint()
        throws TransactionNotInProgressException, TransactionAbortedException, PersistenceException;


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
     * If the timeout has elapsed or a deadlock has been detected,
     * an exception will be thrown but the current lock will be retained.
     *
     * @param object The object to lock
     * @throws TransactionNotInProgressException Method called while
     *   transaction is not in progress
     * @throws ObjectNotPersistentException The object has not been
     *  queried or created in this transaction
     * @throws LockNotGrantedException Timeout or deadlock occured
     *  attempting to acquire lock on object
     * @throws PersistenceException An error reported by the
     *  persistence engine
     */
    public void lock( Object object )
        throws LockNotGrantedException, ObjectNotPersistentException,
               TransactionNotInProgressException,  PersistenceException;



    /**
     * Returns true if the object is persistent. An object is persistent
     * if it was created or queried in this transaction. If the object
     * was created or queried in another transaction, or there is no
     * open transaction, this method returns null.
     *
     * @param object The object
     * @return True if persistent in this transaction
     */
    public boolean isPersistent( Object object );


    /**
     * Makes a transient object durable in the database. Must be
     * executed in the context of an open transaction.
     * <p>
     * If the transaction in which this method is executed commits,
     * then the object is made durable. If the transaction aborts,
     * then this operation is considered not to have been executed,
     * and the target object is again transient.
     *
     * @param object The object to make persistent
     * @throws ClassNotPersistenceCapableException The object cannot
     *  be persisted due to the type of the object
     * @deprecated See {@link #create}
     */
    public void makePersistent( Object object )
        throws ClassNotPersistenceCapableException,
               DuplicateIdentityException, PersistenceException;


    /**
     * Deletes an object from the database. Must be executed in the
     * context of an open transaction.
     * <p>
     * If the object is not persistent, then {@link ObjectNotPersistent}
     * is thrown. If the transaction in which this method is executed
     * commits, then the object is removed from the database. If the
     * transaction aborts, then this operation is considered not to
     * have been executed, and the target object is again in the
     * database.
     *
     * @param object The object to delete
     * @throws ObjectNotPersistent The object does not exist in the
     *  database
     * @deprecated See {@link #remove}
     */
    public void deletePersistent( Object object )
        throws ObjectNotPersistentException, LockNotGrantedException, 
               PersistenceException;


}

