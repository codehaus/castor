package org.exolab.castor.dao;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.PersistenceException;
import org.springframework.orm.castor.CastorCallback;
import org.springframework.orm.castor.CastorTemplate;
import org.springframework.orm.castor.JDOManagerUtils;

public class ProductDaoImplWithInterceptor 
    extends CastorTemplate 
    implements ProductDaoWithInterceptor 
{
    public Product load(final int id) throws ObjectNotFoundException {
        Database database = JDOManagerUtils.getDatabase(getJDOManager(), false);
        try {
            database.begin();
            Product product = (Product) database.load (Product.class, new Integer (1));
            return product;
        }
        catch (ObjectNotFoundException e) {
            throw e;
        }
        catch (PersistenceException ex) {
          throw JDOManagerUtils.convertJdoAccessException(ex);
        }    
    }
}

