// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyResourceAnnotator extends JCasAnnotator_ImplBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(DummyResourceAnnotator.class);

  @ExternalResource(key = "Test")
  private DummyResource test;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    if (test == null || test.getValue() != DummyResource.EXPECTED_VALUE) {
      throw new ResourceInitializationException();
    }
    LOGGER.info("Dummy Resource Annotator initialized");
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Do nothing
  }
}
