// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.kafka;

import java.io.IOException;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.kafka.SharedKafkaResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportCollectionReader;

/**
 * A transport collection reader using Kafka.
 *
 * <p>Requires a {@link SharedKafkaResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class KafkaTransportReceiver extends AbstractTransportCollectionReader {

  @ExternalResource(key = SharedKafkaResource.RESOURCE_KEY)
  private SharedKafkaResource kafkaResource;

  /**
   * The timeout property to use while polling
   *
   * @baleen.config 1000
   */
  public static final String PARAM_TIMEOUT = "timeout";

  @ConfigurationParameter(name = PARAM_TIMEOUT, defaultValue = "1000")
  private int consumerReadTimeout;

  /**
   * The maximum documents to retrieve at once.
   *
   * <p>NB: Larger number are more efficient, but then you are buffering more documents in our
   * memory, rather than in Kafka. If you know you are processing small documents this can be
   * increased.
   *
   * @baleen.config 1
   */
  private static final String PARAM_MAX_POLL_DOCS = "maxPollDocs";

  @ConfigurationParameter(name = PARAM_MAX_POLL_DOCS, defaultValue = "1")
  private int maxPollDocs;

  /**
   * Where to start the receiver from. Options are earliest or latest, that is from the start of the
   * queue or the end.
   *
   * @baleen.config earliest
   */
  private static final String PARAM_AUTO_OFFSET_RESET = "offset";

  @ConfigurationParameter(name = PARAM_AUTO_OFFSET_RESET, defaultValue = "earliest")
  private String autoOffsetReset;

  private Consumer<String, String> consumer = null;

  // NO reason in the current implementation for this to be concurrent, but if might be necessary to
  // move Kafka to another thread so this is done in preparation for that.
  private final Queue<String> queue = new ConcurrentLinkedQueue<>();

  @Override
  protected void createQueue() throws BaleenException {
    // @formatter:off
    consumer =
        kafkaResource.createConsumer(
            topic,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName(),
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName(),
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            maxPollDocs,
            ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
            (int) Duration.ofMinutes(10).toMillis(),
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            autoOffsetReset);
    // @formatter:on
  }

  @Override
  protected void closeQueue() throws IOException {
    if (consumer != null) {
      consumer.close();
      consumer = null;
    }
  }

  @Override
  protected String readFromQueue() throws IOException {
    // Kafka will provide a number of documents at one (depending on the max.poll.records)

    // We'll check if we have any documents already, and if not then we poll to get more to refill
    // out queue.

    if (queue.isEmpty()) {

      // No documents in the queue, so ask Kafka for more...
      while (queue.isEmpty()) {
        final ConsumerRecords<String, String> records = consumer.poll(consumerReadTimeout);
        records.forEach(r -> queue.add(r.value()));
      }
    }

    // We know at this point that we have a non-empty queue to pull from
    return queue.poll();
  }
}
