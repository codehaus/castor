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
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAResource;
import org.exolab.castor.jdo.XADatabase;
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
import org.exolab.castor.persist.XAResourceSource;
import org.exolab.castor.persist.XAResourceImpl;
import org.exolab.castor.util.Messages;


/**
 * An implementation of the JDO database using XAResource for transaction
 * management, subordinate to the app server.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public final class XADatabaseImpl
    extends DatabaseImpl
    implements XADatabase, Database, XAResourceSource
{


    /**
     * The <tt>XAResource</tt> used to manage transactions for this database.
     */
    private XAResource  _xaRes;


    public XADatabaseImpl( String dbName, int lockTimeout, PrintWriter logWriter )
        throws DatabaseNotFoundException
    {
        super( dbName, lockTimeout, logWriter );
        _xaRes = new XAResourceImpl( _dbEngine, this );
    }


    // XADatabase


    public XAResource getXAResource()
    {
        return _xaRes;
    }


    public Database getDatabase()
    {
        return this;
    }


    // XAResourceSource


    /**
     * Returns the transaction context associated with this source.
     */
    public TransactionContext getTransactionContext()
    {
        return _ctx;
    }


    /**
     * Sets the transaction context associated with this source.
     */
    public void setTransactionContext( TransactionContext tx )
    {
        _ctx = tx;
    }


    /**
     * Indicate that the resource has failed and should be discarded.
     */
    public void xaFailed()
    {
        _ctx = null;
        _dbEngine = null;
    }


    /**
     * Called by {@link XAResourceImpl} to produce a new transaction context
     * implementation suitable for this data source.
     */
    public TransactionContext createTransactionContext( Xid xid )
    {
        return new TransactionContextImpl( xid );
    }


    // Database

    public synchronized void close()
        throws PersistenceException
    {
        _ctx = null;
        _dbEngine = null;
    }



    public void begin()
        throws PersistenceException
    {
        throw new IllegalStateException();
    }


    public void commit()
        throws TransactionNotInProgressException, TransactionAbortedException
    {
        throw new IllegalStateException( Messages.message( "jdo.txInJ2EE" ) );
    }


    public void rollback()
        throws TransactionNotInProgressException
    {
        throw new IllegalStateException( Messages.message( "jdo.txInJ2EE" ) );
    }


    /**
     * @deprecated Use {@link #commit} and {@link #rollback} instead
     */
    public void checkpoint()
        throws TransactionNotInProgressException, TransactionAbortedException
    {
        throw new IllegalStateException();
    }


}





