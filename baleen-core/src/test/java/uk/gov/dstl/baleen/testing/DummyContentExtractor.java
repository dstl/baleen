package uk.gov.dstl.baleen.testing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource_ImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.pipelines.content.ContentExtractor;

/* Dummy content extractor logs stream */
public class DummyContentExtractor extends Resource_ImplBase implements ContentExtractor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DummyConsumer.class);

  @Override
  public void processStream(InputStream stream, String source, JCas jCas) throws IOException {
    LOGGER.info(source);
    LOGGER.info(IOUtils.toString(stream, StandardCharsets.UTF_8));
  }
}
