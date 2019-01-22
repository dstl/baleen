// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.language;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;

public class OdinParserTest extends AbstractAnnotatorTest {

  public OdinParserTest() {
    super(OdinParser.class);
  }

  @Test
  public void testParses() throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("This is a test. We should get at least 2 sentences.");
    processJCas();

    Collection<Sentence> select = JCasUtil.select(jCas, Sentence.class);

    assertEquals(2, select.size());

    Sentence sentence = select.iterator().next();
    assertEquals(0, sentence.getBegin());
    assertEquals(15, sentence.getEnd());

    List<WordToken> tokens =
        ImmutableList.copyOf(JCasUtil.selectCovered(WordToken.class, sentence));
    assertEquals(5, tokens.size());

    Iterator<WordToken> iterator = tokens.iterator();
    WordToken next = iterator.next();
    assertEquals(0, next.getBegin());
    assertEquals(4, next.getEnd());
    assertEquals("DT", next.getPartOfSpeech());
    assertEquals(1, next.getLemmas().size());
    assertEquals("DT", next.getLemmas(0).getPartOfSpeech());
    assertEquals("this", next.getLemmas(0).getLemmaForm());
    next = iterator.next();
    assertEquals(5, next.getBegin());
    assertEquals(7, next.getEnd());
    assertEquals("VBZ", next.getPartOfSpeech());
    assertEquals(1, next.getLemmas().size());
    assertEquals("VBZ", next.getLemmas(0).getPartOfSpeech());
    assertEquals("be", next.getLemmas(0).getLemmaForm());
    next = iterator.next();
    assertEquals(8, next.getBegin());
    assertEquals(9, next.getEnd());
    assertEquals("DT", next.getPartOfSpeech());
    assertEquals(1, next.getLemmas().size());
    assertEquals("DT", next.getLemmas(0).getPartOfSpeech());
    assertEquals("a", next.getLemmas(0).getLemmaForm());
    next = iterator.next();
    assertEquals(10, next.getBegin());
    assertEquals(14, next.getEnd());
    assertEquals("NN", next.getPartOfSpeech());
    assertEquals(1, next.getLemmas().size());
    assertEquals("NN", next.getLemmas(0).getPartOfSpeech());
    assertEquals("test", next.getLemmas(0).getLemmaForm());
    next = iterator.next();
    assertEquals(14, next.getBegin());
    assertEquals(15, next.getEnd());
    assertEquals(".", next.getPartOfSpeech());
    assertEquals(".", next.getLemmas(0).getPartOfSpeech());
    assertEquals(".", next.getLemmas(0).getLemmaForm());
    assertEquals(1, next.getLemmas().size());

    Collection<Dependency> dependencies = JCasUtil.selectCovered(Dependency.class, sentence);

    assertEquals(5, dependencies.size());
    Iterator<Dependency> dependencyIterator = dependencies.iterator();
    Dependency nextDependency = dependencyIterator.next();
    assertEquals("nsubj", nextDependency.getDependencyType());
    assertEquals(tokens.get(3), nextDependency.getGovernor());
    assertEquals(tokens.get(0), nextDependency.getDependent());
    nextDependency = dependencyIterator.next();
    assertEquals("cop", nextDependency.getDependencyType());
    assertEquals(tokens.get(3), nextDependency.getGovernor());
    assertEquals(tokens.get(1), nextDependency.getDependent());
    nextDependency = dependencyIterator.next();
    assertEquals("det", nextDependency.getDependencyType());
    assertEquals(tokens.get(3), nextDependency.getGovernor());
    assertEquals(tokens.get(2), nextDependency.getDependent());
    nextDependency = dependencyIterator.next();
    assertEquals(MaltParser.ROOT, nextDependency.getDependencyType());
    assertEquals(tokens.get(3), nextDependency.getGovernor());
    assertEquals(tokens.get(3), nextDependency.getDependent());
    nextDependency = dependencyIterator.next();
    assertEquals("punct", nextDependency.getDependencyType());
    assertEquals(tokens.get(3), nextDependency.getGovernor());
    assertEquals(tokens.get(4), nextDependency.getDependent());
  }
}
