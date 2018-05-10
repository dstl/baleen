// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.gov.dstl.baleen.exceptions.BaleenException;

public class ListValueStrategyTest {

  @Test
  public void aggregateTest() throws BaleenException {
    List strategy = new List();
    assertFalse(strategy.aggregate(ImmutableList.of()).isPresent());
    assertEquals(ImmutableList.of("Male"), strategy.aggregate(ImmutableList.of("Male")).get());
    assertEquals(
        ImmutableList.of("Male", "Female", "Female", "Female"),
        strategy.aggregate(ImmutableList.of("Male", "Female", "Female", "Female")).get());
    assertTrue(
        strategy.aggregate(ImmutableList.of("Male", "Female", "Female", "Female")).get()
            instanceof ArrayList);

    // NB This may fail to serialize
    assertEquals(
        ImmutableList.of(ImmutableMap.of("begin", 0, "end", 10)),
        strategy.aggregate(ImmutableList.of(ImmutableMap.of("begin", 0, "end", 10))).get());
  }
}
