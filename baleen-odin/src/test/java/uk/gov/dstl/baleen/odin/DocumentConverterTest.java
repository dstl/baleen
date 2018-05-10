// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static uk.gov.dstl.baleen.odin.SentenceFactory.MISSING_VALUE;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.clulab.processors.Document;
import org.clulab.processors.Sentence;
import org.clulab.struct.DirectedGraph;
import org.clulab.struct.Edge;
import org.clulab.struct.GraphMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import scala.Some;
import scala.collection.JavaConversions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.WordToken;

@RunWith(MockitoJUnitRunner.class)
public class DocumentConverterTest {

  @Mock private Document document;

  private Sentence sentence;

  private JCas jCas;

  @Test
  public void canConstruct() throws UIMAException {
    assertNotNull(new DocumentConverter(JCasFactory.createJCas(), document));
  }

  @Before
  public void setUp() throws UIMAException {

    jCas = JCasFactory.createJCas();
    jCas.setDocumentText("This is a test.");

    String[] words = {"This", "is", "a", "test", "."};
    int[] startOffsets = {0, 5, 8, 10, 14};
    int[] endOffsets = {4, 7, 9, 14, 15};
    sentence = new Sentence(words, startOffsets, endOffsets);
  }

  @Test
  public void canConvertSentence() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("This is a test. This is another test.");

    Sentence sentence2 =
        new Sentence(
            new String[] {"This", "is", "another", "test", "."},
            new int[] {16, 21, 24, 31, 35},
            new int[] {20, 23, 30, 34, 36});
    when(document.sentences()).thenReturn(new Sentence[] {sentence, sentence2});
    DocumentConverter converter = new DocumentConverter(jCas, document);
    converter.convert();

    Collection<uk.gov.dstl.baleen.types.language.Sentence> actual =
        JCasUtil.select(jCas, uk.gov.dstl.baleen.types.language.Sentence.class);
    assertEquals(2, actual.size());
    Iterator<uk.gov.dstl.baleen.types.language.Sentence> iterator = actual.iterator();

