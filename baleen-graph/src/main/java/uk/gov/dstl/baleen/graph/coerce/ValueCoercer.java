// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.coerce;

/**
 * Interface to allow the coercion of property values in to types supported by the graph
 * implementation.
 */
@FunctionalInterface
public interface ValueCoercer {

  /**
   * @param value
   * @return the value coerced to acceptable type
   */
  Object coerce(Object value);
}
