// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.triage.MalletClassifier.PARAM_METADTA_KEY;
import static uk.gov.dstl.baleen.annotators.triage.MalletClassifier.PARAM_MODEL;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

public class ClassifierTest extends AbstractAnnotatorTest {

  private static final URL MAX_ENT_MODEL_URL =
      ClassifierTest.class.getResource("/max_ent_classifier.mallet");

  private static final URL NAIVE_BAYES_MODEL_URL =
      ClassifierTest.class.getResource("/naive-bayes.mallet");

  public ClassifierTest() {
    super(MalletClassifier.class);
  }

  @Test(expected = ResourceInitializationException.class)
  public void testMissingTopicModel() throws Exception {
    jCas.setDocumentText("Some text");
    processJCas(PARAM_MODEL, "missing.model");
  }

  @Test
  public void testMaxEntClassifiedPositive() throws Exception {

    jCas.setDocumentText("I love this amazing awesome classifier.");
    processJCas(
        PARAM_MODEL, Paths.get(MAX_ENT_MODEL_URL.toURI()).toString(), PARAM_METADTA_KEY, "key");

    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> value = metadata.find("key");
    assertTrue(value.isPresent());
    assertEquals("pos", value.get());
  }

  @Test
  public void testMaxEntClassifiedNegative() throws Exception {

    jCas.setDocumentText("I can't stand this horrible test.");
    processJCas(
        PARAM_MODEL, Paths.get(MAX_ENT_MODEL_URL.toURI()).toString(), PARAM_METADTA_KEY, "key");

    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> value = metadata.find("key");
    assertTrue(value.isPresent());
    assertEquals("neg", value.get());
  }

  @Test
  public void testNaiveBayesClassifiedPositive() throws Exception {

    jCas.setDocumentText("I love this amazing awesome classifier.");
    processJCas(
        PARAM_MODEL, Paths.get(NAIVE_BAYES_MODEL_URL.toURI()).toString(), PARAM_METADTA_KEY, "key");

    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> value = metadata.find("key");
    assertTrue(value.isPresent());
    assertEquals("pos", value.get());
  }

  @Test
  public void testNaiveBayesClassifiedNegative() throws Exception {

    jCas.setDocumentText("I can't stand this horrible test.");
    processJCas(
        PARAM_MODEL, Paths.get(NAIVE_BAYES_MODEL_URL.toURI()).toString(), PARAM_METADTA_KEY, "key");

    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> value = metadata.find("key");
    assertTrue(value.isPresent());
    assertEquals("neg", value.get());
  }
}
