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
 *
 * $Id$
 */
package org.castor.cache;

import java.util.Map;

/**
 * Interface specification for performance caches as used in Castor. Please implement 
 * this interface if you wish to provide your own cache implementation. 
 * 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 * @since 1.0
 */
public interface Cache extends Map {
    //--------------------------------------------------------------------------
    
    /** Mapped initialization parameter: name */
    String PARAM_NAME = "name";
    
    /** Default cache name to be used. */
    String DEFAULT_NAME = "";
    
    //--------------------------------------------------------------------------
    // operations for life-cycle management of cache
    
    /**
     * Lyfe-cycle method to allow custom initialization of cache implementations.
     * 
     * @param params Parameters to initialize the cache (e.g. name, capacity).
     * @throws CacheAcquireException If cache can not be initialized.
     */
    void initialize(Map params) throws CacheAcquireException;
    
    /** 
     * Life-cycle method to allow custom resource cleanup for a cache implementation.
     */
    void close();
    
    //--------------------------------------------------------------------------
    // getters/setters for cache configuration

    /**
     * Indicates the type of this cache.
     * 
     * @return The cache type.
     */
    String getType();

    /**
     * Get virtual name of this cache. Castor sets the cache name to the class name of the
     * objects stored in the cache.
     * 
     * @return The cache name.
     */
    String getName();
    
    //--------------------------------------------------------------------------
    // additional operations of cache interface
    
    /**
     * Remove the mapping identified by key from the cache.
     * 
     * @param key the key that needs to be removed.
     */
    void expire(Object key);
    
    /**
     * Removes all mappings from the cache.
     */
    void expireAll();

    //--------------------------------------------------------------------------
}
