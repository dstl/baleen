// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class NormalizeTemporalTest extends AbstractAnnotatorTest {

  public NormalizeTemporalTest() {
    super(NormalizeTemporal.class);
  }

  @Test
  public void testTime() throws Exception {
    jCas.setDocumentText("It was midnight, and all was quiet");

    Temporal t = new Temporal(jCas, 7, 15);
    t.setTimestampStart(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
    t.setTimestampStop(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
    t.setScope("SINGLE");
    t.setTemporalType("TIME");
    t.setValue("midnight");
    t.addToIndexes();

    processJCas(
        NormalizeTemporal.PARAM_DATE_FORMAT,
        "HH:mm",
        NormalizeTemporal.PARAM_TEMPORAL_TYPE,
        "TIME");

    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    Temporal tTest = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
    assertEquals("00:00", tTest.getValue());
    assertTrue(tTest.getIsNormalised());
  }

  @Test
  public void testTimeRange() throws Exception {
    jCas.setDocumentText("It was July, and all was quiet");

    Temporal t = new Temporal(jCas, 7, 11);
    t.setTimestampStart(LocalDateTime.of(2016, 7, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
    t.setTimestampStop(LocalDateTime.of(2016, 8, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
    t.setScope("RANGE");
    t.setTemporalType("DATE");
    t.setValue("July");
    t.addToIndexes();

    processJCas(
        NormalizeTemporal.PARAM_DATE_FORMAT,
        "HH:mm",
        NormalizeTemporal.PARAM_TEMPORAL_TYPE,
        "TIME");

    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    Temporal tTest = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
    assertEquals("July", tTest.getValue());
    assertFalse(tTest.getIsNormalised());
  }

  @Test
  public void testWrongType() throws Exception {
    jCas.setDocumentText("It was midnight, and all was quiet");

    Temporal t = new Temporal(jCas, 7, 15);
    t.setTimestampStart(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
    t.setTimestampStop(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
    t.setScope("SINGLE");
    t.setTemporalType("TIME");
    t.setValue("midnight");
    t.addToIndexes();

    processJCas(
        NormalizeTemporal.PARAM_DATE_FORMAT,
        "HH:mm",
        NormalizeTemporal.PARAM_TEMPORAL_TYPE,
        "DATE");

    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    Temporal tTest = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
    assertEquals("midnight", tTest.getValue());
    assertFalse(tTest.getIsNormalised());
  }

  @Test
  public void testNoTimestamp() throws Exception {
    jCas.setDocumentText("It was midnight, and all was quiet");

    Temporal t = new Temporal(jCas, 7, 15);
    t.setScope("SINGLE");
    t.setTemporalType("TIME");
    t.setValue("midnight");
    t.addToIndexes();

    processJCas(
        NormalizeTemporal.PARAM_DATE_FORMAT,
        "HH:mm",
        NormalizeTemporal.PARAM_TEMPORAL_TYPE,
        "TIME");

    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    Temporal tTest = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
    assertEquals("midnight", tTest.getValue());
    assertFalse(tTest.getIsNormalised());
  }
}
