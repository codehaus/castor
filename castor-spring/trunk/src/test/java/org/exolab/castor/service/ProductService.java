package org.exolab.castor.service;

import java.util.Collection;

import org.exolab.castor.dao.Product;

/**
 * @author werner.guttmann
 *
 * This is a type.
 */
public interface ProductService {

    public Product loadProduct(final int id);
    
    public void createProduct (final Product product);
    
    public void deleteProduct (final Product product);
    
    public Collection findProducts ();
    
    public Collection findProductsByName (final Object id);
}
