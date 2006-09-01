package org.springframework.xml.castor;



/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestCastorMarshallerFactoryBeanWithoutMapping extends BaseSpringTestCase {

    protected void setUp() throws Exception {
        contextResource = "app-config-without-mapping.xml";
        super.setUp();
    }
    
    
}