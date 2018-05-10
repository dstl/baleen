// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ModeValueStrategyTest {

  @Test
  public void aggregateTest() {
    Longest strategy = new Longest();
    assertFalse(strategy.aggregate(ImmutableList.of()).isPresent());
    assertEquals("Male", strategy.aggregate(ImmutableList.of("Male")).get());
    assertEquals(
        "Female", strategy.aggregate(ImmutableList.of("Male", "Female", "Female", "Female")).get());
  }
}
