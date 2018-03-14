// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.types.structure.Aside;
import uk.gov.dstl.baleen.types.structure.Break;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.types.structure.Figure;
import uk.gov.dstl.baleen.types.structure.Header;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Section;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.structure.Style;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;

public class SelectTest {

  private Node<Structure> root = null;
  private static String text;
  private JCas jCas;

  @BeforeClass
  public static void initClass() {
    StringBuilder sb = new StringBuilder(" ");

    for (int i = 1; i <= 10; i++) {
      sb.append(String.format("%d", i));
    }

    for (int i = 1; i <= 10; i++) {
      sb.append(String.format("%d", i));
      sb.append(String.format("%d", i));
      sb.append(String.format("%d", i));
      sb.append(String.format("%d", i));
    }

    sb.append("\n");
    sb.append("\n");
    sb.append("\n");
    sb.append("Some text before the only child in this div");
    text = sb.toString();
  }

  @Before
  public void init() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText(text);

    int cursor = 0;
    int depth = 0;

    Document document = new Document(jCas);
    document.setBegin(cursor);
    document.setDepth(depth);

    depth++;

    Header header = new Header(jCas);
    header.setBegin(cursor);
    header.setEnd(cursor);
    header.setDepth(depth);
    header.addToIndexes();

    Section section1 = new Section(jCas);
    section1.setBegin(++cursor);
    section1.setDepth(depth);

    depth++;

    for (int i = 1; i <= 10; i++) {
      Paragraph p = new Paragraph(jCas);
      p.setBegin(cursor);
      cursor += Integer.valueOf(i).toString().length();
      p.setEnd(cursor);
      p.setDepth(depth);
      p.addToIndexes();
    }

    depth--;

    section1.setEnd(cursor);
    section1.addToIndexes();

    Section section2 = new Section(jCas);
    section2.setBegin(cursor);
    section2.setDepth(depth);

    depth++;

    for (int i = 1; i <= 10; i++) {
      Paragraph p = new Paragraph(jCas);
      p.setBegin(cursor);
      cursor += Integer.valueOf(i).toString().length();
      p.setEnd(cursor);
      p.setDepth(depth);
      p.addToIndexes();
      Aside a = new Aside(jCas);
      a.setBegin(cursor);
      cursor += Integer.valueOf(i).toString().length();
      a.setEnd(cursor);
      a.setDepth(depth);
      a.addToIndexes();
      Style s = new Style(jCas);
      s.setBegin(cursor);
      cursor += Integer.valueOf(i).toString().length();
      s.setEnd(cursor);
      s.setDepth(depth);
      s.addToIndexes();
      Figure f = new Figure(jCas);
      f.setBegin(cursor);
      cursor += Integer.valueOf(i).toString().length();
      f.setEnd(cursor);
      f.setDepth(depth);
      f.addToIndexes();
    }

    depth--;

    section2.setEnd(cursor);
    section2.addToIndexes();

    Section section3 = new Section(jCas);
    section3.setBegin(cursor);
    section3.setDepth(depth);

    depth++;

    Break break1 = new Break(jCas);
    break1.setBegin(cursor);
    break1.setEnd(++cursor);
    break1.setDepth(depth);
    break1.addToIndexes();

    depth--;

    section3.setEnd(cursor);
    section3.addToIndexes();

    Paragraph empty = new Paragraph(jCas);
    empty.setBegin(++cursor);
    empty.setEnd(cursor);
    empty.setDepth(depth);
    empty.addToIndexes();

    Section section4 = new Section(jCas);
    section4.setBegin(++cursor);
    section4.setDepth(depth);

    depth++;

    cursor += "Some text before the ".length();

    Style em = new Style(jCas);
    em.setBegin(cursor);
    em.setDepth(depth);
    cursor += "only".length();
    em.setEnd(cursor);
    em.addToIndexes();

    cursor += " child in this div".length();
    section4.setEnd(cursor);
    section4.addToIndexes();

