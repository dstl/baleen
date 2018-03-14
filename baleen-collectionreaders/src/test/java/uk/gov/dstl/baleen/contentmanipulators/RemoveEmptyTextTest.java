// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

public class RemoveEmptyTextTest {

  private RemoveEmptyText m;

  @Before
  public void before() {
    m = new RemoveEmptyText();
  }

  @Test
  public void testAlreadyEmpty() {

    Document doc = Jsoup.parseBodyFragment("");
    m.manipulate(doc);

    assertNotNull(doc.body());
  }

  @Test
  public void testNonEmpty() {

    Document doc = Jsoup.parseBodyFragment("<p>Hello</p>");
    m.manipulate(doc);

    assertFalse(doc.body().select("p").isEmpty());
  }

  @Test
  public void testSingleEmpty() {

    Document doc = Jsoup.parseBodyFragment("<p></p>");
    m.manipulate(doc);

    assertTrue(doc.body().select("p").isEmpty());
  }

  @Test
  public void testTwoEmpty() {

    Document doc = Jsoup.parseBodyFragment("<p></p><div></div>");
    m.manipulate(doc);
    assertTrue(doc.body().select("*").not("body").isEmpty());
  }

  @Test
  public void testMixedEmpty() {

    Document doc = Jsoup.parseBodyFragment("<p></p><div></div><p>Hello</p>");
    m.manipulate(doc);

    assertEquals(doc.body().select("p").size(), 1);
  }

  @Test
  public void testHierarchyOfEmpty() {

    Document doc = Jsoup.parseBodyFragment("<div><p></p></div>");
    m.manipulate(doc);

    assertTrue(doc.body().select("*").not("body").isEmpty());
  }
}
