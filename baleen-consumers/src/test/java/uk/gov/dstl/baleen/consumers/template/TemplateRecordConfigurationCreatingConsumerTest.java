// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.gov.dstl.baleen.annotators.templates.TemplateRecordConfiguration;
import uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition;
import uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition;

public class TemplateRecordConfigurationCreatingConsumerTest
    extends AbstractTemplateRecordConfigurationCreatingConsumerTest {

  @Override
  @Before
  public void setup() throws IOException {
    super.setup();

    TemplateRecordDefinition record1 = new TemplateRecordDefinition(jCas);
    record1.setBegin(53);
    record1.setEnd(158);
    record1.setName("record1");
    record1.addToIndexes();
  }

  @Test
  public void testRecordDefinition()
      throws AnalysisEngineProcessException, ResourceInitializationException, JsonParseException,
          JsonMappingException, IOException {
    processJCas(
        TemplateRecordConfigurationCreatingConsumer.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString());
    checkDefinitions();
  }

  @Test(expected = AnalysisEngineProcessException.class)
  public void testErrorWhenRecordNameNotUnique()
      throws AnalysisEngineProcessException, ResourceInitializationException, JsonParseException,
          JsonMappingException, IOException {

    TemplateRecordDefinition record1 = new TemplateRecordDefinition(jCas);
    record1.setBegin(123);
    record1.setEnd(140);
    record1.setName("record1");
    record1.addToIndexes();

    TemplateFieldDefinition field1 = new TemplateFieldDefinition(jCas);
    field1.setBegin(72);
    field1.setEnd(75);
    field1.setName("myField1");
    field1.addToIndexes();

    processJCas(
        TemplateRecordConfigurationCreatingConsumer.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString());
  }

  @Test
  public void testRecordDefinitionOutputFileAlreadyExists()
      throws AnalysisEngineProcessException, ResourceInitializationException, JsonParseException,
          JsonMappingException, IOException {
    assertTrue(
        Paths.get(
                tempDirectory.toString(),
                TemplateRecordConfigurationCreatingConsumerTest.class.getSimpleName() + ".yaml")
            .toFile()
            .createNewFile());
    processJCas(
        TemplateRecordConfigurationCreatingConsumer.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString());
    checkDefinitions();
  }

  @Test
  public void testRecordDefinitionCustomStructureClassList()
      throws AnalysisEngineProcessException, ResourceInitializationException, JsonParseException,
          JsonMappingException, IOException {
    processJCas(
        TemplateRecordConfigurationCreatingConsumer.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        TemplateRecordConfigurationCreatingConsumer.PARAM_TYPE_NAMES,
        new String[] {"Paragraph"});
    checkDefinitions();
  }

  @Test
  public void testRecordDefinitionEmptyStructureClassList()
      throws AnalysisEngineProcessException, ResourceInitializationException, JsonParseException,
          JsonMappingException, IOException {
    processJCas(
        TemplateRecordConfigurationCreatingConsumer.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        TemplateRecordConfigurationCreatingConsumer.PARAM_TYPE_NAMES,
        new String[] {});
    checkDefinitions();
  }

  @Test(expected = ResourceInitializationException.class)
  public void testInvalidTypes()
      throws AnalysisEngineProcessException, ResourceInitializationException, JsonParseException,
          JsonMappingException, IOException {
    processJCas(
        TemplateRecordConfigurationCreatingConsumer.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        TemplateRecordConfigurationCreatingConsumer.PARAM_TYPE_NAMES,
        new String[] {"MadeUpClass"});
  }

  private void checkDefinitions() throws IOException, JsonParseException, JsonMappingException {
    Path yamlFile = getDefinitionPath();

    List<TemplateRecordConfiguration> definitions = readDefinitions(yamlFile);

    TemplateRecordConfiguration record = assertNamedRecord(definitions);
    assertFalse(record.isRepeat());

    assertDefaultRecord(definitions);

    Files.delete(yamlFile);
  }
}
