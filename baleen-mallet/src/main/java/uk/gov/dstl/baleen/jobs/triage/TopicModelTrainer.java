// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.triage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.bson.types.ObjectId;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.FluentIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import uk.gov.dstl.baleen.mallet.MaximumIndex;
import uk.gov.dstl.baleen.mallet.MongoIterable;
import uk.gov.dstl.baleen.mallet.TopicModelPipe;
import uk.gov.dstl.baleen.mallet.TopicWords;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

/**
 * A task to create a Topic Model for a set of documents (stored in mongo).
 *
 * <p>Uses Latent Dirichlet Allocation following Newman, Asuncion, Smyth and Welling, Distributed
 * Algorithms for Topic Models JMLR (2009), with SparseLDA sampling scheme and data structure from
 * Yao, Mimno and McCallum, Efficient Methods for Topic Model Inference on Streaming Document
 * Collections, KDD (2009).
 */
public class TopicModelTrainer extends BaleenTask {

  /** Field the topic information is stored */
  public static final String TOPIC_FIELD = "topic";
  /** Field the keyword information is stored */
  public static final String KEYWORDS_FIELD = "keywords";
  /** Field the topic number is stored */
  public static final String TOPIC_NUMBER_FIELD = "topicNumber";

  /**
   * Connection to Stopwords Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
   */
  public static final String KEY_STOPWORDS = "stopwords";

  @ExternalResource(key = KEY_STOPWORDS)
  protected SharedStopwordResource stopwordResource;

  /**
   * The stoplist to use. If the stoplist matches one of the enum's provided in {@link
   * uk.gov.dstl.baleen.resources.SharedStopwordResource#StopwordList}, then that list will be
   * loaded.
   *
   * <p>Otherwise, the string is taken to be a file path and that file is used. The format of the
   * file is expected to be one stopword per line.
   *
   * @baleen.config DEFAULT
   */
  public static final String PARAM_STOPLIST = "stoplist";

  @ConfigurationParameter(name = PARAM_STOPLIST, defaultValue = "DEFAULT")
  protected String stoplist;

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  @ExternalResource(key = SharedMongoResource.RESOURCE_KEY)
  private SharedMongoResource mongo;

  /**
   * The name of the Mongo collection read from and write to
   *
   * @baleen.config documents
   */
  public static final String PARAM_DOCUMENT_COLLECTION = "collection";

  @ConfigurationParameter(name = PARAM_DOCUMENT_COLLECTION, defaultValue = "documents")
  private String documentCollectionName;

  /**
   * The name of field in the Mongo document storing the content
   *
   * @baleen.config content
   */
  public static final String PARAM_CONTENT_FIELD = "field";

  @ConfigurationParameter(name = PARAM_CONTENT_FIELD, defaultValue = "content")
  private String contentField;

  /**
   * Number of topics
   *
   * @baleen.config 100
   */
  public static final String PARAM_NUMBER_OF_TOPICS = "numTopics";

  @ConfigurationParameter(name = PARAM_NUMBER_OF_TOPICS, defaultValue = "100")
  private int numTopics;

  /**
   * Number of iterations
   *
   * @baleen.config 1000
   */
  public static final String PARAM_NUMBER_OF_ITERATIONS = "numIterations";

  @ConfigurationParameter(name = PARAM_NUMBER_OF_ITERATIONS, defaultValue = "1000")
  private int numIterations;

  /**
   * Number of threads
   *
   * @baleen.config 2
   */
  public static final String PARAM_NUMBER_OF_THREADS = "numThreads";

  @ConfigurationParameter(name = PARAM_NUMBER_OF_THREADS, defaultValue = "2")
  private int numThreads;

  /**
   * Output model file path
   *
   * @baleen.config topicModel
   */
  public static final String PARAM_MODEL_FILE = "modelFile";

  @ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = false)
  private String modelFile;

  private MongoCollection<Document> documentsCollection;
  private Collection<String> stopwords;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);

    MongoDatabase db = mongo.getDB();
    documentsCollection = db.getCollection(documentCollectionName);
    stopwords = stopwordResource.getStopwords(stoplist);
  }

  @Override
  protected void execute(JobSettings settings) throws AnalysisEngineProcessException {

    InstanceList instances = new InstanceList(new TopicModelPipe(stopwords));
    instances.addThruPipe(getDocumentsFromMongo());

    ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

    model.setNumThreads(numThreads);
    model.setNumIterations(numIterations);

    model.addInstances(instances);

    try {
      model.estimate();
    } catch (IOException e) {
      getMonitor().warn("Couldn't estimate topic model");
      throw new AnalysisEngineProcessException(e);
    }

    File serializedModelFile = new File(modelFile);
    try {
      Files.createDirectories(serializedModelFile.toPath().getParent());
      model.write(serializedModelFile);
      writeTopicAssignmentsToMongo(instances, new TopicWords(model), model);
    } catch (IOException e) {
      throw new AnalysisEngineProcessException("Error writing model", new Object[0], e);
    }
  }

  private void writeTopicAssignmentsToMongo(
      InstanceList instances, TopicWords topicWords, ParallelTopicModel model) {
    IntStream.range(0, instances.size())
        .forEach(
            document -> {
              double[] topicDistribution = model.getTopicProbabilities(document);
              int maxAt = new MaximumIndex(topicDistribution).find();
              Instance instance = instances.get(document);

              List<String> iterator = topicWords.forTopic(maxAt);

              documentsCollection.findOneAndUpdate(
                  Filters.eq(new ObjectId((String) instance.getName())),
                  Updates.set(
                      TOPIC_FIELD,
                      new Document()
                          .append(KEYWORDS_FIELD, iterator.toString())
                          .append(TOPIC_NUMBER_FIELD, maxAt)));
            });
  }

  private Iterator<Instance> getDocumentsFromMongo() {
    FindIterable<Document> find = documentsCollection.find();
    return FluentIterable.from(new MongoIterable(find))
        .transform(
            d -> {
              String data = d.getString(contentField);
              String name = d.getObjectId("_id").toHexString();
              return new Instance(data, null, name, null);
            })
        .iterator();
  }
}
