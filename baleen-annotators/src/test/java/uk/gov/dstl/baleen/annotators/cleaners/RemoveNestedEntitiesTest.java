//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class RemoveNestedEntitiesTest extends AnnotatorTestBase {
	@Test
	public void test() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedEntities.class);
		
		populateJCas(jCas);
		rneAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		
		Temporal dt = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("December 1972", dt.getCoveredText());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("Oxford", l.getCoveredText());
	}
	
	@Test
	public void testExclude() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedEntities.class, "excludedTypes", new String[]{"uk.gov.dstl.baleen.types.semantic.Location"});
		
		populateJCas(jCas);
		rneAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		assertEquals(3, JCasUtil.select(jCas, Location.class).size());
	}
	
	private void populateJCas(JCas jCas){
		jCas.setDocumentText("Eliza was born in December 1972 in Oxford");
		
		Annotations.createPerson(jCas, 0, 5, "Eliza");
		Annotations.createTemporal(jCas, 18, 31, "December 1972");
		Annotations.createTemporal(jCas, 18, 26, "December");
		Annotations.createLocation(jCas, 35, 37, "OX", null);
		Annotations.createLocation(jCas, 35, 41, "Oxford", null);
		Annotations.createLocation(jCas, 36, 41, "xford", null);
	}
}
