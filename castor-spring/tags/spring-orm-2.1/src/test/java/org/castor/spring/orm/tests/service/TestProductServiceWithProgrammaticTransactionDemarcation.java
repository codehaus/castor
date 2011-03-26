package org.castor.spring.orm.tests.service;

import javax.annotation.Resource;

import org.castor.spring.orm.tests.BaseSpringTestCaseWithTransactionDemarcation;
import org.exolab.castor.service.ProductService;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration
public class TestProductServiceWithProgrammaticTransactionDemarcation 
    extends BaseSpringTestCaseWithTransactionDemarcation {

	@Resource(name="myProductService")
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
    
}
