/*
 * Copyright 2005 Assaf Arkin, Thomas Yip, Bruce Snyder, Werner Guttmann, Ralf Joachim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.exolab.castor.jdo.engine;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.castor.jdo.engine.ConnectionFactory;
import org.castor.jdo.engine.DatabaseRegistry;
import org.castor.jdo.engine.SQLTypeInfos;
import org.castor.jdo.util.JDOUtils;
import org.castor.persist.ProposedObject;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.loader.FieldHandlerImpl;
import org.exolab.castor.persist.spi.Complex;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.util.Messages;

/**
 * The SQL engine performs persistence of one object type against one
 * SQL database. It can only persist simple objects and extended
 * relationships. An SQL engine is created for each object type
 * represented by a database. When persisting, it requires a physical
 * connection that maps to the SQL database and the transaction
 * running on that database
 *
 * @author <a href="mailto:arkin AT intalio DOT com">Assaf Arkin</a>
 * @author <a href="mailto:yip AT intalio DOT com">Thomas Yip</a>
 * @author <a href="mailto:ferret AT frii DOT com">Bruce Snyder</a>
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 */
public final class SQLEngine implements Persistence {
    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     *  Commons Logging</a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog( SQLEngine.class );

    /** SQL statements for PK lookup */
    private String                      _pkLookup;

    /** SQL statements for object creation */
    private String                      _sqlCreate;

    /** SQL statements for object removal */
    private String                      _sqlRemove;

    /** SQL statements for updating object instance */
    private String                      _sqlStore;

    private String                      _sqlStoreDirty;

    /** SQL statements for loading object instance */
    private String                      _sqlLoad;

    private String                      _sqlLoadLock;

    private final SQLFieldInfo[]        _fields;

    private final SQLColumnInfo[]       _ids;

    private SQLEngine                   _extends;

    private QueryExpression             _sqlFinder;

    private final PersistenceFactory    _factory;

    private final String                _type;

    private final String                _mapTo;

    private final JDOClassDescriptor    _clsDesc;

    private KeyGenerator                _keyGen;

    /** Indicates whether there is a field to persist at all; in the case of 
     *  EXTEND relationships where no additional attributes are defined in the 
     *  extending class, this might NOT be the case; in general, a class has to have
     *  at least one field that is to be persisted. */
    private boolean                     _hasFieldsToPersist = false;

    /** Number of ClassDescriptor that extend this one. */
    private final int                   _numberOfExtendLevels;

    /** Collection of all the ClassDescriptor that extend this one (closure) */
    private final Collection            _extendingClassDescriptors;

