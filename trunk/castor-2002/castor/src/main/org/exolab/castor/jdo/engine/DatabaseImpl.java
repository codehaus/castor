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
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.DatabaseNotFoundException;
import org.exolab.castor.jdo.ObjectNotPersistentException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.QueryException;
import javax.transaction.Status;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.ClassHandler;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.PersistenceExceptionImpl;
import org.exolab.castor.persist.ClassNotPersistenceCapableExceptionImpl;
import org.exolab.castor.util.Messages;


/**
 * An implementation of the ODMG {@link Database} interface. Provides
 * access to a single persistent storage. Type of access depends on
 * the mode in which this database is opened. The SQL database being
 * accessed as well as the mapping supported are defined through
 * {@link DatabaseRegistry}. Operations are not allowed unless the
 * current thread is associated with a transaction.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 * @see DatabaseRegistry
 */
public final class DatabaseImpl
    implements Database, XAResource
{


    /**
     * The database engine used to access the underlying SQL database.
     */
    private PersistenceEngine   _dbEngine;


    /**
     * The transaction context is this database was accessed with an
     * {@link XAResource}.
     */
    private TransactionContext _ctx;


    private int                DefaultWaitLockTimeout = 10;


    // XXX Need accessors to set this and way to pass it to transaction
    private int                _lockTimeout = DefaultWaitLockTimeout;


    private PrintWriter        _logWriter;


    private String             _dbName;


    public DatabaseImpl( String dbName, PrintWriter logWriter )
        throws DatabaseNotFoundException
    {
        // Locate a suitable datasource and database engine
        // and report if not mapping registered any of the two.
        // A new ODMG engine is created each time with different
        // locking mode.
        DatabaseRegistry dbs;
        
        dbs = DatabaseRegistry.getDatabaseRegistry( dbName );
        if ( dbs == null )
            throw new DatabaseNotFoundException( Messages.format( "jdo.dbNoMapping", dbName ) );
        _dbEngine = DatabaseRegistry.getPersistenceEngine( dbs );
        if ( logWriter != null ) {
            _logWriter = logWriter;
            _dbEngine.setLogWriter( _logWriter );
        }
        _dbName = dbName;
    }


    PersistenceEngine getPersistenceEngine()
    {
        return _dbEngine;
    }


    public synchronized void close()
        throws PersistenceException
    {
        try {
            if ( _ctx != null && _ctx.isOpen() ) {
                _ctx.rollback();
                throw new PersistenceExceptionImpl( "jdo.dbClosedTxRolledback" );
            }
        } finally {
            _ctx = null;
            _dbEngine = null;
        }
    }


    public synchronized void makePersistent( Object obj )
        throws ClassNotPersistenceCapableException,
               DuplicateIdentityException, PersistenceException
    {
        TransactionContext tx;
        ClassHandler       handler;

        tx = getTransaction();
        handler = _dbEngine.getClassHandler( obj.getClass() );
        if ( handler == null )
            throw new ClassNotPersistenceCapableExceptionImpl( obj.getClass() );
        tx.create( _dbEngine, obj, handler.getIdentity( obj ) );
    }


    public synchronized void deletePersistent( Object obj )
        throws ObjectNotPersistentException, LockNotGrantedException, 
               PersistenceException
    {
        TransactionContext tx;
        
        tx = getTransaction();
        tx.delete( obj );
    }


    public synchronized Object lookup( Class type, Object primKey )
        throws TransactionNotInProgressException, LockNotGrantedException,
               PersistenceException
    {
        TransactionContext tx;
        ClassHandler       handler;
        Object             obj;
        
        tx = getTransaction();
        handler = _dbEngine.getClassHandler( type );
        if ( handler == null )
            throw new ClassNotPersistenceCapableExceptionImpl( type );
        obj = handler.newInstance();
        try {
            tx.load( _dbEngine, obj, primKey, AccessMode.ReadOnly );
        } catch ( ObjectNotFoundException except ) {
            return null;
        }
        return obj;
    }
    

    public synchronized void bind( Object obj, String name )
        throws DuplicateIdentityException, ClassNotPersistenceCapableException,
               PersistenceException
    {
        TransactionContext tx;
        NameBinding        binding;
        ClassHandler       handler;
        
        tx = getTransaction();
        makePersistent( obj );
        handler = _dbEngine.getClassHandler( obj.getClass() );
        binding = new NameBinding( name, obj, handler );
        tx.create( _dbEngine, binding, name );
    }


    public synchronized void unbind( String name )
        throws ObjectNotFoundException, PersistenceException
    {
        TransactionContext tx;
        NameBinding binding;
        
        tx = getTransaction();
        try {
            binding = new NameBinding();
            tx.load( _dbEngine, binding, name, AccessMode.Exclusive );
            tx.delete( binding );
        } catch ( ObjectNotPersistentException except ) {
            throw new PersistenceExceptionImpl( except );
        } catch ( LockNotGrantedException except ) {
            throw new PersistenceExceptionImpl( except );
        }
    }


    public synchronized Object lookup( String name )
        throws ObjectNotFoundException, PersistenceException
    {
        TransactionContext tx;
        NameBinding        binding;
        Object             obj;
        ClassHandler       handler;
        
        tx = getTransaction();
        try {
            binding = new NameBinding();
            tx.load( _dbEngine, binding, name, AccessMode.ReadOnly );
            handler = _dbEngine.getClassHandler( binding.getType() );
            if ( handler == null )
                throw new ClassNotPersistenceCapableExceptionImpl( handler.getJavaClass() );
            obj = handler.newInstance();
            tx.load( _dbEngine, obj, binding.objectId, AccessMode.ReadOnly );
            return obj;
        } catch ( LockNotGrantedException except ) {
            throw new PersistenceExceptionImpl( except );
        }
    }


    public boolean isPersistent( Object object )
    {
        TransactionContext tx;
        
        if ( _dbEngine == null )
            throw new IllegalStateException( Messages.message( "jdo.dbClosed" ) );
        if ( _ctx != null && _ctx.isOpen()  )
            return _ctx.isPersistent( object );
        return false;
    }


    public OQLQuery getOQLQuery()
    {
        return new OQLQueryImpl( this );
    }


    public OQLQuery getOQLQuery( String oql )
        throws QueryException
    {
        OQLQuery oqlImpl;

        oqlImpl = new OQLQueryImpl( this );
        oqlImpl.create( oql );
        return oqlImpl;
    }
    
    
    protected void finalize()
        throws Throwable
    {
        if ( _dbEngine != null )
            close();
    }


    protected TransactionContext getTransaction()
        throws TransactionNotInProgressException
    {
        TransactionContext tx;
        
        if ( _dbEngine == null )
            throw new IllegalStateException( Messages.message( "jdo.dbClosed" ) );
        if ( _ctx != null && _ctx.isOpen()  )
            return _ctx;
        throw new TransactionNotInProgressException( Messages.message( "jdo.dbTxNotInProgress" ) );
    }


    public void begin()
    {
        // If inside XA transation throw IllegalStateException
        if ( _ctx != null && _ctx.isOpen() )
            throw new IllegalStateException( Messages.message( "jdo.txInProgress" ) );
        _ctx = new TransactionContextImpl( null );
        _ctx.setLockTimeout( _lockTimeout );
    }


    public void commit()
        throws TransactionNotInProgressException, TransactionAbortedException, PersistenceException
    {
        // If inside XA transation throw IllegalStateException
        if ( _ctx == null || ! _ctx.isOpen() )
            throw new TransactionNotInProgressException( Messages.message( "jdo.txNotInProgress" ) );
        if ( _ctx.getStatus() == Status.STATUS_MARKED_ROLLBACK )
            throw new TransactionAbortedException( Messages.message( "jdo.txAborted" ) );
        try {
            _ctx.prepare();
            _ctx.commit();
        } catch ( TransactionAbortedException except ) {
            try {
                _ctx.rollback();
            } catch ( TransactionNotInProgressException except2 ) {
                // This should never happen
            }
            throw except;
        } finally {
            _ctx = null;
        }
    }


    public void rollback()
        throws TransactionNotInProgressException, PersistenceException
    {
        // If inside XA transation throw IllegalStateException
        if ( _ctx == null || ! _ctx.isOpen() )
            throw new TransactionNotInProgressException( Messages.message( "jdo.txNotInProgress" ) );
        try {
            _ctx.rollback();
        } finally {
            _ctx = null;
        }
    }


    public void checkpoint()
        throws TransactionNotInProgressException, TransactionAbortedException, PersistenceException
    {
        // If inside XA transation throw IllegalStateException
        if ( _ctx == null || ! _ctx.isOpen() )
            throw new TransactionNotInProgressException( Messages.message( "jdo.txNotInProgress" ) );
        if ( _ctx.getStatus() == Status.STATUS_MARKED_ROLLBACK )
            throw new TransactionAbortedException( Messages.message( "jdo.txAborted" ) );
        try {
            _ctx.checkpoint();
        } catch ( TransactionAbortedException except ) {
            try {
                _ctx.rollback();
            } catch ( TransactionNotInProgressException except2 ) {
                // This should never happen
            }
            _ctx = null;
            throw except;
        }

    }


    public void lock( Object obj )
        throws LockNotGrantedException, PersistenceException
    {
        if ( _ctx == null || ! _ctx.isOpen() )
            throw new TransactionNotInProgressException( Messages.message( "jdo.txNotInProgress" ) );
        _ctx.writeLock( obj, _lockTimeout );
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
                    _ctx = new TransactionContextImpl( xid );
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
                } catch ( Exception except ) { }
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
                try {
                    ctx.rollback();
                } catch ( Exception except ) { }
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
                } catch ( TransactionNotInProgressException except ) {
                    throw new XAException( XAException.XAER_PROTO );
                } catch ( TransactionAbortedException except ) {
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
                } catch ( TransactionNotInProgressException except ) {
                    throw new XAException( XAException.XAER_PROTO );
                } catch ( TransactionAbortedException except ) {
                    throw new XAException( XAException.XA_HEURRB );
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


    public String toString()
    {
        return _dbName;
    }


}





