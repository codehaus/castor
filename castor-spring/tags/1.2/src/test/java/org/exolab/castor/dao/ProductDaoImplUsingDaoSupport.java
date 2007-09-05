package org.exolab.castor.dao;

import java.util.Collection;

import org.castor.spring.orm.support.CastorDaoSupport;
import org.exolab.castor.jdo.JDOManager;

public class ProductDaoImplUsingDaoSupport extends CastorDaoSupport implements ProductDao {
    
    private JDOManager jdoManager;

    public Product loadProduct(final int id) {
        return (Product) getCastorTemplate().load (Product.class, new Integer (id));
    }
    
    public void createProduct (Product product) {
        getCastorTemplate().create(product);
    }
    
    public void deleteProduct (final Product product) {
        Object toDelete = getCastorTemplate().load(product.getClass(), new Integer(product.getId()));
        getCastorTemplate().remove(toDelete);
    }

    public void deleteProducts (final Collection products) {
        getCastorTemplate().removeAll(products);
    }

    public void updateProduct (final Product product) {
        getCastorTemplate().update(product);
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class)
     */
    public Collection findProducts(final Class entityClass) {
        return getCastorTemplate().find (entityClass);
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class)
     */
    public Collection findProductsNative(final Class entityClass) {
        return getCastorTemplate().findNative (entityClass, "select id, name from product");
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class, java.lang.String)
     */
    public Collection findProducts(Class entityClass, String whereClause) {
        return getCastorTemplate().find (entityClass, whereClause);
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class, java.lang.String)
     */
    public Collection findProducts(Class entityClass, String whereClause, Object[] parameters) {
        return getCastorTemplate().find (entityClass, whereClause, parameters);
    }

    public Collection findProductsByNamedQuery(final String queryName) {
        return getCastorTemplate().findByNamedQuery (queryName);
    }

    public Collection findProductsByNamedQuery(final String queryName, Object[] parameters) {
        return getCastorTemplate().findByNamedQuery (queryName, parameters);
    }

    public void evictProduct(Product product)
    {
        getCastorTemplate().evict(product);
    }

    public boolean isProductCached(Product product)
    {
        return getCastorTemplate().isCached(product);
    }

    public void evictAll()
    {
        getCastorTemplate().evictAll(Product.class);
    }

    public void setJdoManager(JDOManager jdoManager) {
        this.jdoManager = jdoManager;
    }
    
}
