// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface to RabbitMQ.
 *
 * <p>Allow the queue to be closed.
 */
public interface RabbitMQQueue extends Closeable {

  /**
   * Get the queue count
   *
   * @return the number of messages on the queue
   * @throws IOException
   */
  int getQueueCount() throws IOException;
}
