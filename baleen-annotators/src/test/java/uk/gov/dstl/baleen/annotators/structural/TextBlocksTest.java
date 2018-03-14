// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.structure.Footer;
import uk.gov.dstl.baleen.types.structure.Header;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Quotation;

public class TextBlocksTest extends AbstractAnnotatorTest {

  public TextBlocksTest() {
    super(TextBlocks.class);
  }

  @Before
  public void before() {
    jCas.setDocumentText("This is a header. This is a paragraph. This is a footer.");
  }

  private void addStructure() {
    final Header header = new Header(jCas, 0, 17);
    header.addToIndexes();
    final Paragraph paragraph = new Paragraph(jCas, 18, 38);
    paragraph.addToIndexes();
    final Footer footer = new Footer(jCas, 40, jCas.getDocumentText().length());
    footer.addToIndexes();
  }

  @Test
  public void testWithoutStructuralAnnotations()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    processJCas();

    final List<Text> list = new ArrayList<>(JCasUtil.select(jCas, Text.class));
    assertEquals(1, list.size());
    assertEquals(jCas.getDocumentText(), list.get(0).getCoveredText());
  }

  @Test
  public void testWithStructuralAnnotations()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    addStructure();

    processJCas();

    final List<Text> list = new ArrayList<>(JCasUtil.select(jCas, Text.class));
    assertEquals(1, list.size());
    assertEquals("This is a paragraph.", list.get(0).getCoveredText());
  }

  @Test
  public void testWithCustomTypes()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    addStructure();

    processJCas(TextBlocks.PARAM_TYPE_NAMES, new String[] {"Header"});
    final List<Text> list = new ArrayList<>(JCasUtil.select(jCas, Text.class));

    assertEquals(1, list.size());
    assertEquals("This is a header.", list.get(0).getCoveredText());
  }

  @Test
  public void testWithTwoTypes()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    addStructure();

    processJCas(TextBlocks.PARAM_TYPE_NAMES, new String[] {"Header", "Paragraph"});
    final List<Text> list = new ArrayList<>(JCasUtil.select(jCas, Text.class));

    assertEquals(2, list.size());
    assertEquals("This is a header.", list.get(0).getCoveredText());
    assertEquals("This is a paragraph.", list.get(1).getCoveredText());
  }

  @Test
  public void testKeepBiggest()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    addStructure();

    final Quotation q = new Quotation(jCas, 29, 38);
    q.addToIndexes();

    processJCas(TextBlocks.PARAM_KEEP_SMALLEST, false);
    final List<Text> list = new ArrayList<>(JCasUtil.select(jCas, Text.class));

    assertEquals(1, list.size());
    assertEquals("This is a paragraph.", list.get(0).getCoveredText());
  }

  @Test
  public void testKeepSmallest()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    addStructure();
    final Quotation q = new Quotation(jCas, 28, 38);
    q.addToIndexes();

    processJCas(TextBlocks.PARAM_KEEP_SMALLEST, true);
    final List<Text> list = new ArrayList<>(JCasUtil.select(jCas, Text.class));

    assertEquals(1, list.size());
    assertEquals("paragraph.", list.get(0).getCoveredText());
  }
}
