// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.odin.SentenceFactory.MISSING_VALUE;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.clulab.processors.Sentence;
import org.clulab.struct.DirectedGraph;
import org.clulab.struct.GraphMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import scala.Option;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;

@RunWith(MockitoJUnitRunner.class)
public class SentenceFactoryTest {

  @Mock SentenceFactory sentenceFactory;
  private WordToken wt1;
  private WordToken wt2;
  private WordToken wt3;
  private WordToken wt4;
  private WordToken wt5;

  @Test
  public void canConstruct() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    assertNotNull(new SentenceFactory(jCas));
  }

  @Test
  public void canCreateSentences() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    SentenceFactory factory = new SentenceFactory(jCas);

    List<OdinSentence> sentences = factory.create();
    assertNotNull(sentences);
  }

  private void addWordTokens(JCas jCas) {
    jCas.setDocumentText("This is a sentence.");
    uk.gov.dstl.baleen.types.language.Sentence s1 =
        new uk.gov.dstl.baleen.types.language.Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(19);
    s1.addToIndexes();

    wt1 = new WordToken(jCas);
    wt1.setBegin(0);
    wt1.setEnd(4);
    wt1.setPartOfSpeech("DT");
    wt1.addToIndexes();

    wt2 = new WordToken(jCas);
    wt2.setBegin(5);
    wt2.setEnd(7);
    wt2.setPartOfSpeech("VBZ");
    wt2.addToIndexes();

    wt3 = new WordToken(jCas);
    wt3.setBegin(8);
    wt3.setEnd(9);
    wt3.setPartOfSpeech("DT");
    wt3.addToIndexes();

    WordLemma lemma = new WordLemma(jCas);
    lemma.setBegin(10);
    lemma.setEnd(18);
    lemma.setLemmaForm("lemma");
    lemma.addToIndexes();
    wt4 = new WordToken(jCas);
    wt4.setBegin(10);
    wt4.setEnd(18);
    wt4.setPartOfSpeech("NN");
    wt4.setLemmas(new FSArray(jCas, 1));
    wt4.setLemmas(0, lemma);
    wt4.addToIndexes();

    wt5 = new WordToken(jCas);
    wt5.setBegin(18);
    wt5.setEnd(19);
    wt5.setPartOfSpeech(".");
    wt5.addToIndexes();
  }

  @Test
  public void canCreateSentenceFromAnnotations() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();

    addWordTokens(jCas);

    SentenceFactory factory = new SentenceFactory(jCas);

    List<OdinSentence> sentences = factory.create();
    assertEquals(1, sentences.size());
    Sentence sentence = sentences.get(0);

    assertEquals(
        ImmutableSet.of("This", "is", "a", "sentence", "."), ImmutableSet.copyOf(sentence.words()));

    assertTrue(Arrays.equals(new int[] {0, 5, 8, 10, 18}, sentence.startOffsets()));
    assertTrue(Arrays.equals(new int[] {4, 7, 9, 18, 19}, sentence.endOffsets()));

    assertEquals(
        ImmutableSet.of("this", "is", "a", "lemma", "."),
        ImmutableSet.copyOf(sentence.lemmas().get()));
    assertEquals(
        ImmutableSet.of("DT", "VBZ", "DT", "NN", "."), ImmutableSet.copyOf(sentence.tags().get()));
  }

  @Test
  public void canCreateSentenceWithEntities() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();

    addWordTokens(jCas);

    Organisation organisation = Annotations.createOrganisation(jCas, 5, 7, "test");
    organisation.setIsNormalised(true);
    Annotations.createPerson(jCas, 10, 18, "sentence");

    SentenceFactory factory = new SentenceFactory(jCas);

    Sentence sentence = factory.create().get(0);

    assertEquals(
        ImmutableSet.of(
            MISSING_VALUE,
            Organisation.class.getSimpleName(),
            MISSING_VALUE,
            Person.class.getSimpleName(),
            MISSING_VALUE),
        ImmutableSet.copyOf(sentence.entities().get()));

    assertEquals(
        ImmutableSet.of(MISSING_VALUE, "test", MISSING_VALUE, MISSING_VALUE, MISSING_VALUE),
        ImmutableSet.copyOf(sentence.norms().get()));
  }

  @Test
  public void canCreateSentenceWithPhraseChunks() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();

    addWordTokens(jCas);

    String chunk = "CHUNK";

    PhraseChunk c1 = new PhraseChunk(jCas);
    c1.setBegin(0);
    c1.setEnd(9);
    c1.setChunkType(chunk);
    c1.addToIndexes();
    SentenceFactory factory = new SentenceFactory(jCas);

    Sentence sentence = factory.create().get(0);

    assertEquals(
        ImmutableSet.of(chunk, chunk, chunk, MISSING_VALUE, MISSING_VALUE, MISSING_VALUE),
        ImmutableSet.copyOf(sentence.chunks().get()));
  }

  @Test
  public void canCreateSentenceWithDependencies() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();

    addWordTokens(jCas);

    Annotations.createDependency(jCas, wt4, wt4, MaltParser.ROOT);
    Annotations.createDependency(jCas, wt4, wt1, "nsubj");
    Annotations.createDependency(jCas, wt4, wt2, "cop");
    Annotations.createDependency(jCas, wt4, wt3, "det");
    Annotations.createDependency(jCas, wt4, wt5, "punct");

    SentenceFactory factory = new SentenceFactory(jCas);

    Sentence sentence = factory.create().get(0);

    Option<DirectedGraph<String>> dependencies = sentence.dependencies();
    assertTrue(dependencies.isDefined());
    GraphMap graphs = sentence.graphs();
    assertNotNull(graphs);
    Option<DirectedGraph<String>> graph = sentence.dependencies();

    assertTrue(graph.isDefined());
    DirectedGraph<String> directedGraph = graph.get();
    assertTrue(directedGraph.hasEdge(3, 3, "root"));
    assertTrue(directedGraph.hasEdge(3, 0, "nsubj"));
    assertTrue(directedGraph.hasEdge(3, 1, "cop"));
    assertTrue(directedGraph.hasEdge(3, 2, "det"));
    assertTrue(directedGraph.hasEdge(3, 4, "punct"));
  }

  @Test
  public void canCreateMultipleSentenceWithOffsets() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("This is the first sentence. This is the second.");
    Annotations.createWordTokens(jCas);
    Annotations.createSentences(jCas);
    SentenceFactory factory = new SentenceFactory(jCas);

    List<OdinSentence> sentences = factory.create();
    Sentence sentence1 = sentences.get(0);
    Sentence sentence2 = sentences.get(1);

    assertTrue(
        Arrays.toString(sentence1.startOffsets()),
        Arrays.equals(new int[] {0, 5, 8, 12, 18, 26}, sentence1.startOffsets()));
    assertTrue(
        Arrays.toString(sentence1.endOffsets()),
        Arrays.equals(new int[] {4, 7, 11, 17, 26, 27}, sentence1.endOffsets()));
    assertTrue(
        Arrays.toString(sentence2.startOffsets()),
        Arrays.equals(new int[] {28, 33, 36, 40, 46}, sentence2.startOffsets()));
    assertTrue(
        Arrays.toString(sentence2.endOffsets()),
        Arrays.equals(new int[] {32, 35, 39, 46, 47}, sentence2.endOffsets()));
  }
}
