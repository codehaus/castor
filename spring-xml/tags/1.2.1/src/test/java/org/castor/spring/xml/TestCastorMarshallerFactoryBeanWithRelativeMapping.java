package org.castor.spring.xml;



/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestCastorMarshallerFactoryBeanWithRelativeMapping extends BaseFactoryBeanTestCase {

    protected void setUp() throws Exception {
        this.contextResource = "app-config-with-relative-mapping.xml";
        super.setUp();
    }
    
    
}