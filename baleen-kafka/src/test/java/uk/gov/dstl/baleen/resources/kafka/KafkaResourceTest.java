// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.kafka;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.exceptions.BaleenException;

public class KafkaResourceTest {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(KafkaResourceTest.class);

  private static int zkPort;
  private static int brokerPort;
  private static Zookeeper zookeeper;
  private static KafkaServerStartable broker;
  private static String zookeeperString;
  private static final Properties kafkaBrokerConfig = new Properties();

  private SharedKafkaResource kafkaResource;

  @BeforeClass
  public static void startup() throws IOException {
    zkPort = getPort();
    brokerPort = getPort();
    zookeeperString = "localhost:" + zkPort;

    zookeeper = new Zookeeper(zkPort);
    zookeeper.startup();

    final File logDir;
    try {
      logDir = Files.createTempDirectory("kafka").toFile();
    } catch (IOException e) {
      throw new RuntimeException("Unable to start Kafka", e);
    }
    logDir.deleteOnExit();
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                new Runnable() {
                  @Override
                  public void run() {
                    try {
                      FileUtils.deleteDirectory(logDir);
                    } catch (IOException e) {
                      LOGGER.warn(
                          "Problems deleting temporary directory " + logDir.getAbsolutePath(), e);
                    }
                  }
                }));
    kafkaBrokerConfig.setProperty("zookeeper.connect", zookeeperString);
    kafkaBrokerConfig.setProperty("broker.id", "1");
    kafkaBrokerConfig.setProperty("host.name", "localhost");
    kafkaBrokerConfig.setProperty("port", Integer.toString(brokerPort));
    kafkaBrokerConfig.setProperty("log.dir", logDir.getAbsolutePath());
    kafkaBrokerConfig.setProperty("log.flush.interval.messages", String.valueOf(1));
    kafkaBrokerConfig.setProperty("delete.topic.enable", String.valueOf(true));
    kafkaBrokerConfig.setProperty("offsets.topic.replication.factor", String.valueOf(1));

    broker = new KafkaServerStartable(new KafkaConfig(kafkaBrokerConfig));
    broker.startup();
  }

  private static int getPort() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      return serverSocket.getLocalPort();
    }
  }

  @AfterClass
  public static void shutdown() {
    if (broker != null) {
      broker.shutdown();
    }
    if (zookeeper != null) {
      zookeeper.shutdown();
    }
  }

  @Before
  public void beforeTest() throws Exception {

    kafkaResource = new SharedKafkaResource();
    final CustomResourceSpecifier_impl samrSpecifier = new CustomResourceSpecifier_impl();
    final Parameter[] configParams =
        new Parameter[] {
          new Parameter_impl(SharedKafkaResource.PARAM_HOST, "localhost"),
          new Parameter_impl(SharedKafkaResource.PARAM_PORT, Integer.toString(brokerPort)),
          new Parameter_impl(SharedKafkaResource.PARAM_KAFKA_CONSUMER_GROUP_ID, "1")
        };
    samrSpecifier.setParameters(configParams);
    final Map<String, Object> config = Maps.newHashMap();
    kafkaResource.initialize(samrSpecifier, config);
  }

  @Test
  public void testCreateConsumer() throws TimeoutException, BaleenException {

    String topic = "testTopic";

    Consumer<String, String> consumer =
        kafkaResource.createConsumer(
            topic,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName(),
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName(),
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            1);
    Producer<String, String> producer =
        kafkaResource.createProducer(
            topic,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName(),
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());

    ProducerThread producerThread = new ProducerThread(topic, producer);
    producerThread.start();

    Queue<String> queue = new ConcurrentLinkedQueue<>();
    int attempts = 0;
    while (queue.isEmpty() && attempts < 10) {
      final ConsumerRecords<String, String> records = consumer.poll(1000);
      records.forEach(r -> queue.add(r.value()));
      attempts++;
    }

    String poll = queue.poll();

    assertNotNull("No message recieved", poll);
    assertTrue(poll.startsWith("Message_"));

    consumer.close();
    producer.close();
  }
}
