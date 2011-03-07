package org.castor.spring.orm.tests.factory.bean;

import static org.junit.Assert.assertNotNull;

import org.exolab.castor.jdo.JDOManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * JUnit test case for Castor's Spring integration.
 * 
 * @author Werner Guttmann
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestLocalCastorFactoryBeanWithCallbackInterceptor {

	@Autowired
	private JDOManager jdoManager;

	@Test
	public void testGetJDOManager() throws Exception {
		assertNotNull(this.jdoManager);
		assertNotNull(this.jdoManager.getCallbackInterceptor());
	}

}
