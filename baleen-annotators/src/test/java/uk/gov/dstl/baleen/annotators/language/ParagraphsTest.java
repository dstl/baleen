// Dstl (c) Crown Copyright 2018
package uk.gov.dstl.baleen.annotators.language;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.language.Paragraph;

public class ParagraphsTest extends AbstractAnnotatorTest {

  public ParagraphsTest() {
    super(Paragraphs.class);
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText(
        "This is a paragraph.\n\n\n\nSo is this\r\nWhat about windows word endings.");
    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Paragraph.class).size());
    assertEquals(
        "This is a paragraph.", JCasUtil.selectByIndex(jCas, Paragraph.class, 0).getCoveredText());
    assertEquals("So is this", JCasUtil.selectByIndex(jCas, Paragraph.class, 1).getCoveredText());
    assertEquals(
        "What about windows word endings.",
        JCasUtil.selectByIndex(jCas, Paragraph.class, 2).getCoveredText());
  }
}
