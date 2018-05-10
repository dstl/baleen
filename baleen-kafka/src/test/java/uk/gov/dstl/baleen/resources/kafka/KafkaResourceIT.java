// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.kafka;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.exceptions.BaleenException;

public class KafkaResourceIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaResourceIT.class);

  private static final int MESSAGE_COUNT = 100;
  private static final int KAFKA_PORT = 9092;
  private static final int ZOOKEEPER_PORT = 2181;
  private static final String QUEUE = "queue";

  private static final String IMAGE = "spotify/kafka:latest";

  @ClassRule
  @SuppressWarnings("rawtypes")
  public static GenericContainer container =
      new FixedHostPortGenericContainer(IMAGE)
          .withFixedExposedPort(KAFKA_PORT, KAFKA_PORT)
          .withExposedPorts(ZOOKEEPER_PORT)
          .withEnv("ADVERTISED_HOST", "localhost")
          .withEnv("ADVERTISED_PORT", "9092")
          .withMinimumRunningDuration(Duration.ofSeconds(10));

  @Test
  public void testResourceCanSendAndRecieve()
      throws ResourceInitializationException, IOException, InterruptedException, BaleenException {

    SharedKafkaResource resource = new SharedKafkaResource();
    final CustomResourceSpecifier_impl resourceSpecifier = new CustomResourceSpecifier_impl();
    // @formatter:off
    final Parameter[] configParams =
        new Parameter[] {
          new Parameter_impl(SharedKafkaResource.PARAM_HOST, container.getContainerIpAddress()),
          new Parameter_impl(SharedKafkaResource.PARAM_PORT, Integer.toString(KAFKA_PORT))
        };
    // @formatter:on

    resourceSpecifier.setParameters(configParams);
    final Map<String, Object> config = Maps.newHashMap();
    resource.initialize(resourceSpecifier, config);

    final CountDownLatch latch = new CountDownLatch(MESSAGE_COUNT);
    final Producer<String, String> producer =
        resource.createProducer(
            QUEUE,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName(),
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());
    // @formatter:off
    final Consumer<String, String> consumer =
        resource.createConsumer(
            QUEUE,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName(),
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName(),
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            1,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest");
    // @formatter:on

    new Thread(
            () -> {
              int count = 0;
              while (count < MESSAGE_COUNT) {
                final ConsumerRecords<String, String> records = consumer.poll(1000);
                count += records.count();
                records.forEach(
                    (r) -> LOGGER.info("Message recieved: " + r.key() + " " + r.value()));

                records.forEach((r) -> latch.countDown());
              }
            })
        .start();

    new Thread(
            () ->
                IntStream.range(0, MESSAGE_COUNT)
                    .mapToObj(Integer::toString)
                    .map((i) -> new ProducerRecord<>(QUEUE, new Date().toString(), "test"))
                    .map(producer::send)
                    .forEach(
                        (f) -> {
                          try {
                            LOGGER.info("Message sent:" + f.get());
                          } catch (InterruptedException | ExecutionException e) {
                            fail(e.getMessage());
                          }
                        }))
        .start();

    assertTrue("Messages not recieved", latch.await(2, TimeUnit.MINUTES));
  }
}