    SQLEngine(final JDOClassDescriptor clsDesc, final PersistenceFactory factory,
              final String stampField) throws MappingException {

        _clsDesc = clsDesc;
        _factory = factory;
        _keyGen = null;
        _type = clsDesc.getJavaClass().getName();
        _mapTo = clsDesc.getTableName();
        
        if (_clsDesc.getExtends() == null) {
            KeyGeneratorDescriptor keyGenDesc = clsDesc.getKeyGeneratorDescriptor();
            if (keyGenDesc != null) {
                int[] tempType = ((JDOFieldDescriptor) _clsDesc.getIdentity()).getSQLType();
                _keyGen = keyGenDesc.getKeyGeneratorRegistry().getKeyGenerator(
                        _factory, keyGenDesc, (tempType == null) ? 0 : tempType[0]);

                // Does the key generator support the sql type specified in the mapping?
                _keyGen.supportsSqlType(tempType[0]);
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
         */
        // then, we put depended class ids in the back
        JDOClassDescriptor base = clsDesc;

        // walk until the base class which this class extends
        base = clsDesc;
        Stack stack = new Stack();
        stack.push(base);
        while (base.getExtends() != null) {
            // if (base.getDepends() != null) {
            //     throw new MappingException("Class should not both depends on and extended other classes");
            // }
            base = (JDOClassDescriptor) base.getExtends();
            stack.push(base);
            // do we need to add loop detection?
        }

        // now base is either the base of extended class, or
        // clsDesc
        // we always put the original id info in front
        // [oleg] except for SQL name, it may differ.
        FieldDescriptor[] baseIdDescriptors = base.getIdentities();
        FieldDescriptor[] idDescriptors = clsDesc.getIdentities();

        for (int i = 0; i < baseIdDescriptors.length; i++) {
            if (baseIdDescriptors[i] instanceof JDOFieldDescriptor) {
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
                idsInfo.add(new SQLColumnInfo(sqlName[0], sqlType[0], fh.getConvertTo(), fh.getConvertFrom(), fh.getConvertParam()));
            } else {
                throw new MappingException("Except JDOFieldDescriptor");
            }
        }

        // then do the fields
        while (!stack.empty()) {
            base = (JDOClassDescriptor) stack.pop();
            FieldDescriptor[] fieldDescriptors = base.getFields();
            for (int i = 0; i < fieldDescriptors.length; i++) {
                // fieldDescriptors[i] is persistent in db if it is not transient
                // and it is a JDOFieldDescriptor or has a ClassDescriptor
                if (!fieldDescriptors[i].isTransient()) {
                    if ((fieldDescriptors[i] instanceof JDOFieldDescriptor)
                            || (fieldDescriptors[i].getClassDescriptor() != null))  {
                        
                        fieldsInfo.add(new SQLFieldInfo(clsDesc, fieldDescriptors[i],
                                base.getTableName(), !stack.empty()));
                    }
                }
            }
        }

        _ids = new SQLColumnInfo[idsInfo.size()];
        idsInfo.copyInto(_ids);

        _fields = new SQLFieldInfo[fieldsInfo.size()];
        fieldsInfo.copyInto(_fields);

        // obtain the number of ClassDescriptor that extend this one.
        _numberOfExtendLevels = SQLHelper.numberOfExtendingClassDescriptors(getDescriptor());
        _extendingClassDescriptors = getDescriptor().getExtendedBy();

        // iterate through all fields to check whether there is a field
        // to persist at all; in the case of extend relationships where no 
        // additional attributes are defined in the extending class, this 
        // might NOT be the case
        for (int i = 0 ; i < _fields.length ; ++i) {
            if (_fields[i].isStore()) {
                _hasFieldsToPersist = true;
                break;
            }
        }

        if(LOG.isDebugEnabled()) {
            LOG.debug("hasFieldsToPersist = " + _hasFieldsToPersist);
        }
        
        try {
            buildSqlPKLookup();
            // _log.debug ("pkLookup = " + _pkLookup);
            buildSqlCreate();
            // _log.debug ("sqlCreate = " + _sqlCreate);
            buildSqlRemove();
            // _log.debug ("sqlRemove = " + _sqlRemove);
            buildFinder(clsDesc);
            // _log.debug ("sqlLoad = " + _sqlLoad);
            // _log.debug ("sqlLoadLock = " + _sqlLoadLock);
            buildSqlUpdate();
            // _log.debug ("sqlStore = " + _sqlStore);
            // _log.debug ("sqlStoreDirty = " + _sqlStoreDirty);
        } catch (QueryException except) {
            LOG.warn("Problem building SQL", except);
            throw new MappingException(except);
        }
    }

    public SQLColumnInfo[] getColumnInfoForIdentities() {
    	return _ids;
    }
    
    public SQLFieldInfo[] getInfo() {
        return _fields;
    }

    /**
     * Mutator method for setting extends SQLEngine
     * @param engine
     */
    public void setExtends(final SQLEngine engine) {
        _extends = engine;
    }

    private Connection getSeparateConnection(final Database database)
    throws PersistenceException {
        ConnectionFactory factory = null;
        try {
            factory = DatabaseRegistry.getConnectionFactory(database.getDatabaseName());
        } catch (MappingException e) {
            throw new PersistenceException(Messages.message("persist.cannotCreateSeparateConn"), e);
        }
        
        try {
            Connection conn = factory.createConnection();
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            throw new PersistenceException(Messages.message("persist.cannotCreateSeparateConn"), e);
        }
    }

    private void closeSeparateConnection(final Connection conn) {
        try {
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    /**
     * Used by {@link org.exolab.castor.jdo.OQLQuery} to retrieve the class descriptor.
     * @return the JDO class descriptor.
     */
    public JDOClassDescriptor getDescriptor() {
        return _clsDesc;
    }

    public PersistenceQuery createQuery(final QueryExpression query, final Class[] types,
                                        final AccessMode accessMode)
    throws QueryException {
        AccessMode mode = (accessMode != null) ? accessMode : _clsDesc.getAccessMode();
        String sql = query.getStatement(mode == AccessMode.DbLocked);
        
        if(LOG.isDebugEnabled()) {
            LOG.debug(Messages.format("jdo.createSql", sql));
        }
        
        return new SQLQuery(this, _factory, sql, types, false);
    }


    public PersistenceQuery createCall(final String spCall, final Class[] types) {
        FieldDescriptor[] fields;
        String[] jdoFields0;
        String[] jdoFields;
        String sql;
        int[] sqlTypes0;
        int[] sqlTypes;
        int count;

        // changes for the SQL Direct interface begins here
        if (spCall.startsWith("SQL")) {
            sql = spCall.substring(4);
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format("jdo.directSQL", sql));
            }
            
            return new SQLQuery(this, _factory, sql, types, true);
		}

        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.format("jdo.spCall", spCall));
        }

        fields = _clsDesc.getFields();
        jdoFields0 = new String[fields.length + 1];
        sqlTypes0 = new int[fields.length + 1];
        // the first field is the identity

        count = 1;
        jdoFields0[0] = _clsDesc.getIdentity().getFieldName();
        sqlTypes0[0] = ((JDOFieldDescriptor) _clsDesc.getIdentity()).getSQLType()[0];
        for (int i = 0 ; i < fields.length ; ++i) {
            if (fields[i] instanceof JDOFieldDescriptor) {
                jdoFields0[count] = ((JDOFieldDescriptor) fields[i]).getSQLName()[0];
                sqlTypes0[count] = ((JDOFieldDescriptor) fields[i]).getSQLType()[0];
                ++count;
            }
        }
        jdoFields = new String[count];
        sqlTypes = new int[count];
        System.arraycopy(jdoFields0, 0, jdoFields, 0, count);
        System.arraycopy(sqlTypes0, 0, sqlTypes, 0, count);

        return ((BaseFactory) _factory).getCallQuery(spCall, types,_clsDesc.getJavaClass(), jdoFields, sqlTypes);
    }

    public QueryExpression getQueryExpression() {
        return _factory.getQueryExpression();
    }

    public QueryExpression getFinder() {
        return (QueryExpression) _sqlFinder.clone();
    }

    private Object idToSQL(final int index, final Object object) {
        if ((object == null) || (_ids[index].getConvertFrom() == null)) {
            return object;
        }
        return _ids[index].getConvertFrom().convert(object, _ids[index].getConvertParam());
    }

    private Object toSQL(final int field, final int column, final Object object) {
        SQLColumnInfo col = _fields[field].getColumnInfo()[column];
        if ((object == null) || (col.getConvertFrom() == null)) {
            return object;
        }
        return col.getConvertFrom().convert(object, col.getConvertParam());
    }

    protected Object idToJava(final int index, final Object object) {
        if ((object == null) || (_ids[index].getConvertTo() == null)) {
            return object;
        }
        return _ids[index].getConvertTo().convert(object, _ids[index].getConvertParam());
    }

    protected Object toJava(final int field, final int column, final Object object) {
        SQLColumnInfo col = _fields[field].getColumnInfo()[column];
        if ((object == null) || (col.getConvertTo() == null)) {
            return object;
        }
        return col.getConvertTo().convert(object, col.getConvertParam());
    }

    /**
     * Use the specified keygenerator to gengerate a key for this
     * row of object.
     *
     * Result key will be in java type.
     * @param database Database instance
     * @param conn JDBC Connection instance
     * @param stmt JDBC Statement instance
     * @return The generated key
     * @throws PersistenceException If no key can be generated 
     */
    private Object generateKey(final Database database, final Object conn,
                               final PreparedStatement stmt)
    throws PersistenceException {
        Object identity;
        Connection connection;
        Properties prop = null;

        // TODO [SMH]: Change KeyGenerator.isInSameConnection to KeyGenerator.useSeparateConnection?
        // TODO [SMH]: Move "if (_keyGen.isInSameConnection() == false)" out of SQLEngine and into key-generator?
        if (_keyGen.isInSameConnection() == false) {
            connection = getSeparateConnection(database);
        } else {
            connection = (Connection) conn;
        }

        if (stmt != null) {
            prop = new Properties();
            prop.put("insertStatement", stmt);
        }

        try {
            synchronized (connection) {
                identity = _keyGen.generateKey(connection, _clsDesc.getTableName(), _ids[0].getName(), prop);
            }

            // TODO [SMH]: Move "if (identity == null)" into keygenerator.
            if (identity == null) {
                throw new PersistenceException(
                    Messages.format("persist.noIdentity", _clsDesc.getJavaClass().getName()));
            }

            return idToJava(0, identity);
        } finally {
            if (_keyGen.isInSameConnection() == false) {
                closeSeparateConnection(connection);
            }
        }
    }

