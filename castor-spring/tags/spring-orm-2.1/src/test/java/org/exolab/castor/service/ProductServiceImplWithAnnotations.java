package org.exolab.castor.service;

import java.util.Collection;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;
import org.springframework.transaction.annotation.Transactional;

public class ProductServiceImplWithAnnotations implements ProductService {

    private ProductDao productDao;

    public void setProductDao(ProductDao productDao) {
      this.productDao = productDao;
    }

    @Transactional
    public Product load(final int id) {
        return this.productDao.loadProduct(id);
    }

    /**
     * @see org.exolab.castor.service.ProductService#createProduct(org.exolab.castor.dao.Product)
     */
    @Transactional
    public void create(Product product) {
        this.productDao.createProduct(product);
    }

    /**
     * @see org.exolab.castor.service.ProductService#delete(org.exolab.castor.dao.Product)
     */
    @Transactional
    public void delete(Product product) {
        this.productDao.deleteProduct(product);
    }

    /**
     * @see org.exolab.castor.service.ProductService#deleteAll()
     */
    @Transactional
    public void delete(final Collection products) {
        this.productDao.deleteProducts(products);
    }

    /**
     * @see org.exolab.castor.service.ProductService#update(org.exolab.castor.dao.Product)
     */
    @Transactional
    public void update(Product product) {
        this.productDao.updateProduct(product);
    }
    /**
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    @Transactional
    public Collection find() {
        return this.productDao.findProducts (Product.class);
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    @Transactional
    public Collection findNative() {
        return this.productDao.findProductsNative (Product.class);
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProductsByName(java.lang.Object)
     */
    @Transactional
    public Collection findByName(final Object name) {
        return this.productDao.findProducts (Product.class, "WHERE name = $1", new Object[] { name });
    }

    @Transactional
    public Collection findByNamedQuery(String queryName) {
        return this.productDao.findProductsByNamedQuery(queryName);
    }
    
    @Transactional
    public Collection findByNamedQuery(String queryName, Object[] parameters) {
        return this.productDao.findProductsByNamedQuery(queryName, parameters);
    }

    @Transactional
    public void evict(Product product)
    {
        productDao.evictProduct(product);
    }

    @Transactional
    public void evictAll()
    {
        productDao.evictAll();
    }

    @Transactional
    public boolean isCached(Product product)
    {
        return productDao.isProductCached(product);
    }

    
  }
