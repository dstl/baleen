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
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class DocumentRelationshipAnnotatorTest extends AbstractAnnotatorTest {

  private static final String SENTENCE_1 = "Jon went to London today.";

  private static final String SENTENCE_2 = "He met Steve on the train.";

  private static final String SENTENCE_3 = "They met Chris there.";

  public DocumentRelationshipAnnotatorTest() {
    super(DocumentRelationshipAnnotator.class);
  }

  @Test
  public void testDocumentRelations()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE_1 + " " + SENTENCE_2 + " " + SENTENCE_3);
    int s2Begin = SENTENCE_1.length() + 1;
    int s3Begin = s2Begin + SENTENCE_2.length() + 1;

    final Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(SENTENCE_1.length());
    s1.addToIndexes();
    final Sentence s2 = new Sentence(jCas);
    s2.setBegin(s2Begin);
    s2.setEnd(s2Begin + SENTENCE_2.length());
    s2.addToIndexes();
    final Sentence s3 = new Sentence(jCas);
    s3.setBegin(s3Begin);
    s3.setEnd(jCas.getDocumentText().length());
    s3.addToIndexes();

    final Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Person steve = Annotations.createPerson(jCas, s2Begin + 7, s2Begin + 12, "Steve");
    final Person chris = Annotations.createPerson(jCas, s3Begin + 9, s3Begin + 14, "Chris");
    final Location london = Annotations.createLocation(jCas, 12, 18, "London", "");

    processJCas();

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    Assert.assertEquals(5, relations.size());
    final Relation js = findRelationBetween(relations, jon, steve);
    final Relation ls = findRelationBetween(relations, london, steve);
    final Relation jc = findRelationBetween(relations, jon, chris);
    final Relation lc = findRelationBetween(relations, london, chris);
    final Relation sc = findRelationBetween(relations, steve, chris);

    assertEquals(1, js.getSentenceDistance());
    assertEquals(1, ls.getSentenceDistance());
    assertEquals(2, jc.getSentenceDistance());
    assertEquals(2, lc.getSentenceDistance());
    assertEquals(1, sc.getSentenceDistance());
    assertEquals(-1, sc.getWordDistance());
    assertEquals(-1, sc.getDependencyDistance());
  }

  @Test
  public void testSentenceThreshold()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE_1 + " " + SENTENCE_2 + " " + SENTENCE_3);
    int s2Begin = SENTENCE_1.length() + 1;
    int s3Begin = s2Begin + SENTENCE_2.length() + 1;

    final Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(SENTENCE_1.length());
    s1.addToIndexes();
    final Sentence s2 = new Sentence(jCas);
    s2.setBegin(s2Begin);
    s2.setEnd(s2Begin + SENTENCE_2.length());
    s2.addToIndexes();
    final Sentence s3 = new Sentence(jCas);
    s3.setBegin(s3Begin);
    s3.setEnd(jCas.getDocumentText().length());
    s3.addToIndexes();

    final Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Person steve = Annotations.createPerson(jCas, s2Begin + 7, s2Begin + 12, "Steve");
    final Person chris = Annotations.createPerson(jCas, s3Begin + 9, s3Begin + 14, "Chris");
    final Location london = Annotations.createLocation(jCas, 12, 18, "London", "");

    processJCas(DocumentRelationshipAnnotator.PARAM_THRESHOLD, 1);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    Assert.assertEquals(3, relations.size());
    final Relation js = findRelationBetween(relations, jon, steve);
    final Relation ls = findRelationBetween(relations, london, steve);
    final Relation sc = findRelationBetween(relations, steve, chris);

    assertEquals(1, js.getSentenceDistance());
    assertEquals(1, ls.getSentenceDistance());
    assertEquals(1, sc.getSentenceDistance());
  }

  @Test
  public void testTypeFiltering()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE_1 + " " + SENTENCE_2 + " " + SENTENCE_3);
    int s2Begin = SENTENCE_1.length() + 1;
    int s3Begin = s2Begin + SENTENCE_2.length() + 1;

    final Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(SENTENCE_1.length());
    s1.addToIndexes();
    final Sentence s2 = new Sentence(jCas);
    s2.setBegin(s2Begin);
    s2.setEnd(s2Begin + SENTENCE_2.length());
    s2.addToIndexes();
    final Sentence s3 = new Sentence(jCas);
    s3.setBegin(s3Begin);
    s3.setEnd(jCas.getDocumentText().length());
    s3.addToIndexes();

    final Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Person steve = Annotations.createPerson(jCas, s2Begin + 7, s2Begin + 12, "Steve");
    final Person chris = Annotations.createPerson(jCas, s3Begin + 9, s3Begin + 14, "Chris");
    Annotations.createLocation(jCas, 12, 18, "London", "");

    processJCas(DocumentRelationshipAnnotator.PARAM_TYPE_NAMES, new String[] {"Person"});

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    Assert.assertEquals(3, relations.size());
    final Relation js = findRelationBetween(relations, jon, steve);
    final Relation jc = findRelationBetween(relations, jon, chris);
    final Relation sc = findRelationBetween(relations, steve, chris);

    assertEquals(1, js.getSentenceDistance());
    assertEquals(2, jc.getSentenceDistance());
    assertEquals(1, sc.getSentenceDistance());
  }

  private Relation findRelationBetween(
      final List<Relation> relations, final Entity e1, final Entity e2) {
    return relations.stream()
        .filter(r -> r.getSource().equals(e1) && r.getTarget().equals(e2))
        .findFirst()
        .get();
  }
}
