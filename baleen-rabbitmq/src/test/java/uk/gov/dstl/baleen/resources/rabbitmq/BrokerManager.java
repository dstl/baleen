// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

public class BrokerManager {

  public static final String USER = "user";
  public static final String PASS = "guest";

  private static final String KEYSTORE_PATH =
      BrokerManager.class.getResource("/clientkeystore").getPath();

  private final int port;
  private final String configPath;

  private Broker broker;

  public BrokerManager(int port, String configPath) {
    this.port = port;
    this.configPath = configPath;
  }

  public void startBroker() throws Exception {
    broker = new Broker();
    final BrokerOptions brokerOptions = new BrokerOptions();
    brokerOptions.setConfigProperty("qpid.amqp_port", Integer.toString(port));
    brokerOptions.setConfigProperty("qpid.user", USER);
    brokerOptions.setConfigProperty("qpid.pass", PASS);
    brokerOptions.setConfigProperty("qpid.keystore", KEYSTORE_PATH);
    brokerOptions.setInitialConfigurationLocation(configPath);

    broker.startup(brokerOptions);
  }

  public void stopBroker() {
    if (broker != null) {
      broker.shutdown();
    }
  }
}
