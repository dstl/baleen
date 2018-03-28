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
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class DiscardEntityWithSameIdTest extends AnnotatorTestBase {

  @Test
  public void testDuplicateRemoved() throws Exception {
    AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(DiscardEntityWithSameId.class);

    populateJCas(jCas);
    rneAE.process(jCas);

    // duplicate person removed
    assertEquals(1, JCasUtil.select(jCas, Person.class).size());
  }

  @Test
  public void testNonDuplicatesUnaffected() throws Exception {
    AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(DiscardEntityWithSameId.class);

    populateJCas(jCas);
    rneAE.process(jCas);

    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
  }

  private void populateJCas(JCas jCas) {
    jCas.setDocumentText("Eliza was born in December 1972 in Oxford");

    Annotations.createTemporal(jCas, 18, 31, "December 1972");

    Annotations.createLocation(jCas, 35, 41, "Oxford", null);

    // deliberate creation of 2 identical people
    Person eliza1 = Annotations.createPerson(jCas, 0, 5, "Eliza");
    Person eliza2 = Annotations.createPerson(jCas, 0, 5, "Eliza");

    // externalId is based on hash of entity properties
    assertEquals(eliza1.getExternalId(), eliza2.getExternalId());
    assertEquals(2, JCasUtil.select(jCas, Person.class).size());
    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
  }
}
