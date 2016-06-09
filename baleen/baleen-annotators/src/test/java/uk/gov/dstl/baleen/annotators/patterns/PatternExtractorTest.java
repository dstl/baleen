package uk.gov.dstl.baleen.annotators.patterns;

import java.util.Collection;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Assert;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.language.Pattern;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

public class PatternExtractorTest extends AnnotatorTestBase {

	private AnalysisEngine ae;

	@Override
	public void beforeTest() throws UIMAException {
		super.beforeTest();

		ExternalResourceDescription stopwordsDesc = ExternalResourceFactory
				.createExternalResourceDescription(PatternExtractor.KEY_STOPWORDS, SharedStopwordResource.class);
		
		final AnalysisEngineDescription desc = AnalysisEngineFactory.createEngineDescription(PatternExtractor.class, PatternExtractor.KEY_STOPWORDS, stopwordsDesc);

		ae = AnalysisEngineFactory.createEngine(desc);
	}

	@Test
	public void testNegationProcess() throws AnalysisEngineProcessException {
		final String text = "The fox did not jump over the dog.";
		jCas.setDocumentText(text);

		final Sentence sentence = new Sentence(jCas);
		sentence.setBegin(0);
		sentence.setEnd(text.length());
		sentence.addToIndexes(jCas);

		int offset = 0;
		while (offset < text.length()) {
			int end = text.indexOf(" ", offset);
			if (end == -1) {
				end = text.indexOf(".", offset);
			}

			if (end > 0) {
				final WordToken wordToken = new WordToken(jCas);
				wordToken.setBegin(offset);
				wordToken.setEnd(end);
				// Fake the POS
				wordToken.setPartOfSpeech("VBZ");
				wordToken.addToIndexes(jCas);
				offset = end + 1;
			} else {
				offset = text.length();
			}
		}

		final Entity fox = new Entity(jCas);
		fox.setBegin(4);
		fox.setEnd(7);
		fox.addToIndexes(jCas);

		final Entity dog = new Entity(jCas);
		dog.setBegin(30);
		dog.setEnd(33);
		dog.addToIndexes(jCas);

		SimplePipeline.runPipeline(jCas, ae);

		final Collection<Pattern> patterns = JCasUtil.select(jCas, Pattern.class);
		Assert.assertEquals(0, patterns.size());
	}

	@Test
	public void testProcess() throws AnalysisEngineProcessException {
		final String text = "The fox jumps over the dog.";
		jCas.setDocumentText(text);

		final Sentence sentence = new Sentence(jCas);
		sentence.setBegin(0);
		sentence.setEnd(text.length());
		sentence.addToIndexes(jCas);

		int offset = 0;
		while (offset < text.length()) {
			int end = text.indexOf(" ", offset);
			if (end == -1) {
				end = text.indexOf(".", offset);
			}

			if (end > 0) {
				final WordToken wordToken = new WordToken(jCas);
				wordToken.setBegin(offset);
				wordToken.setEnd(end);
				// Fake the POS
				wordToken.setPartOfSpeech("VBZ");
				wordToken.addToIndexes(jCas);
				offset = end + 1;
			} else {
				offset = text.length();
			}
		}

		final Entity fox = new Entity(jCas);
		fox.setBegin(4);
		fox.setEnd(7);
		fox.addToIndexes(jCas);

		final Entity dog = new Entity(jCas);
		dog.setBegin(23);
		dog.setEnd(26);
		dog.addToIndexes(jCas);

		SimplePipeline.runPipeline(jCas, ae);

		final Collection<Pattern> patterns = JCasUtil.select(jCas, Pattern.class);
		Assert.assertEquals(1, patterns.size());

		final Pattern p = patterns.iterator().next();
		Assert.assertEquals(1, p.getWords().size());
		Assert.assertEquals("jumps", p.getWords(0).getCoveredText());

	}

}
