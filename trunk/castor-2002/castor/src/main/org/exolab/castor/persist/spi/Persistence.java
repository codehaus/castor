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


package org.exolab.castor.persist.spi;


import org.exolab.castor.persist.DuplicateIdentityException;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.ObjectDeletedException;
import org.exolab.castor.persist.ObjectModifiedException;
import org.exolab.castor.persist.QueryException;
import org.exolab.castor.persist.PersistenceException;


/**
 * The persistence engine implements this interface in order to allow
 * objects to be created, removed, loaded, stored, locked and checked
 * for dirtyness.
 * <p>
 * The caller takes full responsibility to assure integrity of
 * transactions and object caching and only relies on the engine to
 * assist in assuring that through the mechanisms available to it.
 * <p>
 * All method calls provide an open connection into the persistence
 * storage. The connection is opened by and maintained in the context
 * of a transaction. The type of the connection depends on the
 * persistence engine (JDBC, JNDI, etc).
 * <p>
 * All method calls provide the object itself and the object identity.
 * The engine must always use the identity passed in the method call
 * and never the identity contained in the object itself.
 * <p>
 * The stamp is an arbitrary object that is returned on object
 * creation and loading and passed when performing a dirty check. The
 * stamp can be used to detect object dirtyness in a more efficient
 * manner in database that support a stamping mechanism (e.g. Oracle
 * RAWID, Sybase TIMESTAMP).
 * <p>
 * An implementation of this object must be re-entrant and thread
 * safe. It must depend on the connection passed in each method call
 * and never hold a reference to a connection.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see TransactionContext
 * @see PersistenceQuery
 */
public interface Persistence
{


    /**
     * Creates a new object in persistence storage. Called for an
     * object that was created during the transaction when the identity
     * of that object is known. Creates a new record in persistence
     * storage. Must detect an attempt to create an object with the
     * same identity and must retain a lock on the object after creation.
     *
     * @param conn An open connection
     * @param obj The object to create
     * @param identity The object's identity
     * @return The object's stamp, or null
     * @throws DuplicateIdentityException An object with the same
     *   identity already exists in persistent storage
     * @throws PersistenceException A persistence error occured
     */
    public Object create( Object conn, Object obj, Object identity )
	throws DuplicateIdentityException, PersistenceException;


    /**
     * Loads the object from persistence storage. The object has been
     * created prior to calling this method and its fields might have
     * undetermined values. This method will load the object from
     * persistence storage based on the object's identity and set all
     * fields. This method may return a stamp which can be used at a
     * later point to determine whether the copy of the object in
     * persistence storage is newer than the cached copy (see {@link
     * #needReload}). If <tt>lock</tt> is true the object must be
     * locked in persistence storage to prevent concurrent updates.
     *
     * @param conn An open connection
     * @param obj The object to load into
     * @param identity object's identity
     * @param lock True if object should be locked
     * @return The object's stamp, or null
     * @throws ObjectNotFoundException The object was not found in
     *   persistent storage
     * @throws PersistenceException A persistence error occured
     */
    public Object load( Object conn, Object obj, Object identity, boolean lock )
	throws ObjectNotFoundException, PersistenceException;


    /**
     * Stores the object in persistent storage, given the object and
     * its identity. The object has been loaded before or has been
     * created through a call to {@link #create}. This method is
     * called after a call to {@link #dirtyCheck} has determined that
     * the object has not been modified in persistent storage since
     * it was loaded. After this method returns all locks on the
     * object must be retained until the transaction has completed.
     * This method may return a new stamp to track further updates to
     * the object.
     * <p>
     * If the object was not retrieved for exclusive access, this
     * method will be asked to perform dirty checking prior to storing
     * the object. The <tt>original</tt> argument will contains the
     * object's original value as retrieved in the transaction, and
     * <tt>stamp</tt> the object's stamp returned from a successful
     * call to {@link #load}. These arguments are null for objects
     * retrieved with an exclusive lock.
     *
     * @param conn An open connection
     * @param obj The object to store
     * @param identity The object's identity
     * @param original The original object, or null
     * @param stamp The object's stamp, or null
     * @return The object's stamp, or null
     * @throws ObjectModifiedException The object has been modified
     *  in persistence storage since it was last loaded
     * @throws ObjectDeletedException Indicates the object has been
     *  deleted from persistence storage
     * @throws PersistenceException A persistence error occured
     */
    public Object store( Object conn, Object obj, Object identity,
			 Object original, Object stamp )
	throws ObjectModifiedException, ObjectDeletedException, PersistenceException;


    /**
     * Deletes the object from persistent storage, given the object
     * and its identity. The object has been loaded before or has been
     * created through a call to {@link #create}. This method is called
     * after a call to {@link #dirtyCheck} has determined that the
     * object has not been modified in persistent storage since it was
     * loaded. After this method returns all locks on the object must
     * be retained until the transaction has completed.
     *
     * @param conn An open connection
     * @param obj The object to delete, may be null
     * @param identity The object's identity
     * @throws PersistenceException A persistence error occured
     */
    public void delete( Object conn, Object obj, Object identity )
	throws PersistenceException;


    /**
     * Obtains a write lock on the object. This method is called in
     * order to lock the object and prevent concurrent access from
     * other transactions. The object is known to have been loaded
     * before either in this or another transaction. This method is
     * used to assure that updates or deletion of the object will
     * succeed when the transaction completes, without attempting to
     * reload the object. Unlike {@link #needReload} the caller of
     * this method is not interested in knowing whether the object
     * has been modified since it was last loaded.
     *
     * @param conn An open connection
     * @param obj The object to lock, may be null
     * @param identity The object's identity
     * @throws ObjectDeletedException Indicates the object has been
     *  deleted from persistence storage
     * @throws PersistenceException A persistence error occured
     */
    public void writeLock( Object conn, Object obj, Object identity )
	throws ObjectDeletedException, PersistenceException;


    /**
     * Called to change the identity of an object. If possible, the
     * persistence engine will change the object's identity from the
     * old one to the new one in any manner it sees fit. After this
     * method returns all locks on the object must be retained until
     * the transaction has completed.
     *
     * @param conn An open connection
     * @param obj The object to modify, may be null
     * @param oldIdentity The object's old identity
     * @param newIdentity The object's new identity
     * @throws DuplicateIdentityException An object with the same
     *   identity already exists in persistent storage
     * @throws ObjectDeletedException Indicates the object has been
     *  deleted from persistence storage
     * @throws PersistenceException A persistence error occured
     */
    public void changeIdentity( Object conn, Object obj,
				Object oldIdentity, Object newIdentity )
	throws ObjectDeletedException, DuplicateIdentityException, PersistenceException;


    /**
     * Creates and returns a new query object. The query object is
     * used to execute a query against persistent storage and fetch
     * the results of the query. The query parameter types are
     * specified prehand. If a particular parameter type is unknown,
     * null may be used and type checking will defer to query
     * execution.
     *
     * @param query The query statement
     * @param type List of all parameter types, or null
     * @return A new query object that can be executed
     * @throws QueryException The query is invalid
     */
    public PersistenceQuery createQuery( String query, Class[] types )
	throws QueryException;


}
