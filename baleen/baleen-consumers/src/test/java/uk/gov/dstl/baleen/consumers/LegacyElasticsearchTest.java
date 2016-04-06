//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.SearchHit;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.resources.SharedLocalElasticsearchResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;

public class LegacyElasticsearchTest extends ElasticsearchConsumerTestBase {
	private static final String UNIQUE_ID = "uniqueID";
	private static final String TYPE = "type";
	private static final String CONFIDENCE = "confidence";
	private static final String END = "end";
	private static final String BEGIN = "begin";
	private static final String VALUE = "value";
	private static final String BALEEN_INDEX = "baleen_index";
	private static final String ELASTICSEARCH = "elasticsearch";

	@BeforeClass
	public static void setupClass() throws UIMAException {
		jCas = JCasFactory.createJCas();


		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ELASTICSEARCH,
				SharedLocalElasticsearchResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Elasticsearch.class,
				ELASTICSEARCH, erd, "legacy", true);

		ae = AnalysisEngineFactory.createEngine(aed);
		client = ((SharedElasticsearchResource)ae.getUimaContext().getResourceObject(ELASTICSEARCH)).getClient();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNoEntities() throws Exception {
		long timestamp = createNoEntitiesDocument();
		ae.process(jCas);

		// Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());

		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		assertEquals("Hello World", result.getSource().get("doc"));
		assertEquals("en", result.getSource().get("language"));
		assertEquals(timestamp, result.getSource().get("dateAccessed"));
		assertEquals("test/no_entities", result.getSource().get("link"));

		Map<String, Object> docType = (Map<String, Object>) result.getSource().get("docType");
		assertEquals("test", docType.get(VALUE));
		assertEquals("uk.gov.dstl.baleen.consumers.Elasticsearch", docType.get("annotator"));
		assertEquals(1.0, docType.get(CONFIDENCE));

		assertEquals("OFFICIAL", result.getSource().get("classification"));

		List<String> rels = (List<String>) result.getSource().get("releasability");
		assertEquals(3, rels.size());
		assertTrue(rels.contains("ENG"));

		List<String> cavs = (List<String>) result.getSource().get("caveats");
		assertEquals(2, cavs.size());
		assertTrue(cavs.contains("TEST_A"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMetadata() throws Exception {
		createMetadataDocument();
		ae.process(jCas);

		// Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());

		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		assertEquals("id_1", result.getSource().get("publishedId"));
		assertEquals("D3", ((Map<String,Object>)result.getSource().get("documentInfo")).get("sourceAndInformationGrading"));
		assertEquals(2, result.getSource().get("source_reliability"));
		assertEquals(3, result.getSource().get("source_validity"));

		assertEquals("test_value", ((Map<String,Object>)result.getSource().get("documentInfo")).get("test_key"));
		assertEquals("Test Title", result.getSource().get("title"));

		List<String> countries = (List<String>) result.getSource().get("countries");
		assertEquals(3, countries.size());
		assertTrue(countries.contains("ENG"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEntities() throws Exception {
		createEntitiesDocument();
		ae.process(jCas);

		// Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());

		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		List<Map<String, Object>> entities = (List<Map<String, Object>>) result.getSource().get("entities");
		assertEquals(4, entities.size());

		Map<String, Object> person = entities.get(0);
		assertEquals(9, person.size());
		assertEquals(0, person.get(BEGIN));
		assertEquals(5, person.get(END));
		assertEquals(0.0, person.get(CONFIDENCE));
		assertEquals("Person", person.get(TYPE));
		assertEquals("James", person.get(VALUE));
		assertNotNull(person.get(UNIQUE_ID));

		Map<String, Object> location = entities.get(1);
		assertEquals(9, location.size());
		assertEquals(14, location.get(BEGIN));
		assertEquals(20, location.get(END));
		assertEquals(0.0, location.get(CONFIDENCE));
		assertEquals("Location", location.get(TYPE));
		assertEquals("London", location.get(VALUE));
		assertNotNull(location.get(UNIQUE_ID));

		Map<String, Object> geoJsonMap = new HashMap<>();
		geoJsonMap.put(TYPE, "Feature");
		Map<String, Object> geometryMap = new HashMap<>();
		geometryMap.put(TYPE, "Point");
		geometryMap.put("coordinates", new ArrayList<Double>(Arrays.asList(-0.1, 51.5)));
		geoJsonMap.put("geometry", geometryMap);

		assertEquals(geoJsonMap, location.get("geoJson"));

		Map<String, Object> date = entities.get(2);
		assertEquals(8, date.size());
		assertEquals(24, date.get(BEGIN));
		assertEquals(42, date.get(END));
		assertEquals(1.0, date.get(CONFIDENCE));
		assertEquals("DateType", date.get(TYPE));
		assertEquals("19th February 2015", date.get(VALUE));
		assertNotNull(date.get(UNIQUE_ID));

		Map<String, Object> email = entities.get(3);
		assertEquals(8, email.size());
		assertEquals(66, email.get(BEGIN));
		assertEquals(83, email.get(END));
		assertEquals(0.0, email.get(CONFIDENCE));
		assertEquals("CommsIdentifier", email.get(TYPE));
		assertEquals("email", email.get("subType"));
		assertEquals("james@example.com", email.get(VALUE));
		assertNotNull(email.get(UNIQUE_ID));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNoReportSortDate() throws Exception{
		createMetadataDocument();
		ae.process(jCas);
	
		//Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());

		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		Map<String, Object> metadataMap = (Map<String, Object>) result.getSource().get("documentInfo");

		assertNull(metadataMap.get("report_sort_date"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testReportSortDate() throws Exception{
		createMetadataDocument();
		
		Metadata m1 = new Metadata(jCas);
		m1.setKey("report_date");
		m1.setValue("07 JUL 2013");
		m1.addToIndexes(jCas);
		
		Metadata m2 = new Metadata(jCas);
		m2.setKey("dateOfReport");
		m2.setValue("Invalid Date");
		m2.addToIndexes(jCas);
		
		Metadata m3 = new Metadata(jCas);
		m3.setKey("dateOfInformation");
		m3.setValue("2013-07-05T13:15:12Z");
		m3.addToIndexes(jCas);
		
		ae.process(jCas);
	
		//Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());

		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		Map<String, Object> metadataMap = (Map<String, Object>) result.getSource().get("documentInfo");

		assertEquals("2013-07-05T13:15:12Z", metadataMap.get("report_sort_date"));
	}
}
