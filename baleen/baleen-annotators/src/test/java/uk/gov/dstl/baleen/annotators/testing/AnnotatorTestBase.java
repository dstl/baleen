//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;

/**
 *
 */
public class AnnotatorTestBase {
	protected JCas jCas;

	@Before
	public void beforeTest() throws UIMAException {
		jCas = JCasFactory.createJCas();
	}

	/**
	 * Process the {@link AnnotatorTestBase} jCas object looking for supplied annotations.
	 *
	 * You must call processJcas (or equivalent first).
	 *
	 * @param size
	 *            total number of annotations to expect of this type
	 * @param annotationClass
	 *            the annotation class to look for
	 * @param annotations
	 *            (a subset of) annotations to test
	 * @throws AnalysisEngineProcessException
	 * @throws ResourceInitializationException
	 */
	@SafeVarargs
	protected final <T extends Annotation> void assertAnnotations(int size, Class<T> annotationClass,
			TestAnnotation<T>... annotations) throws AnalysisEngineProcessException, ResourceInitializationException {

		assertEquals(size, JCasUtil.select(jCas, annotationClass).size());

		for (TestAnnotation<T> a : annotations) {
			T t = JCasUtil.selectByIndex(jCas, annotationClass, a.getIndex());
			a.validate(t);
		}
	}

	/**
	 * Get the document annotation from the default jCas.
	 * 
	 * @return documentation annotation
	 */
	protected DocumentAnnotation getDocumentAnnotation() {
		return getDocumentAnnotation(jCas);
	}

	/**
	 * Get the document annotation from a jCas.
	 * 
	 * @param jCas
	 * @return documentation annotation
	 */
	protected DocumentAnnotation getDocumentAnnotation(JCas jCas) {
		return (DocumentAnnotation) jCas.getDocumentAnnotationFs();
	}
}
