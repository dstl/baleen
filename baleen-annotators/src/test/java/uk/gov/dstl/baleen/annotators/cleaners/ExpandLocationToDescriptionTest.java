// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Location;

public class ExpandLocationToDescriptionTest extends AbstractAnnotatorTest {
  public ExpandLocationToDescriptionTest() {
    super(ExpandLocationToDescription.class);
  }

  @Test
  public void test() throws UIMAException {
    jCas.setDocumentText("The weapons were found 50 miles south-west of London");

    Quantity q = new Quantity(jCas);
    q.setBegin(23);
    q.setEnd(31);
    q.setSubType("distance");
    q.addToIndexes();

    Location l = new Location(jCas, 46, 52);
    l.addToIndexes();

    processJCas();

    assertEquals("50 miles south-west of London", l.getCoveredText());
    assertEquals(1, JCasUtil.select(jCas, Quantity.class).size());
  }

  @Test
  public void test2() throws UIMAException {
    jCas.setDocumentText("It happened in northern Syria.");

    Location l = new Location(jCas, 24, 29);
    l.addToIndexes();

    processJCas();

    assertEquals("northern Syria", l.getCoveredText());
    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
  }

  @Test
  public void testRemoveQuantity() throws UIMAException {
    jCas.setDocumentText("The weapons were found 20 miles north of London");

    Quantity q = new Quantity(jCas);
    q.setBegin(23);
    q.setEnd(31);
    q.setSubType("distance");
    q.addToIndexes();

    Location l = new Location(jCas, 41, 47);
    l.addToIndexes();

    processJCas(ExpandLocationToDescription.PARAM_REMOVE_QUANTITY, true);

    assertEquals("20 miles north of London", l.getCoveredText());
    assertEquals(0, JCasUtil.select(jCas, Quantity.class).size());
  }

  @Test
  public void testNoQuantity() throws UIMAException {
    jCas.setDocumentText("The weapons were found 20 miles N of London");

    Location l = new Location(jCas, 37, 43);
    l.addToIndexes();

    processJCas();

    assertEquals("N of London", l.getCoveredText());
    assertEquals(0, JCasUtil.select(jCas, Quantity.class).size());
  }

  @Test
  public void testArea() throws UIMAException {
    jCas.setDocumentText("The weapons were found in the vicinity of Tower Bridge");

    Location l = new Location(jCas, 42, 54);
    l.addToIndexes();

    processJCas();

    assertEquals("the vicinity of Tower Bridge", l.getCoveredText());
    assertEquals(0, JCasUtil.select(jCas, Quantity.class).size());
  }
}
