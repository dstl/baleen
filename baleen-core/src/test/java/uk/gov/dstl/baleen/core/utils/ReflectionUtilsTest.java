// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ReflectionUtilsTest {
  @Test
  public void test() {
    assertNotNull(ReflectionUtils.getInstance());
  }
}
