// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class EpochTimeTest extends AbstractAnnotatorTest {
  public EpochTimeTest() {
    super(EpochTime.class);
  }

  @Test
  public void testMilli() throws Exception {
    jCas.setDocumentText("47 people were seen at 1507725753567.");
    processJCas(EpochTime.PARAM_EARLIEST, "1500000000", EpochTime.PARAM_MILLIS, true);

    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    Temporal t = JCasUtil.selectByIndex(jCas, Temporal.class, 0);

    assertEquals("1507725753567", t.getCoveredText());
    assertEquals(1507725753, t.getTimestampStart());
    assertEquals(1507725754, t.getTimestampStop());
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText("47 people were seen at 1507725753.");
    processJCas(EpochTime.PARAM_EARLIEST, "1500000000");

    assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
    Temporal t = JCasUtil.selectByIndex(jCas, Temporal.class, 0);

    assertEquals("1507725753", t.getCoveredText());
    assertEquals(1507725753, t.getTimestampStart());
    assertEquals(1507725754, t.getTimestampStop());
  }
}
