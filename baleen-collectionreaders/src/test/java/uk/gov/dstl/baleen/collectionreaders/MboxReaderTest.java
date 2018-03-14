// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.*;

import java.net.URL;
import java.nio.file.Paths;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class MboxReaderTest extends AbstractReaderTest {

  public MboxReaderTest() {
    super(MboxReader.class);
  }

  @Test
  public void test() throws Exception {
    URL resource = getClass().getResource("rlug.mbox");
    String filePath = Paths.get(resource.toURI()).toString();

    BaleenCollectionReader bcr = getCollectionReader(MboxReader.PARAM_MBOX, filePath);

    // Message 1
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertTrue(getSource(jCas).endsWith("#1"));
    assertNotNull(jCas.getDocumentText());

    jCas.reset();

    // Message 2
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertTrue(getSource(jCas).endsWith("#2"));
    assertNotNull(jCas.getDocumentText());

    jCas.reset();

    // Message 3
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertTrue(getSource(jCas).endsWith("#3"));
    assertNotNull(jCas.getDocumentText());

    jCas.reset();

    // Message 4
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertTrue(getSource(jCas).endsWith("#4"));
    assertNotNull(jCas.getDocumentText());

    jCas.reset();

    // Message 5
    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertTrue(getSource(jCas).endsWith("#5"));
    assertNotNull(jCas.getDocumentText());

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
