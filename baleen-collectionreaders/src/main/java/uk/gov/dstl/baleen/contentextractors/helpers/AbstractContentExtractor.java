// Dstl (c) Crown Copyright 2017
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.initialize.ConfigurationParameterInitializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.BaleenContentExtractor;
import uk.gov.dstl.baleen.uima.UimaSupport;

/**
 * Provides a basis for content extractors, implementing common functionality.
 *
 * <p>Sets the source and timestamp of the document, and the extraction class as metadata.
 */
public abstract class AbstractContentExtractor extends BaleenContentExtractor {
  private static final String METADATA_KEY_CONTENT_EXTRACTOR = "baleen:content-extractor";

  @Override
  public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {
    DocumentAnnotation doc = UimaSupport.getDocumentAnnotation(jCas);
    doc.setSourceUri(source);
    doc.setTimestamp(System.currentTimeMillis());

    // Add metadata item to capture which content extractor was used
    addMetadata(jCas, METADATA_KEY_CONTENT_EXTRACTOR, this.getClass().getName());
  }

  @Override
  public void doInitialize(UimaContext context, Map<String, Object> params)
      throws ResourceInitializationException {
    ConfigurationParameterInitializer.initialize(this, params);
  }

  @Override
  public void doDestroy() {
    // Do nothing
  }
}
