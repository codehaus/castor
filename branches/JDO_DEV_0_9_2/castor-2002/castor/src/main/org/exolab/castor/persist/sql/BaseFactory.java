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


import java.sql.DriverManager;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;
import java.util.Enumeration;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.conf.*;
import org.exolab.castor.jdo.engine.JDOClassDescriptor;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.persist.LogInterceptor;
import org.exolab.castor.persist.Entity;
import org.exolab.castor.persist.EntityInfo;
import org.exolab.castor.persist.EntityFieldInfo;
import org.exolab.castor.persist.LockEngine;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.Connector;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.persist.spi.PersistenceQuery;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.util.Messages;

/**
 * {@link PersistenceFactory} for generic JDBC driver.
 *
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public abstract class BaseFactory
    implements PersistenceFactory
{


    public Persistence getPersistence( EntityInfo entity, LockEngine lockEngine, LogInterceptor log )
        throws MappingException
    {

        // 092: if ( ! ( clsDesc instanceof JDOClassDescriptor ) )
        //    return null;

        try {
            return new SQLEngine( entity, lockEngine, log, this, null );
        } catch ( MappingException except ) {
            if ( log != null )
                log.exception( except );
            return null;
        }
    }

    public Connector getConnector( Database database )
            throws MappingException, PersistenceException {

        try {
            if ( database.getDriver() != null ) {
                // JDO configuration file specifies a driver, use the driver
                // properties to create a new registry object.
                Properties  props;
                Enumeration params;
                Param       param;

                if ( database.getDriver().getClassName() != null ) {
                    try {
                        Class.forName( database.getDriver().getClassName() ).newInstance();
                    } catch ( Exception except ) {
                        throw new MappingException( except );
                    }
                }
                if ( DriverManager.getDriver( database.getDriver().getUrl() ) == null )
                    throw new MappingException( "jdo.missingDriver", database.getDriver().getUrl() );

                props = new Properties();
                params = database.getDriver().enumerateParam();
                while ( params.hasMoreElements() ) {
                    param = (Param) params.nextElement();
                    props.put( param.getName(), param.getValue() );
                }
                return new SQLConnector( database.getName(), database.getDriver().getUrl(), props );
            } else if ( database.getDataSource() != null ) {
                // JDO configuration file specifies a DataSource object, use the
                // DataSource which was configured from the JDO configuration file
                // to create a new registry object.
                DataSource ds;

                ds = (DataSource) database.getDataSource().getParams();
                if ( ds == null )
                    throw new MappingException( "jdo.missingDataSource", database.getName() );
                return new SQLConnector( database.getName(), ds );
            } else if ( database.getJndi() != null ) {
                // JDO configuration file specifies a DataSource lookup through JNDI,
                // locate the DataSource object frome the JNDI namespace and use it.
                Object    ds;

                try {
                    ds = new InitialContext().lookup( database.getJndi().getName() );
                } catch ( NameNotFoundException except ) {
                    throw new MappingException( "jdo.jndiNameNotFound", database.getJndi().getName() );
                } catch ( NamingException except ) {
                    throw new MappingException( except );
                }
                if ( ! ( ds instanceof DataSource ) )
                    throw new MappingException( "jdo.jndiNameNotFound", database.getJndi().getName() );
                return new SQLConnector( database.getName(), (DataSource) ds );
            } else {
                throw new MappingException( "jdo.missingDataSource", database.getName() );
            }
        } catch ( SQLException e ) {
            throw new PersistenceException( "Exception occurs while obtaining SQL connection", e );
        }
    }

    /**
     * Needed to process OQL queries of "CALL" type (using stored procedure
     * call). This feature is specific for JDO.
     * @param call Stored procedure call (without "{call")
     * @param paramTypes The types of the query parameters
     * @param javaClass The Java class of the query results
     * @param fields The field names
     * @param sqlTypes The field SQL types
     * @return null if this feature is not supported.
     */
    public PersistenceQuery getCallQuery( String call, Class[] paramTypes, Class javaClass,
                                          String[] fields, int[] sqlTypes )
    {
        return null;
    }


    /**
     * Some databases has some problems with some SQL types.
     * Usually it is enough to merely replace one SQL type by another.
     * @param sqlType The correspondent Java class for the SQL type in mapping.xml
     * @return The correspondent Java class for the SQL type that should be used instead.
     */
    public Class adjustSqlType( Class sqlType )
    {
        return sqlType;
    }

    /**
     * Many databases don't support setNull for "WHERE fld=?" and require "WHERE fld IS NULL".
     */
    public boolean supportsSetNullInWhere()
    {
        return false;
    }

    private class SQLConnector implements Connector {
        private String          _name;
        private String          _url;
        private Properties      _pros;
        private LogInterceptor  _log;
        private DataSource      _ds;

        private SQLConnector( String name, String url, Properties pros ) {
            _name  = name;
            _url   = url;
            _pros  = pros;
        }

        private SQLConnector( String name, DataSource ds ) {
            _name  = name;
            _ds    = ds;
        }

        public Object createConnection() throws PersistenceException {
            try {
                if ( _ds != null )
                    return _ds.getConnection();
                else
                    return DriverManager.getConnection( _url, _pros );
            } catch (SQLException except) {
                throw new PersistenceException( Messages.format("persist.nested", except), except );
            }
        }

        public void commitConnection( Object conn ) throws PersistenceException {
            try {
                ((Connection)conn).commit();
            } catch ( SQLException e ) {
                throw new PersistenceException( "SQLException during commit", e );
            }
        }

        public void rollbackConnection( Object conn ) throws PersistenceException {
            try {
                ((Connection)conn).rollback();
            } catch ( SQLException e ) {
                throw new PersistenceException( "SQLException during commit", e );
            }
        }

        public void closeConnection( Object conn ) throws PersistenceException {
            try {
                ((Connection)conn).close();
            } catch ( SQLException e ) {
            }
        }
    }
}


