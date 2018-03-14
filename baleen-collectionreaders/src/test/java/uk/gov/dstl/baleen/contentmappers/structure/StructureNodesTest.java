// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.structure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.uima.UIMAException;
import org.junit.Test;

import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.select.Node;
import uk.gov.dstl.baleen.uima.utils.select.NodeVisitor;
import uk.gov.dstl.baleen.uima.utils.select.Nodes;

public class StructureNodesTest extends AbstractHtmlToStructureTest {

  @Test
  public void filter() throws UIMAException {
    String h =
        "<p>Excl</p><div class=headline><p>Hello</p><p>There</p></div><div class=headline><h1>Headline</h1></div>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> els = doc.select(".headline").select("Paragraph");
    assertEquals(2, els.size());
    assertEquals("Hello", els.get(0).text());
    assertEquals("There", els.get(1).text());
  }

  @Test
  public void attributes() throws UIMAException {
    String h = "<img src=foo><img src=bar><img class=foo><img class=bar>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> withTarget = doc.select("Figure[target]");
    assertEquals(2, withTarget.size());
    assertTrue(withTarget.hasAttr("target"));
    assertFalse(withTarget.hasAttr("class"));
    assertEquals("foo", withTarget.attr("target"));
  }

  @Test
  public void hasAttr() throws UIMAException {
    Node<Structure> doc = createStructure("<p title=foo><p title=bar><p class=foo><p class=bar>");
    Nodes<Structure> ps = doc.select("Paragraph");
    assertTrue(ps.hasAttr("class"));
    assertFalse(ps.hasAttr("style"));
  }

  @Test
  public void attr() throws UIMAException {
    Node<Structure> doc = createStructure("<h1>test</h1>");
    String classVal = doc.select("Heading").attr("level");
    assertEquals("1", classVal);
  }

  @Test
  public void text() throws UIMAException {
    String h = "<div><p>Hello<p>there<p>world</div>";
    Node<Structure> doc = createStructure(h);
    assertEquals("Hello there world", doc.select("Section > *").text());
  }

  @Test
  public void hasText() throws UIMAException {
    Node<Structure> doc = createStructure("<div><p>Hello</p></div><div><p></p></div>");
    Nodes<Structure> divs = doc.select("Section");
    assertTrue(divs.hasText());
    assertFalse(doc.select("Section + Section").hasText());
  }

  @Test
  public void eq() throws UIMAException {
    String h = "<p>Hello<p>there<p>world";
    Node<Structure> doc = createStructure(h);
    assertEquals("there", doc.select("Paragraph").eq(1).text());
    assertEquals("there", doc.select("Paragraph").get(1).text());
  }

  @Test
  public void is() throws UIMAException {
    String h = "<h1>Hello</h1><h2>there</h2>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> ps = doc.select("Heading");
    assertTrue(ps.is("[level=1]"));
    assertFalse(ps.is("[level=3]"));
  }

  @Test
  public void parents() throws UIMAException {
    Node<Structure> doc = createStructure("<div><p>Hello</p></div><p>There</p>");
    Nodes<Structure> parents = doc.select("Paragraph").parents();

    assertEquals(2, parents.size());
    assertEquals("Section", parents.get(0).getTypeName());
    assertEquals("Root", parents.get(1).getTypeName());
  }

  @Test
  public void not() throws UIMAException {
    Node<Structure> doc =
        createStructure("<div id=1><p>One</p></div> <div id=2><p><em>Two</em></p></div>");

    Nodes<Structure> div1 = doc.select("Section").not(":has(Paragraph > Style)");
    assertEquals(1, div1.size());
    assertEquals("One", div1.first().text());

    Nodes<Structure> div2 = doc.select("Section").not("#1");
    assertEquals(1, div2.size());
    assertEquals("Two", div2.first().text());
  }