    uk.gov.dstl.baleen.types.language.Sentence next = iterator.next();
    assertEquals(0, next.getBegin());
    assertEquals(15, next.getEnd());
    next = iterator.next();
    assertEquals(16, next.getBegin());
    assertEquals(36, next.getEnd());
  }

  @Test
  public void canConvertWordTokens() {
    when(document.sentences()).thenReturn(new Sentence[] {sentence});
    DocumentConverter converter = new DocumentConverter(jCas, document);
    converter.convert();

    Collection<WordToken> actual = JCasUtil.select(jCas, WordToken.class);
    assertEquals(5, actual.size());
    Iterator<WordToken> iterator = actual.iterator();
    WordToken next = iterator.next();
    assertEquals(0, next.getBegin());
    assertEquals(4, next.getEnd());
    next = iterator.next();
    assertEquals(5, next.getBegin());
    assertEquals(7, next.getEnd());
    next = iterator.next();
    assertEquals(8, next.getBegin());
    assertEquals(9, next.getEnd());
    next = iterator.next();
    assertEquals(10, next.getBegin());
    assertEquals(14, next.getEnd());
    next = iterator.next();
    assertEquals(14, next.getBegin());
    assertEquals(15, next.getEnd());
  }

  @Test
  public void canConvertPOS() {

    String[] tags = {"DT", "VBZ", "DT", "NN", "."};
    sentence.tags_$eq(Some.apply(tags));
    when(document.sentences()).thenReturn(new Sentence[] {sentence});

    DocumentConverter converter = new DocumentConverter(jCas, document);
    converter.convert();

    Collection<WordToken> actual = JCasUtil.select(jCas, WordToken.class);
    assertEquals(5, actual.size());
    Iterator<WordToken> iterator = actual.iterator();
    WordToken next = iterator.next();
    assertEquals("DT", next.getPartOfSpeech());
    next = iterator.next();
    assertEquals("VBZ", next.getPartOfSpeech());
    next = iterator.next();
    assertEquals("DT", next.getPartOfSpeech());
    next = iterator.next();
    assertEquals("NN", next.getPartOfSpeech());
    next = iterator.next();
    assertEquals(".", next.getPartOfSpeech());
  }

  @Test
  public void canConvertLemmas() {

    String[] lemma = {MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, "lemma", MISSING_VALUE};
    sentence.lemmas_$eq(Some.apply(lemma));
    when(document.sentences()).thenReturn(new Sentence[] {sentence});

    DocumentConverter converter = new DocumentConverter(jCas, document);
    converter.convert();

    Collection<WordToken> actual = JCasUtil.select(jCas, WordToken.class);
    assertEquals(5, actual.size());
    Iterator<WordToken> iterator = actual.iterator();
    WordToken next = iterator.next();
    assertEquals(0, next.getLemmas().size());
    next = iterator.next();
    assertEquals(0, next.getLemmas().size());
    next = iterator.next();
    assertEquals(0, next.getLemmas().size());
    next = iterator.next();
    assertEquals(1, next.getLemmas().size());
    assertEquals("lemma", next.getLemmas(0).getLemmaForm());
    next = iterator.next();
    assertEquals(0, next.getLemmas().size());
  }

  @Test
  public void canConvertLemmasWithTags() {

    String[] lemma = {MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, "lemma", MISSING_VALUE};
    sentence.lemmas_$eq(Some.apply(lemma));
    String[] tags = {"DT", "VBZ", "DT", "NN", "."};
    sentence.tags_$eq(Some.apply(tags));
    when(document.sentences()).thenReturn(new Sentence[] {sentence});

    DocumentConverter converter = new DocumentConverter(jCas, document);
    converter.convert();

    Collection<WordToken> actual = JCasUtil.select(jCas, WordToken.class);
    assertEquals(5, actual.size());
    Iterator<WordToken> iterator = actual.iterator();
    WordToken next = iterator.next();
    assertEquals(0, next.getLemmas().size());
    next = iterator.next();
    assertEquals(0, next.getLemmas().size());
    next = iterator.next();
    assertEquals(0, next.getLemmas().size());
    next = iterator.next();
    assertEquals(1, next.getLemmas().size());
    assertEquals("NN", next.getLemmas(0).getPartOfSpeech());
    next = iterator.next();
    assertEquals(0, next.getLemmas().size());
  }

  @Test
  public void canConvertDependencies() throws UIMAException {

    String[] tags = {"DT", "VBZ", "DT", "NN", "."};
    sentence.tags_$eq(Some.apply(tags));

    Set<Object> roots = ImmutableSet.of(3);
    // @formatter:off
    ImmutableList<Edge<String>> edges =
        ImmutableList.of(
            new Edge<>(3, 0, "nsubj"),
            new Edge<>(3, 1, "cop"),
            new Edge<>(3, 2, "det"),
            new Edge<>(3, 4, "punct"));
    // @formatter:on

    DirectedGraph<String> deps =
        new DirectedGraph<>(
            JavaConversions.asScalaBuffer(edges).toList(),
            JavaConversions.asScalaSet(roots).toSet());
    sentence.setDependencies(GraphMap.UNIVERSAL_ENHANCED(), deps);
    when(document.sentences()).thenReturn(new Sentence[] {sentence});

    DocumentConverter converter = new DocumentConverter(jCas, document);
    converter.convert();

    List<WordToken> tokens = ImmutableList.copyOf(JCasUtil.select(jCas, WordToken.class));
    Collection<Dependency> actual = JCasUtil.select(jCas, Dependency.class);
    assertEquals(5, actual.size());
    Iterator<Dependency> iterator = actual.iterator();
    Dependency next = iterator.next();
    assertEquals("nsubj", next.getDependencyType());
    assertEquals(tokens.get(3), next.getGovernor());
    assertEquals(tokens.get(0), next.getDependent());
    next = iterator.next();
    assertEquals("cop", next.getDependencyType());
    assertEquals(tokens.get(3), next.getGovernor());
    assertEquals(tokens.get(1), next.getDependent());
    next = iterator.next();
    assertEquals("det", next.getDependencyType());
    assertEquals(tokens.get(3), next.getGovernor());
    assertEquals(tokens.get(2), next.getDependent());
    next = iterator.next();
    assertEquals(MaltParser.ROOT, next.getDependencyType());
    assertEquals(tokens.get(3), next.getGovernor());
    assertEquals(tokens.get(3), next.getDependent());
    next = iterator.next();
    assertEquals("punct", next.getDependencyType());
    assertEquals(tokens.get(3), next.getGovernor());
    assertEquals(tokens.get(4), next.getDependent());
  }
}
