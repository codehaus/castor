package org.springframework.orm.castor.tests;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class BaseSpringTestCase extends TestCase
{
    protected ApplicationContext context;

    protected void setUp() throws Exception
    {
        super.setUp();
        this.context = new ClassPathXmlApplicationContext("app-config.xml");
    }

}
