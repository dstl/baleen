//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Custom;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class RegexTest extends AnnotatorTestBase {
	
	private static final String UK_GOV_DSTL_BALEEN_TYPES_COMMON_PERSON = "uk.gov.dstl.baleen.types.common.Person";
	private static final String DIGIT_REGEX = "P[0-9]+";
	private static final String P456 = "p456";
	private static final String P123 = "P123";
	private static final String TEXT = "P123 was seen speaking to p456";


	@Test
	public void testMissingType() throws Exception{
		AnalysisEngine regexAE = AnalysisEngineFactory.createEngine(Custom.class, Custom.PARAM_PATTERN, DIGIT_REGEX, Custom.PARAM_CASE_SENSITIVE, true);
		
		jCas.setDocumentText(TEXT);
		regexAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Entity.class).size());
		
		Entity e1 = JCasUtil.selectByIndex(jCas, Entity.class, 0);
		assertNotNull(e1);
		assertEquals(P123, e1.getCoveredText());
		assertEquals(P123, e1.getValue());
		
		regexAE.destroy();
	}
	
	@Test
	public void testBadTypes() throws Exception{
		try{
			AnalysisEngineFactory.createEngine(Custom.class, Custom.PARAM_TYPE, "this.is.not.a.type", Custom.PARAM_PATTERN, DIGIT_REGEX, Custom.PARAM_CASE_SENSITIVE, true);
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			AnalysisEngineFactory.createEngine(Custom.class, Custom.PARAM_TYPE, Custom.class.getName(), Custom.PARAM_PATTERN, DIGIT_REGEX, Custom.PARAM_CASE_SENSITIVE, true);
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			AnalysisEngineFactory.createEngine(Custom.class, Custom.PARAM_TYPE, Relation.class.getName(), Custom.PARAM_PATTERN, DIGIT_REGEX, Custom.PARAM_CASE_SENSITIVE, true);
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
	}
	
	@Test
	public void testCaseInsensitive() throws Exception{
		AnalysisEngine regexAE = AnalysisEngineFactory.createEngine(Custom.class, Custom.PARAM_TYPE, UK_GOV_DSTL_BALEEN_TYPES_COMMON_PERSON, Custom.PARAM_PATTERN, DIGIT_REGEX, Custom.PARAM_CASE_SENSITIVE, false);
		
		jCas.setDocumentText(TEXT);
		regexAE.process(jCas);
		
		assertEquals(2, JCasUtil.select(jCas, Person.class).size());
		
		Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertNotNull(p1);
		assertEquals(P123, p1.getCoveredText());
		assertEquals(P123, p1.getValue());
		
		Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
		assertNotNull(p2);
		assertEquals(P456, p2.getCoveredText());
		assertEquals(P456, p2.getValue());
		
		regexAE.destroy();
	}
	
	@Test
	public void testCaseSensitive() throws Exception{
		AnalysisEngine regexAE = AnalysisEngineFactory.createEngine(Custom.class, Custom.PARAM_TYPE, UK_GOV_DSTL_BALEEN_TYPES_COMMON_PERSON, Custom.PARAM_PATTERN, DIGIT_REGEX, Custom.PARAM_CASE_SENSITIVE, true);
		
		jCas.setDocumentText(TEXT);
		regexAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		
		Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertNotNull(p1);
		assertEquals(P123, p1.getCoveredText());
		assertEquals(P123, p1.getValue());
		
		regexAE.destroy();
	}
	
	
	@Test
	public void testPatternGroup() throws Exception{
		AnalysisEngine regexAE = AnalysisEngineFactory.createEngine(Custom.class, Custom.PARAM_TYPE, UK_GOV_DSTL_BALEEN_TYPES_COMMON_PERSON, Custom.PARAM_PATTERN, "\\b[A-Z][a-z]+\\s+([A-Z]+)\\b", Custom.PARAM_CASE_SENSITIVE, true, Custom.PARAM_GROUP, "1");
		
		jCas.setDocumentText("John SMITH was seen speaking to p456");
		regexAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		
		Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertNotNull(p1);
		assertEquals("John SMITH", p1.getCoveredText());
		assertEquals("SMITH", p1.getValue());
		
		regexAE.destroy();
	}

}
