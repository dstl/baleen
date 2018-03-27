// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import java.util.List;
import java.util.Optional;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A strategy to combine multiple values into a single value or representation.
 *
 * @param <I> input type
 * @param <O> output type
 */
@FunctionalInterface
public interface ValueStrategy<I, O> {

  /**
   * @param values to be aggregated
   * @return an aggregated value
   * @throws BaleenException
   */
  Optional<O> aggregate(List<? extends I> values) throws BaleenException;
}
