// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class RakeKeywordsTest {

  @Test
  @SuppressWarnings("deprecation")
  public void testCanConstruct() {
    RakeKeywords rk = new RakeKeywords();
    assertNotNull(rk);
  }
}
