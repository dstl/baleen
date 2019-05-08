// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.triage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.FluentIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.consumers.Mongo;
import uk.gov.dstl.baleen.mallet.ClassifierPipe;
import uk.gov.dstl.baleen.mallet.ClassifierTrainerFactory;
import uk.gov.dstl.baleen.mallet.MongoIterable;
import uk.gov.dstl.baleen.mallet.ObjectFile;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

/**
 * A task to train a classifier model from a set of documents stored in mongo with assigned labels
 * in the metadata.
 *
 * <p>
 */
public class MalletClassifierTrainer extends BaleenTask {

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
   * uk.gov.dstl.baleen.resources.SharedStopwordResource.StopwordList}, then that list will be
   * loaded.
   *
   * <p>Otherwise, the string is taken to be a file path and that file is used. The format of the
   * file is expected to be one stopword per line.
   *
   * @baleen.config DEFAULT
   */
  public static final String PARAM_STOPLIST = "stoplist";

  @ConfigurationParameter(name = PARAM_STOPLIST, defaultValue = "DEFAULT")
  private String stoplist;

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
   * The name of the metadata field the label is stored
   *
   * @baleen.config label
   */
  public static final String PARAM_LABEL_METADATA_KEY = "labelField";

  @ConfigurationParameter(name = PARAM_LABEL_METADATA_KEY, defaultValue = "label")
  private String labelField;

  /**
   * The name of field in the Mongo document storing the content
   *
   * @baleen.config content
   */
  public static final String PARAM_CONTENT_FIELD = "field";

  @ConfigurationParameter(name = PARAM_CONTENT_FIELD, defaultValue = Mongo.FIELD_CONTENT)
  private String contentField;

  /**
   * Factory for {@link ClassifierTrainer}s based on the Mallet string format.
   *
   * <pre>
   * ClassName,parameterName=parameterValue,parameterName=parameterValue
   * </pre>
   *
   * For example
   *
   * <pre>
   * MaxEnt,gaussianPriorVariance=1.0,numIterations=1000
   * </pre>
   *
   * @baleen.config topicModel
   */
  public static final String PARAM_CLASSIFIER_TRAINER = "trainer";

  @ConfigurationParameter(
      name = PARAM_CLASSIFIER_TRAINER,
      defaultValue = {"NaiveBayes"})
  private String[] trainerDefinition;

  /**
   * Output model file path-prefix
   *
   * @baleen.config model
   */
  public static final String PARAM_MODEL_FILE = "modelFile";

  @ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = false)
  private String modelFile;

  /**
   * Separate some of the data for testing the classifier
   *
   * @baleen.config topicModel
   */
  public static final String PARAM_FOR_TESTING = "forTesting";

  @ConfigurationParameter(name = PARAM_FOR_TESTING, defaultValue = "0.0")
  private float forTesting;

  /**
   * Test result file
   *
   * <p>Outputs the trial results to file for reference. NB forTesting must be &gt; 0.0 to perform
   * trial.
   *
   * @baleen.config topicModel
   */
  public static final String PARAM_RESULT_FILE = "resultFile";

  @ConfigurationParameter(name = PARAM_RESULT_FILE, mandatory = false)
  private String resultFileName;

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

    Pipe pipe = new ClassifierPipe(stopwords);
    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(getDocumentsFromMongo());

    InstanceList training = null;
    InstanceList testing = null;
    if (forTesting > 0.0) {
      InstanceList[] ilists = instances.split(new double[] {1 - forTesting, forTesting});
      training = ilists[0];
      testing = ilists[1];
    } else {
      training = instances;
    }

    processTrainerDefinitions(training, testing);
  }

  private void processTrainerDefinitions(InstanceList training, InstanceList testing)
      throws AnalysisEngineProcessException {
    for (int i = 0; i < trainerDefinition.length; i++) {
      getMonitor().info("Classifying {} starting", trainerDefinition[i]);
      ClassifierTrainerFactory factory = new ClassifierTrainerFactory(trainerDefinition[i]);
      ClassifierTrainer<?> trainer = factory.createTrainer();

      Classifier classifier = trainer.train(training);

      writeClassifierToModelPath(i, classifier);

      if (testing != null) {
        Trial trial = new Trial(classifier, testing);

        logAccuracyMetrics(classifier, trial);
        if (resultFileName != null) {

          File resultFile = new File(resultFileName);
          List<String> title = createTitle(classifier);

          String titleRow = rowString(title);
          getMonitor().info(titleRow);

          checkResultsFileExists(resultFile, titleRow);

          List<String> row = createRow(training, testing, trainerDefinition[i], classifier, trial);
          String rowString = rowString(row);
          getMonitor().info(rowString);
          try {
            Files.write(
                resultFile.toPath(),
                rowString.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND);
          } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
          }
        }
      }
      getMonitor().info("Classifying {} complete", trainerDefinition[i]);
    }
  }

  private void writeClassifierToModelPath(int i, Classifier classifier) {
    if (modelFile != null) {
      if (trainerDefinition.length > 1) {
        new ObjectFile(
                classifier,
                modelFile + "_" + i + "_" + classifier.getClass().getSimpleName() + ".mallet")
            .write();
      } else {
        new ObjectFile(classifier, modelFile).write();
      }
    }
  }

  private void logAccuracyMetrics(Classifier classifier, Trial trial) {
    getMonitor().info("Accuracy: {}", trial.getAccuracy());
    for (String label : (String[]) classifier.getLabelAlphabet().toArray(new String[0])) {
      getMonitor().info("F1 for class '{}': {}", label, trial.getF1(label));
      getMonitor().info("Precision for class '{}' : {}", label, trial.getPrecision(label));
    }
  }

  private List<String> createTitle(Classifier classifier) {
    List<String> title = new ArrayList<>();
    title.add("Trainer");
    title.add("Training");
    title.add("Trail");
    title.add("Accuracy");
    for (String label : (String[]) classifier.getLabelAlphabet().toArray(new String[0])) {
      title.add(label + "_F1");
      title.add(label + "_P");
      title.add(label + "_R");
    }
    return title;
  }

  private void checkResultsFileExists(File resultFile, String titleRow)
      throws AnalysisEngineProcessException {
    if (!resultFile.exists()) {
      try {
        Files.write(
            resultFile.toPath(),
            titleRow.getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.CREATE_NEW);
      } catch (IOException e) {
        throw new AnalysisEngineProcessException(e);
      }
    }
  }

  private List<String> createRow(
      InstanceList training, InstanceList testing, String e, Classifier classifier, Trial trial) {
    List<String> row = new ArrayList<>();
    row.add(e);
    row.add(Integer.toString(training.size()));
    row.add(Integer.toString(testing.size()));
    row.add(Double.toString(trial.getAccuracy()));
    for (String label : (String[]) classifier.getLabelAlphabet().toArray(new String[0])) {
      row.add(Double.toString(trial.getF1(label)));
      row.add(Double.toString(trial.getPrecision(label)));
      row.add(Double.toString(trial.getRecall(label)));
    }
    return row;
  }

  private String rowString(List<String> row) {
    return row.stream()
            .map(s -> s.replaceAll("\"", "\"\""))
            .map(s -> "\"" + s + "\"")
            .collect(Collectors.joining(","))
        + "\n";
  }

  private Iterator<Instance> getDocumentsFromMongo() {
    FindIterable<Document> find = documentsCollection.find();
    return FluentIterable.from(new MongoIterable(find))
        .transform(
            d -> {
              String name = d.getObjectId("_id").toHexString();
              String data = d.getString(contentField);
              Optional<String> label = getLabel(d);
              if (!label.isPresent()) {
                Document metadata = (Document) d.get(Mongo.FIELD_METADATA);
                label = getLabel(metadata);
              }
              return new Instance(data, label.orElse("UNKNOWN"), name, null);
            })
        .iterator();
  }

  @SuppressWarnings("unchecked")
  private Optional<String> getLabel(Document document) {
    String label = null;
    try {
      label = ((List<String>) document.get(labelField)).get(0);
    } catch (NullPointerException | ClassCastException e) {
      label = document.getString(labelField);
    }
    return Optional.ofNullable(label);
  }
}
