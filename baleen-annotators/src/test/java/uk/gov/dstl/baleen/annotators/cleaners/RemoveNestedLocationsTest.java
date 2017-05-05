//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * 
 */
public class RemoveNestedLocationsTest extends AnnotatorTestBase {
	private static final String FAKE_GEOJSON = "geojson_london";
	private static final String NORTH_LONDON = "North London";
	private static final String LONDON = "London";

	@Test
	public void testNoGeoJson() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedLocations.class);
		
		jCas.setDocumentText(NORTH_LONDON);
		
		Annotations.createLocation(jCas, 6, 12, LONDON, null);
		Location l2 = Annotations.createLocation(jCas, 0, 12, NORTH_LONDON, null);

		rneAE.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		assertEquals(l2, JCasUtil.selectByIndex(jCas, Location.class, 0));
	}

	@Test
	public void testSameGeoJson() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedLocations.class);
		
		jCas.setDocumentText(NORTH_LONDON);
		
		Annotations.createLocation(jCas, 6, 12, LONDON, FAKE_GEOJSON);
		Location l2 = Annotations.createLocation(jCas, 0, 12, NORTH_LONDON, FAKE_GEOJSON);
		
		rneAE.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		assertEquals(l2, JCasUtil.selectByIndex(jCas, Location.class, 0));
	}
	
	@Test
	public void testDifferentGeoJson() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedLocations.class);
		
		jCas.setDocumentText(NORTH_LONDON);
		
		Location l1 = Annotations.createLocation(jCas, 6, 12, LONDON, FAKE_GEOJSON);
		Location l2 = Annotations.createLocation(jCas, 0, 12, NORTH_LONDON, "different");
		
		rneAE.process(jCas);
		assertEquals(2, JCasUtil.select(jCas, Location.class).size());
		assertEquals(l2, JCasUtil.selectByIndex(jCas, Location.class, 0));
		assertEquals(l1, JCasUtil.selectByIndex(jCas, Location.class, 1));
	}
	
	@Test
	public void testOneGeoJsonOuter() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedLocations.class);
		
		jCas.setDocumentText(NORTH_LONDON);
		
		Annotations.createLocation(jCas, 6, 12, LONDON, null);
		Location l2 = Annotations.createLocation(jCas, 0, 12, NORTH_LONDON, "different");
		
		rneAE.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		assertEquals(l2, JCasUtil.selectByIndex(jCas, Location.class, 0));
	}
	
	@Test
	public void testOneGeoJsonInner() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedLocations.class);
		
		jCas.setDocumentText(NORTH_LONDON);
		
		Location l1 = Annotations.createLocation(jCas, 6, 12, LONDON, FAKE_GEOJSON);
		Location l2 = Annotations.createLocation(jCas, 0, 12, NORTH_LONDON, null);
		
		rneAE.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals(l1.getGeoJson(), l.getGeoJson());
		l.setGeoJson(null);
		assertEquals(l2, l);
	}
}
