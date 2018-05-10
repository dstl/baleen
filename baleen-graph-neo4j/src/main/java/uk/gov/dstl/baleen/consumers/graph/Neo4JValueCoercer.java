// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.graph;

import uk.gov.dstl.baleen.graph.coerce.ValueCoercer;

/** toString non supported value types for Neo4J */
public class Neo4JValueCoercer implements ValueCoercer {

  @Override
  public Object coerce(Object value) {
    if (value instanceof Boolean
        || value instanceof Long
        || value instanceof Double
        || value instanceof String) {
      return value;
    } else {
      return value.toString();
    }
  }
}
