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
import java.util.Vector;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.exolab.castor.jdo.desc.PrimaryKeyDesc;
import org.exolab.castor.jdo.desc.JDOFieldDesc;
import org.exolab.castor.jdo.desc.JDOObjectDesc;
import org.exolab.castor.jdo.desc.RelationDesc;
import org.exolab.castor.jdo.desc.Relation;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ObjectDesc;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.QueryException;
import org.exolab.castor.persist.DuplicateIdentityException;
import org.exolab.castor.persist.PersistenceException;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.ObjectModifiedException;
import org.exolab.castor.persist.ObjectDeletedException;
import org.exolab.castor.persist.QueryException;


/**
 * The SQL engine performs persistence of one object type against one
 * SQL database. It can only persist simple objects and extended
 * relationships. An SQL engine is created for each object type
 * represented by a database. When persisting, it requires a physical
 * connection that maps to the SQL database and the transaction
 * running on that database, through the {@link ConnectionProvider}
 * interface.
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
class SQLEngine
    implements Persistence
{


    private JDOObjectDesc    _objDesc;


    PrimaryKeyDesc           _primKey;


    private boolean         _specifyKeyForCreate = true;


    private boolean         _useCursorForLock = false;


    String                  _stampField; // = "ctid";


    private String          _pkLookup;


    private String          _sqlCreate;


    private String          _sqlRemove;


    private String          _sqlStore;


    private String          _sqlLoad;


    private String          _sqlLoadLock;


    String          _sqlFinder;
    String          _sqlFinderJoin;


    JDOFieldDesc[]           _loadFields;


    private SQLRelated[]     _related;


    private SQLEngine        _extends;


    SQLEngine( JDOObjectDesc objDesc, PrintWriter logWriter )
	throws MappingException
    {
	if ( objDesc == null )
	    throw new IllegalArgumentException( "Argument 'objDesc' is null" );
	_objDesc = objDesc;
	_primKey = _objDesc.getPrimaryKey();
	if ( _primKey == null )
	    throw new MappingException( "Cannot persist a table that lacks a primary key descriptor" );
	if ( _objDesc.getPrimaryKeyField() == null )
	    throw new MappingException( "Cannot persist an object with an external primary key" );
	buildCreateSql();
	buildRemoveSql();
	buildStoreSql();
	buildLoadSql();
	buildRelated( logWriter );
	if ( logWriter != null ) {
	    logWriter.println( "SQL for " + _objDesc.getObjectType().getName() +
			       ": " + _sqlLoad );
	    logWriter.println( "SQL for " + _objDesc.getObjectType().getName() +
			       ": " + _sqlCreate );
	    logWriter.println( "SQL for " + _objDesc.getObjectType().getName() +
			       ": " + _sqlStore );
	    logWriter.println( "SQL for " + _objDesc.getObjectType().getName() +
			       ": " + _sqlRemove );
	}
	if ( _objDesc.getExtends() != null )
	    _extends = new SQLEngine( (JDOObjectDesc) _objDesc.getExtends(), logWriter );
    }


    SQLEngine( JDOObjectDesc objDesc, PrimaryKeyDesc primKey, PrintWriter logWriter )
	throws MappingException
    {
	this( objDesc, logWriter );
	_primKey = primKey;
    }


    public JDOObjectDesc getObjectDesc()
    {
	return _objDesc;
    }


    public PersistenceQuery createQuery( String query, Class[] types )
	throws QueryException
    {
	return new SQLQuery( this, query, types );
    }


    public Object create( Object conn, Object obj, Object identity )
	throws DuplicateIdentityException, PersistenceException
    {
	JDOFieldDesc[]    descs;
	PreparedStatement stmt;
	int               i, j;
	int               count;
	Object            value;

	stmt = null;
	try {
	    if ( _related != null ) {
		for ( i = 0 ; i < _related.length ; ++i ) {
		    if ( _related[ i ].getRelationType() == Relation.OneToOne ) {
			_related[ i ].create( conn, obj, identity );
		    }
		}
	    }
	    if ( _extends != null ) {
		_extends.create( conn, obj, identity );
	    }

	    // If creation requires a primary key to be supplied, must check
	    // that no such primary key exists in the table. This call will
	    // also lock the table against creation of an object with such
	    // a primary key.
	    if ( _specifyKeyForCreate && identity == null )
		throw new PersistenceException( "This implementation requires a primary key to be set prior to object creation" );
	    
	    // Must remember that SQL column index is base one
	    count = 1;
	    stmt = ( (Connection) conn ).prepareStatement( _sqlCreate );
	    if ( _specifyKeyForCreate ) {
		if ( _primKey.isPrimitive() ) {
		    stmt.setObject( count, identity );
		    count += 1;
		} else {
		    identity = _objDesc.getPrimaryKeyField().getValue( obj );
		    descs = _primKey.getJDOFields();
		    for ( i = 0 ; i < descs.length ; ++i ) {
			descs[ i ].getValue( identity, stmt, count + i );
		    }
		    count += i;
		}
	    }
	    
	    descs = _objDesc.getJDOFields();
	    for ( i = 0 ; i < descs.length ; ++i ) {
		value = descs[ i ].getValue( obj );
		if ( value != null )
		    stmt.setObject( count + i, value );
	    }
	    count += i;
	    if ( _objDesc.getRelated() != null ) {
		for ( i = 0 ; i < _objDesc.getRelated().length ; ++i ) {
		    RelationDesc related;
		    
		    related = _objDesc.getRelated()[ i ];
		    if ( related.getRelationType() == Relation.ManyToOne ) {
			if ( related.getPrimaryKey().isPrimitive() ) {
			    value = related.getPrimaryKeyField().getValue( obj );
			    if ( value != null )
				stmt.setObject( count, value );
			    count += 1;
			} else {
			    identity = related.getPrimaryKeyField().getValue( obj );
			    descs = related.getPrimaryKey().getJDOFields();
			    for ( j = 0 ; j < descs.length ; ++j ) {
				value = descs[ j ].getValue( identity );
				if ( value != null )
				    stmt.setObject( count + j, value );
			    }
			    count += j;
			}
		    }
		}
	    }

	    stmt.executeUpdate();
	    stmt.close();
	    return null;
	} catch ( SQLException except ) {
	    // [oleg] Check for duplicate key based on X/Open error code
	    if ( except.getSQLState() != null &&
		 except.getSQLState().startsWith( "23" ) )
		throw new DuplicateIdentityException( obj.getClass(), identity );

	    // [oleg] Check for duplicate key the old fashioned way,
	    //        after the INSERT failed to prevent race conditions
	    //        and optimize INSERT times
	    try {
		// Close the insert statement
		if ( stmt != null )
		    stmt.close();

		stmt = ( (Connection) conn ).prepareStatement( _pkLookup );
		if ( _primKey.isPrimitive() ) {
		    stmt.setObject( 1, identity );
		} else {
		    descs = _primKey.getJDOFields();
		    for ( i = 0 ; i < descs.length ; ++i ) {
			descs[ i ].getValue( identity, stmt, i + 1 );
		    }
		}
		if ( stmt.executeQuery().next() )
		    throw new DuplicateIdentityException( obj.getClass(), identity );
	    } catch ( SQLException except2 ) {
		// Error at the stage indicates it wasn't a duplicate
		// primary key problem. But best if the INSERT error is
		// reported, not the SELECT error.
	    }
	    
	    try {
		// Close the insert/select statement
		if ( stmt != null )
		    stmt.close();
	    } catch ( SQLException except2 ) { }

	    throw new PersistenceException( except );
	}
    }

    public Object load( Object conn, Object obj, Object identity, boolean lock )
	throws ObjectNotFoundException, PersistenceException
    {
	PreparedStatement stmt;
	ResultSet         rs;
	JDOFieldDesc[]    pkDescs = null;
	Object            pk;
	Object            stamp = null;

	try {
	    // [Oleg Nitz] SELECT FOR UPDATE requires cursor in some databases
	    if ( _useCursorForLock )
		stmt = ( (Connection) conn ).prepareStatement( lock ? _sqlLoadLock : _sqlLoad,
                    ResultSet.TYPE_FORWARD_ONLY, lock ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY );
	    else
		stmt = ( (Connection) conn ).prepareStatement( lock ? _sqlLoadLock : _sqlLoad );
	    if ( _primKey.isPrimitive() ) {
		stmt.setObject( 1, identity );
	    } else {
		pkDescs = _primKey.getJDOFields();
		for ( int i = 0 ; i < pkDescs.length ; ++i ) {
		    pkDescs[ i ].getValue( identity, stmt, i + 1 );
		}
	    }

	    rs = stmt.executeQuery();
	    if ( ! rs.next() )
		throw new ObjectNotFoundException( obj.getClass(), identity );
	    
	    do {
		// First iteration for a PK: traverse all the fields
		for ( int i = 0; i < _loadFields.length ; ++i  ) {
		    // Usinging typed setValue so float/double, int/long
		    // can be intermixed with automatic conversion, something
		    // that throws an exception in the untyped version
		    _loadFields[ i ].setValue( obj, rs, i + 1 );
		}
	    } while ( rs.next() );
	    if ( _stampField != null )
		stamp = rs.getObject( _loadFields.length + 1 );
	    rs.close();
	    stmt.close();
	} catch ( SQLException except ) {
	    throw new PersistenceException( except );
	}
	return stamp;
    }


    public Object store( Object conn, Object obj, Object identity,
			 Object original, Object stamp )
	throws ObjectModifiedException, ObjectDeletedException, PersistenceException
    {
	PreparedStatement stmt;
	JDOFieldDesc[]    descs;
	RelationDesc      related;
	int               i, j;
	int               count;
	Object            value;

	try {
	    if ( _related != null ) {
		for ( i = 0 ; i < _related.length ; ++i ) {
		    if ( _related[ i ].getRelationType() == Relation.OneToOne ) {
			_related[ i ].store( conn, obj, identity, original, stamp );
		    }
		}
	    }
	    if ( _extends != null ) {
		_extends.store( conn, obj, identity, original, stamp );
	    }

	    stmt = ( (Connection) conn ).prepareStatement( _sqlStore );

	    descs = _objDesc.getJDOFields();
	    count = 1;
	    for ( i = 0 ; i < descs.length ; ++i ) {
		value = descs[ i ].getValue( obj );
		if ( value != null )
		    stmt.setObject( count + i, value );
	    }
	    count += i;
	    if ( _objDesc.getRelated() != null ) {
		for ( i = 0 ; i < _objDesc.getRelated().length ; ++i ) {
		    related = _objDesc.getRelated()[ i ];
		    
		    if ( related.getRelationType() == Relation.ManyToOne ) {
			if ( related.getPrimaryKey().isPrimitive() ) {
			    value = related.getPrimaryKeyField().getValue( obj );
			    if ( value != null )
				stmt.setObject( count, value );
			    count += 1;
			} else {
			    identity = related.getPrimaryKeyField().getValue( obj );
			    descs = related.getPrimaryKey().getJDOFields();
			    for ( j = 0 ; j < descs.length ; ++j ) {
				value = descs[ j ].getValue( identity );
				if ( value != null )
				    stmt.setObject( count + j, value );
			    }
			    count += j;
			}
		    }
		}
	    }
	    
	    if ( _primKey.isPrimitive() ) {
		stmt.setObject( count, identity );
	    } else {
		descs = _primKey.getJDOFields();
		for ( i = 0 ; i < descs.length ; ++i ) {
		    descs[ i ].getValue( identity, stmt, count + 1 );
		}
	    }
	    if ( stmt.executeUpdate() == 0 ) {
		// If no update was performed, the object has been previously
		// removed from persistent storage. Complain about this.
		stmt.close();
		throw new ObjectDeletedException( obj.getClass(), identity );
	    }
	    stmt.close();
	    return null;
	} catch ( SQLException except ) {
	    throw new PersistenceException( except );
	}
    }


    public void delete( Object conn, Object obj, Object identity )
	throws PersistenceException
    {
	PreparedStatement stmt;
	JDOFieldDesc[]    descs;

	try {
	    for ( int i = 0 ; i < _related.length ; ++i ) {
		if ( _related[ i ].getRelationType() == Relation.OneToOne ) {
		    _related[ i ].delete( conn, obj, identity );
		}
	    }
	    if ( _extends != null ) {
		_extends.delete( conn, obj, identity );
	    }

	    stmt = ( (Connection) conn ).prepareStatement( _sqlRemove );
	    if ( _primKey.isPrimitive() ) {
		stmt.setObject( 1, identity );
	    } else {
		descs = _primKey.getJDOFields();
		for ( int i = 0 ; i < descs.length ; ++i ) {
		    descs[ i ].getValue( identity, stmt, i + 1 );
		}
	    }
	    stmt.close();
	} catch ( SQLException except ) {
	    throw new PersistenceException( except );
	}
    }


    public void writeLock( Object conn, Object obj, Object identity )
	throws ObjectDeletedException, PersistenceException
    {
	PreparedStatement stmt;
	JDOFieldDesc[]    descs;

	try {
	    if ( _extends != null ) {
		_extends.writeLock( conn, obj, identity );
	    }
	    // Only write locks are implemented by locking the row.
	    // [Oleg Nitz] SELECT FOR UPDATE requires cursor in some databases
	    if ( _useCursorForLock )
		stmt = ( (Connection) conn ).prepareStatement( _pkLookup, ResultSet.TYPE_FORWARD_ONLY,
							       ResultSet.CONCUR_UPDATABLE );
	    else
		stmt = ( (Connection) conn ).prepareStatement( _pkLookup );

	    if ( _primKey.isPrimitive() ) {
		stmt.setObject( 1, identity );
	    } else {
		descs = _primKey.getJDOFields();
		for ( int i = 0 ; i < descs.length ; ++i ) {
		    descs[ i ].getValue( identity, stmt, i + 1 );
		}
	    }
	    if ( ! stmt.executeQuery().next() )
		throw new ObjectDeletedException( obj.getClass(), identity );
	    stmt.close();
	} catch ( SQLException except ) {
	    throw new PersistenceException( except );
	}
    }


    public void changeIdentity( Object conn, Object obj, Object oldIdentity, Object newIdentity )
	throws DuplicateIdentityException, PersistenceException
    {
    }


    protected void buildRelated( PrintWriter logWriter )
	throws MappingException
    {
	RelationDesc[] related;
	Vector         engines;

	engines = new Vector();
	related = _objDesc.getRelated();
	if ( related != null ) {
	    for ( int i = 0 ; i < related.length ; ++i ) {
		if ( related[ i ].getRelationType() == Relation.OneToOne ) {
		    engines.addElement( new SQLRelated( related[ i ], _objDesc,
							related[ i ].getParentField(), logWriter ) );
		}
	    }
	    _related = new SQLRelated[ engines.size() ];
	    engines.copyInto( _related );
	}
    }


    protected void buildCreateSql()
    {
	StringBuffer   sql;
	JDOFieldDesc[] descs;
	RelationDesc[] related;
	int            count;

	// Create statement to lookup primary key and determine
	// if object with same primary key already exists
	if ( _specifyKeyForCreate ) {
	    sql = new StringBuffer( "SELECT 1 FROM " );
	    sql.append( _objDesc.getSQLName() ).append( " WHERE " );
	    if ( _primKey.isPrimitive() ) {
		sql.append( _primKey.getSQLName() );
		sql.append( "=?" );
	    } else {
		descs = _primKey.getJDOFields();
		for ( int i = 0 ; i < descs.length ; ++i ) {
		    if ( i > 0 )
			sql.append( " AND " );
		    sql.append( descs[ i ].getSQLName() );
		    sql.append( "=?" );
		}
	    }
	    sql.append( " FOR UPDATE" );
	    _pkLookup = sql.toString();
	}

	// Create statement to insert a new row into the table
	// using the specified primary key if one is required
	sql = new StringBuffer( "INSERT INTO " );
	sql.append( _objDesc.getSQLName() ).append( " (" );
	if ( _specifyKeyForCreate ) {
	    if ( _primKey.isPrimitive() ) {
		sql.append( _primKey.getSQLName() );
		count = 1;
	    } else {
		descs = _primKey.getJDOFields();
		for ( int i = 0 ; i < descs.length ; ++i ) {
		    if ( i > 0 )
			sql.append( ',' );
		    sql.append( descs[ i ].getSQLName() );
		}
		count = descs.length;
	    }
	} else {
	    count = 0;
	}

	descs = _objDesc.getJDOFields();
	for ( int i = 0 ; i < descs.length ; ++i ) {
	    if ( count > 0 )
		sql.append( ',' );
	    sql.append( descs[ i ].getSQLName() );
	    ++count;
	}
	related = _objDesc.getRelated();
	if ( related != null ) {
	    for ( int i = 0 ; i < related.length ; ++i ) {
		if ( related[ i ].getRelationType() == Relation.ManyToOne &&
		     related[ i ].getForeignKey() != null ) {
		    if ( count > 0 )
			sql.append( ',' );
		    sql.append( related[ i ].getForeignKey() );
		    ++count;
		}
	    }
	}
	sql.append( ") VALUES (" );
	for ( int i = 0 ; i < count ; ++i ) {
	    if ( i > 0 )
		sql.append( ',' );
	    sql.append( '?' );
	}
	sql.append( ')' );
	_sqlCreate = sql.toString();
    }


    protected void buildRemoveSql()
    {
	StringBuffer sql;

	sql = new StringBuffer( "DELETE FROM " );
	sql.append( _objDesc.getSQLName() ).append( " WHERE " );
	sql.append( buildWherePK() );
	_sqlRemove = sql.toString();
    }


    protected void buildStoreSql()
    {
	StringBuffer   sql;
	JDOFieldDesc[] descs;
	int            count;
	RelationDesc[] related;

	sql = new StringBuffer( "UPDATE " );
	sql.append( _objDesc.getSQLName() ).append( " SET " );
	descs = _objDesc.getJDOFields();
	for ( int i = 0 ; i < descs.length ; ++i ) {
	    if ( i > 0 )
		sql.append( ',' );
	    sql.append( descs[ i ].getSQLName() );
	    sql.append( "=?" );
	}
	count = descs.length;;
	related = _objDesc.getRelated();
	if ( related != null ) {
	    for ( int i = 0 ; i < related.length ; ++i ) {
		if ( related[ i ].getRelationType() == Relation.ManyToOne &&
		     related[ i ].getForeignKey() != null ) {
		    if ( count > 0 )
			sql.append( ',' );
		    sql.append( related[ i ].getForeignKey() );
		    sql.append( "=?" );
		    ++count;
		}
	    }
	}

	sql.append( " WHERE " ).append( buildWherePK() );
	_sqlStore = sql.toString();
    }


    protected StringBuffer buildWherePK()
    {
	StringBuffer   sql;
	JDOFieldDesc[] descs;
	int            i;

	sql = new StringBuffer();
	if ( _objDesc.getPrimaryKey().isPrimitive() ) {
	    sql.append( _objDesc.getPrimaryKey().getSQLName() );
	    sql.append( "=?" );
	} else {
	    descs = _objDesc.getPrimaryKey().getJDOFields();
	    for ( i = 0 ; i < descs.length ; ++i ) {
		if ( i > 0 )
		    sql.append( " AND " );
		sql.append( descs[ i ].getSQLName() );
		sql.append( "=?" );
	    }
	}
	return sql;
    }


    protected void buildLoadSql()
    {
	Vector         loadFields;
	StringBuffer   sqlFields;
	StringBuffer   sqlFrom;
	StringBuffer   sqlJoin;

	loadFields = new Vector();
	sqlFields = new StringBuffer( "SELECT " );
	sqlFrom = new StringBuffer( " FROM " );
	sqlJoin = new StringBuffer( " WHERE " );
	addLoadSql( _objDesc, sqlFields, sqlFrom,
		    sqlJoin, loadFields, 0, false );
	if ( _stampField != null )
	    sqlFields.append( ',' ).append( _stampField );

	_sqlLoad = sqlFields.append( sqlFrom ).append( sqlJoin ).toString();
	_sqlLoadLock = _sqlLoad + " FOR UPDATE";
	_loadFields = new JDOFieldDesc[ loadFields.size() ];
	loadFields.copyInto( _loadFields );

	sqlFields = new StringBuffer( "SELECT " );
	sqlFrom = new StringBuffer( " FROM " );
	sqlJoin = new StringBuffer( "" );
	addLoadSql( _objDesc, sqlFields, sqlFrom,
		    sqlJoin, loadFields, 0, true );
	if ( _stampField != null )
	    sqlFields.append( ',' ).append( _stampField );
	_sqlFinder = sqlFields.append( sqlFrom ).append( " WHERE " ).toString();
	_sqlFinderJoin = sqlJoin.toString();
    }


    private int addLoadSql( JDOObjectDesc objDesc, StringBuffer sqlFields,
			    StringBuffer sqlFrom, StringBuffer sqlJoin,
			    Vector loadFields, int count, boolean loadPk )
    {
	JDOFieldDesc[] descs;
	RelationDesc[] related;
	JDOObjectDesc  extend;
	PrimaryKeyDesc primKey;

	primKey = objDesc.getPrimaryKey();
	extend = (JDOObjectDesc) objDesc.getExtends();
	related = objDesc.getRelated();

	if ( count != 0 )
            sqlFrom.append( ',' );
        sqlFrom.append( objDesc.getSQLName() );

	if ( ! loadPk ) {
	    if ( primKey.isPrimitive() ) {
		sqlJoin.append( objDesc.getSQLName( primKey.getSQLName() ) );
		sqlJoin.append( "=?" );
	    } else {
		descs = primKey.getJDOFields();
		for ( int i = 0 ; i < descs.length ; ++i ) {
		    if ( i > 0 )
			sqlJoin.append( " AND " );
		    sqlJoin.append( objDesc.getSQLName( descs[ i ] ) );
		    sqlJoin.append( "=?" );
		}
	    }
	}

	if ( extend != null ) {
	    if ( primKey.isPrimitive() ) {
		sqlJoin.append( " AND " );
		sqlJoin.append( objDesc.getSQLName( primKey.getSQLName() ) );
		sqlJoin.append( "=" );
		sqlJoin.append( extend.getSQLName( primKey.getSQLName() ) );
	    } else {
		descs = primKey.getJDOFields();
		for ( int i = 0 ; i < descs.length ; ++i ) {
		    sqlJoin.append( " AND " );
		    sqlJoin.append( objDesc.getSQLName( descs[ i ] ) );
		    sqlJoin.append( "=" );
		    sqlJoin.append( extend.getSQLName( descs[ i ] ) );
		}
	    }
	}

	if ( objDesc instanceof RelationDesc )
	if ( objDesc instanceof RelationDesc &&
	     ( (RelationDesc) objDesc ).getRelationType() == Relation.OneToOne )
	    loadPk = false;
	
	if ( loadPk  ) {
	    if ( primKey.isPrimitive() ) {
		if ( count > 0 )
		    sqlFields.append( ',' );
		sqlFields.append( objDesc.getSQLName( primKey.getSQLName() ) );
		loadFields.addElement( objDesc.getPrimaryKeyField() );
		++count;
	    } else {
		descs = primKey.getJDOFields();
		for ( int i = 0 ; i < descs.length ; ++i ) {
		    if ( count > 0 )
			sqlFields.append( ',' );
		    sqlFields.append( objDesc.getSQLName( descs[ i ] ) );
		    loadFields.addElement( descs[ i ] );
		    ++count;
		}
	    }
	}
	descs = objDesc.getJDOFields();
	for ( int i = 0 ; i < descs.length ; ++i ) {
	    if ( count > 0 )
		sqlFields.append( ',' );
	    sqlFields.append( objDesc.getSQLName( descs[ i ] ) );
	    loadFields.addElement( descs[ i ] );
	    ++count;
	}

	related = objDesc.getRelated();
	if ( related != null ) {
	    for ( int i = 0 ; i < related.length ; ++i ) {
		sqlJoin.append( " AND " );
		if ( related[ i ].getForeignKeyField() == null )
		    sqlJoin.append( objDesc.getSQLName( related[ i ].getForeignKey() ) );
		else
		    sqlJoin.append( objDesc.getSQLName( related[ i ].getForeignKeyField().getSQLName() ) );
		sqlJoin.append( '=' );
		if ( related[ i ].getPrimaryKey().isPrimitive() )
		    sqlJoin.append( related[ i ].getSQLName( related[ i ].getPrimaryKey().getSQLName() ) );
		else
		    sqlJoin.append( related[ i ].getSQLName( related[ i ].getPrimaryKey().getJDOFields()[ 0 ].getSQLName() ) );
	    }
	}

	if ( extend != null ) {
	    count = addLoadSql( extend, sqlFields, sqlFrom,
				sqlJoin, loadFields, count, true );
	}
	if ( related != null ) {
	    for ( int i = 0 ; i < related.length ; ++i )
		count = addLoadSql( related[ i ], sqlFields, sqlFrom,
				    sqlJoin, loadFields, count, true );
	}
	return count;
    }



    static class SQLQuery
	implements PersistenceQuery
    {


	private ResultSet _rs;


	private SQLEngine _engine;


	private Class[]   _types;


	private Object[]  _values;


	private String    _sql;


	SQLQuery( SQLEngine engine, String sql, Class[] types )
	{
	    _engine = engine;
	    _types = types;
	    _values = new Object[ _types.length ];
	    _sql = sql;
	}


	public int getParameterCount()
	{
	    return _types.length;
	}


	public Class getParameterType( int index )
	    throws ArrayIndexOutOfBoundsException
	{
	    return _types[ index ];
	}


	public void setParameter( int index, Object value )
	    throws ArrayIndexOutOfBoundsException, IllegalArgumentException
	{
	    // XXX Need to perform type conversion at this point
	    _values[ index ] = value;
	}


	public Class getResultType()
	{
	    return _engine.getObjectDesc().getObjectType();
	}


	public void execute( Object conn, boolean lock )
	    throws QueryException, PersistenceException
	{
	    PreparedStatement stmt;

	    try {
		stmt = ( (Connection) conn ).prepareStatement( _sql );
		for ( int i = 0 ; i < _values.length ; ++i ) {
		    stmt.setObject( i + 1, _values[ i ] );
		}
		_rs = stmt.executeQuery();
	    } catch ( SQLException except ) {
		throw new PersistenceException( except );
	    }
	}


	public Object nextIdentity()
	    throws PersistenceException
	{
	    Object         identity;
	    JDOFieldDesc[] pkDescs;
	    
	    try {
		if ( ! _rs.next() )
		    return null;
		if ( _engine._primKey.isPrimitive() ) {
		    identity = _rs.getObject( 1 );
		} else {
		    identity = _engine._primKey.createNew();
		    pkDescs = _engine._primKey.getJDOFields();
		    for ( int i = 0 ; i < pkDescs.length ; ++i ) {
			pkDescs[ i ].setValue( identity, _rs, 1 + i );
		    }
		}
	    } catch ( SQLException except ) {
		throw new PersistenceException( except );
	    }
	    return identity;
	}
	
	
	public Object getIdentity( int index )
	    throws PersistenceException
	{
	    Object         identity;
	    JDOFieldDesc[] pkDescs;
	    
	    try {
		_rs.absolute( index );
		if ( _rs.isLast() )
		    return null;
		if ( _engine._primKey.isPrimitive() ) {
		    identity = _rs.getObject( 1 );
		} else {
		    identity = _engine._primKey.createNew();
		    pkDescs = _engine._primKey.getJDOFields();
		    for ( int i = 0 ; i < pkDescs.length ; ++i ) {
			pkDescs[ i ].setValue( identity, _rs, 1 + i );
		    }
		}
	    } catch ( SQLException except ) {
		throw new PersistenceException( except );
	    }
	    return identity;
	}


	public int getLocation()
	    throws PersistenceException
	{
	    try {
		return _rs.getRow();
	    } catch ( SQLException except ) {
		throw new PersistenceException( except );
	    }
	}


	public boolean isForwardOnly()
	{
	    return true;
	}


	public Object fetch( Object obj )
	    throws ObjectNotFoundException, PersistenceException
	{
	    int    count;
	    Object stamp = null;
	    
	    try {
		if ( _engine._primKey.isPrimitive() )
		    count = 2;
		else
		    count = _engine._primKey.getJDOFields().length + 1;
		for ( int i = 0; i < _engine._loadFields.length ; ++i  ) {
		    _engine._loadFields[ i ].setValue( obj, _rs, i + count );
		}
		if ( _engine._stampField != null )
		    stamp = _rs.getString( _engine._loadFields.length + count );
	    } catch ( SQLException except ) {
		throw new PersistenceException( except );
	    }
	    return stamp;
	}

    }


}
