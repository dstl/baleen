// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * List value strategy
 *
 * <p>Output values as a list
 */
public class List implements ValueStrategy<Object, java.util.List<?>> {

  @Override
  public Optional<java.util.List<?>> aggregate(java.util.List<?> values) throws BaleenException {
    if (CollectionUtils.isEmpty(values)) {
      return Optional.empty();
    }
    // NB made array list for serialisation
    return Optional.of(new ArrayList<>(values));
  }
}
