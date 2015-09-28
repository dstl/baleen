//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Before;

/**
 * 
 */
public class AnnotatorTestBase {
	protected JCas jCas;

	@Before
	public void beforeTest() throws UIMAException{
		jCas = JCasFactory.createJCas();
	}
}
