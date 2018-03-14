// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;

public class TemplateFieldJoiningAnnotatorTest extends AbstractAnnotatorTest {

  private static final String TEXT = "The quick brown fox jumped over the lazy dog's back.";

  public TemplateFieldJoiningAnnotatorTest() {
    super(TemplateFieldJoiningAnnotator.class);
  }

  @Before
  public void setup() throws IOException {
    jCas.setDocumentText(TEXT);

    TemplateRecord record = new TemplateRecord(jCas);
    record.setName("report");
    record.setSource("brown");
    record.setBegin(0);
    record.setEnd(52);
    record.addToIndexes();

    TemplateField field1 = new TemplateField(jCas);
    field1.setBegin(16);
    field1.setEnd(19);
    field1.setName("athlete");
    field1.addToIndexes();

    TemplateField field2 = new TemplateField(jCas);
    field2.setBegin(41);
    field2.setEnd(44);
    field2.setName("spectator");
    field2.addToIndexes();
  }

  @Test
  public void testAthleteIsMadePersonNoSource()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    processJCas(
        TemplateFieldJoiningAnnotator.PARAM_RECORD,
        "report",
        TemplateFieldJoiningAnnotator.PARAM_FIELD_NAME,
        "fullName",
        TemplateFieldJoiningAnnotator.PARAM_TEMPLATE,
        "{{athlete}}, {{spectator}}");
    Collection<TemplateField> fields = JCasUtil.select(jCas, TemplateField.class);
    assertEquals(3, fields.size());

    List<TemplateField> fullNameFields =
        fields.stream().filter(f -> extracted(f)).collect(Collectors.toList());
    assertEquals(1, fullNameFields.size());

    TemplateField fullName = fullNameFields.iterator().next();
    assertEquals("fox, dog", fullName.getValue());
    assertEquals(16, fullName.getBegin());
    assertEquals(44, fullName.getEnd());
  }

  @Test
  public void testAthleteIsMadePersonSource()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    processJCas(
        TemplateFieldJoiningAnnotator.PARAM_RECORD,
        "report",
        TemplateFieldJoiningAnnotator.PARAM_FIELD_NAME,
        "fullName",
        TemplateFieldJoiningAnnotator.PARAM_TEMPLATE,
        "{{athlete}}, {{spectator}}",
        TemplateFieldJoiningAnnotator.PARAM_SOURCE,
        "brown");
    Collection<TemplateField> fields = JCasUtil.select(jCas, TemplateField.class);
    assertEquals(3, fields.size());

    List<TemplateField> fullNameFields =
        fields.stream().filter(f -> extracted(f)).collect(Collectors.toList());
    assertEquals(1, fullNameFields.size());

    TemplateField fullName = fullNameFields.iterator().next();
    assertEquals("fox, dog", fullName.getValue());
    assertEquals(16, fullName.getBegin());
    assertEquals(44, fullName.getEnd());
  }

  @Test
  public void testAthleteIsMadePersonOtherSource()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    processJCas(
        TemplateFieldJoiningAnnotator.PARAM_RECORD,
        "report",
        TemplateFieldJoiningAnnotator.PARAM_FIELD_NAME,
        "fullName",
        TemplateFieldJoiningAnnotator.PARAM_TEMPLATE,
        "{{athlete}}, {{spectator}}",
        TemplateFieldJoiningAnnotator.PARAM_SOURCE,
        "ketchup");
    Collection<TemplateField> fields = JCasUtil.select(jCas, TemplateField.class);
    assertEquals(2, fields.size());
  }

  private boolean extracted(TemplateField f) {
    return f.getName().equals("fullName");
  }
}
