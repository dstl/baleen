// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

public class MockRabbitMQResource extends SharedRabbitMQResource {

  private final BlockingDeque<byte[]> consumed = new LinkedBlockingDeque<>();
  private final BlockingDeque<byte[]> supply = new LinkedBlockingDeque<>();

  @Override
  protected boolean doInitialize(
      final ResourceSpecifier aSpecifier, final Map<String, Object> aAdditionalParams)
      throws ResourceInitializationException {
    return true;
  }

  @Override
  public RabbitMQConsumer createConsumer(
      final String exchangeName, final String routingKey, final String topic) {
    return new MockRabbitMQConsumer(consumed::add);
  }

  @Override
  public RabbitMQSupplier createSupplier(
      final String exchangeName, final String routingKey, final String topic) {
    return new MockRabbitMQSupplier(
        () -> {
          try {
            return supply.takeFirst();
          } catch (final InterruptedException e) {
            e.printStackTrace();
            return null;
          }
        });
  }

  public BlockingDeque<byte[]> getSupply() {
    return supply;
  }

  public BlockingDeque<byte[]> getConsumed() {
    return consumed;
  }

  public void recieve(byte[] bytes) {
    supply.add(bytes);
  }

  public byte[] sent() {
    try {
      return consumed.takeFirst();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }
  }
}
