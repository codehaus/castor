package org.springframework.xml.castor;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class BaseSpringTestCase extends TestCase {

    protected ApplicationContext context;

    protected void setUp() throws Exception {
        super.setUp();
        this.context = new ClassPathXmlApplicationContext("app-config.xml");
    }
}