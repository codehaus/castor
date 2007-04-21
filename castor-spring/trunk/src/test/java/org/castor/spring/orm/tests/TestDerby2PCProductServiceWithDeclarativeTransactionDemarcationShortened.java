package org.castor.spring.orm.tests;

import org.exolab.castor.service.ProductService;

/**
 * JUnit test case for Castor's Spring integration.
 * @author Werner Guttmann  
 */
public class TestDerby2PCProductServiceWithDeclarativeTransactionDemarcationShortened 
    extends BaseSpringTestCaseWithTransactionDemarcation {

    protected void setUp() throws Exception {
        super.setUp();
        this.productService = (ProductService) this.context.getBean ("myDerby2PCProductServiceDeclarativeShortened");
        assertNotNull (this.productService);
    }
    
}
