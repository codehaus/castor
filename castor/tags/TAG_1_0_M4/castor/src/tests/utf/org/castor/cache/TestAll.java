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
package utf.org.castor.cache;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Run all tests of the org.castor.cache package.
 * 
 * @author <a href="mailto:ralf DOT joachim AT syscon-world DOT de">Ralf Joachim</a>
 * @version $Revision$ $Date$
 * @since 1.0
 */
public final class TestAll extends TestCase {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite("All org.castor.cache tests");

        suite.addTest(TestCacheAcquireException.suite());
        suite.addTest(TestAbstractBaseCache.suite());
        suite.addTest(TestDebuggingCacheProxy.suite());
        suite.addTest(TestAbstractCacheFactory.suite());

        suite.addTest(utf.org.castor.cache.distributed.TestAll.suite());
        suite.addTest(utf.org.castor.cache.hashbelt.TestAll.suite());
        suite.addTest(utf.org.castor.cache.simple.TestAll.suite());

        suite.addTest(TestCacheFactoryRegistry.suite());

        return suite;
    }

    public TestAll(final String name) { super(name); }
}
