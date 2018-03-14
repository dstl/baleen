// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import static org.junit.Assert.assertEquals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

public class CommentAreaTest {

  private CommentArea manipulator;

  @Before
  public void before() {
    manipulator = new CommentArea();
  }

  @Test
  public void testSingle() {
    Document document =
        Jsoup.parseBodyFragment(
            "<p><b>THIS IS A SUBJECT HEADING</b></p><p>COMMENT: This is a comment. COMMENT ENDS</p><p>This is more text</p><p>COMMENT: This is a another comment. COMMENT ENDS</p>");

    manipulator.manipulate(document);

    Elements asides = document.select("aside");
    assertEquals(2, asides.size());
    assertEquals("COMMENT: This is a comment. COMMENT ENDS", asides.first().text());
    assertEquals("COMMENT: This is a another comment. COMMENT ENDS", asides.last().text());
  }

  @Test
  public void testMultiParagraph() {
    Document document =
        Jsoup.parseBodyFragment(
            "<p>Not a comment</p><p>COMMENT: Line one</p><p>Line two COMMENT ENDS</p>");

    manipulator.manipulate(document);

    Elements asides = document.select("aside");
    assertEquals(2, asides.size());
    assertEquals("COMMENT: Line one", asides.first().text());
    assertEquals("Line two COMMENT ENDS", asides.last().text());
  }
}
