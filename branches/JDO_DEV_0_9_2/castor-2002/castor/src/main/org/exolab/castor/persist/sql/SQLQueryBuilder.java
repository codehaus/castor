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
 * A factory to build SQL queries of all kinds (LOOKUP, INSERT, UPDATE, DELETE, SELECT)
 * for SQLEngine.
 * It works it terms of EntityInfo, EntityFieldInfo, Entity.
 *
 * @author <a href="mailto:on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 * @see SQLQueryExecutor
 */
public class SQLQueryBuilder implements SQLQueryKinds {

    /**
     * The HashMap maps SQLQueryBuilder.ExecutorKey to SQLQueryExecutor.
     */
    private static HashMap _executors = new HashMap();


    /**
     * The factory method for creating instances of this class.
     */
    public static SQLQueryExecutor getExecutor(BaseFactory factory, SQLConnector connector, LogInterceptor log,
                                               EntityInfo info, byte kind, boolean dirtyCheck, boolean withLock)
            throws MappingException {
        SQLEntityInfo sqlInfo;
        QueryExpression query;
        SQLQueryExecutor executor;
        ExecutorKey key;
        String sql;
        KeyGenerator keyGen = null;

        key = new ExecutorKey(factory, connector, info.entityClass, kind, dirtyCheck, withLock);
        executor = (SQLQueryExecutor) _executors.get(key);
        sqlInfo = SQLEntityInfo.getInstance(info);
        if (executor == null) {
            if (kind == LOOKUP || kind == SELECT) {
               try {
                    query = factory.getQueryExpression();
                    buildLookup(query, sqlInfo);
                    if (kind == SELECT) {
                        buildSelect(query, sqlInfo);
                    }
                    sql = query.getStatement(withLock);
                    if (kind == SELECT && log != null) {
                        log.storeStatement("SQL for loading " + info + ":  " + sql);
                    }
                } catch (QueryException except) {
                    except.printStackTrace();
                    throw new MappingException( except );
                }
            } else {
                switch (kind) {
                case INSERT:
                    // Prepare key generator
                    // Assume that it generates the very first identity field.
                    if (info.keyGen != null) {
                        keyGen = info.keyGen.getKeyGeneratorRegistry().getKeyGenerator(
                                factory, info.keyGen, sqlInfo.idInfo[0].sqlType[0], log);
                    }
                    sql = buildInsert(factory, sqlInfo, keyGen);
                    if (log != null) {
                        log.storeStatement("SQL for creating " + info + ":  " + sql);
                    }
                    break;
                case UPDATE:
                    sql = buildUpdate(factory, sqlInfo, dirtyCheck);
                    if (log != null) {
                        log.storeStatement("SQL for updating " + info + ":  " + sql);
                    }
                    break;
                case DELETE:
                    sql = buildDelete(factory, sqlInfo, dirtyCheck);
                    if (log != null) {
                        log.storeStatement("SQL for deleting " + info + ":  " + sql);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown kind of SQL query: " + kind);
                }
            }
            executor = new SQLQueryExecutor(factory, connector, log, sqlInfo, sql, kind, dirtyCheck, keyGen);
            _executors.put(key, executor);
        }
        return executor;
    }

    private static void buildLookup(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        EntityFieldInfo idInfo;
        String entityClass;

        entityClass = info.info.entityClass;
        for (int i = 0; i < info.idInfo.length; i++) {
            idInfo = info.idInfo[i].info;
            for (int j = 0; j < idInfo.fieldNames.length; j++) {
                query.addParameter(entityClass, idInfo.fieldNames[j], QueryExpression.OpEquals);
            }
        }
    }

    private static void buildSelect(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        // super-entities must go before sub-entities
        for (int i = 0; i < info.superEntities.length - 1; i++) {
            buildSelectForOneEntity(query, info.superEntities[i]);
            query.addInnerJoin(info.superEntities[i].info.entityClass, info.superEntities[i].idNames,
                               info.superEntities[i + 1].info.entityClass, info.superEntities[i + 1].idNames);
        }
        buildSelectForSubEntities(query, info);
    }

    /**
     * Build SELECT for this entity and all sub-entities (without super-entities)
     */
    private static void buildSelectForSubEntities(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        buildSelectForOneEntity(query, info);
        if (info.subEntities != null) {
            for (int sub = 0; sub < info.subEntities.length; sub++) {
                query.addOuterJoin(info.info.entityClass, info.idNames,
                                   info.subEntities[sub].info.entityClass, info.subEntities[sub].idNames);
                buildSelectForSubEntities(query, info.subEntities[sub]);
            }
        }
    }

    /**
     * Build SELECT for this entity only
     */
    private static void buildSelectForOneEntity(QueryExpression query, SQLEntityInfo info)
            throws QueryException {
        String[] fieldNames;
        String entityClass;

        entityClass = info.info.entityClass;
        for (int i = 0; i < info.idNames.length; i++) {
            query.addColumn(entityClass, info.idNames[i]);
        }
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                fieldNames = info.fieldInfo[i].info.fieldNames;
                for (int j = 0; j < fieldNames.length; j++) {
                    query.addColumn(entityClass,  fieldNames[j]);
                }
            }
        }
    }

    private static String buildInsert(BaseFactory factory, SQLEntityInfo info, KeyGenerator keyGen)
            throws MappingException {
        int count;
        StringBuffer sql;
        String[] fieldNames;
        String result;

        sql = new StringBuffer(JDBCSyntax.Insert);
        sql.append(factory.quoteName(info.info.entityClass));
        sql.append(" (");
        count = 0;
        for (int i = 0; i < info.idNames.length; i++) {
            if (i == 0 && keyGen != null && keyGen.getStyle() != KeyGenerator.BEFORE_INSERT) {
                // The generated column is not known yet
                continue;
            }
            if (count > 0) {
                sql.append(',');
            }
            sql.append(factory.quoteName(info.idNames[i]));
            count++;
        }
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                fieldNames = info.fieldInfo[i].info.fieldNames;
                for (int j = 0; j < fieldNames.length; j++) {
                    if (count > 0) {
                        sql.append(',');
                    }
                    sql.append(factory.quoteName(fieldNames[j]));
                    count++;
                }
            }
        }
        // it is possible to have no fields in INSERT statement:
        // only the primary key field in the table,
        // with KeyGenerator DURING_INSERT or BEFORE_INSERT
        if (count == 0) {
            sql.setLength(sql.length() - 2); // cut " ("
        } else {
            sql.append(')');
        }
        sql.append(" VALUES (");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sql.append( ',' );
            }
            sql.append('?');
        }
        sql.append(')');
        result = sql.toString();

        if (keyGen != null && keyGen.getStyle() != KeyGenerator.BEFORE_INSERT) {
            result = keyGen.patchSQL(result, info.idNames[0]);
            if (keyGen.getStyle() == KeyGenerator.DURING_INSERT) {
                result = JDBCSyntax.Call + result + JDBCSyntax.EndCall;
            }
        }
        return result;
    }

    private static String buildUpdate(BaseFactory factory, SQLEntityInfo info, boolean dirtyCheck) {
        int count;
        StringBuffer sql;
        String[] fieldNames;

        sql = new StringBuffer(JDBCSyntax.Update);
        sql.append(factory.quoteName(info.info.entityClass));
        sql.append(JDBCSyntax.Set);
        count = 0;
        for (int i = 0; i < info.fieldInfo.length; i++) {
            if (info.fieldInfo[i] != null) {
                fieldNames = info.fieldInfo[i].info.fieldNames;
                for (int j = 0; j < fieldNames.length; j++) {
                    if (count > 0) {
                        sql.append(',');
                    }
                    sql.append(factory.quoteName(fieldNames[j]));
                    sql.append("=?");
                    count++;
                }
            }
        }
        addWhere(sql, factory, info, dirtyCheck);
        return sql.toString();
    }

    private static String buildDelete(BaseFactory factory, SQLEntityInfo info, boolean dirtyCheck) {
        StringBuffer sql;

        sql = new StringBuffer(JDBCSyntax.Delete);
        sql.append(factory.quoteName(info.info.entityClass));
        addWhere(sql, factory, info, dirtyCheck);
        return sql.toString();
    }

    private static void addWhere(StringBuffer sql, BaseFactory factory, SQLEntityInfo info, boolean dirtyCheck) {
        String[] fieldNames;

        sql.append(JDBCSyntax.Where);
        for (int i = 0; i < info.idNames.length; i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append(factory.quoteName(info.idNames[i]));
            sql.append("=?");
        }
        if (dirtyCheck) {
            for (int i = 0; i < info.fieldInfo.length; i++) {
                if (info.fieldInfo[i] != null && info.fieldInfo[i].info.dirtyCheck) {
                    fieldNames = info.fieldInfo[i].info.fieldNames;
                    for (int j = 0; j < fieldNames.length; j++) {
                        sql.append(" AND ");
                        sql.append(factory.quoteName(fieldNames[j]));
                        sql.append("=?");
                    }
                }
            }
        }
    }

    //------------------------------ Inner classes -------------------------------------------


    /**
     * The key for the HashMap of all instances of this class
     */
    static class ExecutorKey {

        final BaseFactory factory;

        final SQLConnector connector;

        final String entityClass;

        final byte kind;

        final boolean dirtyCheck;

        final boolean withLock;

        ExecutorKey(BaseFactory factory, SQLConnector connector, String entityClass, byte kind,
                    boolean dirtyCheck, boolean withLock) {
            this.factory = factory;
            this.connector = connector;
            this.entityClass = entityClass;
            this.kind = kind;
            this.dirtyCheck = dirtyCheck;
            this.withLock = withLock;
        }

        public int hashCode() {
            return entityClass.hashCode() + kind + (dirtyCheck ? 8 : 0) + (withLock ? 16 : 0);
        }

        public boolean equals(Object obj) {
            ExecutorKey key = (ExecutorKey) obj;

            return (entityClass.equals(key.entityClass) && (kind == key.kind) &&
                    (dirtyCheck == key.dirtyCheck) && (withLock == key.withLock)) &&
                    (factory == key.factory) && (connector == key.connector);
        }
    }
}
