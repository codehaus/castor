package utf.org.exolab.castor.jdo.transactionmanager;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Properties;

import org.exolab.castor.jdo.transactionmanager.TransactionManagerFactory;
import org.exolab.castor.jdo.transactionmanager.TransactionManagerFactoryRegistry;

import junit.framework.TestCase;

/*
 * JUnit test case for unit testing TransactionManagerFactoryRegistry.
 * @author <a href="werner.guttmann@gmx.net">Werner Guttmann</a>
 *
 */
public class TestTransactionManagerFactoryRegistry 
	extends TestCase 
{

	private PrintWriter writer = null;

	/**
	 * Constructor for TransactionManagerFactoryRegistryTest.
	 * @param arg0
	 */
	public TestTransactionManagerFactoryRegistry(String arg0) {
		super(arg0);
	}

	public void testGetTransactionManagerFactory() 
		throws Exception 
	{
		TransactionManagerFactory localFactory =
			TransactionManagerFactoryRegistry.getTransactionManagerFactory ("local");

		assertEquals("equals", "local", localFactory.getName());
					
		TransactionManagerFactory jndiFactoryNoParams =
			TransactionManagerFactoryRegistry.getTransactionManagerFactory ("jndi");

		assertEquals("equals", "jndi", jndiFactoryNoParams.getName());
		assertNull("params == null", jndiFactoryNoParams.getParams());
			
		TransactionManagerFactory jndiFactory =
			TransactionManagerFactoryRegistry.getTransactionManagerFactory ("jndi");

		assertEquals("factory name == ", "jndi", jndiFactory.getName());
		
		Collection factories = TransactionManagerFactoryRegistry.getTransactionManagerFactories();
		
		assertNotNull ("At least one transaction manager factory", factories);
		assertEquals ("5 transaction manager factories", 5, factories.size());
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		Properties params = new Properties ();
		params.put ("jndiENC", "comp:java/transactionManager");
	}

}
