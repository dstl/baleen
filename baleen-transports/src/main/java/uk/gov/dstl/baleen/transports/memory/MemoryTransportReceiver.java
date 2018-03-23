// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.memory;

import java.io.IOException;
import java.util.function.Supplier;

import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedMemoryQueueResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportCollectionReader;

/**
 * This class provides an in memory implementation of an {@link AbstractTransportCollectionReader}.
 *
 * <p>This implementation can only be used for transport within a single Baleen instance and is
 * primarily designed for testing and development.
 *
 * @baleen.javadoc
 */
public class MemoryTransportReceiver extends AbstractTransportCollectionReader {

  @ExternalResource(key = SharedMemoryQueueResource.RESOURCE_KEY)
  private SharedMemoryQueueResource mqResource;

  private Supplier<String> supplier;

  @Override
  protected void createQueue() throws BaleenException {
    supplier = mqResource.createBlockingSupplier(topic);
  }

  @Override
  protected void closeQueue() throws IOException {
    supplier = null;
  }

  @Override
  protected String readFromQueue() throws IOException {
    return supplier.get();
  }
}
