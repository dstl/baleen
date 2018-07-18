// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.print;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import uk.gov.dstl.baleen.consumers.AbstractDocumentGraphFormatConsumer;
import uk.gov.dstl.baleen.consumers.AbstractEntityGraphFormatConsumer;
import uk.gov.dstl.baleen.graph.EntityGraphFactory;
import uk.gov.dstl.baleen.uima.UimaMonitor;

/**
 * Consume each document as a graph and output to the log.
 *
 * @see EntityGraphFactory
 * @baleen.javadoc
 */
public class EntityGraph extends AbstractEntityGraphFormatConsumer {

  @Override
  protected OutputStream createOutputStream(String documentSourceName) {
    final UimaMonitor monitor = getMonitor();
    return new ByteArrayOutputStream() {
      @Override
      public void flush() throws IOException {
        super.flush();
        monitor.info("{}:\n{}", AbstractDocumentGraphFormatConsumer.class.getName(), toString());
      }
    };
  }
}
