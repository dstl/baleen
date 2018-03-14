// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Section;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;

public class SelectRecordStructureManagerTest {

  protected static final String TEXT = "The quick brown fox jumped over the lazy dog's back.";

  private JCas jCas;

  private Set<Class<? extends Structure>> structuralClasses;

  @Before
  public void setUp() throws Exception {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText(TEXT);
    structuralClasses = StructureUtil.getStructureClasses();
  }

  @Test
  public void testSelectSimple() throws UIMAException, InvalidParameterException {
    Paragraph paragraph = new Paragraph(jCas);
    paragraph.setBegin(0);
    paragraph.setDepth(1);
    paragraph.setEnd(TEXT.length());
    paragraph.addToIndexes();
    RecordStructureManager manager =
        new RecordStructureManager(StructureHierarchy.build(jCas, structuralClasses));
    Optional<Structure> select = manager.select("Paragraph");
    assertTrue(select.isPresent());
    assertEquals(paragraph, select.get());
  }

  @Test
  public void testSelectNthTwo() throws InvalidParameterException {
    Paragraph paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(0);
    paragraph1.setDepth(1);
    paragraph1.setEnd(20);
    paragraph1.addToIndexes();

    Paragraph paragraph2 = new Paragraph(jCas);
    paragraph2.setBegin(20);
    paragraph2.setDepth(1);
    paragraph2.setEnd(TEXT.length());
    paragraph2.addToIndexes();

    RecordStructureManager manager =
        new RecordStructureManager(StructureHierarchy.build(jCas, structuralClasses));
    Optional<Structure> select1 = manager.select("Paragraph:nth-of-type(1)");

    assertTrue(select1.isPresent());
    assertEquals(paragraph1, select1.get());
    assertNotEquals(paragraph2, select1.get());

    Optional<Structure> select2 = manager.select("Paragraph:nth-of-type(2)");
    assertTrue(select2.isPresent());
    assertEquals(paragraph2, select2.get());
    assertNotEquals(paragraph1, select2.get());

    Optional<Structure> select3 = manager.select("Paragraph:nth-of-type(3)");
    assertFalse(select3.isPresent());
  }

  @Test
  public void testSelectNested() throws InvalidParameterException {
    Section section = new Section(jCas);
    section.setBegin(0);
    section.setDepth(1);
    section.setEnd(TEXT.length());
    section.addToIndexes();

    Paragraph paragraph = new Paragraph(jCas);
    paragraph.setBegin(0);
    paragraph.setDepth(2);
    paragraph.setEnd(TEXT.length());
    paragraph.addToIndexes();

    RecordStructureManager manager =
        new RecordStructureManager(StructureHierarchy.build(jCas, structuralClasses));

    Optional<Structure> selectNest1 = manager.select("Section > Paragraph:nth-of-type(1)");
    assertTrue(selectNest1.isPresent());
    assertEquals(paragraph, selectNest1.get());

    Optional<Structure> selectNest2 = manager.select("Section > Paragraph");
    assertTrue(selectNest2.isPresent());
    assertEquals(paragraph, selectNest2.get());

    Optional<Structure> selectNest3 = manager.select("Section:nth-of-type(1) > Paragraph");
    assertTrue(selectNest3.isPresent());
    assertEquals(paragraph, selectNest3.get());

    Optional<Structure> selectRoot = manager.select("Section");
    assertTrue(selectRoot.isPresent());
    assertEquals(section, selectRoot.get());
  }
}
