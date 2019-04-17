// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.triage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.triage.impl.AbstractKeywordsAnnotator.KEYWORD_METADATA_KEY;
import static uk.gov.dstl.baleen.annotators.triage.impl.AbstractKeywordsAnnotator.KEY_STOPWORDS;
import static uk.gov.dstl.baleen.annotators.triage.impl.AbstractKeywordsAnnotator.PARAM_STEMMING;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

public class CommonKeywordsTest extends AbstractAnnotatorTest {

  private ExternalResourceDescription erd =
      ExternalResourceFactory.createNamedResourceDescription(
          KEY_STOPWORDS, SharedStopwordResource.class);

  public CommonKeywordsTest() {
    super(CommonKeywords.class);
  }

  @Test
  public void testProcess() throws Exception {
    jCas.setDocumentText(
        new String(Files.readAllBytes(Paths.get(getClass().getResource("turing.txt").toURI()))));
    processJCas(KEY_STOPWORDS, erd, PARAM_STEMMING, "ENGLISH");

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(5, keywords.size());

    assertTrue(keywords.contains("machine"));
    assertTrue(keywords.contains("computer"));
    assertTrue(keywords.contains("digital computers"));
    assertTrue(keywords.contains("state"));
    // Same score so either is valid
    assertTrue(keywords.contains("question") || keywords.contains("digital"));

    assertTrue(JCasUtil.select(jCas, Buzzword.class).size() > 0);

    Set<String> buzzwords = new HashSet<>();
    for (Buzzword bw : JCasUtil.select(jCas, Buzzword.class)) {
      assertEquals("keyword", bw.getTags(0));
      buzzwords.add(bw.getValue());
    }

    assertTrue(buzzwords.contains("machines"));
    assertTrue(buzzwords.contains("computing"));
    assertTrue(buzzwords.contains("questioning"));
  }

  @Test
  public void testProcessWithText() throws Exception {
    jCas.setDocumentText(
        new String(Files.readAllBytes(Paths.get(getClass().getResource("turing.txt").toURI()))));
    // THe only text we are going to process is "The Imitation Game"
    new Text(jCas, 54, 74).addToIndexes();
    processJCas(KEY_STOPWORDS, erd);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(3, keywords.size());

    assertTrue(keywords.contains("imitation"));
    assertFalse(keywords.contains("machine"));
    assertFalse(keywords.contains("computer"));
    assertFalse(keywords.contains("digital computers"));
    assertFalse(keywords.contains("state"));
    assertFalse(keywords.contains("question"));
    assertFalse(keywords.contains("digital"));

    assertTrue(JCasUtil.select(jCas, Buzzword.class).size() > 0);

    Set<String> buzzwords = new HashSet<>();
    for (Buzzword bw : JCasUtil.select(jCas, Buzzword.class)) {
      assertEquals("keyword", bw.getTags(0));
      buzzwords.add(bw.getValue());
    }

    assertFalse(buzzwords.contains("machines"));
    assertFalse(buzzwords.contains("computing"));
    assertFalse(buzzwords.contains("questioning"));
  }
}
