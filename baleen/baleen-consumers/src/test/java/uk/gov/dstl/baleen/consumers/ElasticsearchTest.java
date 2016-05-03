//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.SearchHit;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.resources.SharedLocalElasticsearchResource;

public class ElasticsearchTest extends ElasticsearchConsumerTestBase{

	private static final String EXTERNAL_ID = "externalId";
	private static final String VALUE = "value";
	private static final String TYPE = "type";
	private static final String CONFIDENCE = "confidence";
	private static final String END = "end";
	private static final String BEGIN = "begin";
	private static final String ELASTICSEARCH = "elasticsearch";
	private static final String DOC_TYPE = "docType";
	private static final String BALEEN_INDEX = "baleen_index";


	@BeforeClass
	public static void setupClass() throws UIMAException{
		jCas = JCasFactory.createJCas();

		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ELASTICSEARCH, SharedLocalElasticsearchResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Elasticsearch.class, ELASTICSEARCH, erd, "legacy", false);

		ae = AnalysisEngineFactory.createEngine(aed);
		client = ((SharedElasticsearchResource)ae.getUimaContext().getResourceObject(ELASTICSEARCH)).getClient();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNoEntities() throws Exception{
		long timestamp = createNoEntitiesDocument();
		ae.process(jCas);

		//Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());

		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		assertEquals("Hello World", result.getSource().get("content"));
		assertEquals("en", result.getSource().get("language"));
		assertEquals(timestamp, result.getSource().get("dateAccessed"));
		assertEquals("test/no_entities", result.getSource().get("sourceUri"));
		assertEquals("test", result.getSource().get(DOC_TYPE));

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
	public void testMetadata() throws Exception{
		createMetadataDocument();
		ae.process(jCas);

		//Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());

		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		List<String> pids = (List<String>) result.getSource().get("publishedId");
		assertEquals("id_1", pids.get(0));
		assertEquals("id_2", pids.get(1));

		Map<String, Object> metadataMap = (Map<String, Object>) result.getSource().get("metadata");

		assertEquals("D3", metadataMap.get("sourceAndInformationGrading"));

		assertEquals("test_value", metadataMap.get("test_key"));
		assertEquals("Test Title", metadataMap.get("documentTitle"));
		assertEquals("ENG|WAL|SCO", metadataMap.get("countryInfo"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEntities() throws Exception{
		createEntitiesDocument();
		ae.process(jCas);

		//Delay, to allow changes made by consumer to propagate
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
		assertNotNull(person.get(EXTERNAL_ID));

		Map<String, Object> location = entities.get(1);
		assertEquals(9, location.size());
		assertEquals(14, location.get(BEGIN));
		assertEquals(20, location.get(END));
		assertEquals(0.0, location.get(CONFIDENCE));
		assertEquals("Location", location.get(TYPE));
		assertEquals("London", location.get(VALUE));
		assertNotNull(location.get(EXTERNAL_ID));

		Map<String, Object> geometryMap = new HashMap<>();
		geometryMap.put(TYPE, "Point");
		geometryMap.put("coordinates", new ArrayList<Double>(Arrays.asList(-0.1, 51.5)));

		assertEquals(geometryMap, location.get("geoJson"));

		Map<String, Object> date = entities.get(2);
		assertEquals(8, date.size());
		assertEquals(24, date.get(BEGIN));
		assertEquals(42, date.get(END));
		assertEquals(1.0, date.get(CONFIDENCE));
		assertEquals("DateType", date.get(TYPE));
		assertEquals("19th February 2015", date.get(VALUE));
		assertNotNull(date.get(EXTERNAL_ID));

		Map<String, Object> email = entities.get(3);
		assertEquals(8, email.size());
		assertEquals(66, email.get(BEGIN));
		assertEquals(83, email.get(END));
		assertEquals(0.0, email.get(CONFIDENCE));
		assertEquals("CommsIdentifier", email.get(TYPE));
		assertEquals("email", email.get("subType"));
		assertEquals("james@example.com", email.get(VALUE));
		assertNotNull(email.get(EXTERNAL_ID));
	}


	@Test
	public void testReindexEntities() throws Exception{
		createEntitiesDocument();
		ae.process(jCas);
		ae.process(jCas);
		
		// Change the last document so we can check its been updated
		getDocumentAnnotation(jCas).setDocumentClassification("TEST");
		ae.process(jCas);

		//Delay, to allow changes made by consumer to propagate
		Thread.sleep(SLEEP_DELAY);

		assertEquals(1, client.count(new CountRequest(BALEEN_INDEX)).actionGet().getCount());
		SearchHit result = client.search(new SearchRequest()).actionGet().getHits().hits()[0];
		
		// This checks the last document is tone we are getting
		assertEquals("TEST", result.getSource().get("classification"));
	}


	@Test
	public void testContentHash(){
		jCas.setDocumentText("Hello World");
		DocumentAnnotation da = getDocumentAnnotation(jCas);
		da.setSourceUri("test.txt");

		Elasticsearch es = new Elasticsearch();

		assertEquals("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e", es.getExternalIdContent(da, true));
		assertEquals("a6ed0c785d4590bc95c216bcf514384eee6765b1c2b732d0b0a1ad7e14d3204a", es.getExternalIdContent(da, false));
	}
}
