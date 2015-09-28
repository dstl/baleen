//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/** A base type for single annotator testing, with helper functions.
 *
 * Similar in nature to {@link AnnotatorTestBase} but assumes testing a single annotator.
 *
 * 
 *
 */
public class AbstractAnnotatorTest extends AnnotatorTestBase {

	private Class<? extends BaleenAnnotator> annotatorClass;


	/** New instance, which will test the supplied annotator class.
	 * @param annotatorClass
	 */
	public AbstractAnnotatorTest(Class<? extends BaleenAnnotator> annotatorClass) {
		this.annotatorClass = annotatorClass;
	}

	/** Get an analysis engine for the
	 * @return
	 * @throws ResourceInitializationException
	 */
	protected AnalysisEngine getAnalysisEngine() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngine(annotatorClass);
	}

	/** Get an analysis engine for the
	 * @param args name-value pairs
	 * @return
	 * @throws ResourceInitializationException
	 */
	protected AnalysisEngine getAnalysisEngine(Object... args) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngine(annotatorClass, args);
	}


	/** process the {@link AnnotatorTestBase}'s jCas with the annotator.
	 * @throws ResourceInitializationException
	 * @throws AnalysisEngineProcessException
	 */
	protected void processJCas() throws ResourceInitializationException, AnalysisEngineProcessException {
		AnalysisEngine analysisEngine = getAnalysisEngine();
		analysisEngine.process(jCas);
		analysisEngine.destroy();
	}

	/** process the {@link AnnotatorTestBase}'s jCas with the annotator (configured with the args provided).
	 * @param args name-value pairs
	 * @throws ResourceInitializationException
	 * @throws AnalysisEngineProcessException
	 */
	protected void processJCas(Object... args) throws ResourceInitializationException, AnalysisEngineProcessException {
		AnalysisEngine analysisEngine = getAnalysisEngine(args);
		analysisEngine.process(jCas);
		analysisEngine.destroy();
	}


	/** Process the {@link AnnotatorTestBase} jCas object looking for supplied annotations.
	 *
	 * You must call processJcas (or equivalent first).
	 *
	 * @param size total number of annotations to expect of this type
	 * @param annotationClass the annotation class to look for
	 * @param annotations (a subset of) annotations to test
	 * @throws AnalysisEngineProcessException
	 * @throws ResourceInitializationException
	 */
	@SafeVarargs
	protected final <T extends Annotation> void assertAnnotations(int size, Class<T> annotationClass, TestAnnotation<T>... annotations) throws AnalysisEngineProcessException, ResourceInitializationException {

		assertEquals(size, JCasUtil.select(jCas, annotationClass).size());

		for(TestAnnotation<T> a : annotations) {
			T t = JCasUtil.selectByIndex(jCas, annotationClass, a.getIndex());
			a.validate(t);
		}
	}

	/** Get the document annotation from the default jCas.
	 * @return documentation annotation
	 */
	protected DocumentAnnotation getDocumentAnnotation() {
		return getDocumentAnnotation(jCas);
	}

	/** Get the document annotation from a jCas.
	 * @param jCas
	 * @return documentation annotation
	 */
	protected DocumentAnnotation getDocumentAnnotation(JCas jCas) {
		return (DocumentAnnotation) jCas.getDocumentAnnotationFs();
	}
}
