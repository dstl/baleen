// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.exceptions;

/** A superclass for exceptions thrown by Baleen */
public class BaleenException extends Exception {
  private static final long serialVersionUID = 1L;

  /** Constructor with no parameters */
  public BaleenException() {
    super();
  }

  /**
   * Constructor with a message
   *
   * @param message
   */
  public BaleenException(String message) {
    super(message);
  }

  /**
   * Constructor with a message and a cause
   *
   * @param message
   * @param cause
   */
  public BaleenException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with a cause
   *
   * @param cause
   */
  public BaleenException(Throwable cause) {
    super(cause);
  }
}
