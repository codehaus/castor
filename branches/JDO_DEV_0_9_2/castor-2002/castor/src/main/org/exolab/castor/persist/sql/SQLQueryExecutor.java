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

import java.util.Set;
import java.util.HashMap;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.exolab.castor.jdo.QueryResults;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectDeletedException;
import org.exolab.castor.jdo.ObjectModifiedException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.engine.DatabaseImpl;
import org.exolab.castor.mapping.TypeConvertor;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.Entity;
import org.exolab.castor.persist.EntityInfo;
import org.exolab.castor.persist.EntityFieldInfo;
import org.exolab.castor.persist.LockEngine;
import org.exolab.castor.persist.LogInterceptor;
import org.exolab.castor.persist.Key;
import org.exolab.castor.persist.Relation;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.TransactionContextListener;
import org.exolab.castor.persist.spi.Connector;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.persist.types.Complex;
import org.exolab.castor.persist.types.SQLTypes;
import org.exolab.castor.util.Messages;

/**
 * A class to execute SQL queries of all kinds (LOOKUP, INSERT, UPDATE, DELETE, SELECT)
 * for SQLEngine.
 * It works it terms of EntityInfo, EntityFieldInfo, Entity.
 *
 * @author <a href="mailto:on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 * @see SQLQueryBuilder
 */
public class SQLQueryExecutor implements SQLConnector.ConnectorListener, SQLQueryKinds {

    /**
     * The maximum depth of the inheritance tree
     */
    private final static int MAX_DEPTH = 256;

    /**
     * The maximum number of SQL columns that a complex field (foreign key or identity) consists of.
     */
    private final static int MAX_COMPLEX = 256;

    private final static boolean DEBUG = true;

    /**
     * The kind of the SQL statement: LOOKUP (select without loading), SELECT, INSERT, UPDATE or DELETE
     */
    private final byte _kind;

    /**
     * Does the query checks for dirtiness (only UPDATE or DELETE)?
     */
    private final boolean _checkDirty;

    /**
     * The HashMap maps Key to batch PreparedStatements, which are executed at the end
     * of the transactions.
     * We assume that one Key uses one Connection,
     * otherwise the key for the HashMap would be a pair of Key and Connection.
     */
    private HashMap _batchStmt;

    /**
     * The HashMap maps Key to ordinary PreparedStatements, which are executed immediately.
     * Thus we prepare each statement only once during transaction.
     * We assume that one Key uses one Connection,
     * otherwise the key for the HashMap would be a pair of Key and Connection.
     */
    private HashMap _ordinaryStmt;


    private final SQLEntityInfo _info;

    private final SQLConnector _connector;

    private final BaseFactory _factory;

    /**
     * We can use batch for UPDATE and DELETE statements only.
     */
    private final boolean _canUseBatch;

    /**
     * The SQL statement to execute
     */
    private final String _sql;

    /**
     * The key generator, is used for INSERT statements only.
     */
    private final KeyGenerator _keyGen;

    /**
     * The log interceptor
     */
    private final LogInterceptor _logInterceptor;

    /**
     * @param factory The persistence factory.
     * @param connector The SQL connection to add listener.
     * @param log The log interceptor
     * @param info The description of the main Entity of this query.
     * @param sql The SQL statement to execute.
     * @param kind The kind of the SQL statement: LOOKUP (select without loading), SELECT, INSERT, UPDATE or DELETE
     * @param checkDirty Does the query checks for dirtiness (only UPDATE or DELETE)?
     * @param keyGen The key generator, is used for INSERT statements only.
     */
    SQLQueryExecutor(BaseFactory factory, SQLConnector connector, LogInterceptor log,
                     SQLEntityInfo info, String sql, byte kind, boolean checkDirty,
                     KeyGenerator keyGen)
            throws MappingException {
        _factory = factory;
        _connector = connector;
        _logInterceptor = log;
        _sql = sql;
        _info = info;
        _kind = kind;
        _checkDirty = checkDirty;
        _canUseBatch = (_kind == UPDATE || _kind == DELETE);
        _keyGen = keyGen;
    }

