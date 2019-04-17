// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.annotators.triage.WordDistributionDocumentSummary.DESIRED_SUMMARY_CHARACTER_COUNT;
import static uk.gov.dstl.baleen.annotators.triage.WordDistributionDocumentSummary.FREQUENCY_THRESHOLD;
import static uk.gov.dstl.baleen.annotators.triage.WordDistributionDocumentSummary.METADATA_KEY;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

public class WordDistributionDocumentSummaryTest extends AbstractMultiAnnotatorTest {

  private static final String FIRST_SENTENCE =
      "Baleen is an extensible text processing capability that allows entity-related information to be extracted from unstructured and semi-structured data sources. ";
  private static final String SECOND_SENTENCE =
      "It makes available in a structured format things of interest otherwise stored in formats such as text documents - references to people, organisations, unique identifiers, location information.";
  private static final String THIRD_SENTENCE =
      "Baleen is written in Java 8 using the software project management tool Maven 3 and draws heavily on the Apache Unstructured Information Management Architecture (UIMA) which provides a framework, components and infrastructure to handle unstructured information management. ";
  private static final String FOURTH_SENTENCE =
      "Baleen was written by the Defence Science and Technology Laboratory (Dstl) in support of UK Defence users looking to extract entities and search unstructured text documents. ";
  private static final String SIXTH_SENTENCE =
      "License information can be found in the accompanying `LICENSE` file in this repository and the licenses of libraries on which Baleen is dependent are listed in the file `THIRD-PARTY`. ";
  private static final String SEVENTH_SENTENCE =
      "Baleen is still under active development, and is released here not as a final product but as a work in progress. ";
  private static final String EIGHTH_SENTENCE =
      "As such, there may be bugs, issues, typos, mistakes in the documentation, and more. ";
  private static final String NINTH_SENTENCE =
      "We hope that contributions from other users will improve Baleen and result in a better framework for others to use. ";

  private static final String DOCUMENT =
      FIRST_SENTENCE
          + SECOND_SENTENCE
          + THIRD_SENTENCE
          + FOURTH_SENTENCE
          + FOURTH_SENTENCE
          + SIXTH_SENTENCE
          + SEVENTH_SENTENCE
          + EIGHTH_SENTENCE
          + NINTH_SENTENCE;

  private JCasMetadata metadata;

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

    ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createNamedResourceDescription("tokens", SharedOpenNLPModel.class);
    ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createNamedResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    ExternalResourceDescription posDesc =
        ExternalResourceFactory.createNamedResourceDescription("posTags", SharedOpenNLPModel.class);
    ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createNamedResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    AnalysisEngineDescription documentSummaryAnalysisEngineDescription =
        AnalysisEngineFactory.createEngineDescription(
            WordDistributionDocumentSummary.class,
            DESIRED_SUMMARY_CHARACTER_COUNT,
            300,
            FREQUENCY_THRESHOLD,
            1);

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

    AnalysisEngine openNlpAnalysisEngine =
        AnalysisEngineFactory.createEngine(openNlpAnalysisEngineDescription);

    AnalysisEngine documentSummaryAnalysisEngine =
        AnalysisEngineFactory.createEngine(documentSummaryAnalysisEngineDescription);

    return new AnalysisEngine[] {openNlpAnalysisEngine, documentSummaryAnalysisEngine};
  }

  @Before
  public void setup() throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(DOCUMENT);
    processJCas();
    metadata = new JCasMetadata(jCas);
  }

  @Test
  public void testThereIsOneMetaDataAnnotation() {

    assertEquals(
        "Should be 1 metadata annotation", 1, JCasUtil.select(jCas, Metadata.class).size());
  }

  @Test
  public void testMetadataValue() {

    final String expectedSummary =
        EIGHTH_SENTENCE + "\n" + FOURTH_SENTENCE + "\n" + SIXTH_SENTENCE + "\n";

    assertEquals(
        "The metadata key should be the summary made up of top scoring sentences",
        expectedSummary,
        metadata.find(METADATA_KEY).get());
  }
}
