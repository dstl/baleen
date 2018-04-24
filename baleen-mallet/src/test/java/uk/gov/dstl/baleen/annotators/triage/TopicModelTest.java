// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.triage.TopicModel.KEY_STOPWORDS;
import static uk.gov.dstl.baleen.annotators.triage.TopicModel.PARAM_METADTA_KEY;
import static uk.gov.dstl.baleen.annotators.triage.TopicModel.PARAM_MODEL;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

public class TopicModelTest extends AbstractAnnotatorTest {

  private static final URL MODEL_URL = TopicModelTest.class.getResource("/topicmodel.mallet");

  public TopicModelTest() {
    super(TopicModel.class);
  }

  @Test(expected = ResourceInitializationException.class)
  public void testMissingTopicModel() throws Exception {
    jCas.setDocumentText("Some text");
    processJCas(PARAM_MODEL, "missing.model");
  }

  @Test
  public void testSingleFileToTopic() throws Exception {
    ExternalResourceDescription stopWordsErd =
        ExternalResourceFactory.createExternalResourceDescription(
            TopicModelTrainer.KEY_STOPWORDS, SharedStopwordResource.class);

    jCas.setDocumentText("Baleen is amazing!.");

    processJCas(
        PARAM_MODEL,
        Paths.get(MODEL_URL.toURI()).toString(),
        PARAM_METADTA_KEY,
        "key",
        KEY_STOPWORDS,
        stopWordsErd);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> value = metadata.find("key");
    assertTrue(value.isPresent());
    assertEquals("[amazing, tired, view, awesome, place]", value.get());
  }
}
