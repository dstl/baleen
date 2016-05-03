//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.junit.Test;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;

import uk.gov.dstl.baleen.annotators.cleaners.NormalizeWhitespace;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;

/**
 * 
 */
public class NormalizeWhitespaceTest extends AnnotatorTestBase {
	private static final String CORRECT_WHITESPACING = "Peter Smith";

	@Test
	public void testNewLine() throws Exception{
		AnalysisEngine nwAE = AnalysisEngineFactory.createEngine(NormalizeWhitespace.class);
		
		jCas.setDocumentText("Peter\nSmith lives in Salisbury");
		
		Annotations.createPerson(jCas, 0, 11, "Peter\nSmith");
		
		nwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(CORRECT_WHITESPACING, JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Person.class, 0).getIsNormalised());
	}
	
	@Test
	public void testSpaces() throws Exception{
		AnalysisEngine nwAE = AnalysisEngineFactory.createEngine(NormalizeWhitespace.class);
		
		jCas.setDocumentText("Peter  Smith lives in Salisbury");
		
		Annotations.createPerson(jCas, 0, 12, "Peter  Smith");
		
		nwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(CORRECT_WHITESPACING, JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Person.class, 0).getIsNormalised());
	}
	
	@Test
	public void testTab() throws Exception{
		AnalysisEngine nwAE = AnalysisEngineFactory.createEngine(NormalizeWhitespace.class);
		
		jCas.setDocumentText("Peter\tSmith lives in Salisbury");
		
		Annotations.createPerson(jCas, 0, 11, "Peter\tSmith");		
		nwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(CORRECT_WHITESPACING, JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Person.class, 0).getIsNormalised());
	}
	
	@Test
	public void testMixed() throws Exception{
		AnalysisEngine nwAE = AnalysisEngineFactory.createEngine(NormalizeWhitespace.class);
		
		jCas.setDocumentText("Peter\n  \n\n\tSmith lives in Salisbury");
		
		Annotations.createPerson(jCas, 0, 11, "Peter\n  \n\n\tSmith");
		
		nwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(CORRECT_WHITESPACING, JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Person.class, 0).getIsNormalised());
	}
	
	@Test
	public void testNoValue() throws Exception{
		AnalysisEngine nwAE = AnalysisEngineFactory.createEngine(NormalizeWhitespace.class);
		
		jCas.setDocumentText("Peter  Smith lives in Salisbury");
		
		Annotations.createPerson(jCas, 0, 11, "Peter  Smith");
		JCasUtil.selectByIndex(jCas, Person.class, 0).setValue(null);
		
		nwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(null, JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
		assertEquals(false, JCasUtil.selectByIndex(jCas, Person.class, 0).getIsNormalised());
	}
}
