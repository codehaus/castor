package org.exolab.castor.service;

import java.util.Collection;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;

public class ProductServiceImplWithDeclarativeTransactionDeclaration implements ProductService {

    private ProductDao productDao;

    public void setProductDao(ProductDao productDao) {
      this.productDao = productDao;
    }

    public Product loadProduct(final int id) {
        return this.productDao.loadProduct(id);
    }

    /**
     * @see org.exolab.castor.service.ProductService#createProduct(org.exolab.castor.dao.Product)
     */
    public void createProduct(Product product) {
        this.productDao.createProduct(product);
    }

    /**
     * @see org.exolab.castor.service.ProductService#deleteProduct(org.exolab.castor.dao.Product)
     */
    public void deleteProduct(Product product) {
        this.productDao.deleteProduct(product);
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    public Collection findProducts() {
        return this.productDao.findProducts (Product.class);
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProductsByName(java.lang.Object)
     */
    public Collection findProductsByName(final Object name) {
        return this.productDao.findProducts (Product.class, "WHERE name = $1", new Object[] { name });
    }
  }
