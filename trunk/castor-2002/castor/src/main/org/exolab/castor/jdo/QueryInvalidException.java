
package org.exolab.castor.jdo;

/**
 * This exception is thrown if the query is not a valid OQL query.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */
public class QueryInvalidException
    extends org.odmg.QueryInvalidException
{


    /**
     * Construct an instance of the exception.
     */
    public QueryInvalidException()
    {
        super();
    }
    

    /**
     * Construct an instance of the exception.
     *
     * @param msg A string indicating why the <code>OQLQuery</code> instance does not
     * represent a valid OQL query.
     */
    public QueryInvalidException(String msg)
    {
        super(msg);
    }


}

