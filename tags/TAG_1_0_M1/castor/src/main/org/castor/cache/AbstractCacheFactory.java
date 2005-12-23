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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.castor.util.Messages;

/**
 * Base implementation of {@link CacheFactory}. Users interested in supplying their
 * own cache implementations might want to extend this class to provide their
 * own {@link CacheFactory} instance. 
 *
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractCacheFactory implements CacheFactory {
    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     *  Commons Logging</a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(AbstractCacheFactory.class);
    
    /**
     * @see org.castor.cache.CacheFactory#getCache(java.lang.ClassLoader)
     */
    public final Cache getCache(final ClassLoader classLoader, final boolean debug)
    throws CacheAcquireException {
        ClassLoader ldr = classLoader;
        if (ldr == null) { ldr = Thread.currentThread().getContextClassLoader(); }
        
        Cache cache = null;
        try {
            cache = (Cache) Class.forName(getCacheClassName(), true, ldr).newInstance();
            
            if (debug) {
                Class cls = Class.forName(DebuggingCacheProxy.class.getName(), true, ldr);
                Class[] types = new Class[] {Cache.class};
                Object[] params = new Object[] {cache};
                cache = (Cache) cls.getConstructor(types).newInstance(params);
            }
        } catch (ClassNotFoundException cnfe) {
            String msg = Messages.format("jdo.engine.classNotFound",
                    getCacheClassName());
            LOG.error(msg, cnfe);
            throw new CacheAcquireException(msg, cnfe);
        } catch (IllegalAccessException iae) {
            String msg = Messages.format("jdo.engine.classIllegalAccess",
                    getCacheClassName());
            LOG.error(msg, iae);
            throw new CacheAcquireException(msg, iae);
        } catch (NoSuchMethodException iae) {
            String msg = Messages.format("jdo.engine.classIllegalAccess",
                    getCacheClassName());
            LOG.error(msg, iae);
            throw new CacheAcquireException(msg, iae);
        } catch (InvocationTargetException iae) {
            String msg = Messages.format("jdo.engine.classIllegalAccess",
                    getCacheClassName());
            LOG.error(msg, iae);
            throw new CacheAcquireException(msg, iae);
        } catch (InstantiationException ie) {
            String msg = Messages.format("jdo.engine.classNotInstantiable",
                    getCacheClassName());
            LOG.error(msg, ie);
            throw new CacheAcquireException(msg, ie);
        }
        
        return cache;
    }

    /**
     * @see org.castor.cache.CacheFactory#shutdown()
     */
    public void shutdown() { }
}
