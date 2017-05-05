//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;

import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * A base class for testing annotators where multiple annotators are needed in a pipeline.
 *
 * For example in testing entity extraction annotator you might first need to perform POS tagging.
 *
 * This is more like an integration test than a unit test, but given the complexity of some
 * annotators outputs it might be easier to use the real annotator than attempt to manually mock its
 * output.
 *
 */
public abstract class AbstractMultiAnnotatorTest extends AnnotatorTestBase {

	private AnalysisEngine[] analysisEngines;

	@Before
	public void beforeMultiAnnotatorTest() throws ResourceInitializationException {
		analysisEngines = createAnalysisEngines();
	}

	protected abstract AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException;

	/**
	 * Get an analysis engine for the provided class.
	 *
	 * @param annotatorClass
	 *            the class of the annotator to create as an analysis engine
	 * @return
	 * @throws ResourceInitializationException
	 */
	protected AnalysisEngine createAnalysisEngine(Class<? extends BaleenAnnotator> annotatorClass)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngine(annotatorClass);
	}

	/**
	 * Get an analysis engine for the provided class
	 *
	 * @param annotatorClass
	 *            the class of the annotorat to create as an analysis engine
	 * @param args
	 *            name-value pairs
	 * @return
	 * @throws ResourceInitializationException
	 */
	protected AnalysisEngine createAnalysisEngine(Class<? extends BaleenAnnotator> annotatorClass, Object... args)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngine(annotatorClass, args);
	}

	/**
	 * Convert variable argument to array.
	 *
	 * Helper for use with createAnalysisEngines() implementations.
	 *
	 * @param args
	 *            analysis engines as variable arguments
	 * @return an array of the args
	 * @throws ResourceInitializationException
	 */
	protected AnalysisEngine[] asArray(AnalysisEngine... args) {
		return args;
	}

	/**
	 * Convert list argument to array of analysisengines.
	 *
	 * Helper for use with createAnalysisEngines() implementations.
	 *
	 * @param aes
	 *            analysis engines as collection
	 * @return an array of of analysis engines provided in the collection
	 * @throws ResourceInitializationException
	 */
	protected AnalysisEngine[] asArray(Collection<AnalysisEngine> aes) {
		return aes.toArray(new AnalysisEngine[aes.size()]);
	}

	/**
	 * process the {@link AnnotatorTestBase}'s jCas with the annotator.
	 *
	 * @throws ResourceInitializationException
	 * @throws AnalysisEngineProcessException
	 */
	protected void processJCas() throws ResourceInitializationException, AnalysisEngineProcessException {
		SimplePipeline.runPipeline(jCas, analysisEngines);
	}

}