//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Before;

import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

/**
 * 
 */
public class ConsumerTestBase {
	protected JCas jCas;

	@Before
	public void beforeTest() throws Exception{
		jCas = JCasSingleton.getJCasInstance();
	}
	

	protected DocumentAnnotation getDocumentAnnotation(JCas jCas) {
		return (DocumentAnnotation) jCas.getDocumentAnnotationFs();
	}
}
