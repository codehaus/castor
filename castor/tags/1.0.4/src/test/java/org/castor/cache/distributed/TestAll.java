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
package org.castor.cache.distributed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Run all tests of the org.castor.cache.distributed package.
 * 
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date: 2006-04-29 03:57:35 -0600 (Sat, 29 Apr 2006) $
 * @since 1.0
 */
public final class TestAll extends TestCase {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite("All org.castor.cache.distributed tests");

        suite.addTest(TestAbstractDistributedCache.suite());

        suite.addTest(TestCoherenceCacheFactory.suite());
        suite.addTest(TestCoherenceCache.suite());
        suite.addTest(TestEHCacheFactory.suite());
        suite.addTest(TestEHCache.suite());
        suite.addTest(TestFKCacheFactory.suite());
        suite.addTest(TestFKCache.suite());
        suite.addTest(TestGigaspacesCacheFactory.suite());
        suite.addTestSuite(TestGigaspacesCache.class);
        suite.addTest(TestJCacheFactory.suite());
        suite.addTest(TestJCache.suite());
        suite.addTest(TestJcsCacheFactory.suite());
        suite.addTest(TestJcsCache.suite());
        suite.addTest(TestOsCacheFactory.suite());
        suite.addTest(TestOsCache.suite());

        return suite;
    }

    public TestAll(final String name) { super(name); }
}
