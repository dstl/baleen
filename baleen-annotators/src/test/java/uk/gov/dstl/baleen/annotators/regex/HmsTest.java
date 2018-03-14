// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;

/** Test {@link uk.gov.dstl.baleen.annotators.regex.Callsign}. */
public class HmsTest extends AbstractAnnotatorTest {
  private AnalysisEngine languageAE;

  public HmsTest() {
    super(Hms.class);
  }

  @Before
  public void before() throws UIMAException {
    ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "tokens", SharedOpenNLPModel.class);
    ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    ExternalResourceDescription posDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "posTags", SharedOpenNLPModel.class);
    ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    AnalysisEngineDescription desc =
        AnalysisEngineFactory.createEngineDescription(
            OpenNLP.class,
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc);

    languageAE = AnalysisEngineFactory.createEngine(desc);
  }

  @Test
  public void testHms() throws Exception {
    jCas.setDocumentText(
        "HMS Troutbridge sailed into New York last Friday. H.M.S. Hidden Dragon provided an escort.");
    process();

    assertAnnotations(
        2,
        MilitaryPlatform.class,
        new TestEntity<>(0, "HMS Troutbridge", "HMS Troutbridge"),
        new TestEntity<>(1, "H.M.S. Hidden Dragon", "H.M.S. Hidden Dragon"));
  }

  @Test
  public void testHmsVariants() throws Exception {
    jCas.setDocumentText(
        "HMJS Troutbridge sailed into New York last Friday. H.M.P.N.G.S. Hidden Dragon provided an escort.");
    process();

    assertAnnotations(
        2,
        MilitaryPlatform.class,
        new TestEntity<>(0, "HMJS Troutbridge", "HMJS Troutbridge"),
        new TestEntity<>(1, "H.M.P.N.G.S. Hidden Dragon", "H.M.P.N.G.S. Hidden Dragon"));
  }

  private void process() throws UIMAException {
    languageAE.process(jCas);
    processJCas();
  }
}
