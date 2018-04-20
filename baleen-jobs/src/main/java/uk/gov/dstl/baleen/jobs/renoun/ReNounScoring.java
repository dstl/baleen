// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.renoun;

import static com.mongodb.client.model.Filters.eq;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.COHERENCE_KEY;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.FREQUENCY_KEY;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.PATTERN_FACT_FIELD;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import de.jungblut.distance.CosineDistance;
import de.jungblut.glove.GloveRandomAccessReader;
import de.jungblut.glove.impl.GloveBinaryRandomAccessReader;
import de.jungblut.glove.impl.GloveBinaryWriter;
import de.jungblut.glove.impl.GloveTextReader;
import de.jungblut.glove.util.StringVectorPair;
import de.jungblut.math.DoubleVector;

import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.data.ReNounFact;
import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

/**
 * Score facts produced by the ReNounRelationshipAnnotator.
 *
 * <p>The annotator stores each fact (subject, attribute and object) and the pattern that matched in
 * a mongo document and store it in the configured collection.
 *
 * <p>The fact score is derived from the scores of the patterns that extracted it and is the sum of
 * the patterns frequency times the coherence for each pattern.
 *
 * <p>The Frequency of a pattern is the number of facts extracted by the pattern.
 *
 * <p>The coherence of a pattern is a measure of the 'closeness' of the attributes of the facts that
 * the pattern extracts. This is calculated using the cosine distance of a word to vector
 * clustering. The paper used word2vec, we use <a
 * href="https://nlp.stanford.edu/projects/glove/">GloVe</a> due to the availability of models.
 *
 * <pre>
 * mongo:
 *   db: baleen
 *   host: localhost
 *
 * job:
 *   tasks:
 *   - class: renoun.ReNounScoring
 *     model: path/to/model
 *     binFolder: path/to/writable/folder
 * </pre>
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ReNounScoring extends BaleenTask {

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  @ExternalResource(key = SharedMongoResource.RESOURCE_KEY)
  private SharedMongoResource mongo;

  /**
   * The name of the Mongo collection to output facts to for scoring
   *
   * @baleen.config renoun_facts
   */
  public static final String PARAM_FACT_COLLECTION = "factCollection";

  @ConfigurationParameter(name = PARAM_FACT_COLLECTION, defaultValue = "renoun_facts")
  private String factCollection;

  /**
   * The name of the Mongo collection to output pattern scores
   *
   * @baleen.config renoun_pattern_scores
   */
  public static final String PARAM_PATTERN_SCORE_COLLECTION = "patternScoreCollection";

  @ConfigurationParameter(
    name = PARAM_PATTERN_SCORE_COLLECTION,
    defaultValue = "renoun_pattern_scores"
  )
  private String patternScoreCollectionName;

  /**
   * The name of the Mongo collection to output scored facts
   *
   * @baleen.config renoun_fact_scores
   */
  public static final String PARAM_FACT_SCORE_COLLECTION = "factScoreCollection";

  @ConfigurationParameter(name = PARAM_FACT_SCORE_COLLECTION, defaultValue = "renoun_fact_scores")
  private String factScoreCollectionName;

  /**
   * The path to the model file
   *
   * @baleen.config /path/to/model
   */
  public static final String PARAM_MODEL_PATH = "model";

  @ConfigurationParameter(name = PARAM_MODEL_PATH, mandatory = true)
  private String modelPath;

  /**
   * The path to a folder that is writable.
   *
   * <p>This is used to store the model in a more efficient way, and is only done once
   *
   * @baleen.config /path/to/writable/folder
   */
  public static final String PARAM_BIN_FOLDER = "binFolder";

  @ConfigurationParameter(name = PARAM_BIN_FOLDER, mandatory = false)
  private String binFolder;

  static final String BINARY_DICTIONARY_FILENAME = "dict.bin";
  static final String BINARY_VECTORS_FILENAME = "vectors.bin";

  private MongoCollection<Document> factsCollection;
  private MongoCollection<Document> patternScoreCollection;
  private MongoCollection<Document> factScoreCollection;

  private GloveRandomAccessReader gloveReader;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    if (binFolder == null) {
      try {
        binFolder = Files.createTempDirectory("renoun").toString();
      } catch (IOException e) {
        throw new ResourceInitializationException("Could not create temporary directory", null, e);
      }
    }

    if (!containsBinaries()) {
      convertModelTextFileToBinary();
    }

    try {
      gloveReader = new GloveBinaryRandomAccessReader(Paths.get(binFolder));
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }

    MongoDatabase db = mongo.getDB();

    factsCollection = db.getCollection(factCollection);
    patternScoreCollection = db.getCollection(patternScoreCollectionName);
    factScoreCollection = db.getCollection(factScoreCollectionName);
  }

  @Override
  protected void execute(JobSettings settings) throws AnalysisEngineProcessException {
    getMonitor().info("Scoring ReNoun Facts");

    MongoCursor<Document> factsCursor = factsCollection.find().iterator();

    SetMultimap<String, ReNounFact> patternMap = getPatternMultimap(factsCursor);

    for (Map.Entry<String, Collection<ReNounFact>> entry : patternMap.asMap().entrySet()) {

      String pattern = entry.getKey();
      Collection<ReNounFact> facts = entry.getValue();

      int frequency = facts.size();

      Set<String> attributes =
          facts.stream().map(ReNounFact::getAttribute).collect(Collectors.toSet());

      double coherenceAverage = calculateCoherenceAverage(attributes);

      insertScoredPattern(pattern, frequency, coherenceAverage);
    }

    SetMultimap<ReNounFact, ScoredPattern> factToScoredPatternMap =
        getFactToScoredPatternMultimap(factsCollection, patternScoreCollection);

    for (Map.Entry<ReNounFact, Collection<ScoredPattern>> entry :
        factToScoredPatternMap.asMap().entrySet()) {

      ReNounFact fact = entry.getKey();
      Collection<ScoredPattern> scoredPatterns = entry.getValue();

      double factScore = calculateFactScore(scoredPatterns);
      fact.setScore(factScore);
      fact.save(factScoreCollection);
    }
  }

  private boolean containsBinaries() {
    return folderContainsBinaries(new File(binFolder));
  }

  private boolean folderContainsBinaries(File modelFile) {
    return new File(modelFile, BINARY_DICTIONARY_FILENAME).exists()
        && new File(modelFile, BINARY_VECTORS_FILENAME).exists();
  }

  private void convertModelTextFileToBinary() throws ResourceInitializationException {

    GloveTextReader reader = new GloveTextReader();
    Stream<StringVectorPair> stream;
    try {
      stream = reader.stream(Paths.get(modelPath));
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    GloveBinaryWriter writer = new GloveBinaryWriter();
    try {
      writer.writeStream(stream, Paths.get(binFolder));
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  private double calculateCoherenceAverage(Set<String> attributes) {

    double sum = 0.0;
    int pairsCount = 0;
    String[] attributesArray = attributes.toArray(new String[attributes.size()]);
    for (int i = 0; i < attributes.size() - 1; i++) {
      String attribute1 = attributesArray[i];
      for (int j = i + 1; j < attributes.size(); j++) {
        String attribute2 = attributesArray[j];
        pairsCount++;
        sum += calculateCosineDistance(attribute1, attribute2);
      }
    }

    if (pairsCount == 0) {
      return 1.0;
    } else {
      return 1.0 - sum / pairsCount;
    }
  }

  private double calculateCosineDistance(String a1, String a2) {

    CosineDistance cosineDistance = new CosineDistance();

    DoubleVector attribute1 = null;
    DoubleVector attribute2 = null;
    try {
      attribute1 = gloveReader.get(a1);
      attribute2 = gloveReader.get(a2);
    } catch (IOException e) {
      getMonitor().error(e.getMessage());
    }

    if (attribute1 == null || attribute2 == null) {
      return 1.0;
    }

    return cosineDistance.measureDistance(attribute1, attribute2);
  }

  private void insertScoredPattern(String pattern, int frequency, double coherenceAverage) {
    patternScoreCollection.insertOne(
        new Document()
            .append(PATTERN_FACT_FIELD, pattern)
            .append(FREQUENCY_KEY, frequency)
            .append(COHERENCE_KEY, coherenceAverage));
  }

  private SetMultimap<String, ReNounFact> getPatternMultimap(MongoCursor<Document> cursor) {
    SetMultimap<String, ReNounFact> patternMap = HashMultimap.create();

    while (cursor.hasNext()) {
      Document d = cursor.next();
      patternMap.put(d.getString(PATTERN_FACT_FIELD), new ReNounFact(d));
    }
    return patternMap;
  }

  private SetMultimap<ReNounFact, ScoredPattern> getFactToScoredPatternMultimap(
      MongoCollection<Document> factsCollection,
      MongoCollection<Document> scoredPatternsCollection) {

    SetMultimap<ReNounFact, ScoredPattern> factToPatternMap = HashMultimap.create();

    MongoCursor<Document> scoredPatternsCursor = scoredPatternsCollection.find().iterator();

    while (scoredPatternsCursor.hasNext()) {
      Document scoredPatternDocument = scoredPatternsCursor.next();
      Iterator<Document> factsMatchingScoredPatternIterator =
          factsCollection
              .find(eq(PATTERN_FACT_FIELD, scoredPatternDocument.get(PATTERN_FACT_FIELD)))
              .iterator();
      while (factsMatchingScoredPatternIterator.hasNext()) {
        Document factMatchingScoredPattern = factsMatchingScoredPatternIterator.next();
        ReNounFact fact = new ReNounFact(factMatchingScoredPattern);
        ScoredPattern scoredPattern = new ScoredPattern(scoredPatternDocument);
        factToPatternMap.put(fact, scoredPattern);
      }
    }
    return factToPatternMap;
  }

  private double calculateFactScore(Collection<ScoredPattern> scoredPatterns) {
    double factScore = 0.0;

    for (ScoredPattern scoredPattern : scoredPatterns) {
      int frequency = scoredPattern.getFrequency();
      double coherence = scoredPattern.getCoherence();
      factScore += frequency * coherence;
    }

    return factScore;
  }
}
