
package org.exolab.castor.jdo;

/**
 * This exception is thrown when an attempt is made to call a method
 * on a Database that has been closed or has not been opened.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 * @see org.odmg.Database
 */
public class DatabaseClosedException
    extends org.odmg.DatabaseClosedException
{
    

    /**
     * Construct an instance of the exception without a message.
     */
    public DatabaseClosedException()
    {
        super();
    }


    /**
     * Construct an instance of the exception with the provided message.
     *
     * @param msg A message indicating why the exception occurred.
     */
    public DatabaseClosedException(String msg)
    {
        super(msg);
    }


}

