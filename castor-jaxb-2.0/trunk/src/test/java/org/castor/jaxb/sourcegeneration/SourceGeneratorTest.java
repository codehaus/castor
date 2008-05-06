/*
 * Copyright 2008 Matthias Epheser
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
package org.castor.jaxb.sourcegeneration;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

/**
 * 
 * @author Matthias Epheser
 * @version $Id$
 *
 */
public class SourceGeneratorTest extends TestCase {

    public void testSourceGeneration() throws Exception {
        InputSource inputSource =
            new InputSource(getClass().getClassLoader().getResource("test.xsd").toExternalForm());
        
        Jaxb2SourceGenerator jaxb2SourceGenerator = new Jaxb2SourceGenerator();
        jaxb2SourceGenerator.setSuppressNonFatalWarnings(true);
//        jaxb2SourceGenerator.set
        jaxb2SourceGenerator.setDestDir("target/test-classes");
        jaxb2SourceGenerator.generateSource(inputSource, "org.castor.jaxb.sourcegeneration.generated");
        
    }

}
