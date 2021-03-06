/*
 * Copyright 2009 Tobias Hochwallner, Ralf Joachim
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
package org.castor.cpa.test.test2996.single;

import org.castor.cpa.test.framework.xml.types.DatabaseEngineType;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.mapping.MappingException;

/**
 * Tests to load Book with XML mapping. 
 */
public final class TestSingleXmlMapping extends AbstractTestSingle {
    private static final String DBNAME = "test2996-single-xml";
    private static final String MAPPING = "/org/castor/cpa/test/test2996/single/mapping.xml";
    
    public TestSingleXmlMapping(final String name) {
        super(name);
    }

    // Test are only included/excluded for engines that have been tested with this test suite.

    public boolean include(final DatabaseEngineType engine) {
        return (engine == DatabaseEngineType.DERBY)
            || (engine == DatabaseEngineType.MYSQL);
    }

    @Override
    protected JDOManager getJDOManager() throws MappingException {
        return getJDOManager(DBNAME, MAPPING);
    }
}
