// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ExternalResourceDescription;
import org.bson.Document;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mongodb.util.JSON;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class MongoTest extends AbstractAnnotatorTest {
  private static final String WORLD = "world";
  private static final String LOCATION = "Location";
  private static final String TYPE = "type";
  private static final String COLLECTION = "collection";
  private static final String FONGO_DATA = "fongo.data";
  private static final String FONGO_COLLECTION = "fongo.collection";
  private static final String MONGO = "mongo";
  private static final String VALUE = "value";
  private static final String MONGO_COLL = "baleen_testing_MongoGazetteerTest";
  private static final Document LONDON_GEOJSON =
      new Document(TYPE, "Point").append("coordinates", Arrays.asList(-0.1275, 51.5072));

  private static final List<Document> GAZ_DATA =
      Lists.newArrayList(
          new Document(VALUE, new String[] {WORLD, "earth", "planet"}),
          new Document(VALUE, new String[] {"london", "londres"}).append("geoJson", LONDON_GEOJSON),
          new Document(VALUE, new String[] {"madrid"}).append("geoJson", "Property Test"),
          new Document(VALUE, new String[] {"sydney (australia"})
              .append("tags", Arrays.asList("broken_regex")));

  private final ExternalResourceDescription erd =
      ExternalResourceFactory.createExternalResourceDescription(
          MONGO,
          SharedFongoResource.class,
          FONGO_COLLECTION,
          MONGO_COLL,
          FONGO_DATA,
          JSON.serialize(GAZ_DATA));

  public MongoTest() {
    super(Mongo.class);
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText("Hello world, this is a test");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);

    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals(WORLD, l.getValue());
    assertEquals(WORLD, l.getCoveredText());
  }

  @Test
  public void testRegex() throws Exception {
    jCas.setDocumentText("Hello Sydney (Australia), this is a test");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);

    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("Sydney (Australia", l.getValue());
    assertEquals("Sydney (Australia", l.getCoveredText());
  }

  @Test
  public void testMidword() throws Exception {
    jCas.setDocumentText("HelloWorld");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);

    assertEquals(0, JCasUtil.select(jCas, Location.class).size());
  }

  @Test
  public void testProperty() throws Exception {
    jCas.setDocumentText("Hello London, this is a test");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);

    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    Location lLon = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", lLon.getValue());
    assertEquals("London", lLon.getCoveredText());
    assertEquals(LONDON_GEOJSON.toJson(), lLon.getGeoJson());

    jCas.reset();

    jCas.setDocumentText("Hello Madrid, this is a test");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);

    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    Location lMad = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("Madrid", lMad.getValue());
    assertEquals("Madrid", lMad.getCoveredText());
    assertEquals("Property Test", lMad.getGeoJson());
  }

  @Test
  public void testBuzzwordProperty() throws Exception {
    jCas.setDocumentText("Hello Sydney (Australia), this is a test");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, "Buzzword");

    assertEquals(1, JCasUtil.select(jCas, Buzzword.class).size());
    Buzzword b = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
    assertEquals("Sydney (Australia", b.getValue());
    assertEquals("Sydney (Australia", b.getCoveredText());

    StringArray tags = b.getTags();
    assertEquals(1, tags.size());
    assertEquals("broken_regex", tags.get(0));
  }

  @Test
  public void testCoref() throws Exception {
    jCas.setDocumentText("Hello World, Hello Earth");
    processJCas(MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);

    assertEquals(2, JCasUtil.select(jCas, Location.class).size());
    assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());

    ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);

    Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("World", l1.getValue());
    assertEquals("World", l1.getCoveredText());
    assertEquals(rt, l1.getReferent());

    Location l2 = JCasUtil.selectByIndex(jCas, Location.class, 1);
    assertEquals("Earth", l2.getValue());
    assertEquals("Earth", l2.getCoveredText());
    assertEquals(rt, l2.getReferent());
  }
}
