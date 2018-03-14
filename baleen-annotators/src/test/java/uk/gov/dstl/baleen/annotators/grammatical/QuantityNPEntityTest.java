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
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

/** Tests for {@link uk.gov.dstl.baleen.annotators.grammatical.QuantityNPEntity}. */
public class QuantityNPEntityTest extends AbstractAnnotatorTest {

  public QuantityNPEntityTest() {
    super(QuantityNPEntity.class);
  }

  @Before
  public void before() throws UIMAException {
    jCas.setDocumentText("The bag contained 4kg of blue powder.");

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
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc);

    AnalysisEngine languageAE = AnalysisEngineFactory.createEngine(desc);
    languageAE.process(jCas);
  }

  @Test
  public void testUnmarked() throws UIMAException {
    Quantity q = new Quantity(jCas);
    q.setBegin(18);
    q.setEnd(21);
    q.addToIndexes();

    processJCas();

    assertAnnotations(2, Entity.class, new TestEntity<>(1, "blue powder"));

    assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
    Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
    assertEquals(18, r.getBegin());
    assertEquals(q, r.getSource());
    assertEquals(36, r.getEnd());
    assertNotNull(r.getTarget());
    assertEquals("QUANTITY", r.getRelationshipType());
  }

  @Test
  public void testMarked() throws UIMAException {
    Quantity q = new Quantity(jCas);
    q.setBegin(18);
    q.setEnd(21);
    q.addToIndexes();

    Buzzword e = new Buzzword(jCas);
    e.setBegin(25);
    e.setEnd(36);
    e.addToIndexes();

    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Entity.class).size());

    assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
    Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
    assertEquals(18, r.getBegin());
    assertEquals(q, r.getSource());
    assertEquals(36, e.getEnd());
    assertEquals(e, r.getTarget());
    assertEquals("QUANTITY", r.getRelationshipType());
  }
}
