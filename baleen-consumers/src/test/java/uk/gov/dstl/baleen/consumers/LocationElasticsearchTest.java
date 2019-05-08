// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_BEGIN;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_CONFIDENCE;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_COORDINATE;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_DOC_ID;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_END;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_EXTERNAL_ID;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_LOCATION;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.FIELD_VALUE;
import static uk.gov.dstl.baleen.consumers.LocationElasticsearch.PARAM_INDEX;
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

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.resources.EmbeddedElasticsearch5;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class LocationElasticsearchTest extends ConsumerTestBase {

  private static final String RESOURCE_KEY = "elasticsearch";
  private static final String LOCATION_INDEX = "location_index";

  private AnalysisEngine ae;
  private EmbeddedElasticsearch5 elasticsearch;

  @Before
  public void setUp() throws Exception {

    jCas.reset();
    elasticsearch = new EmbeddedElasticsearch5();

    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            RESOURCE_KEY,
            SharedElasticsearchResource.class,
            PARAM_PORT,
            Integer.toString(elasticsearch.getTransportPort()),
            PARAM_CLUSTER,
            elasticsearch.getClusterName());

    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            LocationElasticsearch.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            RESOURCE_KEY,
            erd,
            PARAM_INDEX,
            LOCATION_INDEX);

    ae = AnalysisEngineFactory.createEngine(aed);
  }

  @After
  public void tearDown() throws IOException {
    // clearElasticsearchIndex();
    jCas.reset();
    elasticsearch.close();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testLocationsAreSavedToES() throws AnalysisEngineProcessException {
    String text = "(1,2)"; // (lat, lon)
    jCas.setDocumentText(text);
    Coordinate coordinateToBePersisted = new Coordinate(jCas, 0, 5);
    // NB [lon, lan] in geoJson
    coordinateToBePersisted.setGeoJson("{\"type\": \"Point\", \"coordinates\": [2, 1]}");
    coordinateToBePersisted.setCoordinateValue("1,2");
    coordinateToBePersisted.setConfidence(0.5);
    coordinateToBePersisted.setValue(text);
    coordinateToBePersisted.addToIndexes(jCas);

    Coordinate coordinateNotToBePersisted = new Coordinate(jCas, 0, 5);
    coordinateNotToBePersisted.setCoordinateValue(null);
    coordinateNotToBePersisted.setGeoJson(null);
    coordinateNotToBePersisted.addToIndexes(jCas);

    ae.process(jCas);

    elasticsearch.flush(LOCATION_INDEX);

    SearchResponse sr = elasticsearch.client().prepareSearch(LOCATION_INDEX).execute().actionGet();

    assertEquals("Should be 1 result in Elasticsaearch", 1, sr.getHits().getTotalHits());

    SearchHit searchHit = sr.getHits().getHits()[0];
    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
    assertEquals("Should be text", text, sourceAsMap.get(FIELD_VALUE));
    assertEquals("Should be 0", 0, sourceAsMap.get(FIELD_BEGIN));
    assertEquals("Should be 5", 5, sourceAsMap.get(FIELD_END));
    Map<String, Object> location = (Map<String, Object>) sourceAsMap.get(FIELD_LOCATION);
    assertEquals("Should be Point", "Point", location.get("type"));
    assertEquals("Should be [2.0, 1.0]", ImmutableList.of(2.0, 1.0), location.get("coordinates"));
    assertEquals("Should be in es format", "1.0, 2.0", sourceAsMap.get(FIELD_COORDINATE));
    assertEquals("Should be 0.5", 0.5, sourceAsMap.get(FIELD_CONFIDENCE));
    assertEquals(
        "Should be external id",
        "9b1da85a5367efa0001a076ab20943e568f635e00159dedc868c0f4c7bab3773",
        sourceAsMap.get(FIELD_EXTERNAL_ID));
    assertEquals(
        "Should be doc id",
        "418abde75db9a5538d0798bf957a63d2b7976de9a4136992176b6de58d562e79",
        sourceAsMap.get(FIELD_DOC_ID));
  }
}