    /**
     * @see org.exolab.castor.persist.spi.Persistence
     *      #create(org.exolab.castor.jdo.Database, java.lang.Object,
     *              java.lang.Object[], java.lang.Object)
     */
    public Object create(final Database database, final Object conn,
                         final Object[] fields, Object identity)
    throws PersistenceException {
        PreparedStatement stmt = null;
        int count;

        if ((_extends == null) && (_keyGen == null) && (identity == null)) {
            throw new PersistenceException(Messages.format("persist.noIdentity", _clsDesc.getJavaClass().getName()));
        }

        try {
            // Must create record in the parent table first.
            // All other dependents are created afterwards.
            if (_extends != null) {
                // | quick and very dirty hack to try to make multiple class on the same table work
                if (!_extends._mapTo.equals(_mapTo)) {
                    identity = _extends.create(database, conn, fields, identity);
                }
            } else if ((_keyGen != null) && (_keyGen.getStyle() == KeyGenerator.BEFORE_INSERT)) {
                // Generate key before INSERT
                // genKey return identity in JDO type
                identity = generateKey(database, conn, null);
            }

            if ((_keyGen != null) && (_keyGen.getStyle() == KeyGenerator.DURING_INSERT)) {
                stmt = ((Connection) conn).prepareCall(_sqlCreate);
            } else {
                stmt = ((Connection) conn).prepareStatement(_sqlCreate);
            }
             
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format("jdo.creating", _clsDesc.getJavaClass().getName(), stmt.toString()));
            }
            
            // Must remember that SQL column index is base one
            count = 1;
            if ((_keyGen == null) || (_keyGen.getStyle() == KeyGenerator.BEFORE_INSERT)) {
                if ((_ids.length > 1) && !(identity instanceof Complex)) {
                    throw new PersistenceException("Multiple identities expected!");
                }

                if (identity instanceof Complex) {
                    Complex id = (Complex) identity;
                    if ((id.size() != _ids.length) || (_ids.length <= 1)) {
                        throw new PersistenceException("Size of complex field mismatched!");
                    }

                    for (int i = 0; i < _ids.length; i++) {
                        stmt.setObject(count++, idToSQL(i, id.get(i)));
                    }
                } else {
                    if (_ids.length != 1) {
                        throw new PersistenceException("Complex field expected!");
                    }

                    stmt.setObject(count++, idToSQL(0, identity));
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format("jdo.creating", _clsDesc.getJavaClass().getName(), stmt.toString()));
            }

