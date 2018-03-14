// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;

public class PostgresTest {
  @Test
  public void testAddCRS() throws Exception {
    String orig =
        "{\"type\":\"Polygon\",\"coordinates\":[[[1.0, 1.0],[2.0, 2.0],[3.0, 1.0],[1.0, 1.0]]]}";
    String expected =
        "{\"type\":\"Polygon\",\"coordinates\":[[[1.0,1.0],[2.0,2.0],[3.0,1.0],[1.0,1.0]]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}";

    assertEquals(expected, Postgres.addCrsToGeoJSON(orig));
  }

  @Test
  public void testAddCRSWithExisting() throws Exception {
    String geoJson =
        "{\"type\":\"Polygon\",\"coordinates\":[[[1.0,1.0],[2.0,2.0],[3.0,1.0],[1.0,1.0]]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}";

    assertEquals(geoJson, Postgres.addCrsToGeoJSON(geoJson));
  }

  @Test
  public void testAddCRSMalformed() throws Exception {
    String geoJson =
        "{\"type\":\"Polygon\" \"coordinates\":[1.0,1.0],[2.0,2.0],[3.0,1.0],[1.0,1.0]]]}";

    try {
      Postgres.addCrsToGeoJSON(geoJson);
      fail("Expected exception not thrown");
    } catch (BaleenException e) {
      // Do nothing
    }

    assertEquals(null, Postgres.addCrsToGeoJSON(null));
    assertEquals("", Postgres.addCrsToGeoJSON(""));
  }

  @Test
  public void testGetSuperclass() throws Exception {
    assertEquals(Location.class, Postgres.getSuperclass(Location.class, Coordinate.class));
    assertEquals(Location.class, Postgres.getSuperclass(Coordinate.class, Location.class));
    assertEquals(Location.class, Postgres.getSuperclass(Location.class, Location.class));
    assertEquals(Location.class, Postgres.getSuperclass(Location.class, null));
    assertEquals(Location.class, Postgres.getSuperclass(null, Location.class));
    assertEquals(null, Postgres.getSuperclass(null, null));
  }
}
