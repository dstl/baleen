// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.kafka;

import java.io.IOException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.kafka.SharedKafkaResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportConsumer;

/**
 * A transport collection reader using Kafka.
 *
 * <p>This requires a {@link SharedKafkaResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class KafkaTransportSender extends AbstractTransportConsumer {

  @ExternalResource(key = SharedKafkaResource.RESOURCE_KEY)
  private SharedKafkaResource kafkaResource;

  private Producer<Object, Object> producer;

  @Override
  protected void createQueue() throws BaleenException {
    producer =
        kafkaResource.createProducer(
            topic,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName(),
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());
  }

  @Override
  protected void closeQueue() throws IOException {
    if (producer != null) {
      producer.close();
      producer = null;
    }
  }

  @Override
  protected void writeToQueue(final String id, final String jCas) throws IOException {
    producer.send(new ProducerRecord<>(topic, id, jCas));
  }

  @Override
  protected int getQueueLength() {
    return 0;
  }
}
