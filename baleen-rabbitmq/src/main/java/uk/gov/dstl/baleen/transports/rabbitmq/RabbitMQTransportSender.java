// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.rabbitmq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.rabbitmq.RabbitMQConsumer;
import uk.gov.dstl.baleen.resources.rabbitmq.SharedRabbitMQResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportConsumer;

/**
 * A transport collection reader using RabbitMQ.
 *
 * <p>This requires a {@link SharedRabbitMQResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class RabbitMQTransportSender extends AbstractTransportConsumer {

  @ExternalResource(key = SharedRabbitMQResource.RESOURCE_KEY)
  private SharedRabbitMQResource mqResource;

  /**
   * The exchange to use when sending messages.
   *
   * @baleen.config "" (equivalent to amq.direct)
   */
  public static final String PARAM_EXCHANGE = "exchange";

  @ConfigurationParameter(name = PARAM_EXCHANGE, defaultValue = "test")
  private String exchangeName;

  /**
   * The routing key to use when sending messages.
   *
   * @baleen.config
   */
  public static final String PARAM_ROUTING_KEY = "routingKey";

  @ConfigurationParameter(name = PARAM_ROUTING_KEY, defaultValue = "")
  private String routingKey;

  private RabbitMQConsumer consumer;

  @Override
  protected void createQueue() throws BaleenException {
    try {
      consumer = mqResource.createConsumer(exchangeName, routingKey, topic);
    } catch (final IOException e) {
      throw new BaleenException(e);
    }
  }

  @Override
  protected void closeQueue() throws IOException {
    if (consumer != null) {
      consumer.close();
    }
  }

  @Override
  protected void writeToQueue(final String id, final String jCas) throws IOException {
    consumer.accept(jCas.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  protected int getQueueLength() {
    try {
      return consumer.getQueueCount();
    } catch (IOException e) {
      getMonitor().info("Unable to check queue length");
      return 0;
    }
  }
}
