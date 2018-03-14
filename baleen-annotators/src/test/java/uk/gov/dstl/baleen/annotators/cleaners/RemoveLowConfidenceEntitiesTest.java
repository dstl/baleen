// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;

/** */
public class RemoveLowConfidenceEntitiesTest extends AnnotatorTestBase {
  @Test
  public void test() throws Exception {
    AnalysisEngine rpeAE =
        AnalysisEngineFactory.createEngine(
            RemoveLowConfidenceEntities.class, "confidenceThreshold", "0.75");

    jCas.setDocumentText("James works at Dstl.");

    Person p = new Person(jCas);
    p.setValue("James");
    p.setBegin(0);
    p.setEnd(5);
    p.setConfidence(0.98);
    p.addToIndexes();

    Organisation o = new Organisation(jCas);
    o.setValue("Dstl");
    o.setBegin(15);
    o.setEnd(19);
    o.setConfidence(0.96);
    o.addToIndexes();

    Location l = new Location(jCas);
    l.setValue("Dstl");
    l.setBegin(15);
    l.setEnd(19);
    l.setConfidence(0.66);
    l.addToIndexes();

    rpeAE.process(jCas);

    assertEquals(1, JCasUtil.select(jCas, Person.class).size());
    assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
    assertEquals(0, JCasUtil.select(jCas, Location.class).size());

    assertEquals(2, JCasUtil.select(jCas, Entity.class).size());
  }
}
