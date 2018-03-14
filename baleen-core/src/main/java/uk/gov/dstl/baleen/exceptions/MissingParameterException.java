// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.exceptions;

/**
 * Exception thrown when Baleen is missing a required parameter. In general, code should assume a
 * sensible default rather than throwing this exception.
 */
public class MissingParameterException extends BaleenException {
  private static final long serialVersionUID = 1L;

  /** Constructor with no parameters */
  public MissingParameterException() {
    super();
  }

  /**
   * Constructor with a message
   *
   * @param message
   */
  public MissingParameterException(String message) {
    super(message);
  }

  /**
   * Constructor with a message and a cause
   *
   * @param message
   * @param cause
   */
  public MissingParameterException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with a cause
   *
   * @param cause
   */
  public MissingParameterException(Throwable cause) {
    super(cause);
  }
}
