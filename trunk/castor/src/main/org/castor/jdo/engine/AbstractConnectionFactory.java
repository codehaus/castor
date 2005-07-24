/*
 * Copyright 2005 Werner Guttmann, Ralf Joachim
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

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.castor.jdo.conf.Database;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.LockEngine;
import org.exolab.castor.persist.PersistenceEngineFactory;
import org.exolab.castor.persist.PersistenceFactoryRegistry;
import org.exolab.castor.persist.spi.PersistenceFactory;
import org.exolab.castor.util.Messages;

/**
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 */
public abstract class AbstractConnectionFactory implements ConnectionFactory {
    //--------------------------------------------------------------------------

    /** The name of the generic SQL engine, if no SQL engine specified. */
    public static final String GENERIC_ENGINE = "generic";

    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     *  Commons Logging</a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(AbstractConnectionFactory.class);

    //--------------------------------------------------------------------------
    
    /** Has the factory been initialized? */
    private boolean             _initialized = false;
    
    /** The database configuration. */
    private Database            _database;
    
    /** The mapping to load. */
    private Mapping             _mapping;
    
    /** The LockEngine only available after initialization. */
    private LockEngine          _engine = null;
    
    //--------------------------------------------------------------------------

    /**
     * Constructs a new AbstractConnectionFactory with given database and mapping.
     * 
     * @param database  The database configuration.
     * @param mapping   The mapping to load.
     */
    protected AbstractConnectionFactory(final Database database, final Mapping mapping) {
        _database = database;
        _mapping = mapping;
    }
    
    //--------------------------------------------------------------------------

    /**
     * Initialize factory if it had not been initialized before.
     * 
     * @throws MappingException If concrete factory or LockEngine fail to initialize
     *                          or mapping could not be loaded. 
     */
    public final void initialize() throws MappingException {
        // If the factory was already initialized, ignore
        // this request to initialize it.
        if (!_initialized) {
            initializeMapping();
            initializeEngine();
            initializeFactory();
            
            _initialized = true;
        }
    }
    
    /**
     * Load mapping.
     * 
     * @throws MappingException If mapping could not be loaded.
     */
    private void initializeMapping() throws MappingException {
        try {
            // Initialize all the mappings of the database.
            Enumeration mappings = _database.enumerateMapping();
            org.exolab.castor.jdo.conf.Mapping mapConf;
            while (mappings.hasMoreElements()) {
                mapConf = (org.exolab.castor.jdo.conf.Mapping) mappings.nextElement();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Loading the mapping descriptor: " + mapConf.getHref());
                }
                
                if (mapConf.getHref() != null) {
                    _mapping.loadMapping(mapConf.getHref());
                }
            }
        } catch (MappingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MappingException(ex);
        }
    }
    
    /**
     * Initialize LockEngine.
     * 
     * @throws MappingException If Lockengine could not be initialized.
     */
    private void initializeEngine() throws MappingException {
        // Complain if no database engine was specified, otherwise get
        // a persistence factory for that database engine.
        PersistenceFactory factory;
        if (_database.getEngine() == null) {
            factory = PersistenceFactoryRegistry.getPersistenceFactory(GENERIC_ENGINE);
        } else {
            factory = PersistenceFactoryRegistry.getPersistenceFactory(
                    _database.getEngine());
        }
        
        if (factory == null) {
            String msg = Messages.format("jdo.noSuchEngine", _database.getEngine());
            LOG.error(msg);
            throw new MappingException(msg);
        }
        
        _engine = new PersistenceEngineFactory().createEngine(
                this, _mapping.getResolver(Mapping.JDO, factory), factory);
    }
    
    /**
     * Initialize the concrete factory.
     * 
     * @throws MappingException If concrete factory could not be initialized.
     */
    protected abstract void initializeFactory() throws MappingException;
    
    //--------------------------------------------------------------------------
    
    /**
     * Get the database configuration.
     * 
     * @return The database configuration.
     */
    public final Database getDatabase() { return _database; }
    
    /**
     * Get the mapping to load.
     * 
     * @return The mapping to load.
     */
    public final Mapping getMapping() { return _mapping; }
    
    /**
     * Get the LockEngine only available after initialization.
     * 
     * @return The LockEngine.
     */
    public final LockEngine getEngine() { return _engine; }

    //--------------------------------------------------------------------------
}
