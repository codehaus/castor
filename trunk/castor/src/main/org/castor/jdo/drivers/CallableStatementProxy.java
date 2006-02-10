/*
 * Copyright 2005 Werner Guttmann
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
 */
package org.castor.jdo.drivers;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.util.Configuration.Property;

/**
 * Proxy class for JDBC CallableStatement class, to allow information gathering
 * for the purpose of SQL statement logging.
 * @author <a href="werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @since 1.0M3
 */
public class CallableStatementProxy implements CallableStatement {

    /** Commons logger. */
    private static final Log log = LogFactory.getLog(CallableStatementProxy.class);

    /** Has property of LocalConfiguration been read? */
    private static boolean _isConfigured = false;

    /** Should connections been wrapped by a proxy? */
    private static boolean _useProxies = false;

    /** CallableStatement to be proxied. */
    private CallableStatement callableStatement;

    /** Connection instance associated with this CallableStatement */
    private Connection connection;

    /** SQL Parameter mapping */
    private Map parameters = new HashMap();

    /** The SQL statement to be executed  */
    private String sqlStatement = null;

    /** List of batch statements associated with this instance. */
    private List batchStatements = new ArrayList();

    /**
     * Factory method for creating a CallableStamentProxy
    * @param statement Callable statement to be proxied.
     * @param sql SQL string.
     * @param connection JDBC connection
     * @return Callable statement proxy.
     */
    public static CallableStatement newCallableStatementProxy(CallableStatement statement, String sql, Connection connection) {

        if (!_isConfigured) {
            String propertyValue = LocalConfiguration.getInstance().getProperty(
                Property.PROPERTY_USE_JDBC_PROXIES, "true");
            _useProxies = Boolean.valueOf(propertyValue).booleanValue();
            _isConfigured = true;
        }

        if (!_useProxies) {
            return statement;
        } else {
            return new CallableStatementProxy(statement, sql, connection);
        }
    }

    /**
     * Creates an instance of this class.
     * @param statement Callable statement to be proxied.
     * @param sql SQL string.
     * @param connection JDBC connection
     */
    private CallableStatementProxy(CallableStatement statement, String sql, Connection connection) {

        if (log.isDebugEnabled()) {
            log.debug("Creating callable statement proxy for SQL statement " + sql);
        }

        this.callableStatement = statement;
        this.sqlStatement = sql;
        this.connection = connection;
    }

    /**
     * @see PreparedStatement#addBatch()
     */
    public void addBatch() throws SQLException {
        callableStatement.addBatch();
    }

    /**
     * @see PreparedStatement#addBatch(String)
     */
    public void addBatch(String arg0) throws SQLException {
        batchStatements.add(arg0);
        callableStatement.addBatch(arg0);
    }

    /**
     * @see PreparedStatement#cancel()
     */
    public void cancel() throws SQLException {
        callableStatement.cancel();
    }

    /**
     * @see PreparedStatement#clearBatch()
     */
    public void clearBatch() throws SQLException {
        batchStatements.clear();
        callableStatement.clearBatch();
    }

    /**
     * @see PreparedStatement#clearParameters()
     */
    public void clearParameters() throws SQLException {
        parameters.clear();
        callableStatement.clearParameters();
    }

    /**
     * @see PreparedStatement#clearWarnings()
     */
    public void clearWarnings() throws SQLException {
        callableStatement.clearWarnings();
    }

    /**
     * @see PreparedStatement#close()
     */
    public void close() throws SQLException {
        callableStatement.close();
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        return callableStatement.equals(arg0);
    }

    /**
     * @see PreparedStatement#execute()
     */
    public boolean execute() throws SQLException {
        return callableStatement.execute();
    }


