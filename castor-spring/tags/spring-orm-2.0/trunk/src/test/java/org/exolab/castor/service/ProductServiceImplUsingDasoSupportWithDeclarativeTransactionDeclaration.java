package org.exolab.castor.service;

import java.util.Collection;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;

public class ProductServiceImplUsingDasoSupportWithDeclarativeTransactionDeclaration implements ProductService {

    private ProductDao productDao;

    public void setProductDao(ProductDao productDao) {
      this.productDao = productDao;
    }

    public Product load(final int id) {
        return this.productDao.loadProduct(id);
    }

    /**
     * @see org.exolab.castor.service.ProductService#createProduct(org.exolab.castor.dao.Product)
     */
    public void create(Product product) {
        this.productDao.createProduct(product);
    }

    /**
     * @see org.exolab.castor.service.ProductService#delete(org.exolab.castor.dao.Product)
     */
    public void delete(Product product) {
        this.productDao.deleteProduct(product);
    }

    /**
     * @see org.exolab.castor.service.ProductService#deleteAll()
     */
    public void delete(final Collection products) {
        this.productDao.deleteProducts(products);
    }

    /**
     * @see org.exolab.castor.service.ProductService#update(org.exolab.castor.dao.Product)
     */
    public void update(Product product) {
        this.productDao.updateProduct(product);
    }
    /**
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    public Collection find() {
        return this.productDao.findProducts (Product.class);
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    public Collection findNative() {
        return this.productDao.findProductsNative (Product.class);
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProductsByName(java.lang.Object)
     */
    public Collection findByName(final Object name) {
        return this.productDao.findProducts (Product.class, "WHERE name = $1", new Object[] { name });
    }

    public Collection findByNamedQuery(String queryName) {
        return this.productDao.findProductsByNamedQuery(queryName);
    }
    
    public Collection findByNamedQuery(String queryName, Object[] parameters) {
        return this.productDao.findProductsByNamedQuery(queryName, parameters);
    }


    public void evict(Product product)
    {
        productDao.evictProduct(product);
    }

    public void evictAll()
    {
        productDao.evictAll();
    }

    public boolean isCached(Product product)
    {
        return productDao.isProductCached(product);
    }

    
  }
