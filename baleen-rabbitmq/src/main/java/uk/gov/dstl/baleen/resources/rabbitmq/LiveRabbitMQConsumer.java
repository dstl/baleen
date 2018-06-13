// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;

import uk.gov.dstl.baleen.uima.UimaMonitor;

/** Implements a {@link RabbitMQConsumer} with a live channel to an instance of RabbitMQ */
public class LiveRabbitMQConsumer extends AbstractRabbitMQChannel implements RabbitMQConsumer {

  private final String exchangeName;
  private final String routingKey;

  /** New instance */
  public LiveRabbitMQConsumer(
      final UimaMonitor monitor,
      final Channel channel,
      final String exchangeName,
      final String routingKey,
      final String queueName) {
    super(monitor, channel, queueName);
    this.exchangeName = exchangeName;
    this.routingKey = routingKey;
  }

  @Override
  public void accept(byte[] message) {
    try {
      channel.basicPublish(exchangeName, routingKey, null, message);
    } catch (final IOException e) {
      monitor.warn("Failed to publish message", e);
    }
  }
}
