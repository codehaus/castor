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


import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import org.odmg.Database;
import org.odmg.DatabaseOpenException;
import org.odmg.DatabaseClosedException;
import org.odmg.DatabaseIsReadOnlyException;
import org.odmg.DatabaseNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.NotImplementedException;
import org.odmg.ObjectNotPersistentException;
import org.odmg.ClassNotPersistenceCapableException;
import org.odmg.TransactionNotInProgressException;
import org.odmg.TransactionInProgressException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import javax.transaction.Status;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.jdo.DuplicatePrimaryKeyException;
import org.exolab.castor.jdo.desc.JDOObjectDesc;
import org.exolab.castor.util.Messages;


/**
 * An implementation of the ODMG {@link Database} interface. Provides
 * access to a single persistent storage. Type of access depends on
 * the mode in which this database is opened. The SQL database being
 * accessed as well as the mapping supported are defined through
 * {@link DatabaseSource}. Operations are not allowed unless the
 * current thread is associated with a transaction.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see DatabaseSource
 */
public final class DatabaseImpl
    implements Database, XAResource
{


    /**
     * The mode in which this database is open.
     */
    private int               _mode = NOT_OPEN;


    /**
     * The database engine used to access the underlying SQL database.
     */
    private DatabaseEngine   _dbEngine;


    /**
     * A list of all transactions opened during the life time of this
     * database. Used to determine if the database can be closed.
     * Elements are of type {@link TransactionContext}.
     */
    private Vector           _txOpen = new Vector();


    /**
     * The transaction context is this database was accessed with an
     * {@link XAResource}.
     */
    private TransactionContext _ctx;


    private PrintWriter      _logWriter;


    private int              _lockWaitTimeout = 10000;


    public DatabaseImpl()
    {
    }


    public DatabaseImpl( DatabaseEngine dbEngine )
    {
	if ( dbEngine == null )
	    throw new IllegalArgumentException( "Argument 'dbEngine' is null" );
	_dbEngine = dbEngine;
	_mode = OPEN_READ_WRITE;
    }


    public void setLogWriter( PrintWriter logWriter )
    {
	_logWriter = logWriter;
    }


    public PrintWriter getLogWriter()
    {
	return _logWriter;
    }


    public synchronized void open( String dbName, int mode )
	throws ODMGException
    {
	// Check that we are opening this database in a valid mode.
	if ( _mode != NOT_OPEN )
	    throw new DatabaseOpenException( Messages.message( "castor.jdo.odmg.dbAlreadyOpen" ) );
	switch ( mode ) {
	case OPEN_READ_ONLY:
	case OPEN_READ_WRITE:
	case OPEN_EXCLUSIVE:
	    break;
	default:
	    throw new ODMGRuntimeException( Messages.message( "castor.jdo.odmg.dbIllegalOpenMode" ) );
	}
	_mode = mode;

	// Locate a suitable datasource and database engine
	// and report if not mapping registered any of the two.
	// A new ODMG engine is created each time with different
	// locking mode.
	DatabaseSource dbs;

	try {
	    dbs = DatabaseSource.getDatabaseSource( dbName );
	} catch ( MappingException except ) {
	    throw new DatabaseNotFoundException( except.getMessage() );
	}
	if ( dbs == null )
	    throw new DatabaseNotFoundException( Messages.format( "castor.jdo.odmg.dbNoMapping", dbName ) );
	if ( ! dbs.canConnect() )
	    throw new DatabaseNotFoundException( Messages.format( "castor.jdo.odmg.dbNoDataSource", dbName ) );
	_dbEngine = DatabaseEngine.getDatabaseEngine( dbs, _logWriter );
    }


    public synchronized void close()
	throws ODMGException
    {
	Enumeration        enum;
	TransactionContext tx;

	// Never close database while inside a transaction.
	enum = _txOpen.elements();
	while ( enum.hasMoreElements() ) {
	    tx = (TransactionContext) enum.nextElement();
	    if ( tx.isOpen() )
		throw new TransactionInProgressException( Messages.message( "castor.jdo.odmg.dbTxInProgress" ) );
	}
	_dbEngine = null;
	_mode = NOT_OPEN;
    }


    public synchronized void makePersistent( Object obj )
    {
	TransactionContext tx;

	if ( _mode == Database.OPEN_READ_ONLY )
	    throw new DatabaseIsReadOnlyException( Messages.message( "castor.jdo.odmg.dbOpenReadOnly" ) );
	tx = getTransaction();
	tx.create( _dbEngine, obj, null );
    }


    public synchronized void deletePersistent( Object obj )
    {
	TransactionContext tx;

	if ( _mode == Database.OPEN_READ_ONLY )
	    throw new DatabaseIsReadOnlyException( Messages.message( "castor.jdo.odmg.dbOpenReadOnly" ) );
	tx = getTransaction();
	tx.delete( obj );

    }


    public synchronized Object query( Class type, Object primKey )
	throws ODMGException
    {
	TransactionContext tx;

	tx = getTransaction();
	return tx.load( _dbEngine, type, primKey, _mode, _lockWaitTimeout );
    }


    public synchronized void bind( Object obj, String name )
	throws ObjectNameNotUniqueException
    {
	TransactionContext tx;
	NameBinding        binding;
	JDOObjectDesc      objDesc;

	if ( _mode == Database.OPEN_READ_ONLY )
	    throw new DatabaseIsReadOnlyException( Messages.message( "castor.jdo.odmg.dbOpenReadOnly" ) );
	tx = getTransaction();
	tx.create( _dbEngine, obj, name );
	objDesc = _dbEngine.getObjectDesc( obj.getClass() );
	if ( objDesc == null )
	    throw new ClassNotPersistenceCapableExceptionImpl( obj.getClass() );
	binding = new NameBinding( name, obj, objDesc );
	try {
	    tx.create( _dbEngine, binding, name );
	} catch ( DuplicatePrimaryKeyException except ) {
	    throw new ObjectNameNotUniqueException( name );
	}
    }


    public synchronized void unbind( String name )
	throws ObjectNameNotFoundException
    {
	TransactionContext tx;
	NameBinding binding;

	if ( _mode == Database.OPEN_READ_ONLY )
	    throw new DatabaseIsReadOnlyException( Messages.message( "castor.jdo.odmg.dbOpenReadOnly" ) );
	tx = getTransaction();
	try {
	    binding = (NameBinding) tx.load( _dbEngine, NameBinding.class, name,
					     OPEN_READ_WRITE, _lockWaitTimeout );
	} catch ( ODMGException except ) {
	    throw new ObjectNameNotFoundException( "Nested exception: " + except.toString() );
	}
	if ( binding == null )
	    throw new ObjectNameNotFoundException( name );
	tx.delete( binding );
    }


    public synchronized Object lookup( String name )
	throws ObjectNameNotFoundException
    {
	TransactionContext tx;
	NameBinding binding;

	tx = getTransaction();
	try {
	    binding = (NameBinding) tx.load( _dbEngine, NameBinding.class, name,
					     OPEN_READ_WRITE, _lockWaitTimeout );
	    if ( binding == null )
		throw new ObjectNameNotFoundException( name );
	    tx.unlock( binding );
	    return tx.load( _dbEngine, binding.getType(), binding.objectId,
			    _mode, _lockWaitTimeout );
	} catch ( ODMGException except ) {
	    throw new ObjectNameNotFoundException( "Nested exception: " + except.toString() );
	}
    }


    protected void finalize()
	throws Throwable
    {
	if ( _mode != NOT_OPEN )
	    close();
    }


    protected TransactionContext getTransaction()
    {
	TransactionContext tx;

	if ( _dbEngine == null )
	    throw new DatabaseClosedException( Messages.message( "castor.jdo.odmg.dbClosed" ) );
	if ( _ctx != null )
	    return _ctx;

	// Get the current transaction, complain if none found:
	// Cannot persist outside of a transaction.
	tx = TransactionImpl.getCurrentContext();
	if ( tx == null || ! tx.isOpen() )
	    throw new TransactionNotInProgressException( Messages.message( "castor.jdo.odmg.dbTxNotInProgress" ) );
	// Must register transaction with this database.
	if ( ! _txOpen.contains( tx ) )
	    _txOpen.addElement( tx );
	return tx;
    }


    public XAResource getXAResource()
    {
	return this;
    }





    private Hashtable _resManager = new Hashtable();


    public synchronized void start( Xid xid, int flags )
        throws XAException
    {
	// General checks.
	if ( xid == null )
	    throw new XAException( XAException.XAER_INVAL );

	synchronized ( _resManager ) {
	    switch ( flags ) {
	    case TMNOFLAGS:
		_ctx = (TransactionContext) _resManager.get( xid );
		if ( _ctx == null ) {
		    _ctx = new TransactionContext( xid );
		    _resManager.put( xid, _ctx );
		}
		break;
	    case TMJOIN:
	    case TMRESUME:
		_ctx = (TransactionContext) _resManager.get( xid );
		if ( _ctx == null )
		    throw new XAException( XAException.XAER_NOTA );
		if ( ! _ctx.isOpen() )
		    throw new XAException( XAException.XAER_NOTA );
		break;
	    default:
		// No other flags supported in start().
		throw new XAException( XAException.XAER_INVAL );
	    }
	}
    }


    public synchronized void end( Xid xid, int flags )
        throws XAException
    {
	// General checks.
	if ( xid == null )
	    throw new XAException( XAException.XAER_INVAL );

	synchronized ( _resManager ) {
	    if ( _ctx == null )
		throw new XAException( XAException.XAER_INVAL );
	    switch ( flags ) {
	    case TMSUCCESS:
		break;
	    case TMFAIL:
		try {
		    _ctx.rollback();
		} catch ( ODMGRuntimeException except ) {
		}
		break;
	    case TMSUSPEND:
		break;
	    default:
		throw new XAException( XAException.XAER_INVAL );
	    }
	    _ctx = null;
	}
    }


    public synchronized void forget( Xid xid )
	throws XAException
    {
	// General checks.
	if ( xid == null )
	    throw new XAException( XAException.XAER_INVAL );

	synchronized ( _resManager ) {
	    TransactionContext ctx;

	    ctx = (TransactionContext) _resManager.remove( xid );
	    if ( ctx == null )
		throw new XAException( XAException.XAER_NOTA );

	    // Forget is never called on an open transaction, but one
	    // can never tell.
	    if ( ctx.isOpen() ) {
		ctx.rollback();
		throw new XAException( XAException.XAER_PROTO );
	    }
	}
    }


    public synchronized int prepare( Xid xid )
	throws XAException
    {
	// General checks.
	if ( xid == null )
	    throw new XAException( XAException.XAER_INVAL );

	synchronized ( _resManager ) {
	    TransactionContext ctx;

	    ctx = (TransactionContext) _resManager.get( xid );
	    if ( ctx == null )
		throw new XAException( XAException.XAER_NOTA );

	    switch ( ctx.getStatus() ) {
	    case Status.STATUS_PREPARED:
	    case Status.STATUS_ACTIVE:
		// Can only prepare an active transaction. And error
		// is reported as vote to rollback the transaction.
		try {
		    if ( ctx.prepare() ) {
			return XA_OK;
		    } else {
			return XA_RDONLY;
		    }
		} catch ( ODMGRuntimeException except ) {
		    throw new XAException( XAException.XA_RBROLLBACK );
		}
	    case Status.STATUS_MARKED_ROLLBACK:
		// Report transaction marked for rollback.
		throw new XAException( XAException.XA_RBROLLBACK );
	    default:
		throw new XAException( XAException.XAER_PROTO );
	    }
	}
    }


    public Xid[] recover( int flags )
        throws XAException
    {
	return null;
    }


    public synchronized void commit( Xid xid, boolean onePhase )
        throws XAException
    {
	// General checks.
	if ( xid == null )
	    throw new XAException( XAException.XAER_INVAL );

	synchronized ( _resManager ) {
	    TransactionContext ctx;

	    ctx = (TransactionContext) _resManager.get( xid );
	    if ( ctx == null )
		throw new XAException( XAException.XAER_NOTA );
	    switch ( ctx.getStatus() ) {
	    case Status.STATUS_COMMITTED:
		// Allowed to make multiple commit attempts.
		return;
	    case Status.STATUS_ROLLEDBACK:
		// This should not happen unless someone interfered
		// by calling rollback directly or failing a commit,
		// but is still a valid heuristic condition on our behalf.
		throw new XAException( XAException.XA_HEURRB );
	    case Status.STATUS_PREPARED:
		// Commit can only occur after a prepare, so must be
		// in prepared state first. Any ODMG error is reported
		// as a heuristic decision to rollback.
		try {
		    ctx.commit();
		} catch ( ODMGRuntimeException except ) {
		    throw new XAException( XAException.XA_HEURRB );
		} catch ( Exception except ) {
		    throw new XAException( XAException.XAER_RMFAIL );
		}
	    default:
		throw new XAException( XAException.XAER_PROTO );
	    }
	}
    }


    public synchronized void rollback( Xid xid )
        throws XAException
    {
	// General checks.
	if ( xid == null )
	    throw new XAException( XAException.XAER_INVAL );

	synchronized ( _resManager ) {
	    TransactionContext ctx;

	    ctx = (TransactionContext) _resManager.get( xid );
	    if ( ctx == null )
		throw new XAException( XAException.XAER_NOTA );
	    switch ( ctx.getStatus() ) {
	    case Status.STATUS_COMMITTED:
		// This should not happen unless someone interfered
		// by calling commit directly, but is still a valid
		// heuristic condition on our behalf.
		throw new XAException( XAException.XA_HEURCOM );
	    case Status.STATUS_ROLLEDBACK:
		// Allowed to make multiple rollback attempts.
		return;
	    case Status.STATUS_ACTIVE:
	    case Status.STATUS_MARKED_ROLLBACK:
		// Rollback never fails with an application exception.
		try {
		    ctx.rollback();
		} catch ( Exception except ) {
		    throw new XAException( XAException.XAER_RMFAIL );
		}
		return;
	    default:
		throw new XAException( XAException.XAER_PROTO );
	    }
	}
    }


    public synchronized boolean isSameRM( XAResource xaRes )
	throws XAException
    {
	// Two resource managers are equal if they produce equivalent
	// connection (i.e. same database, same user). If the two are
	// equivalent they would share a transaction by joining.
	if ( xaRes == null || ! ( xaRes instanceof DatabaseImpl ) )
	    return false;
	if ( _resManager.equals( ( (DatabaseImpl) xaRes )._resManager ) )
	    return true;
	return false;
    }


    public boolean setTransactionTimeout( int timeout )
    {
	return false;
    }


    public int getTransactionTimeout()
    {
	return 0;
    }


}





