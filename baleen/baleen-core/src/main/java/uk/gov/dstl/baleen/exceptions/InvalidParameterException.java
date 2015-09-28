//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.exceptions;

/**
 * Exception thrown when Baleen is given an invalid parameter.
 * In general, code should assume a sensible default rather than throwing this exception.
 * 
 * 
 */
public class InvalidParameterException extends BaleenException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with no parameters
	 */
	public InvalidParameterException() {
		super();
	}
	
	/**
	 * Constructor with a message
	 * 
	 * @param message
	 */
	public InvalidParameterException(String message) {
		super(message);
	}
	
	/**
	 * Constructor with a message and a cause
	 * 
	 * @param message
	 * @param cause
	 */
	public InvalidParameterException(String message, Throwable cause){
		super(message, cause);
	}

	/**
	 * Constructor with a cause
	 * 
	 * @param cause
	 */
	public InvalidParameterException(Throwable cause){
		super(cause);
	}
}