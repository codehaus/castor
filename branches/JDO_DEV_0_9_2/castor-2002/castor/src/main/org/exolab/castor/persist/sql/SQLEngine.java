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
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.persist.sql;


import java.util.Vector;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.engine.JDOClassDescriptor;
import org.exolab.castor.jdo.engine.JDOFieldDescriptor;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.TypeConvertor;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.mapping.loader.FieldHandlerImpl;
import org.exolab.castor.mapping.loader.FieldDescriptorImpl;
import org.exolab.castor.mapping.loader.ClassDescriptorImpl;
//import org.exolab.castor.persist.ClassMolder;
import org.exolab.castor.persist.AccessMode;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.LogInterceptor;
import org.exolab.castor.persist.OID;
import org.exolab.castor.persist.Entity;
import org.exolab.castor.persist.EntityInfo;
import org.exolab.castor.persist.EntityFieldInfo;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.persist.types.Complex;
import org.exolab.castor.persist.types.SQLTypes;
import org.exolab.castor.util.Logger;
import org.exolab.castor.util.Messages;


/**
 * The SQL engine performs persistence of one object type against one
 * SQL database. It can only persist simple objects and extended
 * relationships. An SQL engine is created for each object type
 * represented by a database. When persisting, it requires a physical
 * connection that maps to the SQL database and the transaction
 * running on that database, through the {@link ConnectionProvider}
 * interface.
 *
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @author <a href="yip@intalio.com">Thomas Yip</a>
 *
 * @version $Revision$ $Date$
 */
public final class SQLEngine implements Persistence {

    private final static boolean ID_TYPE = true;


    private final static boolean FIELD_TYPE = false;


    private String              _pkLookup;


    private String              _sqlCreate;


    private String              _sqlRemove;


    private String              _sqlStore;


    private String              _sqlStoreDirty;


    private String              _sqlLoad;


    private String              _sqlLoadLock;


    private EntityInfo          _entityInfo;


    private FieldInfo[]         _fields;


    private ColumnInfo[]         _ids;


    private SQLEngine           _extends;


    private QueryExpression     _sqlFinder;


    private PersistenceFactory  _factory;


    private String              _stampField;


    private String              _type;


    private String              _mapTo;


    private String              _extTable;


    private LogInterceptor       _logInterceptor;


    private JDOClassDescriptor   _clsDesc;


    private KeyGenerator         _keyGen;


    //private ClassMolder          _mold;




    SQLEngine( /*JDOClassDescriptor clsDesc*/ EntityInfo entityInfo,
               LogInterceptor logInterceptor, PersistenceFactory factory, String stampField )
        throws MappingException {

        _clsDesc = null; //clsDesc;
        _entityInfo = entityInfo;
        _stampField = stampField;
        _factory = factory;
        _logInterceptor = logInterceptor;
        _keyGen = null;
        _type = _clsDesc.getJavaClass().getName();
        _mapTo = _clsDesc.getTableName();

        if ( _clsDesc.getExtends() == null ) {
            KeyGeneratorDescriptor keyGenDesc = _clsDesc.getKeyGeneratorDescriptor();
            if ( keyGenDesc != null ) {
                int[] tempType = ( (JDOFieldDescriptor) _clsDesc.getIdentity() ).getSQLType();
                _keyGen = keyGenDesc.getKeyGeneratorRegistry().getKeyGenerator(
                        _factory, keyGenDesc, tempType==null? 0: tempType[0],
                        _logInterceptor );
            }
        }

        // construct field and id info
        Vector idsInfo = new Vector();
        Vector fieldsInfo = new Vector();

        /*
         * Implementation Note:
         * Extends and Depends has some special mutual exclusive
         * properties, which implementator should aware of.
         *
         * A Depended class may depends on another depended class
         * A class should either extends or depends on other class
         * A class should not depend on extending class.
         *  because, it is the same as depends on the base class
         * A class may be depended by zero or more classes
         * A class may be extended by zero or more classes
         * A class may extends only zero or one class
         * A class may depends only zero or one class
         * A class may depend on extended class
         * A class may extend a dependent class.
         * A class may extend a depended class.
         * No loop or circle should exist
         *
         * In other word,
         *
         */
        // then, we put depended class ids in the back
        JDOClassDescriptor base = _clsDesc;

        // make sure there is no forbidded cases
		/*
        while ( base.getDepends() != null ) {
            if ( base.getExtends() != null )
                throw new MappingException("Class should not both depends on and extended other classes");

            base = (JDOClassDescriptor)base.getDepends();
            if ( base.getExtends() != null )
                throw new MappingException("Class should not depends on an extended class");
            // do we need to add loop detection?
        }*/

        // walk until the base class which this class extends
        base = _clsDesc;
        Stack stack = new Stack();
        stack.push( base );
        while ( base.getExtends() != null ) {
            //if ( base.getDepends() != null )
            //    throw new MappingException("Class should not both depends on and extended other classes");
            base = (JDOClassDescriptor) base.getExtends();
            stack.push( base );
            // do we need to add loop detection?
        }
        if ( base != _clsDesc ) {
            _extTable = base.getTableName();
        }

        // now base is either the base of extended class, or
        // clsDesc
        // we always put the original id info in front
        // [oleg] except for SQL name, it may differ.
        JDOClassDescriptor jdoBase = (JDOClassDescriptor) base;
        FieldDescriptor[] baseIdDescriptors = base.getIdentities();
        FieldDescriptor[] idDescriptors = _clsDesc.getIdentities();

        for ( int i=0; i < baseIdDescriptors.length; i++ ) {
            if ( baseIdDescriptors[i] instanceof JDOFieldDescriptor ) {
                String name = baseIdDescriptors[i].getFieldName();
                String[] sqlName = ((JDOFieldDescriptor) baseIdDescriptors[i]).getSQLName();
                int[] sqlType = ((JDOFieldDescriptor) baseIdDescriptors[i]).getSQLType();
                FieldHandlerImpl fh = (FieldHandlerImpl) baseIdDescriptors[i].getHandler();

                // The extending class may have other SQL names for identity fields
                for (int j = 0; j < idDescriptors.length; j++) {
                    if (name.equals(idDescriptors[j].getFieldName()) &&
                            (idDescriptors[j] instanceof JDOFieldDescriptor)) {
                        sqlName = ((JDOFieldDescriptor) idDescriptors[j]).getSQLName();
                        break;
                    }
                }
                idsInfo.add( new ColumnInfo( sqlName[0], sqlType[0], fh.getConvertTo(), fh.getConvertFrom(), fh.getConvertParam() ) );
            } else
                throw new MappingException("Except JDOFieldDescriptor");
        }


        // if class or base class depend on other class,
        // depended class ids will be part of this ids and
        // will be added in the back. We don't need to take
        // care depended class which is depends on other class.
        // ClassMolder will take care of it.
        /*
        idDescriptors = null;
        if ( clsDesc.getDepends()!= null ) {
            idDescriptors = ((ClassDescriptorImpl)jdoBase.getDepends()).getIdentities();
            for ( int i=0; i<idDescriptors.length; i++ ) {
                if ( idDescriptors[i] instanceof JDOFieldDescriptor ) {
                    String name = ((JDOFieldDescriptor)idDescriptors[i]).getSQLName();
                    int type = ((JDOFieldDescriptor)idDescriptors[i]).getSQLType();
                    FieldHandlerImpl fh = (FieldHandlerImpl)idDescriptors[i].getHandler();
                    idsInfo.add( new ColumnInfo( name, type, fh.getConvertTo(), fh.getConvertFrom(), fh.getConvertParam() ) );
                } else
                    throw new MappingException("Except JDOFieldDescriptor");
            }
        } */

        // then do the fields
        boolean extendField = true;
        while ( !stack.empty() ) {
            base = (JDOClassDescriptor)stack.pop();
            FieldDescriptor[] fieldDescriptors = base.getFields();
            for ( int i=0; i<fieldDescriptors.length; i++ ) {
                if ( stack.empty() ) {
                    fieldsInfo.add( new FieldInfo( _clsDesc, fieldDescriptors[i], _clsDesc.getTableName(), !extendField ) );
                } else {
                    fieldsInfo.add( new FieldInfo( _clsDesc, fieldDescriptors[i], base.getTableName(), extendField ) );
                }
            }
        }

        _ids = new ColumnInfo[idsInfo.size()];
        idsInfo.copyInto( _ids );

        _fields = new FieldInfo[fieldsInfo.size()];
        fieldsInfo.copyInto( _fields );

        try {
            buildSql();
            buildFinder( _clsDesc );
        } catch ( QueryException except ) {
            except.printStackTrace();
            throw new MappingException( except );
        }
    }

