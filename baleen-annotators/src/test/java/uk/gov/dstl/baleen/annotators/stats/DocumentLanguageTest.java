// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.stats;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;

/** */
public class DocumentLanguageTest extends AbstractAnnotatorTest {

  public DocumentLanguageTest() {
    super(DocumentLanguage.class);
  }

  @Test
  public void testEN() throws Exception {
    jCas.setDocumentText(
        "Hello, my name is Andrew. I come from London, but I live in Salisbury. I studied Physics at university, and I play the trumpet.");

    int i = 0;
    while (i < 5
        && !"en".equals(jCas.getDocumentLanguage())) { // Loop because on short pieces of text the
      // DocumentLanguage sampling can give x-unspecified.
      processJCas();
    }

    assertEquals("en", jCas.getDocumentLanguage());
  }

  @Test
  public void testDE() throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(
        "Hallo, mein Name ist Andrew. Ich komme aus London, aber ich lebe in Salisbury. Ich studierte Physik an der Universität, und ich spiele die Trompete.");

    int i = 0;
    while (i < 5
        && !"de".equals(jCas.getDocumentLanguage())) { // Loop because on short pieces of text the
      // DocumentLanguage sampling can give x-unspecified.
      processJCas();
    }

    assertEquals("de", jCas.getDocumentLanguage());
  }

  @Test
  public void testNoneSuch()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    // not any language...
    jCas.setDocumentText("sdrwkcb s't dn, slwv ylrtn s nctns sht.");
    processJCas();

    assertEquals("x-unspecified", jCas.getDocumentLanguage());
  }
}
