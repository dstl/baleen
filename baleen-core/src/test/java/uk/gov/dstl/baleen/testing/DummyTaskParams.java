// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

public class DummyTaskParams extends JCasAnnotator_ImplBase {

  public static final String PARAM1 = "key";

  @ConfigurationParameter(name = PARAM1, defaultValue = "default")
  private String value;

  public String getValue() {
    return value;
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Do nothing
  }
}
