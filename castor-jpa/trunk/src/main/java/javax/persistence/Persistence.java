package javax.persistence;

import java.util.Map;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.castor.jpa.spi.CastorPersistenceProvider;

/**
 * Bootstrap class that is used to obtain an
 * EntityManagerFactory, from which EntityManager
 * references can be obtained.
 */
public class Persistence {
    /**
     * Create and return an EntityManagerFactory for the
     * named persistence unit.
     *
     * @param persistenceUnitName The name of the persistence unit
     * @return The factory that creates EntityManagers configured
     * according to the specified persistence unit
     */
    public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
        PersistenceProvider persistenceProvider = new CastorPersistenceProvider();
        PersistenceUnitInfo persistenceUnitInfo = null;
        return persistenceProvider.createContainerEntityManagerFactory(persistenceUnitInfo);
    }
    /**
     * Create and return an EntityManagerFactory for the
     * named persistence unit using the given properties.
     *
     * @param persistenceUnitName The name of the persistence unit
     * @param props Additional properties to use when creating the
     * factory. The values of these properties override any values
     * that may have been configured elsewhere.
     * @return The factory that creates EntityManagers configured
     * according to the specified persistence unit.
     */
    public static EntityManagerFactory createEntityManagerFactory(
            String persistenceUnitName, Map properties) {
        PersistenceProvider persistenceProvider = new CastorPersistenceProvider(); 
        return persistenceProvider.createEntityManagerFactory(persistenceUnitName, properties);
    }
    
}