package org.exolab.castor.dao;

import java.util.Collection;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.PersistenceException;
import org.springframework.orm.castor.CastorTemplate;
import org.springframework.orm.castor.JDOManagerUtils;

public class ProductDaoImplWithInterceptor extends CastorTemplate implements ProductDaoWithInterceptor
{
    public Product loadProduct(final int id) throws ObjectNotFoundException
    {
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            Product product = (Product) database.load(Product.class, new Integer(1));
            database.commit();
            return product;
        }
        catch (ObjectNotFoundException e)
        {
            throw e;
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
    }

    public void createProduct(Product product)
    {
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            database.create(product);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
    }

    public void deleteProduct(Product product)
    {
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            database.remove(product);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class)
     */
    public Collection findProduct(Class entityClass)
    {
        Collection collection = null;
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            collection = super.find(entityClass);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
        return collection;
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class, java.lang.String)
     */
    public Collection findProduct(Class entityClass, String whereClause)
    {
        Collection collection = null;
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            collection = super.find(entityClass, whereClause);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
        return collection;
   }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class, java.lang.String)
     */
    public Collection findProduct(Class entityClass, String whereClause, Object[] parameters)
    {
        Collection collection = null;
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            collection = super.find(entityClass, whereClause, parameters);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
        return collection;
    }

    public Collection findProductByNamedQuery(final String queryName)
    {
        Collection collection = null;
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            collection = super.findByNamedQuery(queryName);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
        return collection;
    }

    public Collection findProductByNamedQuery(final String queryName, Object[] parameters)
    {
        Collection collection = null;
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            collection = super.findByNamedQuery(queryName, parameters);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
        return collection;
    }


    public Collection findDescending(Class entityClass, String ordering)
    {
        Collection collection = null;
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try
        {
            database.begin();
            collection = super.find(entityClass, "", null, ordering);
            database.commit();
        }
        catch (PersistenceException ex)
        {
            throw JDOManagerUtils.convertJdoAccessException(ex);
        }
        return collection;
    }
    
    

}
