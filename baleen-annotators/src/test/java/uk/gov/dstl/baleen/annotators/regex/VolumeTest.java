// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestQuantity;
import uk.gov.dstl.baleen.types.common.Quantity;

/** Tests for {@link uk.gov.dstl.baleen.annotators.regex.Volume}. */
public class VolumeTest extends AbstractAnnotatorTest {

  private static final String VOLUME = "volume";

  public VolumeTest() {
    super(Volume.class);
  }

  @Test
  public void testM3() throws Exception {

    jCas.setDocumentText("There was approximately 2 cubic metres of water found.");
    processJCas();

    assertAnnotations(
        1, Quantity.class, new TestQuantity(0, "2 cubic metres", 2, "m^3", 2, "m^3", VOLUME));
  }

  @Test
  public void testCM3() throws Exception {

    jCas.setDocumentText("4.7cm^3 of blue powder and 4.7 cubic centimetres of yellow powder.");
    processJCas();

    assertAnnotations(
        2,
        Quantity.class,
        new TestQuantity(0, "4.7cm^3", 4.7, "cm^3", 0.0000047, "m^3", VOLUME),
        new TestQuantity(1, "4.7 cubic centimetres", 4.7, "cm^3", 0.0000047, "m^3", VOLUME));
  }

  @Test
  public void testL() throws Exception {
    jCas.setDocumentText(
        "A 20 litre container was found hidden in the bushes. It contained 4.3l of petrol.");
    processJCas();

    assertAnnotations(
        2,
        Quantity.class,
        new TestQuantity(0, "20 litre", 20.0, "l", 0.02, "m^3", VOLUME),
        new TestQuantity(1, "4.3l", 4.3, "l", 0.0043, "m^3", VOLUME));
  }

  @Test
  public void testML() throws Exception {
    jCas.setDocumentText("A shot can be 25ml or 35 millilitres.");
    processJCas();

    assertAnnotations(
        2,
        Quantity.class,
        new TestQuantity(0, "25ml", 25.0, "ml", 0.000025, "m^3", VOLUME),
        new TestQuantity(1, "35 millilitres", 35, "ml", 0.000035, "m^3", VOLUME));
  }

  @Test
  public void testPint() throws Exception {

    jCas.setDocumentText("5.4 pints later, Tom had had enough to drink.");
    processJCas();

    assertAnnotations(
        1,
        Quantity.class,
        new TestQuantity(
            0,
            "5.4 pints",
            5.4,
            "pt",
            5.4 * uk.gov.dstl.baleen.annotators.regex.Volume.PINT_TO_M3,
            "m^3",
            VOLUME));
  }

  @Test
  public void testGallon() throws Exception {

    jCas.setDocumentText("She filled the car up with 7 gallons of fuel.");
    processJCas();

    assertAnnotations(
        1,
        Quantity.class,
        new TestQuantity(
            0,
            "7 gallons",
            7,
            "gal",
            7 * uk.gov.dstl.baleen.annotators.regex.Volume.GALLON_TO_M3,
            "m^3",
            VOLUME));
  }

  @Test
  public void testMultiplier() throws Exception {

    jCas.setDocumentText("There were 3.8 million litres of water in the aquifer.");
    processJCas();

    assertAnnotations(
        1,
        Quantity.class,
        new TestQuantity(0, "3.8 million litres", 3800000, "l", 3800, "m^3", VOLUME));
  }

  @Test
  public void testPunctuation() throws Exception {

    jCas.setDocumentText("3,700.3m^3 is a valid volume; 40.ml isn't.");
    processJCas();

    assertAnnotations(
        1, Quantity.class, new TestQuantity(0, "3,700.3m^3", 3700.3, "m^3", 3700.3, "m^3", VOLUME));
  }
}