    /**
     * This method is used by SQLEngine in create(), update(), delete() and load()
     * when dirty checking is not performed.
     * @param lockEngine The lock engine, used to addCache and addRelated.
     * @param entity The main Entity (input or output).
     */
    public void executeEntity(Key key, LockEngine lockEngine, Connection conn, Entity entity)
            throws PersistenceException, MappingException {
        executeEntity(key, lockEngine, conn, entity, null);
    }

    /**
     * This method is used by SQLEngine in create(), update(), delete() and load().
     * @param lockEngine The lock engine, used to addCache and addRelated.
     * @param entity The main Entity (input or output).
     * @param entity2 An additional input Entity, it is used for UPDATE and DELETE with dirty checking.
     */
    public void executeEntity(Key key, LockEngine lockEngine, Connection conn, Entity entity, Entity entity2)
            throws PersistenceException, MappingException {
        boolean useBatch;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count;
        HashMap entityMap; // additional loaded entities, maps EntityKey -> Entity
        Entity tmpEntity;
        EntityKey entityKey;
        String[] entityClasses;
        Object[][] values;
        SQLEntityInfo subInfo;
        Object[] temp = new Object[MAX_COMPLEX];
        Object[] temp2 = new Object[MAX_COMPLEX];
        int level;

        try {
            useBatch = (_canUseBatch && conn.getMetaData().supportsBatchUpdates());

            // prepare statement
            if (useBatch) {
                if (_batchStmt == null) {
                    _batchStmt = new HashMap();
                } else {
                    stmt = (PreparedStatement) _batchStmt.get(key);
                }
                if (stmt == null) {
                    stmt = conn.prepareStatement(_sql);
                    _batchStmt.put(key, stmt);
                    _connector.addListener(key, this);
                }
            } else {
                if (_ordinaryStmt == null) {
                    _ordinaryStmt = new HashMap();
                } else {
                    stmt = (PreparedStatement) _ordinaryStmt.get(key);
                }
                if (stmt == null) {
                    stmt = conn.prepareStatement(_sql);
                    _ordinaryStmt.put(key, stmt);
                }
            }

            // bind parameters
            count = 1;
            if (_kind == UPDATE || _kind == INSERT) {
                for (int sub = 0; sub < entity.entityClasses.length; sub++) {
                    if (!entity.entityClasses[sub].equals(_info.info.entityClass)) {
                        continue;
                    }
                    for (int i = 0; i < _info.fieldInfo.length; i++) {
                        if (_info.fieldInfo[i] != null) {
                            count = bindField(entity.values[sub][i], stmt, count, _info.fieldInfo[i]);
                        }
                    }
                    break;
                }
            }
            if (entity.identity != null) {
                bindIdentity(entity.identity, stmt, count, _info.idInfo);
            }
            // Dirty checking
            if ((_kind == UPDATE || _kind == DELETE) && _checkDirty) {
                for (int sub = 0; sub < entity2.entityClasses.length; sub++) {
                    if (!entity2.entityClasses[sub].equals(_info.info.entityClass)) {
                        continue;
                    }
                    for (int i = 0; i < _info.fieldInfo.length; i++) {
                        if (_info.fieldInfo[i] != null && _info.fieldInfo[i].info.dirtyCheck) {
                            count = bindField(entity2.values[sub][i], stmt, count, _info.fieldInfo[i]);
                        }
                    }
                    break;
                }
            }

            // execute the query
            if (_kind == SELECT || _kind == LOOKUP) {
                rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new ObjectNotFoundException( Messages.format("persist.objectNotFound", _info, entity.identity) );
                }
            } else {
                if (useBatch) {
                    stmt.addBatch();
                } else {
                    try {
                        if (stmt.executeUpdate() <= 0) {
                            throwUpdateException(key, lockEngine, conn, entity, null);
                        }
                    } catch (SQLException except) {
                        throwUpdateException(key, lockEngine, conn, entity, except);
                    }
                }
            }
            if (_kind != SELECT) {
                // If it is not SELECT, the task is done
                return;
            }

            // prepare the entity for loading
            if (_info.subEntities == null) {
                entityClasses = new String[1];
                values = new Object[1][];
            } else {
                entityClasses = new String[MAX_DEPTH];
                values = new Object[MAX_DEPTH][];
            }
            entityClasses[0] = _info.info.entityClass;
            values[0] = new Object[_info.fieldInfo.length];

            count = 1;
            // Skip identity fields
            count += _info.idNames.length;
            count = readEntity(values[0], rs, count, _info, temp);
            if (_info.subEntities == null) {
                entity.entityClasses = entityClasses;
                entity.values = values;
            } else {
                // Now load sub-entities
                level = loadSubEntityLevel(_info, 1, entityClasses, values, rs, count, temp, temp2);
                entity.entityClasses = new String[level];
                entity.values = new Object[level][];
                System.arraycopy(entityClasses, 0, entity.entityClasses, 0, level);
                System.arraycopy(values, 0, entity.values, 0, level);
            }
        } catch (SQLException except) {
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
    }

