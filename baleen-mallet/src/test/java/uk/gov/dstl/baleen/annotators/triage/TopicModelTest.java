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

  private static final URL MODEL_URL = TopicModelTest.class.getResource("/model.mallet");

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

    jCas.setDocumentText(
        "Clem Hill (1877–1945) was an Australian cricketer who played 49 Test matches as a specialist batsman between 1896 and 1912. He captained the Australian team in ten Tests, winning five and losing five. A prolific run scorer, Hill scored 3,412 runs in Test cricket—a world record at the time of his retirement—at an average of 39.21 per innings, including seven centuries. In 1902, Hill was the first batsman to make 1,000 Test runs in a calendar year, a feat that would not be repeated for 45 years. His innings of 365 scored against New South Wales for South Australia in 1900–01 was a Sheffield Shield record for 27 years. His Test cricket career ended in controversy after he was involved in a brawl with cricket administrator and fellow Test selector Peter McAlister in 1912. He was one of the \"Big Six\", a group of leading Australian cricketers who boycotted the 1912 Triangular Tournament in England when the players were stripped of the right to appoint the tour manager. The boycott effectively ended his Test career. After retiring from cricket, Hill worked in the horse racing industry as a stipendiary steward and later as a handicapper for races including the Caulfield Cup.");

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
    assertEquals(
        "[yard, test, national, years, gunnhild, wilderness, hill, including, norway, parks]",
        value.get());
  }
}
