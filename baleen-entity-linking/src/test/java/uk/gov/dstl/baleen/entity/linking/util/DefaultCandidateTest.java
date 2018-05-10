// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class DefaultCandidateTest {

  private static final String TEST = "test";

  @Test
  public void candidateTest() {
    DefaultCandidate candidate = new DefaultCandidate(TEST, TEST, ImmutableMap.of(TEST, TEST));
    assertEquals(candidate, candidate);
    assertEquals(ImmutableMap.of(TEST, TEST), candidate.getKeyValuePairs());
    assertEquals(TEST, candidate.getId());
    assertEquals(TEST, candidate.getName());
    assertEquals("Id: test, Name: test\nKey: test Value: test", candidate.toString());
  }
}
