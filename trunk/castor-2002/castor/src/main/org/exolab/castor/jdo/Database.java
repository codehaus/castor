
package org.exolab.castor.jdo;


import org.odmg.ObjectNameNotUniqueException;
import org.odmg.ObjectNameNotFoundException;


/**
 * The interface for interacting with an ODMG database.
 * Databases must be opened before starting any transactions that use the database
 * and closed after ending these transactions.
 * <p>
 * A database application generally begins processing by accessing one or more
 * critical objects and proceeding from there. These objects are root objects,
 * because they lead to interconnected webs of other objects.
 * The ability to name an object (using method <code>bind</code>) and
 * retrieve it later by that name (using method <code>lookup</code> facilitates
 * this start-up capability. A name is not explicitly defined as an attribute of
 * an object. Naming an object also makes it persistent.
 * <p>
 * There is a single flat name scope per database; thus all names in a particular
 * database are unique.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */
public interface Database
    extends org.odmg.Database
{

    /**
     * The database is not open.
     */
    public static final int NOT_OPEN = 0;

    /**
     * The database is opened for read-only access.
     */
    public static final int OPEN_READ_ONLY = 1;
    
    /**
     * The database is opened for reading and writing.
     */
    public static final int OPEN_READ_WRITE = 2;
    
    /**
     * The database is open for exclusive access.
     */
    public static final int OPEN_EXCLUSIVE = 3;
    
    /**
     * Open the named database with the specified access mode.
     * Attempts to open a database when it has already been opened will result in
     * the throwing of the exception <code>DatabaseOpenException</code>.
     * A <code>DatabaseNotFoundException</code> is thrown if the database does not exist.
     * Some implementations may throw additional exceptions that are also derived from
     * <code>ODMGException</code>.
     *
     * @param name The name of the database.
     * @param accessMode The access mode, which should be one of the static fields:
     * <code>OPEN_READ_ONLY</code>, <code>OPEN_READ_WRITE</code>,
     * or <code>OPEN_EXCLUSIVE</code>.
     * @throws ODMGException The database could not be opened.
     */
    public void open( String name, int accessMode )
        throws DatabaseNotFoundException;


    /**
     * Close the database.
     * After you have closed a database, further attempts to access objects in the
     * database will cause the exception <code>DatabaseClosedException</code> to be thrown.
     * Some implementations may throw additional exceptions that are also derived
     * from <code>ODMGException</code>.
     *
     * @throws ODMGException Unable to close the database.
     */
    public void close()
        throws ODMGException;


    /**
     * Associate a name with an object and make it persistent.
     * An object instance may be bound to more than one name.
     * Binding a previously transient object to a name makes that object persistent.
     *
     * @param object The object to be named.
     * @param name The name to be given to the object.
     * @throws ObjectNameNotUniqueException
     * If an attempt is made to bind a name to an object and that name is already bound
     * to an object.
     */
    public void bind( Object object, String name )
        throws ObjectNameNotUniqueException;


    /**
     * Lookup an object via its name.
     *
     * @param name The name of an object.
     * @return The object with that name.
     * @throws ObjectNameNotFoundException There is no object with the specified name.
     * @see ObjectNameNotFoundException
     */
    public Object lookup( String name )
        throws ObjectNameNotFoundException;


    /**
     * Disassociate a name with an object
     *
     * @param name The name of an object.
     * @throws ObjectNameNotFoundException No object exists in the database with that name.
     */
    public void unbind( String name )
        throws ObjectNameNotFoundException;


    /**
     * Make a transient object durable in the database.
     * It must be executed in the context of an open transaction.
     * If the transaction in which this method is executed commits,
     * then the object is made durable.
     * If the transaction aborts,
     * then the makePersistent operation is considered not to have been executed,
     * and the target object is again transient.
     * ClassNotPersistenceCapableException is thrown if the implementation cannot make
     * the object persistent because of the type of the object.
     *
     * @param object The object to make persistent.
     */
    public void makePersistent( Object object );


    /**
     * Deletes an object from the database.
     * It must be executed in the context of an open transaction.
     * If the object is not persistent, then ObjectNotPersistent is thrown.
     * If the transaction in which this method is executed commits,
     * then the object is removed from the database.
     * If the transaction aborts,
     * then the deletePersistent operation is considered not to have been executed,
     * and the target object is again in the database.
     *
     * @param object The object to delete.
     */
    public void deletePersistent( Object object );


}

