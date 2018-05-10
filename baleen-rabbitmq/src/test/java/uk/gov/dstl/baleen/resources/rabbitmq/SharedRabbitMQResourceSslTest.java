// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.rabbitmq;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Maps;

public class SharedRabbitMQResourceSslTest {

  private static final String INITIAL_CONFIG_PATH =
      SharedRabbitMQResourceSslTest.class.getResource("/brokerSsl.json").getPath();

  private static BrokerManager brokerManager;
  private static int port;
  private SharedRabbitMQResource resource;

  @BeforeClass
  public static void beforeClass() throws Exception {
    port = getPort();
    brokerManager = new BrokerManager(port, INITIAL_CONFIG_PATH);
    brokerManager.startBroker();
  }

  @Before
  public void beforeTest() throws Exception {
    resource = new SharedRabbitMQResource();
    final CustomResourceSpecifier_impl samrSpecifier = new CustomResourceSpecifier_impl();
    final Parameter[] configParams =
        new Parameter[] {
          new Parameter_impl(SharedRabbitMQResource.PARAM_PORT, Integer.toString(port)),
          new Parameter_impl(SharedRabbitMQResource.PARAM_USER, BrokerManager.USER),
          new Parameter_impl(SharedRabbitMQResource.PARAM_PASS, BrokerManager.PASS),
          new Parameter_impl(SharedRabbitMQResource.PARAM_HTTPS, "true"),
          new Parameter_impl(SharedRabbitMQResource.PARAM_TRUSTALL, "true")
        };
    samrSpecifier.setParameters(configParams);
    final Map<String, Object> config = Maps.newHashMap();
    resource.initialize(samrSpecifier, config);
  }

  @Test
  public void testCanSendAndRecieveWithExchange() throws IOException {
    final String exchange = "exchange";
    final String queueName = "test";
    final String routingName = "route";
    String message = "message";

    resource.createConsumer(exchange, routingName, queueName).accept(message.getBytes());
    assertEquals(
        message, new String(resource.createSupplier(exchange, routingName, queueName).get()));
  }

  @After
  public void afterTest() throws Exception {
    if (resource != null) {
      resource.destroy();
      resource = null;
    }
  }

  @AfterClass
  public static void afterClass() throws Exception {
    brokerManager.stopBroker();
    FileUtils.deleteDirectory(new File("./work"));
  }

  private static int getPort() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      return serverSocket.getLocalPort();
    }
  }
}
