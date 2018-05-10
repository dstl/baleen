// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

/** A provider of {@link ValueStrategy}s for the given key. */
@FunctionalInterface
public interface ValueStrategyProvider {

  /**
   * @param key
   * @return a {@link ValueStrategy}
   */
  @SuppressWarnings("rawtypes")
  ValueStrategy get(String key);
}
