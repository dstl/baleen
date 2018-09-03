package uk.gov.dstl.baleen.uima;

import java.io.InputStream;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;

public class FakeBaleenContentExtractor extends BaleenContentExtractor {

  public FakeBaleenContentExtractor() {
    super();
  }

  boolean initialised;
  boolean processed;
  boolean destroyed;

  @Override
  public void doInitialize(UimaContext context, Map<String, Object> params) {
    initialised = true;
  }

  @Override
  protected void doProcessStream(InputStream stream, String source, JCas jCas) {
    processed = true;
  }

  @Override
  protected void doDestroy() {
    destroyed = true;
  }
}
