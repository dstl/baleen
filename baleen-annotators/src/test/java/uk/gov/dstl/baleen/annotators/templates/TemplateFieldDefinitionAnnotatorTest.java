// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition;

public class TemplateFieldDefinitionAnnotatorTest extends AbstractAnnotatorTest {

  private static final String FIELD_TEXT = "Full Name <<field:PersonFullName>>  \n";
  private static final String FIELD2_TEXT =
      FIELD_TEXT + " Description: \n" + " <<field:Description>>   More text\n";
  private static final String FIELD_REGEX_TEXT =
      "Email address: \n"
          + " <<field:email regex=\"\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b\">>   More text\n";
  private static final String FIELD_HTML_REGEX =
      "HTML: <<field:html regex=\"/^&lt;([a-z]+)([^&lt;]+)*(?:&gt;(.*)&lt;\\/\\1&gt;|\\s+\\/&gt;)$/\">>   More text >>\n";
  private static final String FIELD_NEIGHBOURS = "<<field:one>><<field:two>>";
  private static final String FIELD_ILLEGAL_REGEX = "Error: <<field:error regex=\"(\">>";
  private static final String FIELD_DEFAULT_TEXT = "<<field:ten defaultValue=\"10\">>";
  private static final String FIELD_REQUIRED_TEXT = "<<field:required required=\"true\">>";
  private static final String FIELD_REPEAT_TEXT = "<<field:required repeat>>";
  private static final String FIELD_REGEX_DEFAULT_REQUIRED_TEXT =
      "<<field:all regex=\"\\d?:\\s\\d?\" defaultValue=\"not found\" required=\"true\" repeat=\"true\">>";
  private static final String FIELD_REGEX_DEFAULT_REQUIRED_TEXT_LENIENT =
      "<<field:all regex=\"\\d?:\\s\\d?\" defaultValue=\"not found\" required repeat>>";

  public TemplateFieldDefinitionAnnotatorTest() {
    super(TemplateFieldDefinitionAnnotator.class);
  }

