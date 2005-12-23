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
package utf.org.castor.persist.cache;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.castor.cache.Cache;
import org.castor.cache.CacheAcquireException;
import org.castor.cache.distributed.CoherenceCache;
import org.castor.cache.distributed.CoherenceCacheFactory;
import org.castor.cache.distributed.FKCache;
import org.castor.cache.distributed.FKCacheFactory;
import org.castor.cache.distributed.JCache;
import org.castor.cache.distributed.JCacheFactory;
import org.castor.cache.distributed.JcsCache;
import org.castor.cache.distributed.JcsCacheFactory;
import org.castor.cache.simple.CountLimited;
import org.castor.cache.simple.CountLimitedFactory;
import org.castor.cache.simple.NoCache;
import org.castor.cache.simple.NoCacheFactory;
import org.castor.cache.simple.TimeLimited;
import org.castor.cache.simple.TimeLimitedFactory;
import org.castor.cache.simple.Unlimited;
import org.castor.cache.simple.UnlimitedFactory;
import org.castor.persist.cache.CacheRegistry;

/**
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 * @since 1.0
 */
public final class TestCacheRegistry extends TestCase {
    private static final boolean DISABLE_LOGGING = true;
    
    public static Test suite() {
        TestSuite suite = new TestSuite("CacheRegistry Tests");

        suite.addTest(new TestCacheRegistry("testUseDebugProxy"));
        suite.addTest(new TestCacheRegistry("testGetCacheNames"));
        suite.addTest(new TestCacheRegistry("testGetCacheFactories"));
        suite.addTest(new TestCacheRegistry("testGetCache"));

        return suite;
    }

    public TestCacheRegistry(final String name) { super(name); }

    public void testUseDebugProxy() {
        assertFalse(CacheRegistry.useDebugProxy());
    }

    public void testGetCacheNames() {
        Collection col = CacheRegistry.getCacheNames();
        assertEquals(8, col.size());
        assertTrue(col.contains(CountLimited.TYPE));
        assertTrue(col.contains(NoCache.TYPE));
        assertTrue(col.contains(TimeLimited.TYPE));
        assertTrue(col.contains(Unlimited.TYPE));
        assertTrue(col.contains(CoherenceCache.TYPE));
        assertTrue(col.contains(FKCache.TYPE));
        assertTrue(col.contains(JCache.TYPE));
        assertTrue(col.contains(JcsCache.TYPE));
    }

    public void testGetCacheFactories() {
        Collection col = CacheRegistry.getCacheFactories();
        assertEquals(8, col.size());
        assertTrue(containsInstanceOf(col, CountLimitedFactory.class));
        assertTrue(containsInstanceOf(col, NoCacheFactory.class));
        assertTrue(containsInstanceOf(col, TimeLimitedFactory.class));
        assertTrue(containsInstanceOf(col, UnlimitedFactory.class));
        assertTrue(containsInstanceOf(col, CoherenceCacheFactory.class));
        assertTrue(containsInstanceOf(col, FKCacheFactory.class));
        assertTrue(containsInstanceOf(col, JCacheFactory.class));
        assertTrue(containsInstanceOf(col, JcsCacheFactory.class));
    }
    
    private boolean containsInstanceOf(final Collection col, final Class cls) {
        Iterator iter = col.iterator();
        while (iter.hasNext()) {
            Object object = iter.next();
            if (cls.isInstance(object)) { return true; }
        }
        return false;
    }

    public void testGetCache() throws CacheAcquireException {
        Logger logger = Logger.getLogger(CacheRegistry.class);
        Level level = logger.getLevel();
        
        Cache cache = null;
        
        if (DISABLE_LOGGING) { logger.setLevel(Level.FATAL); }

        try {
            cache = getCache("not-existing", 0);
            fail("Getting a non existent cache should throw an exception.");
        } catch (CacheAcquireException ex) {
            assertNull(cache);
        }

        logger.setLevel(level);
        
        cache = getCache("", 10); 
        assertEquals("count-limited", cache.getType());
        assertTrue(cache instanceof CountLimited);
        assertEquals(10, ((CountLimited) cache).getCapacity());
        
        cache = getCache(null, 20); 
        assertEquals("count-limited", cache.getType());
        assertTrue(cache instanceof CountLimited);
        assertEquals(20, ((CountLimited) cache).getCapacity());
        
        cache = getCache("count-limited", 3); 
        assertEquals("count-limited", cache.getType());
        assertTrue(cache instanceof CountLimited);
        assertEquals(3, ((CountLimited) cache).getCapacity());
        
        cache = getCache("none", 10); 
        assertEquals("none", cache.getType());
        assertTrue(cache instanceof NoCache);

        cache = getCache("time-limited", 10);
        assertEquals("time-limited", cache.getType());
        assertTrue(cache instanceof TimeLimited);
        assertEquals(10, ((TimeLimited) cache).getTTL());
        
        cache = getCache("unlimited", 10); 
        assertEquals("unlimited", cache.getType());
        assertTrue(cache instanceof Unlimited);
        
        // Creation of distributed caches can not be tested without having their
        // implementations available on the classpath but testing the construction
        // isn't necessary as we have done it at tests of their factories.
        //
        // If you still like to test you only need to comment in one or all of the
        // following sections.

//        cache = getCache("coherence", 10); 
//        assertEquals("coherence", cache.getType());
//        assertTrue(cache instanceof CoherenceCache);

//        cache = getCache("fkcache", 10); 
//        assertEquals("fkcache", cache.getType());
//        assertTrue(cache instanceof FKCache);

//        cache = getCache("jcache", 10); 
//        assertEquals("jcache", cache.getType());
//        assertTrue(cache instanceof JCache);

//        cache = getCache("jcs", 10); 
//        assertEquals("jcs", cache.getType());
//        assertTrue(cache instanceof JcsCache);
    }
    
    private Cache getCache(final String type, final int capacity)
    throws CacheAcquireException {
        return CacheRegistry.getCache(type, "dummy", capacity,
                                      getClass().getClassLoader());
    }
}
