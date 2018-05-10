// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class LongestValueStrategyTest {

  @Test
  public void aggregateTest() {
    Mode<String> strategy = new Mode<>();
    assertFalse(strategy.aggregate(ImmutableList.of()).isPresent());
    assertEquals("Male", strategy.aggregate(ImmutableList.of("Male")).get());
    assertEquals(
        "Female", strategy.aggregate(ImmutableList.of("Male", "Female", "Female", "Female")).get());
  }

  @Test
  public void aggregateWithDateTest() {
    Mode<Date> strategy = new Mode<>();
    assertTrue(strategy.aggregate(ImmutableList.of(new Date(), new Date())).isPresent());
  }
}