  @Test
  public void annotateField()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_TEXT);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(10, field.getBegin());
    assertEquals(34, field.getEnd());
    assertEquals("PersonFullName", field.getName());
    assertEquals("<<field:PersonFullName>>", field.getCoveredText());
    assertNull(field.getDefaultValue());
    assertFalse(field.getRequired());
  }

  @Test
  public void annotate2Fields()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD2_TEXT);
    processJCas();
    TemplateFieldDefinition field1 = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(10, field1.getBegin());
    assertEquals(34, field1.getEnd());
    assertEquals("PersonFullName", field1.getName());
    assertEquals("<<field:PersonFullName>>", field1.getCoveredText());
    assertNull(field1.getDefaultValue());
    assertFalse(field1.getRequired());

    TemplateFieldDefinition field2 = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 1);
    assertEquals(53, field2.getBegin());
    assertEquals(74, field2.getEnd());
    assertEquals("Description", field2.getName());
    assertEquals("<<field:Description>>", field2.getCoveredText());
    assertNull(field2.getDefaultValue());
    assertFalse(field2.getRequired());
  }

  @Test
  public void annotateFieldNeighbours()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_NEIGHBOURS);
    processJCas();
    TemplateFieldDefinition field1 = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(0, field1.getBegin());
    assertEquals(13, field1.getEnd());
    assertEquals("one", field1.getName());
    assertEquals("<<field:one>>", field1.getCoveredText());
    assertNull(field1.getDefaultValue());
    assertFalse(field1.getRequired());

    TemplateFieldDefinition field2 = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 1);
    assertEquals(13, field2.getBegin());
    assertEquals(26, field2.getEnd());
    assertEquals("two", field2.getName());
    assertEquals("<<field:two>>", field2.getCoveredText());
    assertNull(field2.getDefaultValue());
    assertFalse(field2.getRequired());
  }

  @Test
  public void annotateFieldWithRegex()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_REGEX_TEXT);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(17, field.getBegin());
    assertEquals(82, field.getEnd());
    assertEquals(
        "<<field:email regex=\"\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b\">>",
        field.getCoveredText());
    assertEquals("email", field.getName());
    assertEquals("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b", field.getRegex());
    assertNull(field.getDefaultValue());
  }

  @Test
  public void annotateFieldWithHtmlRegex()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_HTML_REGEX);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(6, field.getBegin());
    assertEquals(90, field.getEnd());
    assertEquals(
        "<<field:html regex=\"/^&lt;([a-z]+)([^&lt;]+)*(?:&gt;(.*)&lt;\\/\\1&gt;|\\s+\\/&gt;)$/\">>",
        field.getCoveredText());
    assertEquals("html", field.getName());
    assertEquals("/^<([a-z]+)([^<]+)*(?:>(.*)<\\/\\1>|\\s+\\/>)$/", field.getRegex());
    assertNull(field.getDefaultValue());
    assertFalse(field.getRepeat());
  }

  @Test(expected = AnalysisEngineProcessException.class)
  public void annotateFieldWithIllegalRegex()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_ILLEGAL_REGEX);
    processJCas();
  }

  @Test
  public void annotateFieldWithDefaultValue()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_DEFAULT_TEXT);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(0, field.getBegin());
    assertEquals(FIELD_DEFAULT_TEXT.length(), field.getEnd());
    assertEquals("ten", field.getName());
    assertEquals(FIELD_DEFAULT_TEXT, field.getCoveredText());
    assertEquals("10", field.getDefaultValue());
    assertFalse(field.getRequired());
    assertFalse(field.getRepeat());
  }

  @Test
  public void annotateFieldRequired()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_REQUIRED_TEXT);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(0, field.getBegin());
    assertEquals(FIELD_REQUIRED_TEXT.length(), field.getEnd());
    assertEquals("required", field.getName());
    assertEquals(FIELD_REQUIRED_TEXT, field.getCoveredText());
    assertTrue(field.getRequired());
    assertFalse(field.getRepeat());
    assertNull(field.getDefaultValue());
  }

  @Test
  public void annotateFieldRepeat()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_REPEAT_TEXT);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(0, field.getBegin());
    assertEquals(FIELD_REPEAT_TEXT.length(), field.getEnd());
    assertEquals("required", field.getName());
    assertEquals(FIELD_REPEAT_TEXT, field.getCoveredText());
    assertFalse(field.getRequired());
    assertTrue(field.getRepeat());
    assertNull(field.getDefaultValue());
  }

  @Test
  public void annotateFieldAllAttributes()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_REGEX_DEFAULT_REQUIRED_TEXT);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(0, field.getBegin());
    assertEquals(FIELD_REGEX_DEFAULT_REQUIRED_TEXT.length(), field.getEnd());
    assertEquals("all", field.getName());
    assertEquals(FIELD_REGEX_DEFAULT_REQUIRED_TEXT, field.getCoveredText());
    assertTrue(field.getRequired());
    assertTrue(field.getRepeat());
    assertEquals("\\d?:\\s\\d?", field.getRegex());
    assertEquals("not found", field.getDefaultValue());
  }

  @Test
  public void annotateFieldAllAttributesLenient()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(FIELD_REGEX_DEFAULT_REQUIRED_TEXT_LENIENT);
    processJCas();
    TemplateFieldDefinition field = JCasUtil.selectByIndex(jCas, TemplateFieldDefinition.class, 0);
    assertEquals(0, field.getBegin());
    assertEquals(FIELD_REGEX_DEFAULT_REQUIRED_TEXT_LENIENT.length(), field.getEnd());
    assertEquals("all", field.getName());
    assertEquals(FIELD_REGEX_DEFAULT_REQUIRED_TEXT_LENIENT, field.getCoveredText());
    assertTrue(field.getRequired());
    assertTrue(field.getRepeat());
    assertEquals("\\d?:\\s\\d?", field.getRegex());
    assertEquals("not found", field.getDefaultValue());
  }
}
