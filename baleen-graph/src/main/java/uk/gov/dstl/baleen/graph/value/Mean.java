// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

/**
 * Mean value strategy
 *
 * <p>Output only the mean value
 */
public class Mean implements ValueStrategy<Number, Number> {

  @Override
  public Optional<Number> aggregate(List<? extends Number> values) {
    if (CollectionUtils.isEmpty(values)) {
      return Optional.empty();
    }
    return Optional.of(values.stream().mapToDouble(Number::doubleValue).average().getAsDouble());
  }
}
