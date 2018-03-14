// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.metadata.Metadata;

public class AddSourceToMetadataTest extends AbstractAnnotatorTest {

  private String file;

  public AddSourceToMetadataTest() {
    super(AddSourceToMetadata.class);

    File tempDir = Files.createTempDir();
    File f = new File(tempDir, "Test Document.txt");

    file = f.getAbsolutePath();
  }

  @Test
  public void testPath() throws Exception {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri(file);

    processJCas();

    assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());

    Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
    assertEquals("source", md.getKey());
    assertEquals(file, md.getValue());
  }

  @Test
  public void testName() throws Exception {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri(file);

    processJCas("nameOnly", true, "key", "title");

    assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());

    Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
    assertEquals("title", md.getKey());
    assertEquals("Test Document", md.getValue());
  }
}
