package org.exolab.castor.jdo;

/**
 * This exception is thrown when the implementation cannot make an object persistent because of the type of the object.
 *
 * @author David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
*/
public class ClassNotPersistenceCapableException
    extends org.odmg.ClassNotPersistenceCapableException
{
    
    /**
     * Construct an instance of the exception.
     */
    public ClassNotPersistenceCapableException()
    {
        super();
    }
    
    /**
     * Construct an instance of the exception.
     *
     * @param msg A string providing a description of the exception.
     */
    public ClassNotPersistenceCapableException( String msg )
    {
        super(msg);
    }


}
