// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;

/** */
public class NationalityToLocationTest extends AnnotatorTestBase {
  AnalysisEngine ae;

  @Before
  public void beforeTest() throws UIMAException {
    super.beforeTest();

    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            "country", SharedCountryResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(NationalityToLocation.class, "country", erd);

    ae = AnalysisEngineFactory.createEngine(aed);
  }

  @After
  public void afterTest() {
    ae.destroy();
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText("Pierre is French");

    Person p = new Person(jCas);
    p.setBegin(0);
    p.setEnd(6);
    p.setValue("Pierre");
    p.addToIndexes();

    Nationality n = new Nationality(jCas);
    n.setBegin(10);
    n.setEnd(16);
    n.setValue("French");
    n.setCountryCode("fra");
    n.addToIndexes();

    ae.process(jCas);

    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("French", l.getValue());
    assertNotNull(l.getGeoJson());
  }
}
