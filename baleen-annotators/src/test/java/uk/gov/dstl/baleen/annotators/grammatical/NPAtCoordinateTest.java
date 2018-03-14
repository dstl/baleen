// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class NPAtCoordinateTest extends AbstractAnnotatorTest {
  public NPAtCoordinateTest() {
    super(NPAtCoordinate.class);
  }

  private AnalysisEngine languageAE;

  @Before
  public void before() throws UIMAException {
    ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "tokens", SharedOpenNLPModel.class);
    ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    ExternalResourceDescription posDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "posTags", SharedOpenNLPModel.class);
    ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    AnalysisEngineDescription desc =
        AnalysisEngineFactory.createEngineDescription(
            OpenNLP.class,
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc);

    languageAE = AnalysisEngineFactory.createEngine(desc);
  }

  @Test
  public void testLocationNoRT() throws Exception {
    jCas.setDocumentText("The former school house at GR 1234 5678.");

    Location l = new Location(jCas, 4, 23);
    l.addToIndexes();

    Coordinate c = new Coordinate(jCas, 27, 39);
    c.addToIndexes();

    processJCas();
    assertEquals(2, JCasUtil.select(jCas, Location.class).size());

    assertNotNull(l.getReferent());
    assertEquals(l.getReferent(), c.getReferent());
  }

  @Test
  public void testLocationIsAtNoRT() throws Exception {
    jCas.setDocumentText("The former school house is at GR 1234 5678.");

    Location l = new Location(jCas, 4, 23);
    l.addToIndexes();

    Coordinate c = new Coordinate(jCas, 30, 42);
    c.addToIndexes();

    processJCas();
    assertEquals(2, JCasUtil.select(jCas, Location.class).size());

    assertNotNull(l.getReferent());
    assertEquals(l.getReferent(), c.getReferent());
  }

  @Test
  public void testLocationRT1() throws Exception {
    jCas.setDocumentText("The former school house at GR 1234 5678.");

    ReferenceTarget rt = new ReferenceTarget(jCas);
    rt.addToIndexes();

    Location l = new Location(jCas, 4, 23);
    l.setReferent(rt);
    l.addToIndexes();

    Coordinate c = new Coordinate(jCas, 27, 39);
    c.addToIndexes();

    processJCas();
    assertEquals(2, JCasUtil.select(jCas, Location.class).size());

    assertEquals(rt, c.getReferent());
  }

  @Test
  public void testLocationRT2() throws Exception {
    jCas.setDocumentText("The former school house at GR 1234 5678.");

    ReferenceTarget rt = new ReferenceTarget(jCas);
    rt.addToIndexes();

    Location l = new Location(jCas, 4, 23);
    l.addToIndexes();

    Coordinate c = new Coordinate(jCas, 27, 39);
    c.setReferent(rt);
    c.addToIndexes();

    processJCas();
    assertEquals(2, JCasUtil.select(jCas, Location.class).size());

    assertEquals(rt, l.getReferent());
  }

  @Test
  public void testLocationBothRT() throws Exception {
    jCas.setDocumentText(
        "The former school house at GR 1234 5678. The school house has a blue door.");

    ReferenceTarget rt1 = new ReferenceTarget(jCas);
    rt1.addToIndexes();

    ReferenceTarget rt2 = new ReferenceTarget(jCas);
    rt2.addToIndexes();

    Location l1 = new Location(jCas, 4, 23);
    l1.setReferent(rt1);
    l1.addToIndexes();

    Coordinate c = new Coordinate(jCas, 27, 39);
    c.setReferent(rt2);
    c.addToIndexes();

    Location l2 = new Location(jCas, 45, 57);
    l2.setReferent(rt1);
    l2.addToIndexes();

    processJCas();
    assertEquals(3, JCasUtil.select(jCas, Location.class).size());

    assertEquals(rt2, c.getReferent());
    assertEquals(rt2, l1.getReferent());
    assertEquals(rt2, l2.getReferent());
  }

  @Test
  public void testNP() throws Exception {
    jCas.setDocumentText("The former school house at GR 1234 5678.");

    languageAE.process(jCas);

    Coordinate c = new Coordinate(jCas, 27, 39);
    c.addToIndexes();

    processJCas();
    assertEquals(2, JCasUtil.select(jCas, Location.class).size());

    Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);

    assertNotNull(l.getReferent());
    assertEquals(l.getReferent(), c.getReferent());
  }

  @Test
  public void testBoth() throws Exception {
    jCas.setDocumentText("The former school house at GR 1234 5678.");

    languageAE.process(jCas);

    Location l = new Location(jCas, 4, 23);
    l.addToIndexes();

    Coordinate c = new Coordinate(jCas, 27, 39);
    c.addToIndexes();

    processJCas();
    assertEquals(2, JCasUtil.select(jCas, Location.class).size());

    assertNotNull(l.getReferent());
    assertEquals(l.getReferent(), c.getReferent());
  }
}
