// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.print;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;

public class EntityGraphPrintTest extends AnnotatorTestBase {

  // NOTE: These don't actually check anything is written, just no null
  // pointers etc

  @Test
  public void testGraph() throws UIMAException {

    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngine(EntityGraph.class));
  }
}
