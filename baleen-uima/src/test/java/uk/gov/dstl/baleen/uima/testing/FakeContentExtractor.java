// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.testing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.IContentExtractor;

public class FakeContentExtractor implements IContentExtractor {

  private boolean destroy;
  private boolean processed;
  private boolean initialised;

  @Override
  public void initialize(UimaContext context, Map<String, Object> params)
      throws ResourceInitializationException {
    initialised = true;
  }

  @Override
  public void processStream(InputStream stream, String source, JCas jCas) throws IOException {
    processed = true;
  }

  @Override
  public void destroy() {
    destroy = true;
  }

  public boolean isDestroy() {
    return destroy;
  }

  public boolean isInitialised() {
    return initialised;
  }

  public boolean isProcessed() {
    return processed;
  }
}
