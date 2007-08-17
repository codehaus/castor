package org.castor.spring.xml;



/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestCastorMarshallerFactoryBeanWithoutMapping extends BaseFactoryBeanTestCase {

    protected void setUp() throws Exception {
        contextResource = "app-config-without-mapping.xml";
        super.setUp();
    }
    
    
}