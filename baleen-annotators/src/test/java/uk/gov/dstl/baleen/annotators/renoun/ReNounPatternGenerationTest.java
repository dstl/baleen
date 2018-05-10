// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.resources.SharedFongoResource.PARAM_FONGO_COLLECTION;
import static uk.gov.dstl.baleen.resources.SharedFongoResource.PARAM_FONGO_DATA;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.ATTRIBUTE_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.OBJECT_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.PATTERN_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.SENTENCE_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.SUBJECT_FIELD;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;

public class ReNounPatternGenerationTest extends AbstractMultiAnnotatorTest {

  private static final String MONGO = "mongo";

  private static final String SENTENCE_1 =
      "The CEO of Google, Larry Page started his term in 2011. ";
  private static final String SENTENCE_2 = "The CEO of Google is Larry Page. ";
  private static final String SENTENCE_3 = "Larry Page, Google CEO, started his term in 2011. ";
  private static final String SENTENCE_4 = "Larry Page, Google's CEO started his term in 2011. ";
  private static final String SENTENCE_5 =
      "Larry Page, the CEO of Google started his term in 2011. ";
  private static final String SENTENCE_6 = "Google CEO Larry Page started his term in 2011. ";
  private static final String SENTENCE_7 = "Google CEO, Larry Page started his term in 2011. ";
  private static final String SENTENCE_8 = "Google's CEO, Larry Page started his term in 2011. ";

  private SharedFongoResource sfr;

  private MongoCollection<Document> output;

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

    ImmutableList<String> data =
        ImmutableList.of(
            new Document()
                .append("sourceValue", "Google")
                .append("value", "CEO")
                .append("targetValue", "Larry Page")
                .toJson(),
            new Document()
                .append("sourceValue", "Alphabet Limited")
                .append("value", "chief executive officer")
                .append("targetValue", "Larry Page")
                .toJson());

    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            MONGO,
            SharedFongoResource.class,
            PARAM_FONGO_COLLECTION,
            "renoun_facts",
            PARAM_FONGO_DATA,
            data.toString());

    // Create the analysis engine

    // Use OpenNlp to generate the POS etc for us
    final ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "tokens", SharedOpenNLPModel.class);
    final ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    final ExternalResourceDescription posDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "posTags", SharedOpenNLPModel.class);
    final ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    AnalysisEngine ae = createAnalysisEngine(ReNounPatternDataGenerator.class, MONGO, erd);
    try {
      sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);
      output = sfr.getDB().getCollection("renoun_patterns");
    } catch (ResourceAccessException e) {
      throw new ResourceInitializationException(e);
    }

    return asArray(
        createAnalysisEngine(
            OpenNLP.class,
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc),
        createAnalysisEngine(MaltParser.class),
        ae);
  }

  @Test
  public void testSeedPatterns()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(
        SENTENCE_1
            + SENTENCE_2
            + SENTENCE_3
            + SENTENCE_4
            + SENTENCE_5
            + SENTENCE_6
            + SENTENCE_7
            + SENTENCE_8);

    processJCas();

    MongoCursor<Document> found = output.find().iterator();

    assertTrue(found.hasNext());

    int count = 0;
    while (found.hasNext()) {
      count++;
      Document next = found.next();

      assertEquals("Google", next.get(SUBJECT_FIELD));
      assertEquals("CEO", next.get(ATTRIBUTE_FIELD));
      assertEquals("Larry Page", next.get(OBJECT_FIELD));
      assertNotNull(next.get(SENTENCE_FIELD));
      assertNotNull(next.get(PATTERN_FIELD));
    }

    assertEquals(8, count);
  }

  @Test
  public void testLongerAttributes()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    String initial =
        SENTENCE_1
            + SENTENCE_2
            + SENTENCE_3
            + SENTENCE_4
            + SENTENCE_5
            + SENTENCE_6
            + SENTENCE_7
            + SENTENCE_8;

    String extended =
        initial.replace("CEO", "chief executive officer").replace("Google", "Alphabet Limited");
    jCas.setDocumentText(extended);

    processJCas();

    MongoCursor<Document> found = output.find().iterator();

    assertTrue(found.hasNext());

    int count = 0;
    while (found.hasNext()) {
      count++;
      Document next = found.next();

      assertEquals("Alphabet Limited", next.get(SUBJECT_FIELD));
      assertEquals("chief executive officer", next.get(ATTRIBUTE_FIELD));
      assertEquals("Larry Page", next.get(OBJECT_FIELD));
      assertNotNull(next.get(SENTENCE_FIELD));
      assertNotNull(next.get(PATTERN_FIELD));
    }

    assertEquals(8, count);
  }
}
