// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.events;

import java.net.URL;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;

public class SimpleOdinTest extends AbstractAnnotatorTest {

  private static final String RULES_NAME = "/master.yml";

  private static final URL RULES_FILE = OdinTest.class.getResource(RULES_NAME);

  public SimpleOdinTest() {
    super(Odin.class);
  }

  @Test(expected = ResourceInitializationException.class)
  public void checkThrowsIfIllegalType()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    processJCas(
        Odin.PARAM_TYPE_NAMES, new String[] {"Missing"}, Odin.PARAM_RULES, RULES_FILE.getFile());
  }

  @Test(expected = ResourceInitializationException.class)
  public void checkThrowsIfMissingFile()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    processJCas(Odin.PARAM_RULES, "MissingFile.yaml");
  }
}
