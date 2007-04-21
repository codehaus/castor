package org.castor.spring.orm;

import org.exolab.castor.jdo.Database;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * Holder wrapping a Castor JDO Database.
 * CastorTransactionManager binds instances of this class to the thread, for a 
 * given JDOManager.
 *
 * <p>Note: This is an SPI class, not intended to be used by applications.
 *
 * @author Juergen Hoeller
 * @since 03.06.2003
 * @see CastorTransactionManager
 * @see JDOManagerUtils
 */
public class DatabaseHolder extends ResourceHolderSupport {

    private Database database;

    public DatabaseHolder(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return this.database;
    }

}
