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
import java.io.IOException;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.odmg.ODMGException;
import org.odmg.DatabaseNotFoundException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.jdo.conf.Database;
import org.exolab.castor.jdo.conf.Param;
import org.exolab.castor.jdo.conf.Mapping;
import org.exolab.castor.jdo.conf.DTDResolver;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.ClassDesc;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.PersistenceEngineFactory;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceFactory;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DatabaseSource
{


    /**
     * The JDBC URL when using a JDBC driver.
     */
    private String            _jdbcUrl;
    
    
    /**
     * The properties when using a JDBC driver.
     */
    private Properties        _jdbcProps;


    /**
     * The data source when using a DataSource.
     */
    private DataSource        _dataSource;


    /**
     * The map resolver for this database source.
     */
    private MappingResolver    _mapResolver;


    /**
     * The database name of this database source.
     */
    private String            _dbName;


    /**
     * The presistence engine for this database source.
     */
    private PersistenceEngine _engine;


    /**
     * Listings of all databases by name.
     */
    private static Hashtable  _databases = new Hashtable();


    private static Hashtable  _byEngine = new Hashtable();




    private static MappingResolver _defaultMapping;
    public static String  DefaultMapping = "mapping.xml";


    /**
     * Construct a new database source using a JDBC driver.
     *
     * @param dbName The database name
     * @param mapResolver The mapping resolver
     * @param sqlFactory Factory for SQL engines
     * @param jdbcURL The JDBC URL
     * @param jdbcProps The JDBC properties
     * @throws MappingException Error occured when creating
     *  persistence engines for the mapping descriptors
     */
    DatabaseSource( String dbName, MappingResolver mapResolver, SQLEngineFactory sqlFactory,
                    String jdbcUrl, Properties jdbcProps )
        throws MappingException
    {
        _dbName = dbName;
        _mapResolver = mapResolver;
        _jdbcUrl = jdbcUrl;
        _jdbcProps = jdbcProps;
        _engine = new PersistenceEngineFactory().createEngine( _mapResolver, sqlFactory, null );
        _byEngine.put( _engine, this );
    }


    /**
     * Construct a new database source using a <tt>DataSource</tt>.
     *
     * @param dbName The database name
     * @param mapResolver The mapping resolver
     * @param sqlFactory Factory for SQL engines
     * @param dataSource The data source
     * @throws MappingException Error occured when creating
     *  persistence engines for the mapping descriptors
     */
    DatabaseSource( String dbName, MappingResolver mapResolver, SQLEngineFactory sqlFactory,
                    DataSource dataSource )
        throws MappingException
    {
        _dbName = dbName;
        _mapResolver = mapResolver;
        _dataSource = dataSource;
        _engine = new PersistenceEngineFactory().createEngine( _mapResolver, sqlFactory, null );
        _byEngine.put( _engine, this );
    }



    public MappingResolver getMappingResolver()
    {
        return _mapResolver;
    }


    public String getDBName()
    {
        return _dbName;
    }


    static DatabaseSource registerDatabase( String dbName )
        throws MappingException
    {
        DatabaseSource dbs;

        if ( _defaultMapping == null ) {
            JDOMappingHelper mapping;

            try {
                mapping = new JDOMappingHelper( null );
                mapping.loadMapping( DefaultMapping );
                _defaultMapping = mapping;
            } catch ( IOException except ) {
                throw new MappingException( except );
            }
        }
        
        if ( dbName.startsWith( "jdbc:" ) ) {
            dbs = new DatabaseSource( dbName, _defaultMapping, new SQLEngineFactory(),
                                      dbName, null );
        } else if ( dbName.startsWith( "java:" ) ) {
            Object obj;

            try {
                obj = new InitialContext().lookup( dbName );
            } catch ( NameNotFoundException except ) {
                throw new MappingException( "The JNDI name " + dbName + " does not map to a DataSource" );
            } catch ( NamingException except ) {
                throw new MappingException( except );
            }
            if ( obj instanceof DataSource ) {
                dbs = new DatabaseSource( dbName, _defaultMapping, new SQLEngineFactory(),
                                          (DataSource) obj );
            } else {
                throw new MappingException( "The JNDI name " + dbName + " does not map to a DataSource" );
            }
            _databases.put( dbName, dbs );
        }
        return null;
    }


    public static void loadDatabase( InputSource source, EntityResolver resolver,
                                     PrintWriter logWriter )
        throws MappingException
    {
        Unmarshaller     unm;
        JDOMappingHelper mapping;
        Mapping[]        mappings;
        Database         database;
        DatabaseSource   dbs;
        
        unm = new Unmarshaller( Database.class );
        try {
            if ( resolver == null )
                unm.setEntityResolver( new DTDResolver() );
            else
                unm.setEntityResolver( new DTDResolver( resolver ) );
            if ( logWriter != null )
                unm.setLogWriter( logWriter );
            database = (Database) unm.unmarshal( source );

            mapping = new JDOMappingHelper( null );
            if ( resolver != null )
                mapping.setEntityResolver( resolver );
            if ( logWriter != null )
                mapping.setLogWriter( logWriter );
            if ( source.getSystemId() != null )
                mapping.setBaseURL( source.getSystemId() );
            mappings = database.getMappings();
            for ( int i = 0 ; i < mappings.length ; ++i ) {
                mapping.loadMapping( mappings[ i ].getHref() );
            }

            if ( database.getDriver() != null ) {
                Properties  props;
                Enumeration params;
                Param       param;
          
                if ( database.getDriver().getClassName() != null ) {
                    try {
                        Class.forName( database.getDriver().getClassName() );
                    } catch ( ClassNotFoundException except ) {
                        throw new MappingException( except );
                    }
                }
                if ( DriverManager.getDriver( database.getDriver().getUrl() ) == null )
                    throw new MappingException( "No suitable driver found for URL " + database.getDriver().getUrl() +
                                                " - check if URL is correct and driver accessible in classpath" );

                props = new Properties();
                params = database.getDriver().listParams();
                while ( params.hasMoreElements() ) {
                    param = (Param) params.nextElement();
                    props.put( param.getName(), param.getValue() );
                }
                dbs = new DatabaseSource( database.getDbName(), mapping, new SQLEngineFactory(),
                                          database.getDriver().getUrl(), props );
            } else if ( database.getDataSource() != null ) {
                DataSource ds;
          
                ds = (DataSource) database.getDataSource().getParams();
                if ( ds == null )
                    throw new MappingException( "No data source specified for database " +
                                                database.getDbName() );
                dbs = new DatabaseSource( database.getDbName(), mapping, new SQLEngineFactory(), ds );
            } else if ( database.getDataSourceRef() != null ) {
                Object    ds;
          
                try {
                    ds = new InitialContext().lookup( database.getDbName() );
                } catch ( NameNotFoundException except ) {
                    throw new MappingException( "The JNDI name " + database.getDbName() +
                                                " does not map to a DataSource" );
                } catch ( NamingException except ) {
                    throw new MappingException( except );
                }
                if ( ! ( ds instanceof DataSource ) )
                    throw new MappingException( "The JNDI name " + database.getDbName() +
                                                " does not map to a DataSource" );
                dbs = new DatabaseSource( database.getDbName(), mapping, new SQLEngineFactory(), (DataSource) ds );
            } else {
                throw new MappingException( "Bad" );
            }

            _databases.put( dbs.getDBName(), dbs );
                                      
        } catch ( MappingException except ) {
            throw except;
        } catch ( Exception except ) {
            throw new MappingException( except );
        }
    }


    static PersistenceEngine getPersistenceEngine( Class objType )
    {
        Enumeration    enum;
        DatabaseSource dbs;
        
        enum = _databases.elements();
        while ( enum.hasMoreElements() ) {
            dbs = (DatabaseSource) enum.nextElement();
            if ( dbs._mapResolver.getDescriptor( objType ) != null )
                return dbs._engine;
        }
        return null;
    }


    static PersistenceEngine getPersistenceEngine( DatabaseSource dbs )
    {
        return dbs._engine;
    }


    static DatabaseSource getDatabaseSource( Object obj )
    {
        Enumeration    enum;
        DatabaseSource dbs;
        
        enum = _databases.elements();
        while ( enum.hasMoreElements() ) {
            dbs = (DatabaseSource) enum.nextElement();
            if ( dbs._mapResolver.getDescriptor( obj.getClass() ) != null )
                return dbs;
        }
        return null;
    }


    static synchronized DatabaseSource getDatabaseSource( String dbName )
        throws MappingException
    {
        DatabaseSource dbs;
        
        dbs = (DatabaseSource) _databases.get( dbName );
        if ( dbs == null )
            dbs = registerDatabase( dbName );
        return dbs;
    }
    
    
    static Connection createConnection( PersistenceEngine engine )
        throws SQLException
    {
        DatabaseSource dbs;
        
        dbs = (DatabaseSource) _byEngine.get( engine );
        if ( dbs._dataSource != null )
            return dbs._dataSource.getConnection();
        else
            return DriverManager.getConnection( dbs._jdbcUrl, dbs._jdbcProps );
    }
    

    static class SQLEngineFactory
        implements PersistenceFactory
    {
        
        public Persistence getPersistence( ClassDesc clsDesc, PrintWriter logWriter )
            throws MappingException
        {
            try {
                return new SQLEngine( (JDOClassDesc) clsDesc, logWriter );
            } catch ( MappingException except ) {
                logWriter.println( except.toString() );
                return null;
            }
        }
        
    }
    

}
