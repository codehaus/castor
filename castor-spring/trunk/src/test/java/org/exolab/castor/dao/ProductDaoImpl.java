package org.exolab.castor.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryResults;
import org.springframework.orm.castor.CastorCallback;
import org.springframework.orm.castor.CastorTemplate;

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

       // TODO [WG]: delete needs to load the obect first if it's not TimeStampable
    public void deleteProduct (final Product product) {
        CastorTemplate template = new CastorTemplate(this.jdoManager);
        template.execute(
                new CastorCallback() {
                    public Object doInCastor(Database database) throws PersistenceException {
                        database.begin();
                        database.remove(product);
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
                        QueryResults results = query.execute();
                        // TODO [WG]: is ArrayList the correct type to return here ?
                        Collection objects = new ArrayList();
                        Object object;
                        while (results.hasMore()) {
                            object = results.next();
                            objects.add (object);
                        }
                        database.commit();
                        return objects;
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
                        QueryResults results = query.execute();
                        // TODO [WG]: is ArrayList the correct type to return here ?
                        Collection objects = new ArrayList();
                        Object object;
                        while (results.hasMore()) {
                            object = results.next();
                            objects.add (object);
                        }
                        database.commit();
                        return objects;
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
                        QueryResults results = query.execute();
                        // TODO [WG]: is ArrayList the correct type to return here ?
                        Collection objects = new ArrayList();
                        Object object;
                        while (results.hasMore()) {
                            object = results.next();
                            objects.add (object);
                        }
                        database.commit();
                        return objects;
                    }
                });
    }

    

}

