// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_BEGIN;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_CONFIDENCE;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_DATE;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_DATE_RANGE;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_DOC_ID;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_END;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_EXTERNAL_ID;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_GTE;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_LTE;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.FIELD_VALUE;
import static uk.gov.dstl.baleen.consumers.TemporalElasticsearch.PARAM_INDEX;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_CLUSTER;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_PORT;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.EmbeddedElasticsearch5;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.utils.TemporalUtils;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class TemporalElasticsearchTest extends ConsumerTestBase {

  private static final String RESOURCE_KEY = "elasticsearch";
  private static final String TEMPORAL_INDEX = "temporal_index";

  private AnalysisEngine ae;
  private EmbeddedElasticsearch5 elasticsearch;

  @Before
  public void setUp() throws Exception {
    elasticsearch = new EmbeddedElasticsearch5();

    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            RESOURCE_KEY,
            SharedElasticsearchResource.class,
            PARAM_PORT,
            Integer.toString(elasticsearch.getTransportPort()),
            PARAM_CLUSTER,
            elasticsearch.getClusterName());

    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            TemporalElasticsearch.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            RESOURCE_KEY,
            erd,
            PARAM_INDEX,
            TEMPORAL_INDEX);

    ae = AnalysisEngineFactory.createEngine(aed);
  }

  @After
  public void tearDown() throws IOException {
    elasticsearch.close();
  }

  @Test
  public void testTemporalsArePersistedToES() throws AnalysisEngineProcessException {
    String text = "26 November 1992";
    jCas.setDocumentText(text);
    Temporal temporal = new Temporal(jCas, 0, text.length());
    temporal.setPrecision(TemporalUtils.PRECISION_EXACT);
    temporal.setScope(TemporalUtils.SCOPE_SINGLE);
    temporal.setBegin(0);
    temporal.setEnd(text.length());
    temporal.setTemporalType("Date");
    temporal.setValue(text);
    temporal.setConfidence(1.0);
    temporal.setTimestampStart(12345L);
    temporal.addToIndexes(jCas);

    ae.process(jCas);

    elasticsearch.flush(TEMPORAL_INDEX);

    SearchResponse sr = elasticsearch.client().prepareSearch(TEMPORAL_INDEX).execute().actionGet();

    assertEquals("Should be 1 result in Elasticsaearch", 1, sr.getHits().getTotalHits());
    SearchHit searchHit = sr.getHits().getHits()[0];
    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
    assertEquals("Should be text", text, sourceAsMap.get(FIELD_VALUE));
    assertEquals("Should be 0", 0, sourceAsMap.get(FIELD_BEGIN));
    assertEquals("Should be text length", text.length(), sourceAsMap.get(FIELD_END));
    assertEquals("Should be 1", 1.0, sourceAsMap.get(FIELD_CONFIDENCE));
    assertEquals(
        "Should be 12345",
        "5581b64be63bde7fa0f90b5fa69205b817050413680073c0bdbdae7fdc940a3e",
        sourceAsMap.get(FIELD_EXTERNAL_ID));
    assertEquals(
        "Should be doc id",
        "0e33104d2a045c15c619db9a832c2c67b565f6e0c827970be9f69c6243991483",
        sourceAsMap.get(FIELD_DOC_ID));
    assertNull("Should be null", sourceAsMap.get(FIELD_DATE_RANGE));
    assertEquals("Should be 12345", 12345, sourceAsMap.get(FIELD_DATE));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testTemporalsArePersistedToESWithRange() throws AnalysisEngineProcessException {
    String text = "26 November 1992";
    jCas.setDocumentText(text);
    Temporal temporal = new Temporal(jCas, 0, text.length());
    temporal.setPrecision(TemporalUtils.PRECISION_EXACT);
    temporal.setScope(TemporalUtils.SCOPE_RANGE);
    temporal.setBegin(0);
    temporal.setEnd(text.length());
    temporal.setTemporalType("Date");
    temporal.setValue(text);
    temporal.setTimestampStart(12345L);
    temporal.setTimestampStop(54321L);
    temporal.addToIndexes(jCas);

    ae.process(jCas);

    elasticsearch.flush(TEMPORAL_INDEX);

    SearchResponse sr = elasticsearch.client().prepareSearch(TEMPORAL_INDEX).execute().actionGet();

    assertEquals("Should be 1 result in Elasticsaearch", 1, sr.getHits().getTotalHits());
    SearchHit searchHit = sr.getHits().getHits()[0];
    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
    assertEquals("Should be text", text, sourceAsMap.get(FIELD_VALUE));
    assertEquals("Should be 0", 0, sourceAsMap.get(FIELD_BEGIN));
    assertEquals("Should be text length", text.length(), sourceAsMap.get(FIELD_END));
    assertNull("Should be null", sourceAsMap.get(FIELD_DATE));
    assertEquals(
        "Should be external id",
        "2410963236c1d5f4a8ef7c2db4f8ea37382d9c4a6a01cfd7125d4fd7fcd3fdac",
        sourceAsMap.get(FIELD_EXTERNAL_ID));
    assertEquals(
        "Should be doc id",
        "0e33104d2a045c15c619db9a832c2c67b565f6e0c827970be9f69c6243991483",
        sourceAsMap.get(FIELD_DOC_ID));
    Map<String, Object> dateRange = (Map<String, Object>) sourceAsMap.get(FIELD_DATE_RANGE);

    assertEquals("Should be 12345", 12345, dateRange.get(FIELD_GTE));
    assertEquals("Should be 54321", 54321, dateRange.get(FIELD_LTE));
  }
}
