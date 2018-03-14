// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Section;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;

public class SelectorPathStructureHierarchyTest {

  private static final String TEXT = "The quick brown fox jumped over the lazy dogs back";

  private static Set<Class<? extends Structure>> structuralClasses;

  @BeforeClass
  public static void initClasses() throws ResourceInitializationException {
    structuralClasses = StructureUtil.getStructureClasses();
  }

  private JCas jCas;

  @Before
  public void setup() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText(TEXT);
  }

  @Test
  public void testGenerateSimple() {
    Paragraph paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(0);
    paragraph1.setDepth(1);
    paragraph1.setEnd(20);
    paragraph1.addToIndexes();

    ItemHierarchy<Structure> structureHierarchy = StructureHierarchy.build(jCas, structuralClasses);
    SelectorPath path = structureHierarchy.getSelectorPath(paragraph1);

    assertEquals("Paragraph:nth-of-type(1)", path.toString());
  }

  @Test
  public void testGenerateTwo() {
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

    ItemHierarchy<Structure> structureHierarchy = StructureHierarchy.build(jCas, structuralClasses);
    SelectorPath path1 = structureHierarchy.getSelectorPath(paragraph1);
    SelectorPath path2 = structureHierarchy.getSelectorPath(paragraph2);

    assertEquals("Paragraph:nth-of-type(1)", path1.toString());
    assertEquals("Paragraph:nth-of-type(2)", path2.toString());
  }

  @Test
  public void testGenerateNested() {
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

    ItemHierarchy<Structure> structureHierarchy = StructureHierarchy.build(jCas, structuralClasses);
    SelectorPath path1 = structureHierarchy.getSelectorPath(paragraph);

    assertEquals("Section:nth-of-type(1) > Paragraph:nth-of-type(1)", path1.toString());
  }

  @Test
  public void testGenerateNested2() {
    Section section = new Section(jCas);
    section.setBegin(0);
    section.setDepth(1);
    section.setEnd(TEXT.length());
    section.addToIndexes();

    Paragraph paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(0);
    paragraph1.setDepth(2);
    paragraph1.setEnd(20);
    paragraph1.addToIndexes();

    Paragraph paragraph2 = new Paragraph(jCas);
    paragraph2.setBegin(20);
    paragraph2.setDepth(2);
    paragraph2.setEnd(TEXT.length());
    paragraph2.addToIndexes();

    ItemHierarchy<Structure> structureHierarchy = StructureHierarchy.build(jCas, structuralClasses);
    SelectorPath path1 = structureHierarchy.getSelectorPath(paragraph1);
    SelectorPath path2 = structureHierarchy.getSelectorPath(paragraph2);

    assertEquals("Section:nth-of-type(1) > Paragraph:nth-of-type(1)", path1.toString());
    assertEquals("Section:nth-of-type(1) > Paragraph:nth-of-type(2)", path2.toString());
  }

  @Test
  public void testGenerateNestedToDepth1() {
    Section section = new Section(jCas);
    section.setBegin(0);
    section.setDepth(1);
    section.setEnd(TEXT.length());
    section.addToIndexes();

    Paragraph paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(0);
    paragraph1.setDepth(2);
    paragraph1.setEnd(20);
    paragraph1.addToIndexes();

    Paragraph paragraph2 = new Paragraph(jCas);
    paragraph2.setBegin(20);
    paragraph2.setDepth(2);
    paragraph2.setEnd(TEXT.length());
    paragraph2.addToIndexes();

    ItemHierarchy<Structure> structureHierarchy = StructureHierarchy.build(jCas, structuralClasses);
    SelectorPath path1 = structureHierarchy.getSelectorPath(paragraph1);
    SelectorPath path2 = structureHierarchy.getSelectorPath(paragraph1);

    assertEquals("Section:nth-of-type(1)", path1.toDepth(1).toString());
    assertEquals("Section:nth-of-type(1)", path2.toDepth(1).toString());
  }

  @Test
  public void testGenerateInterupted() {
    Section section = new Section(jCas);
    section.setBegin(0);
    section.setDepth(1);
    section.setEnd(TEXT.length());
    section.addToIndexes();

    Paragraph paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(0);
    paragraph1.setDepth(2);
    paragraph1.setEnd(20);
    paragraph1.addToIndexes();

    Section subSection = new Section(jCas);
    subSection.setBegin(21);
    subSection.setDepth(2);
    subSection.setEnd(22);
    subSection.addToIndexes();

    Paragraph paragraph2 = new Paragraph(jCas);
    paragraph2.setBegin(23);
    paragraph2.setDepth(2);
    paragraph2.setEnd(TEXT.length());
    paragraph2.addToIndexes();

    ItemHierarchy<Structure> structureHierarchy = StructureHierarchy.build(jCas, structuralClasses);
    SelectorPath path1 = structureHierarchy.getSelectorPath(paragraph1);
    assertEquals("Section:nth-of-type(1) > Paragraph:nth-of-type(1)", path1.toString());

    SelectorPath path2 = structureHierarchy.getSelectorPath(paragraph2);
    assertEquals("Section:nth-of-type(1) > Paragraph:nth-of-type(2)", path2.toString());
  }
}
