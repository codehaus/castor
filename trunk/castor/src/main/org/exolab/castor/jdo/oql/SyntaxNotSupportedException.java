package org.exolab.castor.jdo.oql;

/**
 * Exception thrown to indicate that a feature is not supported by a particular database.
 *
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @version $Revision$ $Date$
 */
public class SyntaxNotSupportedException extends OQLSyntaxException {

    private Exception  _exception;

	/**
	 * @param message A description of the error encountered.
	 */
	public SyntaxNotSupportedException(String message) {
		super(message);
	}

	/**
	 * @param message A description of the error encountered.
	 * @param exception The root cause of this exception.
	 */
	public SyntaxNotSupportedException(String message, Exception exception) {
		super(message, exception);
		_exception = exception;
	}

	/**
	 * @return Returns the root exception.	 
	 */
	public Exception getException() {
		return _exception;
	}
}
