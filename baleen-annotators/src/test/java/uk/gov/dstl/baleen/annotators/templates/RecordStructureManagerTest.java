// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.types.structure.Heading;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.SelectorPath;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;

public class RecordStructureManagerTest {

  protected static final String HEADING = "Test";

  protected static final String PARA1 = "The quick brown fox jumped over the lazy dog's back.";

  protected static final String PARA2 = "The quick brown cat jumped over the lazy dog's back.";

  protected static final String PARA3 = "The quick brown rat jumped over the lazy dog's back.";

  protected static final String PARA4 = "The quick brown ant jumped over the lazy dog's back.";

  protected static final String PARA5 = "The quick brown elk jumped over the lazy dog's back.";

  protected static final String TEXT =
      String.join("\n", HEADING, PARA1, PARA2, PARA3, PARA4, HEADING, PARA5);

  private RecordStructureManager recordStructureManager;

  private Paragraph paragraph1;

  private Paragraph paragraph2;

  private Paragraph paragraph3;

  private Paragraph paragraph4;

  private Paragraph paragraph5;

  private Document document;

  private Heading heading1;

  private Heading heading2;

  protected void addAnnotations(JCas jCas) {
    document = new Document(jCas);
    document.setDepth(1);
    document.setBegin(0);
    document.setEnd(TEXT.length());
    document.addToIndexes();

    int cursor = 0;

    heading1 = new Heading(jCas);
    heading1.setDepth(2);
    heading1.setBegin(cursor);
    cursor += HEADING.length();
    heading1.setEnd(cursor);
    heading1.addToIndexes();

    paragraph1 = new Paragraph(jCas);
    paragraph1.setDepth(2);
    paragraph1.setBegin(++cursor);
    cursor += PARA1.length();
    paragraph1.setEnd(cursor);
    paragraph1.addToIndexes();

    paragraph2 = new Paragraph(jCas);
    paragraph2.setDepth(2);
    paragraph2.setBegin(++cursor);
    cursor += PARA2.length();
    paragraph2.setEnd(cursor);
    paragraph2.addToIndexes();

    paragraph3 = new Paragraph(jCas);
    paragraph3.setDepth(2);
    paragraph3.setBegin(++cursor);
    cursor += PARA3.length();
    paragraph3.setEnd(cursor);
    paragraph3.addToIndexes();

    paragraph4 = new Paragraph(jCas);
    paragraph4.setDepth(2);
    paragraph4.setBegin(++cursor);
    cursor += PARA4.length();
    paragraph4.setEnd(cursor);
    paragraph4.addToIndexes();

    heading2 = new Heading(jCas);
    heading2.setDepth(2);
    heading2.setBegin(cursor);
    cursor += HEADING.length();
    heading2.setEnd(cursor);
    heading2.addToIndexes();

    paragraph5 = new Paragraph(jCas);
    paragraph5.setDepth(2);
    paragraph5.setBegin(++cursor);
    cursor += PARA5.length();
    paragraph5.setEnd(cursor);
    paragraph5.addToIndexes();
  }

  @Before
  public void setUp() throws Exception {
    JCas jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText(TEXT);
    addAnnotations(jCas);

    recordStructureManager =
        new RecordStructureManager(
            StructureHierarchy.build(jCas, StructureUtil.getStructureClasses()));
  }

  @Test
  public void testGetMissingStructure() throws InvalidParameterException {
    assertFalse(recordStructureManager.select("Document > Break").isPresent());
  }

  @Test
  public void testGetFirstParagraph() throws InvalidParameterException {
    Optional<Structure> structure =
        recordStructureManager.select("Document > Paragraph:nth-of-type(1)");
    assertTrue(structure.isPresent());
    assertEquals(paragraph1, structure.get());
  }

