// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.relations;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class DependencyRelationshipTest extends AbstractAnnotatorTest {

  private static final String SENTENCE = "Ben visited London and Paris.";

  public DependencyRelationshipTest() {
    super(DependencyRelationshipAnnotator.class);
  }

  @Test
  public void testSentenceGivesTwoRelations()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE);

    final Person person = Annotations.createPerson(jCas, 0, 3, "Ben");
    final Location london = Annotations.createLocation(jCas, 12, 18, "London", "");
    final Location paris = Annotations.createLocation(jCas, 23, 28, "Paris", "");

    final Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(jCas.getDocumentText().length());
    s1.addToIndexes();

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addAnnotations();

    processJCas();

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    Assert.assertEquals(3, relations.size());
    final Relation r1 = findRelationBetween(relations, person, london);

    assertEquals("", r1.getRelationshipType());
    assertEquals("visited", r1.getValue());
    assertEquals(0, r1.getSentenceDistance());
    assertEquals(1, r1.getWordDistance());
    assertEquals(1, r1.getDependencyDistance());

    final Relation r2 = findRelationBetween(relations, person, paris);

    assertEquals("", r2.getRelationshipType());
    assertEquals("visited London", r2.getValue()); // Not great, but is the shortest dependency path
    assertEquals(0, r2.getSentenceDistance());
    assertEquals(3, r2.getWordDistance());
    assertEquals(2, r2.getDependencyDistance());
  }

  private void addAnnotations() {
    WordToken wt0 = new WordToken(jCas);
    wt0.setBegin(0);
    wt0.setEnd(3);
    wt0.setPartOfSpeech("NNP");
    wt0.setSentenceOrder(0);
    wt0.addToIndexes(jCas);

    WordToken wt1 = new WordToken(jCas);
    wt1.setBegin(4);
    wt1.setEnd(11);
    wt1.setPartOfSpeech("VBD");
    wt1.setSentenceOrder(1);
    wt1.addToIndexes(jCas);

    WordToken wt2 = new WordToken(jCas);
    wt2.setBegin(12);
    wt2.setEnd(18);
    wt2.setPartOfSpeech("NNP");
    wt2.setSentenceOrder(2);
    wt2.addToIndexes(jCas);

    WordToken wt3 = new WordToken(jCas);
    wt3.setBegin(19);
    wt3.setEnd(22);
    wt3.setPartOfSpeech("CC");
    wt3.setSentenceOrder(3);
    wt3.addToIndexes(jCas);

    WordToken wt4 = new WordToken(jCas);
    wt4.setBegin(23);
    wt4.setEnd(28);
    wt4.setPartOfSpeech("NNP");
    wt4.setSentenceOrder(4);
    wt4.addToIndexes(jCas);

    WordToken wt5 = new WordToken(jCas);
    wt5.setBegin(28);
    wt5.setEnd(29);
    wt5.setPartOfSpeech(".");
    wt5.setSentenceOrder(5);
    wt5.addToIndexes(jCas);

    Dependency d1 = new Dependency(jCas);
    d1.setBegin(0);
    d1.setEnd(3);
    d1.setGovernor(wt1);
    d1.setDependent(wt0);
    d1.setDependencyType("nsubj");
    d1.addToIndexes(jCas);

    Dependency d2 = new Dependency(jCas);
    d2.setBegin(4);
    d2.setEnd(11);
    d2.setGovernor(wt1);
    d2.setDependencyType("ROOT");
    d2.addToIndexes(jCas);

    Dependency d3 = new Dependency(jCas);
    d3.setBegin(12);
    d3.setEnd(18);
    d3.setGovernor(wt1);
    d3.setDependent(wt2);
    d3.setDependencyType("dobj");
    d3.addToIndexes(jCas);

    Dependency d4 = new Dependency(jCas);
    d4.setBegin(19);
    d4.setEnd(22);
    d4.setGovernor(wt2);
    d4.setDependent(wt3);
    d4.setDependencyType("cc");
    d4.addToIndexes(jCas);

    Dependency d5 = new Dependency(jCas);
    d5.setBegin(23);
    d5.setEnd(28);
    d5.setGovernor(wt2);
    d5.setDependent(wt4);
    d5.setDependencyType("conj");
    d5.addToIndexes(jCas);

    Dependency d6 = new Dependency(jCas);
    d6.setBegin(28);
    d6.setEnd(29);
    d6.setGovernor(wt1);
    d6.setDependent(wt5);
    d6.setDependencyType("punct");
    d6.addToIndexes(jCas);
  }

  private Relation findRelationBetween(
      final List<Relation> relations, final Entity e1, final Entity e2) {
    return relations.stream()
        .filter(r -> r.getSource().equals(e1) && r.getTarget().equals(e2))
        .findFirst()
        .get();
  }
}
