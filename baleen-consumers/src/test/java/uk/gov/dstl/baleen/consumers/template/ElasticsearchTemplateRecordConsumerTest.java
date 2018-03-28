// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource.PARAM_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dstl.baleen.resources.EmbeddedElasticsearch5;
import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;

public class ElasticsearchTemplateRecordConsumerTest extends AbstractTemplateRecordConsumerTest {

  private static final String RESOURCE_KEY = "elasticsearchRest";
  private static final String BALEEN_INDEX = "baleen_record_index";

  private EmbeddedElasticsearch5 elasticsearch;
  private AnalysisEngine analysisEngine;

  public ElasticsearchTemplateRecordConsumerTest() {
    super(ElasticsearchTemplateRecordConsumer.class);
  }

  @Before
  public void beforeElasticsearchRecordConsumerTest() throws Exception {

    elasticsearch = new EmbeddedElasticsearch5();

    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            RESOURCE_KEY,
            SharedElasticsearchRestResource.class,
            PARAM_URL,
            elasticsearch.getHttpUrl());

    analysisEngine = getAnalysisEngine(RESOURCE_KEY, erd);
    elasticsearch.client().admin().indices().refresh(new RefreshRequest(BALEEN_INDEX)).actionGet();
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

    SearchHits hits =
        elasticsearch
            .client()
            .search(new SearchRequest().indices(BALEEN_INDEX))
            .actionGet()
            .getHits();
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
    elasticsearch.client().admin().indices().refresh(new RefreshRequest(BALEEN_INDEX)).actionGet();
  }

  private Long getCount() {
    SearchResponse sr =
        elasticsearch.client().prepareSearch(BALEEN_INDEX).setSize(0).execute().actionGet();
    return sr.getHits().getTotalHits();
  }

  @After
  public void afterElasticsearchRecordConsumerTest() {
    analysisEngine.destroy();
    try {
      elasticsearch.close();
    } catch (IOException ioe) {
      // Do nothing
    }
  }
}
