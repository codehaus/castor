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
import java.util.Enumeration;
import java.util.Hashtable;
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
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.PersistenceEngineFactory;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.ObjectNotPersistentException;
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


    private String          _dn;


    private PersistenceEngine _dirEngine;


    private PrintWriter     _logWriter;


    private MappingResolver _mapResolver;


    private TransactionContext _tx;


    private ObjectDesc        _objDesc;


    DirectoryImpl( LDAPConnection conn, LDAPUrl url, 
		   MappingResolver mapResolver, PrintWriter logWriter )
	throws DirectoryException
    {
	_conn = conn;
	_dn = url.getDN();
	_mapResolver = mapResolver;
	_objDesc = (ObjectDesc) _mapResolver.listDescriptors().nextElement();
	try {
	    _dirEngine = getEngine( url, _objDesc , logWriter );
	} catch ( MappingException except ) {
	    throw new DirectoryException( except );
	}
	_logWriter = logWriter;
    }


    public String getDN()
    {
	return _dn;
    }


    public Search createSearch( String expr )
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

	objDesc = _objDesc;
	// objDesc = _dirEngine.getObjectDesc();
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

	objDesc = _objDesc;
	// objDesc = _dirEngine.getObjectDesc();
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
	ObjectDesc objDesc;

	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );

	objDesc = _objDesc;
	// objDesc = _dirEngine.getObjectDesc();
	while ( objDesc != null ) {
	    if ( objDesc.getObjectType().isAssignableFrom( obj.getClass() ) )
		break;
	    objDesc = objDesc.getExtends();
	}
	if ( objDesc == null )
	    throw new DirectoryException( new ClassNotPersistenceCapableException( obj.getClass() ) );

	try {
	    if ( _tx != null ) {
		_tx.delete( obj );
	    } else {
		TransactionContext tx;

		tx = new TransactionContextImpl( _conn );
		tx.delete( obj );
		tx.commit();
	    }
	} catch ( ObjectNotPersistentException except ) {
	    throw new DuplicateRDNException( "Object not persistent" );
	} catch ( PersistenceException except ) {
	    if ( except.getException() != null )
		throw new DirectoryException( except.getException() );
	    else
		throw new DirectoryException( except );
	}
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


    public synchronized boolean isPersistent( Object obj )
    {
	// If directory is closed or not inside transaction, return null.
	if ( _dirEngine == null || _tx == null )
	    return false;
	return _tx.isPersistent( obj );
    }


    public synchronized void close()
	throws DirectoryException
    {
	if ( _dirEngine == null )
	    throw new DirectoryException( "Directory closed" );
	_dirEngine = null;
	try {
	    _conn.disconnect();
	} catch ( LDAPException except ) {
	    throw new DirectoryException( except );
	} finally {
	    _conn = null;
	}
    }


    public void finalizer()
    {
	try {
	    if ( _conn != null )
		_conn.disconnect();
	} catch ( LDAPException except ) {
	}
    }





    private static Hashtable  _engines = new Hashtable();


    public static PersistenceEngine getEngine( LDAPUrl url, ObjectDesc objDesc, PrintWriter logWriter )
	throws MappingException
    {
	PersistenceEngine engine;

	synchronized ( _engines ) {
	    engine = (PersistenceEngine) _engines.get( url );
	    if ( engine == null ) {
		engine = new PersistenceEngineFactory().createEngine( new SingleMapping( objDesc ),
								      new EngineFactory( url.getDN() ), logWriter );
		_engines.put( url, engine );
	    }
	    return engine;
	}
    }


    static class SingleMapping
	implements MappingResolver
    {

	private Hashtable _objDescs;

	SingleMapping( ObjectDesc objDesc )
	{
	    _objDescs = new Hashtable();
	    _objDescs.put( objDesc.getObjectType(), objDesc );
	}

	public ObjectDesc getDescriptor( Class type )
	{
	    return (ObjectDesc) _objDescs.get( type );
	}

	public Enumeration listDescriptors()
	{
	    return _objDescs.elements();
	}

	public Enumeration listObjectTypes()
	{
	    return _objDescs.keys();
	}

    }


    static class EngineFactory
	implements PersistenceFactory
    {

	private String _rootDN;

	EngineFactory( String rootDN )
	{
	    _rootDN = rootDN;
	}
	
	public Persistence getPersistence( ObjectDesc objDesc, PrintWriter logWriter )
	{
	    try {
		return new MozillaEngine( (DAXObjectDesc) objDesc, _rootDN );
	    } catch ( MappingException except ) {
		return null;
	    }
	}

    }


}
