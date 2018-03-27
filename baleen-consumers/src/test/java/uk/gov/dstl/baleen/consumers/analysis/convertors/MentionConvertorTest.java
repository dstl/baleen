// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.convertors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;
import uk.gov.dstl.baleen.types.semantic.Location;

public class MentionConvertorTest {

  private AnalysisMockData data;
  private MentionConvertor converter;

  @Before
  public void setup() {
    data = new AnalysisMockData();
    converter = new MentionConvertor(data.getMonitor(), data.getIdGenerator(), data.getErc());
  }

  @Test
  public void test() {
    final MentionConvertor converter =
        new MentionConvertor(data.getMonitor(), data.getIdGenerator(), data.getErc());

    final Map<String, BaleenMention> mentions =
        converter.convert(data.getJCas(), data.getDocumentId(), AnalysisMockData.BALEEN_DOC_ID);

    assertEquals(5, mentions.size());

    // Examine Person with pr

    final BaleenMention person =
        mentions.values().stream().filter(p -> p.getValue().equals("Jon")).findFirst().get();

    // Note worried about the values, as long as they are set
    assertEquals("Person", person.getType());
    assertNotEquals(0, person.getBegin());
    assertNotEquals(0, person.getEnd());
    assertEquals(data.getDocumentId(), person.getDocId());
    assertEquals(AnalysisMockData.BALEEN_DOC_ID, person.getBaleenDocId());
    assertNotNull(person.getEntityId());
    assertNotNull(person.getBaleenEntityId());
    assertNotNull(person.getExternalId());
    assertNotNull(person.getBaleenId());
    assertEquals("male", person.getProperties().get("gender"));
  }

  @Test
  public void testGeoJsonPoint() {
    doGeoJsonTest(
        "{ \"type\": \"Point\",  \"coordinates\": [-1, 3] }", "Point", new double[] {-1, 3});
  }

  @Test
  public void testGeoJsonMultiPoint() {
    doGeoJsonTest(
        "{ \"type\": \"MultiPoint\",  \"coordinates\": [[-1, 3],[10, 2]] }",
        "MultiPoint",
        new double[] {-1, 3});
  }

  @Test
  public void testGeoJsonPolygon() {
    doGeoJsonTest(
        "{ \"type\": \"Polygon\",  \"coordinates\": [[[0, 0],[1, 0],[1, 2],[0, 2],[0,0]]] }",
        "Polygon",
        new double[] {0.5, 1});
  }

  @Test
  public void testGeoJsonMultiPolygon() {
    doGeoJsonTest(
        "{ \"type\": \"MultiPolygon\",  \"coordinates\": [[[[0, 0],[1, 0],[1, 2],[0, 2],[0,0]]], [[[10, 10],[11, 10],[11, 12],[10, 12],[10,10]]]] }",
        "MultiPolygon",
        new double[] {0.5, 1});
  }

  @Test
  public void testGeoJsonLineString() {
    doGeoJsonTest(
        "{ \"type\": \"LineString\",  \"coordinates\": [[0, 3],[10, 2]] }",
        "LineString",
        new double[] {5, 2.5});
  }

  private void doGeoJsonTest(
      final String geoJson, final String type, final double[] expectedLonLat) {
    swapGeoJson(geoJson);

    final Map<String, BaleenMention> mentions =
        converter.convert(data.getJCas(), data.getDocumentId(), AnalysisMockData.BALEEN_DOC_ID);

    final BaleenMention l =
        mentions.values().stream().filter(p -> p.getType().equals("Location")).findFirst().get();

    @SuppressWarnings("unchecked")
    final Map<String, Object> geojsonObject =
        (Map<String, Object>) l.getProperties().get(AnalysisConstants.GEOJSON);
    assertEquals(type, geojsonObject.get("type"));

    final double[] actualLonLat = (double[]) l.getProperties().get(AnalysisConstants.POI);

    assertEquals(expectedLonLat[0], actualLonLat[0], 0.01);
    assertEquals(expectedLonLat[1], actualLonLat[1], 0.01);
  }

  private void swapGeoJson(final String geoJson) {
    // Swap out the GeoJson in the location entity for another
    // Note this is not changing the data.getmentions() instance

    JCasUtil.select(data.getJCas(), Location.class).stream().forEach(l -> l.setGeoJson(geoJson));
  }
}
