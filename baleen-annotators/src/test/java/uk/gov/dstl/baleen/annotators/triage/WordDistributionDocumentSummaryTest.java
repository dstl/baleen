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
      "Construction giant Carillion has gone into liquidation, threatening thousands of jobs. ";
  private static final String SECOND_SENTENCE =
      "The move came after talks between the firm, its lenders and the government failed to reach a deal to save the UK's second biggest construction company. ";
  private static final String THIRD_SENTENCE =
      "Carillion ran into trouble after losing money on big contracts and running up huge debts. ";
  private static final String FOURTH_SENTENCE =
      "Its failure means the government will have to provide funding to maintain the private services run by Carillion. ";
  private static final String FIFTH_SENTENCE =
      "\"All employees should keep coming to work, you will continue to get paid. ";
  private static final String SIXTH_SENTENCE =
      "Staff that are engaged on private sector contracts still have important work to do,\" said government minister David Lidington. ";
  private static final String SEVENTH_SENTENCE =
      "Carillion is involved in major projects such as the HS2 high-speed rail line, as well as managing schools and prisons. ";
  private static final String EIGHTH_SENTENCE =
      "It is the second biggest supplier of maintenance services to Network Rail, and it maintains 50,000 homes for the Ministry of Defence. ";
  private static final String NINTH_SENTENCE =
      "Carillion chairman Philip Green said it was a \"very sad day\" for the company's workers, suppliers and customers. ";
  private static final String TENTH_SENTENCE =
      "The company has 43,000 staff worldwide - 20,000 in the UK. ";
  private static final String ELEVENTH_SENTENCE =
      "There are also thousands of small firms that carry out work on Carillion's behalf - many of those have contacted the BBC with concerns about whether they will be paid. ";
  private static final String TWELFTH_SENTENCE =
      "One company, which provided services for Carillion's prisons contract, told the BBC that it might fail if it is not paid the £80,000 owed to it. ";
  private static final String THIRTEENTH_SENTENCE =
      "What happens next depends on the actions of a court-appointed official receiver. \n With the help of a team of experts from accountants PwC, the receiver will review Carillion's business - a process which could take months. ";
  private static final String FOURTEENTH_SENTENCE =
      "With the help of a team of experts from accountants PwC, the receiver will review Carillion's business - a process which could take months. ";
  private static final String FIFTEENTH_SENTENCE =
      "The government has already said it is supporting public services and other firms are likely to take on some of Carillion's other contracts and staff. ";

  private static final String DOCUMENT =
      FIRST_SENTENCE
          + SECOND_SENTENCE
          + THIRD_SENTENCE
          + FOURTH_SENTENCE
          + FIFTH_SENTENCE
          + SIXTH_SENTENCE
          + SEVENTH_SENTENCE
          + EIGHTH_SENTENCE
          + NINTH_SENTENCE
          + TENTH_SENTENCE
          + ELEVENTH_SENTENCE
          + TWELFTH_SENTENCE
          + THIRTEENTH_SENTENCE
          + FOURTEENTH_SENTENCE
          + FIFTEENTH_SENTENCE;

  private JCasMetadata metadata;

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
        FOURTEENTH_SENTENCE + "\n" + FOURTH_SENTENCE + "\n" + EIGHTH_SENTENCE + "\n";

    assertEquals(
        "The metadata key should be the summary made up of top scoring sentences",
        expectedSummary,
        metadata.find(METADATA_KEY).get());
  }
}