    /*
	public Persistence.FieldInfo[] getInfo() {
		return _fields;
	}*/

    /**
     * Mutator method for setting extends SQLEngine
     * @param engine
     */
    public void setExtends( SQLEngine engine ) {
        _extends = engine;
    }

    /**
     * Used by {@link OQLQuery} to retrieve the class descriptor.
     */
    public JDOClassDescriptor getDescriptor()
    {
        return _clsDesc;
    }


    /**
     * Used by ParseTreeWalker to quote names in WHERE clause
     */
    public String quoteName( String name )
    {
        return _factory.quoteName( name );
    }


    public PersistenceQuery createQuery( QueryExpression query, Class[] types, AccessMode accessMode )
        throws QueryException
    {
        String sql;

        //if ( accessMode == null )
        //    accessMode = _clsDesc.getAccessMode();
        sql = query.getStatement( accessMode == AccessMode.DbLocked);
        if ( _logInterceptor != null )
            _logInterceptor.queryStatement( sql );
        return new SQLQuery( this, sql, types );
    }


    public PersistenceQuery createCall( String spCall, Class[] types )
    {
        FieldDescriptor[] fields;
        String[] jdoFields0;
        String[] jdoFields;
        int[] sqlTypes0;
        int[] sqlTypes;
        int count;

        if ( _logInterceptor != null )
            _logInterceptor.queryStatement( spCall );

        fields = _clsDesc.getFields();
        jdoFields0 = new String[ fields.length + 1 ];
        sqlTypes0 = new int[ fields.length + 1 ];
        // the first field is the identity

        // | need some work here
        count = 1;
        jdoFields0[ 0 ] = _clsDesc.getIdentity().getFieldName();
        sqlTypes0[ 0 ] = ( (JDOFieldDescriptor) _clsDesc.getIdentity() ).getSQLType()[0];
        for ( int i = 0 ; i < fields.length ; ++i ) {
            if ( fields[ i ] instanceof JDOFieldDescriptor ) {
                jdoFields0[ count ] = ((JDOFieldDescriptor) fields[ i ]).getSQLName()[0];
                sqlTypes0[ count ] = ((JDOFieldDescriptor) fields[ i ]).getSQLType()[0];
                ++count;
            }
        }
        jdoFields = new String[ count ];
        sqlTypes = new int[ count ];
        System.arraycopy( jdoFields0, 0, jdoFields, 0, count );
        System.arraycopy( sqlTypes0, 0, sqlTypes, 0, count );
        return ((BaseFactory) _factory).getCallQuery( spCall, types, _clsDesc.getJavaClass(), jdoFields, sqlTypes );
    }

    public QueryExpression getQueryExpression() {
        return _factory.getQueryExpression();
    }

    public QueryExpression getFinder()
    {
        return (QueryExpression) _sqlFinder.clone();
    }

	private Object idToSQL( int index, Object object )
			throws PersistenceException {

        if ( object == null || _ids[index].convertFrom == null )
			return object;
		return _ids[index].convertFrom.convert( object, _ids[index].convertParam );
	}

    private Object toSQL( int field, int column, Object object )
            throws PersistenceException {

        ColumnInfo col = _fields[field].columns[column];
        if ( object == null || col.convertFrom == null )
            return object;
        return col.convertFrom.convert( object, col.convertParam );
    }

	private Object idToJava( int index, Object object )
			throws PersistenceException {

		if ( object == null || _ids[index].convertTo == null )
			return object;
		return _ids[index].convertTo.convert( object, _ids[index].convertParam );
	}

    private Object toJava( int field, int column, Object object )
            throws PersistenceException {

        ColumnInfo col = _fields[field].columns[column];
        if ( object == null || col.convertTo == null )
            return object;
        return col.convertTo.convert( object, col.convertParam );
    }

    /**
     * Use the specified keygenerator to gengerate a key for this
     * row of object.
     *
     * Result key will be in java type.
     */
    private Object generateKey( Object conn ) throws PersistenceException {
        Object identity;

        identity = _keyGen.generateKey( (Connection) conn, _clsDesc.getTableName(),
                _ids[0].name, null );

        if ( identity == null )
            throw new PersistenceException( Messages.format("persist.noIdentity", _clsDesc.getJavaClass().getName()) );

        return idToJava( 0, identity );
    }


    /**
     * Creates a new object in persistence storage. Called for an
     * object that was created during the transaction when the identity
     * of that object is known. Creates a new record in persistence
     * storage. Must detect an attempt to create an object with the
     * same identity and must retain a lock on the object after creation.
     * If the identity is null, an identity might be created and returned
     * by this method.
     *
     * @param tx The transaction context
     * @param conn An open connection
     * @param entity The entity to create
     * @throws DuplicateIdentityException An object with the same
     *   identity already exists in persistent storage
     * @throws PersistenceException A persistence error occured
     */
    public void create( TransactionContext tx, Object conn, Entity entity )
        throws DuplicateIdentityException, PersistenceException {

        PreparedStatement stmt = null;
        int               count;
        Object            sqlId;
        Object[]          fields = null;

        if ( _extends == null && _keyGen == null && entity.identity == null )
            throw new PersistenceException( Messages.format("persist.noIdentity", _clsDesc.getJavaClass().getName()) );

        fields = extractFields(entity);
        try {
            // Must create record in the parent table first.
            // All other dependents are created afterwards.
            if ( _extends != null ) {
                // | quick and very dirty hack to try to make multiple class on the same table work
                if ( !_extends._mapTo.equals( _mapTo ) ) {
                    _extends.create( tx, conn, entity );
                }
            }

            // Generate key before INSERT
            else if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT )
                entity.identity = generateKey( conn );   // genKey return identity in JDO type


            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.DURING_INSERT )
                stmt = ( (Connection) conn ).prepareCall( _sqlCreate );
            else
                stmt = ( (Connection) conn ).prepareStatement( _sqlCreate );

            // Must remember that SQL column index is base one
            count = 1;
            if ( _keyGen == null || _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                if ( _ids.length > 1 && !(entity.identity instanceof Complex) )
                    throw new PersistenceException( "Multiple identities expected!" );

                if ( entity.identity instanceof Complex ) {
                    Complex id = (Complex) entity.identity;
                    if ( id.size() != _ids.length || _ids.length <= 1 )
                        throw new PersistenceException( "Size of complex field mismatched!");

                    for ( int i=0; i<_ids.length; i++ )
                        stmt.setObject( count++, idToSQL( i, id.get(i) ) );

                } else {
                    if ( _ids.length != 1 )
                        throw new PersistenceException( "Complex field expected!" );

                    stmt.setObject( count++, idToSQL( 0, entity.identity ) );
                }
            }

