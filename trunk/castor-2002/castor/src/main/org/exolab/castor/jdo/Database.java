
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


}

