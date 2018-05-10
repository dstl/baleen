// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Json value strategy
 *
 * <p>Output the object as JSON
 */
public class Json implements ValueStrategy<Object, String> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public Optional<String> aggregate(List<?> values) throws BaleenException {
    if (CollectionUtils.isEmpty(values)) {
      return Optional.empty();
    }
    try {
      return Optional.of(mapper.writeValueAsString(values));
    } catch (JsonProcessingException e) {
      throw new BaleenException(e);
    }
  }
}
