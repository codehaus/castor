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
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.loader.IndirectFieldHandler;


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


    private boolean           _specifyKeyForCreate = true;


    private boolean           _useCursorForLock = false;


    private String            _stampField; // = "ctid";


    private String            _pkLookup;


    private String            _sqlCreate;


    private String            _sqlRemove;


    private String            _sqlStore;


    private String            _sqlLoad;


    private String            _sqlLoadLock;


    private QueryExpr         _sqlFinder;


    private ClassHandler      _handler;


    private FieldInfo[]       _fields;


    private SQLEngine         _extends;


    private PrintWriter       _logWriter;


    SQLEngine( ClassHandler handler, PrintWriter logWriter )
        throws MappingException
    {
        JDOClassDescriptor clsDesc;

        _handler = handler;
        if ( handler.getExtends() != null )
            _extends = new SQLEngine( handler.getExtends(), logWriter );
        clsDesc = (JDOClassDescriptor) _handler.getDescriptor();
        buildSql( clsDesc, logWriter );
        buildFinder( handler, logWriter );
        _logWriter = logWriter;
    }


    /**
     * Used by {@link OQLQuery} to retrieve the class descriptor.
     */
    JDOClassDescriptor getDescriptor()
    {
        return (JDOClassDescriptor) _handler.getDescriptor();
    }
    
    
    public PersistenceQuery createQuery( String query, Class[] types )
        throws QueryException
    {
        return new SQLQuery( this, query, types );
    }


    public QueryExpr getFinder()
    {
        return (QueryExpr) _sqlFinder.clone();
    }


    public Object create( Object conn, Object[] fields, Object identity )
        throws DuplicateIdentityException, PersistenceException
    {
        PreparedStatement stmt;
        int               count;
        
        stmt = null;
        try {
            if ( _extends != null ) 
                _extends.create( conn, fields, identity );

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

            for ( int i = 0 ; i < _fields.length ; ++i ) {
                if ( _fields[ i ].store ) {
                    stmt.setObject( count, fields[ i ] );
                    ++count;
                }
            }

            stmt.executeUpdate();
            stmt.close();
            return null;
        } catch ( SQLException except ) {
            // [oleg] Check for duplicate key based on X/Open error code
            if ( except.getSQLState() != null &&
                 except.getSQLState().startsWith( "23" ) )
                throw new DuplicateIdentityException( getDescriptor().getJavaClass(), identity );
            
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
                    throw new DuplicateIdentityException( getDescriptor().getJavaClass(), identity );
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


    public Object store( Object conn, Object[] fields, Object identity,
                         Object[] original, Object stamp )
        throws ObjectModifiedException, ObjectDeletedException, PersistenceException
    {
        PreparedStatement stmt;
        int               count;
        
        try {
            if ( _extends != null ) 
                _extends.store( conn, fields, identity, original, stamp );

            count = 1;
            stmt = ( (Connection) conn ).prepareStatement( _sqlStore );

            for ( int i = 0 ; i < _fields.length ; ++i ) {
                if ( _fields[ i ].store ) {
                    stmt.setObject( count, fields[ i ] );
                    ++count;
                }
            }

            stmt.setObject( count, identity );

            if ( stmt.executeUpdate() == 0 ) {
                // If no update was performed, the object has been previously
                // removed from persistent storage. Complain about this.
                stmt.close();
                throw new ObjectDeletedException( getDescriptor().getJavaClass(), identity );
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


    public void writeLock( Object conn, Object identity )
        throws ObjectDeletedException, PersistenceException
    {
        PreparedStatement stmt;
        
        try {
            if ( _extends != null ) 
                _extends.writeLock( conn, identity );

            // Only write locks are implemented by locking the row.
            // [Oleg Nitz] SELECT FOR UPDATE requires cursor in some databases
            if ( _useCursorForLock )
                stmt = ( (Connection) conn ).prepareStatement( _pkLookup, ResultSet.TYPE_FORWARD_ONLY,
                                                               ResultSet.CONCUR_UPDATABLE );
            else
                stmt = ( (Connection) conn ).prepareStatement( _pkLookup );

            stmt.setObject( 1, identity );

            if ( ! stmt.executeQuery().next() )
                throw new ObjectDeletedException( getDescriptor().getJavaClass(), identity );
            stmt.close();
        } catch ( SQLException except ) {
            throw new PersistenceException( except );
        }
    }


    public Object load( Object conn, Object[] fields, Object identity, AccessMode accessMode )
        throws ObjectNotFoundException, PersistenceException
    {
        PreparedStatement stmt;
        ResultSet         rs;
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
                throw new ObjectNotFoundException( getDescriptor().getJavaClass(), identity );
            
            do {
                for ( int i = 0; i < fields.length ; ++i  ) {
                    fields[ i ] = rs.getObject( i + 1 );
                }
            } while ( rs.next() );
            rs.close();
            stmt.close();
        } catch ( SQLException except ) {
            throw new PersistenceException( except );
        }
        return stamp;
    }


    public void changeIdentity( Object conn, Object oldIdentity, Object newIdentity )
        throws DuplicateIdentityException, PersistenceException
    {
        if ( _extends != null ) 
            _extends.changeIdentity( conn, oldIdentity, newIdentity );
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
        count = 0;
        for ( int i = 0 ; i < jdoFields.length ; ++i ) {
            if ( jdoFields[ i ] != null ) {
                if ( count > 0 )
                    sql.append( ',' );
                sql.append( jdoFields[ i ].getSQLName() );
                sql.append( "=?" );
                ++count;
            }
        }
        sql.append( " WHERE " ).append( wherePk );
        _sqlStore = sql.toString();
        if ( logWriter != null )
            logWriter.println( "SQL for updating " + clsDesc.getJavaClass().getName() +
                               ": " + _sqlStore );
    }
    
    
    protected void buildFinder( ClassHandler handler, PrintWriter logWriter )
        throws MappingException
    {
        Vector    fields;
        QueryExpr expr;

        fields = new Vector();
        expr = new QueryExpr();
        addLoadSql( handler, expr, fields, false, true, true );
        
        _sqlLoad = expr.getQuery( false );
        _sqlLoadLock = expr.getQuery( true );
        _fields = new FieldInfo[ fields.size() ];
        fields.copyInto( _fields );
        if ( logWriter != null )
            logWriter.println( "SQL for loading " + handler.getJavaClass().getName() +
                               ":  " + _sqlLoad );

        _sqlFinder = new QueryExpr();
        addLoadSql( handler, _sqlFinder, fields, true, false, true );
    }


    private void addLoadSql( ClassHandler handler, QueryExpr expr, Vector allFields,
                             boolean loadPk, boolean queryPk, boolean store )
        throws MappingException
    {
        JDOClassDescriptor   clsDesc;
        FieldDescriptor[]    fields;
        JDOClassDescriptor   extend;
        FieldDescriptor      identity;
        
        clsDesc = (JDOClassDescriptor) handler.getDescriptor();
        identity = clsDesc.getIdentity();
        expr.addTable( clsDesc );
        
        // If this class extends another class, create a join with the parent table and
        // add the load fields of the parent class (but not the store fields)
        if ( handler.getExtends() != null ) {
            expr.addJoin( clsDesc, (JDOFieldDescriptor) identity,
                          (JDOClassDescriptor) clsDesc.getExtends(), (JDOFieldDescriptor) identity );
            addLoadSql( handler.getExtends(), expr, allFields, true, queryPk, false );
            loadPk = false;
            queryPk = false;
        }
        
        if ( loadPk  )
            expr.addColumn( clsDesc, (JDOFieldDescriptor) identity );
        
        if ( queryPk )
            expr.addParameter( clsDesc, (JDOFieldDescriptor) identity, null );
        
        fields = clsDesc.getFields();
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor ) {
                expr.addColumn( clsDesc, (JDOFieldDescriptor) fields[ i ] );
                allFields.addElement( new FieldInfo( fields[ i ], store ) );
            } else {
                JDOClassDescriptor relDesc;

                relDesc = (JDOClassDescriptor) fields[ i ].getClassDescriptor();
                expr.addTable( relDesc );
                expr.addColumn( relDesc, (JDOFieldDescriptor) relDesc.getIdentity() );
                expr.addJoin( clsDesc.getTableName(), ( (JDOFieldDescriptor) identity ).getSQLName(),
                              relDesc.getTableName(), "prod_id" );
                // XXX                                ^^^
                allFields.addElement( new FieldInfo( fields[ i ], false ) );
            }
        }
    }
        

    public String toString()
    {
        return getDescriptor().toString();
    }


    static class FieldInfo
    {

        final boolean store;

        final String  name;

        FieldInfo( FieldDescriptor fieldDesc, boolean store )
        {
            this.name = fieldDesc.getFieldName();
            this.store = store;
        }

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
            if ( _engine._logWriter != null )
                _engine._logWriter.println( _sql );
            try {
                stmt = ( (Connection) conn ).prepareStatement( _sql );
                for ( int i = 0 ; i < _values.length ; ++i )
                    stmt.setObject( i + 1, _values[ i ] );
                _rs = stmt.executeQuery();
            } catch ( SQLException except ) {
                throw new PersistenceException( except );
            }
        }
        
        
        public Object nextIdentity( Object identity )
            throws PersistenceException
        {
            try {
                if ( _lastIdentity == null ) {
                    if ( ! _rs.next() )
                        return null;
                    _lastIdentity = _rs.getObject( 1 );
                    return _lastIdentity;
                }

                while ( _lastIdentity.equals( identity ) ) {
                    if ( ! _rs.next() ) {
                        _lastIdentity = null;
                        return null;
                    }
                    _lastIdentity = _rs.getObject( 1 );
                }
                return _lastIdentity;
            } catch ( SQLException except ) {
                _lastIdentity = null;
                throw new PersistenceException( except );
            }
        }
        
        
        public boolean isForwardOnly()
        {
            return true;
        }
        
        
        public Object fetch( Object[] fields, Object identity )
            throws ObjectNotFoundException, PersistenceException
        {
            int    count;
            Object stamp = null;
            
            try {
                // Set the identity field and skip that many columns in the result set.
                /*
                _engine._handler.setIdentity( obj, identity );
                */
                count = 2;

                // Load all the fields of the object including one-one relations
                for ( int i = 0 ; i < fields.length ; ++i  )
                    fields[ i ] = _rs.getObject( i + count );

                if ( _rs.next() ) {
                    _lastIdentity = _rs.getObject( 1 );
                    while ( identity.equals( _lastIdentity ) ) {
                        for ( int i = 0; i < fields.length ; ++i  )
                            fields[ i ] = _rs.getObject( i + count );
                        if ( _rs.next() )
                            _lastIdentity = _rs.getObject( 1 );
                        else
                            _lastIdentity = null;
                    }
                    _lastIdentity = null;
                }
            } catch ( SQLException except ) {
                throw new PersistenceException( except );
            }
            return stamp;
        }
        
    }


}
