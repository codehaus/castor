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
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDesc;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.QueryException;
import org.exolab.castor.persist.DuplicateIdentityException;
import org.exolab.castor.persist.PersistenceException;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.ObjectModifiedException;
import org.exolab.castor.persist.ObjectDeletedException;
import org.exolab.castor.persist.QueryException;
import org.exolab.castor.persist.RelationContext;
import org.exolab.castor.mapping.FieldDesc;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.RelationDesc;
import org.exolab.castor.mapping.ContainerFieldDesc;


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


    private JDOClassDesc    _clsDesc;


    private FieldDesc       _identity;


    private boolean         _specifyKeyForCreate = true;


    private boolean         _useCursorForLock = false;


    private String          _stampField; // = "ctid";


    private String          _pkLookup;


    private String          _sqlCreate;


    private String          _sqlRemove;


    private String          _sqlStore;


    private String          _sqlLoad;


    private String          _sqlLoadLock;


    String          _sqlFinder;
    String          _sqlFinderJoin;


    private JDOFieldDesc[]   _loadFields;


    private RelationDesc[]   _loadRelations;


    private SQLRelated[]     _related;


    private SQLEngine        _extends;


    SQLEngine( JDOClassDesc clsDesc, PrintWriter logWriter )
        throws MappingException
    {
        if ( clsDesc == null )
            throw new IllegalArgumentException( "Argument 'clsDesc' is null" );
        _clsDesc = clsDesc;
        _identity = _clsDesc.getIdentity();
        if ( _identity == null )
            throw new MappingException( "Cannot persist a table that lacks a primary key descriptor" );

        buildCreateSql();
        buildRemoveSql();
        buildStoreSql();
        buildLoadSql();
        buildRelated( logWriter );
        if ( logWriter != null ) {
            logWriter.println( "SQL for " + _clsDesc.getJavaClass().getName() +
                               ": " + _sqlLoad );
            logWriter.println( "SQL for " + _clsDesc.getJavaClass().getName() +
                               ": " + _sqlCreate );
            logWriter.println( "SQL for " + _clsDesc.getJavaClass().getName() +
                               ": " + _sqlStore );
            logWriter.println( "SQL for " + _clsDesc.getJavaClass().getName() +
                               ": " + _sqlRemove );
        }
        if ( _clsDesc.getExtends() != null )
            _extends = new SQLEngine( (JDOClassDesc) _clsDesc.getExtends(), logWriter );
    }
    
    
    public JDOClassDesc getClassDesc()
    {
        return _clsDesc;
    }


    public PersistenceQuery createQuery( RelationContext rtx, String query, Class[] types )
        throws QueryException
    {
        return new SQLQuery( this, rtx, query, types );
    }


    public Object create( Object conn, Object obj, Object identity )
        throws DuplicateIdentityException, PersistenceException
    {
        PreparedStatement stmt;
        int               count;
        JDOFieldDesc[]    descs;
        
        stmt = null;
        try {
            if ( _extends != null ) 
                _extends.create( conn, obj, identity );

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
                if ( _identity instanceof ContainerFieldDesc ) {

                    descs = getJDOFields( (ContainerFieldDesc) _identity );
                    for ( int i = 0 ; i < descs.length ; ++i ) {
                        descs[ i ].getValue( identity, stmt, count + i );
                    }
                    count += descs.length;
                } else {
                    stmt.setObject( count, identity );
                    count += 1;
                }
            }
            
            descs = getJDOFields( _clsDesc );
            for ( int i = 0 ; i < descs.length ; ++i ) {
                Object value;

                value = descs[ i ].getValue( obj );
                if ( value != null )
                    stmt.setObject( count + i, value );
            }
            count += descs.length;

            RelationDesc[] rels;

            rels = _clsDesc.getRelations();
            for ( int i = 0 ; i < rels.length ; ++i ) {
                if ( ! rels[ i ].isAttached() ) {
                    FieldDesc idField;

                    idField = (JDOFieldDesc) rels[ i ].getRelatedClassDesc().getIdentity();
                    if ( idField instanceof ContainerFieldDesc ) {
                        Object relObj;
                        
                        relObj = rels[ i ].getRelationField().getValue( obj );
                        descs = getJDOFields( (ContainerFieldDesc) idField );
                        if ( relObj != null ) {
                            for ( int j = 0 ; j < descs.length ; ++j ) {
                                descs[ j ].getValue( relObj, stmt, count + j );
                            }
                        }
                        count += descs.length;
                    } else {
                        Object relObj;
                        
                        relObj = rels[ i ].getRelationField().getValue( obj );
                        if ( relObj != null )
                            ( (JDOFieldDesc) idField ).getValue( relObj, stmt, count );
                        count += 1;
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
                if ( _identity instanceof ContainerFieldDesc ) {
                    descs = getJDOFields( (ContainerFieldDesc) _identity );
                    for ( int i = 0 ; i < descs.length ; ++i )
                        descs[ i ].getValue( identity, stmt, i + 1 );
                } else {
                    stmt.setObject( 1, identity );
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

    public Object load( Object conn, RelationContext rtx, Object obj, Object identity, AccessMode accessMode )
        throws ObjectNotFoundException, PersistenceException
    {
        PreparedStatement stmt;
        ResultSet         rs;
        JDOFieldDesc[]    pkDescs = null;
        Object            pk;
        Object            stamp = null;
        boolean           lock;
        
        lock = ( accessMode == AccessMode.Exclusive );
        try {
            // [Oleg Nitz] SELECT FOR UPDATE requires cursor in some databases
            if ( _useCursorForLock )
                stmt = ( (Connection) conn ).prepareStatement( lock ? _sqlLoadLock : _sqlLoad,
                                                               ResultSet.TYPE_FORWARD_ONLY,
                                                               lock ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY );
            else
                stmt = ( (Connection) conn ).prepareStatement( lock ? _sqlLoadLock : _sqlLoad );
            
            if ( _identity instanceof ContainerFieldDesc ) {
                pkDescs = getJDOFields( (ContainerFieldDesc) _identity );
                for ( int i = 0 ; i < pkDescs.length ; ++i ) {
                    pkDescs[ i ].getValue( identity, stmt, i + 1 );
                }
            } else {
                stmt.setObject( 1, identity );
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
        int               count;
        Object            value;
        
        try {
            if ( _extends != null ) 
                _extends.store( conn, obj, identity, original, stamp );

            stmt = ( (Connection) conn ).prepareStatement( _sqlStore );
            
            descs = getJDOFields( _clsDesc );
            count = 1;
            for ( int i = 0 ; i < descs.length ; ++i ) {
                value = descs[ i ].getValue( obj );
                if ( value != null )
                    stmt.setObject( count + i, value );
            }
            count += descs.length;

            RelationDesc[] rels;

            rels = _clsDesc.getRelations();
            for ( int i = 0 ; i < rels.length ; ++i ) {
                if ( ! rels[ i ].isAttached() ) {
                    FieldDesc idField;

                    idField = (JDOFieldDesc) rels[ i ].getRelatedClassDesc().getIdentity();
                    if ( idField instanceof ContainerFieldDesc ) {
                        Object relObj;
                        
                        relObj = rels[ i ].getRelationField().getValue( obj );
                        descs = getJDOFields( (ContainerFieldDesc) idField );
                        if ( relObj != null ) {
                            for ( int j = 0 ; j < descs.length ; ++j )
                                descs[ j ].getValue( relObj, stmt, count + j );
                        }
                        count += descs.length;
                    } else {
                        Object relObj;
                        
                        relObj = rels[ i ].getRelationField().getValue( obj );
                        if ( relObj != null )
                            ( (JDOFieldDesc) idField ).getValue( relObj, stmt, count );
                        count += 1;
                    }
                }
            }
            
            if ( _identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) _identity );
                for ( int i = 0 ; i < descs.length ; ++i )
                    descs[ i ].getValue( identity, stmt, count + 1 );
            } else {   
                stmt.setObject( count, identity );
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
            stmt = ( (Connection) conn ).prepareStatement( _sqlRemove );
            if ( _identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) _identity );
                for ( int i = 0 ; i < descs.length ; ++i ) {
                    descs[ i ].getValue( identity, stmt, i + 1 );
                }
            } else {
                stmt.setObject( 1, identity );
            }
            // [Oleg] Good practice to execute a statement if it was created
            // in the first place :-)
            stmt.execute();
            stmt.close();

            if ( _extends != null ) 
                _extends.delete( conn, obj, identity );
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
            if ( _extends != null ) 
                _extends.writeLock( conn, obj, identity );

            // Only write locks are implemented by locking the row.
            // [Oleg Nitz] SELECT FOR UPDATE requires cursor in some databases
            if ( _useCursorForLock )
                stmt = ( (Connection) conn ).prepareStatement( _pkLookup, ResultSet.TYPE_FORWARD_ONLY,
                                                               ResultSet.CONCUR_UPDATABLE );
            else
                stmt = ( (Connection) conn ).prepareStatement( _pkLookup );
            
            if ( _identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) _identity );
                for ( int i = 0 ; i < descs.length ; ++i ) {
                    descs[ i ].getValue( identity, stmt, i + 1 );
                }
            } else {
                stmt.setObject( 1, identity );
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
        if ( _extends != null ) 
            _extends.changeIdentity( conn, obj, oldIdentity, newIdentity );
    }


    protected void buildRelated( PrintWriter logWriter )
        throws MappingException
    {
        RelationDesc[] rels;
        Vector         engines;
        
        engines = new Vector();
        rels = _clsDesc.getRelations();
        if ( rels != null ) {
            for ( int i = 0 ; i < rels.length ; ++i ) {
                /*
                if ( rels[ i ].isAttached() ) {
                    engines.addElement( new SQLRelated( related[ i ], _clsDesc,
                                                        related[ i ].getParentField(), logWriter ) );
                }
                */
            }
            _related = new SQLRelated[ engines.size() ];
            engines.copyInto( _related );
        }
    }


    protected void buildCreateSql()
    {
        StringBuffer   sql;
        JDOFieldDesc[] descs;
        RelationDesc[] rels;
        int            count;
        
        // Create statement to lookup primary key and determine
        // if object with same primary key already exists
        if ( _specifyKeyForCreate ) {
            sql = new StringBuffer( "SELECT 1 FROM " );
            sql.append( getSQLName( _clsDesc, true ) ).append( " WHERE " );
            if ( _identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) _identity );
                for ( int i = 0 ; i < descs.length ; ++i ) {
                    if ( i > 0 )
                        sql.append( " AND " );
                    sql.append( descs[ i ].getSQLName() );
                    sql.append( "=?" );
                }
            } else {
                sql.append( ( (JDOFieldDesc) _identity ).getSQLName() );
                sql.append( "=?" );
            }
            sql.append( " FOR UPDATE" );
            _pkLookup = sql.toString();
        }
        
        // Create statement to insert a new row into the table
        // using the specified primary key if one is required
        sql = new StringBuffer( "INSERT INTO " );
        sql.append( getSQLName( _clsDesc, false ) ).append( " (" );
        if ( _specifyKeyForCreate ) {
            if ( _identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) _identity );
                for ( int i = 0 ; i < descs.length ; ++i ) {
                    if ( i > 0 )
                        sql.append( ',' );
                    sql.append( descs[ i ].getSQLName() );
                }
                count = descs.length;
            } else {
                sql.append( ( (JDOFieldDesc) _identity ).getSQLName() );
                count = 1;
            }
        } else {
            count = 0;
        }
        
        descs = getJDOFields( _clsDesc );
        for ( int i = 0 ; i < descs.length ; ++i ) {
            if ( count > 0 )
                sql.append( ',' );
            sql.append( descs[ i ].getSQLName() );
            ++count;
        }

        rels = _clsDesc.getRelations();
        if ( rels != null ) {
            for ( int i = 0 ; i < rels.length ; ++i ) {
                if ( ! rels[ i ].isAttached() ) {
                    if ( count > 0 )
                        sql.append( ',' );
                    sql.append( ( (JDOFieldDesc) rels[ i ].getRelationField() ).getSQLName() );
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
        sql.append( getSQLName( _clsDesc, false ) ).append( " WHERE " );
        sql.append( buildWherePK() );
        _sqlRemove = sql.toString();
    }
    
    
    protected void buildStoreSql()
    {
        StringBuffer   sql;
        JDOFieldDesc[] descs;
        int            count;
        RelationDesc[] rels;
        
        sql = new StringBuffer( "UPDATE " );
        sql.append( getSQLName( _clsDesc, false ) ).append( " SET " );
        descs = getJDOFields( _clsDesc );
        for ( int i = 0 ; i < descs.length ; ++i ) {
            if ( i > 0 )
                sql.append( ',' );
            sql.append( descs[ i ].getSQLName() );
            sql.append( "=?" );
        }
        count = descs.length;

        rels = _clsDesc.getRelations();
        if ( rels != null ) {
            for ( int i = 0 ; i < rels.length ; ++i ) {
                if ( ! rels[ i ].isAttached() ) {
                    if ( count > 0 )
                        sql.append( ',' );
                    sql.append( ( (JDOFieldDesc) rels[ i ].getRelationField() ).getSQLName() );
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
        FieldDesc      identity;
        
        sql = new StringBuffer();
        if ( _identity instanceof ContainerFieldDesc ) {
            descs = getJDOFields( (ContainerFieldDesc) _identity );
            for ( i = 0 ; i < descs.length ; ++i ) {
                if ( i > 0 )
                    sql.append( " AND " );
                sql.append( descs[ i ].getSQLName() );
                sql.append( "=?" );
            }
        } else {
            sql.append( ( (JDOFieldDesc) _identity ).getSQLName() );
            sql.append( "=?" );
        }
        return sql;
    }


    protected void buildLoadSql()
        throws MappingException
    {
        Vector         loadFields;
        Vector         loadRelations;
        StringBuffer   sqlFields;
        StringBuffer   sqlFrom;
        StringBuffer   sqlJoin;
        
        loadFields = new Vector();
        loadRelations = new Vector();
        sqlFields = new StringBuffer( "SELECT " );
        sqlFrom = new StringBuffer( " FROM " );
        sqlJoin = new StringBuffer( " WHERE " );
        addLoadSql( _clsDesc, sqlFields, sqlFrom,
                    sqlJoin, loadFields, loadRelations, 0, false, true );
        if ( _stampField != null )
            sqlFields.append( ',' ).append( _stampField );
        
        _sqlLoad = sqlFields.append( sqlFrom ).append( sqlJoin ).toString();
        _sqlLoadLock = _sqlLoad + " FOR UPDATE";
        _loadFields = new JDOFieldDesc[ loadFields.size() ];
        loadFields.copyInto( _loadFields );
        _loadRelations = new RelationDesc[ loadRelations.size() ];
        loadRelations.copyInto( _loadRelations );
        
        sqlFields = new StringBuffer( "SELECT " );
        sqlFrom = new StringBuffer( " FROM " );
        sqlJoin = new StringBuffer( "" );
        addLoadSql( _clsDesc, sqlFields, sqlFrom,
                    sqlJoin, loadFields, loadRelations, 0, true, true );
        if ( _stampField != null )
            sqlFields.append( ',' ).append( _stampField );
        _sqlFinder = sqlFields.append( sqlFrom ).append( " WHERE " ).toString();
        _sqlFinderJoin = sqlJoin.toString();
    }


    private int addLoadSql( JDOClassDesc clsDesc, StringBuffer sqlFields,
                            StringBuffer sqlFrom, StringBuffer sqlJoin,
                            Vector loadFields, Vector loadRelations, int count,
                            boolean loadPk, boolean firstTable )
        throws MappingException
    {
        JDOFieldDesc[] descs;
        RelationDesc[] rels;
        JDOClassDesc   extend;
        FieldDesc      identity;
        
        identity = clsDesc.getIdentity();
        extend = (JDOClassDesc) clsDesc.getExtends();
        rels = clsDesc.getRelations();
        
        if ( ! firstTable )
            sqlFrom.append( ',' );
        sqlFrom.append( getSQLName( clsDesc, false ) );
        
        if ( ! loadPk ) {
            if ( identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) identity );
                for ( int i = 0 ; i < descs.length ; ++i ) {
                    if ( i > 0 )
                        sqlJoin.append( " AND " );
                    sqlJoin.append( getSQLName( clsDesc, descs[ i ] ) );
                    sqlJoin.append( "=?" );
                }
            } else {
                sqlJoin.append( getSQLName( clsDesc, (JDOFieldDesc) identity ) );
                sqlJoin.append( "=?" );
            }
        }
        
        if ( extend != null ) {
            if ( identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) identity );
                for ( int i = 0 ; i < descs.length ; ++i ) {
                    sqlJoin.append( " AND " );
                    sqlJoin.append( getSQLName( clsDesc, descs[ i ] ) );
                    sqlJoin.append( "=" );
                    sqlJoin.append( getSQLName( extend, descs[ i ] ) );
                }
            } else {
                sqlJoin.append( " AND " );
                sqlJoin.append( getSQLName( clsDesc, (JDOFieldDesc) identity ) );
                sqlJoin.append( "=" );
                sqlJoin.append( getSQLName( extend, (JDOFieldDesc) identity ) );
            }
        }
        
        if ( extend != null ) {
            count = addLoadSql( extend, sqlFields, sqlFrom,
                                sqlJoin, loadFields, loadRelations, count, true, false );
            loadPk = false;
        }

        if ( loadPk  ) {
            if ( identity instanceof ContainerFieldDesc ) {
                descs = getJDOFields( (ContainerFieldDesc) identity );
                for ( int i = 0 ; i < descs.length ; ++i ) {
                    if ( count > 0 )
                        sqlFields.append( ',' );
                    sqlFields.append( getSQLName( clsDesc, descs[ i ] ) );
                    loadFields.addElement( new JDOContainedFieldDesc( descs[ i ], (ContainerFieldDesc) identity, null ) );
                    ++count;
                }
            } else {
                if ( count > 0 )
                    sqlFields.append( ',' );
                sqlFields.append( getSQLName( clsDesc, (JDOFieldDesc) identity ) );
                loadFields.addElement( identity );
                ++count;
            }
        }
        
        descs = getJDOFields( clsDesc );
        for ( int i = 0 ; i < descs.length ; ++i ) {
            if ( count > 0 )
                sqlFields.append( ',' );
            sqlFields.append( getSQLName( clsDesc, descs[ i ] ) );
            loadFields.addElement( descs[ i ] );
            ++count;
        }

        rels = clsDesc.getRelations();
        for ( int i = 0 ; i < rels.length ; ++i ) {
            sqlJoin.append( " AND " );
            if ( rels[ i ].isAttached() )
                sqlJoin.append( getSQLName( clsDesc, (JDOFieldDesc) clsDesc.getIdentity() ) );
            else
                sqlJoin.append( getSQLName( clsDesc, (JDOFieldDesc) rels[ i ].getRelationField() ) );
            sqlJoin.append( '=' );
            if ( rels[ i ].isAttached() )
                sqlJoin.append( getSQLName( (JDOClassDesc) rels[ i ].getRelatedClassDesc(),
                                            ( (JDOFieldDesc) ( rels[ i ].getRelatedClassDesc().getIdentity() ) ) ) );
            else
                sqlJoin.append( getSQLName( (JDOClassDesc) rels[ i ].getRelatedClassDesc(),
                                            (JDOFieldDesc) rels[ i ].getRelatedClassDesc().getIdentity() ) );
        }
        
        for ( int i = 0 ; i < rels.length ; ++i ) {
            if ( ! rels[ i ].isAttached() ) {
                loadRelations.add( rels[ i ] );
                identity = rels[ i ].getRelatedClassDesc().getIdentity();
                if ( identity instanceof ContainerFieldDesc ) {
                    descs = getJDOFields( (ContainerFieldDesc) identity );
                    for ( i = 0 ; i < descs.length ; ++i ) {
                        if ( count > 0 )
                            sqlFields.append( ',' );
                        sqlFields.append( getSQLName( (JDOClassDesc) rels[ i ].getRelatedClassDesc(), descs[ i ] ) );
                        ++count;
                    }
                } else {
                    if ( count > 0 )
                        sqlFields.append( ',' );
                    sqlFields.append( getSQLName( (JDOClassDesc) rels[ i ].getRelatedClassDesc(), (JDOFieldDesc) identity ) );
                    ++count;
                }
            } else {
                count = addLoadSql( (JDOClassDesc) rels[ i ].getRelatedClassDesc(), sqlFields, sqlFrom,
                                    sqlJoin, loadFields, loadRelations, count, true, false );
            }
        }
        return count;
    }


    protected static String getSQLName( JDOClassDesc clsDesc, boolean lock )
    {
        return clsDesc.getTableName();
    }


    protected static String getSQLName( JDOClassDesc clsDesc, JDOFieldDesc field )
    {
        return clsDesc.getTableName() + "." + field.getSQLName();
    }


    private static JDOFieldDesc[] getJDOFields( ClassDesc clsDesc )
    {
        JDOFieldDesc[] jdoFields;
        FieldDesc[]    fields;

        fields = clsDesc.getFields();
        jdoFields = new JDOFieldDesc[ fields.length ];
        for ( int i = 0 ; i < fields.length ; ++i )
            jdoFields[ i ] = (JDOFieldDesc) fields[ i ];
        return jdoFields;
    }


    private static JDOFieldDesc[] getJDOFields( ContainerFieldDesc fieldDesc )
    {
        JDOFieldDesc[] jdoFields;
        FieldDesc[]    fields;

        fields = fieldDesc.getFields();
        jdoFields = new JDOFieldDesc[ fields.length ];
        for ( int i = 0 ; i < fields.length ; ++i )
            jdoFields[ i ] = (JDOFieldDesc) fields[ i ];
        return jdoFields;
    }


    static class SQLQuery
        implements PersistenceQuery
    {
        
        
        private ResultSet _rs;
        
        
        private SQLEngine _engine;
        
        
        private Class[]   _types;
        
        
        private Object[]  _values;
        
        
        private String    _sql;


        private Object    _lastIdentity;


        private RelationContext  _rtx;


        SQLQuery( SQLEngine engine, RelationContext rtx, String sql, Class[] types )
        {
            _engine = engine;
            _types = types;
            _values = new Object[ _types.length ];
            _sql = sql;
            _rtx = rtx;
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
            return _engine.getClassDesc().getJavaClass();
        }
        
        
        public void execute( Object conn, AccessMode accessMode )
            throws QueryException, PersistenceException
        {
            PreparedStatement stmt;

// XXX
System.out.println( _sql );
            _lastIdentity = null;
            try {
                stmt = ( (Connection) conn ).prepareStatement( _sql );
                for ( int i = 0 ; i < _values.length ; ++i )
                    stmt.setObject( i + 1, _values[ i ] );
                _rs = stmt.executeQuery();
            } catch ( SQLException except ) {
                throw new PersistenceException( except );
            }
        }
        
        
        public Object nextIdentity()
            throws PersistenceException
        {
            JDOFieldDesc[] pkDescs;
            
            try {
                if ( ! _rs.next() )
                    return null;
                if ( _engine._identity instanceof ContainerFieldDesc ) {
                    _lastIdentity = ( (ContainerFieldDesc) _engine._identity ).newInstance();
                    pkDescs = getJDOFields( (ContainerFieldDesc) _engine._identity );
                    for ( int i = 0 ; i < pkDescs.length ; ++i ) {
                        pkDescs[ i ].setValue( _lastIdentity, _rs, 1 + i );
                    }
                } else {
                    _lastIdentity = _rs.getObject( 1 );
                }
            } catch ( SQLException except ) {
                _lastIdentity = null;
                throw new PersistenceException( except );
            }
            return _lastIdentity;
        }
        
        
        public Object getIdentity( int index )
            throws PersistenceException
        {
            JDOFieldDesc[] pkDescs;
            
            try {
                _rs.absolute( index );
                if ( _rs.isLast() )
                    return null;
                if ( _engine._identity instanceof ContainerFieldDesc ) {
                    _lastIdentity = ( (ContainerFieldDesc) _engine._identity ).newInstance();
                    pkDescs = getJDOFields( (ContainerFieldDesc) _engine._identity );
                    for ( int i = 0 ; i < pkDescs.length ; ++i ) {
                        pkDescs[ i ].setValue( _lastIdentity, _rs, 1 + i );
                    }
                } else {
                    _lastIdentity = _rs.getObject( 1 );
                }
            } catch ( SQLException except ) {
                _lastIdentity = null;
                throw new PersistenceException( except );
            }
            return _lastIdentity;
        }


        public int getPosition()
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
            
            if ( _lastIdentity == null )
                throw new PersistenceException( "Internal error: fetch called without an identity returned from getIdentity/nextIdentity" );
            try {

                if ( _engine._identity instanceof ContainerFieldDesc ) {
                    JDOFieldDesc[] descs;

                    descs = getJDOFields( (ContainerFieldDesc) _engine._identity );
                    for ( int i = 0 ; i < descs.length ; ++i )
                        descs[ i ].setValue( obj, descs[ i ].getValue( _lastIdentity ) );
                    count = descs.length + 1;
                } else {
                    _engine._identity.setValue( obj, _lastIdentity );
                    count = 2;
                }

                for ( int i = 0; i < _engine._loadFields.length ; ++i  )
                    _engine._loadFields[ i ].setValue( obj, _rs, i + count );
                count += _engine._loadFields.length;
                for ( int i = 0; i < _engine._loadRelations.length ; ++i  ) {
                    Object rel;
                    Object relId;

                    relId = _rs.getObject( i + count );
                    if ( relId != null ) {
                        rel = _rtx.fetch( _engine._loadRelations[ i ].getRelatedClassDesc(), relId  );
                         _engine._loadRelations[ i ].getRelationField().setValue( obj, rel );
                    }
                }
                count += _engine._loadRelations.length;
                if ( _engine._stampField != null )
                    stamp = _rs.getString( _engine._loadFields.length + count );
            } catch ( SQLException except ) {
                throw new PersistenceException( except );
            }
            return stamp;
        }
        
    }


}
