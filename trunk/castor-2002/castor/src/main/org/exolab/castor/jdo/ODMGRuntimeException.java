
package org.exolab.castor.jdo;

/**
 * This is the base class for all RuntimeExceptions thrown by an ODMG implementation.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */
public class ODMGRuntimeException
    extends org.odmg.ODMGRuntimeException
{


    /**
     * Construct an instance of the exception.
     */
    public ODMGRuntimeException()
    {
        super();
    }
    

    /**
     * Construct an instance of the exception with the specified message.
     *
     * @param msg The message associated with the exception.
     */
    public ODMGRuntimeException(String msg)
    {
        super(msg);
    }


}

