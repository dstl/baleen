// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.relations.SentenceRelationshipAnnotator.ValueStrategy;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class SentenceRelationshipTest extends AbstractAnnotatorTest {

  public SentenceRelationshipTest() {
    super(SentenceRelationshipAnnotator.class);
  }

  @Test
  public void testLanguageSentenceGivesRelation()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("Jon, at last, visits London.");

    final Person person = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Location location = Annotations.createLocation(jCas, 21, 27, "London", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addWordTokens();

    processJCas();

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    Assert.assertEquals(1, relations.size());
    final Relation r = relations.get(0);

    assertEquals(person, r.getSource());
    assertEquals(location, r.getTarget());
    assertEquals("", r.getRelationshipType());
    assertEquals("Jon, at last, visits London.", r.getValue());
    assertEquals(0, r.getSentenceDistance());
    assertEquals(5, r.getWordDistance());
    assertEquals(-1, r.getDependencyDistance());
  }

  @Test
  public void testTypeFiltering()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("Jon, at last, visits London.");

    Annotations.createPerson(jCas, 0, 3, "Jon");
    Annotations.createLocation(jCas, 21, 27, "London", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addWordTokens();
    processJCas(SentenceRelationshipAnnotator.PARAM_TYPE_NAMES, new String[] {"Person"});

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(0, relations.size());
  }

  @Test
  public void testStructureSentenceGivesRelation()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("Jon, at last, visits London.");

    final Person person = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Location location = Annotations.createLocation(jCas, 21, 27, "London", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addWordTokens();

    processJCas(SentenceRelationshipAnnotator.PARAM_VALUE_STRATEGY, ValueStrategy.BETWEEN);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(1, relations.size());
    final Relation r = relations.get(0);

    assertEquals(person, r.getSource());
    assertEquals(location, r.getTarget());
    assertEquals("", r.getRelationshipType());
    assertEquals(", at last, visits", r.getValue());
    assertEquals(0, r.getSentenceDistance());
    assertEquals(5, r.getWordDistance());
    assertEquals(-1, r.getDependencyDistance());
  }

  @Test
  public void testBothOnlyGivesOneRelationAndDependencyValue()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("Jon, at last, visits London.");

    final Person person = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Location location = Annotations.createLocation(jCas, 21, 27, "London", "");

    final Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(jCas.getDocumentText().length());
    s1.addToIndexes();

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addDependencyInformation();

    processJCas(SentenceRelationshipAnnotator.PARAM_VALUE_STRATEGY, ValueStrategy.DEPENDENCY);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    Assert.assertEquals(1, relations.size());
    final Relation r = relations.get(0);

    assertEquals(person, r.getSource());
    assertEquals(location, r.getTarget());
    assertEquals("", r.getRelationshipType());
    assertEquals("visits", r.getValue());
    assertEquals(0, r.getSentenceDistance());
    assertEquals(5, r.getWordDistance());
    assertEquals(1, r.getDependencyDistance());
  }

  private void addWordTokens() {
    WordToken wt0 = new WordToken(jCas);
    wt0.setBegin(0);
    wt0.setEnd(3);
    wt0.setPartOfSpeech("NNP");
    wt0.setSentenceOrder(0);
    wt0.addToIndexes(jCas);

    WordToken wt1 = new WordToken(jCas);

    wt1.setBegin(3);
    wt1.setEnd(4);
    wt1.setPartOfSpeech(",");
    wt1.setSentenceOrder(1);
    wt1.addToIndexes(jCas);

    WordToken wt2 = new WordToken(jCas);
    wt2.setBegin(5);
    wt2.setEnd(7);
    wt2.setPartOfSpeech("IN");
    wt2.setSentenceOrder(2);
    wt2.addToIndexes(jCas);

    WordToken wt3 = new WordToken(jCas);

    wt3.setBegin(8);
    wt3.setEnd(12);
    wt3.setPartOfSpeech("JJ");
    wt3.setSentenceOrder(3);
    wt3.addToIndexes(jCas);

    WordToken wt4 = new WordToken(jCas);
    wt4.setBegin(12);
    wt4.setEnd(13);
    wt4.setPartOfSpeech(",");
    wt4.setSentenceOrder(4);
    wt4.addToIndexes(jCas);

    WordToken wt5 = new WordToken(jCas);
    wt5.setBegin(14);
    wt5.setEnd(20);
    wt5.setPartOfSpeech("NNS");
    wt5.setSentenceOrder(5);
    wt5.addToIndexes(jCas);

    WordToken wt6 = new WordToken(jCas);
    wt6.setBegin(21);
    wt6.setEnd(27);
    wt6.setPartOfSpeech("NNP");
    wt6.setSentenceOrder(6);
    wt6.addToIndexes(jCas);

    WordToken wt7 = new WordToken(jCas);
    wt7.setBegin(27);
    wt7.setEnd(28);
    wt7.setPartOfSpeech(".");
    wt7.setSentenceOrder(7);
    wt7.addToIndexes(jCas);
  }

  private void addDependencyInformation() {
    WordToken wt0 = new WordToken(jCas);
    wt0.setBegin(0);
    wt0.setEnd(3);
    wt0.setPartOfSpeech("NNP");
    wt0.setSentenceOrder(0);
    wt0.addToIndexes(jCas);

    WordToken wt1 = new WordToken(jCas);

    wt1.setBegin(3);
    wt1.setEnd(4);
    wt1.setPartOfSpeech(",");
    wt1.setSentenceOrder(1);
    wt1.addToIndexes(jCas);

    WordToken wt2 = new WordToken(jCas);
    wt2.setBegin(5);
    wt2.setEnd(7);
    wt2.setPartOfSpeech("IN");
    wt2.setSentenceOrder(2);
    wt2.addToIndexes(jCas);

    WordToken wt3 = new WordToken(jCas);

    wt3.setBegin(8);
    wt3.setEnd(12);
    wt3.setPartOfSpeech("JJ");
    wt3.setSentenceOrder(3);
    wt3.addToIndexes(jCas);

    WordToken wt4 = new WordToken(jCas);
    wt4.setBegin(12);
    wt4.setEnd(13);
    wt4.setPartOfSpeech(",");
    wt4.setSentenceOrder(4);
    wt4.addToIndexes(jCas);

    WordToken wt5 = new WordToken(jCas);
    wt5.setBegin(14);
    wt5.setEnd(20);
    wt5.setPartOfSpeech("NNS");
    wt5.setSentenceOrder(5);
    wt5.addToIndexes(jCas);

    WordToken wt6 = new WordToken(jCas);
    wt6.setBegin(21);
    wt6.setEnd(27);
    wt6.setPartOfSpeech("NNP");
    wt6.setSentenceOrder(6);
    wt6.addToIndexes(jCas);

    WordToken wt7 = new WordToken(jCas);
    wt7.setBegin(27);
    wt7.setEnd(28);
    wt7.setPartOfSpeech(".");
    wt7.setSentenceOrder(7);
    wt7.addToIndexes(jCas);

    Dependency d1 = new Dependency(jCas);
    d1.setBegin(0);
    d1.setEnd(3);
    d1.setGovernor(wt0);
    d1.setDependencyType("ROOT");
    d1.addToIndexes(jCas);

    Dependency d2 = new Dependency(jCas);
    d2.setBegin(3);
    d2.setEnd(4);
    d2.setGovernor(wt0);
    d2.setDependent(wt1);
    d2.setDependencyType("punct");
    d2.addToIndexes(jCas);

    Dependency d3 = new Dependency(jCas);
    d3.setBegin(5);
    d3.setEnd(7);
    d3.setGovernor(wt3);
    d3.setDependent(wt2);
    d3.setDependencyType("case");
    d3.addToIndexes(jCas);

    Dependency d4 = new Dependency(jCas);
    d4.setBegin(8);
    d4.setEnd(12);
    d4.setGovernor(wt0);
    d4.setDependent(wt2);
    d4.setDependencyType("nmod");
    d4.addToIndexes(jCas);

    Dependency d5 = new Dependency(jCas);
    d5.setBegin(12);
    d5.setEnd(13);
    d5.setGovernor(wt0);
    d5.setDependent(wt3);
    d5.setDependencyType("punct");
    d5.addToIndexes(jCas);

    Dependency d6 = new Dependency(jCas);
    d6.setBegin(14);
    d6.setEnd(20);
    d6.setGovernor(wt0);
    d6.setDependent(wt5);
    d6.setDependencyType("appos");
    d6.addToIndexes(jCas);

    Dependency d7 = new Dependency(jCas);
    d7.setBegin(21);
    d7.setEnd(27);
    d7.setGovernor(wt5);
    d7.setDependent(wt6);
    d7.setDependencyType("vocative");
    d7.addToIndexes(jCas);

    Dependency d8 = new Dependency(jCas);
    d8.setBegin(27);
    d8.setEnd(28);
    d8.setGovernor(wt0);
    d8.setDependent(wt7);
    d8.setDependencyType("punct");
    d8.addToIndexes(jCas);
  }
}
