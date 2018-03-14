// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * <b>Shared resource for accessing ActiveMQ broker</b>
 *
 * <p>This resource removes the need for individual annotators to establish their own connections to
 * ActiveMQ, instead providing a single instance of MessageProducer and MessageConsumer for Baleen
 * that can be used. This provides benefits such as reduced configuration and reduced repeated code.
 * <br>
 * Creation of Connection, Session, MessageProducer, and MessageConsumer typically require
 * individual request/response interactions with the broker so are best created upfront.<br>
 * Note: MessageProducer can be used with multiple destinations
 *
 * @baleen.javadoc
 */
public class SharedActiveMQResource extends BaleenResource {

  public static final String DEFAULT_PROTOCOL = "tcp";
  public static final String DEFAULT_HOST = "localhost";
  public static final String DEFAULT_PORT_STRING = "61616";
  public static final String DEFAULT_BROKER_ARGS = "";
  public static final String DEFAULT_TOPIC = "baleen";
  public static final String DEFAULT_USER = "";
  public static final String DEFAULT_PASS = "";

  /**
   * The protocol to use for ActiveMQ broker connection
   *
   * @baleen.config tcp
   */
  public static final String PARAM_PROTOCOL = "activemq.protocol";

  @ConfigurationParameter(name = PARAM_PROTOCOL, defaultValue = DEFAULT_PROTOCOL)
  private String activeMQProtocol;

  /**
   * The ActiveMQ broker host to connect to
   *
   * @baleen.config localhost
   */
  public static final String PARAM_HOST = "activemq.host";

  @ConfigurationParameter(name = PARAM_HOST, defaultValue = DEFAULT_HOST)
  private String activeMQHost;

  /**
   * The ActiveMQ broker port to connect to
   *
   * @baleen.config 61616
   */
  public static final String PARAM_PORT = "activemq.port";

  @ConfigurationParameter(name = PARAM_PORT, defaultValue = DEFAULT_PORT_STRING)
  private String activeMQPortString;

  /**
   * The ActiveMQ broker arguments as a single string
   *
   * @baleen.config
   */
  public static final String PARAM_BROKERARGS = "activemq.brokerargs";

  @ConfigurationParameter(name = PARAM_BROKERARGS, defaultValue = DEFAULT_BROKER_ARGS)
  private String activeMQBrokerArgs;

  /**
   * The ActiveMQ endpoint to connect to (e.g. queue:foo.bar)
   *
   * @baleen.config baleen
   */
  public static final String PARAM_DB = "activemq.topic";

  @ConfigurationParameter(name = PARAM_DB, defaultValue = DEFAULT_TOPIC)
  private String activeMQTopic;

  /**
   * The username to use for authentication. If left blank, then authentication will not be used.
   *
   * @baleen.config
   */
  public static final String PARAM_USER = "activemq.user";

  @ConfigurationParameter(name = PARAM_USER, defaultValue = DEFAULT_USER)
  private String activeMQUser;

  /**
   * The password to use for authentication. If left blank, then authentication will not be used.
   *
   * @baleen.config
   */
  public static final String PARAM_PASS = "activemq.pass";

  @ConfigurationParameter(name = PARAM_PASS, defaultValue = DEFAULT_PASS)
  private String activeMQPass;

  protected Connection connection;
  protected Session session;
  protected MessageProducer messageProducer;

  @Override
  protected boolean doInitialize(
      ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
      throws ResourceInitializationException {
    try {
      connection =
          createConnection(
              activeMQProtocol,
              activeMQHost,
              activeMQPortString,
              activeMQBrokerArgs,
              activeMQUser,
              activeMQPass);
      connection.start();
      session = createSession(connection);
      messageProducer = createMessageProducer(session);
    } catch (JMSException jmsException) {
      throw new ResourceInitializationException(
          new BaleenException("Error connecting to ActiveMQ", jmsException));
    }

    getMonitor().info("Initialised shared ActiveMQ resource");
    return true;
  }

  /**
   * Creates and returns an ActiveMQ session for use in creating producers/consumers. This method
   * handles the creation of the connection factory and connection
   *
   * @throws JMSException
   */
  private Connection createConnection(
      String protocol,
      String host,
      String port,
      String brokerArgs,
      String username,
      String password)
      throws JMSException {
    String brokerUri = constructBrokerUri(protocol, host, port, brokerArgs);

    // Create connection factory
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL(brokerUri);
    connectionFactory.setUserName(username);
    connectionFactory.setPassword(password);

    // Create a connection
    connection = connectionFactory.createConnection();
    return connection;
  }

  /**
   * Create and return a session
   *
   * @throws JMSException
   */
  private Session createSession(Connection connection) throws JMSException {
    // Create and return a session
    return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  /**
   * Create a new message producer, using a null destination so as to allow multiple destinations on
   * specification at send time
   *
   * @throws JMSException
   */
  private MessageProducer createMessageProducer(Session session) throws JMSException {
    return session.createProducer(null);
  }

  /**
   * creates and returns a MessageConsumer object consuming from the given endpoint. Only messages
   * matching the selector are delivered.
   *
   * @param queueName The name of the queue (or VirtualTopic) to consume from
   * @param messageSelector Only messages matching the selector are delivered
   */
  public MessageConsumer createConsumer(String queueName, String messageSelector)
      throws JMSException {
    Destination destination = session.createQueue(queueName);
    return session.createConsumer(destination, messageSelector);
  }

  /**
   * returns a MessageProducer for use in sending messages. The producer has no preset destination
   * and can be used to send to any destination specified at send-time
   */
  public MessageProducer getProducer() {
    return messageProducer;
  }

  /** returns a Session, mainly for use in creating destinations */
  public Session getSession() {
    return session;
  }

  /**
   * creates and returns a QueueBrowser object browser the given endpoint. Only messages matching
   * the selector are delivered.
   *
   * @param queueName The name of the queue (or VirtualTopic) to consume from
   * @param messageSelector Only messages matching the selector are delivered
   * @throws JMSException
   */
  public QueueBrowser createQueueBrowser(String queueName, String messageSelector)
      throws JMSException {
    Queue queue = session.createQueue(queueName);
    return session.createBrowser(queue, messageSelector);
  }

  @Override
  protected void doDestroy() {
    getMonitor().debug("Disconnecting from ActiveMQ");
    try {
      session.close();
      connection.close();
    } catch (JMSException e) {
      getMonitor().error("Could not close connection to ActiveMQ", e);
    }
  }

  private String constructBrokerUri(String protocol, String host, String port, String brokerArgs) {
    StringBuilder sb = new StringBuilder();
    sb.append(protocol);
    sb.append("://");
    sb.append(host);
    if (!port.isEmpty()) {
      sb.append(":");
      sb.append(port);
    }
    sb.append("?");
    sb.append(brokerArgs);
    return sb.toString();
  }
}
