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


package org.exolab.castor.jdo.drivers;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.spi.KeyGenerator;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.engine.JDBCSyntax;
import org.exolab.castor.util.Messages;

/**
 * The parent abstract class for HIGH/LOW key generators
 * @author <a href="on@ibis.odessa.ua">Oleg Nitz</a>
 * @version $Revision$ $Date$
 * @see HighLowGeneratorFactory
 */
public class HighLowKeyGenerator implements KeyGenerator
{
    private final static BigDecimal ONE = new BigDecimal( 1 );

    private final static String SEQ_TABLE = "table";

    private final static String SEQ_KEY = "key-column";

    private final static String SEQ_VALUE = "value-column";

    private final static String GRAB_SIZE = "grab-size";

    // Sequence table name 
    private final String _seqTable;

    // Sequence table key column name
    private final String _seqKey;

    // Sequence table value column name
    private final String _seqValue;

    // grab size
    private final BigDecimal _grabSize;

    // last generated values
    private Hashtable _lastValues = new Hashtable();
    
    // maximum possible values after which database operation is needed
    private Hashtable _maxValues = new Hashtable();

    /**
     * Initialize the HIGH/LOW key generator.
     */
    public HighLowKeyGenerator( Properties params ) throws MappingException
    {
        String factorStr;

        _seqTable = params.getProperty( SEQ_TABLE );
        if ( _seqTable == null ) {
            throw new MappingException( Messages.format( "mapping.KeyGenParamNotSet",
                    SEQ_TABLE, getClass().getName() ) );
        }

        _seqKey = params.getProperty( SEQ_KEY );
        if ( _seqKey == null ) {
            throw new MappingException( Messages.format( "mapping.KeyGenParamNotSet",
                    SEQ_KEY, getClass().getName() ) );
        }

        _seqValue = params.getProperty( SEQ_VALUE );
        if ( _seqValue == null ) {
            throw new MappingException( Messages.format( "mapping.KeyGenParamNotSet",
                    SEQ_VALUE, getClass().getName() ) );
        }

        factorStr = params.getProperty( GRAB_SIZE, "1" );
        _grabSize = new BigDecimal( factorStr );
        if ( _grabSize.compareTo( new BigDecimal( 0 ) ) <= 0 ) {
            throw new MappingException( Messages.format( "mapping.wrongKeyGenParam",
                    factorStr, GRAB_SIZE, getClass().getName() ) );
        }
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
    public synchronized Object generateKey( Connection conn, String tableName,
            String primKeyName, Properties props )
            throws PersistenceException
    {
        BigDecimal last;
        BigDecimal max;

        last = (BigDecimal) _lastValues.get( tableName );
        max = (BigDecimal) _maxValues.get( tableName );
        if ( last != null ) {    
            last = last.add( ONE );
        } else {
            String sql;
            String pk;
            PreparedStatement stmt = null;
            ResultSet rs;

            try {
                sql = JDBCSyntax.Select + _seqValue + JDBCSyntax.From + _seqTable +
                    JDBCSyntax.Where + _seqKey + QueryExpression.OpEquals +
                    JDBCSyntax.Parameter;
                stmt = conn.prepareStatement( sql, ResultSet.TYPE_FORWARD_ONLY,
                                                ResultSet.CONCUR_UPDATABLE );
                stmt.setString(1, tableName);

                rs = stmt.executeQuery();

                if ( rs.next() ) {
                    last = rs.getBigDecimal( 1 );
                    max = last.add( _grabSize );
                    rs.updateBigDecimal( 1, max );
                    rs.updateRow();
                    last = last.add( ONE );
                } else {
                    stmt = conn.prepareStatement("INSERT INTO " + _seqTable +
                                                " (" + _seqKey + "," + _seqValue +
                                                ") VALUES (?, ?)");
                    stmt.setString( 1, tableName );
                    stmt.setBigDecimal( 2, _grabSize );
                    stmt.executeUpdate();
                    last = ONE;
                    max = _grabSize;
                }
            } catch ( SQLException ex ) {
                throw new PersistenceException( Messages.format(
                        "persist.keyGenSQL", ex.toString() ) );
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException ex ) {
                    }
                }
            }
        }

        if ( last.compareTo( max ) < 0 ) {
            _lastValues.put( tableName, last );
            _maxValues.put( tableName, max );
        } else {
            _lastValues.remove( tableName );
            _maxValues.remove( tableName );
        }
        return last;
    }

    /**
     * Is key generated before INSERT? 
     */
    public final boolean isBeforeInsert() {
        return true;
    }

    /**
     * Is key generated in the same connection as INSERT?
     */
    public final boolean isInSameConnection() {
        return false;
    }
}

