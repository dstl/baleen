// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.*;
import static uk.gov.dstl.baleen.uima.BaleenCollectionReader.KEY_CONTENT_EXTRACTOR;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.UimaContextFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.junit.Test;

import uk.gov.dstl.baleen.uima.testing.DummyBaleenCollectionReader;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class BaleenCollectionReaderTest {
  @Test
  public void testHasNextLooping() throws Exception {

    ExternalResourceDescription contentExtractor =
        ExternalResourceFactory.createNamedResourceDescription(
            KEY_CONTENT_EXTRACTOR, FakeBaleenContentExtractor.class);

    DummyBaleenCollectionReader cr =
        (DummyBaleenCollectionReader)
            CollectionReaderFactory.createReader(
                DummyBaleenCollectionReader.class,
                BaleenCollectionReader.KEY_CONTENT_EXTRACTOR,
                contentExtractor);

    while (cr.hasNext()) {
      JCas jCas = JCasSingleton.getJCasInstance();
      cr.getNext(jCas.getCas());
    }

    cr.destroy();
  }

  @Test
  public void test() throws Exception {

    ExternalResourceDescription contentExtractor =
        ExternalResourceFactory.createNamedResourceDescription(
            KEY_CONTENT_EXTRACTOR, FakeBaleenContentExtractor.class);

    FakeCollectionReader cr =
        (FakeCollectionReader)
            CollectionReaderFactory.createReader(
                FakeCollectionReader.class,
                BaleenCollectionReader.KEY_CONTENT_EXTRACTOR,
                contentExtractor);

    UimaContext context = UimaContextFactory.createUimaContext();
    cr.initialize(context);
    assertTrue(cr.initialised);

    assertNotNull(cr.getSupport());
    assertNotNull(cr.getMonitor());
    assertNotNull(cr.getProgress());

    Progress[] progress = cr.getProgress();
    assertEquals("testunits", progress[0].getUnit());

    assertFalse(cr.hasNext());
    assertTrue(cr.hasNext);

    cr.getNext((JCas) null);
    assertTrue(cr.getNext);

    cr.destroy();
    assertTrue(cr.closed);
  }

  public static class FakeCollectionReader extends BaleenCollectionReader {

    private boolean initialised;
    private boolean hasNext;
    private boolean getNext;
    private boolean closed;

    @Override
    protected void doInitialize(UimaContext context) throws ResourceInitializationException {
      initialised = true;
    }

    @Override
    protected void doGetNext(JCas jCas) {
      getNext = true;
    }

    @Override
    protected void doClose() throws IOException {
      closed = true;
    }

    @Override
    public boolean doHasNext() {
      hasNext = true;
      return false;
    }

    @Override
    public Progress[] doGetProgress() {
      return new Progress[] {new ProgressImpl(1, 2, "testunits")};
    }
  }
}
