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


package org.exolab.castor.dax.engine;


import java.io.PrintWriter;
import java.net.MalformedURLException;
import netscape.ldap.LDAPUrl;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import org.exolab.castor.dax.Directory;
import org.exolab.castor.dax.DirectoryException;
import org.exolab.castor.dax.InvalidSearchException;
import org.exolab.castor.dax.DuplicateRDNException;
import org.exolab.castor.dax.Search;
import org.exolab.castor.mapping.ObjectDesc;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.TransactionContext.AccessMode;
import org.exolab.castor.persist.DuplicateIdentityException;
import org.exolab.castor.persist.TransactionAbortedException;
import org.exolab.castor.persist.PersistenceException;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.TransactionNotInProgressException;
import org.exolab.castor.persist.TransactionAbortedException;
import org.exolab.castor.persist.ClassNotPersistenceCapableException;


/**
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DirectoryImpl
    implements Directory
{


    private LDAPConnection  _conn;


    private LDAPUrl         _url;


    private String          _rdn;


    private DirectoryEngine _dirEngine;


    private PrintWriter     _logWriter;


    private MappingResolver _mapResolver;


    private TransactionContext _tx;


    DirectoryImpl( LDAPConnection conn, LDAPUrl url, 
		   MappingResolver mapResolver, PrintWriter logWriter )
	throws DirectoryException
    {
	_conn = conn;
	_url = url;
	_rdn = _url.getDN();
	_mapResolver = mapResolver;
	try {
	    _dirEngine = DirectoryEngine.getEngine( _url, (ObjectDesc) _mapResolver.listDescriptors().nextElement() );
	} catch ( MappingException except ) {
	    throw new DirectoryException( except );
	}
	_logWriter = logWriter;
    }


    DirectoryImpl( DirectoryImpl source, String rdn )
	throws DirectoryException
    {
	_conn = source._conn;
	_rdn = rdn;
	_url = new LDAPUrl( source._url.getHost(), source._url.getPort(),
			    _rdn + "," + source._url.getDN() );
	try {
	    _dirEngine = DirectoryEngine.getEngine( _url, (ObjectDesc) source._mapResolver.listDescriptors().nextElement() );
	} catch ( MappingException except ) {
	    throw new DirectoryException( except );
	}
	_logWriter = source._logWriter;
    }


    public String getRDN()
    {
	return _rdn;
    }


    public Search newSearch( String expr )
	throws InvalidSearchException, DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );

	return null;
    }


    public synchronized Object read( Object rdn )
	throws DirectoryException
    {
	ObjectDesc         objDesc;
	Object             obj;

	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );

	objDesc = _dirEngine.getObjectDesc();
	obj = objDesc.createNew();
	try {
	    if ( _tx != null ) {
		_tx.load( _dirEngine, obj, rdn, AccessMode.ReadWrite );
	    } else {
		TransactionContext tx;
		
		tx = new TransactionContextImpl( _conn );
		tx.load( _dirEngine, obj, rdn, AccessMode.ReadOnly );
		tx.commit();
	    }
	} catch ( ObjectNotFoundException except ) {
	    return null;
	} catch ( PersistenceException except ) {
	    if ( except.getException() != null )
		throw new DirectoryException( except.getException() );
	    else
		throw new DirectoryException( except );
	}
	return obj;
    }


    public synchronized void create( Object obj )
 	throws DuplicateRDNException, DirectoryException
    {
	ObjectDesc         objDesc;
	Object             rdn;

	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );

	objDesc = _dirEngine.getObjectDesc();
	while ( objDesc != null ) {
	    if ( objDesc.getObjectType().isAssignableFrom( obj.getClass() ) )
		break;
	    objDesc = objDesc.getExtends();
	}
	if ( objDesc == null )
	    throw new DirectoryException( new ClassNotPersistenceCapableException( obj.getClass() ) );

	rdn = objDesc.getIdentityField().getValue( obj );
	if ( rdn == null )
	    throw new DirectoryException( "Object has no RDN" );
	try {
	    if ( _tx != null ) {
		_tx.create( _dirEngine, obj, rdn );
	    } else {
		TransactionContext tx;

		tx = new TransactionContextImpl( _conn );
		tx.create( _dirEngine, obj, rdn );
		tx.commit();
	    }
	} catch ( DuplicateIdentityException except ) {
	    throw new DuplicateRDNException( "Duplicate RDN" );
	} catch ( PersistenceException except ) {
	    if ( except.getException() != null )
		throw new DirectoryException( except.getException() );
	    else
		throw new DirectoryException( except );
	}
    }


    public synchronized void delete( Object obj )
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
    }


    public synchronized void store( Object obj )
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
    }


    public synchronized void begin()
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
	if ( _tx != null )
	    throw new DirectoryException( "Already inside a transaction" );
	_tx = new TransactionContextImpl( _conn );
    }


    public synchronized void commit()
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
	if ( _tx == null )
	    throw new DirectoryException( "Not inside a transaction" );
	try {
	    try {
		_tx.prepare();
		_tx.commit();
	    } catch ( TransactionAbortedException except ) {
		_tx.rollback();
	    }
	} catch ( TransactionNotInProgressException except ) {
	    throw new DirectoryException( except );
	} finally {
	    _tx = null;
	}
    }


    public synchronized void rollback()
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
	if ( _tx == null )
	    throw new DirectoryException( "Not inside a transaction" );
	try {
	    _tx.rollback();
	} catch ( TransactionNotInProgressException except ) {
	    throw new DirectoryException( except );
	} finally {
	    _tx = null;
	}
    }


    public boolean isPersistent( Object obj )
    {
	return false;
    }


    public Directory getDirectory( String rdn )
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
	return new DirectoryImpl( this, rdn );
    }


    public synchronized void close()
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
	_dirEngine = null;
	_conn = null;
    }


}