  @Test
  public void testCanNotRepeatMissingStructure() throws InvalidParameterException {
    SelectorPath minimal = SelectorPath.parse("Table");
    RepeatSearch repeatSearch = new RepeatSearch(ImmutableList.of(minimal), minimal);
    Optional<Structure> structure = recordStructureManager.select("Document > Heading");
    assertFalse(recordStructureManager.repeatRecord(structure, repeatSearch, true).isPresent());
  }

  @Test
  public void testCanRepeatParagraph() throws InvalidParameterException {
    SelectorPath minimal = SelectorPath.parse("Document > Paragraph:nth-of-type(1)");
    RepeatSearch repeatUnit = new RepeatSearch(ImmutableList.of(minimal), minimal);
    Optional<Structure> structure = recordStructureManager.select("Document > Heading");
    Optional<Structure> repeat1 = recordStructureManager.repeatRecord(structure, repeatUnit, true);
    assertTrue(repeat1.isPresent());
    assertEquals(paragraph1, repeat1.get());
    Optional<Structure> repeat2 = recordStructureManager.repeatRecord(repeat1, repeatUnit, false);
    assertTrue(repeat2.isPresent());
    assertEquals(paragraph2, repeat2.get());
    Optional<Structure> repeat3 = recordStructureManager.repeatRecord(repeat2, repeatUnit, false);
    assertTrue(repeat3.isPresent());
    assertEquals(paragraph3, repeat3.get());
    Optional<Structure> repeat4 = recordStructureManager.repeatRecord(repeat3, repeatUnit, false);
    assertTrue(repeat4.isPresent());
    assertEquals(paragraph4, repeat4.get());
    Optional<Structure> repeat5 = recordStructureManager.repeatRecord(repeat4, repeatUnit, false);
    assertFalse(repeat5.isPresent());
  }

  @Test
  public void testCanGetAfterRepeatParagraph() throws InvalidParameterException {
    SelectorPath minimal = SelectorPath.parse("Document > Paragraph:nth-of-type(1)");
    RepeatSearch repeatUnit = new RepeatSearch(ImmutableList.of(minimal), minimal);
    Optional<Structure> structure = recordStructureManager.select("Document > Heading");

    Optional<Structure> repeat1 = recordStructureManager.repeatRecord(structure, repeatUnit, true);
    Optional<Structure> repeat2 = recordStructureManager.repeatRecord(repeat1, repeatUnit, false);
    Optional<Structure> repeat3 = recordStructureManager.repeatRecord(repeat2, repeatUnit, false);
    recordStructureManager.repeatRecord(repeat3, repeatUnit, false);

    Optional<Structure> adjusted =
        recordStructureManager.select("Document > Paragraph:nth-of-type(2)");
    assertTrue(adjusted.isPresent());
    assertEquals(paragraph5, adjusted.get());
  }

  @Test
  public void testCanRepeatFromStartOfDocument() throws InvalidParameterException {
    SelectorPath maximal = SelectorPath.parse("Document > Heading");
    RepeatSearch repeatUnit = new RepeatSearch(ImmutableList.of(maximal), maximal);
    Optional<Structure> repeat1 =
        recordStructureManager.repeatRecord(Optional.empty(), repeatUnit, true);
    assertTrue(repeat1.isPresent());
    assertEquals(heading1, repeat1.get());
    Optional<Structure> repeat2 = recordStructureManager.repeatRecord(repeat1, repeatUnit, false);
    assertFalse(repeat2.isPresent());
  }

  @Test
  public void testCanGetAfterMissingRepeat() throws InvalidParameterException {
    SelectorPath minimal = SelectorPath.parse("Document > Paragraph");
    RepeatSearch repeatUnit = new RepeatSearch(ImmutableList.of(minimal), minimal);
    recordStructureManager.repeatRecord(Optional.empty(), repeatUnit, true);

    Optional<Structure> adjusted =
        recordStructureManager.select("Document > Paragraph:nth-of-type(2)");
    assertTrue(adjusted.isPresent());
    assertEquals(paragraph1, adjusted.get());
  }
}
