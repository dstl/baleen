// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;

public class IdentityUtilsTest {
  @Test
  public void test() throws BaleenException {
    assertEquals(
        "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
        IdentityUtils.hashStrings("hello"));
    assertEquals(
        "185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969",
        IdentityUtils.hashStrings("Hello"));
    assertEquals(
        "872e4e50ce9990d8b041330c47c9ddd11bec6b503ae9386a99da8584e9bb12c4",
        IdentityUtils.hashStrings("Hello", "World"));
    assertEquals("", IdentityUtils.hashStrings((String[]) null));
  }
}
