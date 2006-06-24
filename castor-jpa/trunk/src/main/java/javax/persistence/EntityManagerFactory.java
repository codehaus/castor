package javax.persistence;

public interface EntityManagerFactory {
    
    /**
     * Create a new EntityManager of type PersistenceContextType.TRANSACTION. 
     * This method returns a new application-managed EntityManager instance 
     * (with a new stand-alone persistence context) each time it is invoked. 
     * The isOpen method will return true on the returned instance.
     */
    EntityManager createEntityManager();
    
    /**
     * Create a new EntityManager of the specified persistence context 
     * type. This method returns a new application-managed EntityManager
     * instance (with a new stand-alone persistence context) each time 
     * it is invoked. The isOpen method will return true on the returned
     * instance.
     */
    EntityManager createEntityManager(PersistenceContextType type);
    
    /**
     * Get an EntityManager instance whose persistence context is propagated
     * with the current JTA transaction. If there is no persistence context 
     * bound to the current JTA transaction, a new transaction-scoped 
     * persistence context is created and associated with the transaction 
     * and the entity manager instance that is created and returned. If no 
     * JTA transaction is in progress, an EntityManager instance is created 
     * for which the persistence context will be propagated with subsequent 
     * JTA transactions. Throws IllegalStateException if called on an 
     * EntityManagerFactory that does not provide JTA EntityManagers.
     */
    EntityManager getEntityManager();
    
    /**
     * Close the factory, releasing any resources that it holds. After a 
     * factory instance is closed, all methods invoked on it will throw
     * an IllegalStateException, except for isOpen, which will return false.
     */
    void close();
    
    /**
     * Indicates whether the factory is open. Returns true until the 
     * factory has been closed.
     */
    public boolean isOpen();
    
}