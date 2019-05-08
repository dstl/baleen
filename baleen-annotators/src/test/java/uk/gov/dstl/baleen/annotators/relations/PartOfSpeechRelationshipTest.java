// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.relations;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.annotators.relations.PartOfSpeechRelationshipAnnotator.PARAM_EXPAND_TAGS;
import static uk.gov.dstl.baleen.annotators.relations.PartOfSpeechRelationshipAnnotator.PARAM_PATTERN;
import static uk.gov.dstl.baleen.annotators.relations.PartOfSpeechRelationshipAnnotator.PARAM_SOURCE_GROUP;
import static uk.gov.dstl.baleen.annotators.relations.PartOfSpeechRelationshipAnnotator.PARAM_STOP_WORDS;
import static uk.gov.dstl.baleen.annotators.relations.PartOfSpeechRelationshipAnnotator.PARAM_TARGET_GROUP;
import static uk.gov.dstl.baleen.annotators.relations.PartOfSpeechRelationshipAnnotator.PARAM_VALUE_GROUP;
import static uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator.PARAM_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class PartOfSpeechRelationshipTest extends AbstractAnnotatorTest {

  private static final String SENTENCE =
      "Ben visited London. Tom went to Paris. Clare, once, visited Tokyo.";
  private static final String LOCATED_AT = "locatedAt";

  public PartOfSpeechRelationshipTest() {
    super(PartOfSpeechRelationshipAnnotator.class);
  }

  @Test
  public void testPartOfSpeechRelationExtraction()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE);

    final Person ben = Annotations.createPerson(jCas, 0, 3, "Ben");
    final Person tom = Annotations.createPerson(jCas, 20, 23, "Tom");
    Annotations.createPerson(jCas, 39, 44, "Clare");
    final Location london = Annotations.createLocation(jCas, 12, 18, "London", "");
    final Location paris = Annotations.createLocation(jCas, 32, 37, "Paris", "");
    Annotations.createLocation(jCas, 60, 65, "Tokyo", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addPartsOfSpeech();

    processJCas(
        PARAM_PATTERN,
        "(NNP) (VBD) (?:W )*(NNP)",
        PARAM_TYPE,
        LOCATED_AT,
        PARAM_SOURCE_GROUP,
        1,
        PARAM_TARGET_GROUP,
        3,
        PARAM_VALUE_GROUP,
        2);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(2, relations.size());
    final Relation r1 = findRelationBetween(relations, ben, london);
    final Relation r2 = findRelationBetween(relations, tom, paris);

    assertEquals(LOCATED_AT, r1.getRelationshipType());
    assertEquals("visited", r1.getValue());
    assertEquals(0, r1.getBegin());
    assertEquals(18, r1.getEnd());
    assertEquals("", r1.getRelationSubType());
    assertEquals(LOCATED_AT, r2.getRelationshipType());
    assertEquals("went", r2.getValue());
    assertEquals(20, r2.getBegin());
    assertEquals(37, r2.getEnd());
    assertEquals("", r2.getRelationSubType());
  }

  @Test
  public void testExpandedTagsPartOfSpeechRelationExtraction()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE);

    final Person ben = Annotations.createPerson(jCas, 0, 3, "Ben");
    final Person tom = Annotations.createPerson(jCas, 20, 23, "Tom");
    Annotations.createPerson(jCas, 39, 44, "Clare");
    final Location london = Annotations.createLocation(jCas, 12, 18, "London", "");
    final Location paris = Annotations.createLocation(jCas, 32, 37, "Paris", "");
    Annotations.createLocation(jCas, 60, 65, "Tokyo", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addPartsOfSpeech();

    processJCas(
        PARAM_PATTERN,
        "(NN) (VB) (?:W )*(NN)",
        PARAM_TYPE,
        LOCATED_AT,
        PARAM_SOURCE_GROUP,
        1,
        PARAM_TARGET_GROUP,
        3,
        PARAM_VALUE_GROUP,
        2);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(2, relations.size());
    findRelationBetween(relations, ben, london);
    findRelationBetween(relations, tom, paris);
  }

  @Test
  public void testContractedPartOfSpeechRelationExtraction()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE);

    Annotations.createPerson(jCas, 0, 3, "Ben");
    Annotations.createPerson(jCas, 20, 23, "Tom");
    Annotations.createPerson(jCas, 39, 44, "Clare");
    Annotations.createLocation(jCas, 12, 18, "London", "");
    Annotations.createLocation(jCas, 32, 37, "Paris", "");
    Annotations.createLocation(jCas, 60, 65, "Tokyo", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addPartsOfSpeech();

    processJCas(
        PARAM_PATTERN,
        "(NN).*(VB).*(NN)",
        PARAM_TYPE,
        LOCATED_AT,
        PARAM_SOURCE_GROUP,
        1,
        PARAM_TARGET_GROUP,
        3,
        PARAM_VALUE_GROUP,
        2,
        PARAM_EXPAND_TAGS,
        false);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(0, relations.size());
  }

  @Test
  public void testDotInRegExPartOfSpeechRelationExtraction()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE);

    final Person ben = Annotations.createPerson(jCas, 0, 3, "Ben");
    final Person tom = Annotations.createPerson(jCas, 20, 23, "Tom");
    final Person clare = Annotations.createPerson(jCas, 39, 44, "Clare");
    final Location london = Annotations.createLocation(jCas, 12, 18, "London", "");
    final Location paris = Annotations.createLocation(jCas, 32, 37, "Paris", "");
    final Location tokyo = Annotations.createLocation(jCas, 60, 65, "Tokyo", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addPartsOfSpeech();

    processJCas(
        PARAM_PATTERN,
        "(NNP).*(VBD).*(NNP)",
        PARAM_TYPE,
        LOCATED_AT,
        PARAM_SOURCE_GROUP,
        1,
        PARAM_TARGET_GROUP,
        3,
        PARAM_VALUE_GROUP,
        2);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(3, relations.size());
    findRelationBetween(relations, ben, london);
    findRelationBetween(relations, tom, paris);
    findRelationBetween(relations, clare, tokyo);
  }

  @Test
  public void testWordsInRegExPartOfSpeechRelationExtraction()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(SENTENCE);

    Annotations.createPerson(jCas, 0, 3, "Ben");
    Annotations.createPerson(jCas, 20, 23, "Tom");
    final Person clare = Annotations.createPerson(jCas, 39, 44, "Clare");
    Annotations.createLocation(jCas, 12, 18, "London", "");
    Annotations.createLocation(jCas, 32, 37, "Paris", "");
    final Location tokyo = Annotations.createLocation(jCas, 60, 65, "Tokyo", "");

    final uk.gov.dstl.baleen.types.structure.Sentence s =
        new uk.gov.dstl.baleen.types.structure.Sentence(jCas);
    s.setBegin(0);
    s.setEnd(jCas.getDocumentText().length());
    s.addToIndexes();

    addPartsOfSpeech();

    processJCas(
        PARAM_PATTERN,
        "(NNP), once, (VBD).*(NNP)",
        PARAM_TYPE,
        LOCATED_AT,
        PARAM_SOURCE_GROUP,
        1,
        PARAM_TARGET_GROUP,
        3,
        PARAM_VALUE_GROUP,
        2,
        PARAM_STOP_WORDS,
        new String[] {"once"});

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(1, relations.size());
    findRelationBetween(relations, clare, tokyo);
  }

  private void addPartsOfSpeech() {

    Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(19);
    s1.addToIndexes(jCas);
    Sentence s2 = new Sentence(jCas);
    s2.setBegin(20);
    s2.setEnd(38);
    s2.addToIndexes(jCas);
    Sentence s3 = new Sentence(jCas);
    s3.setBegin(39);
    s3.setEnd(66);
    s3.addToIndexes(jCas);

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
    wt3.setBegin(18);
    wt3.setEnd(19);
    wt3.setPartOfSpeech(".");
    wt3.setSentenceOrder(3);
    wt3.addToIndexes(jCas);

    WordToken wt4 = new WordToken(jCas);
    wt4.setBegin(20);
    wt4.setEnd(23);
    wt4.setPartOfSpeech("NNP");
    wt4.setSentenceOrder(0);
    wt4.addToIndexes(jCas);

    WordToken wt5 = new WordToken(jCas);
    wt5.setBegin(24);
    wt5.setEnd(28);
    wt5.setPartOfSpeech("VBD");
    wt5.setSentenceOrder(1);
    wt5.addToIndexes(jCas);

    WordToken wt6 = new WordToken(jCas);
    wt6.setBegin(29);
    wt6.setEnd(31);
    wt6.setPartOfSpeech("TO");
    wt6.setSentenceOrder(2);
    wt6.addToIndexes(jCas);

    WordToken wt7 = new WordToken(jCas);
    wt7.setBegin(32);
    wt7.setEnd(37);
    wt7.setPartOfSpeech("NNP");
    wt7.setSentenceOrder(3);
    wt7.addToIndexes(jCas);

    WordToken wt8 = new WordToken(jCas);
    wt8.setBegin(37);
    wt8.setEnd(38);
    wt8.setPartOfSpeech(".");
    wt8.setSentenceOrder(4);
    wt8.addToIndexes(jCas);

    WordToken wt9 = new WordToken(jCas);
    wt9.setBegin(39);
    wt9.setEnd(44);
    wt9.setPartOfSpeech("NNP");
    wt9.setSentenceOrder(0);
    wt9.addToIndexes(jCas);

    WordToken wt10 = new WordToken(jCas);
    wt10.setBegin(44);
    wt10.setEnd(45);
    wt10.setPartOfSpeech(",");
    wt10.setSentenceOrder(1);
    wt10.addToIndexes(jCas);

    WordToken wt11 = new WordToken(jCas);
    wt11.setBegin(46);
    wt11.setEnd(50);
    wt11.setPartOfSpeech("RB");
    wt11.setSentenceOrder(2);
    wt11.addToIndexes(jCas);

    WordToken wt12 = new WordToken(jCas);
    wt12.setBegin(50);
    wt12.setEnd(51);
    wt12.setPartOfSpeech(",");
    wt12.setSentenceOrder(3);
    wt12.addToIndexes(jCas);

    WordToken wt13 = new WordToken(jCas);
    wt13.setBegin(52);
    wt13.setEnd(59);
    wt13.setPartOfSpeech("VBD");
    wt13.setSentenceOrder(4);
    wt13.addToIndexes(jCas);

    WordToken wt14 = new WordToken(jCas);
    wt14.setBegin(60);
    wt14.setEnd(65);
    wt14.setPartOfSpeech("NNP");
    wt14.setSentenceOrder(5);
    wt14.addToIndexes(jCas);

    WordToken wt15 = new WordToken(jCas);
    wt15.setBegin(65);
    wt15.setEnd(66);
    wt15.setPartOfSpeech(".");
    wt15.setSentenceOrder(6);
    wt15.addToIndexes(jCas);
  }

  private Relation findRelationBetween(
      final List<Relation> relations, final Entity e1, final Entity e2) {
    return relations.stream()
        .filter(r -> r.getSource().equals(e1) && r.getTarget().equals(e2))
        .findFirst()
        .get();
  }
}
