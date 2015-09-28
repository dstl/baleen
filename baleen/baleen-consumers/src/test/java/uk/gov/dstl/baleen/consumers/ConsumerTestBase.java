//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Before;

/**
 * 
 */
public class ConsumerTestBase {
	protected JCas jCas;

	@Before
	public void beforeTest() throws Exception{
		jCas = JCasFactory.createJCas();
	}
	

	protected DocumentAnnotation getDocumentAnnotation(JCas jCas) {
		return (DocumentAnnotation) jCas.getDocumentAnnotationFs();
	}
}
