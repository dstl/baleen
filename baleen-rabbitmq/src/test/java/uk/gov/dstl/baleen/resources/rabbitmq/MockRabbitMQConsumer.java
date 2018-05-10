// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.io.IOException;
import java.util.function.Consumer;

public class MockRabbitMQConsumer implements RabbitMQConsumer {

  private Consumer<byte[]> fake;

  public MockRabbitMQConsumer(Consumer<byte[]> fake) {
    this.fake = fake;
  }

  @Override
  public void accept(byte[] message) {
    fake.accept(message);
  }

  @Override
  public void close() throws IOException {
    // DO NOTHING
  }

  @Override
  public int getQueueCount() throws IOException {
    return 0;
  }
}
