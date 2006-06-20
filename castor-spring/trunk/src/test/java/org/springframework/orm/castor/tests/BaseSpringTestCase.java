package org.springframework.orm.castor.tests;

import java.sql.Connection;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class BaseSpringTestCase extends TestCase
{

    private static final int DEFAULT_ID = 2;

    protected ApplicationContext context;

    protected void setUp() throws Exception
    {
        super.setUp();
        this.context = new ClassPathXmlApplicationContext("app-config.xml");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        JDOManager jdoManager = (JDOManager) this.context.getBean("jdoManager");
        Database db = jdoManager.getDatabase();
        db.begin();
        Connection connection = db.getJdbcConnection();
        connection.createStatement().execute("delete from spring.product where id = " + DEFAULT_ID);
        db.commit();
        db.close();
    }

}
