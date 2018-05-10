// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

/** The graph output format options. */
public enum GraphFormat {
  /** @see http://graphml.graphdrawing.org/ */
  GRAPHML,
  /**
   * @see http://tinkerpop.apache.org/docs/
   * @see http://tinkerpop.apache.org/docs/3.0.0-incubating/#graphson-reader-writer
   */
  GRAPHSON,
  /**
   * (Binary format)
   *
   * @see https://github.com/EsotericSoftware/kryo
   */
  GYRO
}
