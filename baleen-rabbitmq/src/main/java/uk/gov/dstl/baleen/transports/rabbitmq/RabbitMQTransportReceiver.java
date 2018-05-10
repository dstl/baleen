// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.rabbitmq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.rabbitmq.RabbitMQSupplier;
import uk.gov.dstl.baleen.resources.rabbitmq.SharedRabbitMQResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportCollectionReader;

/**
 * A transport collection reader using RabbitMQ.
 *
 * <p>Requires a {@link SharedRabbitMQResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class RabbitMQTransportReceiver extends AbstractTransportCollectionReader {

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

  private RabbitMQSupplier supplier;

  @Override
  protected void createQueue() throws BaleenException {
    try {
      supplier = mqResource.createSupplier(exchangeName, routingKey, topic);
    } catch (final IOException e) {
      throw new BaleenException(e);
    }
  }

  @Override
  protected void closeQueue() throws IOException {
    if (supplier != null) {
      supplier.close();
    }
  }

  @Override
  protected String readFromQueue() throws IOException {
    final byte[] bs = supplier.get();
    return new String(bs, StandardCharsets.UTF_8);
  }
}
