package org.exolab.castor.dao;

import org.exolab.castor.jdo.PersistenceException;

public interface ProductDaoWithInterceptor {

    public Product load(final int id) throws PersistenceException;
}
