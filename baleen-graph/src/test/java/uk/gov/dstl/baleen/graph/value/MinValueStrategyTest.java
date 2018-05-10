// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class MinValueStrategyTest {

  @Test
  public void aggregateTest() {
    Min strategy = new Min();
    assertFalse(strategy.aggregate(ImmutableList.of()).isPresent());
    assertEquals(0, strategy.aggregate(ImmutableList.of(0)).get());
    assertEquals(0, strategy.aggregate(ImmutableList.of(0, 10, 9, 3)).get());
  }
}
