// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.javadoc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BaleenJavadocTest {

  private static final String TEXT = "This is a test";
  private static final String TR = "tr";

  @Test
  public void testWrap() {
    assertEquals("<tr>This is a test</tr>", BaleenJavadoc.wrapWithTag(TR, TEXT, null));
    assertEquals("<tr style=\"\">This is a test</tr>", BaleenJavadoc.wrapWithTag(TR, TEXT, ""));
    assertEquals(
        "<tr style=\"padding-right: 20px\">This is a test</tr>",
        BaleenJavadoc.wrapWithTag(TR, TEXT, "padding-right: 20px"));
  }
}
