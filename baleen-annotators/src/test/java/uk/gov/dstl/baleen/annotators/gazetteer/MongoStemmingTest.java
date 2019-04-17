// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class MongoStemmingTest extends AbstractAnnotatorTest {

  public MongoStemmingTest() {
    super(MongoStemming.class);
  }

  private static final String LOCATION = "Location";
  private static final String BUZZWORD = "Buzzword";
  private static final String TYPE = "type";
  private static final String COLLECTION = "collection";
  private static final String FONGO_DATA = "fongo.data";
  private static final String FONGO_COLLECTION = "fongo.collection";
  private static final String MONGO = "mongo";
  private static final String VALUE = "value";
  private static final String MONGO_COLL = "baleen_testing_MongoStemmingRadixTreeGazetteerTest";
  private static final Document LONDON_GEOJSON =
      new Document(TYPE, "Point").append("coordinates", Arrays.asList(-0.1275, 51.5072));

  private static final List<Document> GAZ_DATA =
      Lists.newArrayList(
          new Document(VALUE, new String[] {"conspiracy", "conspire", "scheme", "plot"}),
          new Document(VALUE, new String[] {"london", "londres"}).append("geoJson", LONDON_GEOJSON),
          new Document(VALUE, new String[] {"knight", "sir", "dame", "lady"}),
          new Document(VALUE, new String[] {"enter the room"}));

  private static ExternalResourceDescription erd;

  @BeforeClass
  public static void setup() throws JsonProcessingException {
    erd =
        ExternalResourceFactory.createNamedResourceDescription(
            MONGO,
            SharedFongoResource.class,
            FONGO_COLLECTION,
            MONGO_COLL,
            FONGO_DATA,
            new ObjectMapper().writeValueAsString(GAZ_DATA));
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText("Forty seven knights conspired against the crown.");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);

    assertEquals(2, JCasUtil.select(jCas, Buzzword.class).size());

    Buzzword b1 = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
    assertEquals("knights", b1.getValue());
    assertEquals("knights", b1.getCoveredText());

    Buzzword b2 = JCasUtil.selectByIndex(jCas, Buzzword.class, 1);
    assertEquals("conspired", b2.getValue());
    assertEquals("conspired", b2.getCoveredText());
  }

  @Test
  public void testMultipleWords() throws Exception {
    jCas.setDocumentText("Bill and Ben entered the room on a dark and windy night.");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);

    assertEquals(1, JCasUtil.select(jCas, Buzzword.class).size());

    Buzzword b1 = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
    assertEquals("entered the room", b1.getValue());
    assertEquals("entered the room", b1.getCoveredText());
  }

  @Test
  public void testMidword() throws Exception {
    jCas.setDocumentText("Desiring chocolate is not a sin");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);

    assertEquals(0, JCasUtil.select(jCas, Buzzword.class).size());
  }

  @Test
  public void testProperty() throws Exception {
    jCas.setDocumentText("Guy Fawkes was caught in London");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);

    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    Location lLon = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", lLon.getValue());
    assertEquals("London", lLon.getCoveredText());
    assertEquals(LONDON_GEOJSON.toJson(), lLon.getGeoJson());
  }

  @Test
  public void testCoref() throws Exception {
    jCas.setDocumentText("Lords, ladies, sirs, and madames...");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);

    assertEquals(2, JCasUtil.select(jCas, Buzzword.class).size());
    assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());

    ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);

    Buzzword b1 = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
    assertEquals("ladies", b1.getValue());
    assertEquals("ladies", b1.getCoveredText());
    assertEquals(rt, b1.getReferent());

    Buzzword b2 = JCasUtil.selectByIndex(jCas, Buzzword.class, 1);
    assertEquals("sirs", b2.getValue());
    assertEquals("sirs", b2.getCoveredText());
    assertEquals(rt, b2.getReferent());
  }
}
