// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders.renoun;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class ReNounSeedDocumentTest extends AbstractReaderTest {

  public ReNounSeedDocumentTest() {
    super(ReNounSeedDocument.class);
  }

  @Test
  public void test() throws Exception {

    BaleenCollectionReader bcr = getCollectionReader();

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());
    assertFalse(jCas.getDocumentText().isEmpty());
    assertFalse(bcr.doHasNext());

    bcr.close();
  }
}
