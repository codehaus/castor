
package org.exolab.castor.jdo;


/**
 * This is the base class for all exceptions thrown by an ODMG implementation.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */
public class ODMGException
    extends org.odmg.ODMGException
{


    /**
     * Construct an <code>ODMGException</code> object without an error message.
     */
    public ODMGException()
    {
        super();
    }


    /**
     * Construct an <code>ODMGException</code> object with an error message.
     *
     * @param msg The error message associated with this exception.
     */
    public ODMGException(String msg)
    {
        super(msg);
    }


}

