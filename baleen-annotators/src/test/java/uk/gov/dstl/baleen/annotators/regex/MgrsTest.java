// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.apache.commons.lang3.SystemUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCoordinate;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.language.Text;

/** */
public class MgrsTest extends AbstractAnnotatorTest {

  private static final String GEOJSON =
      "{\"type\":\"Polygon\",\"coordinates\":[[[-157.90975514490475,21.410749379555252],[-157.91946842978894,21.410749379555252],[-157.91946842978894,21.401778260360324],[-157.90975514490475,21.401778260360324],[-157.90975514490475,21.410749379555252]]]}";
  private static final String GEOJSON_8 =
      "{\"type\":\"Polygon\",\"coordinates\":[[[-157.90975514490475,21.41074937955525],[-157.91946842978894,21.41074937955525],[-157.91946842978894,21.401778260360324],[-157.90975514490475,21.401778260360324],[-157.90975514490475,21.41074937955525]]]}";

  public MgrsTest() {
    super(Mgrs.class);
  }

  @Test
  public void test() throws Exception {

    jCas.setDocumentText("James has almost certainly never been to 4QFJ1267");
    processJCas();

    String geoJson;
    if (SystemUtils.IS_JAVA_1_8) {
      geoJson = GEOJSON_8;
    } else {
      geoJson = GEOJSON;
    }

    assertAnnotations(1, Coordinate.class, new TestCoordinate(0, "4QFJ1267", "mgrs", geoJson));
  }

  @Test
  public void testIgnoreDates()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("Bob was born on 19 MAR 1968");
    processJCas("ignoreDates", true);

    assertAnnotations(0, Coordinate.class);
  }

  @Test
  public void testWithText() throws Exception {

    jCas.setDocumentText(
        "James has almost certainly never been to 4QFJ1267. But he's been to 4QFJ1268");

    new Text(jCas, 0, 51).addToIndexes();
    // Dont add the second one ... so we should still get 1 results new Text(jCas, 52,
    // jCas.getDocumentText().length()).addToIndexes();
    processJCas();

    String geoJson;
    if (SystemUtils.IS_JAVA_1_8) {
      geoJson = GEOJSON_8;
    } else {
      geoJson = GEOJSON;
    }
    assertAnnotations(1, Coordinate.class, new TestCoordinate(0, "4QFJ1267", "mgrs", geoJson));
  }
}
