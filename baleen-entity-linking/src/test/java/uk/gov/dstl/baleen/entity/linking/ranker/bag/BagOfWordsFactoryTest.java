// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.ranker.bag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class BagOfWordsFactoryTest {

  @Test
  public void testEmptyBag() {
    BagOfWordsFactory factory = new BagOfWordsFactory(ImmutableSet.of());
    assertTrue(factory.bag(ImmutableList.of()).isEmpty());
    assertTrue(factory.bagSentences(ImmutableList.of()).isEmpty());
  }

  @Test
  public void testSingleWordGoesInToBag() {
    BagOfWordsFactory factory = new BagOfWordsFactory(ImmutableSet.of());
    BagOfWords bag = factory.bag(ImmutableList.of("test"));
    assertFalse(bag.isEmpty());
    assertTrue(bag.contains("test"));
    assertEquals(1, bag.count("test"));
    assertFalse(bag.contains("missing"));
  }

  @Test
  public void testDuplicatesAreInputToBag() {
    BagOfWordsFactory factory = new BagOfWordsFactory(ImmutableSet.of());
    BagOfWords bag = factory.bag(ImmutableList.of("test", "test"));
    assertFalse(bag.isEmpty());
    assertTrue(bag.contains("test"));
    assertEquals(2, bag.count("test"));
    assertFalse(bag.contains("missing"));
  }

  @Test
  public void testFactoryPreservesCase() {
    BagOfWordsFactory factory = new BagOfWordsFactory(ImmutableSet.of());
    BagOfWords bag = factory.bag(ImmutableList.of("test", "Test"));
    assertFalse(bag.isEmpty());
    assertTrue(bag.contains("test"));
    assertTrue(bag.contains("Test"));
    assertFalse(bag.contains("TEST"));
    assertEquals(1, bag.count("test"));
    assertEquals(1, bag.count("Test"));
    assertEquals(0, bag.count("TEST"));
    assertFalse(bag.contains("missing"));
  }

  @Test
  public void testStopwordDoNotGetPutInBag() {
    BagOfWordsFactory factory = new BagOfWordsFactory(ImmutableSet.of("test"));
    BagOfWords bag = factory.bag(ImmutableList.of("test"));
    assertTrue(bag.isEmpty());
  }

  @Test
  public void testNonStopwordDoGetPutInBag() {
    BagOfWordsFactory factory = new BagOfWordsFactory(ImmutableSet.of("test"));
    BagOfWords bag = factory.bag(ImmutableList.of("testing"));
    assertFalse(bag.isEmpty());
  }

  @Test
  public void testFactoryBuildFromLines() {
    BagOfWordsFactory factory = new BagOfWordsFactory(ImmutableSet.of());
    BagOfWords bag =
        factory.bagLines(ImmutableList.of("test test", "other other", "test. other", "other/test"));
    assertFalse(bag.isEmpty());
    assertTrue(bag.contains("test"));
    assertTrue(bag.contains("other"));
    assertFalse(bag.contains("TEST"));
    assertEquals(4, bag.count("test"));
    assertEquals(4, bag.count("other"));
    assertEquals(0, bag.count("TEST"));
    assertFalse(bag.contains("missing"));
  }
}
