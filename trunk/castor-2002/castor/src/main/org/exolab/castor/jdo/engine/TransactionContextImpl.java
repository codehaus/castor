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
import javax.transaction.Status;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAResource;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.PersistenceException;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.TransactionAbortedException;


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
final class TransactionContextImpl
    extends TransactionContext
{


    /**
     * Lists all the connections opened for particular database engines
     * used in the lifetime of this transaction. The database engine
     * is used as the key to an open/transactional connection.
     */
    private Hashtable   _conns = new Hashtable();


    /**
     * Create a new transaction context. This method is used by the
     * ODMG transaction model, see {@link TransactionImpl}.
     */
    public TransactionContextImpl()
    {
        super();
    }


    /**
     * Create a new transaction context. This method is used by the
     * JTA transaction model, see {@link XAResourceImpl}.
     */
    public TransactionContextImpl( Xid xid )
    {
        super( xid );
    }


    /*
      // XXX NOT FULLY IMPLEMENTED
      public synchronized Object query( DatabaseEngine dbEngine, Class type, String sql,
      Object[] values, int accessMode, int timeout )
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
      ( accessMode == AccessMode.Exclusive ), timeout );
      if ( oid == null )
      return null;
      
      // It is possible that we already retrieved this object in this
      // transaction, in which case we look up the retrieve record and
      // decide whether to return it or not.
      entry = getObjectEntry( oid );
      if ( entry != null ) {
      if ( entry.deleted )
      return null;
      if ( accessMode != AccessMode.ReadOnly )
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
      if ( accessMode == AccessMode.ReadOnly ) {
      entry.readOnly = true;
      entry.dbEngine.releaseLock( this, entry.oid );
      }    
      return obj;
      }
    */

    protected void commitConnections( boolean keepOpen )
        throws TransactionAbortedException
    {
        Enumeration enum;
        Connection  conn;
        
        try {
            if ( getXid() == null ) {
                // Go through all the connections opened in this transaction,
                // commit and close them one by one.
                enum = _conns.elements();
                while ( enum.hasMoreElements() ) {
                    conn = (Connection) enum.nextElement();
                    // Checkpoint can only be done if transaction is not running
                    // under transaction monitor
                    conn.commit();
                    if ( keepOpen )
                        conn.setAutoCommit( false );
                }
            }
        } catch ( SQLException except ) {
            // [oleg] Check for rollback exception based on X/Open error code
            if ( except.getSQLState() != null &&
                 except.getSQLState().startsWith( "40" ) )
                throw new TransactionAbortedException( except );
            
            throw new TransactionAbortedException( except );
        } finally {
            if ( ! keepOpen ) {
                enum = _conns.elements();
                while ( enum.hasMoreElements() ) {
                    try {
                        ( (Connection) enum.nextElement() ).close();
                    } catch ( SQLException except ) { }
                }
                _conns.clear();
            }
        }
    }


    protected void rollbackConnections()
    {
        Connection  conn;
        Enumeration enum;
        
        if ( getXid() == null ) {
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
    }


    public Object getConnection( PersistenceEngine engine )
        throws PersistenceException
    {
        Connection conn;
        
        conn = (Connection) _conns.get( engine );
        if ( conn == null ) {
            try {
                // Get a new connection from the engine. Since the
                // engine has no transaction association, we must do
                // this sort of round trip. An attempt to have the
                // transaction association in the engine inflates the
                // code size in other places.
                conn = DatabaseSource.createConnection( engine );
                if ( getXid() == null )
                    conn.setAutoCommit( false );
                _conns.put( engine, conn );
            } catch ( SQLException except ) {
                throw new PersistenceException( except );
            }
        }
        return conn;
    }


}