    /**
     * This method is used by SQLEngine in loadRelated().
     * @param relation The Relation to load
     */
    public void executeRelation(Key key, Connection conn, Relation relation)
            throws PersistenceException {
    }


    /**
     * Recursive method that loads sub-entities in depth-first order.
     * @return The number of the first null level.
     */
    private int loadSubEntityLevel(SQLEntityInfo info, int level, String[] entityClasses, Object[][] values,
                                   ResultSet rs, int count, Object[] temp, Object[] temp2)
            throws PersistenceException {
        SQLEntityInfo subInfo;

        /*
         * The tree of sub-entities must go in the depth-first order
         * The algorithm of loading:
         * 1) go in depth along the first branch until subEntity.id == null
         * 2) skip all sub-entities of the null branch
         * 3) go to the next subEntity on the same level as a null branch until subEntity.id != null
         * 4) if all sub-entities are null, then stop
         */
        for (int sub = 0; sub < info.subEntities.length; sub++) {
            subInfo = info.subEntities[sub];
            // The first field for each entity should be its identity
            if (readIdentity(rs, count, subInfo, temp, temp2) == null) {
                count += info.idNames.length;
                count = skipEntity(rs, count, subInfo);
            } else {
                entityClasses[level] = subInfo.info.entityClass;
                count = readEntity(values[level], rs, count, subInfo, temp);
                return loadSubEntityLevel(subInfo, level + 1, entityClasses, values, rs, count, temp, temp2);
            }
        }
        return level;
    }

    private void throwUpdateException(Key key, LockEngine lockEngine, Connection conn, Entity entity,
                                      SQLException except)
            throws PersistenceException, MappingException {
        SQLQueryExecutor lookup;

        lookup = SQLQueryBuilder.getInstance(_factory, _connector, _logInterceptor,
                                             entity.info, LOOKUP, false, false);
        try {
            lookup.executeEntity(key, lockEngine, conn, entity);

            // The entity exists in the database
            if (_kind == INSERT) {
                throw new DuplicateIdentityException(Messages.format("persist.duplicateIdentity", _info, entity.identity));
            } else {
                throw new ObjectModifiedException(Messages.format("persist.objectModified", _info, entity.identity));
            }
        } catch (ObjectNotFoundException exept) {
            // The entity doesn't exist in the database
            if (_kind == INSERT) {
                throw new PersistenceException(Messages.format("persist.nested", except), except);
            } else {
                throw new ObjectDeletedException(Messages.format("persist.objectDeleted", _info, entity.identity));
            }
        }
    }


