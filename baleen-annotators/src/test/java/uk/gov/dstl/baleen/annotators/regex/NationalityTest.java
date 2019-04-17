// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.internals.NationalityRegex;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.language.Text;

/** */
public class NationalityTest extends AnnotatorTestBase {
  AnalysisEngine ae;

  @Override
  @Before
  public void beforeTest() throws UIMAException {
    super.beforeTest();

    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            "country", SharedCountryResource.class);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(NationalityRegex.class, "country", erd);

    ae = AnalysisEngineFactory.createEngine(aed);
  }

  @After
  public void afterTest() {
    ae.destroy();
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText(
        "James is a BRITISH national. Last month, he met an Irish bloke in the pub. He is friends with Bob, who is an Spanish.");
    ae.process(jCas);

    assertEquals(3, JCasUtil.select(jCas, Nationality.class).size());

    Nationality british = JCasUtil.selectByIndex(jCas, Nationality.class, 0);
    assertNotNull(british);
    assertEquals("BRITISH", british.getCoveredText());
    assertEquals("BRITISH", british.getValue());
    assertEquals("GBR", british.getCountryCode());

    Nationality irish = JCasUtil.selectByIndex(jCas, Nationality.class, 1);
    assertNotNull(irish);
    assertEquals("Irish", irish.getCoveredText());
    assertEquals("Irish", irish.getValue());
    assertEquals("IRL", irish.getCountryCode());

    Nationality spanish = JCasUtil.selectByIndex(jCas, Nationality.class, 2);
    assertNotNull(spanish);
    assertEquals("Spanish", spanish.getCoveredText());
    assertEquals("Spanish", spanish.getValue());
    assertEquals("ESP", spanish.getCountryCode());
  }

  @Test
  public void test2() throws Exception {
    jCas.setDocumentText("Derek is from Afghanistan");
    ae.process(jCas);

    assertEquals(0, JCasUtil.select(jCas, Nationality.class).size());
  }

  @Test
  public void testWithText() throws Exception {
    jCas.setDocumentText(
        "James is a BRITISH national. Last month, he met an Irish bloke in the pub. He is friends with Bob, who is an Spanish.");

    new Text(jCas, 30, 75).addToIndexes();
    new Text(jCas, 76, jCas.getDocumentText().length()).addToIndexes();

    ae.process(jCas);

    assertEquals(2, JCasUtil.select(jCas, Nationality.class).size());

    Nationality irish = JCasUtil.selectByIndex(jCas, Nationality.class, 0);
    assertNotNull(irish);
    assertEquals("Irish", irish.getCoveredText());
    assertEquals("Irish", irish.getValue());
    assertEquals("IRL", irish.getCountryCode());

    Nationality spanish = JCasUtil.selectByIndex(jCas, Nationality.class, 1);
    assertNotNull(spanish);
    assertEquals("Spanish", spanish.getCoveredText());
    assertEquals("Spanish", spanish.getValue());
    assertEquals("ESP", spanish.getCountryCode());
  }
}
