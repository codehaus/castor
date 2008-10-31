package org.castor.spring.xml.context;

import org.exolab.castor.xml.Unmarshaller;

/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */
public class TestCastorUnmarshallerFactoryBeanWithXmlContext extends BaseTestCastorUnmarshallerFactoryBean {

    private Unmarshaller unmarshaller;

    protected void setUp() throws Exception {
        contextResource = "app-config-xml-context.xml";
        super.setUp();
    }

}
