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
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.jdo.conf.Database;
import org.exolab.castor.jdo.conf.Param;
import org.exolab.castor.jdo.conf.Mapping;
import org.exolab.castor.jdo.conf.DTDResolver;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingResolver;
import org.exolab.castor.persist.ClassHandler;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.PersistenceEngineFactory;
import org.exolab.castor.persist.PersistenceFactoryRegistry;
import org.exolab.castor.persist.spi.Persistence;
import org.exolab.castor.persist.spi.PersistenceFactory;


/**
 *
 *
 * @author <a href="arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
 */
public class DatabaseRegistry
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
    private MappingResolver   _mapResolver;


    /**
     * The database name of this database source.
     */
    private String            _name;


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
     * Construct a new database registry using a JDBC driver.
     *
     * @param name The database name
     * @param mapResolver The mapping resolver
     * @param factory Factory for persistence engines
     * @param jdbcURL The JDBC URL
     * @param jdbcProps The JDBC properties
     * @param logWriter For tracing messages
     * @throws MappingException Error occured when creating
     *  persistence engines for the mapping descriptors
     */
    DatabaseRegistry( String name, MappingResolver mapResolver, PersistenceFactory factory,
                      String jdbcUrl, Properties jdbcProps, PrintWriter logWriter )
        throws MappingException
    {
        _name = name;
        _mapResolver = mapResolver;
        _jdbcUrl = jdbcUrl;
        _jdbcProps = jdbcProps;
        _engine = new PersistenceEngineFactory().createEngine( _mapResolver, factory, logWriter );
        _byEngine.put( _engine, this );
    }


    /**
     * Construct a new database registry using a <tt>DataSource</tt>.
     *
     * @param name The database name
     * @param mapResolver The mapping resolver
     * @param factory Factory for persistence engines
     * @param dataSource The data source
     * @param logWriter For tracing messages
     * @throws MappingException Error occured when creating
     *  persistence engines for the mapping descriptors
     */
    DatabaseRegistry( String name, MappingResolver mapResolver, PersistenceFactory factory,
                    DataSource dataSource, PrintWriter logWriter )
        throws MappingException
    {
        _name = name;
        _mapResolver = mapResolver;
        _dataSource = dataSource;
        _engine = new PersistenceEngineFactory().createEngine( _mapResolver, factory, logWriter );
        _byEngine.put( _engine, this );
    }



    public MappingResolver getMappingResolver()
    {
        return _mapResolver;
    }


    public String getName()
    {
        return _name;
    }


    static DatabaseRegistry registerDatabase( String name, String engine, PrintWriter logWriter )
        throws MappingException
    {
        DatabaseRegistry   dbs;
        PersistenceFactory factory;

        if ( _defaultMapping == null ) {
            JDOMappingLoader mapping;

            try {
                mapping = new JDOMappingLoader( null );
                mapping.loadMapping( new InputSource( DatabaseRegistry.class.getClassLoader().getResourceAsStream( DefaultMapping ) ) );
                _defaultMapping = mapping;
            } catch ( IOException except ) {
                throw new MappingException( except );
            }
        }
        factory = PersistenceFactoryRegistry.getPersistenceFactory( engine );
        if ( factory == null )
            throw new MappingException( "jdo.noSuchEngine", engine );

        if ( name.startsWith( "jdbc:" ) ) {
            dbs = new DatabaseRegistry( name, _defaultMapping, factory,
                                        name, null, logWriter );
        } else if ( name.startsWith( "java:" ) ) {
            Object obj;

            try {
                obj = new InitialContext().lookup( name );
            } catch ( NameNotFoundException except ) {
                throw new MappingException( "jdo.jndiNameNotFound", name );
            } catch ( NamingException except ) {
                throw new MappingException( except );
            }
            if ( obj instanceof DataSource )
                dbs = new DatabaseRegistry( name, _defaultMapping, factory,
                                            (DataSource) obj, logWriter );
            else
                throw new MappingException( "jdo.jndiNameNotFound", name );
            _databases.put( name, dbs );
        }
        return null;
    }


    public static void loadDatabase( InputSource source, EntityResolver resolver,
                                     PrintWriter logWriter, ClassLoader loader )
        throws MappingException
    {
        Unmarshaller       unm;
        JDOMappingLoader   mapping;
        Mapping[]          mappings;
        Database           database;
        DatabaseRegistry   dbs;
        PersistenceFactory factory;

        unm = new Unmarshaller( Database.class );
        try {
            if ( resolver == null )
                unm.setEntityResolver( new DTDResolver() );
            else
                unm.setEntityResolver( new DTDResolver( resolver ) );
            if ( logWriter != null )
                unm.setLogWriter( logWriter );
            database = (Database) unm.unmarshal( source );

            mapping = new JDOMappingLoader( loader );
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

            if ( database.getEngine() == null || database.getEngine().getName() == null )
                throw new MappingException( "jdo.missingEngine", database.getName() );
            factory = PersistenceFactoryRegistry.getPersistenceFactory( database.getEngine().getName() );
            if ( factory == null )
                throw new MappingException( "jdo.noSuchEngine", database.getEngine().getName() );

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
                    throw new MappingException( "jdo.missingDriver", database.getDriver().getUrl() );

                props = new Properties();
                params = database.getDriver().listParams();
                while ( params.hasMoreElements() ) {
                    param = (Param) params.nextElement();
                    props.put( param.getName(), param.getValue() );
                }
                dbs = new DatabaseRegistry( database.getName(), mapping, factory,
                                            database.getDriver().getUrl(), props, logWriter );
            } else if ( database.getDataSource() != null ) {
                DataSource ds;

                ds = (DataSource) database.getDataSource().getParams();
                if ( ds == null )
                    throw new MappingException( "jdo.missingDataSource", database.getName() );
                dbs = new DatabaseRegistry( database.getName(), mapping, factory,
                                            ds, logWriter );
            } else if ( database.getJndi() != null ) {
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

                dbs = new DatabaseRegistry( database.getName(), mapping, factory,
                                            (DataSource) ds, logWriter );
            } else {
                throw new MappingException( "jdo.missingDataSource", database.getName() );
            }

            _databases.put( dbs.getName(), dbs );

        } catch ( MappingException except ) {
            throw except;
        } catch ( Exception except ) {
            throw new MappingException( except );
        }
    }


    public Connection createConnection()
        throws SQLException
    {
        if ( _dataSource != null )
            return _dataSource.getConnection();
        else
            return DriverManager.getConnection( _jdbcUrl, _jdbcProps );
    }


    static PersistenceEngine getPersistenceEngine( Class objType )
    {
        Enumeration      enum;
        DatabaseRegistry dbs;

        enum = _databases.elements();
        while ( enum.hasMoreElements() ) {
            dbs = (DatabaseRegistry) enum.nextElement();
            if ( dbs._mapResolver.getDescriptor( objType ) != null )
                return dbs._engine;
        }
        return null;
    }


    static PersistenceEngine getPersistenceEngine( DatabaseRegistry dbs )
    {
        return dbs._engine;
    }


    static DatabaseRegistry getDatabaseRegistry( Object obj )
    {
        Enumeration      enum;
        DatabaseRegistry dbs;

        enum = _databases.elements();
        while ( enum.hasMoreElements() ) {
            dbs = (DatabaseRegistry) enum.nextElement();
            if ( dbs._mapResolver.getDescriptor( obj.getClass() ) != null )
                return dbs;
        }
        return null;
    }


    public static synchronized DatabaseRegistry getDatabaseRegistry( String name )
        throws MappingException
    {
        DatabaseRegistry dbs;

        dbs = (DatabaseRegistry) _databases.get( name );
        return dbs;
    }


    static Connection createConnection( PersistenceEngine engine )
        throws SQLException
    {
        DatabaseRegistry dbs;

        dbs = (DatabaseRegistry) _byEngine.get( engine );
        if ( dbs._dataSource != null )
            return dbs._dataSource.getConnection();
        else
            return DriverManager.getConnection( dbs._jdbcUrl, dbs._jdbcProps );
    }


}
