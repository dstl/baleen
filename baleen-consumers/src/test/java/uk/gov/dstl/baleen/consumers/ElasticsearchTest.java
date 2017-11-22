//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ElasticsearchTest extends ElasticsearchTestBase{
	private static final String ELASTICSEARCH = "elasticsearch";

	@Before
	public void setup() throws UIMAException{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ELASTICSEARCH, SharedElasticsearchResource.class, SharedElasticsearchResource.PARAM_CLUSTER, CLUSTER);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Elasticsearch.class, TypeSystemSingleton.getTypeSystemDescriptionInstance(), ELASTICSEARCH, erd);

		ae = AnalysisEngineFactory.createEngine(aed);
	}
	
	@Test
	public void testCountries() throws Exception{
		//Explicitly test each country in SharedCountryResource,
		//to make sure the GeoJSON is accepted by ES (see GitHub Issue #3)
		
		//Only test this through the transport API, as it was an ES storage issue and only needs testing once
		
		assertEquals(new Long(0), getCount());
		
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription("country", SharedCountryResource.class);
		SharedCountryResource scr = new SharedCountryResource();
		scr.initialize(erd.getResourceSpecifier(), Collections.emptyMap());
		
		Set<String> countryCodes = new HashSet<>(scr.getCountryNames().values());
		
		for(String cca3 : countryCodes){
			jCas.reset();
			jCas.setDocumentText(cca3);
			
			Location l = new Location(jCas, 0, cca3.length());
			l.setValue(cca3);
			l.setGeoJson(scr.getGeoJson(cca3));
			l.addToIndexes();
			
			ae.process(jCas);
		}
		
		//Call refresh to force ES to write buffer
		client.admin().indices().refresh(new RefreshRequest("baleen_index")).actionGet();
		
		assertEquals(new Long(countryCodes.size()), getCount());
		
		scr.destroy();
	}
}
