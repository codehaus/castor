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
package org.castor.persist.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.castor.cache.Cache;
import org.castor.cache.CacheAcquireException;
import org.castor.cache.CacheFactory;
import org.castor.cache.simple.CountLimited;
import org.castor.cache.simple.TimeLimited;
import org.exolab.castor.util.LocalConfiguration;

/**
 * Registry for {@link CacheFactory} implementations obtained from the Castor
 * properties file and used by the JDO mapping configuration file.
 * 
 * @author <a href="mailto:ferret AT frii DOT com">Bruce Snyder</a>
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 */
public final class CacheRegistry {
    //--------------------------------------------------------------------------

    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta Commons
     *  Logging </a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(CacheRegistry.class);
    
    /** Property listing all available {@link Cache} implementations 
     *  (<tt>org.castor.jdo.cacheFactories</tt>). */
    private static final String PROP_FACTORY = "org.castor.jdo.cacheFactories";

    /** Property if access to cache should be logged 
     *  (<tt>org.castor.jdo.cacheDebug</tt>). */
    private static final String PROP_DEBUG = "org.castor.jdo.cacheDebug";

    /** Default cache type to be used. */
    private static final String DEFAULT_TYPE = "count-limited";
    
    /** Association between {@link Cache} name and factory implementation. */
    private static Hashtable  _cacheFactories;
    
    /** Should debugging be turned on for the cache? */
    private static boolean _debug;

    //--------------------------------------------------------------------------

    /**
     * Returns a {@link Cache} instance of the specified type, with the specified name
     * and if applicable capacity. If not done before, the factory class names are
     * loaded from the Castor properties file. Throws a CacheAcquireException if
     * the named factory is not supported.
     *
     * @param type Cache type identifier
     * @param name Class name.
     * @param capacity Cache capacity.
     * @param classLoader A ClassLoader instance.
     * @return A {@link Cache} instance.
     * @throws CacheAcquireException A cache of the type specified can not be acquired.
     */
    public static Cache getCache(
            final String type, final String name, 
            final int capacity, final ClassLoader classLoader) 
    throws CacheAcquireException {
        load();
        
        // Assume that a user does not have to specify a cache type in the mapping
        // file. For such a case, we still set the default value to be "count-limited".
        String cacheType = type;
        if (cacheType == null || "".equals(cacheType)) { cacheType = DEFAULT_TYPE; }
        
        CacheFactory cacheFactory = (CacheFactory) _cacheFactories.get(cacheType);
        
        if (cacheFactory != null) {
            // Initialize named parameters.
            HashMap params = new HashMap();
            // Add name for all cache types.
            params.put(Cache.PARAM_NAME, name);
            // Add capacity for CountLimited.
            params.put(CountLimited.PARAM_CAPACITY, new Integer(capacity));
            // Also add capacity as TTL for TimeLimited.
            params.put(TimeLimited.PARAM_TTL, new Integer(capacity));
            
            Cache cache = cacheFactory.getCache(classLoader, _debug);
            cache.initialize(params);
            
            if (LOG.isDebugEnabled()) {
                LOG.debug("Successfully instantiated '" + type + "' cache: " + name);
            }
            
            return cache;
        } else {
            LOG.error("Unknown cache type '" + type + "'");
            throw new CacheAcquireException("Unknown cache type '" + type + "'");
        }
    }
    
    /**
     * Returns a collection of the current configured cache factories.
     * 
     * @return Collection of the current configured cache factories.
     */
    public static Collection getCacheFactories() {
        load();
        return Collections.unmodifiableCollection(_cacheFactories.values());
    }
    
    /**
     * Returns a collection of the current configured cache factory names.
     *
     * @return Names of the configured cache factories.
     */
    public static Collection getCacheNames() {
        load();
        return Collections.unmodifiableCollection(_cacheFactories.keySet());
    }

    /**
     * Shell caches be wrapped by a DebuggingCacheProxy when constructed?
     * 
     * @return <code>true</code> if caches created with the CacheRegistry shell be
     *         wrapped with a DebuggingCacheProxy, <code>false</code> otherwise.
     */
    public static boolean useDebugProxy() {
        load();
        return _debug;
    }

    /**
     * Load the {@link CacheFactory} implementations from the properties file, if
     * not loaded before.
     */
    private static synchronized void load() {
        if (_cacheFactories == null) {
            _cacheFactories = new Hashtable();
            
            String prop = LocalConfiguration.getInstance().getProperty(PROP_FACTORY, "");
            StringTokenizer tokenizer = new StringTokenizer(prop, ", ");
            while (tokenizer.hasMoreTokens()) {
                prop = tokenizer.nextToken();
                try {
                    Class cls = CacheRegistry.class.getClassLoader().loadClass(prop);
                    CacheFactory cacheFactory = (CacheFactory) cls.newInstance();
                    _cacheFactories.put(cacheFactory.getCacheType(), cacheFactory);
                } catch (Exception except) {
                    LOG.error("Problem instantiating cache implementation.", except);
                }
            }
            
            String debug = LocalConfiguration.getInstance().getProperty(PROP_DEBUG, "");
            _debug = Boolean.valueOf(debug).booleanValue();
        }
    }

    //--------------------------------------------------------------------------

    /**
     * Hide utility class constructor.
     */
    private CacheRegistry () { }

    //--------------------------------------------------------------------------
}
