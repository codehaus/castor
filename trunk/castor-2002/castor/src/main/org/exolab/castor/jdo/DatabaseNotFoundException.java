package org.exolab.castor.jdo;


/**
 * This exception is thrown when attempting to open a database that
 * does not exist.
 *
 * @author David Jordan (OMG)
 * @version ODMG 3.0
 * @version $Revision$ $Date$
 */
public class DatabaseNotFoundException
    extends PersistenceException
{
    

    public DatabaseNotFoundException( String message )
    {
        super( message );
    }


}

