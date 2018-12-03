// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders.testing;

import static uk.gov.dstl.baleen.uima.BaleenCollectionReader.KEY_CONTENT_EXTRACTOR;

import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.contentextractors.PlainTextContentExtractor;
import uk.gov.dstl.baleen.core.pipelines.content.ContentExtractor;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

/** Abstract class for testing collection readers */
public abstract class AbstractReaderTest {
  protected JCas jCas;
  private Class<? extends BaleenCollectionReader> readerClass;

  private final ExternalResourceDescription contentExtractor;

  public AbstractReaderTest(Class<? extends BaleenCollectionReader> readerClass) {
    this.readerClass = readerClass;

    contentExtractor =
        ExternalResourceFactory.createExternalResourceDescription(
            KEY_CONTENT_EXTRACTOR, getContentExtractorClass());
  }

  /*
   * Override to use different content extractor
   */
  protected Class<? extends ContentExtractor> getContentExtractorClass() {
    return PlainTextContentExtractor.class;
  }

  @Before
  public void beforeTest() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
  }

  protected BaleenCollectionReader getCollectionReader(Object... args)
      throws ResourceInitializationException {
    Object[] argumentWithExtractor =
        ImmutableList.builder()
            .add(KEY_CONTENT_EXTRACTOR)
            .add(contentExtractor)
            .addAll(Arrays.asList(args))
            .build()
            .toArray();
    return (BaleenCollectionReader)
        CollectionReaderFactory.createReader(
            readerClass,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            argumentWithExtractor);
  }
}
