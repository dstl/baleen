// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition;

public class TemplateRecordDefinitionAnnotatorTest extends AbstractAnnotatorTest {

  private static final String RECORD_TEXT =
      " We may also find text of interest in table form, such as this:<<record:foo:begin>> \n"
          + "    Full Name:  \n"
          + "    <<field:PersonFullName>> \n"
          + " Description: \n"
          + " <<field:Description>><<record:foo:end>>\n"
          + "Some text afterwards.\n";

  private static final String REPEAT_RECORD_TEXT =
      " We may also find text of interest in table form, such as this:<<record:foo begin repeat>> \n"
          + "    Full Name:  \n"
          + "    <<field:PersonFullName>> \n"
          + " Description: \n <<field:Description>><<record:foo>>\n"
          + "Some text afterwards.\n";

  private static final String RECORD2_TEXT = RECORD_TEXT + RECORD_TEXT;

  public TemplateRecordDefinitionAnnotatorTest() {
    super(TemplateRecordDefinitionAnnotator.class);
  }

  @Test
  public void annotateRecord()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(RECORD_TEXT);
    processJCas();
    TemplateRecordDefinition record =
        JCasUtil.selectByIndex(jCas, TemplateRecordDefinition.class, 0);
    assertEquals(83, record.getBegin());
    assertEquals(169, record.getEnd());
    assertFalse(record.getRepeat());
    assertEquals(
        " \n"
            + "    Full Name:  \n"
            + "    <<field:PersonFullName>> \n"
            + " Description: \n"
            + " <<field:Description>>",
        record.getCoveredText());
  }

  @Test
  public void annotate2Record()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(RECORD2_TEXT);
    processJCas();
    TemplateRecordDefinition record =
        JCasUtil.selectByIndex(jCas, TemplateRecordDefinition.class, 0);
    assertEquals(83, record.getBegin());
    assertEquals(169, record.getEnd());
    assertEquals(
        " \n"
            + "    Full Name:  \n"
            + "    <<field:PersonFullName>> \n"
            + " Description: \n"
            + " <<field:Description>>",
        record.getCoveredText());

    TemplateRecordDefinition record2 =
        JCasUtil.selectByIndex(jCas, TemplateRecordDefinition.class, 1);
    assertEquals(293, record2.getBegin());
    assertEquals(379, record2.getEnd());
    assertEquals(
        " \n"
            + "    Full Name:  \n"
            + "    <<field:PersonFullName>> \n"
            + " Description: \n"
            + " <<field:Description>>",
        record2.getCoveredText());
  }

  @Test
  public void annotateRepeatingRecord()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(REPEAT_RECORD_TEXT);
    processJCas();
    TemplateRecordDefinition record =
        JCasUtil.selectByIndex(jCas, TemplateRecordDefinition.class, 0);
    assertEquals(90, record.getBegin());
    assertEquals(176, record.getEnd());
    assertTrue(record.getRepeat());
    assertEquals(
        " \n"
            + "    Full Name:  \n"
            + "    <<field:PersonFullName>> \n"
            + " Description: \n"
            + " <<field:Description>>",
        record.getCoveredText());
  }
}
