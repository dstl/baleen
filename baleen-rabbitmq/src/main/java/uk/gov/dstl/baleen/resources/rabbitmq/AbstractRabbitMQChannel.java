// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;

import uk.gov.dstl.baleen.uima.UimaMonitor;

/** Abstract {@link RabbitMQQueue} with a live channel to an instance of RabbitMQ */
public abstract class AbstractRabbitMQChannel implements RabbitMQQueue {

  protected final Channel channel;
  protected final UimaMonitor monitor;
  protected final String queueName;

  /** New instance */
  public AbstractRabbitMQChannel(
      final UimaMonitor monitor, final Channel channel, final String queueName) {
    this.monitor = monitor;
    this.channel = channel;
    this.queueName = queueName;
  }

  @Override
  public int getQueueCount() throws IOException {
    return (int) channel.messageCount(queueName);
  }

  @Override
  public void close() throws IOException {
    try {
      channel.close();
    } catch (TimeoutException e) {
      throw new IOException(e);
    }
  }
}
