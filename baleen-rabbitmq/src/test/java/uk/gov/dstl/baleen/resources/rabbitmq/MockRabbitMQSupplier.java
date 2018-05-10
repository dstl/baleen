// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

public class MockRabbitMQSupplier implements Closeable, Supplier<byte[]>, RabbitMQSupplier {

  private final Supplier<byte[]> fake;

  public MockRabbitMQSupplier(final Supplier<byte[]> fake) {
    this.fake = fake;
  }

  @Override
  public byte[] get() {
    return fake.get();
  }

  @Override
  public int getQueueCount() throws IOException {
    return 0;
  }

  @Override
  public void close() throws IOException {
    // DO NOTHING
  }
}