            for ( int i = 0 ; i < _fields.length ; ++i ) {
                if ( _fields[ i ].store ) {
                    if ( fields[i] == null ) {
                        for ( int j=0; j < _fields[i].columns.length; j++ )
                            stmt.setNull( count++, _fields[i].columns[j].sqlType );

                    } else if ( fields[i] instanceof Complex ) {
                        Complex complex = (Complex)fields[i];
                        if ( complex.size() != _fields[i].columns.length )
                            throw new PersistenceException( "Size of complex field mismatch!" );

                        for ( int j=0; j<_fields[i].columns.length; j++ ) {
                            Object value = ( complex == null ? null : complex.get(j) );
                            SQLTypes.setObject( stmt, count++, toSQL( i, j, value), _fields[i].columns[j].sqlType );
                        }
                    } else {
                        if ( _fields[i].columns.length != 1 )
                            throw new PersistenceException( "Complex field expected! ");

                        SQLTypes.setObject( stmt, count++, toSQL( i, 0, fields[i]), _fields[i].columns[0].sqlType );
                    }
                }
            }

            // Generate key during INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.DURING_INSERT ) {
                CallableStatement cstmt = (CallableStatement) stmt;
                int sqlType;

                sqlType = _ids[0].sqlType;
                cstmt.registerOutParameter( count, sqlType );
                cstmt.execute();

                // First skip all results "for maximum portability"
                // as proposed in CallableStatement javadocs.
                while ( cstmt.getMoreResults() || cstmt.getUpdateCount() != -1 );

