package org.exolab.castor.dao;

import java.util.Collection;

import org.exolab.castor.jdo.PersistenceException;

public interface ProductDaoWithInterceptor {

    public Product loadProduct(final int id) throws PersistenceException;
    
    public void createProduct(Product product);
    
    public void deleteProduct(Product product);

    public Collection findProduct(Class entityClass);

    public Collection findProduct(Class entityClass, String whereClause);

    public Collection findProduct(Class entityClass, String whereClause, Object[] parameters);

    public Collection findProductByNamedQuery(String queryName);

    public Collection findProductByNamedQuery(String queryName, Object[] parameters);

    public Collection findDescending(Class entityClass, String ordering);
    
}