            count = bindFields(fields, stmt, count);

            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format("jdo.creating", _clsDesc.getJavaClass().getName(), stmt.toString()));
            }

            // Generate key during INSERT
            if ((_keyGen != null) && (_keyGen.getStyle() == KeyGenerator.DURING_INSERT)) {
                CallableStatement cstmt = (CallableStatement) stmt;
                int sqlType;

                sqlType = _ids[0].getSqlType();
                cstmt.registerOutParameter(count, sqlType);
                
                // [WG]: TODO: Verify that this really works !!!
                if (LOG.isDebugEnabled()) {
                 	  LOG.debug(Messages.format("jdo.creating.bound", _clsDesc.getJavaClass().getName(), cstmt));
                }
                
                cstmt.execute();

                // First skip all results "for maximum portability"
                // as proposed in CallableStatement javadocs.
                while (cstmt.getMoreResults() || (cstmt.getUpdateCount() != -1)) {
                	// no code to execute
                }

                // Identity is returned in the last parameter
                // Workaround: for INTEGER type in Oracle getObject returns BigDecimal
                if (sqlType == java.sql.Types.INTEGER) {
                    identity = new Integer(cstmt.getInt(count));
                } else {
                    identity = cstmt.getObject(count);
                }
                identity = idToJava(0, identity);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(Messages.format("jdo.creating", _clsDesc.getJavaClass().getName(), stmt.toString()));
                }
            	stmt.executeUpdate();
            }

            stmt.close();

            // Generate key after INSERT
            if ((_keyGen != null) && (_keyGen.getStyle() == KeyGenerator.AFTER_INSERT)) {
                identity = generateKey(database, conn, stmt);
            }

            return identity;
        } catch (SQLException except) {
        	if (LOG.isInfoEnabled()) {
        		LOG.info(Messages.format("jdo.storeFatal",  _type,  _sqlCreate), except);
        	}

            // [oleg] Check for duplicate key based on X/Open error code
            // Bad way: all validation exceptions are reported as DuplicateKey
            //if ( except.getSQLState() != null &&
            //     except.getSQLState().startsWith( "23" ) )
            //    throw new DuplicateIdentityException( _clsDesc.getJavaClass(), identity );

            // Good way: let PersistenceFactory try to determine
            Boolean isDupKey;

            isDupKey = _factory.isDuplicateKeyException(except);
            if (Boolean.TRUE.equals(isDupKey)) {
                throw new DuplicateIdentityException(Messages.format("persist.duplicateIdentity", _clsDesc.getJavaClass().getName(), identity));
            } else if (Boolean.FALSE.equals(isDupKey)) {
                throw new PersistenceException(Messages.format("persist.nested", except), except);
            }
            // else unknown, let's check directly.

            // [oleg] Check for duplicate key the old fashioned way,
            //        after the INSERT failed to prevent race conditions
            //        and optimize INSERT times
            try {
                // Close the insert statement
                if (stmt != null) {
                    stmt.close();
                }

                stmt = ((Connection) conn).prepareStatement(_pkLookup);

                if (LOG.isDebugEnabled()) {
                    LOG.debug(Messages.format("jdo.duplicateKeyCheck", _pkLookup));
                }
                
                // bind the identity to the preparedStatement
                count = 1;
                if (identity instanceof Complex) {
                    Complex id = (Complex) identity;
                    if ((id.size() != _ids.length) || (_ids.length <= 1)) {
                        throw new PersistenceException("Size of complex field mismatched!");
                    }

                    for (int i = 0; i < _ids.length; i++) {
                        stmt.setObject(count++, idToSQL(i, id.get(i)));
                    }
                } else {
                    if (_ids.length != 1) {
                        throw new PersistenceException("Complex field expected!");
                    }

                    stmt.setObject(count++, idToSQL(0, identity));
                }

                if (stmt.executeQuery().next()) {
                    stmt.close();
                    throw new DuplicateIdentityException(Messages.format("persist.duplicateIdentity", _clsDesc.getJavaClass().getName(), identity));
                }
            } catch (SQLException except2) {
                // Error at the stage indicates it wasn't a duplicate
                // primary key problem. But best if the INSERT error is
                // reported, not the SELECT error.
            }

            try {
                // Close the insert/select statement
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException except2) {
                LOG.warn("Problem closing JDBC statement", except2);
            }
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
    }

	/**
	 * Bind non-identity fields to prepared statement.
     * 
	 * @param fields Field to bind.
	 * @param stmt PreparedStatement instance.
	 * @param count Field counter
	 * @throws SQLException If the fields cannot be bound successfully.
	 * @throws PersistenceException
	 */
	private int bindFields(final Object[] fields, final PreparedStatement stmt, int count) 
	throws SQLException, PersistenceException {
		for (int i = 0 ; i < _fields.length ; ++i) {
		    if (_fields[ i ].isStore()) {
		        if (fields[i] == null) {
		            for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
		                stmt.setNull(count++, _fields[i].getColumnInfo()[j].getSqlType());
                    }

		        } else if (fields[i] instanceof Complex) {
		            Complex complex = (Complex) fields[i];
		            if (complex.size() != _fields[i].getColumnInfo().length) {
		                throw new PersistenceException("Size of complex field mismatch!");
                    }

		            for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
		                Object value = (complex == null) ? null : complex.get(j);
		                SQLTypeInfos.setValue(stmt, count++, toSQL(i, j, value), _fields[i].getColumnInfo()[j].getSqlType());
		            }
		        } else {
		            if (_fields[i].getColumnInfo().length != 1) {
		                throw new PersistenceException("Complex field expected!");
                    }

		            SQLTypeInfos.setValue(stmt, count++, toSQL(i, 0, fields[i]), _fields[i].getColumnInfo()[0].getSqlType());
		        }
		    }
		}
        return count;
	}

    /**
     * if isNull, replace next "=?" with " IS NULL", otherwise skip next "=?",
     * move "pos" to the left.
     * 
     * @param isNull True if =? should be replaced with 'IS NULL'
     * @param sb StringBUffer holding the SQL statement to be modified 
     * @param pos The current position (where to apply the replacement).
     * @return The next position.
     */
    private int nextParameter(final boolean isNull, final StringBuffer sb, int pos) {
        for ( ; pos > 0; pos--) {
            if ((sb.charAt(pos - 1) == '=') && (sb.charAt(pos) == '?')) {
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
     * If the RDBMS doesn't support setNull for "WHERE fld=?" and requires
     * "WHERE fld IS NULL", we need to modify the statement.
     */
    private String getStoreStatement(final Object[] original)
    throws PersistenceException {
        StringBuffer sb = null;
        int pos = 0;

        if (original == null) {
            return _sqlStore;
        }
        if (((BaseFactory) _factory).supportsSetNullInWhere()) {
            return _sqlStoreDirty;
        }
        pos = _sqlStoreDirty.length() - 1;
        sb = new StringBuffer(pos * 4);
        sb.append(_sqlStoreDirty);
        for (int i = _fields.length - 1; i >= 0; i--) {
            if (_fields[i].isStore() && _fields[i].isDirtyCheck()) {
                if (original[i] == null) {
                    for (int j = _fields[i].getColumnInfo().length - 1; j >= 0; j--) {
                        pos = nextParameter(true, sb, pos);
                    }
                } else if (original[i] instanceof Complex) {
                    Complex complex = (Complex) original[i];
                    if (complex.size() != _fields[i].getColumnInfo().length) {
                        throw new PersistenceException("Size of complex field mismatch!");
                    }

                    for (int j = _fields[i].getColumnInfo().length - 1; j >= 0; j--) {
                        pos = nextParameter((complex.get(j) == null), sb, pos);
                    }
                } else {
                    if (_fields[i].getColumnInfo().length != 1) {
                        throw new PersistenceException("Complex field expected!");
                    }

                    pos = nextParameter(false, sb, pos);
                }
            }
        }
        return sb.toString();
    }

    public Object store(final Object conn, final Object[] fields, final Object identity,
                        final Object[] original, final Object stamp)
    throws PersistenceException {
        PreparedStatement stmt = null;
        int count;
        String storeStatement = null;

        // Must store record in parent table first.
        // All other dependents are stored independently.
        if (_extends != null) {
            // | quick and very dirty hack to try to make multiple class on the same table work
            if (!_extends._mapTo.equals(_mapTo)) {
                _extends.store(conn, fields, identity, original, stamp);
            }
        }

        // Only build and execute an UPDATE statement if the class to be updated has 
        // fields to persist.
        if (_hasFieldsToPersist) {
            try {
                storeStatement = getStoreStatement(original);
                stmt = ((Connection) conn).prepareStatement(storeStatement);
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug(Messages.format("jdo.storing", _clsDesc.getJavaClass().getName(), stmt.toString()));
                }
                
                count = 1;
                
                // bind fields of the row to be stored into the preparedStatement
                for (int i = 0 ; i < _fields.length ; ++i) {
                    if (_fields[i].isStore()) {
                        if (fields[i] == null) {
                            for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                                stmt.setNull(count++, _fields[i].getColumnInfo()[j].getSqlType());
                            }
                            
                        } else if (fields[i] instanceof Complex) {
                            Complex complex = (Complex) fields[i];
                            if (complex.size() != _fields[i].getColumnInfo().length) {
                                throw new PersistenceException("Size of complex field mismatch!");
                            }
                            
                            for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                                SQLTypeInfos.setValue(stmt, count++, toSQL(i, j, complex.get(j)), _fields[i].getColumnInfo()[j].getSqlType());
                            }
                        } else {
                            if (_fields[i].getColumnInfo().length != 1) {
                                throw new PersistenceException("Complex field expected!");
                            }
                            
                            SQLTypeInfos.setValue(stmt, count++, toSQL(i, 0, fields[i]), _fields[i].getColumnInfo()[0].getSqlType());
                        }
                    }
                }
                
                // bind the identity of the row to be stored into the preparedStatement
                if (identity instanceof Complex) {
                    Complex id = (Complex) identity;
                    if ((id.size() != _ids.length) || (_ids.length <= 1)) {
                        throw new PersistenceException("Size of complex field mismatched!");
                    }
                    
                    for (int i = 0; i < _ids.length; i++) {
                        stmt.setObject(count++, idToSQL(i, id.get(i)));
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(Messages.format("jdo.bindingIdentity", _ids[i].getName(), idToSQL(i, id.get(i))));
                        }
                    }                    
                } else {
                    if (_ids.length != 1) {
                        throw new PersistenceException("Complex field expected!");
                    }
                    
                    stmt.setObject(count++, idToSQL(0, identity));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(Messages.format("jdo.bindingIdentity", _ids[0].getName(), idToSQL(0, identity)));
                    }
                }
                
                // bind the old fields of the row to be stored into the preparedStatement
                if (original != null) {
                    boolean supportsSetNull = ((BaseFactory) _factory).supportsSetNullInWhere();
                    
                    for (int i = 0 ; i < _fields.length ; ++i) {
                        if (_fields[i].isStore() && _fields[i].isDirtyCheck()) {
                            if (original[i] == null) {
                                if (supportsSetNull) {
                                    for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                                        stmt.setNull(count++, _fields[i].getColumnInfo()[j].getSqlType());
                                    }
                                }
                            } else if (original[i] instanceof Complex) {
                                Complex complex = (Complex) original[i];
                                if (complex.size() != _fields[i].getColumnInfo().length) {
                                    throw new PersistenceException("Size of complex field mismatch!");
                                }
                                
                                for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                                    SQLTypeInfos.setValue(stmt, count++, toSQL(i, j, complex.get(j)), _fields[i].getColumnInfo()[j].getSqlType());
                                    
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug(Messages.format("jdo.bindingField", _fields[i].getColumnInfo()[j].getName(), toSQL(i, j, complex.get(j))));
                                    }
                                }
                            } else {
                                if (_fields[i].getColumnInfo().length != 1) {
                                    throw new PersistenceException("Complex field expected!");
                                }
                                
                                SQLTypeInfos.setValue(stmt, count++, toSQL(i, 0, original[i]), _fields[i].getColumnInfo()[0].getSqlType() );
                            
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(Messages.format("jdo.bindingField", _fields[i].getColumnInfo()[0].getName(), toSQL(i, 0, original[i])));
                                }
                            }
                        }
                    }
                }
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug(Messages.format("jdo.storing", _clsDesc.getJavaClass().getName(), stmt.toString()));
                }

                if (stmt.executeUpdate() <= 0) { // SAP DB returns -1 here
                    // If no update was performed, the object has been previously
                    // removed from persistent storage or has been modified if
                    // dirty checking. Determine which is which.
                    stmt.close();
                    if (original != null) {
                        stmt = ((Connection) conn).prepareStatement(_sqlLoad);
                        
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(Messages.format("jdo.storing", _clsDesc.getJavaClass().getName(), stmt.toString()));
                        }
                        
                        // bind the identity to the prepareStatement
                        count = 1;
                        if (identity instanceof Complex) {
                            Complex id = (Complex) identity;
                            for (int i = 0; i < _ids.length; i++) {
                                stmt.setObject(count++, idToSQL(i, id.get(i)));
                            }
                        } else {
                            stmt.setObject(count++, idToSQL(0, identity));
                        }
                        
                        ResultSet res = stmt.executeQuery();
                        if (res.next()) {                     
                            StringBuffer enlistFieldsNotMatching = new StringBuffer();
                            
                            Object currentField = null;
                            int numberOfFieldsNotMatching = 0;
                            for (int i = 0; i < _fields.length; i++) {
                                currentField = toJava(i, 0, res.getObject(_fields[i].getColumnInfo()[0].getName()));
                                if (_fields[i].getTableName().compareTo(_mapTo) == 0) {
                                    if ((original[i] == null && currentField != null)
                                            || (currentField == null && original[i] != null)
                                            || (original[i] == null && currentField == null)) {
                                        
                                        enlistFieldsNotMatching.append("(" + _clsDesc.getJavaClass().getName() + ")." + _fields[i].getColumnInfo()[0].getName() + ": ");
                                        enlistFieldsNotMatching.append("[" + original[i] + "/" + currentField + "]"); 
                                    } else if (!original[i].equals(currentField) ) {
                                        if (numberOfFieldsNotMatching >= 1) {
                                            enlistFieldsNotMatching.append(", ");
                                        }
                                        enlistFieldsNotMatching.append("(" + _clsDesc.getJavaClass().getName() + ")." + _fields[i].getColumnInfo()[0].getName() + ": ");
                                        enlistFieldsNotMatching.append("[" + original[i] + "/" + currentField + "]"); 
                                        numberOfFieldsNotMatching++;
                                    }
                                }
                            }
                            throw new ObjectModifiedException(Messages.format("persist.objectModified", _clsDesc.getJavaClass().getName(), identity, enlistFieldsNotMatching.toString()));
                        }
                    }
                    throw new ObjectDeletedException(Messages.format("persist.objectDeleted", _clsDesc.getJavaClass().getName(), identity));
                }                
            } catch (SQLException except) {
                LOG.fatal(Messages.format("jdo.storeFatal", _type,  storeStatement), except);
                throw new PersistenceException(Messages.format("persist.nested", except), except);
            } finally {
                try {
                    // Close the insert/select statement
                    if (stmt != null) {
                        stmt.close();
                    }
                } 
                catch (SQLException except2) {
                    LOG.warn("Problem closing JDBC statement", except2);
                }
            }
        }

        return null;
    }

    public void delete(final Object conn, final Object identity)
    throws PersistenceException {
        PreparedStatement stmt = null;

        try {
            stmt = ((Connection) conn).prepareStatement(_sqlRemove);
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format("jdo.removing", _clsDesc.getJavaClass().getName(), stmt.toString()));
            }

            int count = 1;
            // bind the identity of the preparedStatement
            if (identity instanceof Complex) {
                Complex id = (Complex) identity;
                if ((id.size() != _ids.length) || (_ids.length <= 1)) {
                    throw new PersistenceException("Size of complex field mismatched!");
                }

                for (int i = 0; i < _ids.length; i++) {
                    stmt.setObject(count++, idToSQL(i, id.get(i)));
                }
            } else {
                if (_ids.length != 1) {
                    throw new PersistenceException("Complex field expected!");
                }
                stmt.setObject(count++, idToSQL(0, identity));
            }

            if (LOG.isDebugEnabled()) {
              LOG.debug(Messages.format("jdo.removing", _clsDesc.getJavaClass().getName(), stmt.toString()));
          }

            int result = stmt.executeUpdate();
            if (result < 1) {
                throw new PersistenceException("Object to be deleted does not exist! "+ identity);
            }

            // Must delete record in parent table last.
            // All other dependents have been deleted before.
            if (_extends != null) {
                _extends.delete(conn, identity);
            }
        } catch (SQLException except) {
            LOG.fatal(Messages.format("jdo.deleteFatal", _type, _sqlRemove), except);
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                LOG.warn("Problem closing JDBC statement", e);
            }
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
     * @param conn An open connection
     * @param fields An Object[] to load field values into
     * @param identity Identity of the object to load.
     * @param accessMode The access mode (null equals shared)
     * @return The object's stamp, or null
     * @throws ObjectNotFoundException The object was not found in persistent storage
     * @throws PersistenceException A persistence error occured
     */
    public Object load(final Object conn, final ProposedObject proposedObject,
                       final Object identity, final AccessMode accessMode)
    throws PersistenceException {
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;
        Object            stamp = null;
        boolean           notNull;

        Object[] fields = proposedObject.getFields();
        
        try {
            String sqlString = (accessMode == AccessMode.DbLocked) ? _sqlLoadLock : _sqlLoad; 
            stmt = ((Connection) conn).prepareStatement(sqlString);
                        
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format("jdo.loading", _clsDesc.getJavaClass().getName(), stmt.toString()));
            }
            
            int fieldIndex = 1;
            // bind the identity of the preparedStatement
            if (identity instanceof Complex) {
                Complex id = (Complex) identity;
                if ((id.size() != _ids.length) || (_ids.length <= 1)) {
                    throw new PersistenceException("Size of complex field mismatched! expected: " + _ids.length + " found: " + id.size());
                }

                for (int i = 0; i < _ids.length; i++) {
                    stmt.setObject(fieldIndex++, idToSQL(i, id.get(i)));
                }
            } else {
                if (_ids.length != 1) {
                    throw new PersistenceException("Complex field expected!");
                }
                stmt.setObject(fieldIndex++, idToSQL(0, identity));
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format( "jdo.loading", _clsDesc.getJavaClass().getName(), stmt.toString()));
            }

            // execute the SQL query 
            rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new ObjectNotFoundException(Messages.format("persist.objectNotFound", _clsDesc.getJavaClass().getName(), identity));
            }

            if (_extendingClassDescriptors.size() > 0) {
                Object[] returnValues = 
                    SQLHelper.calculateNumberOfFields(_extendingClassDescriptors, 
                            _ids.length, _fields.length, _numberOfExtendLevels, rs);
                JDOClassDescriptor potentialLeafDescriptor = (JDOClassDescriptor) returnValues[0];
            	
                if ((potentialLeafDescriptor != null)
                        && !potentialLeafDescriptor.getJavaClass().getName().equals(getDescriptor().getJavaClass().getName())) {
                    
                    Object[] expandedFields = new Object[potentialLeafDescriptor.getFields().length];
                    
                    fields = expandedFields;
                    proposedObject.setFields(expandedFields);
                    proposedObject.setActualClass(potentialLeafDescriptor.getJavaClass());
                    proposedObject.setExpanded(true);
                }

            	return null;
            }
            
            // Load all the fields of the object including one-one relations
            // index to use during ResultSet.getXXX(); don't forget to ignore 
            // the identity columns
            int columnIndex = _ids.length + 1;
            
            // index in fields[] for storing result of SQLTypes.getObject()
            fieldIndex = 1;
            String tableName = null;
            String tableNameOld = tableName;
            Object[] temp = new Object[10]; // assume complex field max at 10
            for (int i = 0 ; i < _fields.length ; ++i) {
            	tableName = _fields[i].getTableName();
            	if ((i > 0) && !tableName.equals(tableNameOld) && !_fields[i].isJoined()) {
            		columnIndex = columnIndex + _ids.length; 
            	}
            	
                if (!_fields[i].isMulti()) {
                    notNull = false;
                    if (_fields[i].getColumnInfo().length == 1) {
                        fields[i] = toJava(i, 0, SQLTypeInfos.getValue(rs, columnIndex++, _fields[i].getColumnInfo()[0].getSqlType()));
                        fieldIndex++;
                    } else {
                        for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                            temp[j] = toJava(i, j, SQLTypeInfos.getValue(rs, columnIndex++, _fields[i].getColumnInfo()[j].getSqlType()));
                            fieldIndex++;
                            if (temp[j] != null) {
                                notNull = true;
                            }
                        }
                        if (notNull) {
                            fields[i] = new Complex(_fields[i].getColumnInfo().length, temp);
                        } else {
                            fields[i] = null;
                        }
                    }
                } else {
                    ArrayList res = new ArrayList();
                    notNull = false;
                    for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                        temp[j] = toJava(i, j, SQLTypeInfos.getValue(rs, columnIndex, _fields[i].getColumnInfo()[j].getSqlType()));
                        if (temp[j] != null) {
                            notNull = true;
                        }
                        fieldIndex++;
                        columnIndex++;
                    }
                    if (notNull) {
                        if (_fields[i].getColumnInfo().length == 1) {
                            res.add( temp[0] );
                        } else {
                            res.add(new Complex(_fields[i].getColumnInfo().length, temp));
                        }
                    }
                    fields[i] = res;
                }
                
                tableNameOld = tableName;
            }

            while (rs.next()) {
                fieldIndex = 1;
                columnIndex = _ids.length + 1;

                tableName = null;
                tableNameOld = tableName;

                for (int i = 0; i < _fields.length ; ++i) {
                	tableName = _fields[i].getTableName();
                	if ((i > 0) && !tableName.equals(tableNameOld) && !_fields[i].isJoined()) {
                	    columnIndex = columnIndex + _ids.length;
                	}
                	
                    if (_fields[i].isMulti()) {
                        ArrayList res = (ArrayList) fields[i];
                        notNull = false;
                        for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                            temp[j] = toJava(i, j, SQLTypeInfos.getValue(rs, columnIndex, _fields[i].getColumnInfo()[j].getSqlType()));
                            if (temp[j] != null) { notNull = true; }
                            columnIndex++;
                        }
                        fieldIndex++;
                        if (notNull) {
                            if (_fields[i].getColumnInfo().length == 1) {
                                if (!res.contains(temp[0])) {
                                    res.add(temp[0]);
                                }
                            } else {
                                Complex com = new Complex(_fields[i].getColumnInfo().length, temp);
                                if (!res.contains(com)) {
                                    res.add(new Complex(_fields[i].getColumnInfo().length, temp));
                                }
                            }
                        }
                    } else {
                        fieldIndex++;
                        columnIndex += _fields[i].getColumnInfo().length;
                    }
                    tableNameOld = tableName;
                }
                
                proposedObject.setFields(fields);
            }
        } catch (SQLException except) {
            LOG.fatal(Messages.format("jdo.loadFatal", _type, (accessMode == AccessMode.DbLocked) ? _sqlLoadLock : _sqlLoad), except);
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        } finally {
            JDOUtils.closeResultSet(rs);
            JDOUtils.closeStatement(stmt);
        }
        return stamp;
    }
    
    private void buildSqlCreate() throws QueryException {
        StringBuffer sql;
        int count;
        boolean keyGened = false;
        
        String tableName = _mapTo;
        
        // Create statement to insert a new row into the table
        // using the specified primary key if one is required
        sql = new StringBuffer("INSERT INTO ");
        sql.append(_factory.quoteName(tableName)).append(" (");
        count = 0;
        for (int i = 0; i < _ids.length; i++) {
            if ((_keyGen == null) || (_keyGen.getStyle() == KeyGenerator.BEFORE_INSERT)) {
                if (count > 0) { sql.append(','); }
                keyGened = true;
                sql.append(_factory.quoteName(_ids[i].getName()));
                ++count;
            }
        }
        for (int i = 0 ; i < _fields.length; ++i) {
            if (_fields[i].isStore()) {
                for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                    if (count > 0) {
                        sql.append(',');
                    }
                    sql.append(_factory.quoteName(_fields[i].getColumnInfo()[j].getName()));
                    ++count;
                }
            }
        }
        // it is possible to have no fields in INSERT statement:
        // only the primary key field in the table,
        // with KeyGenerator DURING_INSERT or BEFORE_INSERT
        if (count == 0) {
            sql.setLength(sql.length() - 2); // cut " ("
        } else {
            sql.append(")");
        }
        sql.append(" VALUES (");
        for (int i = 0 ; i < count; ++i) {
            if (i > 0) { sql.append(','); }
            sql.append('?');
        }
        sql.append(')');
        _sqlCreate = sql.toString();

        if (!keyGened) {
            try {
                _sqlCreate = _keyGen.patchSQL(_sqlCreate, _ids[0].getName() /*primKeyName*/ );
            } catch (MappingException except)  {
                LOG.fatal(except);
                // proceed without this stupid key generator
                _keyGen = null;
                buildSqlCreate();
                return;
            }
            if (_keyGen.getStyle() == KeyGenerator.DURING_INSERT) {
                _sqlCreate = "{call " + _sqlCreate + "}";
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.format("jdo.creating", _type, _sqlCreate));
        }
    }

    private void buildSqlUpdate() {
        StringBuffer         sql;
        int                  count;
        
        // append the SET clause only if there are any fields that need to be persisted.
        if (_hasFieldsToPersist) {
            sql = new StringBuffer("UPDATE ");
            sql.append(_factory.quoteName(_mapTo));
            sql.append(" SET ");
            
            count = 0;
            for (int i = 0 ; i < _fields.length ; ++i) {
                if (_fields[ i ].isStore()) {
                    for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                        if (count > 0) { sql.append(','); }
                        sql.append(_factory.quoteName(_fields[i].getColumnInfo()[j].getName()));
                        sql.append("=?");
                        ++count;
                    }
                }
            }
            
            sql.append(buildWherePK());
            _sqlStore = sql.toString();

            for (int i = 0 ; i < _fields.length ; ++i) {
                if (_fields[i].isStore() && _fields[i].isDirtyCheck()) {
                    for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                        sql.append(" AND ");
                        sql.append(_factory.quoteName(_fields[i].getColumnInfo()[j].getName()));
                        sql.append("=?");
                    }
                }
            }
            _sqlStoreDirty = sql.toString();
            
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.format("jdo.updating", _type, _sqlStoreDirty));
            }
        } 
    }
    
    private void buildSqlRemove() {
        StringBuffer         sql;

        String tableName = _mapTo;
        
        sql = new StringBuffer("DELETE FROM ").append(_factory.quoteName(tableName));
        sql.append(buildWherePK());
        _sqlRemove = sql.toString();
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.format("jdo.removing", _type, _sqlRemove));
        }
    }
    
    private String buildWherePK() {
        // create sql statements
        StringBuffer sb = new StringBuffer();
        sb.append(JDBCSyntax.Where);
        for (int i = 0; i < _ids.length; i++) {
            if (i > 0) { sb.append(" AND "); }
            sb.append(_factory.quoteName(_ids[i].getName()));
            sb.append(QueryExpression.OpEquals);
            sb.append(JDBCSyntax.Parameter);
        }
        return sb.toString();
    }
    
    private void buildSqlPKLookup() throws QueryException {
        String tableName = _mapTo;
        QueryExpression query = _factory.getQueryExpression();

        // initalize lookup query
        for (int i = 0; i < _ids.length; i++) {
            query.addParameter(tableName, _ids[i].getName(), QueryExpression.OpEquals);
        }
        _pkLookup = query.getStatement(true);
    }
    
    private void buildFinder(final JDOClassDescriptor clsDesc) throws QueryException {
        QueryExpression expr = _factory.getQueryExpression();
        QueryExpression find = _factory.getQueryExpression();

        Map identitiesUsedForTable = new HashMap();

        // get id columns' names
        for (int i = 0; i < _ids.length; i++) {
            expr.addParameter(_mapTo, _ids[i].getName(), QueryExpression.OpEquals);
        }

        // join all the extended table
        JDOClassDescriptor curDesc = clsDesc;
        JDOClassDescriptor baseDesc;
        while (curDesc.getExtends() != null) {
            baseDesc = (JDOClassDescriptor) curDesc.getExtends();
            expr.addInnerJoin(curDesc.getTableName(), curDesc.getIdentityColumnNames(),
                              baseDesc.getTableName(), baseDesc.getIdentityColumnNames());
            find.addInnerJoin(curDesc.getTableName(), curDesc.getIdentityColumnNames(),
                              baseDesc.getTableName(), baseDesc.getIdentityColumnNames());
            curDesc = baseDesc;
        }
        
        // join all the related/depended table
        Vector joinTables = new Vector();
        String aliasOld = null;
        String alias = null;
        
        for (int i = 0; i < _fields.length; i++) {
        	if (i > 0) { aliasOld = alias; }
            alias = _fields[i].getTableName();

            // add id fields for root table if first field points to a separate table
            if ((i == 0) && _fields[i].isJoined()) {
                String[] ids = _clsDesc.getIdentityColumnNames();
                for (int j = 0; j < ids.length; j++) {
                    expr.addColumn(curDesc.getTableName(), ids[j]);
                    find.addColumn(curDesc.getTableName(), ids[j]);
                }
                identitiesUsedForTable.put(curDesc.getTableName(), new Boolean(true));
            }
            
            // add id columns to select statement
            // TODO does not work if first (non-identity) field mapping is not a plain field
            if (!alias.equals(aliasOld) && !_fields[i].isJoined()) {
                JDOClassDescriptor classDescriptor = (JDOClassDescriptor) 
                    _fields[i].getFieldDescriptor().getContainingClassDescriptor();
                String[] ids = classDescriptor.getIdentityColumnNames();
            	for (int j = 0; j < ids.length; j++) {
                    boolean isTableNameAlreadyAdded = identitiesUsedForTable.containsKey(classDescriptor.getTableName()); 
                    if (!isTableNameAlreadyAdded) {
                        expr.addColumn(alias, ids[j]);
                        find.addColumn(alias, ids[j]);
                    }
            	}
            }

            if (_fields[i].isJoined()) {
                int offset = 0;
                String[] rightCol = _fields[i].getJoinFields();
                String[] leftCol = new String[_ids.length - offset];
                for (int j = 0; j < leftCol.length; j++) {
                    leftCol[j] = _ids[j + offset].getName();
                }
                if (joinTables.contains(_fields[i].getTableName())
                        || clsDesc.getTableName().equals(_fields[i].getTableName())) {
                    
                    alias = alias.replace('.', '_') + "_f" + i; // should not mix with aliases in ParseTreeWalker
                    expr.addOuterJoin(_mapTo, leftCol, _fields[i].getTableName(), rightCol, alias);
                    find.addOuterJoin(_mapTo, leftCol, _fields[i].getTableName(), rightCol, alias);
                } else {
                    expr.addOuterJoin(_mapTo, leftCol, _fields[i].getTableName(), rightCol);
                    find.addOuterJoin(_mapTo, leftCol, _fields[i].getTableName(), rightCol);
                    joinTables.add(_fields[i].getTableName());
                }
            }

            for (int j = 0; j < _fields[i].getColumnInfo().length; j++) {
                expr.addColumn(alias, _fields[i].getColumnInfo()[j].getName());
                find.addColumn(alias, _fields[i].getColumnInfo()[j].getName());
            }
            
            expr.addTable(_fields[i].getTableName(), alias);
            find.addTable(_fields[i].getTableName(), alias);
        }

        // 'join' all the extending tables 
        curDesc = clsDesc;
        List classDescriptorsToAdd = new LinkedList();
        JDOClassDescriptor classDescriptor = null;
        SQLHelper.addExtendingClassDescriptors(classDescriptorsToAdd, curDesc.getExtendedBy());
        
        if (classDescriptorsToAdd.size() > 0) {
        	for (Iterator iter = classDescriptorsToAdd.iterator(); iter.hasNext(); ) {
        		classDescriptor = (JDOClassDescriptor) iter.next();
        		
        		if (LOG.isDebugEnabled()) {
                    LOG.debug("Adding outer left join for "
                            + classDescriptor.getJavaClass().getName() + " on table "
                            + classDescriptor.getTableName());
                }
                
                expr.addOuterJoin(_mapTo, curDesc.getIdentityColumnNames(), 
                        classDescriptor.getTableName(), classDescriptor.getIdentityColumnNames());
                find.addOuterJoin(_mapTo, curDesc.getIdentityColumnNames(), 
                		classDescriptor.getTableName(), classDescriptor.getIdentityColumnNames());

                Persistence persistenceEngine;
				try {
					persistenceEngine = _factory.getPersistence(classDescriptor);
				} catch (MappingException e) {
					throw new QueryException("Problem obtaining persistence engine for ClassDescriptor " + classDescriptor.getJavaClass().getName(), e);
				}

				SQLColumnInfo[] idInfos = ((SQLEngine) persistenceEngine)._ids;
                for (int i = 0; i < idInfos.length; i++) {
                	expr.addColumn (classDescriptor.getTableName(), idInfos[i].getName());
                	find.addColumn (classDescriptor.getTableName(), idInfos[i].getName());
                }
                
                SQLFieldInfo[] fieldInfos = ((SQLEngine) persistenceEngine)._fields;
                for (int i = 0; i < fieldInfos.length; i++) {
                	boolean hasFieldToAdd = false;
                	SQLColumnInfo[] columnInfos = fieldInfos[i].getColumnInfo();
                	if (classDescriptor.getTableName().equals(fieldInfos[i].getTableName())) {
                		for (int j = 0; j < columnInfos.length; j++) {
                    		expr.addColumn(classDescriptor.getTableName(), fieldInfos[i].getColumnInfo()[j].getName());
                    		find.addColumn(classDescriptor.getTableName(), fieldInfos[i].getColumnInfo()[j].getName());
                		}
                		hasFieldToAdd = true;
                	}
                    
                    if (hasFieldToAdd) {
                    	expr.addTable(classDescriptor.getTableName());
                    	find.addTable(classDescriptor.getTableName());
                    }
                }
        	}
        }
        
        // add table information if the class in question does not have any non-identity 
        // fields
        if (_fields.length == 0) {
            for (int i = 0; i < _ids.length; i++) {
                find.addColumn(_mapTo, _ids[i].getName());
            }
        }

        _sqlLoad = expr.getStatement(false);
        _sqlLoadLock = expr.getStatement(true);

        _sqlFinder = find;

        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.format("jdo.loading", _type, _sqlLoad));
            LOG.debug(Messages.format("jdo.loading.with.lock", _type, _sqlLoadLock));
            LOG.debug(Messages.format("jdo.finding", _type, _sqlFinder));
        }
    }
    
    public String toString() { return _clsDesc.toString(); }
}
