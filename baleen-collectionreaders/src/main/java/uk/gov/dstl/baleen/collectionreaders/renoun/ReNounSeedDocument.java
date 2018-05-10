// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders.renoun;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class ReNounSeedDocument extends BaleenCollectionReader {

  private static final String SEP = "\n";

  // @formatter:off
  private static final String SENTENCE_1 =
      "The CEO of Google, Larry Page started his term in 2011. ";
  private static final String SENTENCE_2 = "The CEO of Google is Larry Page. ";
  private static final String SENTENCE_3 = "Larry Page, Google CEO, started his term in 2011. ";
  private static final String SENTENCE_4 = "Larry Page, Google's CEO started his term in 2011. ";
  private static final String SENTENCE_5 =
      "Larry Page, the CEO of Google started his term in 2011. ";
  private static final String SENTENCE_6 = "Google CEO Larry Page started his term in 2011. ";
  private static final String SENTENCE_7 = "Google CEO, Larry Page started his term in 2011. ";
  private static final String SENTENCE_8 = "Google's CEO, Larry Page started his term in 2011. ";
  // @formatter:on

  private boolean supplied = false;

  @Override
  protected void doInitialize(UimaContext context) throws ResourceInitializationException {
    // DO NOTHING
  }

  @Override
  protected void doGetNext(JCas jCas) throws IOException, CollectionException {
    supplied = true;
    // @formatter:off
    jCas.setDocumentText(
        new StringBuilder()
            .append(SENTENCE_1)
            .append(SEP)
            .append(SENTENCE_2)
            .append(SEP)
            .append(SENTENCE_3)
            .append(SEP)
            .append(SENTENCE_4)
            .append(SEP)
            .append(SENTENCE_5)
            .append(SEP)
            .append(SENTENCE_6)
            .append(SEP)
            .append(SENTENCE_7)
            .append(SEP)
            .append(SENTENCE_8)
            .toString());
    // @formatter:on

  }

  @Override
  protected void doClose() throws IOException {
    // IGNORE
  }

  @Override
  // IGNORE
  public boolean doHasNext() throws IOException, CollectionException {
    return !supplied;
  }
}
