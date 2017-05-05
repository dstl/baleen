//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * 
 */
public class BlacklistTest extends AnnotatorTestBase {
	private static final String NOVEMBER2 = "NOVEMBER";
	private static final String LONDON = "London";
	private static final String NOVEMBER = "November";

	@Test
	public void test() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(Blacklist.class, Blacklist.PARAM_BLACKLIST, new String[]{NOVEMBER2, LONDON});
		createDocument(jCas);
		
		rneAE.process(jCas);
		
		assertCorrect(1, 0, 0);
		
		rneAE.destroy();
	}
	
	@Test
	public void testCaseSensitive() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(Blacklist.class, Blacklist.PARAM_BLACKLIST, new String[]{NOVEMBER2, LONDON}, Blacklist.PARAM_CASE_SENSITIVE, true);
		createDocument(jCas);
		
		rneAE.process(jCas);
		
		assertCorrect(1, 1, 0);
		
		rneAE.destroy();
	}

	@Test
	public void testSpecifiedType() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(Blacklist.class, Blacklist.PARAM_BLACKLIST, new String[]{NOVEMBER2, LONDON}, Blacklist.PARAM_TYPE, "uk.gov.dstl.baleen.types.semantic.Location");
		createDocument(jCas);
		
		rneAE.process(jCas);
		
		assertCorrect(1, 1, 0);
		
		rneAE.destroy();
	}
	
	@Test
	public void testBadTypes() throws Exception{
		try{
			AnalysisEngineFactory.createEngine(Blacklist.class, Blacklist.PARAM_BLACKLIST, new String[]{NOVEMBER2, LONDON}, Blacklist.PARAM_TYPE, "this.is.not.a.type");
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			AnalysisEngineFactory.createEngine(Blacklist.class, Blacklist.PARAM_BLACKLIST, new String[]{NOVEMBER2, LONDON}, Blacklist.PARAM_TYPE, Blacklist.class.getName());
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			AnalysisEngineFactory.createEngine(Blacklist.class, Blacklist.PARAM_BLACKLIST, new String[]{NOVEMBER2, LONDON}, Blacklist.PARAM_TYPE, Relation.class.getName());

			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
	}
	
	private void createDocument(JCas jCas){
		jCas.reset();
		jCas.setDocumentText("Simon was born in November 1980 in London");
		
		Person p = new Person(jCas);
		p.setValue("Simon");
		p.setBegin(0);
		p.setEnd(5);
		p.addToIndexes();
		
		Temporal d = new Temporal(jCas);
		d.setValue(NOVEMBER);
		d.setBegin(18);
		d.setEnd(26);
		d.addToIndexes();
		
		Location l = new Location(jCas);
		l.setValue(LONDON);
		l.setBegin(35);
		l.setEnd(41);
		l.addToIndexes();

		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
	}
	
	private void assertCorrect(int people, int datetypes, int locations){
		assertEquals(people, JCasUtil.select(jCas, Person.class).size());
		assertEquals(datetypes, JCasUtil.select(jCas, Temporal.class).size());
		assertEquals(locations, JCasUtil.select(jCas, Location.class).size());
	}
}
