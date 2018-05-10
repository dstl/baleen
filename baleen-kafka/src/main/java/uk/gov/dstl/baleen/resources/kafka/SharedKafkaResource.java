// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.kafka;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * <b>Shared resource for accessing Kafka</b>
 *
 * <p>This resource removes the need for individual services to establish their own connections to
 * Kafka, instead providing the ability to create Consumers and Producers accessing the shared Kafka
 * server. This provides benefits such as reduced configuration and reduced repeated code.
 *
 * <p>Creation of Producer and Consumer typically require individual request/response interactions
 * with Kafka so are best created upfront. Note that you will need to provide the topic, this may be
 * done via you configuration using:
 *
 * <pre>
 * &#64;ConfigurationParameter(name = "kafka.consumer.topic", defaultValue = "events")
 * private String consumerTopic;
 * </pre>
 *
 * @baleen.javadoc
 */
public class SharedKafkaResource extends BaleenResource {

  /** default key for accessing the Kafka resource */
  public static final String RESOURCE_KEY = "kafkaResource";

  /**
   * The host of the kafka server
   *
   * @baleen.config localhost
   */
  public static final String PARAM_HOST = "kafka.host";

  @ConfigurationParameter(name = PARAM_HOST, defaultValue = "localhost")
  private String bootstrapHost;

  /**
   * The port of the kafka server
   *
   * @baleen.config 9092
   */
  public static final String PARAM_PORT = "kafka.port";

  @ConfigurationParameter(name = PARAM_PORT, defaultValue = "9092")
  private String bootstrapPort;

  /**
   * The consumer group id for kafka consumers
   *
   * @baleen.config 1
   */
  public static final String PARAM_KAFKA_CONSUMER_GROUP_ID = "kafka.consumerGroupId";

  @ConfigurationParameter(name = PARAM_KAFKA_CONSUMER_GROUP_ID, defaultValue = "1")
  private String consumerGroupId;

  private String bootstrapServer;

  @Override
  protected boolean doInitialize(
      final ResourceSpecifier specifier, final Map<String, Object> additionalParams)
      throws ResourceInitializationException {
    getMonitor().info("Initialised Kafka resources");
    bootstrapServer = bootstrapHost + ":" + bootstrapPort;
    return true;
  }

  /**
   * Creates and returns a {@link Consumer} for the given topic and configuration parameters.
   *
   * @param topic the topic to consume from
   * @param keyValueConfiguration Valid configuration strings are documented at {@link
   *     ConsumerConfig}
   * @return the consumer
   * @throws BaleenException
   */
  public <K, V> Consumer<K, V> createConsumer(
      final String topic, final Object... keyValueConfiguration) throws BaleenException {
    checkConfiguration(keyValueConfiguration);
    final Properties properties = getConsumerProperties(keyValueConfiguration);
    final KafkaConsumer<K, V> consumer = new KafkaConsumer<>(properties);
    consumer.subscribe(Collections.singleton(topic));
    return consumer;
  }

  /**
   * Creates and returns a {@link Producer} for the given topic and configuration parameters.
   *
   * @param topic the topic to produce to
   * @param keyValueConfiguration Valid configuration strings are documented at {@link
   *     ProducerConfig}
   * @return the producer
   * @throws BaleenException
   */
  public <K, V> Producer<K, V> createProducer(
      final String topic, final Object... keyValueConfiguration) throws BaleenException {
    checkConfiguration(keyValueConfiguration);

    final Properties properties = getProducerProperties(keyValueConfiguration);
    return new KafkaProducer<>(properties);
  }

  private Properties getConsumerProperties(final Object[] keyValueConfiguration) {
    final Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    addConfigurationToProperties(properties, keyValueConfiguration);
    return properties;
  }

  private Properties getProducerProperties(final Object[] keyValueConfiguration) {
    final Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    addConfigurationToProperties(properties, keyValueConfiguration);

    return properties;
  }

  private void checkConfiguration(final Object[] keyValueConfiguration) throws BaleenException {

    if (keyValueConfiguration == null) {
      return;
    }

    if (keyValueConfiguration.length % 2 == 1) {
      throw new BaleenException(
          "Format is key,value,key,value must be an even number of entries in the array");
    }
  }

  private void addConfigurationToProperties(
      final Properties properties, final Object[] keyValueConfiguration) {
    if (keyValueConfiguration != null && keyValueConfiguration.length > 0) {
      for (int i = 0; i < keyValueConfiguration.length; i += 2) {
        final Object key = keyValueConfiguration[i];
        final Object value = keyValueConfiguration[i + 1];
        properties.put(key, value);
      }
    }
  }

  @Override
  protected void doDestroy() {
    // TODO: Should we maintain a list of all the kafka resources and close them ourselves?
  }
}
