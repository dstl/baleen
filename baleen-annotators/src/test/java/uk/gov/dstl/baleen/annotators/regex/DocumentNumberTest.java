// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.DocumentReference;

/** */
public class DocumentNumberTest extends AbstractAnnotatorTest {

  public DocumentNumberTest() {
    super(DocumentNumber.class);
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText(
        "Document 123 was produced, and complemented letters 56, 59 and 57, 12 is not a resolution.");
    processJCas();

    assertEquals(2, JCasUtil.select(jCas, DocumentReference.class).size());

    DocumentReference dr1 = JCasUtil.selectByIndex(jCas, DocumentReference.class, 0);
    assertEquals("Document 123", dr1.getCoveredText());

    DocumentReference dr2 = JCasUtil.selectByIndex(jCas, DocumentReference.class, 1);
    assertEquals("letters 56, 59 and 57", dr2.getCoveredText());
  }
}
