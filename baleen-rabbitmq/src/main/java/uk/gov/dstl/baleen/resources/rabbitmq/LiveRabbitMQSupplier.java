// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

import uk.gov.dstl.baleen.uima.UimaMonitor;

/** Implements a {@link RabbitMQSupplier} with a live channel to an instance of RabbitMQ */
public class LiveRabbitMQSupplier extends AbstractRabbitMQChannel implements RabbitMQSupplier {

  /** New instance */
  public LiveRabbitMQSupplier(
      final UimaMonitor monitor, final Channel channel, final String queueName) {
    super(monitor, channel, queueName);
  }

  @Override
  public byte[] get() {
    try {
      GetResponse response = null;
      while (response == null) {
        response = channel.basicGet(queueName, true);
      }
      return response.getBody();
    } catch (final IOException e) {
      monitor.warn("Failed to publish message", e);
      return new byte[0];
    }
  }
}