  @Test
  public void traverse() throws UIMAException {
    Node<Structure> doc = createStructure("<div><p>Hello</p></div><div>There</div>");
    final StringBuilder accum = new StringBuilder();
    doc.select("Section")
        .traverse(
            new NodeVisitor<Structure>() {
              @Override
              public void head(Node<Structure> node, int depth) {
                accum.append("<" + node.getTypeName() + ">");
              }

              @Override
              public void tail(Node<Structure> node, int depth) {
                accum.append("</" + node.getTypeName() + ">");
              }
            });
    assertEquals("<Section><Paragraph></Paragraph></Section><Section></Section>", accum.toString());
  }

  @Test
  public void siblings() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12</div>");

    Nodes<Structure> els = doc.select("Paragraph:eq(3)"); // gets p4 and p10
    assertEquals(2, els.size());

    Nodes<Structure> next = els.next();
    assertEquals(2, next.size());
    assertEquals("5", next.first().text());
    assertEquals("11", next.last().text());

    assertEquals(0, els.next("Paragraph:contains(6)").size());
    final Nodes<Structure> nextF = els.next("Paragraph:contains(5)");
    assertEquals(1, nextF.size());
    assertEquals("5", nextF.first().text());

    Nodes<Structure> nextA = els.nextAll();
    assertEquals(4, nextA.size());
    assertEquals("5", nextA.first().text());
    assertEquals("12", nextA.last().text());

    Nodes<Structure> nextAF = els.nextAll("Paragraph:contains(6)");
    assertEquals(1, nextAF.size());
    assertEquals("6", nextAF.first().text());

    Nodes<Structure> prev = els.prev();
    assertEquals(2, prev.size());
    assertEquals("3", prev.first().text());
    assertEquals("9", prev.last().text());

    assertEquals(0, els.prev("Paragraph:contains(1)").size());
    final Nodes<Structure> prevF = els.prev("Paragraph:contains(3)");
    assertEquals(1, prevF.size());
    assertEquals("3", prevF.first().text());

    Nodes<Structure> prevA = els.prevAll();
    assertEquals(6, prevA.size());
    assertEquals("3", prevA.first().text());
    assertEquals("7", prevA.last().text());

    Nodes<Structure> prevAF = els.prevAll("Paragraph:contains(1)");
    assertEquals(1, prevAF.size());
    assertEquals("1", prevAF.first().text());
  }

  @Test
  public void eachText() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12<p></p></div>");
    List<String> divText = doc.select("Section").eachText();
    assertEquals(2, divText.size());
    assertEquals("123456", divText.get(0));
    assertEquals("789101112", divText.get(1));

    List<String> pText = doc.select("Paragraph").eachText();
    Nodes<Structure> ps = doc.select("Paragraph");
    assertEquals(13, ps.size());
    assertEquals(12, pText.size()); // not 13, as last doesn't have text
    assertEquals("1", pText.get(0));
    assertEquals("2", pText.get(1));
    assertEquals("5", pText.get(4));
    assertEquals("7", pText.get(6));
    assertEquals("12", pText.get(11));
  }

  @Test
  public void eachAttr() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<div><a href='/foo'>1</a><a href='http://example.com/bar'>2</a><a href=''>3</a><a href='/foo'>4</a><a>5</a>");

    List<String> hrefAttrs = doc.select("Link").eachAttr("target");
    assertEquals(3, hrefAttrs.size());
    assertEquals("/foo", hrefAttrs.get(0));
    assertEquals("http://example.com/bar", hrefAttrs.get(1));
    assertEquals("/foo", hrefAttrs.get(2));
    assertEquals(3, doc.select("Link").size());
  }

  @Test
  public void empty() throws UIMAException {
    Nodes<Structure> doc = new Nodes<>();
    assertNull(doc.first());
    assertNull(doc.last());
    assertTrue(doc.isEmpty());
    assertTrue(doc.parents().isEmpty());
    assertTrue(doc.eachAttr("test").isEmpty());
    assertTrue(doc.attr("test").isEmpty());
    assertTrue(doc.eq(0).isEmpty());
    assertFalse(doc.hasAttr("class"));
  }
}
