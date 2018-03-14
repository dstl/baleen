// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

/** Dummy collection reader that produces 5 simple documents */
public class DummyBaleenCollectionReader extends BaleenCollectionReader {
  public static final Integer DOCUMENT_COUNT = 5;

  List<String> documents = new ArrayList<>();

  @Override
  public void doInitialize(UimaContext context) throws ResourceInitializationException {
    for (int i = 0; i < DOCUMENT_COUNT; i++) {
      documents.add("This is document number " + (i + 1));
    }
  }

  @Override
  public void doGetNext(JCas jCas) throws IOException, CollectionException {
    jCas.setDocumentText(documents.remove(0));
  }

  @Override
  public Progress[] doGetProgress() {
    return new Progress[] {
      new ProgressImpl(DOCUMENT_COUNT - documents.size(), DOCUMENT_COUNT, Progress.ENTITIES)
    };
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    return !documents.isEmpty();
  }

  @Override
  public void doClose() throws IOException {
    // Do nothing
  }
}
