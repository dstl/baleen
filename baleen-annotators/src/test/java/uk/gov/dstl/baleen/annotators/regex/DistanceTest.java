// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Quantity;

/** Test {@link uk.gov.dstl.baleen.annotators.regex.Distance}. */
public class DistanceTest extends AbstractAnnotatorTest {

  public DistanceTest() {
    super(Distance.class);
  }

  /*
   *  Ensures basic unit detections. These just test that the regex is identifying the correct unit
   */

  // Kilometres
  @Test
  public void testDetectKilometers() throws Exception {
    jCas.setDocumentText("It was 300 km, 300 kilometers, 300 kilometres, 1click North of London.");
    processJCas();
    assertAnnotations(
        4,
        Quantity.class,
        new TestEntity<>(0, "300 km", "300 km"),
        new TestEntity<>(1, "300 kilometers", "300 kilometers"),
        new TestEntity<>(2, "300 kilometres", "300 kilometres"),
        new TestEntity<>(3, "1click", "1click"));
  }

  // Metres
  @Test
  public void testDetectMetres() throws Exception {
    jCas.setDocumentText("It was 300 m, 300 metres, 300meters, 1m long.");
    processJCas();
    assertAnnotations(
        4,
        Quantity.class,
        new TestEntity<>(0, "300 m", "300 m"),
        new TestEntity<>(1, "300 metres", "300 metres"),
        new TestEntity<>(2, "300meters", "300meters"),
        new TestEntity<>(3, "1m", "1m"));
  }

  // Centimetres
  @Test
  public void testDetectCentimetres() throws Exception {
    jCas.setDocumentText("It was 50cm, 50 centimeters, 50 centimetres long.");
    processJCas();
    assertAnnotations(
        3,
        Quantity.class,
        new TestEntity<>(0, "50cm", "50cm"),
        new TestEntity<>(1, "50 centimeters", "50 centimeters"),
        new TestEntity<>(2, "50 centimetres", "50 centimetres"));
  }

  // Millimetres
  @Test
  public void testDetectMilimetres() throws Exception {
    jCas.setDocumentText("It was 1mm, 1 millimetre, 1 millimeter, thick");
    processJCas();
    assertAnnotations(
        3,
        Quantity.class,
        new TestEntity<>(0, "1mm", "1mm"),
        new TestEntity<>(1, "1 millimetre", "1 millimetre"),
        new TestEntity<>(2, "1 millimeter", "1 millimeter"));
  }

  // Miles
  @Test
  public void testDetectMiles() throws Exception {
    jCas.setDocumentText("It was 5 miles, 10miles, 1mile away");
    processJCas();
    assertAnnotations(
        3,
        Quantity.class,
        new TestEntity<>(0, "5 miles", "5 miles"),
        new TestEntity<>(1, "10miles", "10miles"),
        new TestEntity<>(2, "1mile", "1mile"));
  }

  // Yards
  @Test
  public void testDetectYards() throws Exception {
    jCas.setDocumentText("It was 20 yards, 10yds, 1yd away");
    processJCas();
    assertAnnotations(
        3,
        Quantity.class,
        new TestEntity<>(0, "20 yards", "20 yards"),
        new TestEntity<>(1, "10yds", "10yds"),
        new TestEntity<>(2, "1yd", "1yd"));
  }

  // Feet
  @Test
  public void testDetectFeet() throws Exception {
    jCas.setDocumentText("It was 20 feet, 10feet, 1 foot, 1ft away");
    processJCas();
    assertAnnotations(
        4,
        Quantity.class,
        new TestEntity<>(0, "20 feet", "20 feet"),
        new TestEntity<>(1, "10feet", "10feet"),
        new TestEntity<>(2, "1 foot", "1 foot"),
        new TestEntity<>(3, "1ft", "1ft"));
  }

  // Inches
  @Test
  public void testDetectInches() throws Exception {
    jCas.setDocumentText("It was 20 inches, 1inch away");
    processJCas();
    assertAnnotations(
        2,
        Quantity.class,
        new TestEntity<>(0, "20 inches", "20 inches"),
        new TestEntity<>(1, "1inch", "1inch"));
  }

  // Nautical Miles
  @Test
  public void testDetectNauticalMiles() throws Exception {
    jCas.setDocumentText("It was 20 nautical miles, 1nm, 5 nmi, 1 nautical mile away");
    processJCas();
    assertAnnotations(
        4,
        Quantity.class,
        new TestEntity<>(0, "20 nautical miles", "20 nautical miles"),
        new TestEntity<>(1, "1nm", "1nm"),
        new TestEntity<>(2, "5 nmi", "5 nmi"),
        new TestEntity<>(3, "1 nautical mile", "1 nautical mile"));
  }

  /*
   * The following tests (and the utility function below) check that the properties get set correctly
   * and that conversion is done correctly.
   */

  // Utility function to minimise duplication
  public static void checkQuantityProperties(
      Quantity q,
      int begin,
      int end,
      String value,
      Double quantity,
      Double normQuantity,
      String unit,
      String normUnit) {

    // String positions/content
    assertEquals(begin, q.getBegin());
    assertEquals(end, q.getEnd());
    assertEquals(value, q.getValue());

    // Quantities
    assertEquals(quantity, Double.valueOf(q.getQuantity()));
    assertEquals(normQuantity, Double.valueOf(q.getNormalizedQuantity()));

    // units
    assertEquals(unit, q.getUnit());
    assertEquals(normUnit, q.getNormalizedUnit());
  }

  // Kilometres
  @Test
  public void testKm() throws Exception {
    jCas.setDocumentText("It was 400 km North of London.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 13, "400 km", 400d, 400000d, "km", "m");
  }

  // Metres
  @Test
  public void testMetres() throws Exception {
    jCas.setDocumentText("It was 800m North of London.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 11, "800m", 800d, 800d, "m", "m");
  }

  // Centimetres
  @Test
  public void testCentimetres() throws Exception {
    jCas.setDocumentText("It was 50cm wide.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 11, "50cm", 50d, 0.5d, "cm", "m");
  }

  // Millimetres
  @Test
  public void testMillimetres() throws Exception {
    jCas.setDocumentText("It was 1mm thick wide.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 10, "1mm", 1d, 0.001d, "mm", "m");
  }

  // Miles
  @Test
  public void testMiles() throws Exception {
    jCas.setDocumentText("It was 1 mile wide.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 13, "1 mile", 1d, 1609.344, "mi", "m");
  }

  // Yards
  @Test
  public void testYards() throws Exception {
    jCas.setDocumentText("It was 200 yards long.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 16, "200 yards", 200d, 182.88, "yd", "m");
  }

  // Inches
  @Test
  public void testInches() throws Exception {
    jCas.setDocumentText("It was 60 inch deep.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 14, "60 inch", 60d, 1.524, "in", "m");
  }

  // Inches
  @Test
  public void testNauticalMiles() throws Exception {
    jCas.setDocumentText("It was 4 nautical miles wide.");
    processJCas();
    Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
    checkQuantityProperties(q, 7, 23, "4 nautical miles", 4d, 7408d, "nmi", "m");
  }
}
