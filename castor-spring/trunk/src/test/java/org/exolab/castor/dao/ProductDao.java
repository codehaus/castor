package org.exolab.castor.dao;

import java.util.Collection;

public interface ProductDao {

    public Product loadProduct(final int id);
    
    public void createProduct (Product product);
    
    public void deleteProduct (Product product);

    public Collection findProducts (Class entityClass);

    public Collection findProducts (Class entityClass, String whereClause);

    public Collection findProducts (Class entityClass, String whereClause, Object[] parameters);
    
    public void evictProduct(Product product);
    
    public boolean isProductPersistent(Product product);

    public boolean isProductCached(Product product);
    

}
