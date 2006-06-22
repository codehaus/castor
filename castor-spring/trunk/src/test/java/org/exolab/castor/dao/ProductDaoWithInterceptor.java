package org.exolab.castor.dao;

import java.util.Collection;

import org.exolab.castor.jdo.PersistenceException;

public interface ProductDaoWithInterceptor {

    public Product load(final int id) throws PersistenceException;
    
    public void create(Product product);
    
    public void delete(Product product);

    public Collection find(Class entityClass);

    public Collection find(Class entityClass, String whereClause);

    public Collection find(Class entityClass, String whereClause, Object[] parameters);
    
}
