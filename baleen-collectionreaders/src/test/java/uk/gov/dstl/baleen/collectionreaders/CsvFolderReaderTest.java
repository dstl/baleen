// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class CsvFolderReaderTest extends AbstractReaderTest {
  private static final String TEST2_FILE = "test2.csv";
  private static final String TEST1_FILE = "test1.csv";
  private static final String DIR = "baleen-test";

  private File inputDir;

  public CsvFolderReaderTest() {
    super(CsvFolderReader.class);
  }

  @Before
  public void before() throws Exception {
    inputDir = Files.createTempDirectory(DIR).toFile();
  }

  @After
  public void after() throws IOException {
    String[] entries = inputDir.list();
    if (entries != null) {
      for (String s : entries) {
        File currentFile = new File(inputDir.getPath(), s);
        currentFile.delete();
      }
    }
    inputDir.delete();
  }

  @Test
  public void test() throws Exception {
    List<String> lines1 = new ArrayList<>();
    lines1.add("id,date,content,evaluated");
    lines1.add("1,2017-10-11 14:58:00,\"Hello, world!\",Y");
    lines1.add("2,2017-10-11 14:58:18,\"Hello, Ben!\",N");

    Path p1 = new File(inputDir, TEST1_FILE).toPath();

    Files.write(p1, lines1);

    BaleenCollectionReader bcr =
        getCollectionReader(CsvFolderReader.PARAM_FOLDERS, new String[] {inputDir.getPath()});

    // Document 1, Row 1
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertEquals("Hello, world!", jCas.getDocumentText());

    Map<String, String> meta1 = new HashMap<>();
    JCasUtil.select(jCas, Metadata.class).forEach(md -> meta1.put(md.getKey(), md.getValue()));
    assertEquals(3, meta1.size());
    assertEquals("1", meta1.get("id"));
    assertEquals("2017-10-11 14:58:00", meta1.get("date"));
    assertEquals("Y", meta1.get("evaluated"));
    assertEquals(p1.toString(), getSource(jCas));

    jCas.reset();

    // Document 1, Row 2
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertEquals("Hello, Ben!", jCas.getDocumentText());

    Map<String, String> meta2 = new HashMap<>();
    JCasUtil.select(jCas, Metadata.class).forEach(md -> meta2.put(md.getKey(), md.getValue()));
    assertEquals(3, meta2.size());
    assertEquals("2", meta2.get("id"));
    assertEquals("2017-10-11 14:58:18", meta2.get("date"));
    assertEquals("N", meta2.get("evaluated"));
    assertEquals(p1.toString(), getSource(jCas));

    jCas.reset();

    // No document
    assertFalse(bcr.doHasNext());

    List<String> lines2 = new ArrayList<>();
    lines2.add("id,date,content,validated");
    lines2.add("1,2017-10-12 15:13:23,\"Goodbye, Bob!\",N");

    Path p2 = new File(inputDir, TEST2_FILE).toPath();

    Files.write(p2, lines2);
    Thread.sleep(1000);

    // Document 2, Row 1
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertEquals("Goodbye, Bob!", jCas.getDocumentText());

    Map<String, String> meta3 = new HashMap<>();
    JCasUtil.select(jCas, Metadata.class).forEach(md -> meta3.put(md.getKey(), md.getValue()));
    assertEquals(3, meta3.size());
    assertEquals("1", meta3.get("id"));
    assertEquals("2017-10-12 15:13:23", meta3.get("date"));
    assertEquals("N", meta3.get("validated"));
    assertEquals(p2.toString(), getSource(jCas));

    jCas.reset();

    // No document
    assertFalse(bcr.doHasNext());

    bcr.close();
  }

  private String getSource(JCas jCas) {
    DocumentAnnotation doc = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    return doc.getSourceUri();
  }
}
