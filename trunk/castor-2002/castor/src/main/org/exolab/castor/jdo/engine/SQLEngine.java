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
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.ClassHandler;
import org.exolab.castor.persist.RelationHandler;
import org.exolab.castor.persist.QueryException;
import org.exolab.castor.persist.DuplicateIdentityException;
import org.exolab.castor.persist.PersistenceException;
import org.exolab.castor.persist.ObjectNotFoundException;
import org.exolab.castor.persist.ObjectModifiedException;
import org.exolab.castor.persist.ObjectDeletedException;
import org.exolab.castor.persist.FetchContext;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.AccessMode;


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


    private boolean             _specifyKeyForCreate = true;


    private boolean             _useCursorForLock = false;


    private String          _stampField; // = "ctid";


    private String          _pkLookup;


    private String          _sqlCreate;


    private String          _sqlRemove;


    private String          _sqlStore;


    private String          _sqlLoad;


    private String          _sqlLoadLock;


    private QueryExpr       _sqlFinder;


    private ClassHandler      _handler;


    private FieldHandler[]    _loadFields;


    private RelationHandler[] _loadRels;


    private FieldHandler[]    _storeFields;


    private RelationHandler[] _storeRels;


    private SQLEngine         _extends;


    SQLEngine( ClassHandler handler, PrintWriter logWriter )
        throws MappingException
    {
        JDOClassDescriptor clsDesc;

        _handler = handler;
        if ( handler.getExtends() != null )
            _extends = new SQLEngine( handler.getExtends(), logWriter );
        clsDesc = (JDOClassDescriptor) _handler.getDescriptor();
        buildSql( clsDesc, logWriter );
        buildFinder( clsDesc, logWriter );
    }


    /**
     * Used by {@link OQLQuery} to retrieve the class descriptor.
     */
    JDOClassDescriptor getDescriptor()
    {
        return (JDOClassDescriptor) _handler.getDescriptor();
    }
    
    
    public PersistenceQuery createQuery( FetchContext ctx, String query, Class[] types )
        throws QueryException
    {
        return new SQLQuery( this, ctx, query, types );
    }


    public QueryExpr getFinder()
    {
        return (QueryExpr) _sqlFinder.clone();
    }


    public Object create( Object conn, Object obj, Object identity )
        throws DuplicateIdentityException, PersistenceException
    {
        PreparedStatement stmt;
        int               count;
        
        stmt = null;
        try {
            if ( _extends != null ) 
                _extends.create( conn, obj, identity );

            // If creation requires a primary key to be supplied, must check
            // that no such primary key exists in the table. This call will
            // also lock the table against creation of an object with such
            // a primary key.
            if ( _specifyKeyForCreate && identity == null )
                throw new PersistenceException( "persist.createWithoutIdentity" );
            
            // Must remember that SQL column index is base one
            count = 1;
            stmt = ( (Connection) conn ).prepareStatement( _sqlCreate );
            if ( _specifyKeyForCreate ) {
                stmt.setObject( count, identity );
                count += 1;
            }
            
            for ( int i = 0 ; i < _storeFields.length ; ++i ) {
                Object value;

                value = _storeFields[ i ].getValue( obj );
                if ( value != null )
                    stmt.setObject( count + i, value );
            }
            count += _storeFields.length;

            /*
            for ( int i = 0 ; i < _storeRels.length ; ++i ) {
                Object relIdentity;
                
                relIdentity = _storeRels[ i ].getIdentity( obj );
                if ( relIdentity != null )
                    stmt.setObject( count, relIdentity );
                count += 1;
            }
            */
            
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
                stmt.setObject( 1, identity );
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


    public Object store( Object conn, Object obj, Object identity,
                         Object original, Object stamp )
        throws ObjectModifiedException, ObjectDeletedException, PersistenceException
    {
        PreparedStatement stmt;
        int               count;
        
        try {
            if ( _extends != null ) 
                _extends.store( conn, obj, identity, original, stamp );

            count = 1;
            stmt = ( (Connection) conn ).prepareStatement( _sqlStore );

            for ( int i = 0 ; i < _storeFields.length ; ++i ) {
                Object value;

                value = _storeFields[ i ].getValue( obj );
                if ( value != null )
                    stmt.setObject( count , value );
                ++count;
            }
            count += _storeFields.length;

            /*
            for ( int i = 0 ; i < _storeRels.length ; ++i ) {
                Object relIdentity;
                
                relIdentity = _storeRels[ i ].getIdentity( obj );
                if ( relIdentity != null )
                    stmt.setObject( count, relIdentity );
                ++count;
            }
            */
            
            stmt.setObject( count, identity );

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


    public void delete( Object conn, Object identity )
        throws PersistenceException
    {
        PreparedStatement stmt;
        
        try {
            stmt = ( (Connection) conn ).prepareStatement( _sqlRemove );
            stmt.setObject( 1, identity );

            // [Oleg] Good practice to execute a statement if it was created
            // in the first place :-)
            stmt.execute();
            stmt.close();

            if ( _extends != null ) 
                _extends.delete( conn, identity );
        } catch ( SQLException except ) {
            throw new PersistenceException( except );
        }
    }


    public void writeLock( Object conn, Object obj, Object identity )
        throws ObjectDeletedException, PersistenceException
    {
        PreparedStatement stmt;
        
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

            stmt.setObject( 1, identity );

            if ( ! stmt.executeQuery().next() )
                throw new ObjectDeletedException( obj.getClass(), identity );
            stmt.close();
        } catch ( SQLException except ) {
            throw new PersistenceException( except );
        }
    }


    public Object load( Object conn, FetchContext ctx, Object obj, Object identity, AccessMode accessMode )
        throws ObjectNotFoundException, PersistenceException
    {
        PreparedStatement stmt;
        ResultSet         rs;
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
            
            stmt.setObject( 1, identity );
            
            rs = stmt.executeQuery();
            if ( ! rs.next() )
                throw new ObjectNotFoundException( obj.getClass(), identity );
            
            do {
                // First iteration for a PK: traverse all the fields
                for ( int i = 0; i < _loadFields.length ; ++i  ) {
                    // Usinging typed setValue so float/double, int/long
                    // can be intermixed with automatic conversion, something
                    // that throws an exception in the untyped version
                    _loadFields[ i ].setValue( obj, rs.getObject( i + 1 ) );
                }
            } while ( rs.next() );

            /*
            if ( _stampField != null )
                stamp = rs.getObject( _loadFields.length + 1 );
            */
            rs.close();
            stmt.close();
        } catch ( SQLException except ) {
            throw new PersistenceException( except );
        }
        return stamp;
    }


    public void changeIdentity( Object conn, Object obj, Object oldIdentity, Object newIdentity )
        throws DuplicateIdentityException, PersistenceException
    {
        if ( _extends != null ) 
            _extends.changeIdentity( conn, obj, oldIdentity, newIdentity );
    }


    protected void buildSql( JDOClassDescriptor clsDesc, PrintWriter logWriter )
    {
        StringBuffer         sql;
        JDOFieldDescriptor[] jdoFields;
        FieldDescriptor[]    fields;
        int                  count;
        String               wherePk;

        wherePk = ( (JDOFieldDescriptor) clsDesc.getIdentity() ).getSQLName() + "=?";
        sql = new StringBuffer( "SELECT 1 FROM " );
        sql.append( clsDesc.getTableName() ).append( " WHERE " );
        sql.append( wherePk ).append(  " FOR UPDATE" );
        _pkLookup = sql.toString();

        fields = clsDesc.getFields();
        jdoFields = new JDOFieldDescriptor[ fields.length ];
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor )
                jdoFields[ i ] = (JDOFieldDescriptor) fields[ i ];
        }
        
        // Create statement to insert a new row into the table
        // using the specified primary key if one is required
        sql = new StringBuffer( "INSERT INTO " );
        sql.append( clsDesc.getTableName() ).append( " (" );
        if ( _specifyKeyForCreate ) {
            sql.append( ( (JDOFieldDescriptor) clsDesc.getIdentity() ).getSQLName() );
            count = 1;
        } else
            count = 0;
        for ( int i = 0 ; i < jdoFields.length ; ++i ) {
            if ( jdoFields[ i ] != null ) {
                if ( count > 0 )
                    sql.append( ',' );
                sql.append( jdoFields[ i ].getSQLName() );
                ++count;
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
        if ( logWriter != null )
            logWriter.println( "SQL for creating " + clsDesc.getJavaClass().getName() +
                               ": " + _sqlCreate );

        
        sql = new StringBuffer( "DELETE FROM " ).append( clsDesc.getTableName() );
        sql.append( " WHERE " ).append( wherePk );
        _sqlRemove = sql.toString();
        if ( logWriter != null )
            logWriter.println( "SQL for deleting " + clsDesc.getJavaClass().getName() +
                               ": " + _sqlRemove );


        sql = new StringBuffer( "UPDATE " );
        sql.append( clsDesc.getTableName() ).append( " SET " );
        for ( int i = 0 ; i < jdoFields.length ; ++i ) {
            if ( jdoFields[ i ] != null ) {
                if ( i > 0 )
                    sql.append( ',' );
                sql.append( jdoFields[ i ].getSQLName() );
                sql.append( "=?" );
            }
        }
        sql.append( " WHERE " ).append( wherePk );
        _sqlStore = sql.toString();
        if ( logWriter != null )
            logWriter.println( "SQL for updating " + clsDesc.getJavaClass().getName() +
                               ": " + _sqlStore );

        Vector    storeFields;

        storeFields = new Vector();
        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor )
                storeFields.addElement( fields[ i ].getHandler() );
        }
        _storeFields = new FieldHandler[ storeFields.size() ];
        storeFields.copyInto( _storeFields );
    }
    
    
    protected void buildFinder( JDOClassDescriptor clsDesc, PrintWriter logWriter )
        throws MappingException
    {
        Vector    loadFields;
        Vector    loadRels;
        QueryExpr expr;

        loadFields = new Vector();
        loadRels = new Vector();
        expr = new QueryExpr();
        addLoadSql( clsDesc, expr, loadFields, loadRels, false, true );
        /*
          if ( _stampField != null )
            sqlFields.append( ',' ).append( _stampField );
        */
        
        _sqlLoad = expr.getQuery( false );
        _sqlLoadLock = expr.getQuery( true );
        _loadFields = new FieldHandler[ loadFields.size() ];
        loadFields.copyInto( _loadFields );
        _loadRels = new RelationHandler[ loadRels.size() ];
        loadRels.copyInto( _loadRels );
        if ( logWriter != null )
            logWriter.println( "SQL for loading " + clsDesc.getJavaClass().getName() +
                               ":  " + _sqlLoad );

        _sqlFinder = new QueryExpr();
        addLoadSql( clsDesc, _sqlFinder, loadFields, loadRels, true, false );
        /*
        if ( _stampField != null )
            sqlFields.append( ',' ).append( _stampField );
        */
    }


    private void addLoadSql( JDOClassDescriptor clsDesc, QueryExpr expr, Vector loadFields,
                             Vector loadRels, boolean loadPk, boolean queryPk )
        throws MappingException
    {
        FieldDescriptor[]    fields;
        JDOClassDescriptor   extend;
        FieldDescriptor      identity;
        
        identity = clsDesc.getIdentity();
        extend = (JDOClassDescriptor) clsDesc.getExtends();
        // rels = clsDesc.getRels();
        expr.addTable( clsDesc );
        
        if ( extend != null ) {
            expr.addJoin( clsDesc, (JDOFieldDescriptor) identity, extend, (JDOFieldDescriptor) identity );
            addLoadSql( extend, expr, loadFields, loadRels, true, queryPk );
            loadPk = false;
            queryPk = false;
        }
        
        if ( loadPk  ) {
            expr.addColumn( clsDesc, (JDOFieldDescriptor) identity );
            // XXX How do we handle ProductInventory?
        }
        
        if ( queryPk )
            expr.addParameter( clsDesc, (JDOFieldDescriptor) identity, null );
        
        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor ) {
                expr.addColumn( clsDesc, (JDOFieldDescriptor) fields[ i ] );
                loadFields.addElement( fields[ i ].getHandler() );
            }
        }
        
        /*
          rels = clsDesc.getRelations();
          for ( int i = 0 ; i < rels.length ; ++i ) {
          if ( rels[ i ].isAttached() ) {
          if ( rels[ i ].isMany() ) {
          expr.addJoin( clsDesc, (JDOFieldDesc) rels[ i ].getRelationField(),
          (JDOClassDesc) rels[ i ].getRelatedClass(), 
          (JDOFieldDesc) rels[ i ].getRelatedClass().getIdentity() );
          } else {
          expr.addJoin( clsDesc, (JDOFieldDesc) clsDesc.getIdentity(),
          (JDOClassDesc) rels[ i ].getRelatedClass(), 
          ( (JDOFieldDesc) ( rels[ i ].getRelatedClass().getIdentity() ) ) );
          }
          }
          }
          
          for ( int i = 0 ; i < rels.length ; ++i ) {
          if ( ! rels[ i ].isAttached() ) {
          loadRels.addElement( rels[ i ] );
          identity = rels[ i ].getRelatedClass().getIdentity();
          expr.addRelationColumn( clsDesc, (JDOFieldDesc) rels[ i ].getRelationField() );
          } else {
          addLoadSql( asContained( (JDOClassDesc) rels[ i ].getRelatedClass(),
          rels[ i ].getRelationField() ),
          expr, loadFields, loadRels, false, false );
          }
          }
        */
    }
        

        /*
          private static JDOClassDesc asContained( JDOClassDesc clsDesc, FieldDescriptor refField )
          throws MappingException
          {
          FieldDesc   identity;
          FieldDesc[] fields;
          FieldDesc[] cFields;
          
          identity = clsDesc.getIdentity();
          identity = new JDOContainedFieldDesc( (JDOFieldDesc) identity, refField );
          fields = clsDesc.getFields();
          cFields = new FieldDesc[ fields.length ];
          for ( int i = 0 ; i < fields.length ; ++i )
          cFields[ i ] = new JDOContainedFieldDesc( (JDOFieldDesc) fields[ i ], refField );
          return new JDOClassDesc( new SimpleClassDesc( clsDesc.getJavaClass(), cFields,
          clsDesc.getRelations(), identity, clsDesc.getExtends(),
          clsDesc.getHandler().getAccessMode() ), clsDesc.getTableName() );
          }
        */


    public String toString()
    {
        return getDescriptor().toString();
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


        private FetchContext  _ctx;


        SQLQuery( SQLEngine engine, FetchContext ctx, String sql, Class[] types )
        {
            _engine = engine;
            _types = types;
            _values = new Object[ _types.length ];
            _sql = sql;
            _ctx = ctx;
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
            return _engine._handler.getJavaClass();
        }
        
        
        public void execute( Object conn, AccessMode accessMode )
            throws QueryException, PersistenceException
        {
            PreparedStatement stmt;

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
            try {
                if ( ! _rs.next() )
                    return null;
                _lastIdentity = _rs.getObject( 1 );
            } catch ( SQLException except ) {
                _lastIdentity = null;
                throw new PersistenceException( except );
            }
            return _lastIdentity;
        }
        
        
        public Object getIdentity( int index )
            throws PersistenceException
        {
            try {
                _rs.absolute( index );
                if ( _rs.isLast() )
                    return null;
                _lastIdentity = _rs.getObject( 1 );
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
                throw new PersistenceException( "persist.fetchWithoutIdentity" );
            try {
                // Set the identity field and skip that many columns in the result set.
                _engine._handler.setIdentity( obj, _lastIdentity );
                count = 2;

                // Load all the fields of the object including one-one relations
                for ( int i = 0; i < _engine._loadFields.length ; ++i  )
                    _engine._loadFields[ i ].setValue( obj, _rs.getObject( i + count ) );
                count += _engine._loadFields.length;

                // Load all the many-one relations of the object
                for ( int i = 0; i < _engine._loadRels.length ; ++i  ) {
                    Object rel;
                    Object relId;

                    relId = _rs.getObject( i + count );
                    if ( relId != null ) {
                        if ( _engine._loadRels[ i ].isMulti() ) {
                            // XXX Need to deal with multiple relations
                        } else {
                            /* Don't use fetch context at this point
                            rel = _ctx.fetch( _engine._loadRels[ i ].getRelatedClass(), relId );
                            _engine._loadRels[ i ].setRelated( obj, rel );
                            */
                        }
                    }
                }
                count += _engine._loadRels.length;

                /*
                if ( _engine._stampField != null )
                    stamp = _rs.getString( _engine._loadFields.length + count );
                */
            } catch ( SQLException except ) {
                throw new PersistenceException( except );
            }
            return stamp;
        }
        
    }


}
