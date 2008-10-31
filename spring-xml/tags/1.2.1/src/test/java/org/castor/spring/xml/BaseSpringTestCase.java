package org.castor.spring.xml;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class BaseSpringTestCase extends TestCase {

    protected ApplicationContext context;
    
    protected String contextResource = "app-config.xml";

    protected void setUp() throws Exception {
        super.setUp();
        this.context = new ClassPathXmlApplicationContext(contextResource);
    }

}