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


import java.util.Vector;
import java.util.StringTokenizer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.odmg.OQLQuery;
import org.odmg.Database;
import org.odmg.ODMGRuntimeException;
import org.odmg.ODMGException;
import org.odmg.QueryParameterCountInvalidException;
import org.odmg.QueryParameterTypeInvalidException;
import org.odmg.QueryInvalidException;
import org.odmg.TransactionNotInProgressException;
import org.exolab.castor.util.Messages;
import org.exolab.castor.persist.TransactionContext.AccessMode;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.QueryResults;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.QueryException;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.PersistenceException;
import org.exolab.castor.persist.LockNotGrantedException;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.mapping.ContainerFieldDesc;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class OQLQueryImpl
    implements OQLQuery
{


    private int                _fieldNum;


    private PersistenceEngine  _dbEngine;


    private Class              _objClass;


    private PersistenceQuery   _query;


    public void bind( Object obj )
	throws QueryParameterCountInvalidException,
	       QueryParameterTypeInvalidException
    {
	if ( _query == null ) {
	    throw new ODMGRuntimeException( "Must create query before using it" );
	}
	if ( _fieldNum == _query.getParameterCount() )
	    throw new QueryParameterCountInvalidException( "Only " + _query.getParameterCount() +
							   " fields in this query" );
	try {
	    _query.setParameter( _fieldNum, obj );
	} catch ( IndexOutOfBoundsException except ) {
	    throw new QueryParameterCountInvalidException( "Only " + _query.getParameterCount() +
							   " fields in this query" );
	} catch ( IllegalArgumentException except ) {
	    throw new QueryParameterTypeInvalidException( except.getMessage() );
	}
	++_fieldNum;
    }


    public void create( String oql )
	throws QueryInvalidException
    {
	StringTokenizer token;
	String          objType;
	String          objName;
	StringBuffer    sql;
	JDOClassDesc   clsDesc;
	SQLEngine       engine;
	Vector          types;
	Class[]         array;

	_fieldNum = 0;
	types = new Vector();
	sql = new StringBuffer();
	token = new StringTokenizer( oql );
	if ( ! token.hasMoreTokens() || ! token.nextToken().equalsIgnoreCase( "SELECT" ) )
	    throw new QueryInvalidException( "Query must start with SELECT" );
	if ( ! token.hasMoreTokens() )
	    throw new QueryInvalidException( "Missing object name" );
	objName = token.nextToken();
	if ( ! token.hasMoreTokens() || ! token.nextToken().equalsIgnoreCase( "FROM" ) )
	    throw new QueryInvalidException( "Object must be followed by FROM" );
	if ( ! token.hasMoreTokens() )
	    throw new QueryInvalidException( "Missing object type" );
	objType = token.nextToken();
	if ( ! token.hasMoreTokens() )
	    throw new QueryInvalidException( "Missing object name" );
	if ( ! objName.equals( token.nextToken() ) )
	    throw new QueryInvalidException( "Object name not same in SELECT and FROM" );

	try {
	    _objClass = Class.forName( objType );
	} catch ( ClassNotFoundException except ) {
	    throw new QueryInvalidException( "Could not find class " + objType );
	}
	_dbEngine = DatabaseSource.getPersistenceEngine( _objClass ); 
	if ( _dbEngine == null )
	    throw new QueryInvalidException( "Cold not find an engine supporting class " + objType );
	engine = (SQLEngine) _dbEngine.getPersistence( _objClass );
	clsDesc = engine.getClassDesc();

	if ( token.hasMoreTokens() ) {
	    if ( ! token.nextToken().equalsIgnoreCase( "WHERE" ) )
		throw new QueryInvalidException( "Missing WHERE clause" );
	    parseField( clsDesc, token, sql, types );
	    while ( token.hasMoreTokens() ) {
		if ( ! token.nextToken().equals( "AND" ) )
		    throw new QueryInvalidException( "Only AND supported in WHERE clause" );
		parseField( clsDesc, token, sql, types );
	    }
	} else {
	    sql.append( "1 = 1" );
	}

	sql.insert( 0, engine._sqlFinder ).append( engine._sqlFinderJoin );
	array = new Class[ types.size() ];
	types.copyInto( array );
	try {
	    _query = engine.createQuery( sql.toString(), array );
	} catch ( QueryException except ) {
	    throw new QueryInvalidException( except.getMessage() );
	}
    }


    private void parseField( JDOClassDesc clsDesc, StringTokenizer token,
			     StringBuffer sqlWhere, Vector types )
	throws QueryInvalidException
    {
	String         name;
	String         op;
	String         value;
	JDOFieldDesc[] fields;
	JDOFieldDesc   field;

	if ( ! token.hasMoreTokens() )
	    throw new QueryInvalidException( "Missing field name" );
	name = token.nextToken();
	if ( ! token.hasMoreTokens() )
	    throw new QueryInvalidException( "Missing operator" );
	op = token.nextToken();
	if ( ! token.hasMoreTokens() )
	    throw new QueryInvalidException( "Missing field value" );

	value = token.nextToken();
	if ( name.indexOf( "." ) > 0 )
	    name = name.substring( name.indexOf( "." ) + 1 );
	fields = (JDOFieldDesc[]) clsDesc.getFields();
	field = null;
	for ( int i = 0 ; i < fields.length ; ++i ) {
	    if ( fields[ i ].getFieldName().equals( name ) ) {
		sqlWhere.append( clsDesc.getTableName() + "." + fields[ i ].getSQLName() );
		field = fields[ i ];
		break;
	    }
	}

	if ( fields == null ) {
	    if ( clsDesc.getIdentity() instanceof ContainerFieldDesc ) {
		fields = (JDOFieldDesc[]) ( (ContainerFieldDesc) clsDesc.getIdentity() ).getFields();
		for ( int i = 0 ; i < fields.length ; ++i ) {
		    if ( fields[ i ].getFieldName().equals( name ) ) {
			sqlWhere.append( clsDesc.getTableName() + "." + fields[ i ].getSQLName() );
			field = fields[ i ];
			break;
		    }
		}
	    } else if ( clsDesc.getIdentity().getFieldName().equals( name ) ) {
		sqlWhere.append( clsDesc.getTableName() + "." + ( (JDOFieldDesc) clsDesc.getIdentity() ).getSQLName() );
		field = (JDOFieldDesc) clsDesc.getIdentity();
	    }
	}

	if ( field == null )
	    throw new QueryInvalidException( "The field " + name + " was not found" );
	sqlWhere.append( op );
	if ( value.startsWith( "$" ) ) {
	    sqlWhere.append( "?" );
	    types.addElement( field.getFieldType() );
	} else {
	    sqlWhere.append( value );
	}
    }


    public Object execute()
	throws QueryInvalidException
    {
	TransactionContext tx;
	QueryResults       results;
	Object             obj;
	Object             identity;
	Vector             set;

	try {
	    tx = TransactionImpl.getCurrentContext();
	    if ( tx == null || ! tx.isOpen() )
		throw new TransactionNotInProgressException( Messages.message( "castor.jdo.odmg.dbTxNotInProgress" ) );
	    results = tx.query( _dbEngine, _query, AccessMode.ReadWrite );
	    _fieldNum = 0;

	    set = new Vector();
	    identity = results.nextIdentity();
	    while ( identity != null ) {
		try {
		    obj = _dbEngine.getClassDesc( results.getResultType() ).newInstance();
		    results.fetch( obj );
		    set.addElement( obj );
		} catch ( ObjectNotFoundException except ) {
		}
		identity = results.nextIdentity();
	    }
	    if ( set.size() == 0 )
		return null;
	    if ( set.size() == 1 )
		return set.elementAt( 0 );
	    return set.elements();
	} catch ( QueryException except ) {
	    throw new QueryInvalidException( except.getMessage() );
	} catch ( org.exolab.castor.persist.TransactionNotInProgressException except ) {
	    throw new TransactionNotInProgressException( except.getMessage() );
	} catch ( LockNotGrantedException except ) {
	    throw new ODMGRuntimeException( except.toString() );
	} catch ( PersistenceException except ) {
	    throw new ODMGRuntimeException( except.toString() );
	}
    }


}
