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

import java.util.HashMap;
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
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.persist.types.Complex;
import org.exolab.castor.persist.types.SQLTypes;
import org.exolab.castor.persist.Entity;
import org.exolab.castor.persist.EntityInfo;
import org.exolab.castor.persist.EntityFieldInfo;
import org.exolab.castor.persist.LockEngine;
import org.exolab.castor.persist.TransactionContext;
import org.exolab.castor.persist.TransactionContextListener;
import org.exolab.castor.util.Messages;

/**
 * A class to create and execute SQL queries of all kinds (INSERT, UPDATE, DELECT, SELECT)
 * for SQLEngine.
 * It operates it terms of EntityInfo, EntityFieldInfo, Entity.
 *
 * @author <a href="mailto:on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 */
public class SQLQueryExecutor implements TransactionContextListener
{

    private final static boolean DEBUG = true;


    /**
     * The HashMap maps TransactionContext to batch PreparedStatements, which are executed at the end
     * of the transactions.
     * We assume that one TransactionContext uses one Connection,
     * otherwise the key for the HashMap would be a pair of TransactionContext and Connection.
     */
    private HashMap _batchStmt;

    /**
     * The HashMap maps TransactionContext to ordinary PreparedStatements, which are executed immediately.
     * Thus we prepare each statement only once during transaction.
     * We assume that one TransactionContext uses one Connection,
     * otherwise the key for the HashMap would be a pair of TransactionContext and Connection.
     */
    private HashMap _ordinaryStmt;


    private LockEngine _lockEngine;

    /**
     * Is it a SELECT statement?
     */
    private final boolean _isSelect;

    /**
     * We can use batch for UPDATE and DELETE statements only.
     */
    private final boolean _canUseBatch;

    /**
     * The SQL statement to execute
     */
    private final String _sql;

    /**
     * The set of fields of the first Entity to bind as a query parameter (may be null).
     * These fields are bound before identity.
     */
    private final SQLFieldInfo[] _in;

    /**
     * The set of fields of the identity.
     */
    private final SQLFieldInfo[] _id;

    /**
     * The set of fields of the second Entity to bind as a query parameter (may be null).
     * These fields are bound after identity.
     */
    private final SQLFieldInfo[] _in2;

    /**
     * The set of fields to read from the result set (may be null).
     */
    private final SQLFieldInfo[] _out;


    /**
     * @param SQL statement
     * @param in The set of fields of the first Entity to bind as a query parameter (may be null).
     *           These fields are bound before identity.
     * @param in2 The set of fields of the second Entity to bind as a query parameter (may be null).
     *           These fields are bound after identity.
     * @param out The set of fields to read from the result set (may be null).
     *            The two-dimensional array here has the following meaning: the first index means a sequence
     *            of fields in the result set, the second index means the path from the main Entity
     *            to the target related Entity, the last element in the path describes the value field.
     */
    public SQLQueryExecutor(LockEngine lockEngine, String sql, EntityFieldInfo[] in, EntityFieldInfo[] id,
                            EntityFieldInfo[] in2, EntityFieldInfo[][] out)
            throws MappingException {
        _lockEngine = lockEngine;
        _sql = sql;
        _isSelect = _sql.startsWith(JDBCSyntax.Select);
        _canUseBatch = (_sql.startsWith(JDBCSyntax.Update) || _sql.startsWith(JDBCSyntax.Delete));
        _in = SQLFieldInfo.newArray(in);
        _id = SQLFieldInfo.newArray(id);
        _in2 = SQLFieldInfo.newArray(in2);
        _out = SQLFieldInfo.newPathArray(out);
    }

