// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;

public class TemplateValidatorTest extends AbstractRecordAnnotatorTest {

  public TemplateValidatorTest() {
    super(TemplateValidator.class);
  }

  @Override
  @Before
  public void setup() throws IOException {
    super.setup();

    TemplateRecord otherRecord = new TemplateRecord(jCas);
    otherRecord.setBegin(53);
    otherRecord.setEnd(105);
    otherRecord.setName("otherRecord");
    otherRecord.setSource("other");
    otherRecord.addToIndexes();

    TemplateField otherField = new TemplateField(jCas);
    otherField.setBegin(53);
    otherField.setEnd(68);
    otherField.setName("otherField");
    otherField.setSource("other");
    otherField.setValue("default");
    otherField.addToIndexes();
  }

  @Test
  public void testRecordWithAllFieldsRemains()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);
    addOptionalField(source);
    addRequiredField(source);

    try {
      processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(1, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(2, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testRecordWithOnlyRequiredFiledRemains()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);
    addRequiredField(source);

    try {
      processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(1, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(1, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testRecordWithMissingRequiredFieldIsRemoved()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);

    try {
      processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(0, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(0, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testRecordWithMissingRequiredFieldIsRemovedWhenSourceSpecified()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);

    try {
      processJCas(
          TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY,
          tempDirectory.toString(),
          TemplateValidator.PARAM_SOURCE,
          source);

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(0, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(0, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testRecordWithMissingRequiredFieldIsRemovedWhenRecordSpecified()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);

    try {
      processJCas(
          TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY,
          tempDirectory.toString(),
          TemplateValidator.PARAM_RECORDS,
          new String[] {"record"});

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(0, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(0, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testRecordWithMissingRequiredFieldIsRemovedWhenSourceAndRecordSpecified()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);

    try {
      processJCas(
          TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY,
          tempDirectory.toString(),
          TemplateValidator.PARAM_SOURCE,
          source,
          TemplateValidator.PARAM_RECORDS,
          new String[] {"record"});

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(0, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(0, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testRecordWithMissingRequiredFieldIsNotRemovedWhenOtherSourceSpecified()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);
    addOptionalField(source);

    try {
      processJCas(
          TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY,
          tempDirectory.toString(),
          TemplateValidator.PARAM_SOURCE,
          "other",
          TemplateValidator.PARAM_RECORDS,
          new String[] {"record"});

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(1, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(1, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testRecordWithMissingRequiredFieldIsNotRemovedWhenOtherRecordSpecified()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();
    String source = definitionFile.toFile().getName();
    addRecord(source);
    addOptionalField(source);

    try {
      processJCas(
          TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY,
          tempDirectory.toString(),
          TemplateValidator.PARAM_SOURCE,
          source,
          TemplateValidator.PARAM_RECORDS,
          new String[] {"other"});

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(1, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(1, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  @Test
  public void testOptionalFieldsFromRecordWithMissingRequiredFiledAreRemoved()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = createRecordDefinitionWithRequiredField();

    String source = definitionFile.toFile().getName();
    addRecord(source);
    addOptionalField(source);

    try {
      processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

      Collection<TemplateRecord> records =
          removeOtherRecord(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(0, records.size());

      Collection<TemplateField> fields =
          removeOtherField(JCasUtil.select(jCas, TemplateField.class));
      assertEquals(0, fields.size());

    } finally {
      Files.delete(definitionFile);
    }
  }

  private Collection<TemplateRecord> removeOtherRecord(Collection<TemplateRecord> selected) {
    Collection<TemplateRecord> remaining = new ArrayList<>();
    for (TemplateRecord r : selected) {
      if (!"otherRecord".equals(r.getName())) {
        remaining.add(r);
      }
    }
    assertTrue(remaining.size() < selected.size());
    return remaining;
  }

  private Collection<TemplateField> removeOtherField(Collection<TemplateField> selected) {
    Collection<TemplateField> remaining = new ArrayList<>();
    for (TemplateField r : selected) {
      if (!"otherField".equals(r.getName())) {
        remaining.add(r);
      }
    }
    assertTrue(remaining.size() < selected.size());
    return remaining;
  }

  private Path createRecordDefinitionWithRequiredField() throws IOException {
    TemplateFieldConfiguration notRequiredField =
        new TemplateFieldConfiguration("optional", "Paragraph:nth-of-type(3)");
    TemplateFieldConfiguration requiredField =
        new TemplateFieldConfiguration("required", "Paragraph:nth-of-type(2)");
    requiredField.setRequired(true);
    return createRecord("record", notRequiredField, requiredField);
  }

  private void addRecord(String source) {
    TemplateRecord record1 = new TemplateRecord(jCas);
    record1.setBegin(52);
    record1.setEnd(212);
    record1.setName("record");
    record1.setSource(source);
    record1.addToIndexes();
  }

  private void addRequiredField(String source) {
    TemplateField field = new TemplateField(jCas);
    field.setBegin(53);
    field.setEnd(105);
    field.setName("required");
    field.setSource(source);
    field.setValue(PARA2);
    field.addToIndexes();
  }

  private void addOptionalField(String source) {
    TemplateField field = new TemplateField(jCas);
    field.setBegin(122);
    field.setEnd(125);
    field.setName("optional");
    field.setSource(source);
    field.setValue("rat");
    field.addToIndexes();
  }
}
