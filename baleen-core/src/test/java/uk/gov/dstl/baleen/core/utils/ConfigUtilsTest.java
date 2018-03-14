// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigUtilsTest {
  @Test
  public void testInteger() {
    assertEquals(new Integer(57), ConfigUtils.stringToInteger("57", 10));
    assertEquals(new Integer(10), ConfigUtils.stringToInteger("abc", 10));
    assertEquals(new Integer(10), ConfigUtils.stringToInteger("57.2", 10));
  }

  @Test
  public void testLong() {
    assertEquals(new Long(57), ConfigUtils.stringToLong("57", 10L));
    assertEquals(new Long(10), ConfigUtils.stringToLong("abc", 10L));
    assertEquals(new Long(10), ConfigUtils.stringToLong("57.2", 10L));
  }

  @Test
  public void testFloat() {
    assertEquals(new Float(57), ConfigUtils.stringToFloat("57", new Float(10.0)));
    assertEquals(new Float(10), ConfigUtils.stringToFloat("abc", new Float(10.0)));
    assertEquals(new Float(57.2), ConfigUtils.stringToFloat("57.2", new Float(10.0)));
  }
}
