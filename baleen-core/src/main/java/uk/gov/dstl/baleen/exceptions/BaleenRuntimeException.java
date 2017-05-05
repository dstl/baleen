//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.exceptions;

/**
 * A superclass for runtime exceptions thrown by Baleen
 * 
 * 
 */
public class BaleenRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with no parameters
	 */
	public BaleenRuntimeException() {
		super();
	}
	
	/**
	 * Constructor with a message
	 * 
	 * @param message
	 */
	public BaleenRuntimeException(String message) {
		super(message);
	}
	
	/**
	 * Constructor with a message and a cause
	 * 
	 * @param message
	 * @param cause
	 */
	public BaleenRuntimeException(String message, Throwable cause){
		super(message, cause);
	}
	
	/**
	 * Constructor with a cause
	 * 
	 * @param cause
	 */
	public BaleenRuntimeException(Throwable cause){
		super(cause);
	}
}
