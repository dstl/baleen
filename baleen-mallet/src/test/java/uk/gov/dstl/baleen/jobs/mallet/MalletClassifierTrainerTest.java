// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.mallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer.KEY_STOPWORDS;
import static uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer.PARAM_CLASSIFIER_TRAINER;
import static uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer.PARAM_DOCUMENT_COLLECTION;
import static uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer.PARAM_FOR_TESTING;
import static uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer.PARAM_LABEL_METADATA_KEY;
import static uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer.PARAM_MODEL_FILE;
import static uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer.PARAM_RESULT_FILE;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import cc.mallet.classify.Classifier;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.consumers.Mongo;
import uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer;
import uk.gov.dstl.baleen.jobs.triage.MaxEntClassifierTrainer;
import uk.gov.dstl.baleen.mallet.FileObject;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.uima.AbstractBaleenTaskTest;

public class MalletClassifierTrainerTest extends AbstractBaleenTaskTest {

  private static final String SOURCE = Mongo.FIELD_DOCUMENT_SOURCE;
  private static final String CONTENT = Mongo.FIELD_CONTENT;
  private static final String DOCUMENT = Mongo.FIELD_DOCUMENT;
  private static final String METADATA = Mongo.FIELD_METADATA;
  private static final String LABEL = "label";
  private static final String COLLECTION = "collection";

  private Path modelPath;
  private Path resultPath;
  private ExternalResourceDescription fongoErd;
  private ExternalResourceDescription stopWordsErd;

  @Before
  public void before()
      throws URISyntaxException, ResourceInitializationException, AnalysisEngineProcessException,
          ResourceAccessException {

    // @formatter:off
    ImmutableList<String> data =
        ImmutableList.of(
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos1.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "I love this sandwich.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos2.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "this is an amazing place!'")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos3.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "I feel very good about these beers.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos4.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "this is my best work.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos5.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "what an awesome view")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos6.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "the beer was good.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos7.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "I feel amazing!")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos8.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "Gary is a friend of mine.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg1.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I do not like this restaurant")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg2.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I am tired of this stuff.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg3.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I can't deal with this")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg4.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "he is my sworn enemy!")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg5.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "my boss is horrible.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg6.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I do not enjoy my job")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg7.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I ain't feeling dandy today.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg8.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I can't believe I'm doing this.")
                .toJson());

    // @formatter:on

    try {
      modelPath = Files.createTempFile("model", ".mallet");
      resultPath = Files.createTempFile("result", ".csv");
      Files.delete(resultPath);
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }

    fongoErd =
        ExternalResourceFactory.createExternalResourceDescription(
            SharedMongoResource.RESOURCE_KEY,
            SharedFongoResource.class,
            "fongo.collection",
            COLLECTION,
            "fongo.data",
            data.toString());

    stopWordsErd =
        ExternalResourceFactory.createExternalResourceDescription(
            MaxEntClassifierTrainer.KEY_STOPWORDS, SharedStopwordResource.class);
  }

  @Test
  public void testTaskProducesValidModelFile() throws Exception {

    final AnalysisEngine ae =
        create(
            MalletClassifierTrainer.class,
            KEY_STOPWORDS,
            stopWordsErd,
            SharedMongoResource.RESOURCE_KEY,
            fongoErd,
            PARAM_DOCUMENT_COLLECTION,
            COLLECTION,
            PARAM_LABEL_METADATA_KEY,
            LABEL,
            PARAM_MODEL_FILE,
            modelPath.toString(),
            PARAM_FOR_TESTING,
            0.2f);

    execute(ae);

    validateModel();
  }

  @Test
  public void testTaskProducesValidModelFileWithConfig() throws Exception {

    final AnalysisEngine ae =
        create(
            MalletClassifierTrainer.class,
            KEY_STOPWORDS,
            stopWordsErd,
            SharedMongoResource.RESOURCE_KEY,
            fongoErd,
            PARAM_DOCUMENT_COLLECTION,
            COLLECTION,
            PARAM_LABEL_METADATA_KEY,
            LABEL,
            PARAM_MODEL_FILE,
            modelPath.toString(),
            PARAM_FOR_TESTING,
            0.2f,
            PARAM_CLASSIFIER_TRAINER,
            new String[] {"DecisionTreeTrainer,maxDepth=5"},
            PARAM_RESULT_FILE,
            resultPath.toString());

    execute(ae);

    validateModel();

    assertTrue(resultPath.toFile().exists());
    List<String> readAllLines = Files.readAllLines(resultPath);
    assertEquals(2, readAllLines.size());
    assertTrue(readAllLines.get(0).contains("pos"));
    assertTrue(readAllLines.get(1).contains("DecisionTreeTrainer,maxDepth=5"));
  }

  private void validateModel() {
    File modelFile = modelPath.toFile();
    assertTrue(modelFile.exists());

    Classifier classifier = new FileObject<Classifier>(modelFile.getPath()).object();
    assertTrue(classifier.getLabelAlphabet().contains("pos"));
    assertTrue(classifier.getLabelAlphabet().contains("neg"));

    Pipe pipe = classifier.getInstancePipe();
    InstanceList instanceList = new InstanceList(pipe);

    instanceList.addThruPipe(
        new Instance("I love this amazing awesome classifier.", "", null, null));
    instanceList.addThruPipe(new Instance("I can't stand this horrible test.", "", null, null));

    ImmutableSet<String> labels = ImmutableSet.of("pos", "neg");
    assertTrue(
        labels.contains(
            classifier.classify(instanceList.get(0)).getLabeling().getBestLabel().toString()));
    assertTrue(
        labels.contains(
            classifier.classify(instanceList.get(1)).getLabeling().getBestLabel().toString()));
  }
}
