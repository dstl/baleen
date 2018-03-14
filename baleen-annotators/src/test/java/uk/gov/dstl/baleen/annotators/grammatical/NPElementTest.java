// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import static org.junit.Assert.assertEquals;

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
import uk.gov.dstl.baleen.types.common.Chemical;

public class NPElementTest extends AbstractAnnotatorTest {
  public NPElementTest() {
    super(NPElement.class);
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
  public void test() throws Exception {
    jCas.setDocumentText(
        "He used hydrogen peroxide to dye his hair. The diamond was made of carbon-12.");

    languageAE.process(jCas);
    processJCas();
    assertEquals(2, JCasUtil.select(jCas, Chemical.class).size());

    assertEquals(
        "hydrogen peroxide", JCasUtil.selectByIndex(jCas, Chemical.class, 0).getCoveredText());
    assertEquals("carbon-12", JCasUtil.selectByIndex(jCas, Chemical.class, 1).getCoveredText());
  }
}
