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
import javax.transaction.Status;
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
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.ClassHandler;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.PersistenceExceptionImpl;
import org.exolab.castor.persist.ClassNotPersistenceCapableExceptionImpl;
import org.exolab.castor.util.Messages;


/**
 * An implementation of the JDO database supporting explicit transaction
 * demaracation.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DatabaseImpl
    implements Database
{


    /**
     * The database engine used to access the underlying SQL database.
     */
    protected PersistenceEngine   _dbEngine;


    /**
     * The transaction context is this database was accessed with an
     * {@link XAResource}.
     */
    protected TransactionContext  _ctx;


    /**
     * The lock timeout for this database. Zero for immediate
     * timeout, an infinite value for no timeout. The timeout is
     * specified in seconds.
     */
    private int                _lockTimeout;


    /**
     * The log writer is a character output stream to which all
     * logging and tracing messages will be printed.
     */
    private PrintWriter        _logWriter;


    /**
     * The name of this database.
     */
    private String             _dbName;


    public DatabaseImpl( String dbName, int lockTimeout, PrintWriter logWriter )
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
        _lockTimeout = lockTimeout;
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
                try {
                    _ctx.rollback();
                } catch ( Exception except ) {
                }
                throw new PersistenceExceptionImpl( "jdo.dbClosedTxRolledback" );
            }
        } finally {
            _ctx = null;
            _dbEngine = null;
        }
    }


    public boolean isClosed()
    {
        return ( _dbEngine == null );
    }


    public Object load( Class type, Object identity )
        throws TransactionNotInProgressException, ObjectNotFoundException,
               LockNotGrantedException, PersistenceException
    {
        TransactionContext tx;
        ClassHandler       handler;

        tx = getTransaction();
        handler = _dbEngine.getClassHandler( type );
        if ( handler == null )
            throw new ClassNotPersistenceCapableExceptionImpl( type );
        return tx.fetch( _dbEngine, handler, identity, null );
    }


    public Object load( Class type, Object identity, short accessMode )
        throws TransactionNotInProgressException, ObjectNotFoundException,
               LockNotGrantedException, PersistenceException
    {
        TransactionContext tx;
        ClassHandler       handler;
        AccessMode         mode;

        switch ( accessMode ) {
        case ReadOnly:
            mode = AccessMode.ReadOnly;
            break;
        case Shared:
            mode = AccessMode.Shared;
            break;
        case Exclusive:
            mode = AccessMode.Exclusive;
            break;
        case Locked:
            mode = AccessMode.Locked;
            break;
        default:
            throw new IllegalArgumentException( "Value for 'accessMode' is invalid" );
        }
        tx = getTransaction();
        handler = _dbEngine.getClassHandler( type );
        if ( handler == null )
            throw new ClassNotPersistenceCapableExceptionImpl( type );
        return tx.fetch( _dbEngine, handler, identity, mode );
    }


    public void create( Object object )
        throws ClassNotPersistenceCapableException, DuplicateIdentityException,
               TransactionNotInProgressException, PersistenceException
    {
        TransactionContext tx;
        ClassHandler       handler;

        tx = getTransaction();
        handler = _dbEngine.getClassHandler( object.getClass() );
        if ( handler == null )
            throw new ClassNotPersistenceCapableExceptionImpl( object.getClass() );
        tx.create( _dbEngine, object, handler.getIdentity( object ) );
    }


    /**
     * @deprecated
     */
    public synchronized void makePersistent( Object object )
        throws ClassNotPersistenceCapableException, DuplicateIdentityException,
               TransactionNotInProgressException, PersistenceException
    {
        create( object );
    }


    public void remove( Object object )
        throws ObjectNotPersistentException, LockNotGrantedException, 
               TransactionNotInProgressException, PersistenceException
    {
        TransactionContext tx;
        
        tx = getTransaction();
        tx.delete( object );
    }


    /**
     * @deprecated
     */
    public synchronized void deletePersistent( Object object )
        throws ObjectNotPersistentException, LockNotGrantedException, 
               PersistenceException
    {
        remove( object );
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


    public void lock( Object object )
        throws LockNotGrantedException, ObjectNotPersistentException,
               TransactionNotInProgressException,  PersistenceException
    {
        if ( _ctx == null || ! _ctx.isOpen() )
            throw new TransactionNotInProgressException( Messages.message( "jdo.txNotInProgress" ) );
        _ctx.writeLock( object, _lockTimeout );
    }


    public synchronized Object lookup( Class type, Object primKey )
        throws TransactionNotInProgressException, LockNotGrantedException,
               PersistenceException
    {
        TransactionContext tx;
        ClassHandler       handler;
        
        tx = getTransaction();
        handler = _dbEngine.getClassHandler( type );
        if ( handler == null )
            throw new ClassNotPersistenceCapableExceptionImpl( type );
        try {
            return tx.fetch( _dbEngine, handler, primKey, AccessMode.ReadOnly );
        } catch ( ObjectNotFoundException except ) {
            return null;
        }
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
        ClassHandler       handler;
        
        tx = getTransaction();
        try {
            handler = _dbEngine.getClassHandler( NameBinding.class );
            tx.fetch( _dbEngine, handler, name, AccessMode.Exclusive );
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
        ClassHandler       handler;
        
        tx = getTransaction();
        try {
            handler = _dbEngine.getClassHandler( NameBinding.class );
            binding = (NameBinding) tx.fetch( _dbEngine, handler, name, AccessMode.ReadOnly );
            handler = _dbEngine.getClassHandler( binding.getType() );
            if ( handler == null )
                throw new ClassNotPersistenceCapableExceptionImpl( handler.getJavaClass() );
            return tx.fetch( _dbEngine, handler, binding.objectId, AccessMode.ReadOnly );
        } catch ( LockNotGrantedException except ) {
            throw new PersistenceExceptionImpl( except );
        }
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
        throws PersistenceException
    {
        // If inside XA transation throw IllegalStateException
        if ( _ctx != null && _ctx.isOpen() )
            throw new PersistenceException( Messages.message( "jdo.txInProgress" ) );
        _ctx = new TransactionContextImpl( null );
        _ctx.setLockTimeout( _lockTimeout );
    }


    public void commit()
        throws TransactionNotInProgressException, TransactionAbortedException
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
        throws TransactionNotInProgressException
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


    /**
     * @deprecated Use {@link #commit} and {@link #rollback} instead
     */
    public void checkpoint()
        throws TransactionNotInProgressException, TransactionAbortedException
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


    public String toString()
    {
        return _dbName;
    }


}





