// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class MaximunIndexTest {

  @Test
  public void testEmptyArray() {
    MaximumIndex maximumIndex = new MaximumIndex(new double[0]);
    int find = maximumIndex.find();
    assertEquals(-1, find);
  }

  @Test
  public void testForMax() {
    MaximumIndex maximumIndex = new MaximumIndex(new double[] {0, 1.0, 2.0, 1.5, -1});
    int find = maximumIndex.find();
    assertEquals(2, find);
  }

  @Test
  public void testNoClearMax() {
    MaximumIndex maximumIndex = new MaximumIndex(new double[] {0, 1.0, 1.0, 0.5});
    int find = maximumIndex.find();
    assertTrue(ImmutableSet.of(1, 2).contains(find));
  }
}
