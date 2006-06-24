package javax.persistence.spi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

/**
 * Interface implemented by the persistence provider. This interface 
 * is used to create an EntityManagerFactory. It is invoked by the
 * container in Java EE environments and by the Persistence class in 
 * Java SE environments.
 */
public interface PersistenceProvider
{
    /**
     * Called by Persistence class when an EntityManagerFactory is to be 
     * created.
     * 
     * @param emName The name of the persistence unit
     * @param map A Map of properties for use by the persistence provider. These 
     *            properties may be used to override the settings in the
     *            persistence.xml.
     * @return EntityManagerFactory for the persistence unit, or null if 
     *            the provider is not the right provider
     */
    public EntityManagerFactory createEntityManagerFactory(String emName, Map map);

    /**
     * Called by the container when an EntityManagerFactory is to be 
     * created.
     * 
     * @param info Metadata for use by the persistence provider
     * @return EntityManagerFactory for the persistence unit specified 
     * by the metadata
     */
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info);
}