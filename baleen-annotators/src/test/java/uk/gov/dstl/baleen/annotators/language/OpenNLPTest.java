// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.language;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.language.WordToken;

public class OpenNLPTest extends AnnotatorTestBase {

  AnalysisEngine ae;

  @Override
  public void beforeTest() throws UIMAException {
    super.beforeTest();

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

    ae = AnalysisEngineFactory.createEngine(desc);
  }

  @Test
  public void test() throws Exception {

    String text = "This is some text. It has three sentences. The first sentence has four words.";

    jCas.setDocumentText(text);
    SimplePipeline.runPipeline(jCas, ae);

    assertEquals(3, JCasUtil.select(jCas, Sentence.class).size()); // 3 sentences

    Sentence s1 = JCasUtil.selectByIndex(jCas, Sentence.class, 0);
    List<WordToken> tokens = JCasUtil.selectCovered(jCas, WordToken.class, s1);

    assertEquals(5, tokens.size()); // 5 tokens in the first sentence
    assertEquals("NN", tokens.get(3).getPartOfSpeech()); // 4th token of first sentence is a noun

    List<PhraseChunk> phrases = JCasUtil.selectCovered(jCas, PhraseChunk.class, s1);
    assertEquals(3, phrases.size()); // 3 chunks in the first sentence
    assertEquals(
        "some text", phrases.get(2).getCoveredText()); // 3rd chunk of 1st sentence is "some text"
  }

  @Test
  public void testWiithText() throws Exception {

    String text = "This is some text. It has three sentences. The first sentence has four words.";
    jCas.setDocumentText(text);

    Text t1 = new Text(jCas, 19, 43);
    t1.addToIndexes();
    Text t2 = new Text(jCas, 43, jCas.getDocumentText().length());
    t2.addToIndexes();

    SimplePipeline.runPipeline(jCas, ae);

    assertEquals(2, JCasUtil.select(jCas, Sentence.class).size()); // 2 sentences

    // note due to text the first sentence annotation is the second in the text

    Sentence s1 = JCasUtil.selectByIndex(jCas, Sentence.class, 0);
    List<WordToken> tokens = JCasUtil.selectCovered(jCas, WordToken.class, s1);

    assertEquals(5, tokens.size()); // 5 tokens in the first sentence
    assertEquals("NNS", tokens.get(3).getPartOfSpeech()); // 4th token of first sentence is a noun

    List<PhraseChunk> phrases = JCasUtil.selectCovered(jCas, PhraseChunk.class, s1);
    assertEquals(3, phrases.size()); // 3 chunks in the first sentence
    assertEquals(
        "three sentences",
        phrases.get(2).getCoveredText()); // 3rd chunk of 1st sentence is "some text"
  }
}
