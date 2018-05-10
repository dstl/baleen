// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A fake Kafka consumer used for unit testing.
 *
 * <p>This is extremely limited and designed for the most simple unit tests. The consumer/producer
 * will always return the same values. This class assumes you will ask for string,string for key
 * value.
 */
public class MockKafkaResource extends SharedKafkaResource {

  private MockConsumer<String, String> mockConsumer =
      new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
  private MockProducer<String, String> mockProducer = new MockProducer<>();

  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Consumer<K, V> createConsumer(
      final String topic, final Object... keyValueConfiguration) throws BaleenException {
    return (Consumer<K, V>) mockConsumer;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Producer<K, V> createProducer(
      final String topic, final Object... keyValueConfiguration) throws BaleenException {
    return (Producer<K, V>) mockProducer;
  }

  /** @return the mock consumer for testing */
  public MockConsumer<String, String> getMockConsumer() {
    return mockConsumer;
  }

  /** @return the mock producer for testing */
  public MockProducer<String, String> getMockProducer() {
    return mockProducer;
  }
}
