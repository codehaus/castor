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


import javax.transaction.Status;
import org.exolab.castor.mapping.ObjectDesc;


/**
 * The result of a query in the context of a transaction. A query is
 * executed against the cache engine in the context of a transaction.
 * The result of a query is this object that can be used to obtain
 * the next object in the query.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public final class QueryResults
{

    
    /**
     * The transaction context in which this query was executed.
     */
    private TransactionContext _tx;
    
    
    /**
     * The cache engine against which this query was executed.
     */
    private CacheEngine         _cache;
    
    
    /**
     * The executed query.
     */
    private PersistenceQuery     _query;
    
    
    /**
     * The mode in which this query is running (read-only, read/write,
     * exclusive).
     */
    private int                 _accessMode;
    
    
    /**
     * The last identity retrieved with a call to {@link #nextIdentity}.
     */
    private Object              _lastIdentity;
    

    QueryResults( TransactionContext tx, CacheEngine cache, PersistenceQuery query, int accessMode )
    {
	_tx = tx;
	_cache = cache;
	_query = query;
	_accessMode = accessMode;
    }
    
    
    public Object nextIdentity()
	throws TransactionNotInProgressException, PersistenceException
    {
	// Make sure transaction is still open.
	if ( _tx.getStatus() != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();
	
	// Get the next OID from the query engine. The object is
	// already loaded into the cache engine at this point and
	// has a lock based on the original query (i.e. read write
	// or exclusive). If no next record return null.
	return _query.nextIdentity();
    }


    public Class getResultType()
    {
	return _query.getResultType();
    }
    
    
    public boolean fetch( Object obj, Object identity )
	throws TransactionNotInProgressException, PersistenceException,
	       ObjectNotFoundException, LockNotGrantedException
    {
	OID                            oid;
	TransactionContext.ObjectEntry entry;
	ObjectDesc                     objDesc;
	
	// Make sure transaction is still open.
	if ( _tx.getStatus() != Status.STATUS_ACTIVE )
	    throw new TransactionNotInProgressException();
	if ( identity == null )
	    throw new IllegalArgumentException( "Argument 'identity' is null" );
	
	synchronized ( _tx ) {
	    // Handle the case where object has already been loaded in
	    // the context of this transaction. The case where object
	    // has been loaded in another transaction is handled by the
	    // locking mechanism.
	    entry = _tx.getObjectEntry( obj );
	    if ( entry != null ) {
		// If the object has been loaded in this transaction from a
		// different engine this is an error. If the object has been
		// deleted in this transaction, it cannot be re-loaded. If the
		// object has been created in this transaction, it cannot be
		// re-loaded but no error is reported.
		if ( entry.cache != _cache )
		    throw new PersistenceException( "persist.multipleLoad", obj.getClass(), identity );
		if ( entry.deleted )
		    throw new ObjectNotFoundException( obj.getClass(), identity );
		if ( obj.getClass() != entry.obj.getClass() )
		    throw new PersistenceException( "persist.typeMismatch", obj.getClass(), entry.obj.getClass() );
		if ( entry.created )
		    return false;
		if ( _accessMode == TransactionContext.AccessMode.Exclusive &&
		     ! entry.oid.isExclusive() ) {
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

	    // Get the next OID from the query engine. The object is
	    // already loaded into the cache engine at this point and
	    // has a lock based on the original query (i.e. read write
	    // or exclusive). If no next record return null.
	    objDesc = _cache.getObjectDesc( _query.getResultType() );
	    oid = new OID( objDesc, identity );
	    
	    // Did we already load (or created) this object in this
	    // transaction.
	    entry = _tx.getObjectEntry( _cache, oid );
	    if ( entry != null ) {
		// The object has already been loaded in this transaction
		// and is available from the cache engine.
		if ( entry.deleted )
		    // Object has been deleted in this transaction, so skip
		    // to next object.
		    throw new ObjectNotFoundException( obj.getClass(), identity );
		else {
		    if ( _accessMode == TransactionContext.AccessMode.Exclusive &&
			 ! oid.isExclusive() ) {
			// If we are in exclusive mode and object has not been
			// loaded in exclusive mode before, then we have a
			// problem. We cannot return an object that is not
			// synchronized with the database, but we cannot
			// synchronize a live object.
			throw new PersistenceException( "persist.lockConflict",
							_query.getResultType(), identity );
		    } else {
			// Either read only or exclusive mode, and we
			// already have an object in that mode, so we
			// return that object.
			_cache.copyObject( _tx, oid, obj );
			return true;
		    }
		}
	    } else {
		// First time we see the object in this transaction,
		// must create a new record for this object. We only
		// record the object in the transaction if in read-write
		// or exclusive mode.
		_cache.fetch( _tx, _query, identity,
			      ( _accessMode == TransactionContext.AccessMode.Exclusive ),
			      _tx.getLockTimeout() );
		_cache.copyObject( _tx, oid, obj );
		if ( _accessMode == TransactionContext.AccessMode.ReadOnly )
		    _cache.releaseLock( _tx, oid );
		else
		    _tx.addObjectEntry( obj, oid, _cache );
		return true;
	    }
	}
    }
    
    
}

