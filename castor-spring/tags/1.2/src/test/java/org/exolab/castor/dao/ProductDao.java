package org.exolab.castor.dao;

import java.util.Collection;

public interface ProductDao {

    public Product loadProduct(final int id);
    
    public void createProduct (Product product);
    
    public void deleteProduct (Product product);
    
    public void deleteProducts(Collection products);
    
    public void updateProduct (Product product);

    public Collection findProducts (Class entityClass);

    public Collection findProductsNative (Class entityClass);

    public Collection findProducts (Class entityClass, String whereClause);

    public Collection findProducts (Class entityClass, String whereClause, Object[] parameters);
    
    public Collection findProductsByNamedQuery (String queryName);
    
    public Collection findProductsByNamedQuery (String queryName, Object[] parametgers);
    
    public void evictProduct(Product product);
    
    public void evictAll();

    public boolean isProductCached(Product product);
    

}