    document.setEnd(cursor);
    document.addToIndexes();

    root = StructureHierarchy.build(jCas, StructureUtil.getStructureClasses()).getRoot();
  }

  @Test
  public void firstChild() {
    check(root.select("Section:nth-of-type(1) :first-child"), "1");
    check(root.select("root:first-child"));
  }

  @Test
  public void lastChild() {
    check(root.select("Section:nth-of-type(1) :last-child"), "10");
    check(root.select("root:last-child"));
  }

  @Test
  public void nthChild_simple() {
    for (int i = 1; i <= 10; i++) {
      check(
          root.select(String.format("Section:nth-of-type(1) :nth-child(%d)", i)),
          String.valueOf(i));
    }
  }

  @Test
  public void nthOfType_unknownTag() {
    for (int i = 1; i <= 10; i++) {
      check(
          root.select(String.format("Section:nth-of-type(2) Figure:nth-of-type(%d)", i)),
          String.valueOf(i));
    }
  }

  @Test
  public void nthLastChild_simple() {
    for (int i = 1; i <= 10; i++) {
      check(
          root.select(String.format("Section:nth-of-type(1) :nth-last-child(%d)", i)),
          String.valueOf(11 - i));
    }
  }

  @Test
  public void nthOfType_simple() {
    for (int i = 1; i <= 10; i++) {
      check(
          root.select(String.format("Section:nth-of-type(2) Paragraph:nth-of-type(%d)", i)),
          String.valueOf(i));
    }
  }

  @Test
  public void nthLastOfType_simple() {
    for (int i = 1; i <= 10; i++) {
      check(
          root.select(String.format("Section:nth-of-type(2) :nth-last-of-type(%d)", i)),
          String.valueOf(11 - i),
          String.valueOf(11 - i),
          String.valueOf(11 - i),
          String.valueOf(11 - i));
    }
  }

  @Test
  public void nthChild_advanced() {
    check(root.select("Section:nth-of-type(1) :nth-child(-5)"));
    check(root.select("Section:nth-of-type(1) :nth-child(odd)"), "1", "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(1) :nth-child(2n-1)"), "1", "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(1) :nth-child(2n+1)"), "1", "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(1) :nth-child(2n+3)"), "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(1) :nth-child(even)"), "2", "4", "6", "8", "10");
    check(root.select("Section:nth-of-type(1) :nth-child(2n)"), "2", "4", "6", "8", "10");
    check(root.select("Section:nth-of-type(1) :nth-child(3n-1)"), "2", "5", "8");
    check(root.select("Section:nth-of-type(1) :nth-child(-2n+5)"), "1", "3", "5");
    check(root.select("Section:nth-of-type(1) :nth-child(+5)"), "5");
  }

  @Test
  public void nthOfType_advanced() {
    check(root.select("Section:nth-of-type(2) :nth-of-type(-5)"));
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-of-type(odd)"), "1", "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(2) Style:nth-of-type(2n-1)"), "1", "3", "5", "7", "9");
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-of-type(2n+1)"), "1", "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(2) Aside:nth-of-type(2n+3)"), "3", "5", "7", "9");
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-of-type(even)"),
        "2",
        "4",
        "6",
        "8",
        "10");
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-of-type(2n)"), "2", "4", "6", "8", "10");
    check(root.select("Section:nth-of-type(2) Paragraph:nth-of-type(3n-1)"), "2", "5", "8");
    check(root.select("Section:nth-of-type(2) Paragraph:nth-of-type(-2n+5)"), "1", "3", "5");
    check(root.select("Section:nth-of-type(2) :nth-of-type(+5)"), "5", "5", "5", "5");
  }

  @Test
  public void nthLastChild_advanced() {
    check(root.select("Section:nth-of-type(1) :nth-last-child(-5)"));
    check(root.select("Section:nth-of-type(1) :nth-last-child(odd)"), "2", "4", "6", "8", "10");
    check(root.select("Section:nth-of-type(1) :nth-last-child(2n-1)"), "2", "4", "6", "8", "10");
    check(root.select("Section:nth-of-type(1) :nth-last-child(2n+1)"), "2", "4", "6", "8", "10");
    check(root.select("Section:nth-of-type(1) :nth-last-child(2n+3)"), "2", "4", "6", "8");
    check(root.select("Section:nth-of-type(1) :nth-last-child(even)"), "1", "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(1) :nth-last-child(2n)"), "1", "3", "5", "7", "9");
    check(root.select("Section:nth-of-type(1) :nth-last-child(3n-1)"), "3", "6", "9");

    check(root.select("Section:nth-of-type(1) :nth-last-child(-2n+5)"), "6", "8", "10");
    check(root.select("Section:nth-of-type(1) :nth-last-child(+5)"), "6");
  }

  @Test
  public void nthLastOfType_advanced() {
    check(root.select("Section:nth-of-type(2) :nth-last-of-type(-5)"));
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-last-of-type(odd)"),
        "2",
        "4",
        "6",
        "8",
        "10");
    check(
        root.select("Section:nth-of-type(2) Style:nth-last-of-type(2n-1)"),
        "2",
        "4",
        "6",
        "8",
        "10");
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-last-of-type(2n+1)"),
        "2",
        "4",
        "6",
        "8",
        "10");
    check(root.select("Section:nth-of-type(2) Aside:nth-last-of-type(2n+3)"), "2", "4", "6", "8");
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-last-of-type(even)"),
        "1",
        "3",
        "5",
        "7",
        "9");
    check(
        root.select("Section:nth-of-type(2) Paragraph:nth-last-of-type(2n)"),
        "1",
        "3",
        "5",
        "7",
        "9");
    check(root.select("Section:nth-of-type(2) Paragraph:nth-last-of-type(3n-1)"), "3", "6", "9");

    check(root.select("Section:nth-of-type(2) Aside:nth-last-of-type(-2n+5)"), "6", "8", "10");
    check(root.select("Section:nth-of-type(2) :nth-last-of-type(+5)"), "6", "6", "6", "6");
  }

  @Test
  public void firstOfType() {
    check(root.select("Section:nth-of-type(2) :first-of-type"), "1", "1", "1", "1");
  }

  @Test
  public void lastOfType() {
    check(root.select("Section:nth-of-type(2) :last-of-type"), "10", "10", "10", "10");
  }

  @Test
  public void empty() {
    final Nodes<Structure> sel = root.select(":empty");
    assertEquals(2, sel.size());
    assertEquals("Header", sel.get(0).getTypeName());
    assertEquals("Paragraph", sel.get(1).getTypeName());
  }

  @Test
  public void onlyChild() {
    final Nodes<Structure> sel = root.select("Section :only-child");
    assertEquals(2, sel.size());
    assertEquals("Break", sel.get(0).getTypeName());
    assertEquals("Style", sel.get(1).getTypeName());

    check(root.select("Section :only-child"), "\n", "only");
  }

  @Test
  public void onlyOfType() {
    final Nodes<Structure> sel = root.select(":only-of-type");
    assertEquals(5, sel.size());
    assertEquals("Document", sel.get(0).getTypeName()); // TODO: should we have Document?
    assertEquals("Header", sel.get(1).getTypeName());
    assertEquals("Break", sel.get(2).getTypeName());
    assertEquals("Paragraph", sel.get(3).getTypeName());
    assertEquals("Style", sel.get(4).getTypeName());
  }

  protected void check(Nodes<Structure> result, String... expectedContent) {
    assertEquals("Number of elements", expectedContent.length, result.size());
    for (int i = 0; i < expectedContent.length; i++) {
      assertNotNull(result.get(i));
      assertEquals(
          "Expected element", expectedContent[i], result.get(i).getItem().getCoveredText());
    }
  }

  @Test
  public void root() {
    Nodes<Structure> sel = root.select(":root");
    assertEquals(1, sel.size());
    assertNotNull(sel.get(0));
  }
}
