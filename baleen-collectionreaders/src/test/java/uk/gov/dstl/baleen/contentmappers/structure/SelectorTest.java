// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.structure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.junit.Test;

import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.select.Node;
import uk.gov.dstl.baleen.uima.utils.select.Nodes;

public class SelectorTest extends AbstractHtmlToStructureTest {

  @Test
  public void testByTypeName() throws UIMAException {
    // should be case insensitive
    Nodes<Structure> els =
        createStructure("<div><div id=2><p>Hello</p></div></div><DIV id=3>").select("Section");
    assertEquals(3, els.size());
    assertEquals("Hello", els.get(0).text());
    assertEquals("Hello", els.get(1).text());
    assertEquals("", els.get(2).text());

    Nodes<Structure> none =
        createStructure("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("Style");
    assertEquals(0, none.size());
  }

  @Test
  public void testByAttribute() throws UIMAException {
    String h =
        "<a href=Foo />1<a href=Bar />2<a Style=Qux />3<a href=Bam />4<a href=SLAM />5"
            + "<a href='with spaces'/>";
    Node<Structure> doc = createStructure(h);

    Nodes<Structure> withTitle = doc.select("[target]");
    assertEquals(5, withTitle.size());

    Nodes<Structure> foo = doc.select("[TARGET=foo]");
    assertEquals(1, foo.size());

    Nodes<Structure> foo2 = doc.select("[target=\"foo\"]");
    assertEquals(1, foo2.size());

    Nodes<Structure> foo3 = doc.select("[target=\"Foo\"]");
    assertEquals(1, foo3.size());

    Nodes<Structure> dataName = doc.select("[target=\"with spaces\"]");
    assertEquals(1, dataName.size());
    assertEquals("with spaces", dataName.first().attr("target"));

    Nodes<Structure> not = doc.select("Link[target!=bar]");
    assertEquals(4, not.size());
    assertEquals("Foo", not.first().attr("target"));

    Nodes<Structure> starts = doc.select("[target^=ba]");
    assertEquals(2, starts.size());
    assertEquals("Bar", starts.first().attr("target"));
    assertEquals("Bam", starts.last().attr("target"));

    Nodes<Structure> ends = doc.select("[target$=am]");
    assertEquals(2, ends.size());
    assertEquals("Bam", ends.first().attr("target"));
    assertEquals("SLAM", ends.last().attr("target"));

    Nodes<Structure> contains = doc.select("[target*=a]");
    assertEquals(4, contains.size());
    assertEquals("Bar", contains.first().attr("target"));
    assertEquals("with spaces", contains.last().attr("target"));
  }

  @Test
  public void testByAttributeStarting() throws UIMAException {
    Node<Structure> doc =
        createStructure("<a id=1 href=jsoup>Hello</a><em>There</em><a href=3>No</a>");
    Nodes<Structure> withData = doc.select("[^tar]");
    assertEquals(2, withData.size());
    assertEquals("Hello", withData.first().text());
    assertEquals("No", withData.last().text());

    withData = doc.select("Style[^decor]");
    assertEquals(1, withData.size());
    assertEquals("There", withData.first().text());
  }

  @Test
  public void testByAttributeRegex() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<p><img src=foo.png id=1>1<img src=bar.jpg id=2>2<img src=qux.JPEG id=3>3<img src=old.gif>4<img>5</p>");
    Nodes<Structure> imgs = doc.select("Figure[target~=(?i)\\.(png|jpe?g)]");
    assertEquals(3, imgs.size());
    assertEquals("foo.png", imgs.get(0).attr("target"));
    assertEquals("bar.jpg", imgs.get(1).attr("target"));
    assertEquals("qux.JPEG", imgs.get(2).attr("target"));
  }

