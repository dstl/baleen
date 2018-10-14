// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.language;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;

public class OpenNLPParserClauseTest extends AbstractMultiAnnotatorTest {

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

    ExternalResourceDescription parserChunkingDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "parserChunking", SharedOpenNLPModel.class);

    // Add in the OpenNLP implementation too, as its a prerequisite
    // (in theory we should test OpenNLPParser in isolation, but in practise
    // it as this as a dependency so better test they work together)

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

    AnalysisEngineFactory.createEngineDescription();

    return asArray(
        createAnalysisEngine(
            OpenNLP.class,
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc),
        createAnalysisEngine(
            OpenNLPParser.class,
            "parserChunking",
            parserChunkingDesc,
            OpenNLPParser.INCLUDE_CLAUSES_PARAM,
            true));
  }

  @Test
  public void test() throws AnalysisEngineProcessException, ResourceInitializationException {

    String text = "The fox jumps over the dog.";
    jCas.setDocumentText(text);

    processJCas();

    Collection<Sentence> select = JCasUtil.select(jCas, Sentence.class);
    Sentence s1 = select.iterator().next();

    List<PhraseChunk> phrases = JCasUtil.selectCovered(jCas, PhraseChunk.class, s1);
    assertEquals(5, phrases.size());
    assertEquals("The fox jumps over the dog.", phrases.get(0).getCoveredText());
    assertEquals("S", phrases.get(0).getChunkType());
    assertEquals("The fox", phrases.get(1).getCoveredText());
    assertEquals("jumps over the dog", phrases.get(2).getCoveredText());
    assertEquals("over the dog", phrases.get(3).getCoveredText());
    assertEquals("the dog", phrases.get(4).getCoveredText());
  }
}
