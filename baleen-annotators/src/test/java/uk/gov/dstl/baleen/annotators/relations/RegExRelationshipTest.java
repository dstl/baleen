// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.annotators.relations.RegExRelationshipAnnotator.PARAM_PATTERN;
import static uk.gov.dstl.baleen.annotators.relations.RegExRelationshipAnnotator.PARAM_SOURCE_GROUP;
import static uk.gov.dstl.baleen.annotators.relations.RegExRelationshipAnnotator.PARAM_TARGET_GROUP;
import static uk.gov.dstl.baleen.annotators.relations.RegExRelationshipAnnotator.PARAM_VALUE_GROUP;
import static uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator.PARAM_SUB_TYPE;
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
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class RegExRelationshipTest extends AbstractAnnotatorTest {

  private static final String LOCATED_AT = "locatedAt";
  private static final String SUB = "sub";

  public RegExRelationshipTest() {
    super(RegExRelationshipAnnotator.class);
  }

  @Test
  public void testRegExRelationExtraction()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("Jon visits London. Steve visited Cheltenham");

    final Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Person steve = Annotations.createPerson(jCas, 19, 24, "Steve");
    final Location london = Annotations.createLocation(jCas, 11, 17, "London", "");
    final Location cheltenham = Annotations.createLocation(jCas, 33, 43, "Cheltenham", "");

    processJCas(
        PARAM_PATTERN, "(:Person:)\\s+(?:visit\\w*|went)\\s+(:Location:)", PARAM_TYPE, LOCATED_AT);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(2, relations.size());
    final Relation jlr = findRelationBetween(relations, jon, london);
    final Relation scr = findRelationBetween(relations, steve, cheltenham);

    assertEquals(LOCATED_AT, jlr.getRelationshipType());
    assertEquals("Jon visits London", jlr.getValue());
    assertEquals(0, jlr.getBegin());
    assertEquals(17, jlr.getEnd());
    assertEquals("", jlr.getRelationSubType());
    assertEquals(LOCATED_AT, scr.getRelationshipType());
    assertEquals("", scr.getRelationSubType());
    assertEquals("Steve visited Cheltenham", scr.getValue());
    assertEquals(19, scr.getBegin());
    assertEquals(43, scr.getEnd());
  }

  @Test
  public void testValueGroup()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("Jon visits London. Steve went to Cheltenham");

    final Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Person steve = Annotations.createPerson(jCas, 19, 24, "Steve");
    final Location london = Annotations.createLocation(jCas, 11, 17, "London", "");
    final Location cheltenham = Annotations.createLocation(jCas, 33, 43, "Cheltenham", "");

    processJCas(
        PARAM_PATTERN,
        "(:Person:)\\s+(visit\\w*|went to)\\s+(:Location:)",
        PARAM_TYPE,
        LOCATED_AT,
        PARAM_SOURCE_GROUP,
        1,
        PARAM_VALUE_GROUP,
        2,
        PARAM_TARGET_GROUP,
        3,
        PARAM_SUB_TYPE,
        SUB);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(2, relations.size());
    final Relation jlr = findRelationBetween(relations, jon, london);
    final Relation scr = findRelationBetween(relations, steve, cheltenham);

    assertEquals(LOCATED_AT, jlr.getRelationshipType());
    assertEquals(SUB, jlr.getRelationSubType());
    assertEquals("visits", jlr.getValue());
    assertEquals(0, jlr.getBegin());
    assertEquals(17, jlr.getEnd());
    assertEquals(LOCATED_AT, scr.getRelationshipType());
    assertEquals(SUB, scr.getRelationSubType());
    assertEquals("went to", scr.getValue());
    assertEquals(19, scr.getBegin());
    assertEquals(43, scr.getEnd());
  }

  @Test
  public void testEntityUse()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("Jon visits London. Steve went to Cheltenham.");

    final Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    final Person steve = Annotations.createPerson(jCas, 19, 24, "Steve");
    final Location london = Annotations.createLocation(jCas, 11, 17, "London", "");
    final Location cheltenham = Annotations.createLocation(jCas, 33, 43, "Cheltenham", "");

    processJCas(
        PARAM_PATTERN,
        "(:Entity:)\\s+((?:\\w*\\s+)+)(:Entity:)",
        PARAM_TYPE,
        LOCATED_AT,
        PARAM_SOURCE_GROUP,
        1,
        PARAM_VALUE_GROUP,
        2,
        PARAM_TARGET_GROUP,
        3,
        PARAM_SUB_TYPE,
        SUB);

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
    assertEquals(2, relations.size());
    final Relation jlr = findRelationBetween(relations, jon, london);
    final Relation scr = findRelationBetween(relations, steve, cheltenham);

    assertEquals(LOCATED_AT, jlr.getRelationshipType());
    assertEquals(SUB, jlr.getRelationSubType());
    assertEquals("visits ", jlr.getValue());
    assertEquals(0, jlr.getBegin());
    assertEquals(17, jlr.getEnd());
    assertEquals(LOCATED_AT, scr.getRelationshipType());
    assertEquals(SUB, scr.getRelationSubType());
    assertEquals("went to ", scr.getValue());
    assertEquals(19, scr.getBegin());
    assertEquals(43, scr.getEnd());
  }

  private Relation findRelationBetween(
      final List<Relation> relations, final Entity e1, final Entity e2) {
    return relations.stream()
        .filter(r -> r.getSource().equals(e1) && r.getTarget().equals(e2))
        .findFirst()
        .get();
  }
}
