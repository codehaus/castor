package org.castor.spring.orm.tests;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public abstract class BaseSpringTestCase extends TestCase
{
    protected ApplicationContext context;

    protected void setUp() throws Exception
    {
        super.setUp();
        this.context = getApplicationContext();
    }

    protected abstract ClassPathXmlApplicationContext getApplicationContext();

}
