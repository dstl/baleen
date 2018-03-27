// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ScoredCandidateTest {

  private static final String TEST = "test";
  private static final String OTHER = "other";

  @Test
  public void scoredCandidateDelegates() {
    DefaultCandidate delegate = new DefaultCandidate(TEST, TEST, ImmutableMap.of(TEST, TEST));
    ScoredCandidate scoredCandidate = new ScoredCandidate(delegate, 10);
    assertEquals(TEST, scoredCandidate.getId());
    assertEquals(TEST, scoredCandidate.getName());
    assertEquals(ImmutableMap.of(TEST, TEST), scoredCandidate.getKeyValuePairs());
    assertEquals("test:10", scoredCandidate.toString());
  }

  @Test
  public void scoredCandidateEquals() {
    DefaultCandidate delegate1 = new DefaultCandidate(TEST, TEST, ImmutableMap.of(TEST, TEST));
    DefaultCandidate delegate2 = new DefaultCandidate(OTHER, OTHER, ImmutableMap.of(OTHER, OTHER));
    ScoredCandidate scored1 = new ScoredCandidate(delegate1, 10);
    ScoredCandidate scored2 = new ScoredCandidate(delegate1, 0);
    ScoredCandidate scored0 = new ScoredCandidate(delegate2, 10);
    assertEquals(scored1, scored1);
    // just checks id, not score
    assertEquals(scored1, scored2);
    assertTrue(scored1.equals(delegate1));

    assertFalse(scored1.equals(null));
    assertFalse(scored1.equals(scored0));
  }
}
