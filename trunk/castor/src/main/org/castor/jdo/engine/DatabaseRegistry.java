/*
 * Copyright 2005 Ralf Joachim
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
package org.castor.jdo.engine;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.exolab.castor.jdo.conf.Database;
import org.exolab.castor.jdo.conf.JdoConf;
import org.exolab.castor.jdo.engine.JDOConfLoader;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.util.Messages;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @author <a href="mailto:ferret AT frii dot com">Bruce Snyder</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 */
public final class DatabaseRegistry {
    //--------------------------------------------------------------------------

    /** Property telling if database should be initialized when loading. */
    private static final String INITIALIZE_AT_LOAD = 
        "org.exolab.castor.jdo.DatabaseInitializeAtLoad";
    
    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     *  Commons Logging</a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(DatabaseRegistry.class);

    /** Map of all registered connection factories by name. */
    private static final Hashtable  FACTORIES = new Hashtable();

    //--------------------------------------------------------------------------

    /**
     * Instantiates a database instance from an in-memory JDO configuration.
     * 
     * @param  jdoConf  An in-memory JDO configuration. 
     * @param  resolver An entity resolver.
     * @param  loader   A class loader
     * @throws MappingException If the database cannot be instantiated/loadeed.
     */
    public static synchronized void loadDatabase(final JdoConf jdoConf,
                                                 final EntityResolver resolver,
                                                 final ClassLoader loader)
    throws MappingException {
        Database[] databases = JDOConfLoader.getDatabases(jdoConf);
        loadDatabase(databases, resolver, loader, null);
    }

    /**
     * Instantiates a database instance from the JDO configuration file
     * 
     * @param  source   {@link InputSource} pointing to the JDO configuration. 
     * @param  resolver An entity resolver.
     * @param  loader   A class loader
     * @throws MappingException If the database cannot be instantiated/loadeed.
     */
    public static synchronized void loadDatabase(final InputSource source,
                                                 final EntityResolver resolver,
                                                 final ClassLoader loader)
    throws MappingException {
        // Load the JDO configuration file from the specified input source.
        Database[] databases = JDOConfLoader.getDatabases(source, resolver);
        loadDatabase(databases, resolver, loader, source.getSystemId());
    }
    
    /**
     * Creates a entry for every database and associates them with their name in a
     * map. It then instantiates all databases if
     * 'org.exolab.castor.jdo.DatabaseInitializeAtLoad' key can not be found or is
     * set to <code>true</code> in castor.properties file. If above property is set
     * to <code>false</code> it will instantiate all databases only when they are
     * needed.
     * 
     * @param  databases    Database configuration instances. 
     * @param  resolver     An entity resolver.
     * @param  loader       A class loader
     * @param  baseURI      The base URL for the mapping
     * @throws MappingException If the database cannot be instantiated/loadeed.
     */
    private static synchronized void loadDatabase(final Database[] databases,
                                                  final EntityResolver resolver,
                                                  final ClassLoader loader,
                                                  final String baseURI)
    throws MappingException {
        // Do we need to initialize database now or should we
        // wait until we want to use it.
        LocalConfiguration cfg = LocalConfiguration.getInstance();
        String property = cfg.getProperty(INITIALIZE_AT_LOAD, "true");
        boolean init = Boolean.valueOf(property).booleanValue();
        
        // Load the JDO configuration file from the specified input source.
        // databases = JDOConfLoader.getDatabases(baseURI, resolver);
        Database database;
        Mapping mapping;
        AbstractConnectionFactory factory;
        for (int i = 0; i < databases.length; i++) {
            database = databases[i];

            // Load the mapping file from the URL specified in the database
            // configuration file, relative to the configuration file.
            // Fail if cannot load the mapping for whatever reason.
            mapping = new Mapping(loader);
            if (resolver != null) { mapping.setEntityResolver(resolver); }
            if (baseURI != null) { mapping.setBaseURL(baseURI); }
            
            factory = DatabaseRegistry.createFactory(database, mapping);
            if (init) { factory.initialize(); }
            if (FACTORIES.put(database.getName(), factory) != null) {
                LOG.warn(Messages.format("jdo.configLoadedTwice", database.getName()));
            }
        }
    }
    
    /**
     * Factory methode to create a ConnectionFactory for given database configuration
     * and with given mapping.
     * 
     * @param database  The database configuration.
     * @param mapping   The mapping to load.
     * @return The ConnectionFactory.
     * @throws MappingException If the database cannot be instantiated/loadeed.
     */
    private static AbstractConnectionFactory createFactory(final Database database,
                                                           final Mapping mapping)
    throws MappingException {
        AbstractConnectionFactory factory;
        
        if (database.getDatabaseChoice() == null) {
            String msg = Messages.format("jdo.missingDataSource", database.getName());
            LOG.error(msg);
            throw new MappingException(msg);
        }
        
        if (database.getDatabaseChoice().getDriver() != null) {
            // JDO configuration file specifies a driver, use the driver
            // properties to create a new registry object.
            factory = new DriverConnectionFactory(database, mapping);
        } else if (database.getDatabaseChoice().getDataSource() != null) {
            // JDO configuration file specifies a DataSource object, use the
            // DataSource which was configured from the JDO configuration file
            // to create a new registry object.
            factory = new DataSourceConnectionFactory(database, mapping);
        } else if (database.getDatabaseChoice().getJndi() != null) {
            // JDO configuration file specifies a DataSource lookup through JNDI, 
            // locate the DataSource object frome the JNDI namespace and use it.
            factory = new JNDIConnectionFactory(database, mapping);
        } else {
            String msg = Messages.format("jdo.missingDataSource", database.getName());
            LOG.error(msg);
            throw new MappingException(msg);
        }
        
        return factory;
    }

    /**
     * Check if any database configuration has been loaded.
     * 
     * @return <code>true</code> if a databases configuration has been loaded.
     */
    public static boolean hasDatabaseRegistries() {
        return (!FACTORIES.isEmpty());
    }
    
    /**
     * Check if database configuration with given name has been loaded.
     * 
     * @param  name     Name of the database to check if loaded.
     * @return <code>true</code> if databases configuration has been loaded.
     */
    public static boolean isDatabaseRegistred(final String name) {
        return FACTORIES.containsKey(name);
    }
    
    /**
     * Get the ConnectionFactory for the given database name.
     * 
     * @param  name     Name of the database configuration.
     * @return The ConnectionFactory for the given database name.
     * @throws MappingException If database can not be instantiated.
     */
    public static AbstractConnectionFactory getConnectionFactory(final String name)
    throws MappingException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetching ConnectionFactory: " + name);
        }
        
        AbstractConnectionFactory factory;
        factory = (AbstractConnectionFactory) FACTORIES.get(name);
        
        if (factory == null) {
            String msg = Messages.format("jdo.missingDataSource", name);
            LOG.error(msg);
            throw new MappingException(msg);
        }
        
        factory.initialize();
        return factory;
    }

    /**
     * Reset all database configurations.
     */
    public static void clear() {
        FACTORIES.clear();

        // reset the JDO configuration data to re-enable loadConfiguration()
        JDOConfLoader.deleteConfiguration();
    }
    
    //--------------------------------------------------------------------------
    
    /**
     * Hide constructor of utility class.
     */
    private DatabaseRegistry() { }
    
    //--------------------------------------------------------------------------
}
