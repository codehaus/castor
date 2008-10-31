package org.castor.spring.xml.context;

import org.castor.spring.xml.BaseFactoryBeanTestCase;

/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestCastorUnmarshallerFactoryBeanWithXmlContextPartialMapping extends BaseFactoryBeanTestCase {

    protected void setUp() throws Exception {
        contextResource = "app-config-xml-context-partial-mapping.xml";
        super.setUp();
    }
        
}