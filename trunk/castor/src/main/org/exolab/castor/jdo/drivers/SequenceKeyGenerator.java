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


package org.exolab.castor.jdo.drivers;



import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.util.Messages;

/**
 * SEQUENCE key generator.
 * @author <a href="on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 * @see SequenceKeyGeneratorFactory
 */
public final class SequenceKeyGenerator implements KeyGenerator
{

    /**
     * The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     * Commons Logging</a> instance used for all logging.
     */
    private static Log _log = LogFactory.getFactory().getInstance(SequenceKeyGenerator.class);
    
    private DefaultIdentityValue identityValue = null;
    private AbstractType type = null;
    
    
    protected final PersistenceFactory _factory;


    protected final String _factoryName;


    protected final String _seqName;


    private byte _style;


    private final int _sqlType;


    private int _increment;


    /**
     * Indicates whether a trigger is present.
     */
    private boolean _triggerPresent;


    /**
     * Initialize the SEQUENCE key generator.
     * @param factory A PersistenceFactory instance.
     * @param params Properties.
     * @param sqlType A SQL type identifier.
     * @throws MappingException Problem initializing this key generator.
     */
    public SequenceKeyGenerator( PersistenceFactory factory, Properties params, int sqlType )
	throws MappingException
    {
        boolean returning;

        _factoryName = factory.getFactoryName();
        _factory = factory;
        returning = "true".equals( params.getProperty("returning") );
        _triggerPresent = "true".equals( params.getProperty("trigger","false") );

        _seqName = params.getProperty("sequence", "{0}_seq");
        
        if ( (! _factoryName.equals( "oracle" )) &&  
        		(! _factoryName.equals( "postgresql")) &&
				(! _factoryName.equals( "interbase" )) &&
				(! _factoryName.equals( "sapdb" )) &&
				(! _factoryName.equals( "db2" )) ) {
        	throw new MappingException( Messages.format( "mapping.keyGenNotCompatible",
                                        getClass().getName(), _factoryName ) );
        }
        if ((! _factoryName.equals( "oracle" )) && 
        		(returning) ) {
            throw new MappingException( Messages.format( "mapping.keyGenParamNotCompat",
                                        "returning=\"true\"", getClass().getName(), _factoryName ) );
        }

        _style = (( _factoryName.equals( "postgresql" )) || 
        		(_factoryName.equals("interbase"))  || 
				(_factoryName.equals("db2"))
                ? BEFORE_INSERT : ( returning  ? DURING_INSERT : AFTER_INSERT) );
        
        if (_triggerPresent && !returning) {
            _style = AFTER_INSERT;
        }
        
        if (_triggerPresent && _style==BEFORE_INSERT)
            throw new MappingException( Messages.format( "mapping.keyGenParamNotCompat",
                                        "trigger=\"true\"", getClass().getName(), _factoryName ) );

        _sqlType = sqlType;
        
        if ((sqlType != Types.INTEGER) 
		&& (sqlType != Types.NUMERIC) 
		&& (sqlType != Types.DECIMAL) 
		&& (sqlType != Types.BIGINT))
        	throw new MappingException( Messages.format( "mapping.keyGenSQLType",
                                        getClass().getName(), new Integer(sqlType)));
        
        
        // etablish increment to use;if not specified, use default value of 1.
        try {
            _increment = Integer.parseInt(params.getProperty("increment", "1"));
        } catch (NumberFormatException nfe) {
            _increment = 1;
        }
        
        initIdentityValue(sqlType);
        initType(_factoryName);
        
    }


    /**
     * @param conn An open connection within the given transaction
     * @param tableName The table name
     * @param primaryKeyName The primary key name
     * @param props A temporary replacement for Principal object
     * @return A new key
     * @throws PersistenceException An error occured talking to persistent
     *  storage
     */
    public Object generateKey( Connection conn, String tableName, String primaryKeyName,
            Properties props )
    throws PersistenceException
    {
        return type.getValue( conn, tableName, primaryKeyName, props);
    }

