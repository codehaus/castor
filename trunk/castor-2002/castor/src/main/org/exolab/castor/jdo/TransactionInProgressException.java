
package org.exolab.castor.jdo;

/**
 * This exception is thrown when a call has been made to a method that
 * should not be called when a transaction is in progress.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */
public class TransactionInProgressException
    extends org.odmg.TransactionInProgressException
{


    /**
     * Constructs an instance of the exception.
     */
    public TransactionInProgressException()
    {
        super();
    }


    /**
     * Constructs an instance of the exception with the provided message.
     *
     * @param msg The message explaining the exception.
     */
    public TransactionInProgressException(String msg)
    {
        super(msg);
    }


}

