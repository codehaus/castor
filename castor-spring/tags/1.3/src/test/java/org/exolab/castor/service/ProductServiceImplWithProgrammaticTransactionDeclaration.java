package org.exolab.castor.service;

import java.util.Collection;

import org.exolab.castor.dao.Product;
import org.exolab.castor.dao.ProductDao;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class ProductServiceImplWithProgrammaticTransactionDeclaration implements ProductService {

    private PlatformTransactionManager transactionManager;
    private ProductDao productDao;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
      this.transactionManager = transactionManager;
    }

    public void setProductDao(ProductDao productDao) {
      this.productDao = productDao;
    }

    public Product load(final int id) {
      TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
      transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
      return (Product) transactionTemplate.execute(new TransactionCallback() {
        public Object doInTransaction(TransactionStatus status) {
          Product product = productDao.loadProduct(id);
          return product;
        }
      });
    }

    /**
     * @see org.exolab.castor.service.ProductService#createProduct(org.exolab.castor.dao.Product)
     */
    public void create(final Product product) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            productDao.createProduct (product);
            return null;
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#delete(org.exolab.castor.dao.Product)
     */
    public void delete(final Product product) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            productDao.deleteProduct(product);
            return null;
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#delete(org.exolab.castor.dao.Product)
     */
    public void delete(final Collection products) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            productDao.deleteProducts(products);
            return null;
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#update(org.exolab.castor.dao.Product)
     */
    public void update(final Product product) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            productDao.updateProduct(product);
            return null;
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    public Collection find() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return (Collection) transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            return productDao.findProducts (Product.class);
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    public Collection findNative() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return (Collection) transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            return productDao.findProductsNative (Product.class);
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProductsByName(java.lang.Object)
     */
    public Collection findByName(final Object name) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return (Collection) transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            return productDao.findProducts (Product.class, "WHERE name = $1", new Object[] { name });
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProductsByNamedQuery(java.util.String)
     */
    public Collection findByNamedQuery(final String queryName) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return (Collection) transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            return productDao.findProductsByNamedQuery(queryName);
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProductsByNamedQuery(java.util.String)
     */
    public Collection findByNamedQuery(final String queryName, final Object[] parameters) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return (Collection) transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            return productDao.findProductsByNamedQuery(queryName, parameters);
          }
        });
    }

    public void evict(final Product product)
    {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            productDao.evictProduct(product);
            return null;
          }
        });
    }

    public boolean isCached(final Product product)
    {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        Boolean returnValue = (Boolean) transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                return Boolean.valueOf(productDao.isProductCached(product));
            }
        });
        return returnValue.booleanValue();
    }

    public void evictAll() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            productDao.evictAll();
            return null;
          }
        });
    }
    
    
  }
