//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.gazetteer.Country;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.types.semantic.Location;

public class CountryGazetteerTest extends AnnotatorTestBase{
	private static final String COUNTRY = "country";

	@Test
	public void test() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(COUNTRY, SharedCountryResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Country.class, COUNTRY, erd);
		
		testAE(aed, "Jamaica");
	}
	
	@Test
	public void testCaseSensitive() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(COUNTRY, SharedCountryResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Country.class, COUNTRY, erd, "caseSensitive", true);
		
AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Last month, Peter visited the coast of JamaICA");
		
		ae.process(jCas);
		
		assertEquals(0, JCasUtil.select(jCas, Location.class).size());
		
		
	}
	
	@Test
	public void testUTF() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(COUNTRY, SharedCountryResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Country.class, COUNTRY, erd);
		
		
		testAE(aed, "\u062c\u0645\u0647\u0648\u0631\u064a\u0629 \u062c\u064a\u0628\u0648\u062a\u064a");
	}
	
	@Test
	public void testWrongType() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(COUNTRY, SharedCountryResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Country.class, COUNTRY, erd, "type", "Person");
		
		testAE(aed, "Jamaica");
	}

	private void testAE(AnalysisEngineDescription aed, String country)
			throws ResourceInitializationException,
			AnalysisEngineProcessException {
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Last month, Peter visited the coast of "+country);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals(country, l.getValue());
		assertNotNull(l.getGeoJson());
		
		ae.destroy();
	}
}
