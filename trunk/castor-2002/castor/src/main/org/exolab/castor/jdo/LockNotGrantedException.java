
package org.exolab.castor.jdo;

/**
 * This exception is thrown if a lock could not be granted on an object.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */
public class LockNotGrantedException
    extends org.odmg.LockNotGrantedException
{


    /**
     * Construct an instance of the exception.
     */
    public LockNotGrantedException()
    {
        super();
    }


    /**
     * Construct an instance of the exception with a descriptive message.
     *
     * @param msg A description of the exception.
     */
    public LockNotGrantedException(String msg)
    {
        super(msg);
    }


}