                // Identity is returned in the last parameter
                // Workaround: for INTEGER type in Oracle getObject returns BigDecimal
                if ( sqlType == java.sql.Types.INTEGER )
                    entity.identity = new Integer( cstmt.getInt( count ) );
                else
                    entity.identity = cstmt.getObject( count );
                entity.identity = idToJava( 0, entity.identity );
            } else
                stmt.executeUpdate();

            stmt.close();

            // Generate key after INSERT
            if ( _keyGen != null && _keyGen.getStyle() == KeyGenerator.AFTER_INSERT ) {
                entity.identity = generateKey( conn );
            }
        } catch ( SQLException except ) {
            // [oleg] Check for duplicate key based on X/Open error code
            // Bad way: all validation exceptions are reported as DuplicateKey
            //if ( except.getSQLState() != null &&
            //     except.getSQLState().startsWith( "23" ) )
            //    throw new DuplicateIdentityException( _clsDesc.getJavaClass(), entity.identity );

            // Good way: let PersistenceFactory try to determine
            Boolean isDupKey;

            isDupKey = _factory.isDuplicateKeyException( except );
            if ( Boolean.TRUE.equals( isDupKey ) ) {
                throw new DuplicateIdentityException( Messages.format("persist.duplicateIdentity", _clsDesc.getJavaClass().getName(), entity.identity ) );
            } else if ( Boolean.FALSE.equals( isDupKey ) ) {
                throw new PersistenceException( Messages.format("persist.nested", except), except );
            }
            // else unknown, let's check directly.

            // [oleg] Check for duplicate key the old fashioned way,
            //        after the INSERT failed to prevent race conditions
            //        and optimize INSERT times
            try {
                // Close the insert statement
                if ( stmt != null )
                    stmt.close();

                stmt = ( (Connection) conn ).prepareStatement( _pkLookup );

                // bind the identity to the preparedStatement
                count = 1;
                if ( entity.identity instanceof Complex ) {
                    Complex id = (Complex) entity.identity;
                    if ( id.size() != _ids.length || _ids.length <= 1 )
                        throw new PersistenceException( "Size of complex field mismatched!");

                    for ( int i=0; i<_ids.length; i++ )
                        stmt.setObject( count++, idToSQL( i, id.get(i) ) );

                } else {
                    if ( _ids.length != 1 )
                        throw new PersistenceException( "Complex field expected!" );

                    stmt.setObject( count++, idToSQL( 0, entity.identity ) );
                }

                if ( stmt.executeQuery().next() ) {
                    stmt.close();
                    throw new DuplicateIdentityException( Messages.format("persist.duplicateIdentity", _clsDesc.getJavaClass().getName(), entity.identity ) );
                }
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
            throw new PersistenceException( Messages.format("persist.nested", except), except );
        }
    }


    /**
     * if isNull, replace next "=?" with " IS NULL",
     * otherwise skip next "=?",
     * move "pos" to the left.
     */
    private int nextParameter(boolean isNull, StringBuffer sb, int pos) {
        for ( ; pos > 0; pos--) {
            if (sb.charAt(pos - 1) == '=' && sb.charAt(pos) == '?') {
                break;
            }
        }
        if (pos > 0) {
            pos--;
            if (isNull) {
                sb.delete(pos, pos + 2);
                sb.insert(pos, " IS NULL");
            }
        }
        return pos;
    }

    /**
     * If the RDBMS doesn't support setNull for "WHERE fld=?" and requires "WHERE fld IS NULL",
     * we need to modify the statement.
     */
    private String getStoreStatement( Object[] origFields ) throws PersistenceException {
        StringBuffer sb = null;
        int pos = 0;

        if (origFields == null) {
            return _sqlStore;
        }
        if (((BaseFactory) _factory).supportsSetNullInWhere()) {
            return _sqlStoreDirty;
        }
        pos = _sqlStoreDirty.length() - 1;
        sb = new StringBuffer(pos * 4);
        sb.append(_sqlStoreDirty);
        for (int i = _fields.length - 1; i >= 0; i--) {
            if (_fields[i].store && _fields[i].dirtyCheck) {
                if (origFields[i] == null) {
                    for (int j = _fields[i].columns.length - 1; j >= 0; j--) {
                        pos = nextParameter(true, sb, pos);
                    }
                } else if ( origFields[i] instanceof Complex ) {
                    Complex complex = (Complex) origFields[i];
                    if ( complex.size() != _fields[i].columns.length )
                        throw new PersistenceException( "Size of complex field mismatch!" );

                    for (int j = _fields[i].columns.length - 1; j >= 0; j--) {
                        pos = nextParameter((complex.get(j) == null), sb, pos);
                    }
                } else {
                    if (_fields[i].columns.length != 1)
                        throw new PersistenceException( "Complex field expected! ");

                    pos = nextParameter(false, sb, pos);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Stores the object in persistent storage, given the object fields
     * and its identity. The object has been loaded before or has been
     * created through a call to {@link #create}. This method should
     * detect whether the object has been modified in persistent storage
     * since it was loaded. After this method returns all locks on the
     * object must be retained until the transaction has completed.
     * This method may return a new stamp to track further updates to
     * the object.
     * <p>
     * If the object was not retrieved for exclusive access, this
     * method will be asked to perform dirty checking prior to storing
     * the object. The <tt>original</tt> argument will contains the
     * object's original fields as retrieved in the transaction, and
     * <tt>stamp</tt> the object's stamp returned from a successful
     * call to {@link #load}. These arguments are null for objects
     * retrieved with an exclusive lock.
     *
     * @param tx The transaction context
     * @param conn An open connection
     * @param entity The entity to store
     * @param original The original entity, or null
     * @throws ObjectModifiedException The object has been modified
     *  in persistence storage since it was last loaded
     * @throws ObjectDeletedException Indicates the object has been
     *  deleted from persistence storage
     * @throws PersistenceException A persistence error occured
     */
    public void store( TransactionContext tx, Object conn, Entity entity, Entity original )
        throws ObjectModifiedException, ObjectDeletedException, PersistenceException {

        PreparedStatement stmt = null;
        int               count;
        Object[]          fields;
        Object[]          origFields;

        fields = extractFields(entity);
        origFields = extractFields(original);
        try {
            // Must store record in parent table first.
            // All other dependents are stored independently.
            if ( _extends != null ) {
                // | quick and very dirty hack to try to make multiple class on the same table work
                if ( !_extends._mapTo.equals( _mapTo ) ) {
                    _extends.store( tx, conn, entity, original );
                }
            }
            stmt = ( (Connection) conn ).prepareStatement( getStoreStatement( origFields ) );

            count = 1;

            // bind fields of the row to be stored into the preparedStatement
            for ( int i = 0 ; i < _fields.length ; ++i ) {
                if ( _fields[ i ].store ) {
                    if ( fields[i] == null ) {
                        for ( int j=0; j < _fields[i].columns.length; j++ )
                            stmt.setNull( count++, _fields[i].columns[j].sqlType );

                    } else if ( fields[i] instanceof Complex ) {
                        Complex complex = (Complex) fields[i];
                        if ( complex.size() != _fields[i].columns.length )
                            throw new PersistenceException( "Size of complex field mismatch!" );

                        for ( int j=0; j<_fields[i].columns.length; j++ ) {
                            SQLTypes.setObject( stmt, count++, toSQL( i, j, complex.get(j)), _fields[i].columns[j].sqlType );
                        }
                    } else {
                        if ( _fields[i].columns.length != 1 )
                            throw new PersistenceException( "Complex field expected! ");

                        SQLTypes.setObject( stmt, count++, toSQL( i, 0, fields[i]), _fields[i].columns[0].sqlType );
                    }
                }
            }

            // bind the identity of the row to be stored into the preparedStatement
            if ( entity.identity instanceof Complex ) {
                Complex id = (Complex) entity.identity;
                if ( id.size() != _ids.length || _ids.length <= 1 )
                    throw new PersistenceException( "Size of complex field mismatched!");

                for ( int i=0; i<_ids.length; i++ )
                    stmt.setObject( count++, idToSQL( i, id.get(i) ) );

            } else {
                if ( _ids.length != 1 )
                    throw new PersistenceException( "Complex field expected!" );

                stmt.setObject( count++, idToSQL( 0, entity.identity ) );
            }

            // bind the old fields of the row to be stored into the preparedStatement
            if ( original != null ) {
                boolean supportsSetNull = ((BaseFactory) _factory).supportsSetNullInWhere();

                for ( int i = 0 ; i < _fields.length ; ++i ) {
                    if ( _fields[ i ].store && _fields[i].dirtyCheck ) {
                        if ( origFields[i] == null ) {
                            if (supportsSetNull) {
                                for ( int j=0; j < _fields[i].columns.length; j++ )
                                    stmt.setNull( count++, _fields[i].columns[j].sqlType );
                            }
                        } else if ( origFields[i] instanceof Complex ) {
                            Complex complex = (Complex) origFields[i];
                            if ( complex.size() != _fields[i].columns.length )
                                throw new PersistenceException( "Size of complex field mismatch!" );

                            for ( int j=0; j<_fields[i].columns.length; j++ ) {
                                SQLTypes.setObject( stmt, count++, toSQL( i, j, complex.get(j)), _fields[i].columns[j].sqlType );
                            }
                        } else {
                            if ( _fields[i].columns.length != 1 )
                                throw new PersistenceException( "Complex field expected! ");

                            SQLTypes.setObject( stmt, count++, toSQL( i, 0, origFields[i]), _fields[i].columns[0].sqlType );
                        }
                    }
                }
            }

            if ( stmt.executeUpdate() <= 0 ) { // SAP DB returns -1 here
                // If no update was performed, the object has been previously
                // removed from persistent storage or has been modified if
                // dirty checking. Determine which is which.
                stmt.close();
                if ( original != null ) {
                    stmt = ( (Connection) conn ).prepareStatement( _sqlLoad ); //_pkLookup

                    // bind the identity to the prepareStatement
                    count = 1;
                    if ( entity.identity instanceof Complex ) {
                        Complex id = (Complex) entity.identity;
                        for ( int i=0; i<_ids.length; i++ )
                            stmt.setObject( count++, idToSQL( i, id.get(i) ) );

                    } else {
                        stmt.setObject( count++, idToSQL( 0, entity.identity ) );
                    }

                    ResultSet res = stmt.executeQuery();
                    int c = res.getMetaData().getColumnCount();
                    if ( res.next() ) {

                        stmt.close();
                        throw new ObjectModifiedException( Messages.format("persist.objectModified", _clsDesc.getJavaClass().getName(), entity.identity ) );
                    }
                    stmt.close();
                }

                throw new ObjectDeletedException( Messages.format("persist.objectDeleted", _clsDesc.getJavaClass().getName(), entity.identity) );
            }
            stmt.close();
        } catch ( SQLException except ) {
            try {
                // Close the insert/select statement
                if ( stmt != null )
                    stmt.close();
            } catch ( SQLException except2 ) { }
            throw new PersistenceException( Messages.format("persist.nested", except), except );
        }
    }


    /**
     * Deletes the object from persistent storage, given the object'
     * identity. The object has been loaded before or has been created
     * through a call to {@link #create}. After this method returns all
     * locks on the object must be retained until the transaction has
     * completed.
     *
     * @param tx The transaction context
     * @param conn An open connection
     * @param entity The entity to delete
     * @throws PersistenceException A persistence error occured
     */
    public void delete( TransactionContext tx, Object conn, Entity entity )
        throws PersistenceException {

        PreparedStatement stmt = null;

        try {
            stmt = ( (Connection) conn ).prepareStatement( _sqlRemove );
            int count = 1;
            // bind the identity of the preparedStatement
            if ( entity.identity instanceof Complex ) {
                Complex id = (Complex) entity.identity;
                if ( id.size() != _ids.length || _ids.length <= 1 )
                    throw new PersistenceException( "Size of complex field mismatched!");

                for ( int i=0; i<_ids.length; i++ )
                    stmt.setObject( count++, idToSQL( i, id.get(i) ) );

            } else {
                if ( _ids.length != 1 )
                    throw new PersistenceException( "Complex field expected!" );
                stmt.setObject( count++, idToSQL( 0, entity.identity ) );
            }

            int result = stmt.executeUpdate();
            if ( result < 1 )
                throw new PersistenceException("Object to be deleted does not exist! "+ entity.identity );

            stmt.close();

            // Must delete record in parent table last.
            // All other dependents have been deleted before.
            if ( _extends != null )
                _extends.delete( tx, conn, entity );
        } catch ( SQLException except ) {
            try {
                // Close the insert/select statement
                if ( stmt != null )
                    stmt.close();
            } catch ( SQLException except2 ) { }
            throw new PersistenceException( Messages.format("persist.nested", except), except );
        }
    }


    /**
     * Obtains a write lock on the object. This method is called in
     * order to lock the object and prevent concurrent access from
     * other transactions. The object is known to have been loaded
     * before either in this or another transaction. This method is
     * used to assure that updates or deletion of the object will
     * succeed when the transaction completes, without attempting to
     * reload the object.
     *
     * @param tx The transaction context
     * @param conn An open connection
     * @param entity The entity to lock
     * @throws ObjectDeletedException Indicates the object has been
     *  deleted from persistence storage
     * @throws PersistenceException A persistence error occured
     */
    public void writeLock( TransactionContext tx, Object conn, Entity entity )
        throws ObjectDeletedException, PersistenceException {

        PreparedStatement stmt = null;
        try {
            // Must obtain lock on record in parent table first.
            if ( _extends != null )
                _extends.writeLock( tx, conn, entity );

            stmt = ( (Connection) conn ).prepareStatement( _pkLookup );

            int count = 1;
            // bind the identity of the preparedStatement
            if ( entity.identity instanceof Complex ) {
                Complex id = (Complex) entity.identity;
                if ( id.size() != _ids.length || _ids.length <= 1 )
                    throw new PersistenceException( "Size of complex field mismatched!");

                for ( int i=0; i<_ids.length; i++ )
                    stmt.setObject( count++, idToSQL( i, id.get(i) ) );

            } else {
                if ( _ids.length != 1 )
                    throw new PersistenceException( "Complex field expected!" );

                stmt.setObject( count++, idToSQL( 0, entity.identity ) );
            }

            // If no query was performed, the object has been previously
            // removed from persistent storage. Complain about this.
            if ( ! stmt.executeQuery().next() )
                throw new ObjectDeletedException( Messages.format("persist.objectDeleted", _clsDesc.getJavaClass().getName(), entity.identity ) );
            stmt.close();
        } catch ( SQLException except ) {
            try {
                // Close the insert/select statement
                if ( stmt != null )
                    stmt.close();
            } catch ( SQLException except2 ) { }
            throw new PersistenceException( Messages.format("persist.nested", except), except );
        }
    }


    /**
     * Loads the object from persistence storage. This method will load
     * the object fields from persistence storage based on the object's
     * identity. This method may return a stamp which can be used at a
     * later point to determine whether the copy of the object in
     * persistence storage is newer than the cached copy (see {@link
     * #store}). If <tt>lock</tt> is true the object must be
     * locked in persistence storage to prevent concurrent updates.
     *
     * @param tx The transaction context
     * @param conn An open connection
     * @param entity The entity to load into
     * @param accessMode The access mode (null equals shared)
     * @return The object's stamp, or null
     * @throws ObjectNotFoundException The object was not found in
     *   persistent storage
     * @throws PersistenceException A persistence error occured
     */
    public void load( TransactionContext tx, Object conn, Entity entity, AccessMode accessMode )
        throws ObjectNotFoundException, PersistenceException {

        PreparedStatement stmt;
        ResultSet         rs;
        Object            stamp = null;
        boolean           notNull;
        Object[]          fields;

        fields = extractFields(entity);
        try {
            stmt = ( (Connection) conn ).prepareStatement( ( accessMode == AccessMode.DbLocked ) ? _sqlLoadLock : _sqlLoad );
            int count = 1;
            // bind the identity of the preparedStatement
            if ( entity.identity instanceof Complex ) {
                Complex id = (Complex) entity.identity;
                if ( id.size() != _ids.length || _ids.length <= 1 )
                    throw new PersistenceException( "Size of complex field mismatched! expected: "+_ids.length+" found: "+id.size() );

                for ( int i=0; i<_ids.length; i++ )
                    stmt.setObject( count++, idToSQL( i, id.get(i) ) );

            } else {
                if ( _ids.length != 1 )
                    throw new PersistenceException( "Complex field expected!" );
                stmt.setObject( count++, idToSQL( 0, entity.identity ) );
            }

            // query the object
            rs = stmt.executeQuery();
            if ( ! rs.next() )
                throw new ObjectNotFoundException( Messages.format("persist.objectNotFound", _clsDesc.getJavaClass().getName(), entity.identity) );

            // Load all the fields of the object including one-one relations
            count = 1;
            Object[] temp = new Object[10]; // assume complex field max at 10
            for ( int i = 0 ; i < _fields.length ; ++i  ) {
                if ( !_fields[i].load )
                    continue;

                if ( !_fields[i].multi ) {
                    notNull = false;
                    if ( _fields[i].columns.length == 1 ) {
                        Object value = toJava( i, 0, SQLTypes.getObject( rs, count++, _fields[i].columns[0].sqlType ) );
                        if ( !rs.wasNull() )
                            fields[i] = value;
                        else
                            fields[i] = null;
                    } else {
                    for ( int j=0; j<_fields[i].columns.length; j++ ) {
                            Object value = toJava( i, j, SQLTypes.getObject( rs, count++, _fields[i].columns[j].sqlType ) );
                        if ( !rs.wasNull() ) {
                            temp[j] = value;
                            notNull = true;
                            } else {
                                temp[j] = null;
                            }
                        }
                        if ( notNull )
                            fields[i] = new Complex( _fields[i].columns.length, temp );
                        else
                            fields[i] = null;
                    }
                } else {
                    ArrayList res = new ArrayList();
                    notNull = false;
                    for ( int j=0; j<_fields[i].columns.length; j++ ) {
                        Object value = toJava( i, j, SQLTypes.getObject( rs, count, _fields[i].columns[j].sqlType ) );
                        if ( !rs.wasNull() ) {
                            temp[j] = value;
                            notNull = true;
                        } else {
                                temp[j] = null;
                        }
                        count++;
                    }
                    if ( notNull ) {
                        if ( _fields[i].columns.length == 1 )
                            res.add( temp[0] );
                        else
                            res.add( new Complex( _fields[i].columns.length, temp ) );
                    }
                    fields[i] = res;
                }
            }

            while ( rs.next() ) {
                count = 1;
                for ( int i = 0; i < _fields.length ; ++i  ) {
                    if ( !_fields[i].load )
                        continue;

                    if ( _fields[i].multi ) {
                        ArrayList res = (ArrayList)fields[i];
                        notNull = false;
                        for ( int j=0; j<_fields[i].columns.length; j++ ) {
                            Object value = toJava( i, j, SQLTypes.getObject( rs, count, _fields[i].columns[j].sqlType ) );
                            if ( !rs.wasNull() ) {
                                temp[j] = value;
                                notNull = true;
                            } else {
                                temp[j] = null;
                            }
                            count++;
                        }
                        if ( notNull ) {
                            if ( _fields[i].columns.length == 1 ) {
                                if ( !res.contains( temp[0] ) )
                                    res.add( temp[0] );
                            } else {
                                Complex com = new Complex( _fields[i].columns.length, temp );
                                if ( !res.contains( com ) )
                                    res.add( new Complex( _fields[i].columns.length, temp ) );
                            }
                        }
                    } else {
                        count += _fields[i].columns.length;
                    }
                }
            }
            rs.close();
            stmt.close();

        } catch ( SQLException except ) {
            except.printStackTrace();
            throw new PersistenceException( Messages.format("persist.nested", except), except );
        }
        entity.objectStamp = stamp;
    }

    /**
     * Loads all the identities of entity in which the specified field match
     * the supplied value. Conceptually, the specified field is a foreign key
     * field; the supplied values is the value the foreign key.
     *
     * @param tx The transaction context
     * @param conn An open connection
     * @param field The field on the "many" side of the relation
     * @param value The value of the field
     * @param entityIds The list of loaded identities that should be filled in the method
     * @param accessMode The access mode (null equals shared)
     * @throws PersistenceException A persistence error occured
     */
    public void loadRelated( TransactionContext tx, Object conn, EntityFieldInfo field, Object value, List entityIds, AccessMode accessMode )
            throws PersistenceException {
    }

    private void buildSql() throws QueryException {

        StringBuffer         sql;
        JDOFieldDescriptor[] jdoFields;
        FieldDescriptor[]    fields;
        int                  count;
        QueryExpression      query;
        String               wherePK;
        String               primKeyName;
        String               tableName;
        boolean              keyGened = false;


        tableName = _mapTo;
        query = _factory.getQueryExpression();

        // initalize lookup query
        for ( int i=0; i<_ids.length; i++ ) {
            query.addParameter( tableName, _ids[i].name, QueryExpression.OpEquals );
        }
        _pkLookup = query.getStatement( true );

        // create sql statements
        StringBuffer sb = new StringBuffer();
        sb.append( JDBCSyntax.Where );
        for ( int i=0; i<_ids.length; i++ ) {
            if ( i > 0 ) sb.append( " AND " );
            sb.append( _factory.quoteName( _ids[i].name ) );
            sb.append( QueryExpression.OpEquals );
            sb.append( JDBCSyntax.Parameter );
        }
        wherePK = sb.toString();

        // Create statement to insert a new row into the table
        // using the specified primary key if one is required
        sql = new StringBuffer( "INSERT INTO " );
        sql.append( _factory.quoteName( tableName ) ).append( " (" );
        count = 0;
        for ( int i=0; i<_ids.length; i++ ) {
            if ( _keyGen == null || _keyGen.getStyle() == KeyGenerator.BEFORE_INSERT ) {
                if ( count > 0 ) sql.append( ',' );
                keyGened = true;
                sql.append( _factory.quoteName( _ids[i].name ) );
                ++count;
            }
        }
        for ( int i = 0 ; i < _fields.length; ++i ) {
            if ( _fields[i].store ) {
                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    if ( count > 0 )
                        sql.append( ',' );
                    sql.append( _factory.quoteName( _fields[i].columns[j].name ) );
                    ++count;
                }
            }
        }
        // it is possible to have no fields in INSERT statement:
        // only the primary key field in the table,
        // with KeyGenerator DURING_INSERT or BEFORE_INSERT
        if ( count == 0 )
            sql.setLength( sql.length() - 2 ); // cut " ("
        else
            sql.append( ")" );
        sql.append( " VALUES (" );
        for ( int i = 0 ; i < count; ++i ) {
            if ( i > 0 )
                sql.append( ',' );
            sql.append( '?' );
        }
        sql.append( ')' );
        _sqlCreate = sql.toString();

        if ( ! keyGened ) {
            try {
                _sqlCreate = _keyGen.patchSQL( _sqlCreate, _ids[0].name /*primKeyName*/ );
            } catch ( MappingException except )  {
                _logInterceptor.exception( except );
                // proceed without this stupid key generator
                _keyGen = null;
                buildSql();
                return;
            }
            if ( _keyGen.getStyle() == KeyGenerator.DURING_INSERT )
                _sqlCreate = "{call " + _sqlCreate + "}";
        }
        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for creating " + _type + ": " + _sqlCreate );


        sql = new StringBuffer( "DELETE FROM " ).append( _factory.quoteName( tableName ) );
        sql.append( wherePK );
        _sqlRemove = sql.toString();
        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for deleting " + _type + ": " + _sqlRemove );

        sql = new StringBuffer( "UPDATE " );
        sql.append( _factory.quoteName( _mapTo ) );
        sql.append( " SET " );
        count = 0;
        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[ i ].store ) {
                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    if ( count > 0 )
                        sql.append( ',' );
                    sql.append( _factory.quoteName( _fields[i].columns[j].name ) );
                    sql.append( "=?" );
                    ++count;
                }
            }
        }
        sql.append( wherePK );
        _sqlStore = sql.toString();

        for ( int i = 0 ; i < _fields.length ; ++i ) {
            if ( _fields[i].store && _fields[i].dirtyCheck ) {
                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    sql.append( " AND " );
                    sql.append( _factory.quoteName( _fields[i].columns[j].name ) );
                    sql.append( "=?" );
                }
            }
        }
        _sqlStoreDirty = sql.toString();
        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for updating " + _type + ": " + _sqlStoreDirty );
    }


    private void buildFinder( JDOClassDescriptor clsDesc ) throws MappingException, QueryException {
        Vector          fields;
        QueryExpression expr;
        QueryExpression find;

        fields = new Vector();
        expr = _factory.getQueryExpression();
        find = _factory.getQueryExpression();
        //addLoadSql( expr, fields, false, true, true );

        //_fields = new FieldInfo[ fields.size() ];
        //fields.copyInto( _fields );

        // get id columns' names
        String[] idnames = new String[_ids.length];
        for ( int i=0; i<_ids.length; i++ ) {
            idnames[i] = _ids[i].name;
            expr.addParameter( _mapTo, _ids[i].name, QueryExpression.OpEquals );
        }

        // join all the extended table
        JDOClassDescriptor curDesc = clsDesc;
        JDOClassDescriptor baseDesc;
        String[] curIds = idnames;
        String[] baseIds;
        while ( curDesc.getExtends() != null ) {
            baseDesc = (JDOClassDescriptor) curDesc.getExtends();
            baseIds = new String[_ids.length];
            for ( int i=0; i<_ids.length; i++ ) {
                baseIds[i] = ((JDOFieldDescriptor) (baseDesc.getIdentities()[i])).getSQLName()[0];
            }
            expr.addInnerJoin( curDesc.getTableName(), curIds, baseDesc.getTableName(), baseIds );
            find.addInnerJoin( curDesc.getTableName(), curIds, baseDesc.getTableName(), baseIds );
            curDesc = baseDesc;
            curIds = baseIds;
        }
        for ( int i=0; i<_ids.length; i++ ) {
            find.addColumn( _mapTo, idnames[i] );
        }

        // join all the related/depended table
        Vector joinTables = new Vector();
        for ( int i=0; i<_fields.length; i++ ) {
            if ( _fields[i].load ) {
                if ( _fields[i].joined /*&& !joinTables.contains( _fields[i].tableName )*/ ) {
                    int offset = 0;
                    String[] rightCol = _fields[i].joinFields;
                    String[] leftCol = new String[_ids.length-offset];
                    for ( int j=0; j<leftCol.length; j++ ) {
                        leftCol[j] = _ids[j+offset].name;
                    }
                    expr.addOuterJoin( _mapTo, leftCol, _fields[i].tableName, rightCol );
                    find.addOuterJoin( _mapTo, leftCol, _fields[i].tableName, rightCol );
                    joinTables.add( _fields[i].tableName );
                }

                for ( int j=0; j<_fields[i].columns.length; j++ ) {
                    expr.addColumn( _fields[i].tableName, _fields[i].columns[j].name );
                    find.addColumn( _fields[i].tableName, _fields[i].columns[j].name );
                }
            }
        }
        _sqlLoad = expr.getStatement( false );
        _sqlLoadLock = expr.getStatement( true );

        _sqlFinder = find;

        if ( _logInterceptor != null )
            _logInterceptor.storeStatement( "SQL for loading " + _type + ":  " + _sqlLoad );

    }


    private void addLoadSql( QueryExpression expr, Vector allFields,
            boolean loadPk, boolean queryPk, boolean store )
            throws MappingException {
    }


    private Object[] extractFields(Entity entity) throws PersistenceException {
        if (entity == null) {
            return null;
        }
        for (int i = 0; i < entity.entityClasses.length; i++) {
            if (entity.entityClasses[i].equals(_entityInfo)) {
                return entity.fields[i];
            }
        }
        throw new PersistenceException( "Entity of a wrong class passed" );
    }

    public String toString() {
        return _clsDesc.toString();
    }

    static final class FieldInfo /*implements Persistence.FieldInfo*/ {

        final String  tableName;

        final String  jdoName;

        final boolean load;

        final boolean store;

        final boolean multi;

        final boolean joined;

        final boolean dirtyCheck;

        final String[] joinFields;
        //!TY fix this. joinFields should be in FieldInfo, not ColumnInfo

        //final boolean foreign;

        ColumnInfo[] columns;

        FieldInfo( JDOClassDescriptor clsDesc, FieldDescriptor fieldDesc, String classTable, boolean ext )
                throws MappingException{

            // for readability
            final int FIELD_TYPE = 0;

            final int REF_TYPE = 1;

            final int REL_TABLE_TYPE = 2;

            int type;

            FieldDescriptor[] classids = clsDesc.getIdentities();
            ClassDescriptor related = fieldDesc.getClassDescriptor();
            if ( related != null && !( related instanceof JDOClassDescriptor ) )
                    throw new MappingException("Related class is not JDOClassDescriptor");

            if ( fieldDesc.getClassDescriptor() != null ) {
                // !(fieldDesc instanceof JDOFieldDescriptor) ) {
                // no <sql> tag, treated as foreign key field of
                // PersistenceCapable

                // determine the type of field
                if ( !( fieldDesc instanceof JDOFieldDescriptor ) )
                    type = REF_TYPE;
                else if ( ((JDOFieldDescriptor)fieldDesc).getManyTable() != null )
                    type = REL_TABLE_TYPE;
                else if ( ((JDOFieldDescriptor)fieldDesc).getSQLName() != null )
                    type = FIELD_TYPE;
                else
                    type = REF_TYPE;

                // initalize the column names
                FieldDescriptor[] relids = ((JDOClassDescriptor)related).getIdentities();
                String[] names = null;
                if ( fieldDesc instanceof JDOFieldDescriptor )
                    names = ((JDOFieldDescriptor)fieldDesc).getSQLName();
                String[] relnames = new String[relids.length];
                for ( int i=0; i<relids.length; i++ ) {
                    relnames[i] = ((JDOFieldDescriptor)relids[i]).getSQLName()[0];
                    if ( relnames[i] == null )
                        throw new MappingException("Related class identities field does not contains sql information!");
                }
                String[] joins = null;
                if ( fieldDesc instanceof JDOFieldDescriptor )
                    joins = ((JDOFieldDescriptor)fieldDesc).getManyKey();
                String[] classnames = new String[classids.length];
                for ( int i=0; i<classids.length; i++ ) {
                    classnames[i] = ((JDOFieldDescriptor)classids[i]).getSQLName()[0];
                    if ( classnames[i] == null )
                        throw new MappingException("Related class identities field does not contains sql information!");
                }

                // basic check of column names
                if ( names != null && names.length != relids.length )
                    throw new MappingException("The number of column of foreign keys doesn't not match with what specified in manyKey");
                if ( joins != null && joins.length != classids.length )
                    throw new MappingException("The number of column of foreign keys doesn't not match with what specified in manyKey");

                // initalize the class
                switch (type) {
                case FIELD_TYPE:
                    this.tableName = classTable;//((JDOClassDescriptor)clsDesc).getTableName();;
                    this.jdoName = fieldDesc.getFieldName();
                    this.load = true;
                    this.store = !ext;
                    this.multi = false;
                    this.joined = false;
                    this.dirtyCheck = ((JDOFieldDescriptor)fieldDesc).isDirtyCheck();
                    names = (names!=null?names:relnames);
                    this.joinFields = classnames;
                    break;
                case REF_TYPE:
                    this.tableName = ((JDOClassDescriptor)related).getTableName();
                    this.jdoName = fieldDesc.getFieldName();
                    this.load = true;
                    this.store = false;
                    this.multi = fieldDesc.isMultivalued();
                    this.joined = true;
                    this.dirtyCheck = (fieldDesc instanceof JDOFieldDescriptor)?((JDOFieldDescriptor)fieldDesc).isDirtyCheck():true;
                    names = (names!=null?names:relnames);
                    this.joinFields = (joins!=null?joins:classnames);
                    break;
                case REL_TABLE_TYPE:
                    this.tableName = ((JDOFieldDescriptor)fieldDesc).getManyTable();
                    this.jdoName = fieldDesc.getFieldName();
                    this.load = true;
                    this.store = false;
                    this.multi = fieldDesc.isMultivalued();
                    this.joined = true;
                    this.dirtyCheck = ((JDOFieldDescriptor)fieldDesc).isDirtyCheck();
                    names = (names!=null?names:relnames);
                    this.joinFields = (joins!=null?joins:classnames);
                    break;
                default:
                    throw new MappingException("Never happen! But, it won't compile without the exception");
                }

                this.columns = new ColumnInfo[relids.length];
                for ( int i=0; i<relids.length; i++ ) {
                    if ( !(relids[i] instanceof JDOFieldDescriptor) )
                        throw new MappingException("Related class identities field does not contains sql information!");

                    JDOFieldDescriptor relId = (JDOFieldDescriptor)relids[i];
                    FieldHandlerImpl fh = (FieldHandlerImpl) relId.getHandler();
                    columns[i] = new ColumnInfo( names[i], relId.getSQLType()[0],
                            fh.getConvertTo(), fh.getConvertFrom(), fh.getConvertParam() );
                }
            } else {
                // primitive field
                this.tableName = classTable;
                this.jdoName = fieldDesc.getFieldName();
                this.load = true;
                this.store = !ext;
                this.multi = false;
                this.joined = false;
                this.joinFields = null;
                this.dirtyCheck = ((JDOFieldDescriptor)fieldDesc).isDirtyCheck();

                FieldHandlerImpl fh = (FieldHandlerImpl) fieldDesc.getHandler();
                this.columns = new ColumnInfo[1];
                String[] sqlNameArray = ((JDOFieldDescriptor)fieldDesc).getSQLName();
                String sqlName;
                if ( sqlNameArray == null ) {
                    sqlName = fieldDesc.getFieldName();
                } else {
                    sqlName = sqlNameArray[0];
                }
                this.columns[0] = new ColumnInfo( sqlName,
                        ((JDOFieldDescriptor)fieldDesc).getSQLType()[0], fh.getConvertTo(),
                        fh.getConvertFrom(), fh.getConvertParam() );
            }
        }
		public boolean isComplex() {
			return true;
		}
		public boolean isPersisted() {
			return store;
		}
		public String getFieldName() {
			return jdoName;
		}
        public String toString() {
            return tableName + "." + jdoName;
        }
    }

    static final class ColumnInfo {

        final String  name;

        final int sqlType;

        final TypeConvertor convertTo;

        final TypeConvertor convertFrom;

        final String convertParam;

        ColumnInfo( String name, int type, TypeConvertor convertTo,
                TypeConvertor convertFrom, String convertParam ) {
            this.name = name;
            this.sqlType = type;
            this.convertTo = convertTo;
            this.convertFrom = convertFrom;
            this.convertParam = convertParam;
        }
    }
    static final class SQLQuery implements PersistenceQuery {


        private PreparedStatement _stmt;


        private ResultSet         _rs;


        private final SQLEngine _engine;


        private final Class[]   _types;


        private final Object[]  _values;


        private final String    _sql;


        private Object[]        _lastIdentity;


        private int[]           _identSqlType;


        private boolean         _resultSetDone;


        SQLQuery( SQLEngine engine, String sql, Class[] types )
        {
            _engine = engine;
            _types = types;
            _values = new Object[ _types.length ];
            _sql = sql;
            _identSqlType = new int[_engine._clsDesc.getIdentities().length];
            for (int i = 0; i < _identSqlType.length; i++) {
                _identSqlType[i] = ((JDOFieldDescriptor) _engine._clsDesc.getIdentities()[i]).getSQLType()[0];
            }
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
            return _engine._clsDesc.getJavaClass();
        }


        public void execute( Object conn, AccessMode accessMode )
            throws QueryException, PersistenceException
        {
            _lastIdentity = null;
            try {
                _stmt = ( (Connection) conn ).prepareStatement( _sql );
                for ( int i = 0 ; i < _values.length ; ++i ) {
                    _stmt.setObject( i + 1, _values[ i ] );
                    _values[ i ] = null;
                }
                _rs = _stmt.executeQuery();
                _resultSetDone = false;
            } catch ( SQLException except ) {
                if ( _stmt != null ) {
                    try {
                        _stmt.close();
                    } catch ( SQLException e2 ) { }
                }
                _resultSetDone = true;
                throw new PersistenceException( Messages.format("persist.nested", except) + " while executing "+ _sql, except );
            }
        }


        public Object nextIdentity( Object identity )
                throws PersistenceException {

            Object[] oldIdentity;
            Object[] returnId;
            boolean empty = false;
            boolean newId = false;

            try {
                if ( _resultSetDone )
                    return null;

                // _lastIdentity is null in the first call to this method
                if ( _lastIdentity == null ) {
                    // the query is empty
                    if ( ! _rs.next() ) {
                        _resultSetDone = true;
                        return null;
                    }

                    _lastIdentity = new Object[_engine._ids.length];
                    returnId = new Object[_engine._ids.length];
                    empty = true;
                    for ( int i=0; i<_engine._ids.length; i++ ) {
                        _lastIdentity[i] = SQLTypes.getObject( _rs, 1+i, _identSqlType[i] );
                        returnId[i] = _engine.idToJava( i, _lastIdentity[i] );
                        if ( _lastIdentity[i] != null )
                            empty = false;
                    }
                    if ( empty ) {
                        return null;
                    } else {
                        switch (_engine._ids.length) {
                        case 1:
                            return returnId[0];
                        case 2:
                            return new Complex( returnId[0], returnId[1] );
                        default:
                            return new Complex( returnId );
                        }
                    }
                }

                // convert the identity from java type into sql
                // type for comparsion
                oldIdentity =  new Object[_engine._ids.length];
                returnId = new Object[_engine._ids.length];

                // determine if the id in the resultSet is a new one
                if ( _engine._ids.length > 1 ) {
                    Complex id = (Complex) identity;
                    for ( int i=0; i < _engine._ids.length; i++ ) {
                        returnId[i] = id.get(i);
                        oldIdentity[i] = _engine.idToSQL( i, id.get(i) );
                        if ( !oldIdentity[i].equals( _lastIdentity[i] ) ) {
                            newId = true;
                            _lastIdentity[i] = oldIdentity[i];
                            returnId[i] = _engine.idToJava( i, _lastIdentity[i] );
                        }
                    }
                } else {
                    returnId[0] = identity;
                    oldIdentity[0] = _engine.idToSQL( 0, identity );
                    if ( !oldIdentity[0].equals( _lastIdentity[0] ) ) {
                        newId = true;
                        _lastIdentity[0] = oldIdentity[0];
                        returnId[0] = _engine.idToJava( 0, _lastIdentity[0] );
                    }
                }

                // skip to the next id
                while ( !newId && !_resultSetDone ) {
                    if ( ! _rs.next() ) {
                        _lastIdentity = null;
                        _resultSetDone = true;
                        return null;
                    }

                    empty = true;
                    for ( int i=0; i<_engine._ids.length; i++ ) {
                        Object o = SQLTypes.getObject( _rs, 1+i, _identSqlType[i] );
                        if ( !oldIdentity[i].equals( o ) ) {
                            newId = true;
                            _lastIdentity[i] = o;
                            returnId[i] = _engine.idToJava( i, _lastIdentity[i] );
                        }
                        if ( o != null )
                            empty = false;
                    }
                }
                    if ( empty )
                    return null;
                else if ( _engine._ids.length > 1 )
                    return new Complex( returnId );
                else
                    return returnId[0];

            } catch ( SQLException except ) {
                _lastIdentity = null;
                throw new PersistenceException( Messages.format("persist.nested", except), except );
            }
        }


        public void close()
        {
            if ( _rs != null ) {
                try {
                    _rs.close();
                } catch ( SQLException except ) { }
                _rs = null;
            }
            if ( _stmt != null ) {
                try {
                    _stmt.close();
                } catch ( SQLException except ) { }
                _stmt = null;
            }
        }


        public Object fetch(Object[] fields,Object identity)
                throws ObjectNotFoundException, PersistenceException {

            int    count;
            Object stamp = null;
            boolean newId = false;
            Object[] sqlIdentity = new Object[_engine._ids.length];

            try {
                // Load all the fields of the object including one-one relations
                Object[] temp = new Object[10];  // bad pratice, assume complex field smaller than 10

                count = _engine._ids.length;

                // load all the fields
                for ( int i = 0 ; i < _engine._fields.length ; ++i  ) {
                    if ( !_engine._fields[i].load )
                        continue;

                    if ( !_engine._fields[i].multi ) {
                        boolean notNull = false;
                        if ( _engine._fields[i].columns.length == 1 ) {
                            Object value = _engine.toJava( i, 0, SQLTypes.getObject( _rs, count++, _engine._fields[i].columns[0].sqlType ) );
                            if ( !_rs.wasNull() )
                                fields[i] = value;
                            else
                                fields[i] = null;
                        } else {
                            Complex inner = (Complex) fields[i];
                        for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                                Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count++, _engine._fields[i].columns[j].sqlType ) );
                                if ( !_rs.wasNull() ) {
                                temp[j] = value ;
                                    notNull = true;
                                } else {
                                    temp[j] = null;
                                }
                            }
                            fields[i] = null;
                            if ( notNull )
                                fields[i] = new Complex( _engine._fields[i].columns.length, temp );
                        }
                    } else {
                        ArrayList res = new ArrayList();
                        boolean notNull = false;
                        for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                            Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                            if ( !_rs.wasNull() ) {
                                temp[j] = value;
                                notNull = true;
                            } else {
                                temp[j] = null;
                            }
                            count++;
                        }
                        if ( notNull ) {
                            if ( _engine._fields[i].columns.length == 1 )
                                res.add( temp[0] );
                            else
                                res.add( new Complex( _engine._fields[i].columns.length, temp ) );
                        }
                        fields[i] = res;
                    }
                }

                // add other one-to-many fields
                if ( !_resultSetDone && _rs.next() ) {
                    count = 1;
                    if ( _lastIdentity == null )
                        _lastIdentity = new Object[_engine._ids.length];

                    // check if the table row consists data of the interested identity
                    for ( int i=0; i<_lastIdentity.length; i++ ) {
                        Object o = SQLTypes.getObject( _rs, count, _identSqlType[i] );
                        if ( !o.equals( sqlIdentity[i] ) ) {
                            newId = true;
                            _lastIdentity[i] = o;
                        }
                        count++;
                    }

                    // move forward in the ResultSet, until we see
                    // another identity
                    while ( !newId ) {
                        for ( int i = 0 ; i < _engine._fields.length ; ++i  ) {
                            if ( !_engine._fields[i].load )
                                continue;

                            if ( _engine._fields[i].multi ) {
                                ArrayList res = (ArrayList)fields[i];
                                boolean notNull = false;
                                for ( int j=0; j<_engine._fields[i].columns.length; j++ ) {
                                    Object value = _engine.toJava( i, j, SQLTypes.getObject( _rs, count, _engine._fields[i].columns[j].sqlType ) );
                                    if ( !_rs.wasNull() ) {
                                        temp[j] = value ;
                                        notNull = true;
                                    } else {
                                       temp[j] = null;
                                }
                                count++;
                                }
                                if ( notNull ) {
                                    if ( _engine._fields[i].columns.length == 1 ) {
                                        if ( !res.contains( temp[0] ) )
                                            res.add( temp[0] );
                                    } else {
                                        Complex com = new Complex( _engine._fields[i].columns.length, temp );
                                        if ( !res.contains( com ) )
                                            res.add( new Complex( _engine._fields[i].columns.length, temp ) );
                                    }
                                }
                            } else {
                                // non-multi fields have to be done one only
                                // so, skip to next
                                count += _engine._fields[i].columns.length;
                            }
                        }

                        if ( _rs.next() ) {
                            // check if the table row consists data of the interested identity
                            for ( int i=0; i<_lastIdentity.length; i++ ) {
                                Object o = SQLTypes.getObject( _rs, count, _identSqlType[i] );
                                if ( !o.equals( sqlIdentity[i] ) ) {
                                    newId = true;
                                    _lastIdentity[i] = o;
                                }
                                count++;
                            }
                        } else {
                            _resultSetDone = true;
                            _lastIdentity = null;
                        }
                    }
                } else {
                    _lastIdentity = null;
                    _resultSetDone = true;
                }
            } catch ( SQLException except ) {
                throw new PersistenceException( Messages.format("persist.nested", except), except );
            }

            return stamp;
        }
    }
}
