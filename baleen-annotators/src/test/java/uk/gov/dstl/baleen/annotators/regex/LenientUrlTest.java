// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Url;

public class LenientUrlTest extends AbstractAnnotatorTest {

  public LenientUrlTest() {
    super(LenientUrl.class);
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText(
        "If you visit bbc.co.uk, then you may also want to visit bbc.co.uk/news or http://news.bbc.co.uk, or even news.bbc.co.uk/stories/view.php?id=123. But don't pull out james@example.com");
    processJCas();

    assertEquals(4, JCasUtil.select(jCas, Url.class).size());

    Url url1 = JCasUtil.selectByIndex(jCas, Url.class, 0);
    assertEquals("bbc.co.uk", url1.getCoveredText());

    Url url2 = JCasUtil.selectByIndex(jCas, Url.class, 1);
    assertEquals("bbc.co.uk/news", url2.getCoveredText());

    Url url3 = JCasUtil.selectByIndex(jCas, Url.class, 2);
    assertEquals("http://news.bbc.co.uk", url3.getCoveredText());

    Url url4 = JCasUtil.selectByIndex(jCas, Url.class, 3);
    assertEquals("news.bbc.co.uk/stories/view.php?id=123", url4.getCoveredText());
  }
}
