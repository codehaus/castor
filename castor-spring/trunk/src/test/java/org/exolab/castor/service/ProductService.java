package org.exolab.castor.service;

import java.util.Collection;

import org.exolab.castor.dao.Product;

/**
 * @author werner.guttmann
 *
 * This is a type.
 */
public interface ProductService {

    public Product load(final int id);
    
    public void create(final Product product);
    
    public void delete(final Product product);
    
    public Collection find();
    
    public Collection findByName (final Object id);
    
    public void evict(final Product product);
    
    public boolean isCached (Product product);
    
    public boolean isPersistent (Product product);
}
