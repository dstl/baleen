// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

public class ShannonEntropyAnnotatorTest extends AbstractMultiAnnotatorTest {

  private static final String DOCUMENT =
      "This is a test document. It is made up of a couple of " + "sentences";

  private JCasMetadata jCasMetadata;

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

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

    AnalysisEngineDescription openNlpAnalysisEngineDescription =
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

    AnalysisEngineDescription shannonEntropyAnalysisEngineDescription =
        AnalysisEngineFactory.createEngineDescription(ShannonEntropyAnnotator.class);

    AnalysisEngine openNlpAnalysisEngine =
        AnalysisEngineFactory.createEngine(openNlpAnalysisEngineDescription);
    AnalysisEngine shannonEntropyAnalysisEngine =
        AnalysisEngineFactory.createEngine(shannonEntropyAnalysisEngineDescription);

    return new AnalysisEngine[] {openNlpAnalysisEngine, shannonEntropyAnalysisEngine};
  }

  @Before
  public void setup() throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(DOCUMENT);
    processJCas();

    jCasMetadata = new JCasMetadata(jCas);
  }

  @Test
  public void testWordBasedShannonEntropy() {

    assertEquals(
        "Word based shannon entropy should be 3.506890595608519",
        "3.506890595608519",
        jCasMetadata.find(ShannonEntropyAnnotator.METADATA_WORD_BASED_ENTROPY_KEY).get());
  }

  @Test
  public void testCharacterBasedShannonEntropy() {

    assertEquals(
        "Character based shannon entropy should be 3.912949440895038",
        "3.912949440895038",
        jCasMetadata.find(ShannonEntropyAnnotator.METADATA_CHARACTER_BASED_ENTROPY_KEY).get());
  }
}