    /**
     * @param arg0
     * @return
     * @throws java.sql.SQLException
     */
    public boolean execute(String arg0) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.execute(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws java.sql.SQLException
     */
    public boolean execute(String arg0, int arg1) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.execute(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws java.sql.SQLException
     */
    public boolean execute(String arg0, int[] arg1) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.execute(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws java.sql.SQLException
     */
    public boolean execute(String arg0, String[] arg1) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.execute(arg0, arg1);
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int[] executeBatch() throws SQLException {
        return callableStatement.executeBatch();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet executeQuery() throws SQLException {
        return callableStatement.executeQuery();
    }

    /**
     * @param arg0
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet executeQuery(String arg0) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.executeQuery(arg0);
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int executeUpdate() throws SQLException {
        return callableStatement.executeUpdate();
    }

    /**
     * @param arg0
     * @return
     * @throws java.sql.SQLException
     */
    public int executeUpdate(String arg0) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.executeUpdate(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws java.sql.SQLException
     */
    public int executeUpdate(String arg0, int arg1) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.executeUpdate(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws java.sql.SQLException
     */
    public int executeUpdate(String arg0, int[] arg1) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.executeUpdate(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws java.sql.SQLException
     */
    public int executeUpdate(String arg0, String[] arg1) throws SQLException {
        sqlStatement = arg0;
        return callableStatement.executeUpdate(arg0, arg1);
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public Connection getConnection() throws SQLException {
        //return callableStatement.getConnection();
        return connection;
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getFetchDirection() throws SQLException {
        return callableStatement.getFetchDirection();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getFetchSize() throws SQLException {
        return callableStatement.getFetchSize();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet getGeneratedKeys() throws SQLException {
        return callableStatement.getGeneratedKeys();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getMaxFieldSize() throws SQLException {
        return callableStatement.getMaxFieldSize();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getMaxRows() throws SQLException {
        return callableStatement.getMaxRows();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return callableStatement.getMetaData();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public boolean getMoreResults() throws SQLException {
        return callableStatement.getMoreResults();
    }

    /**
     * @param arg0
     * @return
     * @throws java.sql.SQLException
     */
    public boolean getMoreResults(int arg0) throws SQLException {
        return callableStatement.getMoreResults(arg0);
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return callableStatement.getParameterMetaData();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getQueryTimeout() throws SQLException {
        return callableStatement.getQueryTimeout();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet getResultSet() throws SQLException {
        return callableStatement.getResultSet();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getResultSetConcurrency() throws SQLException {
        return callableStatement.getResultSetConcurrency();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getResultSetHoldability() throws SQLException {
        return callableStatement.getResultSetHoldability();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getResultSetType() throws SQLException {
        return callableStatement.getResultSetType();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public int getUpdateCount() throws SQLException {
        return callableStatement.getUpdateCount();
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public SQLWarning getWarnings() throws SQLException {
        return callableStatement.getWarnings();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return callableStatement.hashCode();
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setArray(int arg0, Array arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setArray(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {
        callableStatement.setAsciiStream(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setBigDecimal(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setBinaryStream(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setBlob(int arg0, Blob arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setBlob(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setBoolean(int arg0, boolean arg1) throws SQLException {
        parameters.put(new Integer(arg0), new Boolean(arg1));
        callableStatement.setBoolean(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setByte(int arg0, byte arg1) throws SQLException {
        parameters.put(new Integer(arg0), new Byte(arg1));
        callableStatement.setByte(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setBytes(int arg0, byte[] arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setBytes(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setCharacterStream(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setClob(int arg0, Clob arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setClob(arg0, arg1);
    }

    /**
     * @param arg0
     * @throws java.sql.SQLException
     */
    public void setCursorName(String arg0) throws SQLException {
        callableStatement.setCursorName(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setDate(int arg0, Date arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setDate(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setDate(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setDouble(int arg0, double arg1) throws SQLException {
        parameters.put(new Integer(arg0), new Double(arg1));
        callableStatement.setDouble(arg0, arg1);
    }

    /**
     * @param arg0
     * @throws java.sql.SQLException
     */
    public void setEscapeProcessing(boolean arg0) throws SQLException {
        callableStatement.setEscapeProcessing(arg0);
    }

    /**
    * @param arg0
     * @throws java.sql.SQLException
     */
    public void setFetchDirection(int arg0) throws SQLException {
        callableStatement.setFetchDirection(arg0);
    }

    /**
     * @param arg0
     * @throws java.sql.SQLException
     */
    public void setFetchSize(int arg0) throws SQLException {
        callableStatement.setFetchSize(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setFloat(int arg0, float arg1) throws SQLException {
        parameters.put(new Integer(arg0), new Float(arg1));
        callableStatement.setFloat(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setInt(int arg0, int arg1) throws SQLException {
        parameters.put(new Integer(arg0), new Integer(arg1));
        callableStatement.setInt(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setLong(int arg0, long arg1) throws SQLException {
        parameters.put(new Integer(arg0), new Long(arg1));
        callableStatement.setLong(arg0, arg1);
    }

    /**
     * @param arg0
     * @throws java.sql.SQLException
     */
    public void setMaxFieldSize(int arg0) throws SQLException {
        callableStatement.setMaxFieldSize(arg0);
    }

    /**
     * @param arg0
     * @throws java.sql.SQLException
     */
    public void setMaxRows(int arg0) throws SQLException {
        callableStatement.setMaxRows(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setNull(int arg0, int arg1) throws SQLException {
        parameters.put(new Integer(arg0), "null");
        callableStatement.setNull(arg0, arg1);
    }
    
    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setNull(int arg0, int arg1, String arg2) throws SQLException {
        parameters.put(new Integer(arg0), "null");
        callableStatement.setNull(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setObject(int arg0, Object arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setObject(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setObject(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws java.sql.SQLException
     */
    public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setObject(arg0, arg1, arg2, arg3);
    }

    /**
     * @param arg0
     * @throws java.sql.SQLException
     */
    public void setQueryTimeout(int arg0) throws SQLException {
        callableStatement.setQueryTimeout(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setRef(int arg0, Ref arg1) throws SQLException {
        callableStatement.setRef(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setShort(int arg0, short arg1) throws SQLException {
        parameters.put(new Integer(arg0), new Short(arg1));
        callableStatement.setShort(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setString(int arg0, String arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setString(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setTime(int arg0, Time arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setTime(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setTime(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setTimestamp(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setTimestamp(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.sql.SQLException
     */
    public void setUnicodeStream(int arg0, InputStream arg1, int arg2) throws SQLException {
        callableStatement.setUnicodeStream(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws java.sql.SQLException
     */
    public void setURL(int arg0, URL arg1) throws SQLException {
        parameters.put(new Integer(arg0), arg1);
        callableStatement.setURL(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(sqlStatement, "?");
        String partOfStatement;
        List parameterValues = new ArrayList(parameters.keySet());
        Collections.sort(parameterValues);
        Iterator iter = parameterValues.iterator();
        Object key = null;
        while (tokenizer.hasMoreTokens()) {
            partOfStatement = tokenizer.nextToken();
            if (iter.hasNext()) {
                key = iter.next();
                buffer.append(partOfStatement);
                buffer.append("'" + parameters.get(key).toString() + "'");
            } else {
                buffer.append(partOfStatement);
                buffer.append("?");
            }
        }
        return buffer.toString();
    }

    // CallableStatement interface
    /**
     *
     * @param parameterIndex int
     * @param sqlType int
     * @throws SQLException
     */
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        callableStatement.registerOutParameter(parameterIndex, sqlType);
    }

    /**
     *
     * @param parameterIndex int
     * @param sqlType int
     * @param scale int
     * @throws SQLException
     */
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
    }

    /**
     *
     * @throws SQLException
     * @return boolean
     */
    public boolean wasNull() throws SQLException {
        return callableStatement.wasNull();
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return String
     */
    public String getString(int parameterIndex) throws SQLException {
        return callableStatement.getString(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return boolean
     */
    public boolean getBoolean(int parameterIndex) throws SQLException {
        return callableStatement.getBoolean(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return byte
     */
    public byte getByte(int parameterIndex) throws SQLException {
        return callableStatement.getByte(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return short
     */
    public short getShort(int parameterIndex) throws SQLException {
        return callableStatement.getShort(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return int
     */
    public int getInt(int parameterIndex) throws SQLException {
        return callableStatement.getInt(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return long
     */
    public long getLong(int parameterIndex) throws SQLException {
        return callableStatement.getLong(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return float
     */
    public float getFloat(int parameterIndex) throws SQLException {
        return callableStatement.getFloat(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return double
     */
    public double getDouble(int parameterIndex) throws SQLException {
        return callableStatement.getDouble(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @param scale int
     * @throws SQLException
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return callableStatement.getBigDecimal(parameterIndex, scale);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return byte[]
     */
    public byte[] getBytes(int parameterIndex) throws SQLException {
        return callableStatement.getBytes(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return Date
     */
    public java.sql.Date getDate(int parameterIndex) throws SQLException {
        return callableStatement.getDate(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return Time
     */
    public java.sql.Time getTime(int parameterIndex) throws SQLException {
        return callableStatement.getTime(parameterIndex);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return Timestamp
     */
    public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return callableStatement.getTimestamp(parameterIndex);
    }

    //----------------------------------------------------------------------
    // Advanced features:

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return Object
     */
    public Object getObject(int parameterIndex) throws SQLException {
        return callableStatement.getObject(parameterIndex);
    }

    //--------------------------JDBC 2.0-----------------------------

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return callableStatement.getBigDecimal(parameterIndex);
    }

    /**
     *
     * @param i int
     * @param map Map
     * @throws SQLException
     * @return Object
     */
    public Object getObject(int i, java.util.Map map) throws SQLException {
        return callableStatement.getObject(i, map);
    }

    /**
     *
     * @param i int
     * @throws SQLException
     * @return Ref
     */
    public Ref getRef(int i) throws SQLException {
        return callableStatement.getRef(i);
    }

    /**
     *
     * @param i int
     * @throws SQLException
     * @return Blob
     */
    public Blob getBlob(int i) throws SQLException {
        return callableStatement.getBlob(i);
    }

    /**
     *
     * @param i int
     * @throws SQLException
     * @return Clob
     */
    public Clob getClob(int i) throws SQLException {
        return callableStatement.getClob(i);
    }

    /**
     *
     * @param i int
     * @throws SQLException
     * @return Array
     */
    public Array getArray(int i) throws SQLException {
        return callableStatement.getArray(i);
    }

    /**
     *
     * @param parameterIndex int
     * @param cal Calendar
     * @throws SQLException
     * @return Date
     */
    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return callableStatement.getDate(parameterIndex, cal);
    }

    /**
     *
     * @param parameterIndex int
     * @param cal Calendar
     * @throws SQLException
     * @return Time
     */
    public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return callableStatement.getTime(parameterIndex, cal);
    }

    /**
     *
     * @param parameterIndex int
     * @param cal Calendar
     * @throws SQLException
     * @return Timestamp
     */
    public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return callableStatement.getTimestamp(parameterIndex, cal);
    }

    /**
     *
     * @param paramIndex int
     * @param sqlType int
     * @param typeName String
     * @throws SQLException
     */
    public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        callableStatement.registerOutParameter(paramIndex, sqlType, typeName);
    }

    //--------------------------JDBC 3.0-----------------------------

    /**
     *
     * @param parameterName String
     * @param sqlType int
     * @throws SQLException
     */
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        callableStatement.registerOutParameter(parameterName, sqlType);
    }

    /**
     *
     * @param parameterName String
     * @param sqlType int
     * @param scale int
     * @throws SQLException
     */
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        callableStatement.registerOutParameter(parameterName, sqlType, scale);
    }

    /**
     *
     * @param parameterName String
     * @param sqlType int
     * @param typeName String
     * @throws SQLException
     */
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        callableStatement.registerOutParameter(parameterName, sqlType, typeName);
    }

    /**
     *
     * @param parameterIndex int
     * @throws SQLException
     * @return URL
     */
    public java.net.URL getURL(int parameterIndex) throws SQLException {
        return callableStatement.getURL(parameterIndex);
    }

    /**
     *
     * @param parameterName String
     * @param val URL
     * @throws SQLException
     */
    public void setURL(String parameterName, java.net.URL val) throws SQLException {
        callableStatement.setURL(parameterName, val);
    }

    /**
     *
     * @param parameterName String
     * @param sqlType int
     * @throws SQLException
     */
    public void setNull(String parameterName, int sqlType) throws SQLException {
        callableStatement.setNull(parameterName, sqlType);
    }

    /**
     *
     * @param parameterName String
     * @param x boolean
     * @throws SQLException
     */
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        callableStatement.setBoolean(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x byte
     * @throws SQLException
     */
    public void setByte(String parameterName, byte x) throws SQLException {
        callableStatement.setByte(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x short
     * @throws SQLException
     */
    public void setShort(String parameterName, short x) throws SQLException {
        callableStatement.setShort(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x int
     * @throws SQLException
     */
    public void setInt(String parameterName, int x) throws SQLException {
        callableStatement.setInt(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x long
     * @throws SQLException
     */
    public void setLong(String parameterName, long x) throws SQLException {
        callableStatement.setLong(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x float
     * @throws SQLException
     */
    public void setFloat(String parameterName, float x) throws SQLException {
        callableStatement.setFloat(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x double
     * @throws SQLException
     */
    public void setDouble(String parameterName, double x) throws SQLException {
        callableStatement.setDouble(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x BigDecimal
     * @throws SQLException
     */
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        callableStatement.setBigDecimal(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x String
     * @throws SQLException
     */
    public void setString(String parameterName, String x) throws SQLException {
        callableStatement.setString(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x byte[]
     * @throws SQLException
     */
    public void setBytes(String parameterName, byte x[]) throws SQLException {
        callableStatement.setBytes(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x Date
     * @throws SQLException
     */
    public void setDate(String parameterName, java.sql.Date x) throws SQLException {
        callableStatement.setDate(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x Time
     * @throws SQLException
     */
    public void setTime(String parameterName, java.sql.Time x) throws SQLException {
        callableStatement.setTime(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x Timestamp
     * @throws SQLException
     */
    public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
        callableStatement.setTimestamp(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param x InputStream
     * @param length int
     * @throws SQLException
     */
    public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        callableStatement.setAsciiStream(parameterName, x, length);
    }

    /**
     *
     * @param parameterName String
     * @param x InputStream
     * @param length int
     * @throws SQLException
     */
    public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        callableStatement.setBinaryStream(parameterName, x, length);
    }

    /**
     *
     * @param parameterName String
     * @param x Object
     * @param targetSqlType int
     * @param scale int
     * @throws SQLException
     */
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        callableStatement.setObject(parameterName, x, targetSqlType, scale);
    }

    /**
     *
     * @param parameterName String
     * @param x Object
     * @param targetSqlType int
     * @throws SQLException
     */
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        callableStatement.setObject(parameterName, x, targetSqlType);
    }

    /**
     *
     * @param parameterName String
     * @param x Object
     * @throws SQLException
     */
    public void setObject(String parameterName, Object x) throws SQLException {
        callableStatement.setObject(parameterName, x);
    }

    /**
     *
     * @param parameterName String
     * @param reader Reader
     * @param length int
     * @throws SQLException
     */
    public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
        callableStatement.setCharacterStream(parameterName, reader, length);
    }

    /**
     *
     * @param parameterName String
     * @param x Date
     * @param cal Calendar
     * @throws SQLException
     */
    public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
        callableStatement.setDate(parameterName, x, cal);
    }

    /**
     *
     * @param parameterName String
     * @param x Time
     * @param cal Calendar
     * @throws SQLException
     */
    public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
        callableStatement.setTime(parameterName, x, cal);
    }

    /**
     *
     * @param parameterName String
     * @param x Timestamp
     * @param cal Calendar
     * @throws SQLException
     */
    public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
        callableStatement.setTimestamp(parameterName, x, cal);
    }

    /**
     *
     * @param parameterName String
     * @param sqlType int
     * @param typeName String
     * @throws SQLException
     */
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        callableStatement.setNull(parameterName, sqlType, typeName);
    }

    /**
    *
     * @param parameterName String
     * @throws SQLException
     * @return String
     */
    public String getString(String parameterName) throws SQLException {
        return callableStatement.getString(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return boolean
     */
    public boolean getBoolean(String parameterName) throws SQLException {
        return callableStatement.getBoolean(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return byte
     */
    public byte getByte(String parameterName) throws SQLException {
        return callableStatement.getByte(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return short
     */
    public short getShort(String parameterName) throws SQLException {
        return callableStatement.getShort(parameterName);
   }

   /**
    *
     * @param parameterName String
     * @throws SQLException
     * @return int
     */
    public int getInt(String parameterName) throws SQLException {
       return callableStatement.getInt(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return long
     */
    public long getLong(String parameterName) throws SQLException {
        return callableStatement.getLong(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return float
     */
    public float getFloat(String parameterName) throws SQLException {
        return callableStatement.getFloat(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return double
     */
    public double getDouble(String parameterName) throws SQLException {
        return callableStatement.getDouble(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return byte[]
     */
    public byte[] getBytes(String parameterName) throws SQLException {
       return callableStatement.getBytes(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Date
     */
    public java.sql.Date getDate(String parameterName) throws SQLException {
        return callableStatement.getDate(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Time
     */
    public java.sql.Time getTime(String parameterName) throws SQLException {
        return callableStatement.getTime(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Timestamp
     */
    public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
        return callableStatement.getTimestamp(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Object
    */
    public Object getObject(String parameterName) throws SQLException {
        return callableStatement.getObject(parameterName);
   }

    /**
    *
     * @param parameterName String
     * @throws SQLException
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return callableStatement.getBigDecimal(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @param map Map
     * @throws SQLException
     * @return Object
     */
    public Object getObject(String parameterName, java.util.Map map) throws SQLException {
        return callableStatement.getObject(parameterName, map);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Ref
     */
    public Ref getRef(String parameterName) throws SQLException {
        return callableStatement.getRef(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Blob
     */
    public Blob getBlob(String parameterName) throws SQLException {
        return callableStatement.getBlob(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Clob
     */
    public Clob getClob(String parameterName) throws SQLException {
        return callableStatement.getClob(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return Array
     */
    public Array getArray(String parameterName) throws SQLException {
        return callableStatement.getArray(parameterName);
    }

    /**
     *
     * @param parameterName String
     * @param cal Calendar
     * @throws SQLException
     * @return Date
     */
    public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
        return callableStatement.getDate(parameterName, cal);
    }

    /**
     *
     * @param parameterName String
     * @param cal Calendar
     * @throws SQLException
     * @return Time
     */
    public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
        return callableStatement.getTime(parameterName, cal);
    }

    /**
     *
     * @param parameterName String
     * @param cal Calendar
     * @throws SQLException
     * @return Timestamp
     */
    public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return callableStatement.getTimestamp(parameterName, cal);
    }

    /**
     *
     * @param parameterName String
     * @throws SQLException
     * @return URL
     */
    public java.net.URL getURL(String parameterName) throws SQLException {
        return callableStatement.getURL(parameterName);
    }
}

