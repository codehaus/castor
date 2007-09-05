package org.exolab.castor.dao;

import java.util.Collection;

import org.castor.spring.orm.CastorTemplate;

public class ProductDaoImplUsingTemplate extends CastorTemplate implements ProductDao {

    public Product loadProduct(final int id) {
        return (Product) load (Product.class, new Integer (id));
    }
    
    public void createProduct (Product product) {
        create(product);
    }
    
    public void deleteProduct (final Product product) {
        Object toDelete = load(product.getClass(), new Integer(product.getId()));
        remove(toDelete);
    }

    public void deleteProducts (final Collection products) {
        removeAll(products);
    }

    public void updateProduct (final Product product) {
        update(product);
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class)
     */
    public Collection findProducts(final Class entityClass) {
        return find (entityClass);
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class)
     */
    public Collection findProductsNative(final Class entityClass) {
        return findNative (entityClass, "select id, name from product");
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class, java.lang.String)
     */
    public Collection findProducts(Class entityClass, String whereClause) {
        return find (entityClass, whereClause);
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class, java.lang.String)
     */
    public Collection findProducts(Class entityClass, String whereClause, Object[] parameters) {
        return find (entityClass, whereClause, parameters);
    }

    public Collection findProductsByNamedQuery(final String queryName) {
        return findByNamedQuery (queryName);
    }

    public Collection findProductsByNamedQuery(final String queryName, Object[] parameters) {
        return findByNamedQuery (queryName, parameters);
    }

    public void evictProduct(Product product)
    {
        evict(product);
    }

    public boolean isProductCached(Product product)
    {
        return isCached(product);
    }

    public void evictAll()
    {
        evictAll(Product.class);
    }
    
}
