// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines.content;

import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource;

/** Content Extractor interface */
public interface ContentExtractor extends Resource {

  /**
   * Process an input stream
   *
   * @param stream The InputStream of data to process
   * @param source The source URI to set
   * @param jCas The JCas object to add data to
   */
  void processStream(InputStream stream, String source, JCas jCas) throws IOException;
}