    /**
     * Converts field value from Java type to SQL type.
     */
    private Object javaToSql(Object value, SQLFieldInfo fldInfo, int index)
            throws PersistenceException {
        if (value == null || fldInfo.javaToSql[index] == null) {
            return value;
        }
        return fldInfo.javaToSql[index].convert(value, fldInfo.info.typeParam[index]);
    }


    /**
     * Converts field value from SQL type to Java type.
     */
    private Object sqlToJava(Object value, SQLFieldInfo fldInfo, int index)
            throws PersistenceException {
        if (value == null || fldInfo.sqlToJava[index] == null) {
            return value;
        }
        return fldInfo.sqlToJava[index].convert(value, fldInfo.info.typeParam[index]);
    }

    /**
     * Reads all proprietary fields of the given EntityInfo from the given ResultSet.
     * @param values The array of values, only one of them with the given index is to be loaded.
     * @param rs The result set.
     * @param count starting index in the ResultSet.
     * @param info SQLFieldInfo decribing the field.
     * @param temp Auxilary array for reading Complex values.
     * @return next index in the ResultSet.
     */
    private int readEntity(Object[] values, ResultSet rs, int count, SQLEntityInfo info, Object[] temp)
            throws PersistenceException {
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                count = readEntityField(i, values, rs, count, info.fieldInfo[i], temp);
            }
        }
        return count;
    }

    /**
     * Skips Entity in the ResultSet. Also skips all sub-entities in depth-first order.
     * @param rs The result set.
     * @param count starting index in the ResultSet.
     * @param info SQLFieldInfo decribing the field.
     * @return next index in the ResultSet.
     */
    private int skipEntity(ResultSet rs, int count, SQLEntityInfo info)
            throws PersistenceException {
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                count += info.fieldInfo[i].sqlType.length;
            }
        }
        // now skip all sub-entities in depth-first order
        for (int sub = 0; sub < info.subEntities.length; sub++) {
            count = skipEntity(rs, count, info.subEntities[sub]);
        }
        return count;
    }

    /**
     * Reads the identity.
     */
    private Object readIdentity(ResultSet rs, int count, SQLEntityInfo info, Object[] temp, Object[] temp2)
            throws PersistenceException {
        boolean isNull;

        isNull = true;
        for (int i = 0; i < info.idInfo.length; i++) {
            count = readEntityField(i, temp2, rs, count, info.idInfo[i], temp);
            if (temp2[i] != null) {
                isNull = false;
            }
        }
        return (isNull ? null : (info.idInfo.length == 1 ? temp2[0] : new Complex(info.idInfo.length, temp2)));
    }


    /**
     * Reads field value from the ResultSet into the given position of the array of values.
     * @param index The index of the field in the array of values.
     * @param values The array of values, only one of them with the given index is to be loaded.
     * @param rs The result set.
     * @param count starting index in the result set.
     * @param fldInfo SQLFieldInfo decribing the field.
     * @param temp Auxilary array for reading Complex values.
     * @return next index in the ResultSet.
     */
    private int readEntityField(int index, Object[] values, ResultSet rs, int count, SQLFieldInfo fldInfo,
                                Object[] temp)
            throws PersistenceException {
        boolean isNull;

        isNull = true;
        for (int i = 0; i < fldInfo.sqlType.length; i++) {
            try {
                temp[i] = sqlToJava(SQLTypes.getObject(rs, count++, fldInfo.sqlType[i]), fldInfo, i);
            } catch (SQLException except) {
                throw new PersistenceException( Messages.format("persist.nested", except), except );
            }
            if (temp[i] != null) {
                isNull = false;
            }
        }
        if (isNull) {
            values[index] = null;
        } else {
            if (fldInfo.sqlType.length == 1) {
                values[index] = temp[0];
            } else {
                values[index] = new Complex(fldInfo.sqlType.length, temp);
            }
        }
        return count;
    }

    /**
     * Binds field value to the given PreparedStatement starting from the given index.
     * @param count starting index
     * @param stmt The statement
     * @param info SQLFieldInfo decribing the field
     * @param value The value of the field
     * @return next index
     */
    private int bindField(Object value, PreparedStatement stmt, int count, SQLFieldInfo fldInfo)
            throws PersistenceException {
        int len;

        len = fldInfo.sqlType.length;
        try {
            if (value == null) {
                for (int i=0; i < len; i++) {
                    stmt.setNull(count++, fldInfo.sqlType[i]);
                }
            } else {
                if (DEBUG) {
                    int valueLength = ((value instanceof Complex) ? ((Complex) value).size() : 1);
                    if (len != valueLength) {
                        throw new IllegalArgumentException("Size of field mismatched:" +
                                                        " actual = " + valueLength +
                                                        ", expected = " + len +
                                                        ", field = " + fldInfo);
                    }
                }

                if (len == 1) {
                    SQLTypes.setObject(stmt, count++, javaToSql(value, fldInfo, 0), fldInfo.sqlType[0]);
                } else {
                    Complex complex = (Complex) value;

                    for (int i=0; i < len; i++) {
                        SQLTypes.setObject(stmt, count++, javaToSql(complex.get(i), fldInfo, i),
                                        fldInfo.sqlType[i]);
                    }
                }
            }
        } catch (SQLException except) {
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
        return count;
    }


    /**
     * Binds identity values to the given PreparedStatement starting from the given index.
     * @param count starting index
     * @param stmt The statement
     * @param idInfo Array of EntityFieldInfo decribing the identity
     * @param identity The value of the identity
     * @return next index
     */
    private int bindIdentity(Object identity, PreparedStatement stmt, int count, SQLFieldInfo[] idInfo)
            throws PersistenceException {
        if (DEBUG) {
            int length = ((identity instanceof Complex) ? ((Complex) identity).size() : 1);
            if (idInfo.length != length) {
                throw new IllegalArgumentException("Size of identity mismatched:" +
                                                   " actual = " + length +
                                                   ", expected = " + idInfo.length +
                                                   ", field = " + idInfo[0]);
            }
        }

        if (idInfo.length == 1) {
            count = bindField(identity, stmt, count, idInfo[0]);
        } else {
            Complex complex = (Complex) identity;

            for (int i=0; i < idInfo.length; i++) {
                count = bindField(complex.get(i), stmt, count, idInfo[i]);
            }
        }
        return count;
    }

    /**
     * Implementation of SQLConnector.ConnectorListener
     */
    public void connectionPrepare( Key key )
            throws PersistenceException {
    }

    /**
     * Implementation of SQLConnector.ConnectorListener
     */
    public void connectionRelease( Key key )
            throws PersistenceException {

        PreparedStatement stmt;

        _connector.removeListener(key, this);
        stmt = (PreparedStatement) _ordinaryStmt.remove(key);
        try {
            stmt.close();
        } catch(SQLException except) {
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
        if (!_canUseBatch || stmt != null) {
            return;
        }
        stmt = (PreparedStatement) _batchStmt.remove(key);
        if (stmt == null) {
            throw new IllegalStateException("The statement for the transaction not found");
        }
        try {
            stmt.executeBatch();
            stmt.close();
        } catch(SQLException except) {
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
    }


    //------------------------------ Inner classes -------------------------------------------


    static class EntityKey {
        final EntityFieldInfo[] path;

        EntityKey(EntityFieldInfo[] path) {
            this.path = path;
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof EntityKey)) {
                return false;
            }
            return Arrays.equals(path, ((EntityKey) obj).path);
        }

        public int hashCode() {
            int hash = 0;

            for (int i = 0; i < path.length; i++) {
                hash += path[i].hashCode();
            }
            return hash;
        }
    }
}
