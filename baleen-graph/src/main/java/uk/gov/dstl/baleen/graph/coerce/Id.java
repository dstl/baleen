// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.coerce;

/** Return all the values as they are */
public class Id implements ValueCoercer {

  @Override
  public Object coerce(Object value) {
    return value;
  }
}
