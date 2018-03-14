// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import static org.junit.Assert.assertEquals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.contentmanipulators.helpers.MarkupUtils;

public class ParagraphMarkedClassificationTest {

  private ParagraphMarkedClassification m;

  @Before
  public void before() {
    m = new ParagraphMarkedClassification();
  }

  @Test
  public void testNoMarking() {
    Document doc = Jsoup.parseBodyFragment("<p>This is some text</p>");
    m.manipulate(doc);

    assertEquals(doc.body().text(), "This is some text");
  }

  @Test
  public void testMarking() {
    Document doc = Jsoup.parseBodyFragment("<p>(UK OFFICIAL)This is some text</p>");
    m.manipulate(doc);

    assertEquals(
        MarkupUtils.getAttribute(doc.body().select("p").first(), "classification"), "UK OFFICIAL");
    assertEquals(doc.body().text(), "This is some text");
  }
}