    /**
     * @param old is used for UPDATE only.
     */
    public void execute(TransactionContext tx, Connection conn, Entity entity, Entity entity2)
            throws PersistenceException {
        boolean useBatch;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count;

        try {
            useBatch = (_canUseBatch && conn.getMetaData().supportsBatchUpdates());

            // prepare statement
            if (useBatch) {
                if (_batchStmt == null) {
                    _batchStmt = new HashMap();
                } else {
                    stmt = (PreparedStatement) _batchStmt.get(tx);
                }
                if (stmt == null) {
                    stmt = conn.prepareStatement(_sql);
                    _batchStmt.put(tx, stmt);
                    // TODO: uncomment when this method becomes available
                    //tx.addListener(this);
                }
            } else {
                if (_ordinaryStmt == null) {
                    _ordinaryStmt = new HashMap();
                } else {
                    stmt = (PreparedStatement) _ordinaryStmt.get(tx);
                }
                if (stmt == null) {
                    stmt = conn.prepareStatement(_sql);
                    _ordinaryStmt.put(tx, stmt);
                }
            }

            // bind parameters
            count = 1;
            if (_in != null) {
                for (int i = 0; i < _in.length; i++) {
                    count = bindEntityField(entity, stmt, count, _in[i]);
                }
            }
            if (entity.identity != null) {
                bindIdentity(entity.identity, stmt, count, _id);
            }
            if (_in2 != null && entity2 != null) {
                for (int i = 0; i < _in2.length; i++) {
                    count = bindEntityField(entity2, stmt, count, _in2[i]);
                }
            }

            // execute query
            if (_isSelect) {
                rs = stmt.executeQuery();
            } else {
                if (useBatch) {
                    stmt.addBatch();
                } else {
                    try {
                        if (stmt.executeUpdate() <= 0) {
                            throwUpdateException(tx, conn, entity, null);
                        }
                    } catch (SQLException except) {
                        throwUpdateException(tx, conn, entity, except);
                    }
                }
            }
            if (_out != null && rs != null) {
                while (rs.next()) {
                    count = 1;
                    for (int i = 0; i < _out.length; i++) {
                        count = readEntityField(entity, rs, count, _out[i]);
                    }
                }
            }
        } catch (SQLException except) {
            throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
    }

    private void throwUpdateException(TransactionContext tx, Connection conn, Entity entity, SQLException except)
            throws PersistenceException {
        boolean entityExists = false;

        // TODO: lookup database record by entity.identity
        if (entityExists) {
            if (_sql.startsWith(JDBCSyntax.Insert)) {
                throw new DuplicateIdentityException(Messages.format("persist.duplicateIdentity", entity.info.entityClass, entity.identity));
            } else {
                throw new ObjectModifiedException(Messages.format("persist.objectModified", entity.info.entityClass, entity.identity));
            }
        } else {
            if (_sql.startsWith(JDBCSyntax.Insert)) {
                throw new PersistenceException(Messages.format("persist.nested", except), except);
            } else {
                throw new ObjectDeletedException(Messages.format("persist.objectDeleted", entity.info.entityClass, entity.identity));
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
     * Reads field value from the given ResultSet starting from the given index into the given Entity.
     * @param count starting index.
     * @param rs The result set.
     * @param info SQLFieldInfo decribing the field.
     * @param entity The entity to put the value to.
     * @return next index
     */
    private int readEntityField(Entity entity, ResultSet rs, int count, SQLFieldInfo fldInfo)
            throws PersistenceException {
        int sub = -1;
        String fldClass = fldInfo.info.entityClass.entityClass;
        Object value;

        // TODO: to read value
        value = null;

        for (int i = 0; i < entity.values.length; i++) {
            if (entity.values[i].entityClass.equals(fldClass)) {
                sub = i;
                break;
            }
        }
        if (sub >= 0) {
            for (int i = 0; i < entity.values[sub].fieldInfo.length; i++) {
                if (entity.values[sub].fieldInfo.equals(fldInfo.info)) {
                    entity.values[sub].values[i] = value;
                    return count;
                }
            }
        }
        throw new IllegalStateException("Field " + fldInfo + " not fount in the Entity " + entity);
    }

    /**
     * Binds field value to the given PreparedStatement starting from the given index.
     * @param count starting index.
     * @param stmt The statement.
     * @param info EntityFieldInfo decribing the field.
     * @param entity The entity to extract the value from.
     * @return next index
     */
    private int bindEntityField(Entity entity, PreparedStatement stmt, int count, SQLFieldInfo fldInfo)
            throws PersistenceException {
        int sub = -1;
        String fldClass = fldInfo.info.entityClass.entityClass;

        for (int i = 0; i < entity.values.length; i++) {
            if (entity.values[i].entityClass.equals(fldClass)) {
                sub = i;
                break;
            }
        }
        if (sub >= 0) {
            for (int i = 0; i < entity.values[sub].fieldInfo.length; i++) {
                if (entity.values[sub].fieldInfo.equals(fldInfo.info)) {
                    return bindField(entity.values[sub].values[i], stmt, count, fldInfo);
                }
            }
        }
        throw new IllegalStateException("Field " + fldInfo + " not fount in the Entity " + entity);
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



    public void txStarted(TransactionContext tx) {
    }

    public void txClosed(TransactionContext tx) {
        PreparedStatement stmt;

        // TODO: uncomment when this method becomes available
        //tx.removeListener(this);
        stmt = (PreparedStatement) _ordinaryStmt.remove(tx);
        try {
            stmt.close();
        } catch(SQLException except) {
            // TODO: uncomment when possible to throw exceptions
            // throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
        if (!_canUseBatch || stmt != null) {
            return;
        }
        stmt = (PreparedStatement) _batchStmt.remove(tx);
        if (stmt == null) {
            throw new IllegalStateException("The statement for the transaction not found");
        }
        try {
            stmt.executeBatch();
            stmt.close();
        } catch(SQLException except) {
            // TODO: uncomment when possible to throw exceptions
            // throw new PersistenceException(Messages.format("persist.nested", except), except);
        }
    }

    public void txPrepared(TransactionContext tx) {
    }

    public void txCompleted(TransactionContext tx, int status) {
    }
}
