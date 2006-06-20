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

    public Product loadProduct(final int id) {
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
    public void createProduct(final Product product) {
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
     * @see org.exolab.castor.service.ProductService#deleteProduct(org.exolab.castor.dao.Product)
     */
    public void deleteProduct(final Product product) {
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
     * @see org.exolab.castor.service.ProductService#findProducts()
     */
    public Collection findProducts() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return (Collection) transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            return productDao.findProducts (Product.class);
          }
        });
    }

    /**
     * @see org.exolab.castor.service.ProductService#findProductsByName(java.lang.Object)
     */
    public Collection findProductsByName(final Object name) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return (Collection) transactionTemplate.execute(new TransactionCallback() {
          public Object doInTransaction(TransactionStatus status) {
            return productDao.findProducts (Product.class, "WHERE name = $1", new Object[] { name });
          }
        });
    }
  }
