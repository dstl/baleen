// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.ranker.bag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class BagOfWordsTest {

  @Test
  public void testEmptyBag() {
    BagOfWords bagOfWords = new BagOfWords();
    assertTrue(bagOfWords.isEmpty());
    assertFalse(bagOfWords.contains("test"));
    assertEquals(0, bagOfWords.count("test"));
    assertEquals(0, bagOfWords.size());
  }

  public void testBagContains() {
    BagOfWords bagOfWords = new BagOfWords(ImmutableSet.of("test"));
    assertFalse(bagOfWords.isEmpty());
    assertTrue(bagOfWords.contains("test"));
    assertEquals(1, bagOfWords.count("test"));
    assertEquals(1, bagOfWords.size());
  }

  public void testBagContainsDuplicates() {
    BagOfWords bagOfWords = new BagOfWords("test", "test");
    assertFalse(bagOfWords.isEmpty());
    assertTrue(bagOfWords.contains("test"));
    assertEquals(2, bagOfWords.count("test"));
    assertEquals(2, bagOfWords.size());
  }

  public void testNoIntersection() {
    BagOfWords bag1 = new BagOfWords("test");
    BagOfWords bag2 = new BagOfWords("other");

    BagOfWords bag3 = bag1.retain(bag2);
    assertTrue(bag3.isEmpty());
    assertEquals(0, bag3.size());
  }

  public void testIntersection() {
    BagOfWords bag1 = new BagOfWords("test", "intersect");
    BagOfWords bag2 = new BagOfWords("intersect", "other");

    BagOfWords bag3 = bag1.retain(bag2);
    assertFalse(bag3.isEmpty());
    assertFalse(bag3.contains("intersect"));
    assertEquals(1, bag3.count("intersect"));
    assertEquals(1, bag3.size());
  }

  public void testIntersectionRetainsCount() {
    BagOfWords bag1 = new BagOfWords("test", "intersect", "intersect", "intersect");
    BagOfWords bag2 = new BagOfWords("intersect", "other");

    BagOfWords bag3 = bag1.retain(bag2);
    assertFalse(bag3.isEmpty());
    assertFalse(bag3.contains("intersect"));
    assertEquals(3, bag3.count("intersect"));
    assertEquals(3, bag3.size());
  }
}
