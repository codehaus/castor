
package org.exolab.castor.jdo;


/**
 * Interface for opening and interacting with the database. Databases
 * must be opened before starting any transactions that use the
 * database and closed after ending these transactions.
 *
 * @author David Jordan (OMG)
 * @version ODMG 3.0
 * @version $Revision$ $Date$
 */
public interface Database
{


    /**
     * Returns a new OQL query.
     */
    public OQLQuery getOQLQuery();


    /**
     * Returns a new OQL query.
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
     * Associate a name with an object and make it persistent.
     * An object instance may be bound to more than one name.
     * Binding a previously transient object to a name makes that object persistent.
     *
     * @param object The object to be named.
     * @param name The name to be given to the object.
     * @throws ObjectNameNotUniqueException
     * If an attempt is made to bind a name to an object and that name is already bound
     * to an object.
     */
    public void bind( Object object, String name )
        throws DuplicateIdentityException, ClassNotPersistenceCapableException,
               PersistenceException;


    /**
     * Lookup an object via its name.
     *
     * @param name The name of an object.
     * @return The object with that name.
     * @throws ObjectNameNotFoundException There is no object with the specified name.
     * @see ObjectNameNotFoundException
     */
    public Object lookup( String name )
        throws ObjectNotFoundException, PersistenceException;


    /**
     * Disassociate a name with an object
     *
     * @param name The name of an object.
     * @throws ObjectNameNotFoundException No object exists in the database with that name.
     */
    public void unbind( String name )
        throws ObjectNotFoundException, PersistenceException;


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
     */
    public void deletePersistent( Object object )
        throws ObjectNotPersistentException, LockNotGrantedException, 
               PersistenceException;



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
     * Upgrade the lock on the given object to the given lock mode.
     * The call has no effect if the object's current lock is already at or above
     * that level of lock mode.
     *
     * @param obj The object to acquire a lock on.
     * @param lockMode The lock mode to acquire. The lock modes are <code>READ</code>,
     * <code>UPGRADE</code>, and <code>WRITE</code>.
     * @throws LockNotGrantedException Is thrown if the given lock mode could not be acquired.
     */
    public void lock( Object obj )
        throws LockNotGrantedException, PersistenceException;



}

