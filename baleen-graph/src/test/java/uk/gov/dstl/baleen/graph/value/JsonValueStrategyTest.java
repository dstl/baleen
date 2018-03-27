// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.gov.dstl.baleen.exceptions.BaleenException;

public class JsonValueStrategyTest {

  @Test
  public void aggregateTest() throws BaleenException {
    Json strategy = new Json();
    assertFalse(strategy.aggregate(ImmutableList.of()).isPresent());
    assertEquals("[\"Male\"]", strategy.aggregate(ImmutableList.of("Male")).get());
    assertEquals(
        "[\"Male\",\"Female\",\"Female\",\"Female\"]",
        strategy.aggregate(ImmutableList.of("Male", "Female", "Female", "Female")).get());
    assertEquals(
        "[{\"begin\":0,\"end\":10}]",
        strategy.aggregate(ImmutableList.of(ImmutableMap.of("begin", 0, "end", 10))).get());
  }
}
