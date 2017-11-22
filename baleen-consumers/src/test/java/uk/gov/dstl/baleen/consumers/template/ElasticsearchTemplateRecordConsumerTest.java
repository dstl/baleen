//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dstl.baleen.resources.EmbeddedElasticsearch5;
import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ElasticsearchTemplateRecordConsumerTest extends AbstractTemplateRecordConsumerTest {

	private static final String ELASTICSEARCH = "elasticsearchRest";
	private static final String BALEEN_INDEX = "baleen_record_index";

	private Path tmpDir;

	private EmbeddedElasticsearch5 es5;
	private Client client;

	private AnalysisEngine analysisEngine;

	public ElasticsearchTemplateRecordConsumerTest() {
		super(ElasticsearchTemplateRecordConsumer.class);
	}

	@Before
	public void beforeElasticsearchRecordConsumerTest() throws ResourceInitializationException {
		// Initialise a local instance of Elasticsearch
		try {
			tmpDir = Files.createTempDirectory("elasticsearch");
		} catch (IOException ioe) {
			throw new ResourceInitializationException(ioe);
		}

		try {
			es5 = new EmbeddedElasticsearch5(tmpDir.toString(), "test-cluster");
		}catch (NodeValidationException nve){
			throw new ResourceInitializationException(nve);
		}

		Settings settings = Settings.builder().put("cluster.name", "test-cluster").build();
		try {
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		}catch(UnknownHostException uhe){
			throw new ResourceInitializationException(uhe);
		}

		//Configure
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ELASTICSEARCH,
				SharedElasticsearchRestResource.class, "elasticsearchrest.url", "http://localhost:9200");
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

		client.close();

		try {
			es5.close();
		}catch (IOException ioe){
			//Do nothing
		}

		tmpDir.toFile().delete();
	}
}