// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.util.FileUtils;
import org.junit.After;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import uk.gov.dstl.baleen.annotators.templates.TemplateFieldConfiguration;
import uk.gov.dstl.baleen.annotators.templates.TemplateRecordConfiguration;
import uk.gov.dstl.baleen.annotators.templates.TemplateRecordConfiguration.Kind;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition;

public abstract class AbstractTemplateRecordConfigurationCreatingConsumerTest
    extends AbstractAnnotatorTest {

  private static final String SOURCEURI =
      AbstractTemplateRecordConfigurationCreatingConsumerTest.class.getSimpleName() + ".yaml";

  private static final String PARA1 = "The quick brown fox jumped over the lazy dog's back.";

  private static final String PARA2 = "The quick brown cat jumped over the lazy dog's back.";

  private static final String PARA3 = "The quick brown rat jumped over the lazy dog's back.";

  private static final String PARA4 = "The quick brown bat jumped over the lazy dog's back.";

  private static final String TEXT = String.join("\n", PARA1, PARA2, PARA3, PARA4);

  private static final ObjectMapper YAMLMAPPER = new ObjectMapper(new YAMLFactory());

  protected Path tempDirectory;

  public AbstractTemplateRecordConfigurationCreatingConsumerTest() {
    super(TemplateRecordConfigurationCreatingConsumer.class);
  }

  public void setup() throws IOException {
    jCas.setDocumentText(TEXT);
    tempDirectory =
        Files.createTempDirectory(
            AbstractTemplateRecordConfigurationCreatingConsumerTest.class.getSimpleName());
    tempDirectory.toFile().deleteOnExit();

    DocumentAnnotation documentAnnotation = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    documentAnnotation.setSourceUri(SOURCEURI);

    Paragraph paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(0);
    paragraph1.setDepth(1);
    paragraph1.setEnd(52);
    paragraph1.addToIndexes();

    Paragraph paragraph2 = new Paragraph(jCas);
    paragraph2.setBegin(53);
    paragraph2.setDepth(1);
    paragraph2.setEnd(105);
    paragraph2.addToIndexes();

    Paragraph paragraph3 = new Paragraph(jCas);
    paragraph3.setBegin(106);
    paragraph3.setDepth(1);
    paragraph3.setEnd(158);
    paragraph3.addToIndexes();

    Paragraph paragraph4 = new Paragraph(jCas);
    paragraph4.setBegin(159);
    paragraph4.setDepth(1);
    paragraph4.setEnd(212);
    paragraph4.addToIndexes();

    TemplateFieldDefinition field1 = new TemplateFieldDefinition(jCas);
    field1.setBegin(72);
    field1.setEnd(75);
    field1.setName("field1");
    field1.addToIndexes();

    TemplateFieldDefinition field2 = new TemplateFieldDefinition(jCas);
    field2.setBegin(123);
    field2.setEnd(140);
    field2.setName("field2");
    field2.addToIndexes();

    TemplateFieldDefinition field3 = new TemplateFieldDefinition(jCas);
    field3.setBegin(17);
    field3.setEnd(20);
    field3.setName("noRecordField");
    field3.addToIndexes();
  }

  protected List<TemplateRecordConfiguration> readDefinitions(Path yamlFile)
      throws IOException, JsonParseException, JsonMappingException {
    List<TemplateRecordConfiguration> definitions =
        YAMLMAPPER.readValue(
            yamlFile.toFile(),
            YAMLMAPPER
                .getTypeFactory()
                .constructCollectionType(List.class, TemplateRecordConfiguration.class));
    return definitions;
  }

  protected Path getDefinitionPath() {
    Path yamlFile =
        Paths.get(
            tempDirectory.toString(),
            AbstractTemplateRecordConfigurationCreatingConsumerTest.class.getSimpleName()
                + ".yaml");
    yamlFile.toFile().deleteOnExit();
    return yamlFile;
  }

  protected void assertDefaultRecord(List<TemplateRecordConfiguration> definitions) {
    TemplateRecordConfiguration defaultRecord =
        definitions
            .stream()
            .filter(p -> p.getKind().equals(Kind.DEFAULT))
            .collect(Collectors.toList())
            .get(0);
    assertEquals(null, defaultRecord.getName());
    assertEquals(1, defaultRecord.getFields().size());
    TemplateFieldConfiguration field = defaultRecord.getFields().get(0);
    assertEquals("noRecordField", field.getName());
    assertEquals("Paragraph:nth-of-type(1)", field.getPath());
  }

  protected TemplateRecordConfiguration assertNamedRecord(
      List<TemplateRecordConfiguration> definitions) {
    TemplateRecordConfiguration record =
        definitions
            .stream()
            .filter(p -> p.getKind().equals(Kind.NAMED) && p.getName().equals("record1"))
            .collect(Collectors.toList())
            .get(0);
    assertEquals(Kind.NAMED, record.getKind());
    assertEquals(2, record.getFields().size());
    for (TemplateFieldConfiguration field : record.getFields()) {
      String name = field.getName();
      if (name.equals("field1")) {
        assertEquals("Paragraph:nth-of-type(2)", field.getPath());
      } else if (field.getName().equals("field2")) {
        assertEquals("Paragraph:nth-of-type(3)", field.getPath());
      } else {
        fail("field not expected: " + name);
      }
    }
    assertEquals("Paragraph:nth-of-type(1)", record.getPrecedingPath());
    assertEquals("Paragraph:nth-of-type(4)", record.getFollowingPath());
    return record;
  }

  @After
  public void tearDown() throws IOException {
    FileUtils.deleteRecursive(tempDirectory.toFile());
  }
}
