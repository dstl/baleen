// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

/** Exception to report error in Dependency Tree format */
public class DependencyParseException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * New instance
   *
   * @param line
   * @param message
   */
  public DependencyParseException(int line, String message) {
    super("Error at line " + line + ":" + message);
  }
}
