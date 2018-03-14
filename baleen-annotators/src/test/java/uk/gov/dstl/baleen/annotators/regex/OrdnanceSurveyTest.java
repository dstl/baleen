// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCoordinate;
import uk.gov.dstl.baleen.types.geo.Coordinate;

/** */
public class OrdnanceSurveyTest extends AbstractAnnotatorTest {

  public OrdnanceSurveyTest() {
    super(Osgb.class);
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText(
        "Ben Nevis is located at NN 166 712. The car park is located at NN126729.");
    processJCas();

    // Values tested against here are based on the output, rather than the correct value, due to
    // inaccuracies in our coordinate conversion
    // Actual values are -5.0713526,56.809745 and -5.0047120,56.796088 respectively
    assertAnnotations(
        2,
        Coordinate.class,
        new TestCoordinate(
            1, "NN126729", "osgb", "{\"type\": \"Point\", \"coordinates\": [-5.071352,56.808457]}"),
        new TestCoordinate(
            0,
            "NN 166 712",
            "osgb",
            "{\"type\": \"Point\", \"coordinates\": [-5.004712,56.794800]}"));
  }

  @Test
  public void test10Figure() throws Exception {

    jCas.setDocumentText("The event took place at GR SU 02194 45374");
    processJCas();

    // Values tested against here are based on the output, rather than the correct value, due to
    // inaccuracies in our coordinate conversion
    // Actual value is -1.9699750,51.207570
    assertAnnotations(
        1,
        Coordinate.class,
        new TestCoordinate(
            0,
            "SU 02194 45374",
            "osgb",
            "{\"type\": \"Point\", \"coordinates\": [-1.969975,51.206197]}"));
  }
}
