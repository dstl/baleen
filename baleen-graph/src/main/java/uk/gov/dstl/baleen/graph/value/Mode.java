// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

/**
 * Mode value strategy
 *
 * <p>Output only the mode value
 */
public class Mode<T> implements ValueStrategy<T, T> {

  @Override
  public Optional<T> aggregate(List<? extends T> values) {
    if (CollectionUtils.isEmpty(values)) {
      return Optional.empty();
    }
    return values.stream()
        .collect(Collectors.groupingBy(t -> t, Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey);
  }
}
