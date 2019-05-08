// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.renoun;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.jobs.renoun.ReNounScoring.BINARY_DICTIONARY_FILENAME;
import static uk.gov.dstl.baleen.jobs.renoun.ReNounScoring.BINARY_VECTORS_FILENAME;
import static uk.gov.dstl.baleen.jobs.renoun.ReNounScoring.PARAM_BIN_FOLDER;
import static uk.gov.dstl.baleen.jobs.renoun.ReNounScoring.PARAM_FACT_COLLECTION;
import static uk.gov.dstl.baleen.jobs.renoun.ReNounScoring.PARAM_MODEL_PATH;
import static uk.gov.dstl.baleen.jobs.renoun.ReNounScoring.PARAM_PATTERN_SCORE_COLLECTION;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.COHERENCE_KEY;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.FREQUENCY_KEY;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.ATTRIBUTE_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.OBJECT_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.PATTERN_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.SCORE_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.SUBJECT_FIELD;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.uima.AbstractBaleenTaskTest;

public class ReNounScoringTest extends AbstractBaleenTaskTest {

  private static final URL MODEL_URL = ReNounScoringTest.class.getResource("test-model.txt");

  // @formatter:off
  private static final String PATTERN_1 =
      "_NN:attribute\n" + "  name _NN:subject\n" + "  punct ,_,\n" + "  appos _NN:object\n";

  private static final String PATTERN_2 =
      "_NN:object\n" + "  punct ,_,\n" + "  appos _NN:subject\n" + "  list _NN:attribute\n";

  private static final String PATTERN_3 =
      "_VB\n"
          + "  nsubj _NN:object\n"
          + "      compound _NN:subject\n"
          + "      compound _NN:attribute\n";

  // @formatter:on

  private static final String RENOUN_FACTS = "renoun_facts";
  private static final String RENOUN_SCORED_FACTS = "renoun_fact_scores";
  private static final String RENOUN_PATTERNS = "renoun_patterns";
  private ExternalResourceDescription fongoErd;
  private MongoCollection<Document> patternsCollection;
  private MongoCollection<Document> scoredFactsCollection;

  private Path tempDirectory;

  @Before
  public void before()
      throws URISyntaxException, ResourceInitializationException, AnalysisEngineProcessException,
          ResourceAccessException, IOException {

    ImmutableList<String> data =
        ImmutableList.of(
            new Document()
                .append(PATTERN_FIELD, PATTERN_1)
                .append(SUBJECT_FIELD, "Google")
                .append(ATTRIBUTE_FIELD, "second")
                .append(OBJECT_FIELD, "Larry Page")
                .toJson(),
            new Document()
                .append(PATTERN_FIELD, PATTERN_1)
                .append(SUBJECT_FIELD, "Microsoft")
                .append(ATTRIBUTE_FIELD, "first")
                .append(OBJECT_FIELD, "Bill Gates")
                .toJson(),
            new Document()
                .append(PATTERN_FIELD, PATTERN_1)
                .append(SUBJECT_FIELD, "Microsoft")
                .append(ATTRIBUTE_FIELD, "third")
                .append(OBJECT_FIELD, "Bill Gates")
                .toJson(),
            new Document()
                .append(PATTERN_FIELD, PATTERN_2)
                .append(SUBJECT_FIELD, "Microsoft")
                .append(ATTRIBUTE_FIELD, "CEO")
                .append(OBJECT_FIELD, "Bill Gates")
                .toJson(),
            new Document()
                .append(PATTERN_FIELD, PATTERN_3)
                .append(SUBJECT_FIELD, "Microsoft")
                .append(ATTRIBUTE_FIELD, "CEO")
                .append(OBJECT_FIELD, "Bill Gates")
                .toJson(),
            new Document()
                .append(PATTERN_FIELD, PATTERN_3)
                .append(SUBJECT_FIELD, "Microsoft")
                .append(ATTRIBUTE_FIELD, "not in model")
                .append(OBJECT_FIELD, "Bill Gates")
                .toJson());

    fongoErd =
        ExternalResourceFactory.createNamedResourceDescription(
            SharedMongoResource.RESOURCE_KEY,
            SharedFongoResource.class,
            "fongo.collection",
            RENOUN_FACTS,
            "fongo.data",
            data.toString());

    tempDirectory = Files.createTempDirectory("binFolder");

    final AnalysisEngine ae =
        create(
            ReNounScoring.class,
            SharedMongoResource.RESOURCE_KEY,
            fongoErd,
            PARAM_FACT_COLLECTION,
            "renoun_facts",
            PARAM_PATTERN_SCORE_COLLECTION,
            RENOUN_PATTERNS,
            PARAM_MODEL_PATH,
            Paths.get(MODEL_URL.toURI()).toString(),
            PARAM_BIN_FOLDER,
            tempDirectory.toString());

    execute(ae);

    SharedFongoResource sfr =
        (SharedFongoResource)
            ae.getUimaContext().getResourceObject(SharedMongoResource.RESOURCE_KEY);
    patternsCollection = sfr.getDB().getCollection(RENOUN_PATTERNS);
    scoredFactsCollection = sfr.getDB().getCollection(RENOUN_SCORED_FACTS);
  }

