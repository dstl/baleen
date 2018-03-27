// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class MeanValueStrategyTest {

  @Test
  public void aggregateTest() {
    Mean strategy = new Mean();
    assertFalse(strategy.aggregate(ImmutableList.of()).isPresent());
    assertEquals(0.0, strategy.aggregate(ImmutableList.of(0)).get());
    assertEquals(5.0, strategy.aggregate(ImmutableList.of(10, 0)).get());
  }
}
