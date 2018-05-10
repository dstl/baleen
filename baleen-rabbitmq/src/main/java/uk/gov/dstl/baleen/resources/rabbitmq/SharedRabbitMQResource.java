// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * <b>Shared resource for accessing RabbitMQ broker</b>
 *
 * <p>This resource removes the need for individual annotators to establish their own connections to
 * RabbitMQ, instead providing a single instance Channel for Baleen that can be used. This provides
 * benefits such as reduced configuration and reduced repeated code.
 *
 * <p>Creation of Queue, Consumer and Supplier typically require individual request/response
 * interactions with the broker so are best created upfront.
 *
 * @baleen.javadoc
 */
public class SharedRabbitMQResource extends BaleenResource {

  /** A default resource key for this shared resource */
  public static final String RESOURCE_KEY = "rabbitmqResource";

  /**
   * The RabbitMQ broker host to connect to
   *
   * @baleen.config localhost
   */
  public static final String PARAM_HOST = "rabbitmq.host";

  @ConfigurationParameter(name = PARAM_HOST, defaultValue = "localhost")
  private String host;

  /**
   * The RabbitMQ virtual host to connect to
   *
   * @baleen.config localhost
   */
  public static final String PARAM_VIRTUAL_HOST = "rabbitmq.virtualHost";

  @ConfigurationParameter(name = PARAM_VIRTUAL_HOST, defaultValue = "/")
  private String virtualHost;

  /**
   * The RabbitMQ broker port to connect to
   *
   * @baleen.config 61616
   */
  public static final String PARAM_PORT = "rabbitmq.port";

  @ConfigurationParameter(name = PARAM_PORT, defaultValue = "5672")
  private int port;

  /**
   * The username to use for authentication. If left blank, then authentication will not be used.
   *
   * @baleen.config
   */
  public static final String PARAM_USER = "rabbitmq.user";

  @ConfigurationParameter(name = PARAM_USER, defaultValue = "")
  private String username;

  /**
   * The password to use for authentication. If left blank, then authentication will not be used.
   *
   * @baleen.config
   */
  public static final String PARAM_PASS = "rabbitmq.pass";

  @ConfigurationParameter(name = PARAM_PASS, defaultValue = "")
  private String password;

  /**
   * Set true to use https (tls) communication protocol
   *
   * @baleen.config false
   */
  public static final String PARAM_HTTPS = "rabbitmq.https";

  @ConfigurationParameter(name = PARAM_HTTPS, defaultValue = "false")
  private boolean useHttps;

  /**
   * Set true to use default tls and a trust all certificates. Not recommended for production use.
   *
   * @baleen.config false
   */
  public static final String PARAM_TRUSTALL = "rabbitmq.trustAll";

  @ConfigurationParameter(name = PARAM_TRUSTALL, defaultValue = "false")
  private boolean trustAll;

  /**
   * The ssl communication protocol to use (eg TLSv1.1, TLSv1.2)
   *
   * @baleen.config
   */
  public static final String PARAM_SSLPROTCOL = "rabbitmq.sslprotocol";

  @ConfigurationParameter(name = PARAM_PASS, defaultValue = "TLSv1.1")
  private String sslProtocol;

  /**
   * The keystore passphrase
   *
   * @baleen.config
   */
  public static final String PARAM_KEYSTORE_PASS = "rabbitmq.keystorePass";

  @ConfigurationParameter(name = PARAM_KEYSTORE_PASS, mandatory = false)
  private String keystorePass;

  /**
   * The path to the keystore eg "/path/to/client/keycert.p12"
   *
   * @baleen.config
   */
  public static final String PARAM_KEYSTORE_PATH = "rabbitmq.keystorePath";

  @ConfigurationParameter(name = PARAM_KEYSTORE_PATH, mandatory = false)
  private String keystorePath;

  /**
   * The truststore passphrase
   *
   * @baleen.config
   */
  public static final String PARAM_TRUSTSTORE_PASS = "rabbitmq.truststorePass";

  @ConfigurationParameter(name = PARAM_TRUSTSTORE_PASS, mandatory = false)
  private String truststorePass;

  /**
   * The path to the truststore eg "/path/to/trustStore"
   *
   * @baleen.config
   */
  public static final String PARAM_TRUSTSTORE_PATH = "rabbitmq.truststorePath";

  @ConfigurationParameter(name = PARAM_TRUSTSTORE_PATH, mandatory = false)
  private String truststorePath;

  private Connection connection;

  @Override
  protected boolean doInitialize(
      final ResourceSpecifier aSpecifier, final Map<String, Object> aAdditionalParams)
      throws ResourceInitializationException {
    try {
      final ConnectionFactory factory = new ConnectionFactory();
      factory.setUsername(username);
      factory.setPassword(password);
      factory.setVirtualHost(virtualHost);
      factory.setHost(host);
      factory.setPort(port);

      if (useHttps) {
        if (trustAll) {
          factory.useSslProtocol();
        } else {

          try (FileInputStream keystoreStream = new FileInputStream(keystorePath);
              FileInputStream trustStoreStream = new FileInputStream(truststorePath); ) {

            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(keystoreStream, keystorePass.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keystorePass.toCharArray());

            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(trustStoreStream, truststorePass.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            SSLContext c = SSLContext.getInstance(sslProtocol);
            c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            factory.useSslProtocol(c);
          }
        }
      }

      connection = factory.newConnection();
    } catch (final Exception e) {
      throw new ResourceInitializationException(
          new BaleenException("Error connecting to RabbitMQ", e));
    }

    getMonitor().info("Initialised shared RabbitMQ resource");
    return true;
  }

  private void declareRoute(
      final String exchangeName,
      final String routingKey,
      final String queueName,
      final Channel channel)
      throws IOException {
    channel.exchangeDeclare(exchangeName, "direct", true);
    channel.queueDeclare(queueName, true, false, false, null);
    channel.queueBind(queueName, exchangeName, routingKey);
  }

  public RabbitMQConsumer createConsumer(
      final String exchangeName, final String routingKey, final String queueName)
      throws IOException {
    final Channel channel = connection.createChannel();
    declareRoute(exchangeName, routingKey, queueName, channel);
    return new LiveRabbitMQConsumer(getMonitor(), channel, exchangeName, routingKey, queueName);
  }

  public RabbitMQSupplier createSupplier(
      final String exchangeName, final String routingKey, final String queueName)
      throws IOException {
    final Channel channel = connection.createChannel();
    declareRoute(exchangeName, routingKey, queueName, channel);
    return new LiveRabbitMQSupplier(getMonitor(), channel, queueName);
  }

  @Override
  protected void doDestroy() {
    getMonitor().debug("Disconnecting from RabbitMQ");
    try {
      connection.close();
    } catch (final Exception e) {
      getMonitor().error("Could not close connection to RabbitMQ", e);
    }
  }
}
