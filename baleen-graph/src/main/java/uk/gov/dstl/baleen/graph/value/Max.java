// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

/**
 * Max value strategy
 *
 * <p>Output only the maximum value
 */
public class Max implements ValueStrategy<Number, Number> {

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Number> aggregate(List<? extends Number> values) {
    if (CollectionUtils.isEmpty(values)) {
      return Optional.empty();
    }
    return (Optional<Number>)
        values.stream()
            .max(
                (Number n1, Number n2) -> {
                  BigDecimal b1 = BigDecimal.valueOf(n1.doubleValue());
                  BigDecimal b2 = BigDecimal.valueOf(n2.doubleValue());
                  return b1.compareTo(b2);
                });
  }
}
