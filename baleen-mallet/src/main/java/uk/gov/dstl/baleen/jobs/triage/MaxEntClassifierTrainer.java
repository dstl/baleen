// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.triage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.bson.types.ObjectId;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.FeatureConstraintUtil;
import cc.mallet.classify.MaxEntGETrainer;
import cc.mallet.classify.constraints.ge.MaxEntGEConstraint;
import cc.mallet.classify.constraints.ge.MaxEntKLFLGEConstraints;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.FluentIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import uk.gov.dstl.baleen.mallet.MaxEntClassifierPipe;
import uk.gov.dstl.baleen.mallet.MongoIterable;
import uk.gov.dstl.baleen.mallet.ObjectFile;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

/**
 * A task to create a classifier model for a set of documents (stored in mongo) using a given
 * labelled features.
 *
 * <p>Uses the Mallet Maximum entropy trainer with labelled features.
 *
 * <p>Based on: "Learning from Labeled Features using Generalized Expectation Criteria" Gregory
 * Druck, Gideon Mann, Andrew McCallum SIGIR 2008
 */
public class MaxEntClassifierTrainer extends BaleenTask {

  /** Field the classification information is stored */
  public static final String CLASSIFICATION_FIELD = "classification";

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

  @ConfigurationParameter(
    name = PARAM_CONTENT_FIELD,
    defaultValue = uk.gov.dstl.baleen.consumers.Mongo.FIELD_CONTENT
  )
  private String contentField;

  /**
   * Labels file describing the features for each label.
   *
   * <p>Each label is defined on a line with a space separated list of word features, with format
   *
   * <pre>
   * label feature1 feature2 feature3
   * </pre>
   *
   * e.g.
   *
   * <pre>
   * positive good love amazing best awesome
   * negative not can't enemy horrible ain't
   * </pre>
   *
   * @baleen.config 100
   */
  public static final String PARAM_LABELS_FILE = "labelsFile";

  @ConfigurationParameter(name = PARAM_LABELS_FILE, mandatory = true)
  private File labelsFile;

  /**
   * Max number of iterations
   *
   * @baleen.config
   */
  public static final String PARAM_NUMBER_OF_ITERATIONS = "numIterations";

  @ConfigurationParameter(name = PARAM_NUMBER_OF_ITERATIONS, mandatory = false)
  private int numIterations = Integer.MAX_VALUE;

  /**
   * The gaussian prior variance
   *
   * @baleen.config 1.0
   */
  public static final String PARAM_VARIANCE = "variance";

  @ConfigurationParameter(name = PARAM_VARIANCE, defaultValue = "1.0")
  private double variance;

  /**
   * Output model file path
   *
   * @baleen.config topicModel
   */
  public static final String PARAM_MODEL_FILE = "modelFile";

  @ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = false)
  private String modelFile;

  private MongoCollection<Document> documentsCollection;

  private Map<String, List<String>> labelsAndFeatures;

  private Collection<String> stopwords;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);

    MongoDatabase db = mongo.getDB();
    documentsCollection = db.getCollection(documentCollectionName);
    labelsAndFeatures = readLabelsAndFeaturesFromFile(labelsFile);
    stopwords = stopwordResource.getStopwords(stoplist);
  }

  @Override
  protected void execute(JobSettings settings) throws AnalysisEngineProcessException {

    Pipe pipe = new MaxEntClassifierPipe(labelsAndFeatures.keySet(), stopwords);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(getDocumentsFromMongoWithRandonLabelAssignement());

    Alphabet targetAlphabet = instances.getTargetAlphabet();
    HashMap<Integer, ArrayList<Integer>> featuresAndLabels =
        mapFeaturesToLabels(instances.getDataAlphabet(), targetAlphabet);

    int numLabels = targetAlphabet.size();
    HashMap<Integer, double[]> constraintsMap =
        FeatureConstraintUtil.setTargetsUsingHeuristic(featuresAndLabels, numLabels, 0.9);

    MaxEntKLFLGEConstraints geConstraints =
        new MaxEntKLFLGEConstraints(instances.getDataAlphabet().size(), numLabels, false);
    constraintsMap
        .entrySet()
        .forEach(e -> geConstraints.addConstraint(e.getKey(), e.getValue(), 1));
    ArrayList<MaxEntGEConstraint> constraints = new ArrayList<>();
    constraints.add(geConstraints);

    // Create a classifier trainer, and use it to create a classifier
    MaxEntGETrainer trainer = new MaxEntGETrainer(constraints);
    trainer.setMaxIterations(numIterations);
    trainer.setGaussianPriorVariance(variance);

    instances.forEach(
        i -> {
          i.unLock();
          i.setTarget(null);
          i.lock();
        });

    Classifier classifier = trainer.train(instances);

    List<Classification> classify = classifier.classify(instances);

    writeClassificationToMongo(classify);
    new ObjectFile(classifier, modelFile).write();
  }

  private Map<String, List<String>> readLabelsAndFeaturesFromFile(File file)
      throws ResourceInitializationException {
    Map<String, List<String>> map = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line = reader.readLine();
      while (line != null) {
        line = line.trim();
        String[] split = line.split("\\s+");
        String label = split[0];
        List<String> features = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
          features.add(split[i]);
        }
        map.put(label, features);
        line = reader.readLine();
      }
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    return map;
  }

  private HashMap<Integer, ArrayList<Integer>> mapFeaturesToLabels(
      Alphabet dataAlphabet, Alphabet targetAlphabet) {

    HashMap<Integer, ArrayList<Integer>> featuresAndLabels = new HashMap<>();

    labelsAndFeatures.forEach(
        (k, v) -> {
          Integer label = targetAlphabet.lookupIndex(k);
          v.forEach(
              f -> {
                Integer feature = dataAlphabet.lookupIndex(f);
                ArrayList<Integer> labels = featuresAndLabels.get(feature);
                if (labels == null) {
                  labels = new ArrayList<>();
                  featuresAndLabels.put(feature, labels);
                }
                labels.add(label);
              });
        });

    return featuresAndLabels;
  }

  private void writeClassificationToMongo(List<Classification> classify) {
    classify.forEach(
        classification -> {
          Instance instance = classification.getInstance();
          documentsCollection.findOneAndUpdate(
              Filters.eq(new ObjectId((String) instance.getName())),
              Updates.set(
                  CLASSIFICATION_FIELD, classification.getLabeling().getBestLabel().toString()));
        });
  }

  private Iterator<Instance> getDocumentsFromMongoWithRandonLabelAssignement() {
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
