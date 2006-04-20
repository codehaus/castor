/*
 * Copyright 2005 Tim Telcik, Werner Guttmann, Ralf Joachim
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
package org.castor.cache.distributed;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.castor.cache.AbstractBaseCache;
import org.castor.cache.CacheAcquireException;

/**
 * JCS (Java Caching System) implementation of Castor JDO Cache.
 * 
 * For more details of JCS, see http://jakarta.apache.org/jcs
 * 
 * @see org.apache.jcs.JCS
 * @author <a href="mailto:ttelcik AT hbf DOT com DOT au">Tim Telcik</a>
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Id$
 * @since 1.0
 */
public final class JcsCache extends AbstractBaseCache {
    //--------------------------------------------------------------------------

    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta Commons
     *  Logging </a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(JcsCache.class);

    /** The type of the cache. */
    public static final String TYPE = "jcs";
    
    /** The classname of the implementations factory class. */
    public static final String IMPLEMENTATION = "org.apache.jcs.JCS";
    
    /** The cache instance. */
    private Object _cache = null;

    //--------------------------------------------------------------------------
    // operations for life-cycle management of cache
    
    /**
     * @see org.castor.cache.Cache#initialize(java.util.Map)
     */
    public void initialize(final Map params) throws CacheAcquireException {
        initialize(IMPLEMENTATION, params);
    }

    /**
     * Normally called to initialize JcsCache. To be able to test the method
     * without having <code>org.apache.jcs.JCS</code> implementation, it can also
     * be called with a test implementations classname.
     * 
     * @param implementation Cache implementation classname to initialize.
     * @param params Parameters to initialize the cache (e.g. name, capacity).
     * @throws CacheAcquireException If cache can not be initialized.
     */
    public void initialize(final String implementation, final Map params)
    throws CacheAcquireException {
        super.initialize(params);

        try {
            ClassLoader ldr = this.getClass().getClassLoader();
            Class cls = ldr.loadClass(implementation);
            Method method = cls.getMethod("getInstance", new Class[] {String.class});
            _cache = method.invoke(null, new Object[] {getName()});
        } catch (Exception e) {
            String msg = "Error creating cache: " + e.getMessage();
            LOG.error(msg, e);
            throw new CacheAcquireException(msg);
        }
    }

    //--------------------------------------------------------------------------
    // getters/setters for cache configuration

    /**
     * @see org.castor.cache.Cache#getType()
     */
    public String getType() { return TYPE; }

    //--------------------------------------------------------------------------
    // query operations of map interface

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        throw new UnsupportedOperationException("size()");
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        throw new UnsupportedOperationException("isEmpty()");
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(final Object key) {
        Object value = invokeMethod(_cache, "get", 
                new Class[] {Object.class}, new Object[] {key});
        return (value != null);
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException("containsValue(Object)");
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(final Object key) {
        return invokeMethod(_cache, "get", 
                new Class[] {Object.class}, new Object[] {key});
    }

    //--------------------------------------------------------------------------
    // modification operations of map interface
    
    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(final Object key, final Object value) {
        Object oldValue = invokeMethod(_cache, "get", 
                new Class[] {Object.class}, new Object[] {key});
        invokeMethod(_cache, "put", 
                new Class[] {Object.class, Object.class}, new Object[] {key, value});
        return oldValue;
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(final Object key) {
        Object oldValue = invokeMethod(_cache, "get", 
                new Class[] {Object.class}, new Object[] {key});
        invokeMethod(_cache, "remove", 
                new Class[] {Object.class}, new Object[] {key});
        return oldValue;
    }

    //--------------------------------------------------------------------------
    // bulk operations of map interface
    
    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(final Map map) {
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            invokeMethod(_cache, "put", 
                    new Class[] {Object.class, Object.class},
                    new Object[] {entry.getKey(), entry.getValue()});
        }
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        invokeMethod(_cache, "clear", null, null);
    }

    //--------------------------------------------------------------------------
    // view operations of map interface
    
    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        throw new UnsupportedOperationException("keySet()");
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection values() {
        throw new UnsupportedOperationException("values()");
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        throw new UnsupportedOperationException("entrySet()");
    }

    //--------------------------------------------------------------------------
    // helper methods

    /**
     * Invoke method with given name and arguments having parameters of types
     * specified on the given target. Any possible exception will be catched and
     * IllegalStateException will be thrown instead.
     * 
     * @param target The target object to invoke the method on.
     * @param name The name of the method to invoke.
     * @param types The types of the parameters.
     * @param arguments The parameters.
     * @return The result of the method invocation.
     */
    private Object invokeMethod(final Object target, final String name,
            final Class[] types, final Object[] arguments) {
        try {
            Method method = target.getClass().getMethod(name, types);
            return method.invoke(target, arguments);
        } catch (SecurityException e) {
            LOG.error("SecurityException", e);
            throw new IllegalStateException(e.getMessage());
        } catch (NoSuchMethodException e) {
            LOG.error("NoSuchMethodException", e);
            throw new IllegalStateException(e.getMessage()); 
        } catch (IllegalArgumentException e) {
            LOG.error("IllegalArgumentException", e);
            throw new IllegalStateException(e.getMessage()); 
        } catch (IllegalAccessException e) {
            LOG.error("IllegalAccessException", e);
            throw new IllegalStateException(e.getMessage()); 
        } catch (InvocationTargetException e) {
            LOG.error("InvocationTargetException", e);
            throw new IllegalStateException(e.getMessage()); 
        }
    }
    
    //--------------------------------------------------------------------------
}
