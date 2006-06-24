package javax.persistence;

public interface EntityTransaction
{
    /**
     * Start a resource transaction.
     * 
     * @throws IllegalStateException if isActive() is true.
     */
    public void begin();
    
    /**
     * Commit the current transaction, writing any unflushed changes to the database.
     * 
     * @throws IllegalStateException if isActive() is false.
     * @throws PersistenceException if the commit fails.
     */
    public void commit();
    
    /**
     * Roll back the current transaction.
     * 
     * @throws IllegalStateException if isActive() is false.
     * @throws PersistenceException if an unexpected error condition is encountered.
     */
    public void rollback();
    
    /**
     * Indicate whether a transaction is in progress.
     * 
     * @throws PersistenceException if an unexpected error condition is encountered.
     */
    public boolean isActive();
}