package org.exolab.castor.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.castor.spring.orm.CastorCallback;
import org.castor.spring.orm.CastorTemplate;
import org.exolab.castor.jdo.CacheManager;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryResults;

public class ProductDaoImpl implements ProductDao {
    
    private JDOManager jdoManager;
    
    public void setJDOManager(JDOManager jdoManager) {
        this.jdoManager = jdoManager;
    }
    
    public Product loadProduct(final int id) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        return (Product) template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        Product product = (Product) database.load(Product.class, new Integer (id));
                        database.commit();
                        return product;
                    }
                });
    }

    // TODO [WG]: if a key generator is configured, how will the generated key be returned if product is declared final
    public void createProduct(final Product product) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        database.create(product);
                        database.commit();
                        return null;
                    }
                });
    }

    public void deleteProduct (final Product product) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        Product toDelete = (Product) database.load(product.getClass(), database.getIdentity(product));
                        database.remove(toDelete);
                        database.commit();
                        return null;
                    }
                });
    }

    public void deleteProducts (final Collection products) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        Iterator iter = products.iterator();
                        while (iter.hasNext()) {
                            Product product = (Product) iter.next();
                            Product toDelete = (Product) database.load(product.getClass(), database.getIdentity(product));
                            database.remove(toDelete);
                        }
                        database.commit();
                        return null;
                    }
                });
    }
    
    public void updateProduct (final Product product) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        database.update(product);
                        database.commit();
                        return null;
                    }
                });
    }
    public Collection findProducts(final Class entityClass) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        return (Collection) template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        OQLQuery query = database.getOQLQuery("select o from " + entityClass.getName() + " o");
                        QueryResults queryResults = query.execute();
                        List results = Collections.list(queryResults); 
                        database.commit();
                        return results;
                    }
                });
    }

    public Collection findProductsNative(final Class entityClass) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        return (Collection) template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        OQLQuery query = database.getOQLQuery("CALL SQL select id, name from product AS " + entityClass.getName());
                        QueryResults queryResults = query.execute();
                        List results = Collections.list(queryResults); 
                        database.commit();
                        return results;
                    }
                });
    }

    public Collection findProducts(final Class entityClass, final String whereClause) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        return template.executeFind(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        OQLQuery query = database.getOQLQuery("select o from " + entityClass.getName() + " o " + whereClause);
                        QueryResults queryResults = query.execute();
                        List results = Collections.list(queryResults); 
                        database.commit();
                        return results;
                    }
                });
    }

    public Collection findProducts(final Class entityClass, final String whereClause, final Object[] parameters) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        return template.executeFind(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        OQLQuery query = database.getOQLQuery("select o from " + entityClass.getName() + " o " + whereClause);
                        for (int i = 0; i < parameters.length; i++) {
                            query.bind(parameters[i]);
                        }
                        QueryResults queryResults = query.execute();
                        List results = Collections.list(queryResults); 
                        database.commit();
                        return results;
                    }
                });
    }

    public Collection findProductsByNamedQuery(final String queryName) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        return (Collection) template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        OQLQuery query = database.getNamedQuery(queryName);
                        QueryResults queryResults = query.execute();
                        List results = Collections.list(queryResults); 
                        database.commit();
                        return results;
                    }
                });
    }

    public Collection findProductsByNamedQuery(final String queryName, final Object[] parameters) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        return template.executeFind(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        OQLQuery query = database.getNamedQuery(queryName);
                        for (int i = 0; i < parameters.length; i++) {
                            query.bind(parameters[i]);
                        }
                        QueryResults queryResults = query.execute();
                        List results = Collections.list(queryResults); 
                        database.commit();
                        return results;
                    }
                });
    }

    public void evictProduct(final Product product)
    {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        CacheManager cacheManager = database.getCacheManager();
                        cacheManager.expireCache(product.getClass(), database.getIdentity(product));
                        database.commit();
                        return null;
                    }
                });
    }


    public void evictAll()
    {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        CacheManager cacheManager = database.getCacheManager();
                        cacheManager.expireCache(new Class[] { Product.class });
                        database.commit();
                        return null;
                    }
                });
    }

    public boolean isProductCached(final Product product)
    {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        Boolean isCached = (Boolean) template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        CacheManager cacheManager = database.getCacheManager();
                        boolean isCached = cacheManager.isCached(product.getClass(), database.getIdentity(product));
                        database.commit();
                        return Boolean.valueOf(isCached);
                    }
                });
        return isCached.booleanValue();
    }

}