    /**
     * @param conn An open connection within the given transaction
     * @param tableName The table name
     * @param primKeyName The primary key name
     * @param props A temporary replacement for Principal object
     * @return A new key
     * @throws PersistenceException An error occured talking to persistent
     *  storage
     */
    public Object generateKeyOld( Connection conn, String tableName, String primKeyName,
            Properties props )
            throws PersistenceException
    {
        PreparedStatement stmt = null;
        ResultSet rs;
        int value;
        String seqName;
        String table;

        seqName = MessageFormat.format( _seqName, new String[] {tableName,primKeyName});
        table = _factory.quoteName(tableName);
        try {
            if (_factory.getFactoryName().equals("interbase")) {
                //interbase only does before_insert, and does it its own way
                stmt = conn.prepareStatement("SELECT gen_id(" + seqName +
                        "," + _increment + ") FROM rdb$database");
                rs = stmt.executeQuery();
            } else if (_factory.getFactoryName().equals("db2")) {
                stmt = conn.prepareStatement("SELECT nextval FOR " + seqName + " FROM SYSIBM.SYSDUMMY1" );
                rs = stmt.executeQuery();
            } else {
                if ( _style == BEFORE_INSERT ) {
                    stmt = conn.prepareStatement("SELECT nextval('" + seqName + "')" );
                    rs = stmt.executeQuery();
                } else if (_triggerPresent && _factoryName.equals( "postgresql" )) {
                    Object insStmt = props.get("insertStatement");
                    Class psqlStmtClass = Class.forName("org.postgresql.Statement");
                    Method getInsertedOID = psqlStmtClass.getMethod("getInsertedOID", null);
                    int insertedOID = ((Integer) getInsertedOID.invoke(insStmt, null)).intValue();
                    stmt = conn.prepareStatement("SELECT " + _factory.quoteName( primKeyName ) +
                            " FROM " + table + " WHERE OID=?");
                    stmt.setInt(1, insertedOID);
                    rs = stmt.executeQuery();

                } else {
                    stmt = conn.prepareStatement("SELECT " + _factory.quoteName(seqName + ".currval") + " FROM " + table);
                    rs = stmt.executeQuery();
                }
            }

            if ( rs.next() ) {
                value = rs.getInt( 1 );
                if ( _sqlType == Types.INTEGER )
                    return new Integer( value );
                else if ( _sqlType == Types.BIGINT )
                    return new Long( value );
                else
                    return new BigDecimal( value );
            } else {
                throw new PersistenceException( Messages.message( "persist.keyGenFailed" ) );
            }
        } catch ( Exception ex ) {
            throw new PersistenceException( Messages.format(
                    "persist.keyGenSQL", ex.toString() ) );
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException ex ) {
                    _log.warn ("Problem closing JDBC Statement", ex);
                }
            }
        }
    }


    /**
     * Style of key generator: BEFORE_INSERT, DURING_INSERT or AFTER_INSERT ?
     */
    public byte getStyle() {
        return _style;
    }


    /**
     * Gives a possibility to patch the Castor-generated SQL statement
     * for INSERT (makes sense for DURING_INSERT key generators)
     */
    public String patchSQL( String insert, String primKeyName )
            throws MappingException {
        StringTokenizer st;
        String tableName;
        String seqName;
        String nextval;
        StringBuffer sb;
        int lp1;  // the first left parenthesis, which starts fields list
        int lp2;  // the second left parenthesis, which starts values list

        if ( _style == BEFORE_INSERT ) {
            return insert;
        }

        // First find the table name
        st = new StringTokenizer( insert );
        if ( !st.hasMoreTokens() || !st.nextToken().equalsIgnoreCase("INSERT") ) {
            throw new MappingException( Messages.format( "mapping.keyGenCannotParse",
                                                         insert ) );
        }
        if ( !st.hasMoreTokens() || !st.nextToken().equalsIgnoreCase("INTO") ) {
            throw new MappingException( Messages.format( "mapping.keyGenCannotParse",
                                                         insert ) );
        }
        if ( !st.hasMoreTokens() ) {
            throw new MappingException( Messages.format( "mapping.keyGenCannotParse",
                                                         insert ) );
        }
        tableName = st.nextToken();

        // remove quotes
        if ( tableName.startsWith("\"") ) {
            tableName = tableName.substring( 1, tableName.length() - 1 );
        }
        seqName = MessageFormat.format( _seqName, new String[] {tableName,primKeyName});
        nextval = _factory.quoteName(seqName + ".nextval");
        lp1 = insert.indexOf( '(' );
        lp2 = insert.indexOf( '(', lp1 + 1 );
        if ( lp1 < 0 ) {
            throw new MappingException( Messages.format( "mapping.keyGenCannotParse",
                                                         insert ) );
        }
        sb = new StringBuffer( insert );
        if (!_triggerPresent) { // if no onInsert triggers in the DB, we have to supply the Key values manually
           if ( lp2 < 0 ) {
                // Only one pk field in the table, the INSERT statement would be
                // INSERT INTO table VALUES ()
                lp2 = lp1;
                lp1 = insert.indexOf( " VALUES " );
                // don't change the order of lines below,
                // otherwise index becomes invalid
                sb.insert( lp2 + 1, nextval);
                sb.insert( lp1 + 1, "(" + _factory.quoteName( primKeyName ) + ") " );
            } else {
                // don't change the order of lines below,
                // otherwise index becomes invalid
                sb.insert( lp2 + 1, nextval + ",");
                sb.insert( lp1 + 1, _factory.quoteName( primKeyName ) + "," );
            }
        }
        if ( _style == DURING_INSERT ) {
            // append 'RETURNING primKeyName INTO ?'
            sb.append( " RETURNING " );
            sb.append( _factory.quoteName( primKeyName ) );
            sb.append( " INTO ?" );
        }
        return sb.toString();
    }


    /**
     * Is key generated in the same connection as INSERT?
     */
    public boolean isInSameConnection() {
        return true;
    }

    private void initIdentityValue(int sqlType) {
        if (sqlType == Types.INTEGER) {
            identityValue = new IntegerIdenityValue();
        } else if (sqlType == Types.BIGINT) {
            identityValue = new LongIdenityValue();
        } else {
            identityValue = new DefaultIdentityValue();
        }
    }
    
    private void initType(String fName) {
        if (fName.equals("interbase")) {
            type = new InterbaseType();
        } else if (fName.equals("db2")) {
            type = new DB2Type();
        } else {
            type = new DefaultType();
        }
    }
    
    
    private class DefaultIdentityValue {
        Object getValue(int value) {
            return new BigDecimal(value);
        }
    }
    
    private class IntegerIdenityValue extends DefaultIdentityValue {
        Object getValue(int value) {
            return new Integer(value);
        }
    }
    
    private class LongIdenityValue extends DefaultIdentityValue {
        Object getValue(int value) {
            return new Long(value);
        }
    }
 
    private abstract class AbstractType {
        abstract Object getValue(Connection conn, String sequenceName, String primaryKeyName, Properties props) 
		throws PersistenceException;

        Object getValue(PreparedStatement stmt) throws PersistenceException, SQLException {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return identityValue.getValue(rs.getInt(1));
            } else {
                throw new PersistenceException(Messages.message("persist.keyGenFailed"));
            }
        }

        Object getValue(String sql, Connection conn) throws PersistenceException {
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement(sql);
                return getValue(stmt);
            } catch (SQLException e) {
                throw new PersistenceException(Messages.format("persist.keyGenSQL", e.toString()));
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    private class DefaultType extends AbstractType {
        
        Object getValue(Connection conn, String tableName, String primaryKeyName, Properties props) 
		throws PersistenceException 
        {
            StringBuffer buf = new StringBuffer();
            
            String sequenceName = getSequenceName(_seqName, tableName, primaryKeyName);
            String table = _factory.quoteName(tableName);
            
            if ( _style == BEFORE_INSERT) {
                buf.append ("SELECT nextval('").append(sequenceName).append("')");
            } else if (_triggerPresent && _factoryName.equals( "postgresql" )) {
                Object insStmt = props.get("insertStatement");
                Class psqlStmtClass;
                Method getInsertedOID;
                int insertedOID;
                PreparedStatement stmt;
                try {
					psqlStmtClass = Class.forName("org.postgresql.Statement");
                    getInsertedOID = psqlStmtClass.getMethod ("getInsertedOID", null);
                    insertedOID = ((Integer) getInsertedOID.invoke(insStmt, null)).intValue();
                    stmt = conn.prepareStatement("SELECT " + _factory.quoteName( primaryKeyName ) +
                            " FROM " + table + " WHERE OID=?");
                    stmt.setInt(1, insertedOID);
                    return getValue (stmt);
                } 
                catch (Exception e) {
                    _log.warn (Messages.format("persist.keyGenSQL", e.toString()));
                    throw new PersistenceException( Messages.format(
                            "persist.keyGenSQL", e.toString() ) );
				}

            } else {
                buf.append ("SELECT ").append(_factory.quoteName(sequenceName + ".currval")).append(" FROM " + table);
            }
            
            return getValue(buf.toString(), conn);
        }
        
        
    }
    
    private class DB2Type extends AbstractType {
        Object getValue(Connection conn, String tableName, String primaryKeyName, Properties props) 
		throws PersistenceException 
        {
            String sequenceName = getSequenceName(_seqName, tableName, primaryKeyName);
            StringBuffer buf = new StringBuffer();
            buf.append ("SELECT nextval FOR ").append(sequenceName).append(" FROM SYSIBM.SYSDUMMY1");
            return getValue(buf.toString(), conn);
        }
    }

    private class InterbaseType extends AbstractType {
        
        Object getValue(Connection conn, String tableName, String primaryKeyName, Properties props) 
		throws PersistenceException 
        {
            // interbase only does before_insert, and does it its own way
            String sequenceName = getSequenceName(_seqName, tableName, primaryKeyName);
            StringBuffer buf = new StringBuffer();
        	buf.append("SELECT gen_id(")
			.append(sequenceName)
			.append(",")
			.append(_increment)
			.append(") FROM rdb$database");
            return getValue(buf.toString(), conn);
        }
    }
    
    /**
     * Helper method to derive complete name of the sequence.
     * @param seqName Sequence name
     * @param tableName Table name.
     * @param primaryKeyName Name of the primary key.
     * @return A complete sequence name.
     */
    private String getSequenceName (String seqName, String tableName, String primaryKeyName) {
    	return MessageFormat.format(seqName, new String[] {tableName, primaryKeyName});    
    }
    
    
    
    
}
