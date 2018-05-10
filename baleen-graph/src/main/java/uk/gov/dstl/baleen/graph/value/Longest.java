// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

/**
 * Longest value strategy
 *
 * <p>Output only the longest value, by string length
 */
public class Longest implements ValueStrategy<Object, String> {

  @Override
  public Optional<String> aggregate(List<? extends Object> values) {
    if (CollectionUtils.isEmpty(values)) {
      return Optional.empty();
    }
    // Ensure string as could be accidentally assigned to non string value
    return values
        .stream()
        .map(Object::toString)
        .max(Comparator.comparing(String::length))
        .map(Object::toString);
  }
}
