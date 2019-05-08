// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.triage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.triage.impl.AbstractKeywordsAnnotator.KEYWORD_METADATA_KEY;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

public class RakeKeywordsTest extends AnnotatorTestBase {
  private static String STOPWORDS = "stopwords";

  @Test
  public void testNoBuzzwords()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            false);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for contructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(9, keywords.size());
    assertTrue(keywords.contains("minimal generating sets"));
    assertTrue(keywords.contains("linear diophantine equations"));
    assertTrue(keywords.contains("minimal supporting set"));
    assertTrue(keywords.contains("minimal set"));
    assertTrue(keywords.contains("linear constraints"));
    assertTrue(keywords.contains("natural numbers"));
    assertTrue(keywords.contains("strict inequations"));
    assertTrue(keywords.contains("nonstrict inequations"));
    assertTrue(keywords.contains("upper bounds"));

    ae.destroy();
  }

  @Test
  public void testBuzzwords()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            true);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for contructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(9, keywords.size());
    assertTrue(keywords.contains("minimal generating sets"));
    assertTrue(keywords.contains("linear diophantine equations"));
    assertTrue(keywords.contains("minimal supporting set"));
    assertTrue(keywords.contains("minimal set"));
    assertTrue(keywords.contains("linear constraints"));
    assertTrue(keywords.contains("natural numbers"));
    assertTrue(keywords.contains("strict inequations"));
    assertTrue(keywords.contains("nonstrict inequations"));
    assertTrue(keywords.contains("upper bounds"));

    assertEquals(9, JCasUtil.select(jCas, Buzzword.class).size());
    assertEquals(
        "linear constraints", JCasUtil.selectByIndex(jCas, Buzzword.class, 0).getCoveredText());
    assertEquals("keyword", JCasUtil.selectByIndex(jCas, Buzzword.class, 0).getTags(0));

    assertEquals(
        "natural numbers", JCasUtil.selectByIndex(jCas, Buzzword.class, 1).getCoveredText());
    assertEquals(
        "linear Diophantine equations",
        JCasUtil.selectByIndex(jCas, Buzzword.class, 2).getCoveredText());
    assertEquals(
        "strict inequations", JCasUtil.selectByIndex(jCas, Buzzword.class, 3).getCoveredText());
    assertEquals(
        "nonstrict inequations", JCasUtil.selectByIndex(jCas, Buzzword.class, 4).getCoveredText());
    assertEquals("Upper bounds", JCasUtil.selectByIndex(jCas, Buzzword.class, 5).getCoveredText());
    assertEquals("minimal set", JCasUtil.selectByIndex(jCas, Buzzword.class, 6).getCoveredText());
    assertEquals(
        "minimal generating sets",
        JCasUtil.selectByIndex(jCas, Buzzword.class, 7).getCoveredText());
    assertEquals(
        "minimal supporting set", JCasUtil.selectByIndex(jCas, Buzzword.class, 8).getCoveredText());

    ae.destroy();
  }

  @Test
  public void testMaxNumber()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            3,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            false);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for contructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(3, keywords.size());
    assertTrue(keywords.contains("minimal generating sets"));
    assertTrue(keywords.contains("linear diophantine equations"));
    assertTrue(keywords.contains("minimal supporting set"));

    ae.destroy();
  }

  @Test
  public void testCharacters()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            false);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        "Thursday 28th January - Test Report\n\n\tMichelle was seen meeting Katie at the Diner (Mary's Diner, on Main Street), at approximately 6:00pm. Michelle was later seen to be leaving the Diner, carrying a black folder of unknown contents. Katie is a known sympathiser, and it is hypothesised that she passed training materials to Michelle. When questioned later, Michelle stated: \"I know nothing of any training materials/folder!\".");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    for (String keyword : keywords) {
      assertEquals("", keyword.replaceAll("[a-z0-9 ]", ""));
    }

    ae.destroy();
  }

  @Test
  public void testFoxStoplist()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            false,
            RakeKeywords.PARAM_STOPLIST,
            SharedStopwordResource.StopwordList.FOX);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for contructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(8, keywords.size());
    assertTrue(keywords.contains("minimal generating sets"));
    assertTrue(keywords.contains("linear diophantine equations"));
    assertTrue(keywords.contains("minimal supporting set"));
    assertTrue(keywords.contains("minimal set"));
    assertTrue(keywords.contains("linear constraints"));
    assertTrue(keywords.contains("strict inequations"));
    assertTrue(keywords.contains("nonstrict inequations"));
    assertTrue(keywords.contains("upper bounds"));

    ae.destroy();
  }

  @Test
  public void testCustomStoplist()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            false,
            RakeKeywords.PARAM_STOPLIST,
            getClass().getResource("exampleStoplist.txt").getPath());

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText("Bill and Ben went off to the shops in London town.");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(1, keywords.size());
    assertTrue(keywords.contains("london town"));

    ae.destroy();
  }

  @Test
  public void testStemmer() throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            true,
            RakeKeywords.PARAM_STEMMING,
            SnowballStemmer.ALGORITHM.ENGLISH);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for contructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(11, keywords.size());
    assertTrue(keywords.contains("minimal generating sets"));
    assertTrue(keywords.contains("linear diophantine equations"));
    assertTrue(keywords.contains("minimal supporting set"));
    assertTrue(keywords.contains("minimal set"));
    assertTrue(keywords.contains("linear constraints"));
    assertTrue(keywords.contains("natural numbers"));
    assertTrue(keywords.contains("strict inequations"));
    assertTrue(keywords.contains("nonstrict inequations"));
    assertTrue(keywords.contains("upper bounds"));
    assertTrue(keywords.contains("considered types"));
    assertTrue(keywords.contains("mixed types"));

    assertEquals(11, JCasUtil.select(jCas, Buzzword.class).size());
    assertEquals(
        "linear constraints", JCasUtil.selectByIndex(jCas, Buzzword.class, 0).getCoveredText());
    assertEquals("keyword", JCasUtil.selectByIndex(jCas, Buzzword.class, 0).getTags(0));

    assertEquals(
        "natural numbers", JCasUtil.selectByIndex(jCas, Buzzword.class, 1).getCoveredText());
    assertEquals(
        "linear Diophantine equations",
        JCasUtil.selectByIndex(jCas, Buzzword.class, 2).getCoveredText());
    assertEquals(
        "strict inequations", JCasUtil.selectByIndex(jCas, Buzzword.class, 3).getCoveredText());
    assertEquals(
        "nonstrict inequations", JCasUtil.selectByIndex(jCas, Buzzword.class, 4).getCoveredText());
    assertEquals("Upper bounds", JCasUtil.selectByIndex(jCas, Buzzword.class, 5).getCoveredText());
    assertEquals("minimal set", JCasUtil.selectByIndex(jCas, Buzzword.class, 6).getCoveredText());
    assertEquals(
        "minimal generating sets",
        JCasUtil.selectByIndex(jCas, Buzzword.class, 7).getCoveredText());
    assertEquals(
        "minimal supporting set", JCasUtil.selectByIndex(jCas, Buzzword.class, 8).getCoveredText());
    assertEquals(
        "considered types", JCasUtil.selectByIndex(jCas, Buzzword.class, 9).getCoveredText());
    assertEquals("mixed types", JCasUtil.selectByIndex(jCas, Buzzword.class, 10).getCoveredText());

    ae.destroy();
  }

  @Test
  public void testBadStemmer()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            false,
            RakeKeywords.PARAM_STEMMING,
            "NotARealStemmer");

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for contructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.");
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Set<String> keywords = metadata.findAll(KEYWORD_METADATA_KEY);

    assertEquals(9, keywords.size());
    assertTrue(keywords.contains("minimal generating sets"));
    assertTrue(keywords.contains("linear diophantine equations"));
    assertTrue(keywords.contains("minimal supporting set"));
    assertTrue(keywords.contains("minimal set"));
    assertTrue(keywords.contains("linear constraints"));
    assertTrue(keywords.contains("natural numbers"));
    assertTrue(keywords.contains("strict inequations"));
    assertTrue(keywords.contains("nonstrict inequations"));
    assertTrue(keywords.contains("upper bounds"));

    ae.destroy();
  }

  @Test
  public void testLongDocument() throws Exception {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            true);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        new String(Files.readAllBytes(Paths.get(getClass().getResource("turing.txt").toURI()))));
    ae.process(jCas);

    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> keyword = metadata.find(KEYWORD_METADATA_KEY);

    assertTrue(keyword.isPresent());

    ae.destroy();
  }

  @Test
  public void testLongDocumentWithText() throws Exception {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            STOPWORDS, SharedStopwordResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            RakeKeywords.class,
            STOPWORDS,
            erd,
            RakeKeywords.PARAM_MAX_KEYWORDS,
            12,
            RakeKeywords.PARAM_ADD_BUZZWORDS,
            true);

    AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);

    jCas.setDocumentText(
        new String(Files.readAllBytes(Paths.get(getClass().getResource("turing.txt").toURI()))));
    ae.process(jCas);

    JCasMetadata metadata1 = new JCasMetadata(jCas);
    Set<String> fullDocKeywords = metadata1.findAll(KEYWORD_METADATA_KEY);

    jCas.reset();

    jCas.setDocumentText(
        new String(Files.readAllBytes(Paths.get(getClass().getResource("turing.txt").toURI()))));
    new Text(jCas, 0, 250).addToIndexes();
    ae.process(jCas);

    JCasMetadata metadata2 = new JCasMetadata(jCas);
    Set<String> textKeywords = metadata2.findAll(KEYWORD_METADATA_KEY);
    assertFalse(textKeywords.isEmpty());
    assertNotEquals(fullDocKeywords, textKeywords);

    ae.destroy();
  }
}
