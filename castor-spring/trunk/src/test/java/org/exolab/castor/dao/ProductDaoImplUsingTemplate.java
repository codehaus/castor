package org.exolab.castor.dao;
import java.util.Collection;
import org.springframework.orm.castor.CastorTemplate;

public class ProductDaoImplUsingTemplate extends CastorTemplate implements ProductDao {

    public Product loadProduct(final int id) {
        return (Product) load (Product.class, new Integer (id));
    }
    
    public void createProduct (Product product) {
        create(product);
    }
    
    public void deleteProduct (Product product) {
        remove (product);
    }

    /**
     * @see org.exolab.castor.dao.ProductDao#findProducts(java.lang.Class)
     */
    public Collection findProducts(Class entityClass) {
        return find (entityClass);
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

}
