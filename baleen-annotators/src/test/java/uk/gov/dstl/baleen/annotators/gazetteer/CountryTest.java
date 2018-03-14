// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.types.semantic.Location;

public class CountryTest extends AbstractAnnotatorTest {
  private static final String COUNTRY = "country";
  private static final String PREFIX = "Last month, Peter visited the coast of ";

  private final ExternalResourceDescription erd =
      ExternalResourceFactory.createExternalResourceDescription(
          COUNTRY, SharedCountryResource.class);

  public CountryTest() {
    super(Country.class);
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText(PREFIX + "Jamaica");
    processJCas(COUNTRY, erd);

    testJCas(jCas, "Jamaica");
  }

  @Test
  public void testCaseSensitive() throws Exception {
    jCas.setDocumentText(PREFIX + "JamaICA");
    processJCas(COUNTRY, erd, Country.PARAM_CASE_SENSITIVE, true);

    assertEquals(0, JCasUtil.select(jCas, Location.class).size());
  }

  @Test
  public void testUTF() throws Exception {
    jCas.setDocumentText(
        PREFIX + "\u062c\u0645\u0647\u0648\u0631\u064a\u0629 \u062c\u064a\u0628\u0648\u062a\u064a");
    processJCas(COUNTRY, erd);

    testJCas(
        jCas, "\u062c\u0645\u0647\u0648\u0631\u064a\u0629 \u062c\u064a\u0628\u0648\u062a\u064a");
  }

  @Test
  public void testWrongType() throws Exception {
    jCas.setDocumentText(PREFIX + "Jamaica");
    processJCas(COUNTRY, erd, Country.PARAM_TYPE, "Person");

    testJCas(jCas, "Jamaica");
  }

  private void testJCas(JCas jCas, String s) {
    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals(s, l.getValue());
    assertNotNull(l.getGeoJson());
  }
}
