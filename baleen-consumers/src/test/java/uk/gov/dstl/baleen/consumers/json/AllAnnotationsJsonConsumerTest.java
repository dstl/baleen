// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.structure.Paragraph;

public class AllAnnotationsJsonConsumerTest extends AbstractAnnotatorTest {
  private static final String EXPECTED_OUTPUT_FILE =
      AllAnnotationsJsonConsumerTest.class.getSimpleName() + ".json";

  private static final String SOURCEURI =
      AllAnnotationsJsonConsumerTest.class.getSimpleName() + ".txt";

  private static final String PARA1 = "The quick brown fox jumped over the lazy dog's back.";

  private static final String PARA2 = "The quick brown cat jumped over the lazy dog's back.";

  private static final String TEXT = String.join("\n", PARA1, PARA2);

  private Path tempDirectory;

  public AllAnnotationsJsonConsumerTest() {
    super(AllAnnotationsJsonConsumer.class);
  }

  @Before
  public void setup() throws IOException {
    jCas.setDocumentText(TEXT);
    tempDirectory = Files.createTempDirectory(AllAnnotationsJsonConsumerTest.class.getSimpleName());
    tempDirectory.toFile().deleteOnExit();

    DocumentAnnotation documentAnnotation = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    documentAnnotation.setSourceUri(SOURCEURI);

    Paragraph paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(0);
    paragraph1.setDepth(1);
    paragraph1.setEnd(52);
    paragraph1.addToIndexes();

    Person entity1 = new Person(jCas);
    entity1.setBegin(70);
    entity1.setEnd(73);
    entity1.setValue("cat");
    entity1.addToIndexes();

    Event event = new Event(jCas);
    event.setBegin(53);
    event.setEnd(105);
    event.setArguments(new StringArray(jCas, 2));
    event.setArguments(0, "cat");
    event.setArguments(1, "dog");
    event.setEntities(new FSArray(jCas, 1));
    event.setEntities(0, entity1);
    event.addToIndexes();
  }

  @Test
  public void testJson()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {
    processJCas(AllAnnotationsJsonConsumer.PARAM_OUTPUT_DIRECTORY, tempDirectory.toString());
    Path outputPath = tempDirectory.resolve(EXPECTED_OUTPUT_FILE);
    outputPath.toFile().deleteOnExit();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode tree = objectMapper.readTree(Files.newInputStream(outputPath));

    // simplistic test for any emitted json
    assertNotNull(tree);
    assertTrue(tree.isContainerNode());

    Files.delete(outputPath);
  }

  @Test
  public void testCannotWriteFile()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path outputPath = tempDirectory.resolve(EXPECTED_OUTPUT_FILE);
    outputPath.toFile().deleteOnExit();

    outputPath.toFile().createNewFile();
    outputPath.toFile().setReadOnly();
    outputPath.toFile().setWritable(false);

    processJCas(AllAnnotationsJsonConsumer.PARAM_OUTPUT_DIRECTORY, tempDirectory.toString());

    byte[] outputFile = Files.readAllBytes(outputPath);

    assertEquals(0, outputFile.length);

    outputPath.toFile().setReadable(true);
    outputPath.toFile().setWritable(true);

    Files.delete(outputPath);
  }
}