  @Test
  public void testByAttributeRegexCharacterClass() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif id=4></p>");
    Nodes<Structure> imgs = doc.select("Figure[target~=[o]]");
    assertEquals(2, imgs.size());
    assertEquals("foo.png", imgs.get(0).attr("target"));
    assertEquals("old.gif", imgs.get(1).attr("target"));
  }

  @Test
  public void testByAttributeRegexCombined() throws UIMAException {
    Node<Structure> doc = createStructure("<div><a href=x>Hello</a></div>");
    Nodes<Structure> els = doc.select("Section Link[target~=x|y]");
    assertEquals(1, els.size());
    assertEquals("Hello", els.text());
  }

  @Test
  public void testCombinedWithContains() throws UIMAException {
    Node<Structure> doc = createStructure("<p id=1>One</p><p>Two +</p><p>Three +</p>");
    Nodes<Structure> els = doc.select("Paragraph:nth-of-type(1) + :contains(+)");
    assertEquals(1, els.size());
    assertEquals("Two +", els.text());
    assertEquals("Paragraph", els.first().getTypeName());
  }

  @Test
  public void testAllNodes() throws UIMAException {
    String h = "<div><p>Hello</p><p><b>there</b></p></div>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> allDoc = doc.select("*");
    Nodes<Structure> allUnderDiv = doc.select("Section *");
    assertEquals(5, allDoc.size());
    assertEquals(3, allUnderDiv.size());
    assertEquals("Paragraph", allUnderDiv.first().getTypeName());
  }

  @Test
  public void testGroupOr() throws UIMAException {
    String h = "<a href=foo />1<a href=bar />2<div />3<p>4</p>5<img />6<img src=qux>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> els = doc.select("Link,Section,[target]");

    assertEquals(4, els.size());
    assertEquals("Link", els.get(0).getTypeName());
    assertEquals("foo", els.get(0).attr("target"));
    assertEquals("Link", els.get(1).getTypeName());
    assertEquals("bar", els.get(1).attr("target"));
    assertEquals("Section", els.get(2).getTypeName());
    assertTrue(els.get(2).attr("target").length() == 0); // missing attributes come back as empty
    // string
    assertFalse(els.get(2).hasAttr("title"));
    assertEquals("Figure", els.get(3).getTypeName());
    assertEquals("qux", els.get(3).attr("target"));
  }

  @Test
  public void testGroupOrAttribute() throws UIMAException {
    String h = "<h1 /><h2 /><img src=foo /><img src=bar />";
    Nodes<Structure> els = createStructure(h).select("[level],[target=foo]");

    assertEquals(3, els.size());
    assertEquals("1", els.get(0).attr("level"));
    assertEquals("2", els.get(1).attr("level"));
    assertEquals("foo", els.get(2).attr("target"));
  }

  @Test
  public void descendant() throws UIMAException {
    String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
    Node<Structure> root = createStructure(h);

    Nodes<Structure> els = root.select("Section Paragraph");
    assertEquals(2, els.size());
    assertEquals("Hello", els.get(0).text());
    assertEquals("There", els.get(1).text());

    Nodes<Structure> p = root.select("Paragraph:nth-of-type(1)");
    assertEquals(2, p.size());
    assertEquals("Hello", p.get(0).text());
    assertEquals("None", p.get(1).text());

    Nodes<Structure> empty = root.select("Paragraph Paragraph");
    assertEquals(0, empty.size());
  }

  @Test
  public void and() throws UIMAException {
    String h = "<a href=foo><img src=foo>Hello</img></a>";
    Node<Structure> doc = createStructure(h);

    Nodes<Structure> a = doc.select("Link[target=foo]");
    assertEquals(1, a.size());
    assertEquals("Link", a.first().getTypeName());

    Nodes<Structure> img = doc.select("Link [target=foo]");
    assertEquals(1, img.size());
    assertEquals("Figure", img.first().getTypeName());
  }

  @Test
  public void deeperDescendant() throws UIMAException {
    String h =
        "<div class=head><p><a href=first>Hello</a></div><div class=head><p class=first><a>Another</a><p>Again</div>";
    Node<Structure> root = createStructure(h);

    Nodes<Structure> els = root.select("Section Paragraph [target=first]");
    assertEquals(1, els.size());
    assertEquals("Hello", els.first().text());
    assertEquals("Link", els.first().getTypeName());

    Nodes<Structure> aboveRoot = root.select("Document Paragraph [target=first]");
    assertEquals(0, aboveRoot.size());
  }

  @Test
  public void parentChildElement() throws UIMAException {
    String h = "<div id=1>1<div>2<div>3</div></div></div><div>4</div>";
    Node<Structure> doc = createStructure(h);

    Nodes<Structure> divs = doc.select("Section > Section");
    assertEquals(2, divs.size());
    assertEquals("23", divs.get(0).text()); // 2 is child of 1
    assertEquals("3", divs.get(1).text()); // 3 is child of 2

    Nodes<Structure> div2 = doc.select("Section#1 > Section");
    assertEquals(1, div2.size());
    assertEquals("23", div2.get(0).text());
  }

  @Test
  public void parentWithClassChild() throws UIMAException {
    String h =
        "<h1 class=foo><a href=1 />1</h1><h2 class=foo><a href=2 class=bar />2</h2><h1><a href=3 />3</h1>";
    Node<Structure> doc = createStructure(h);

    Nodes<Structure> allAs = doc.select("Heading > Link");
    assertEquals(3, allAs.size());
    assertEquals("Link", allAs.first().getTypeName());

    Nodes<Structure> fooAs = doc.select("Heading[level=1] > Link");
    assertEquals(2, fooAs.size());
    assertEquals("Link", fooAs.first().getTypeName());

    Nodes<Structure> barAs = doc.select("Heading[level=2] > Link[class=bar]");
    assertEquals(1, barAs.size());
  }

  @Test
  public void parentChildStar() throws UIMAException {
    String h = "<div id=1><p>Hello<p><b>there</b></p></div><div id=2><a>Hi</a></div>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> divChilds = doc.select("Section > *");
    assertEquals(3, divChilds.size());
    assertEquals("Paragraph", divChilds.get(0).getTypeName());
    assertEquals("Paragraph", divChilds.get(1).getTypeName());
    assertEquals("Anchor", divChilds.get(2).getTypeName());
  }

  @Test
  public void multiChildDescent() throws UIMAException {
    String h = "<div id=foo><h1 class=bar><a href=http://example.com/>One</a></h1></div>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> els = doc.select("Section > Heading[level=1] > Link[target*=example]");
    assertEquals(1, els.size());
    assertEquals("Link", els.first().getTypeName());
  }

  @Test
  public void caseInsensitive() throws UIMAException {
    String h = "<A href=bAr><a href=foo>"; // mixed case so a simple toLowerCase() on value doesn't
    // catch
    Node<Structure> doc = createStructure(h);

    assertEquals(2, doc.select("Link").size());
    assertEquals(2, doc.select("LInk[taRget]").size());
    assertEquals(1, doc.select("LINK[Target=BAR]").size());
    assertEquals(0, doc.select("Link[TARGET=BARBARELLA]").size());
  }

  @Test
  public void adjacentSiblings() throws UIMAException {
    String h = "<ol><li>One<li>Two<li>Three</ol>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> sibs = doc.select("ListItem + ListItem");
    assertEquals(2, sibs.size());
    assertEquals("Two", sibs.get(0).text());
    assertEquals("Three", sibs.get(1).text());
  }

  @Test
  public void notAdjacent() throws UIMAException {
    String h = "<h1>One</h1><h2>Two</h2><h3>Three</h3>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> sibs = doc.select("Heading[level=1] + Heading[level=3]");
    assertEquals(0, sibs.size());
  }

  @Test
  public void mixCombinator() throws UIMAException {
    String h = "<h1><div class=foo><ol><li>One<li>Two<li>Three</ol></div></h1>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> sibs = doc.select("Heading > Section ListItem + ListItem");

    assertEquals(2, sibs.size());
    assertEquals("Two", sibs.get(0).text());
    assertEquals("Three", sibs.get(1).text());
  }

  @Test
  public void mixCombinatorGroup() throws UIMAException {
    String h = "<h1><ol><li>One<li>Two<li>Three</ol></h1>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> els = doc.select("[level=1] > Ordered, Ordered > ListItem + ListItem");

    assertEquals(3, els.size());
    assertEquals("Ordered", els.get(0).getTypeName());
    assertEquals("Two", els.get(1).text());
    assertEquals("Three", els.get(2).text());
  }

  @Test
  public void generalSiblings() throws UIMAException {
    String h = "<h1>One</h1><h2>Two</h2><h3>Three</h3>";
    Node<Structure> doc = createStructure(h);
    Nodes<Structure> sibs = doc.select("Heading[level=1] ~ Heading[level=3]");
    assertEquals(1, sibs.size());
    assertEquals("Three", sibs.first().text());
  }

  // for http://github.com/jhy/jsoup/issues#issue/13
  @Test
  public void testSupportsLeadingCombinator() throws UIMAException {
    String h = "<div><p><a>One</a><a>Two</a></p></div>";
    Node<Structure> doc = createStructure(h);

    Node<Structure> p = doc.select("Section > Paragraph").first();
    Nodes<Structure> spans = p.select("> Anchor");
    assertEquals(2, spans.size());
    assertEquals("One", spans.first().text());

    // make sure doesn't get nested
    h = "<div id=1>1<div id=2>2<div id=3>3</div></div></div>";
    doc = createStructure(h);
    Node<Structure> div = doc.select("Section").select(" > Section").first();
    assertEquals("23", div.text());
  }

  @Test
  public void testPseudoLessThan() throws UIMAException {
    Node<Structure> doc =
        createStructure("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
    Nodes<Structure> ps = doc.select("Section Paragraph:lt(2)");
    assertEquals(3, ps.size());
    assertEquals("One", ps.get(0).text());
    assertEquals("Two", ps.get(1).text());
    assertEquals("Four", ps.get(2).text());
  }

  @Test
  public void testPseudoGreaterThan() throws UIMAException {
    Node<Structure> doc =
        createStructure("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
    Nodes<Structure> ps = doc.select("Section Paragraph:gt(0)");
    assertEquals(2, ps.size());
    assertEquals("Two", ps.get(0).text());
    assertEquals("Three", ps.get(1).text());
  }

  @Test
  public void testPseudoEquals() throws UIMAException {
    Node<Structure> doc =
        createStructure("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
    Nodes<Structure> ps = doc.select("Section Paragraph:eq(0)");
    assertEquals(2, ps.size());
    assertEquals("One", ps.get(0).text());
    assertEquals("Four", ps.get(1).text());

    Nodes<Structure> ps2 = doc.select("Section:eq(0) Paragraph:eq(0)");
    assertEquals(1, ps2.size());
    assertEquals("One", ps2.get(0).text());
    assertEquals("Paragraph", ps2.get(0).getTypeName());
  }

  @Test
  public void testPseudoBetween() throws UIMAException {
    Node<Structure> doc =
        createStructure("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
    Nodes<Structure> ps = doc.select("Section Paragraph:gt(0):lt(2)");
    assertEquals(1, ps.size());
    assertEquals("Two", ps.get(0).text());
  }

  @Test
  public void testPseudoCombined() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<h1 class='foo'><p>One</p><p>Two</p></h1><h1><p>Three</p><p>Four</p></h1>");
    Nodes<Structure> ps = doc.select("Heading.foo Paragraph:gt(0)");
    assertEquals(1, ps.size());
    assertEquals("Two", ps.get(0).text());
  }

  @Test
  public void testPseudoHas() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<div id=0><p><img>Hello</img></p></div> <div id=1><img src=foo>There</img></div> <div id=2><p>Not</p></div>");

    Nodes<Structure> divs1 = doc.select("Section:has(Figure)");
    assertEquals(2, divs1.size());
    assertEquals("Hello", divs1.get(0).text());
    assertEquals("There", divs1.get(1).text());

    Nodes<Structure> divs2 = doc.select("Section:has([target])");
    assertEquals(1, divs2.size());
    assertEquals("There", divs2.get(0).text());

    Nodes<Structure> divs3 = doc.select("Section:has(Figure, Paragraph)");
    assertEquals(3, divs3.size());
    assertEquals("Hello", divs3.get(0).text());
    assertEquals("There", divs3.get(1).text());
    assertEquals("Not", divs3.get(2).text());

    Nodes<Structure> els1 = doc.select(":has(Paragraph)");
    assertEquals(3, els1.size()); // body, div, dib
    assertEquals("Root", els1.first().getTypeName());
    assertEquals("Hello", els1.get(1).text());
    assertEquals("Not", els1.get(2).text());
  }

  @Test
  public void testNestedHas() throws UIMAException {
    Node<Structure> doc = createStructure("<div><p><a>One</a></p></div> <div><p>Two</p></div>");
    Nodes<Structure> divs = doc.select("Section:has(Paragraph:has(Anchor))");
    assertEquals(1, divs.size());
    assertEquals("One", divs.first().text());

    // test matches in has
    divs = doc.select("Section:has(Paragraph:matches((?i)two))");
    assertEquals(1, divs.size());
    assertEquals("Section", divs.first().getTypeName());
    assertEquals("Two", divs.first().text());

    // test contains in has
    divs = doc.select("Section:has(Paragraph:contains(two))");
    assertEquals(1, divs.size());
    assertEquals("Section", divs.first().getTypeName());
    assertEquals("Two", divs.first().text());
  }

  @Test
  public void testPseudoContains() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<div><p>The Rain.</p> <p class=light>The <i>rain</i>.</p> <p>Rain, the.</p></div>");

    Nodes<Structure> ps1 = doc.select("Paragraph:contains(Rain)");
    assertEquals(3, ps1.size());

    Nodes<Structure> ps2 = doc.select("Paragraph:contains(the rain)");
    assertEquals(2, ps2.size());
    assertEquals("The Rain.", ps2.first().text());
    assertEquals("The rain.", ps2.last().text());

    Nodes<Structure> ps3 = doc.select("Paragraph:contains(the Rain):has(Style)");
    assertEquals(1, ps3.size());
    assertEquals("The rain.", ps3.first().text());

    Nodes<Structure> ps5 = doc.select(":contains(rain)");
    assertEquals(5, ps5.size()); // Section, Paragraph, Paragraph, Style, Paragraph
  }

  @Test
  public void testPsuedoContainsWithParentheses() throws UIMAException {
    Node<Structure> doc =
        createStructure("<div><p id=1>This (is good)</p><p id=2>This is bad)</p>");

    Nodes<Structure> ps1 = doc.select("Paragraph:contains(this (is good))");
    assertEquals(1, ps1.size());
    assertEquals("This (is good)", ps1.first().text());

    Nodes<Structure> ps2 = doc.select("Paragraph:contains(this is bad\\))");
    assertEquals(1, ps2.size());
    assertEquals("This is bad)", ps2.first().text());
  }

  @Test
  public void containsOwn() throws UIMAException {
    Node<Structure> doc = createStructure("<p id=1>Hello <b>there</b> now</p>");
    Nodes<Structure> ps = doc.select("Paragraph:containsOwn(Hello  now)");
    assertEquals(1, ps.size());
    assertEquals("Hello there now", ps.first().text());

    assertEquals(0, doc.select("Paragraph:containsOwn(there)").size());
  }

  @Test
  public void testMatches() throws UIMAException {
    Node<Structure> doc =
        createStructure(
            "<p id=1>The <i>Rain</i></p> <p id=2>There are 99 bottles.</p> <p id=3>Harder (this)</p> <p id=4>Rain</p>");

    Nodes<Structure> p1 = doc.select("Paragraph:matches(The rain)"); // no match,
    // case
    // sensitive
    assertEquals(0, p1.size());

    Nodes<Structure> p2 = doc.select("Paragraph:matches((?i)the rain)"); // case
    // insense.
    // should
    // include root,
    // html,
    // body
    assertEquals(1, p2.size());
    assertEquals("The Rain", p2.first().text());

    Nodes<Structure> p4 = doc.select("Paragraph:matches((?i)^rain$)"); // bounding
    assertEquals(1, p4.size());
    assertEquals("Rain", p4.first().text());

    Nodes<Structure> p5 = doc.select("Paragraph:matches(\\d+)");
    assertEquals(1, p5.size());
    assertEquals("There are 99 bottles.", p5.first().text());

    Nodes<Structure> p6 = doc.select("Paragraph:matches(\\w+\\s+\\(\\w+\\))"); // test
    // bracket
    // matching
    assertEquals(1, p6.size());
    assertEquals("Harder (this)", p6.first().text());

    Nodes<Structure> p7 = doc.select("Paragraph:matches((?i)the):has(Style)"); // multi
    assertEquals(1, p7.size());
    assertEquals("The Rain", p7.first().text());
  }

  @Test
  public void matchesOwn() throws UIMAException {
    Node<Structure> doc = createStructure("<p id=1>Hello <b>there</b> now</p>");

    Nodes<Structure> p1 = doc.select("Paragraph:matchesOwn((?i)hello  now)");
    assertEquals(1, p1.size());
    assertEquals("Hello there now", p1.first().text());

    assertEquals(0, doc.select("Paragraph:matchesOwn(there)").size());
  }

  @Test
  public void notParas() throws UIMAException {
    Node<Structure> doc = createStructure("<h1 id=1>One</h1> <h2>Two</h2> <h3><a>Three</a></h3>");

    Nodes<Structure> el1 = doc.select("Heading:not([level=1])");
    assertEquals(2, el1.size());
    assertEquals("Two", el1.first().text());
    assertEquals("Three", el1.last().text());

    Nodes<Structure> el2 = doc.select("Heading:not(:has(Anchor))");
    assertEquals(2, el2.size());
    assertEquals("One", el2.first().text());
    assertEquals("Two", el2.last().text());
  }

  @Test
  public void notAll() throws UIMAException {
    Node<Structure> doc = createStructure("<p>Two</p> <p><a>Three</a></p>");

    Nodes<Structure> el1 = doc.select(":not(Paragraph)"); // should just be the a
    assertEquals(2, el1.size());
    assertEquals("Root", el1.first().getTypeName());
    assertEquals("Anchor", el1.last().getTypeName());
  }

  @Test
  public void handlesCommasInSelector() throws UIMAException {
    Node<Structure> doc =
        createStructure("<a href='1,2'>One</a><div>Two</div><ol><li>123</li><li>Text</li></ol>");

    Nodes<Structure> ps = doc.select("[target=1,2]");
    assertEquals(1, ps.size());

    Nodes<Structure> containers = doc.select("Section, ListItem:matches([0-9,]+)");
    assertEquals(2, containers.size());
    assertEquals("Section", containers.get(0).getTypeName());
    assertEquals("ListItem", containers.get(1).getTypeName());
    assertEquals("123", containers.get(1).text());
  }

  @Test
  public void selectClassWithSpace() throws UIMAException {
    final String html =
        "<a href=\"value\">class without space</a>\n" + "<a href=\"value \">class with space</a>";

    Node<Structure> doc = createStructure(html);

    Nodes<Structure> found = doc.select("Link[target=value ]");
    assertEquals(2, found.size());
    assertEquals("class without space", found.get(0).text());
    assertEquals("class with space", found.get(1).text());

    found = doc.select("Link[target=\"value \"]");
    assertEquals(2, found.size());
    assertEquals("class without space", found.get(0).text());
    assertEquals("class with space", found.get(1).text());

    found = doc.select("Link[target=\"value\\ \"]");
    assertEquals(0, found.size());
  }

  @Test
  public void selectSameNodes() throws UIMAException {
    final String html = "<div>one</div><div>one</div>";

    Node<Structure> doc = createStructure(html);
    Nodes<Structure> els = doc.select("Section");
    assertEquals(2, els.size());

    Nodes<Structure> subSelect = els.select(":contains(one)");
    assertEquals(2, subSelect.size());
  }

  @Test
  public void attributeWithBrackets() throws UIMAException {
    String html = "<a href='End]'>One</a> <a href='[Another)]]'>Two</a>";
    Node<Structure> doc = createStructure(html);
    assertEquals("One", doc.select("Link[target='End]']").first().text());
    assertEquals("Two", doc.select("Link[target='[Another)]]']").first().text());
    assertEquals("One", doc.select("Link[target=\"End]\"]").first().text());
    assertEquals("Two", doc.select("Link[target=\"[Another)]]\"]").first().text());
  }

  @Test
  public void containsWithQuote() throws UIMAException {
    String html = "<p>One'One</p><p>One'Two</p>";
    Node<Structure> doc = createStructure(html);
    Nodes<Structure> els = doc.select("Paragraph:contains(One\\'One)");
    assertEquals(1, els.size());
    assertEquals("One'One", els.text());
  }
}