  @Test
  public void testModelPathContainsBinaries() {
    assertTrue(new File(tempDirectory.toFile(), BINARY_VECTORS_FILENAME).exists());
    assertTrue(new File(tempDirectory.toFile(), BINARY_DICTIONARY_FILENAME).exists());
  }

  @Test
  public void testScoredDocumentsArePersistedToMongo() throws UIMAException, IOException {

    MongoCursor<Document> scoredDocumentsIterator = patternsCollection.find().iterator();

    assertTrue(scoredDocumentsIterator.hasNext());
  }

  @Test
  public void testFoundPatternContainsPattern() {

    Document scoredPattern1 = findPatternScore(PATTERN_1);
    Document scoredPattern2 = findPatternScore(PATTERN_2);
    Document scoredPattern3 = findPatternScore(PATTERN_3);

    assertEquals(
        "First scored document's pattern should be pattern1",
        PATTERN_1,
        scoredPattern1.get(PATTERN_FIELD));

    assertEquals(
        "Second scored document's pattern should be pattern2",
        PATTERN_2,
        scoredPattern2.get(PATTERN_FIELD));

    assertEquals(
        "Third scored document's pattern should be pattern2",
        PATTERN_3,
        scoredPattern3.get(PATTERN_FIELD));
  }

  @Test
  public void testPatternFrequencies() {

    assertEquals(
        "First scored pattern's frequency should be 3",
        Integer.valueOf(3),
        findPatternFrequency(PATTERN_1));

    assertEquals(
        "Second scored document's frequency should be 1",
        Integer.valueOf(1),
        findPatternFrequency(PATTERN_2));

    assertEquals(
        "Third scored document's frequency should be 2",
        Integer.valueOf(2),
        findPatternFrequency(PATTERN_3));
  }

  @Test
  public void testPatternCoherence() {

    assertEquals(
        "First scored pattern's coherence should be 0.999986426588715...",
        0.999986426588715,
        findPatternCoherence(PATTERN_1),
        0.0001);

    assertEquals(
        "Second scored pattern's coherence should be 1.0",
        1.0,
        findPatternCoherence(PATTERN_2),
        0.0);

    assertEquals(
        "Third scored pattern's coherence should be 0.0",
        0.0,
        findPatternCoherence(PATTERN_3),
        0.0);
  }

  @Test
  public void testScoredFactsArePersistedToMongo() {
    MongoCursor<Document> scoredFacts = scoredFactsCollection.find().iterator();
    assertTrue(scoredFacts.hasNext());
  }

  @Test
  public void testScoredFactsContainFacts() {
    Document scoredDocument = scoredFactsCollection.find().iterator().next();

    assertNotNull(scoredDocument.getString(ATTRIBUTE_FIELD));
    assertNotNull(scoredDocument.getString(OBJECT_FIELD));
    assertNotNull(scoredDocument.getString(SUBJECT_FIELD));
  }

  @Test
  public void scoredFactsShouldHaveACalculatedScore() {

    assertEquals(
        "First scored fact should have a calculated score of 2.999959279766145",
        2.999959279766145,
        findFactScore("Google", "second", "Larry Page"),
        0.0001);

    assertEquals(
        "Second scored fact should have a calculated score of 2.999959279766145",
        2.999959279766145,
        findFactScore("Microsoft", "first", "Bill Gates"),
        0.0001);

    assertEquals(
        "Third scored fact should have a calculated score of 2.999959279766145",
        2.999959279766145,
        findFactScore("Microsoft", "third", "Bill Gates"),
        0.0001);

    assertEquals(
        "Fourth scored fact should have a calculated score of 1.0",
        1.0,
        findFactScore("Microsoft", "CEO", "Bill Gates"),
        0.0);

    assertEquals(
        "Fifth scored fact should have a calculated score of 0.0",
        0.0,
        findFactScore("Microsoft", "not in model", "Bill Gates"),
        0.0);
  }

  private Integer findPatternFrequency(String pattern) {
    return findPatternScore(pattern).getInteger(FREQUENCY_KEY);
  }

  private Double findPatternCoherence(String pattern) {
    return findPatternScore(pattern).getDouble(COHERENCE_KEY);
  }

  private Document findPatternScore(String pattern) {
    return patternsCollection.find(eq(PATTERN_FIELD, pattern)).first();
  }

  private double findFactScore(String subject, String attribute, String object) {
    Bson filter =
        and(eq(SUBJECT_FIELD, subject), eq(ATTRIBUTE_FIELD, attribute), eq(OBJECT_FIELD, object));
    return scoredFactsCollection.find(filter).first().getDouble(SCORE_FIELD);
  }
}
