// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class EntityCountTest {
  private static final String OUTPUT_FILE = "outputFile";
  private static final String TEST1_TXT = "test1.txt";
  JCas jCas;

  @Before
  public void beforeTest() throws Exception {
    jCas = JCasSingleton.getJCasInstance();
  }

  @Test
  public void testEntityCountOutput() throws Exception {
    File output = Files.createTempFile("baleen-entitycount", ".tsv").toFile();

    AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(
            EntityCount.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            OUTPUT_FILE,
            output.getPath());

    createDocument();

    consumer.process(jCas);

    assertEquals("test1.txt\t2", FileUtils.file2String(output).trim());

    consumer.destroy();
    output.delete();
  }

  @Test
  public void testEntityCountOutputNewFile() throws Exception {
    File outputFolder = Files.createTempDirectory("baleen").toFile();
    File output = new File(outputFolder, "baleen-entitycount.tsv");

    AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(
            EntityCount.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            OUTPUT_FILE,
            output.getPath());

    createDocument();

    consumer.process(jCas);

    assertEquals("test1.txt\t2", FileUtils.file2String(output).trim());

    consumer.destroy();
    output.delete();
    outputFolder.delete();
  }

  private void createDocument() {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri(TEST1_TXT);

    CommsIdentifier ci = new CommsIdentifier(jCas);
    ci.addToIndexes();

    Person p = new Person(jCas);
    p.addToIndexes();
  }

  @Test
  public void testEntityCountOutputReadOnly() throws Exception {
    File output = Files.createTempFile("baleen-entitycount", ".tsv").toFile();
    output.setReadOnly();

    try {
      AnalysisEngineFactory.createEngine(
          EntityCount.class,
          TypeSystemSingleton.getTypeSystemDescriptionInstance(),
          OUTPUT_FILE,
          output.getPath());
      fail("Expected exception not thrown");
    } catch (Exception ex) {
      // Do nothing
    }

    output.delete();
  }

  @Test
  public void testEntityCountOutputCantWrite() throws Exception {
    File output = Files.createTempDirectory("baleen").toFile();

    createDocument();

    // Try writing to folder
    AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(
            EntityCount.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            OUTPUT_FILE,
            output.getPath());
    consumer.process(jCas);
    consumer.destroy();

    output.delete();
  }
}
