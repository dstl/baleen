// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCoordinate;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.language.Text;

/** */
public class MgrsTest extends AbstractAnnotatorTest {

  public MgrsTest() {
    super(Mgrs.class);
  }

  @Test
  public void test() throws Exception {

    jCas.setDocumentText("James has almost certainly never been to 4QFJ1267");
    processJCas();

    assertAnnotations(1, Coordinate.class, new TestCoordinate(0, "4QFJ1267", "mgrs", null));
  }

  @Test
  public void testIgnoreDates()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("Bob was born on 19 MAR 1968");
    processJCas("ignoreDates", true);

    assertAnnotations(0, Coordinate.class);
  }

  @Test
  public void testWithText() throws Exception {

    jCas.setDocumentText(
        "James has almost certainly never been to 4QFJ1267. But he's been to 4QFJ1268");

    new Text(jCas, 0, 51).addToIndexes();
    // Dont add the second one ... so we should still get 1 results new Text(jCas, 52,
    // jCas.getDocumentText().length()).addToIndexes();
    processJCas();

    assertAnnotations(1, Coordinate.class, new TestCoordinate(0, "4QFJ1267", "mgrs", null));
  }
}
