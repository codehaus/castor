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
    
    public void delete(final Collection products);
    
    public void update (Product product);
    
    public Collection find();

    public Collection findNative();

    public Collection findByName (final Object id);

    public Collection findByNamedQuery(String queryName);
    
    public Collection findByNamedQuery(String queryName, Object[] parameters);

    public void evict(final Product product);

    public void evictAll();
    
    public boolean isCached (Product product);
    
}
