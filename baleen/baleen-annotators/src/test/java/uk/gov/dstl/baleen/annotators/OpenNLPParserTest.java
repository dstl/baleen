package uk.gov.dstl.baleen.annotators;

import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.language.OpenNLPParser;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;

public class OpenNLPParserTest extends AbstractMultiAnnotatorTest {

	@Override
	protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

		final ExternalResourceDescription parserChunkingDesc = ExternalResourceFactory
				.createExternalResourceDescription("parserChunking", SharedOpenNLPModel.class);

		// Add in the OpenNLP implementation too, as its a prerequisite
		// (in theory we should test OpenNLPParser in isolation, but in practise
		// it as this as a
		// dependency
		// so better test they work together)

		final ExternalResourceDescription tokensDesc = ExternalResourceFactory.createExternalResourceDescription(
				"tokens",
				SharedOpenNLPModel.class);
		final ExternalResourceDescription sentencesDesc = ExternalResourceFactory
				.createExternalResourceDescription("sentences", SharedOpenNLPModel.class);
		final ExternalResourceDescription posDesc = ExternalResourceFactory.createExternalResourceDescription("posTags",
				SharedOpenNLPModel.class);
		final ExternalResourceDescription chunksDesc = ExternalResourceFactory
				.createExternalResourceDescription("phraseChunks", SharedOpenNLPModel.class);

		AnalysisEngineFactory.createEngineDescription();

		return asArray(
				createAnalysisEngine(OpenNLP.class, "tokens", tokensDesc, "sentences", sentencesDesc, "posTags",
						posDesc, "phraseChunks", chunksDesc),
				createAnalysisEngine(OpenNLPParser.class, "parserChunking", parserChunkingDesc));

	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {

		final String text = "The fox jumps over the dog.";
		jCas.setDocumentText(text);

		processJCas();

		final Collection<Sentence> select = JCasUtil.select(jCas, Sentence.class);
		final Sentence s1 = select.iterator().next();

		final List<PhraseChunk> phrases = JCasUtil.selectCovered(jCas, PhraseChunk.class, s1);
		Assert.assertEquals(4, phrases.size());
		Assert.assertEquals("The fox", phrases.get(0).getCoveredText());
		Assert.assertEquals("jumps over the dog", phrases.get(1).getCoveredText());
		Assert.assertEquals("over the dog", phrases.get(2).getCoveredText());
		Assert.assertEquals("the dog", phrases.get(3).getCoveredText());
	}

}
