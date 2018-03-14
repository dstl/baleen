// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.metadata.Metadata;

public class CommonKeywordsTest extends AbstractAnnotatorTest {
  private static String STOPWORDS = "stopwords";
  private ExternalResourceDescription erd =
      ExternalResourceFactory.createExternalResourceDescription(
          STOPWORDS, SharedStopwordResource.class);

  public CommonKeywordsTest() {
    super(CommonKeywords.class);
  }

  @Test
  public void testProcess() throws Exception {
    jCas.setDocumentText(
        new String(Files.readAllBytes(Paths.get(getClass().getResource("turing.txt").toURI()))));
    processJCas(STOPWORDS, erd);

    assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());
    Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
    assertEquals("keywords", md.getKey());

    List<String> keywords = Arrays.asList(md.getValue().split(";"));
    assertEquals(
        6,
        keywords
            .size()); // Question and Digital get the same score, so we end up with 6 keywords not 5
    assertTrue(keywords.contains("machine"));
    assertTrue(keywords.contains("computer"));
    assertTrue(keywords.contains("digital computers"));
    assertTrue(keywords.contains("state"));
    assertTrue(keywords.contains("question"));
    assertTrue(keywords.contains("digital"));

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
    processJCas(STOPWORDS, erd);

    assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());
    Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
    assertEquals("keywords", md.getKey());

    List<String> keywords = Arrays.asList(md.getValue().split(";"));
    assertEquals(
        3,
        keywords
            .size()); // Question and Digital get the same score, so we end up with 6 keywords not 5
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
