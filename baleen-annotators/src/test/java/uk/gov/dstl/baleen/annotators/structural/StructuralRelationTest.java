// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.annotators.structural.StructuralRelation.PARAM_QUERY;
import static uk.gov.dstl.baleen.annotators.structural.StructuralRelation.PARAM_SOURCE_QUERY;
import static uk.gov.dstl.baleen.annotators.structural.StructuralRelation.PARAM_SUB_TYPE;
import static uk.gov.dstl.baleen.annotators.structural.StructuralRelation.PARAM_TARGET_QUERY;
import static uk.gov.dstl.baleen.annotators.structural.StructuralRelation.PARAM_TYPE;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Table;
import uk.gov.dstl.baleen.types.structure.TableBody;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.types.structure.TableHeader;
import uk.gov.dstl.baleen.types.structure.TableRow;

public class StructuralRelationTest extends AbstractAnnotatorTest {

  private static final String TH1 = "Name";
  private static final String TH2 = "eMail";
  private static final String R1C1 = "Stuart";
  private static final String R2C1 = "James";
  private static final String R3C1 = "Chris";
  private static final String R1C2 = "a@b.com";
  private static final String R2C2 = "";
  private static final String R3C2 = "d@e.com";
  private static final String HEADING = TH1 + " " + TH2;
  private static final String ROW1 = R1C1 + " " + R1C2;
  private static final String ROW2 = R2C1 + " " + R2C2;
  private static final String ROW3 = R3C1 + " " + R3C2;

  private static final String TEXT = String.join("\n", "", HEADING, ROW1, ROW2, ROW3, " other");

  public StructuralRelationTest() {
    super(StructuralRelation.class);
  }

  @Before
  public void setup() throws IOException {
    jCas.setDocumentText(TEXT);

    int cursor = 0;
    int depth = 0;
    Document document = new Document(jCas);
    document.setBegin(cursor);
    document.setDepth(depth);
    document.setEnd(TEXT.length());
    document.addToIndexes();

    Table table = new Table(jCas);
    table.setBegin(cursor);
    table.setDepth(depth);

    TableHeader th = new TableHeader(jCas);
    th.setBegin(cursor);
    th.setDepth(++depth);

    cursor = addRow(depth, cursor, TH1, TH2, false, false);

    th.setEnd(cursor);
    th.addToIndexes();
    --depth;

    TableBody tableBody = new TableBody(jCas);
    tableBody.setBegin(cursor);
    tableBody.setDepth(++depth);

    cursor = addRow(depth, cursor, R1C1, R1C2, true, true);
    cursor = addRow(depth, cursor, R2C1, R2C2, true, false);
    cursor = addRow(depth, cursor, R3C1, R3C2, true, true);

    tableBody.setEnd(cursor);
    tableBody.addToIndexes();
    --depth;

    table.setEnd(cursor);
    table.addToIndexes();
    --depth;
  }

  private int addRow(
      int depth, int cursor, String cell1, String cell2, boolean person, boolean comms) {
    TableRow tableRow = new TableRow(jCas);
    tableRow.setBegin(++cursor);
    tableRow.setDepth(++depth);

    TableCell c1 = new TableCell(jCas);
    c1.setBegin(cursor);
    c1.setDepth(++depth);

    Paragraph p1 = new Paragraph(jCas);
    p1.setBegin(cursor);
    p1.setDepth(++depth);

    if (person) {
      Person p = new Person(jCas);
      p.setBegin(cursor);
      p.setEnd(cursor + cell1.length());
      p.addToIndexes();
    }

    cursor += cell1.length();
    p1.setEnd(cursor);
    p1.addToIndexes();

    --depth;
    c1.setEnd(cursor);
    c1.addToIndexes();

    TableCell c2 = new TableCell(jCas);
    c2.setBegin(++cursor);
    c2.setDepth(depth);

    Paragraph p2 = new Paragraph(jCas);
    p2.setBegin(cursor);
    p2.setDepth(++depth);

    if (comms) {
      CommsIdentifier c = new CommsIdentifier(jCas);
      c.setBegin(cursor);
      c.setEnd(cursor + cell2.length());
      c.addToIndexes();
    }

    cursor += cell2.length();
    p2.setEnd(cursor);
    p2.addToIndexes();
    --depth;

    c2.setEnd(cursor);
    c2.addToIndexes();

    --depth;

    tableRow.setEnd(cursor);
    tableRow.addToIndexes();

    --depth;

    return cursor;
  }

  @Test
  public void testProcess() throws Exception {

    // Query could be less specific for this example, but just showing a
    // more realistic query.
    // eg "TableRow" or "TableRow:has(Person):has(CommsIdentifier)"

    processJCas(
        PARAM_QUERY,
        "Table:has(TableHeader:matches((?i)name):matches((?i)email)) TableRow:has(Person):has(CommsIdentifier)",
        PARAM_SOURCE_QUERY,
        "Person",
        PARAM_TARGET_QUERY,
        "CommsIdentifier",
        PARAM_TYPE,
        "emailAddress",
        PARAM_SUB_TYPE,
        "sub");

    Collection<Relation> relations = JCasUtil.select(jCas, Relation.class);
    assertEquals(2, relations.size());

    Iterator<Relation> iterator = relations.iterator();
    Relation first = iterator.next();
    assertEquals(HEADING.length() + 2, first.getBegin());
    assertEquals(HEADING.length() + 2 + ROW1.length(), first.getEnd());
    assertEquals(Person.class, first.getSource().getClass());
    assertEquals(R1C1, first.getSource().getCoveredText());
    assertEquals(CommsIdentifier.class, first.getTarget().getClass());
    assertEquals(R1C2, first.getTarget().getCoveredText());
    assertEquals("emailAddress", first.getRelationshipType());
    Relation second = iterator.next();
    int begin2 = HEADING.length() + ROW1.length() + ROW2.length() + 4;
    assertEquals(begin2, second.getBegin());
    assertEquals(begin2 + ROW3.length(), second.getEnd());
    assertEquals(Person.class, second.getSource().getClass());
    assertEquals(R3C1, second.getSource().getCoveredText());
    assertEquals(CommsIdentifier.class, second.getTarget().getClass());
    assertEquals(R3C2, second.getTarget().getCoveredText());
    assertEquals("emailAddress", second.getRelationshipType());
  }
}
