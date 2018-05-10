// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.print;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;
import uk.gov.dstl.baleen.rdf.RdfFormat;

public class RdfPrintTest extends AnnotatorTestBase {

  // NOTE: These don't actually check anything is written, just no null
  // pointers etc

  @Test
  public void testRdfXml() throws UIMAException {
    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngine(Rdf.class));
  }

  @Test
  public void testRdfXmlAbv() throws UIMAException {
    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(
        jCas,
        AnalysisEngineFactory.createEngine(
            Rdf.class, Rdf.PARAM_OUTPUT_FORMAT, RdfFormat.RDF_XML_ABBREV.toString()));
  }

  @Test
  public void testJsonLD() throws UIMAException {
    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(
        jCas,
        AnalysisEngineFactory.createEngine(
            Rdf.class, Rdf.PARAM_OUTPUT_FORMAT, RdfFormat.JSONLD.toString()));
  }

  @Test
  public void testN3() throws UIMAException {
    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(
        jCas,
        AnalysisEngineFactory.createEngine(
            Rdf.class, Rdf.PARAM_OUTPUT_FORMAT, RdfFormat.N3.toString()));
  }

  @Test
  public void testNTriples() throws UIMAException {
    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(
        jCas,
        AnalysisEngineFactory.createEngine(
            Rdf.class, Rdf.PARAM_OUTPUT_FORMAT, RdfFormat.N_TRIPLES.toString()));
  }

  @Test
  public void testRdfJson() throws UIMAException {
    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(
        jCas,
        AnalysisEngineFactory.createEngine(
            Rdf.class, Rdf.PARAM_OUTPUT_FORMAT, RdfFormat.RDF_JSON.toString()));
  }

  @Test
  public void testTurtle() throws UIMAException {
    JCasTestGraphUtil.populateJcas(jCas);
    SimplePipeline.runPipeline(
        jCas,
        AnalysisEngineFactory.createEngine(
            Rdf.class, Rdf.PARAM_OUTPUT_FORMAT, RdfFormat.TURTLE.toString()));
  }
}
