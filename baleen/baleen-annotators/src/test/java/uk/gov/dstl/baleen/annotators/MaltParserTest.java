package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;

public class MaltParserTest extends AbstractMultiAnnotatorTest {

	@Override
	protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

		// Use OpenNlp to generate the POS etc for us
		final ExternalResourceDescription tokensDesc = ExternalResourceFactory.createExternalResourceDescription(
				"tokens",
				SharedOpenNLPModel.class);
		final ExternalResourceDescription sentencesDesc = ExternalResourceFactory
				.createExternalResourceDescription("sentences", SharedOpenNLPModel.class);
		final ExternalResourceDescription posDesc = ExternalResourceFactory.createExternalResourceDescription("posTags",
				SharedOpenNLPModel.class);
		final ExternalResourceDescription chunksDesc = ExternalResourceFactory
				.createExternalResourceDescription("phraseChunks", SharedOpenNLPModel.class);

		return asArray(
				createAnalysisEngine(OpenNLP.class, "tokens", tokensDesc, "sentences", sentencesDesc, "posTags",
						posDesc, "phraseChunks", chunksDesc),
				createAnalysisEngine(MaltParser.class));
	}

	@Test
	public void testProcess() throws AnalysisEngineProcessException, ResourceInitializationException {
		final String text = "The fox jumps over the dog.";
		jCas.setDocumentText(text);

		processJCas();

		final Collection<Sentence> select = JCasUtil.select(jCas, Sentence.class);
		final Sentence s1 = select.iterator().next();

		final List<Dependency> dependencies = JCasUtil.selectCovered(jCas, Dependency.class, s1);

		// We could test the output here, but its so model dependent its not
		// worth it, as long as annotations have been created"

		// 7 = 6 words + 1 punctuation, each should have a dependency
		assertEquals(7, dependencies.size());

	}

}
