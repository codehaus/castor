
package org.exolab.castor.jdo;

/**
 * This exception is thrown when accessing an object that was deleted.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */
public class ObjectDeletedException
    extends PersistenceException
{
    

    /**
     * Construct an instance of the exception.
     *
     * @param msg A string providing a description of the exception.
     */
    public ObjectDeletedException( String message )
    {
        super( message );
    }


} 
