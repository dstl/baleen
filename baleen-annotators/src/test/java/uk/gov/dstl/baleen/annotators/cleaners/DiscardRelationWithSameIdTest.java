// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class DiscardRelationWithSameIdTest extends AnnotatorTestBase {

  @Test
  public void testDuplicateRemoved() throws Exception {
    AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(DiscardRelationWithSameId.class);

    populateJCas(jCas);
    rneAE.process(jCas);

    // duplicate relation removed
    assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
  }

  @Test
  public void testNonDuplicatesUnaffected() throws Exception {
    AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(DiscardRelationWithSameId.class);

    populateJCas(jCas);
    rneAE.process(jCas);

    assertEquals(1, JCasUtil.select(jCas, Person.class).size());
    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
  }

  private void populateJCas(JCas jCas) {
    jCas.setDocumentText("Eliza was born in December 1972 in Oxford");

    Annotations.createTemporal(jCas, 18, 31, "December 1972");

    Location oxford = Annotations.createLocation(jCas, 35, 41, "Oxford", null);

    Person eliza = Annotations.createPerson(jCas, 0, 5, "Eliza");

    // deliberate creation of 2 identical relations
    Relation r1 = createRelation(jCas, oxford, eliza);
    Relation r2 = createRelation(jCas, oxford, eliza);

    // externalId is based on hash of entity properties
    assertEquals(r1.getExternalId(), r2.getExternalId());
    assertEquals(2, JCasUtil.select(jCas, Relation.class).size());
    assertEquals(1, JCasUtil.select(jCas, Person.class).size());
    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
  }

  private Relation createRelation(JCas jCas, Entity e1, Entity e2) {
    final Relation p2l = new Relation(jCas);
    p2l.setSource(e2);
    p2l.setTarget(e1);
    p2l.setRelationshipType("from");
    p2l.addToIndexes();
    return p2l;
  }
}
