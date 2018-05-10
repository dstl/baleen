// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static uk.gov.dstl.baleen.annotators.language.MaltParser.ROOT;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.clulab.processors.Document;
import org.clulab.processors.Sentence;
import org.clulab.struct.DirectedGraph;

import scala.Int;
import scala.Option;
import scala.Tuple3;
import scala.collection.JavaConverters;

import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;

/** Class the converts the {@link Document} details to {@link JCas} elements */
public class DocumentConverter {

  private static final String MISSING_VALUE = OdinSentence.MISSING_VALUE;

  private final JCas jCas;
  private final Document document;

  /**
   * Construct the document converter
   *
   * @param jCas to convert element to
   * @param document to convert elements from
   */
  public DocumentConverter(JCas jCas, Document document) {
    this.jCas = jCas;
    this.document = document;
  }

  /** convert the document elements to JCas elements */
  public void convert() {
    for (Sentence sentence : document.sentences()) {
      convert(sentence);
    }
  }

  private void convert(Sentence sentence) {
    createSentence(sentence);
    List<WordToken> tokens = createWordTokens(sentence);
    createDependencies(sentence, tokens);
  }

  private void createDependencies(Sentence sentence, List<WordToken> tokens) {
    Option<DirectedGraph<String>> dependencies = sentence.dependencies();
    if (dependencies.isDefined()) {
      DirectedGraph<String> directedGraph = dependencies.get();
      JavaConverters.asJavaCollection(directedGraph.roots()).forEach(r -> createRoot(tokens, r));
      JavaConverters.asJavaCollection(directedGraph.allEdges())
          .forEach(edge -> createEdge(tokens, edge));
    }
  }

  private void createEdge(List<WordToken> tokens, Tuple3<Object, Object, String> edge) {
    WordToken govenor = tokens.get(getInt(edge._1()));
    WordToken dependent = tokens.get(getInt(edge._2()));
    String type = edge._3();
    createdependency(govenor, dependent, type);
  }

  private void createRoot(List<WordToken> tokens, Object r) {
    WordToken root = tokens.get(getInt(r));
    createdependency(root, root, ROOT);
  }

  private void createdependency(WordToken govenor, WordToken dependent, String type) {
    Dependency dependency = new Dependency(jCas);
    dependency.setBegin(dependent.getBegin());
    dependency.setEnd(dependent.getEnd());
    dependency.setGovernor(govenor);
    dependency.setDependent(dependent);
    dependency.setDependencyType(type);
    dependency.addToIndexes();
  }

  private int getInt(Object r) {
    if (r instanceof Int) {
      return ((Int) r).toInt();
    }
    if (r instanceof Integer) {
      return ((Integer) r).intValue();
    } else {
      throw new IllegalStateException("dependency value must be an int of some form");
    }
  }

  private List<WordToken> createWordTokens(Sentence sentence) {
    String[] words = sentence.words();
    List<WordToken> tokens = new ArrayList<>();
    for (int i = 0; i < words.length; i++) {
      tokens.add(createWordToken(sentence, i));
    }
    return tokens;
  }

  private WordToken createWordToken(Sentence sentence, int i) {
    WordToken wordToken = new WordToken(jCas);
    wordToken.setBegin(sentence.startOffsets()[i]);
    wordToken.setEnd(sentence.endOffsets()[i]);
    Option<String[]> tags = sentence.tags();
    if (tags.isDefined()) {
      wordToken.setPartOfSpeech(tags.get()[i]);
    }
    Option<String[]> lemmas = sentence.lemmas();
    if (lemmas.isDefined() && !MISSING_VALUE.equals(lemmas.get()[i])) {
      wordToken.setLemmas(new FSArray(jCas, 1));
      WordLemma lemma = new WordLemma(jCas);
      lemma.setLemmaForm(lemmas.get()[i]);
      if (tags.isDefined()) {
        lemma.setPartOfSpeech(tags.get()[i]);
      }
      lemma.addToIndexes();
      wordToken.setLemmas(0, lemma);
    } else {
      wordToken.setLemmas(new FSArray(jCas, 0));
    }
    wordToken.addToIndexes();
    return wordToken;
  }

  private void createSentence(Sentence sentence) {
    uk.gov.dstl.baleen.types.language.Sentence s =
        new uk.gov.dstl.baleen.types.language.Sentence(jCas);
    s.setBegin(sentence.startOffsets()[0]);
    int[] endOffsets = sentence.endOffsets();
    s.setEnd(endOffsets[endOffsets.length - 1]);
    s.addToIndexes();
  }
}
