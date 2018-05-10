// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.mallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.KEYWORDS_FIELD;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.KEY_STOPWORDS;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.PARAM_MODEL_FILE;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.PARAM_NUMBER_OF_ITERATIONS;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.PARAM_NUMBER_OF_TOPICS;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.TOPIC_FIELD;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.TOPIC_NUMBER_FIELD;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.IDSorter;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import uk.gov.dstl.baleen.annotators.triage.TestData;
import uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.uima.AbstractBaleenTaskTest;

public class TopicModelTrainerTest extends AbstractBaleenTaskTest {

  private static final int NUM_TOPICS = 5;
  private static final int NUM_ITERATIONS = 10;
  private static final String COLLECTION = "documents";
  private Path modelPath;
  private MongoCollection<Document> documents;

  @Before
  public void before()
      throws URISyntaxException, ResourceInitializationException, AnalysisEngineProcessException,
          ResourceAccessException {

    ExternalResourceDescription stopWordsErd =
        ExternalResourceFactory.createExternalResourceDescription(
            TopicModelTrainer.KEY_STOPWORDS, SharedStopwordResource.class);

    List<String> data = new TestData().asList();

    try {
      modelPath = Files.createTempFile("model", ".mallet");
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }

    ExternalResourceDescription fongoErd =
        ExternalResourceFactory.createExternalResourceDescription(
            SharedMongoResource.RESOURCE_KEY,
            SharedFongoResource.class,
            "fongo.collection",
            COLLECTION,
            "fongo.data",
            data.toString());

    final AnalysisEngine ae =
        create(
            TopicModelTrainer.class,
            KEY_STOPWORDS,
            stopWordsErd,
            SharedMongoResource.RESOURCE_KEY,
            fongoErd,
            PARAM_NUMBER_OF_TOPICS,
            NUM_TOPICS,
            PARAM_NUMBER_OF_ITERATIONS,
            NUM_ITERATIONS,
            PARAM_MODEL_FILE,
            modelPath.toString());

    execute(ae);

    SharedFongoResource sfr =
        (SharedFongoResource)
            ae.getUimaContext().getResourceObject(SharedMongoResource.RESOURCE_KEY);
    documents = sfr.getDB().getCollection(COLLECTION);
  }

  @Test
  public void testTaskProducesValidModelFile() throws Exception {

    File modelFile = modelPath.toFile();
    assertTrue(modelFile.exists());

    ParallelTopicModel model = ParallelTopicModel.read(modelFile);
    assertEquals(NUM_TOPICS, model.getNumTopics());

    // Sanity check the Mallet code does something, but no need to test it.
    double[] topicDistribution = model.getTopicProbabilities(0);
    ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
    Formatter out = new Formatter(new StringBuilder(), Locale.UK);
    for (int topic = 0; topic < NUM_TOPICS; topic++) {
      out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
      int rank = 0;
      Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
      while (iterator.hasNext() && rank < 5) {
        IDSorter idCountPair = iterator.next();
        out.format(
            "%s (%.0f) ",
            model.alphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
        rank++;
      }
      out.format("\n");
    }
    System.out.println(out);

    FindIterable<Document> find = documents.find();
    MongoCursor<Document> iterator = find.iterator();
    int count = 0;
    while (iterator.hasNext()) {
      Document document = iterator.next();
      Document topic = (Document) document.get(TOPIC_FIELD);
      assertNotNull(topic.getString(KEYWORDS_FIELD));
      assertNotNull(topic.getInteger(TOPIC_NUMBER_FIELD));
      count++;
    }

    assertEquals(16, count);
  }
}
