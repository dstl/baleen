// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.memory;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedMemoryQueueResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportConsumer;

/**
 * This class provides an in memory implementation of an {@link AbstractTransportConsumer}.
 *
 * <p>This implementation can only be used for transport within a single Baleen instance and for
 * testing and development.
 *
 * @baleen.javadoc
 */
public class MemoryTransportSender extends AbstractTransportConsumer {

  @ExternalResource(key = SharedMemoryQueueResource.RESOURCE_KEY)
  private SharedMemoryQueueResource mqResource;

  private Consumer<String> consumer;

  @Override
  protected void createQueue() throws BaleenException {
    consumer = mqResource.createBlockingConsumer(topic);
  }

  @Override
  protected void closeQueue() throws IOException {
    if (consumer != null) {
      consumer = null;
    }
  }

  @Override
  protected void writeToQueue(final String id, final String jCas) throws IOException {
    consumer.accept(jCas);
  }

  @Override
  protected int getDefaultCapacity() {
    return 5;
  }

  @Override
  protected int getQueueLength() {
    return mqResource.getQueue(topic).size();
  }
}
