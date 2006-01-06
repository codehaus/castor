/*
 * Copyright 2005 Bruce Snyder, Werner Guttmann, Ralf Joachim
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
 *
 * $Id$
 */
package org.castor.cache;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.castor.cache.simple.CountLimited;
import org.castor.cache.simple.TimeLimited;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.util.Messages;

/**
 * Registry for {@link CacheFactory} implementations obtained from the Castor
 * properties file and used by the JDO mapping configuration file.
 * 
 * @author <a href="mailto:ferret AT frii DOT com">Bruce Snyder</a>
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 */
public final class CacheFactoryRegistry {
    //--------------------------------------------------------------------------

    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta Commons
     *  Logging </a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(CacheFactoryRegistry.class);
    
    /** Property listing all available {@link Cache} implementations 
     *  (<tt>org.castor.cache.Factories</tt>). */
    public static final String PROP_FACTORY = "org.castor.cache.Factories";

    /** Default cache type to be used. */
    public static final String DEFAULT_TYPE = CountLimited.TYPE;
    
    /** Association between {@link Cache} name and factory implementation. */
    private Hashtable  _cacheFactories = new Hashtable();
    
    /** Should debugging be turned on for the cache? */
    private boolean _debug;

    //--------------------------------------------------------------------------

    /**
     * Construct an instance of CacheFactoryRegistry that uses given LocalConfiguration
     * to get required configuration properties.
     * 
     * @param config The LocalConfiguration.
     */
    public CacheFactoryRegistry(final LocalConfiguration config) {
        String prop = config.getProperty(PROP_FACTORY, "");
        StringTokenizer tokenizer = new StringTokenizer(prop, ", ");
        ClassLoader loader = CacheFactoryRegistry.class.getClassLoader();
        while (tokenizer.hasMoreTokens()) {
            String classname = tokenizer.nextToken();
            try {
                Class cls = loader.loadClass(classname);
                Object obj = cls.newInstance();
                CacheFactory factory = (CacheFactory) obj;
                _cacheFactories.put(factory.getCacheType(), factory);
            } catch (Exception ex) {
                LOG.error("Problem instantiating cache implementation: " + classname, ex);
            }
        }
        
        _debug = LogFactory.getLog(Cache.class).isDebugEnabled();
    }

    //--------------------------------------------------------------------------

    /**
     * Returns a {@link Cache} instance of the specified type, with the specified name
     * and if applicable capacity. Throws a CacheAcquireException if the named factory
     * is not supported.
     *
     * @param type Cache type identifier
     * @param name Class name.
     * @param capacity Cache capacity.
     * @param classLoader A ClassLoader instance.
     * @return A {@link Cache} instance.
     * @throws CacheAcquireException A cache of the type specified can not be acquired.
     */
    public Cache getCache(
            final String type, final String name, 
            final int capacity, final ClassLoader classLoader) 
    throws CacheAcquireException {
        
        // Initialize named parameters.
        Properties props = new Properties();
        // Add name for all cache types.
        props.put(Cache.PARAM_NAME, name);
        // Individual debugging is disabled by default.
        props.put(Cache.PARAM_DEBUG, Cache.DEFAULT_DEBUG);
        // Add capacity for CountLimited.
        props.put(CountLimited.PARAM_CAPACITY, new Integer(capacity));
        // Also add capacity as TTL for TimeLimited.
        props.put(TimeLimited.PARAM_TTL, new Integer(capacity));
        
        return getCache(type, props, classLoader);
    }
    
    /**
     * Returns a {@link Cache} instance of the specified type, with the specified
     * properties. Throws a CacheAcquireException if the named factory is not supported.
     *
     * @param type Cache type identifier
     * @param props Properties to initialize the cache with.
     * @param classLoader A ClassLoader instance.
     * @return A {@link Cache} instance.
     * @throws CacheAcquireException A cache of the type specified can not be acquired.
     */
    public Cache getCache(
            final String type, final Properties props, final ClassLoader classLoader) 
    throws CacheAcquireException {
        // Assume that a user does not have to specify a cache type in the mapping
        // file. For such a case, we still set the default value to be "count-limited".
        String cacheType = type;
        if (cacheType == null || "".equals(cacheType)) { cacheType = DEFAULT_TYPE; }
        
        CacheFactory cacheFactory = (CacheFactory) _cacheFactories.get(cacheType);
        if (cacheFactory == null) {
            LOG.error("Unknown cache type '" + type + "'");
            throw new CacheAcquireException("Unknown cache type '" + type + "'");
        }
        
        Cache cache = cacheFactory.getCache(classLoader);
        
        String debug = props.getProperty(Cache.PARAM_DEBUG, Cache.DEFAULT_DEBUG);
        if (_debug || Boolean.valueOf(debug).booleanValue()) {
            try {
                ClassLoader loader = CacheFactoryRegistry.class.getClassLoader();
                Class cls = loader.loadClass(DebuggingCacheProxy.class.getName());
                Class[] types = new Class[] {Cache.class};
                Object[] params = new Object[] {cache};
                cache = (Cache) cls.getConstructor(types).newInstance(params);
            } catch (ClassNotFoundException cnfe) {
                String msg = Messages.format("jdo.engine.classNotFound",
                        DebuggingCacheProxy.class.getName());
                LOG.error(msg, cnfe);
                throw new CacheAcquireException(msg, cnfe);
            } catch (IllegalAccessException iae) {
                String msg = Messages.format("jdo.engine.classIllegalAccess",
                        DebuggingCacheProxy.class.getName());
                LOG.error(msg, iae);
                throw new CacheAcquireException(msg, iae);
            } catch (NoSuchMethodException iae) {
                String msg = Messages.format("jdo.engine.classIllegalAccess",
                        DebuggingCacheProxy.class.getName());
                LOG.error(msg, iae);
                throw new CacheAcquireException(msg, iae);
            } catch (InvocationTargetException iae) {
                String msg = Messages.format("jdo.engine.classIllegalAccess",
                        DebuggingCacheProxy.class.getName());
                LOG.error(msg, iae);
                throw new CacheAcquireException(msg, iae);
            } catch (InstantiationException ie) {
                String msg = Messages.format("jdo.engine.classNotInstantiable",
                        DebuggingCacheProxy.class.getName());
                LOG.error(msg, ie);
                throw new CacheAcquireException(msg, ie);
            }
        }
        
        cache.initialize(props);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Successfully instantiated '" + type + "' cache: "
                    + props.get(Cache.PARAM_NAME));
        }
        
        return cache;
    }
    
    /**
     * Returns a collection of the current configured cache factories.
     * 
     * @return Collection of the current configured cache factories.
     */
    public Collection getCacheFactories() {
        return Collections.unmodifiableCollection(_cacheFactories.values());
    }
    
    /**
     * Returns a collection of the current configured cache factory names.
     *
     * @return Names of the configured cache factories.
     */
    public Collection getCacheNames() {
        return Collections.unmodifiableCollection(_cacheFactories.keySet());
    }
    
    /**
     * Is debugging for Cache enabled through logging system.
     * 
     * @return <code>true</code> if debugging is enabled, <code>false</code>
     *         otherwise.
     */
    public boolean isDebugEnabled() {
        return _debug;
    }

    //--------------------------------------------------------------------------
}
