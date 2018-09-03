// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.testing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.UimaContextAdmin;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.Logger;

import uk.gov.dstl.baleen.core.pipelines.content.ContentExtractor;

public class FakeContentExtractor implements ContentExtractor {

  private boolean destroy;
  private boolean processed;
  private boolean initialised;

  @Override
  public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
      throws ResourceInitializationException {
    initialised = true;
    return initialised;
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

  @Override
  public ResourceMetaData getMetaData() {
    return null;
  }

  @Override
  public ResourceManager getResourceManager() {
    return null;
  }

  @Override
  public Logger getLogger() {
    return null;
  }

  @Override
  public void setLogger(Logger aLogger) {}

  @Override
  public UimaContext getUimaContext() {
    return null;
  }

  @Override
  public UimaContextAdmin getUimaContextAdmin() {
    return null;
  }
}
