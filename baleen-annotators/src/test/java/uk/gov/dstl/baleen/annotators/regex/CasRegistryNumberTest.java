// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Chemical;

public class CasRegistryNumberTest extends AbstractAnnotatorTest {
  public CasRegistryNumberTest() {
    super(CasRegistryNumber.class);
  }

  @Test
  public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(
        "The CAS Number for water is 7732-18-5, but carbon could be either CASRN:7440-44-0 or CAS Registry Number 7782-42-5. CAS Number 7440-44-5 is not valid.");

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Chemical.class).size());
    assertEquals("7732-18-5", JCasUtil.selectByIndex(jCas, Chemical.class, 0).getCoveredText());
    assertEquals("7440-44-0", JCasUtil.selectByIndex(jCas, Chemical.class, 1).getCoveredText());
    assertEquals("7782-42-5", JCasUtil.selectByIndex(jCas, Chemical.class, 2).getCoveredText());
  }
}
