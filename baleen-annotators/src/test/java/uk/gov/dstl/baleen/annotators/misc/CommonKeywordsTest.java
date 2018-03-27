// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CommonKeywordsTest {

  @Test
  @SuppressWarnings("deprecation")
  public void testCanConstruct() {
    CommonKeywords ck = new CommonKeywords();
    assertNotNull(ck);
  }
}
