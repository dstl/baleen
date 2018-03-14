// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestVehicle;
import uk.gov.dstl.baleen.types.common.Vehicle;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;

public class GenericVehicleTest extends AbstractAnnotatorTest {
  public GenericVehicleTest() {
    super(GenericVehicle.class);
  }

  @Test
  public void testSingleVehicle() throws UIMAException {
    jCas.setDocumentText("Natalie was seen driving an old rusty, blue boat.");
    createSentences(jCas);
    createWordTokens(jCas);

    processJCas();

    assertAnnotations(1, Vehicle.class, new TestVehicle(0, "old rusty, blue boat", "MARITIME"));
  }

  @Test
  public void testMultipleVehicle() throws UIMAException {
    jCas.setDocumentText("Natalie owns a lime green van and a red jumbo jet.");
    createSentences(jCas);
    createWordTokens(jCas);

    processJCas();

    assertAnnotations(
        2,
        Vehicle.class,
        new TestVehicle(0, "lime green van", "ROAD"),
        new TestVehicle(1, "red jumbo jet", "AIR"));
  }

  @Test
  public void testPlural() throws UIMAException {
    jCas.setDocumentText("Old Sam owned four cars.");
    createSentences(jCas);
    createWordTokens(jCas);

    processJCas();

    assertAnnotations(1, Vehicle.class, new TestVehicle(0, "cars", "ROAD"));
  }

  @Test
  public void testNoDescriptor() throws UIMAException {
    jCas.setDocumentText("Sam owned a locomotive.");
    createSentences(jCas);
    createWordTokens(jCas);

    processJCas();

    assertAnnotations(1, Vehicle.class, new TestVehicle(0, "locomotive", "RAIL"));
  }

  @Test
  public void testMultipleSentences() throws UIMAException {
    jCas.setDocumentText(
        "It was red. Hovercraft was the password. A blue satellite was the future.");
    createSentences(jCas);
    createWordTokens(jCas);

    processJCas();

    assertAnnotations(
        2,
        Vehicle.class,
        new TestVehicle(0, "Hovercraft", "OTHER"),
        new TestVehicle(1, "blue satellite", "SPACE"));
  }

  private void createSentences(JCas jCas) {
    Pattern p = Pattern.compile("[^ ].*?\\.");
    Matcher m = p.matcher(jCas.getDocumentText());
    while (m.find()) {
      new Sentence(jCas, m.start(), m.end()).addToIndexes();
    }
  }

  private void createWordTokens(JCas jCas) {
    Pattern p = Pattern.compile("[A-Za-z]+");
    Matcher m = p.matcher(jCas.getDocumentText());
    while (m.find()) {
      new WordToken(jCas, m.start(), m.end()).addToIndexes();
    }
  }
}
