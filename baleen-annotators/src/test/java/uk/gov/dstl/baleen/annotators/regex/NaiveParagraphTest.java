package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.language.Paragraph;

public class NaiveParagraphTest extends AbstractAnnotatorTest {

  public NaiveParagraphTest() {
    super(NaiveParagraph.class);
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText("Hello\nWorld\n\nThis is the second paragraph!");
    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Paragraph.class).size());

    assertEquals("Hello\nWorld", JCasUtil.selectByIndex(jCas, Paragraph.class, 0).getCoveredText());
    assertEquals(
        "This is the second paragraph!",
        JCasUtil.selectByIndex(jCas, Paragraph.class, 1).getCoveredText());
  }
}
