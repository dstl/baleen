//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;

public class ElasticsearchTemplateRecordConsumerTest extends AbstractTemplateRecordConsumerTest {

	private static final String ELASTICSEARCH = "elasticsearchRest";

	private static final String BALEEN_INDEX = "baleen_record_index";

	private static Path tmpDir;

	private static Client client;

	private static Node node;

	private AnalysisEngine analysisEngine;

	@BeforeClass
	public static void setupLocalElasticsearch() throws UIMAException {
		// Initialise a local instance of Elasticsearch
		try {
			tmpDir = Files.createTempDirectory("elasticsearch");
		} catch (IOException ioe) {
			throw new ResourceInitializationException(ioe);
		}

		// Don't use the default ports for testing purposes
		Settings settings = Settings.builder().put("path.home", tmpDir.toString()).put("http.port", "29200")
				.put("transport.tcp.port", "29300").build();

		node = NodeBuilder.nodeBuilder().settings(settings).data(true).local(true).clusterName("test_cluster").node();

		client = node.client();
	}

	public ElasticsearchTemplateRecordConsumerTest() {
		super(ElasticsearchTemplateRecordConsumer.class);
	}

	@Before
	public void beforeElasticsearchRecordConsumerTest() throws ResourceInitializationException {
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ELASTICSEARCH,
				SharedElasticsearchRestResource.class, "elasticsearchrest.url", "http://localhost:29200");
		analysisEngine = getAnalysisEngine(ELASTICSEARCH, erd);
		client.admin().indices().refresh(new RefreshRequest("baleen_record_index")).actionGet();
	}

	@Test
	public void testSave() throws Exception {
		assertEquals(new Long(0), getCount());
		process();
		assertEquals(new Long(3), getCount());
	}

	@Test
	public void testRecords()
			throws JsonParseException, JsonMappingException, IOException, AnalysisEngineProcessException {
		assertEquals(new Long(0), getCount());
		process();

		SearchHits hits = client.search(new SearchRequest().indices("baleen_record_index")).actionGet().getHits();
		assertEquals(3, hits.getTotalHits());

		ObjectMapper mapper = new ObjectMapper();
		Collection<ExtractedRecord> records = new ArrayList<>();
		for (SearchHit hit : hits.getHits()) {
			String json = hit.getSourceAsString();
			ElasticsearchExtractedRecord er = mapper.readValue(json, ElasticsearchExtractedRecord.class);
			records.add(er);
		}
		assertEquals(3, records.size());
		Map<String, Collection<ExtractedRecord>> recordsMap = new HashMap<>();
		recordsMap.put(annotatorClass.getSimpleName(), records);
		checkRecords(recordsMap);
	}

	private void process() throws AnalysisEngineProcessException {
		analysisEngine.process(jCas);
		client.admin().indices().refresh(new RefreshRequest("baleen_record_index")).actionGet();
	}

	private Long getCount() {
		SearchResponse sr = client.prepareSearch(BALEEN_INDEX).setSize(0).execute().actionGet();
		return sr.getHits().getTotalHits();
	}

	@After
	public void afterElasticsearchRecordConsumerTest() {
		analysisEngine.destroy();
		client.admin().indices().delete(new DeleteIndexRequest("baleen_record_index")).actionGet();
	}

	@AfterClass
	public static void destroyLocalElasticSearch() {
		client.close();
		node.close();
	}
}